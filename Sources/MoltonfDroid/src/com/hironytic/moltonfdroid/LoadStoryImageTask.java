/*
 * Moltonf
 *
 * Copyright (c) 2011 Hironori Ichimiya <hiron@hironytic.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.hironytic.moltonfdroid;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import com.hironytic.moltonfdroid.model.Avatar;
import com.hironytic.moltonfdroid.model.Story;
import com.hironytic.moltonfdroid.util.BitmapHolder;
import com.hironytic.moltonfdroid.util.Proc1;
import com.hironytic.moltonfdroid.util.SmartUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

/**
 * Story 内の画像を読み込んで各 BitmapHolder にセットするタスク
 */
public class LoadStoryImageTask extends AsyncTask<Story, LoadStoryImageTask.ProgressData, Void>{
    /** 1つの画像を読み込む度に UI スレッドを呼び出す際の引数 */
    protected static class ProgressData {
        /** 画像を格納する BitmapHolder */
        public BitmapHolder bitmapHolder;
        
        /** 読み込んだ画像 */
        public Bitmap loadedBitmap;
    }

    /**
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected Void doInBackground(Story... params) {
        Story story = params[0];
        
        // 墓アイコン画像
        loadImage(story.getGraveIconHolder(), "grave", story.getGraveIconUri());
        if (isCancelled()) {
            return null;
        }
        
        // アバターのアイコン画像
        List<Avatar> avatarList = story.getAvatarList();
        for (Avatar avatar : avatarList) {
            loadImage(avatar.getFaceIconHolder(), avatar.getAvatarId(), avatar.getFaceIconUri());
            if (isCancelled()) {
                return null;
            }
        }

        return null;
    }

    /**
     * 画像を読み込みます。
     * @param bitmapHolder 
     * @param imageName
     * @param imageUri
     */
    private void loadImage(final BitmapHolder bitmapHolder, String imageName, URI imageUri) {
        // bitmapHolder がすでに画像を持っていたら読み込まなくてもいい
        if (bitmapHolder.hasBitmap()) {
            return;
        }

        // ファイルとして既に画像が存在していればそれを読み込む。
        // 存在していなければ HTTP 経由でとりにいく。その際に、読み込んだ画像をファイルに落とす
        URL imageUrl = null;
        try {
            imageUrl = imageUri.toURL();
        } catch (MalformedURLException ex) {
            return;
        }
        String extension = getExtension(imageUrl);
        final String iconFileName = (extension == null) ? imageName : imageName + "." + extension;
        File iconFileForRead = getIconFile(iconFileName, false);
        if (iconFileForRead != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(iconFileForRead.getAbsolutePath());
            if (bitmap != null) {
                ProgressData data = new ProgressData();
                data.bitmapHolder = bitmapHolder;
                data.loadedBitmap = bitmap;
                publishProgress(data);
                return;
            }
        }
        
        // 読み込めなければとりにいく
        try {
            HttpAccess.doGet(imageUri, new Proc1<InputStream>() {
                @Override
                public void perform(InputStream arg) {
                    BufferedInputStream inStream = new BufferedInputStream(arg);
                    try {
                        inStream.mark(8192);
                        
                        // ファイルに落とす
                        boolean isSaved = false;
                        File iconFileForWrite = getIconFile(iconFileName, true);                        
                        if (iconFileForWrite != null) {
                            try {
                                final int BUFFER_SIZE = 4096;
                                byte[] data = new byte[BUFFER_SIZE];
                                OutputStream fileOutStream = new FileOutputStream(iconFileForWrite);
                                try {
                                    int readSize = inStream.read(data);
                                    while (readSize > 0) {
                                        fileOutStream.write(data, 0, readSize);
                                        readSize = inStream.read(data);
                                    }
                                    isSaved = true;
                                } finally {
                                    fileOutStream.close();
                                }
                            } catch (IOException ex) {
                                Moltonf.getLogger().warning("failed to write downloaded image", ex);
                            }
                        }
                        
                        // ビットマップ生成
                        Bitmap bitmap = null;
                        try {
                            inStream.reset();
                            bitmap = BitmapFactory.decodeStream(inStream);
                        } catch (IOException ex) {
                            // 画像がリミット以上に大きかった場合など
                            // ファイルに落とすのに成功しているならファイルを読み込む。
                            if (isSaved) {
                                bitmap = BitmapFactory.decodeFile(iconFileForWrite.getAbsolutePath());
                            }
                        }
                        
                        if (bitmap != null) {
                            ProgressData data = new ProgressData();
                            data.bitmapHolder = bitmapHolder;
                            data.loadedBitmap = bitmap;
                            publishProgress(data);
                        }
                    } finally {
                        try {
                            inStream.close();
                        } catch (IOException ex) {
                            // ignore
                        }
                    }
                }
            });
        } catch (MoltonfException ex) {
            Moltonf.getLogger().warning("failed to load image", ex);
        }
    }
    
    /**
     * URLから拡張子を得ます。
     * @param url URL
     * @return 拡張子文字列。なければ null。
     */
    private String getExtension(URL url) {
        String fileName = url.getPath();
        if (!SmartUtils.isStringEmpty(fileName)) {
            int dirSepIndex = fileName.lastIndexOf('/');
            if (dirSepIndex > 0) {
                fileName = fileName.substring(dirSepIndex + 1);
            }
            int extSepIndex = fileName.lastIndexOf('.');
            if (extSepIndex > 0 && extSepIndex + 1 < fileName.length()) {
                return fileName.substring(extSepIndex + 1);
            }
        }
        
        return null;
    }
    
    /**
     * アイコンのファイルを得ます。
     * @param fileName ファイル名
     * @param isForWrite 書き込み用に用いるかどうか
     * @return アイコンファイルの File オブジェクト。得られなければ null。
     */
    private File getIconFile(String fileName, boolean isForWrite) {
        try {
            File workDir = Moltonf.getWorkDir();
            File iconDir = null;
            if (workDir != null) {
                iconDir = new File(workDir, "icons");
                if (!iconDir.exists()) {
                    if (isForWrite && workDir.canWrite()) {
                        iconDir.mkdir();
                    } else {
                        return null;
                    }
                }
                
                File iconFile = new File(iconDir, fileName);
                if (iconFile.exists()) {
                    if (isForWrite && !iconFile.canWrite()) {
                        return null;
                    } else {
                        return iconFile;
                    }
                } else {
                    if (isForWrite && iconDir.canWrite()) {
                        return iconFile;
                    } else {
                        return null;
                    }
                }
            }
        } catch (SecurityException ex) {
            return null;
        }
        
        return null;
    }
    
    /**
     * @see android.os.AsyncTask#onProgressUpdate(ProgressData[])
     */
    @Override
    protected void onProgressUpdate(ProgressData... values) {
        ProgressData progress = values[0];
        progress.bitmapHolder.setBitmap(progress.loadedBitmap);
    }
}

