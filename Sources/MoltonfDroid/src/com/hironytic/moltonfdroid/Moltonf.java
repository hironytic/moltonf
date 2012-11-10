/*
 * Moltonf
 *
 * Copyright (c) 2011,2012 Hironori Ichimiya <hiron@hironytic.com>
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

import java.io.File;

import android.os.Environment;

import com.hironytic.moltonfdroid.util.Logger;

/**
 * Moltonf アプリケーション全体で共有する処理
 */
public class Moltonf {
    private static final String LOGGER_TAG = "MoltonfDroid";
    private static final String WORK_DIR_NAME = "MoltonfDroid";

    private static final Moltonf theInstance = new Moltonf();
    
    /** バージョン文字列 */
    private String versionString = null;
    
    /** ログ出力用オブジェクト */
    private Logger logger = new Logger(LOGGER_TAG);
    
    /**
     * コンストラクタ
     */
    private Moltonf() {
    }

    /**
     * 唯一のインスタンスを得ます。
     * @return 唯一のインスタンス
     */
    public static Moltonf getInstance() {
        return theInstance;
    }

    /**
     * MoltonfDroid アプリケーション用のログ出力用オブジェクトを返します。
     * @return Logger オブジェクト
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * MoltonfDroid アプリケーションのバージョン文字列を得ます。
     * @return バージョン文字列
     */
    public String getVersionString() {
        return versionString;
    }
    
    /**
     * MoltonfDroid アプリケーションのバージョン文字列を設定します。
     * @param versionString バージョン文字列
     */
    public void setVersionString(String versionString) {
        this.versionString = versionString;
    }

    /**
     * MoltonfDroid アプリケーションの扱うファイルを格納しているディレクトリを返します。
     * @return アプリケーションの扱うファイルを格納しているディレクトリ
     *          取得できなければ null を返します。
     */
    public File getWorkDir() {
        try {
            File sdcardDir = Environment.getExternalStorageDirectory();
            File moltonfDroidDir = new File(sdcardDir, WORK_DIR_NAME);
            if (!moltonfDroidDir.exists()) {
                if (sdcardDir.canWrite()) {
                    moltonfDroidDir.mkdir();
                } else {
                    return null;
                }
            }
            return moltonfDroidDir;
        } catch (SecurityException ex) {
            return null;
        }
    }
}
