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

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.hironytic.moltonfdroid.util.Logger;

/**
 * Moltonf アプリケーション全体で共有する処理
 */
public class Moltonf extends Application {
    /** 唯一のアプリケーションオブジェクト */
    private static Moltonf app = null;
    
    /** ロガー */
    private Logger logger = new Logger("MoltonfDroid");
    
    /** バージョン文字列 */
    private String versionString = null;

    /**
     * コンストラクタ
     */
    public Moltonf() {
        app = this;
    }
    
    /**
     * Moltonf アプリケーション用のログ出力用オブジェクトを返します。
     * @return Logger オブジェクト
     */
    public static Logger getLogger() {
        return app.logger;
    }

    /**
     * Moltonf アプリケーションのバージョン文字列を得ます。
     * @param context アプリケーションのコンテキスト
     * @return バージョン文字列
     */
    public static String getVersionString() {
        return app.versionString;
    }

    /**
     * アプリケーションが作られたときに呼び出されます。
     */
    @Override
    public void onCreate() {
        super.onCreate();

        logger = new Logger("MoltonfDroid");

        try {
            String packageName = getPackageName();
            PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, PackageManager.GET_META_DATA);
            versionString = packageInfo.versionName;
        } catch (NameNotFoundException ex) {
            versionString = "";
        }
    }
}
