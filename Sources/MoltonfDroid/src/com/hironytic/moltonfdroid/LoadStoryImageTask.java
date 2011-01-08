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

import java.io.InputStream;
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
        
        // アバターのアイコン画像
        List<Avatar> avatarList = story.getAvatarList();
        for (Avatar avatar : avatarList) {
            loadImage(avatar.getFaceIconHolder(), avatar.getAvatarId(), avatar.getFaceIconUri());
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
        // TODO: bitmapHolder がすでに画像を持っていたら読み込まなくてもいいのでは

        // TODO: 常に読み込むのではなくてファイルに持っていなければ読み込む
        // また、読み込んだ画像をファイルに落とす
        URL imageUrl = null;
        try {
            imageUrl = imageUri.toURL();
        } catch (MalformedURLException ex) {
            return;
        }
        String extension = getExtension(imageUrl);
        
        try {
            HttpAccess.doGet(imageUri, new Proc1<InputStream>() {
                @Override
                public void perform(InputStream arg) {
                    final Bitmap bitmap = BitmapFactory.decodeStream(arg);
                    if (bitmap != null) {
                        ProgressData data = new ProgressData();
                        data.bitmapHolder = bitmapHolder;
                        data.loadedBitmap = bitmap;
                        publishProgress(data);
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
     * @see android.os.AsyncTask#onProgressUpdate(ProgressData[])
     */
    @Override
    protected void onProgressUpdate(ProgressData... values) {
        ProgressData progress = values[0];
        progress.bitmapHolder.setBitmap(progress.loadedBitmap);
    }
}

