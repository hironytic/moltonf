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
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;

import com.hironytic.moltonfdroid.model.HighlightSetting;
import com.hironytic.moltonfdroid.util.Logger;

/**
 * Moltonf アプリケーション全体で共有する処理
 */
public class Moltonf {
    private static final String LOGGER_TAG = "MoltonfDroid";

    private static final String WORKDIR_ICONS = "icons";
    private static final String WORKDIR_PLAYDATA = "playdata";
    
    private static final Moltonf theInstance = new Moltonf();
    
    /** バージョン文字列 */
    private String versionString = null;
    
    /** MoltonfDroid アプリケーションの扱うファイルを格納しているディレクトリ */
    private File workDir = null;
    
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
        return workDir;
    }
    
    /**
     * MoltonfDroid アプリケーションの扱うファイルを格納しているディレクトリを設定します。
     * @param workDir 作業ディレクトリ
     */
    public void setWorkDir(File workDir) {
        this.workDir = workDir;
    }

    /**
     * アイコンを格納するディレクトリを返します。
     * @return アイコンを格納するディレクトリ。取得できなければnullを返します。
     */
    public File getIconDir() {
        if (workDir != null) {
            return new File(workDir, WORKDIR_ICONS);
        } else {
            return null;
        }
    }
    
    /**
     * プレイデータパッケージを格納するディレクトリを返します
     * @return プレイデータパッケージを格納するディレクトリ。取得できなければnullを返します。
     */
    public File getPlayDataDir() {
        if (workDir != null) {
            return new File(workDir, WORKDIR_PLAYDATA);
        } else {
            return null;
        }
    }
    
    /**
     * ストーリーを表示する際の強調表示設定を返します。
     * @param context コンテキスト
     * @return 強調表示設定のリスト
     */
    public List<HighlightSetting> getHighlightSettings(Context context) {
        List<HighlightSetting> highlightSettingList = new ArrayList<HighlightSetting>();
        HighlightSetting hlSetting;
        hlSetting = new HighlightSetting(); hlSetting.setPatternString("【.*?】");    hlSetting.setHighlightColor(Color.RED);   highlightSettingList.add(hlSetting);
        hlSetting = new HighlightSetting(); hlSetting.setPatternString("★");    hlSetting.setHighlightColor(Color.GREEN);   highlightSettingList.add(hlSetting);
        hlSetting = new HighlightSetting(); hlSetting.setPatternString("☆");    hlSetting.setHighlightColor(Color.GREEN);   highlightSettingList.add(hlSetting);
        hlSetting = new HighlightSetting(); hlSetting.setPatternString("●");    hlSetting.setHighlightColor(Color.MAGENTA);   highlightSettingList.add(hlSetting);
        hlSetting = new HighlightSetting(); hlSetting.setPatternString("○");    hlSetting.setHighlightColor(Color.MAGENTA);   highlightSettingList.add(hlSetting);
        hlSetting = new HighlightSetting(); hlSetting.setPatternString("▼");    hlSetting.setHighlightColor(Color.CYAN);   highlightSettingList.add(hlSetting);
        hlSetting = new HighlightSetting(); hlSetting.setPatternString("▽");    hlSetting.setHighlightColor(Color.CYAN);   highlightSettingList.add(hlSetting);
        hlSetting = new HighlightSetting(); hlSetting.setPatternString("■");    hlSetting.setHighlightColor(0xffffc800);   highlightSettingList.add(hlSetting);
        hlSetting = new HighlightSetting(); hlSetting.setPatternString("□");    hlSetting.setHighlightColor(0xffffc800);   highlightSettingList.add(hlSetting);
        return highlightSettingList;
    }
}
