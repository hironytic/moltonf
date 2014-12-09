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
import java.util.Locale;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;

/**
 * Moltonfアプリケーション
 */
public class MoltonfApplication extends Application {

    private static final String WORK_DIR_NAME_FORMAT = "Android/data/%s/files";
    
    /**
     * アプリケーションが作られたときに呼び出されます。
     */
    @Override
    public void onCreate() {
        super.onCreate();

        Moltonf moltonf = Moltonf.getInstance();        
        String packageName = getPackageName();
        
        // バージョン文字列
        String versionString = "";
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, PackageManager.GET_META_DATA);
            versionString = packageInfo.versionName;
        } catch (NameNotFoundException ex) {
        }
        moltonf.setVersionString(versionString);
        
        // 作業ディレクトリ
        File workDir;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            workDir = getWorkDirV8();
        } else {
            try {
                File sdcardDir = Environment.getExternalStorageDirectory();
                workDir = new File(sdcardDir, String.format(Locale.US, WORK_DIR_NAME_FORMAT, packageName));
                if (!workDir.exists()) {
                    if (sdcardDir.canWrite()) {
                        workDir.mkdirs();
                    } else {
                        workDir = null;
                    }
                }
            } catch (SecurityException ex) {
                workDir = null;
            }
        }
        moltonf.setWorkDir(workDir);        
    }
    
    /**
     * MoltonfDroid アプリケーションの扱うファイルを格納しているディレクトリを返します。（API Level 8以上専用）
     * @return アプリケーションの扱うファイルを格納しているディレクトリ
     *          取得できなければ null を返します。
     */
    @TargetApi(8)
    private File getWorkDirV8() {
        return this.getExternalFilesDir(null); 
    }
    
}
