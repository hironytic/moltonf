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

import java.io.File;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;

import com.hironytic.moltonfdroid.util.Logger;
import com.hironytic.moltonfdroid.util.ObjectBank;

/**
 * Moltonf アプリケーション全体で共有する処理
 */
public class Moltonf extends Application {
    private static final String LOGGER_TAG = "MoltonfDroid";
    private static final String WORK_DIR_NAME = "MoltonfDroid";
        
    /** バージョン文字列 */
    private String versionString = null;
    
    /** 同一アプリケーション内のオブジェクトの受け渡しのために一時的にオブジェクトを預ける場所 */
    private final ObjectBank objectBank = new ObjectBank();
    
    /**
     * コンストラクタ
     */
    public Moltonf() {
    }

    /**
     * アプリケーションが作られたときに呼び出されます。
     */
    @Override
    public void onCreate() {
        super.onCreate();

        try {
            String packageName = getPackageName();
            PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, PackageManager.GET_META_DATA);
            versionString = packageInfo.versionName;
        } catch (NameNotFoundException ex) {
            versionString = "";
        }
    }

    /**
     * MoltonfDroid アプリケーション用のログ出力用オブジェクトを返します。
     * @return Logger オブジェクト
     */
    public static Logger getLogger() {
    	return new Logger(LOGGER_TAG);
    }

    /**
     * MoltonfDroid アプリケーションのバージョン文字列を得ます。
     * @param context アプリケーションのコンテキスト
     * @return バージョン文字列
     */
    public String getVersionString() {
        return versionString;
    }

    /**
     * 同一アプリケーション内のオブジェクトの引き渡しに用いる ObjectBank を返します。
     * @return ObjectBank オブジェクト
     */
    public ObjectBank getObjectBank() {
        return objectBank;
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
