/*
 * Moltonf
 *
 * Copyright (c) 2010,2011 Hironori Ichimiya <hiron@hironytic.com>
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

package com.hironytic.moltonfdroid.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;

import com.hironytic.moltonfdroid.MoltonfException;
import com.hironytic.moltonfdroid.model.archived.PackagedStory;

/**
 * Moltonf ワークスペースの情報を保持するクラス
 */
public class Workspace {

    private static final String KEY_VERSION = "version";
    private static final int VALUE_VERSION_UNDEFINED = 0;
    private static final int VALUE_VERSION_1 = 1;
    
    private static final String KEY_PACKAGE = "package"; 
    
    
    /** このワークスペースの情報を保存してあるファイル */
    private File workspaceFile;
    
    /** ワークスペース情報保存ファイルの内容 */
    private JSONObject workspaceFileContents;
    
    /** 最後に保存してから情報が変更されているかどうか */
    private boolean isModified;
    
    /** このワークスペースが扱うストーリーが記述されたプレイデータアーカイブのパッケージディレクトリ */
    private File packageDir;
    
    /** このワークスペースが扱うストーリー */
    private Story story;
    
    /**
     * private コンストラクタ
     * 外部からのインスタンス生成は {@link #createNewWorkspace(File)} か {@link #loadWorkspace(File)} を使って行います。
     * @param workspaceFile このワークスペースの情報を保存してあるファイル
     */
    private Workspace(File workspaceFile) {
        this.workspaceFile = workspaceFile;
    }

    /**
     * 新しくワークスペースを作成します。
     * @param workspaceFile ワークスペースの情報を保存するファイル
     * @return 作成したワークスペース
     */
    public static Workspace createNewWorkspace(File workspaceFile) {
        Workspace ws = new Workspace(workspaceFile);
        ws.createNew();
        return ws;
    }
    
    /**
     * 既存のワークスペースを読み込みます。
     * @param workspaceFile ワークスペースの情報が保存されているファイル
     * @return 読み込んだワークスペース
     * @throws MoltonfException 読み込めなかったとき
     */
    public static Workspace loadWorkspace(File workspaceFile) {
        Workspace ws = new Workspace(workspaceFile);
        ws.load();
        return ws;
    }
    
    /**
     * このワークスペースが扱うストーリーが記述されたプレイデータアーカイブのパッケージディレクトリを取得します。
     * @return プレイデータアーカイブのパッケージディレクトリ
     */    

    public File getPackageDir() {
        return packageDir;
    }

    /**
     * このワークスペースが扱うストーリーを返します。
     * @return ストーリー
     */
    public Story getStory() {
        return story;
    }
    
    /**
     * このワークスペースが扱うストーリーが記述されたプレイデータアーカイブのパッケージディレクトリをセットします。
     * @param packageDir プレイデータアーカイブのパッケージディレクトリ
     */
    public void setPackageDir(File packageDir) {
        try {
            // FIXME: MoltonfDroid/playdata 以下にあるものは相対パスにしたいな...
            workspaceFileContents.put(KEY_PACKAGE, packageDir.getAbsolutePath());
        } catch (JSONException ex) {
            throw new MoltonfException(ex);
        }
        
        this.packageDir = packageDir;
        this.story = new PackagedStory(this.packageDir);
        isModified = true;
    }
    
    /**
     * 新規作成を行います。
     * @throws MoltonfException JSON例外が発生したとき
     */
    private void createNew() {
        try {
            workspaceFileContents = new JSONObject();
            workspaceFileContents.put(KEY_VERSION, VALUE_VERSION_1);
        } catch (JSONException ex) {
            throw new MoltonfException(ex);
        }
        isModified = true;
    }
    
    /**
     * ワークスペースファイルの内容を読み込みます。
     * @throws MoltonfException 読み込めなかったとき
     */
    private void load() {
        try {
            StringBuilder jsonBuf = new StringBuilder();
            InputStream inStream = new FileInputStream(workspaceFile);
            InputStreamReader inStreamReader = new InputStreamReader(inStream, Charset.forName("utf-8"));
            try {
                char[] workBuf = new char[4096];
                int readLen;
                while ((readLen = inStreamReader.read(workBuf)) >= 0) {
                    jsonBuf.append(workBuf, 0, readLen);
                }
            } finally {
                inStreamReader.close();
            }
            workspaceFileContents = new JSONObject(jsonBuf.toString());
        } catch (JSONException ex) {
            throw new MoltonfException("failed to load workspace", ex);
        } catch (IOException ex) {
            throw new MoltonfException("failed to load workspace", ex);
        }
        
        // ワークスペースファイルの内容を読み込み
        int version = workspaceFileContents.optInt(KEY_VERSION, VALUE_VERSION_UNDEFINED);
        if (version == VALUE_VERSION_1) {
            String packageDirString = workspaceFileContents.optString(KEY_PACKAGE, null);
            if (packageDirString != null) {
                // FIXME: MoltonfDroid/playdata 以下にあるものは相対パスにしたいな...
                packageDir = new File(packageDirString);
                story = new PackagedStory(packageDir);
            }
        } else {
            throw new MoltonfException("failed to load workspace: unknown version");
        }

        isModified = false;
    }
    
    /**
     * 変更内容をワークスペースファイルに反映します。
     */
    public void save() {
        if (!isModified) {
            return;
        }
        
        String contents = workspaceFileContents.toString();

        OutputStream outStream;
        try {
            outStream = new FileOutputStream(workspaceFile);
            OutputStreamWriter outStreamWriter = new OutputStreamWriter(outStream, Charset.forName("utf-8"));
            try {
                outStreamWriter.write(contents);
            } finally {
                outStreamWriter.close();
            }
        } catch (FileNotFoundException ex) {
            throw new MoltonfException("failed to save workspace", ex);
        } catch (IOException ex) {
            throw new MoltonfException("failed to save workspace", ex);
        }
    }
}
