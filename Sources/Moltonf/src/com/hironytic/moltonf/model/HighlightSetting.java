/*
 * Moltonf
 *
 * Copyright (c) 2010 Project Moltonf
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

package com.hironytic.moltonf.model;

import java.awt.Color;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 1つ分の強調表示設定を保持するクラス
 */
public class HighlightSetting {

    /** 設定名 */
    private String name;
    
    /** 有効かどうか */
    private boolean isEnabled = true;
    
    /** 正規表現で表されるマッチング文字列 */
    private String patternString = null;
    
    /** コンパイル済みパターン */
    private Pattern pattern = null;
    
    /** 強調表示色 */
    private Color highlightColor = null;
    
    /**
     * コンストラクタ
     */
    public HighlightSetting() {
        
    }

    /**
     * 強調表示設定が有効かどうかを調べます。
     * @return 有効なら true、無効なら false
     */
    public boolean isValid() {
        return pattern != null && highlightColor != null && isEnabled;
    }

    /**
     * 設定名を取得します。
     * @return 設定名を返します。
     */
    public String getName() {
        return name;
    }

    /**
     * 設定名をセットします。
     * @param name 設定名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 有効フラグの値を取得します。
     * @return 有効フラグの値
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * 有効フラグの値をセットします。
     * @param isEnabled 有効フラグの値。真なら有効です。
     */
    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    /**
     * マッチング文字列を取得します。
     * @return マッチング文字列
     */
    public String getPatternString() {
        return patternString;
    }

    /**
     * マッチング文字列をセットします。
     * @param patternString マッチング文字列を正規表現で指定します。
     */
    public void setPatternString(String patternString) {
        this.patternString = patternString;
        try {
            pattern = Pattern.compile(patternString);
        } catch (PatternSyntaxException ex) {
            pattern = null;
        }
    }

    /**
     * コンパイル済みパターンを取得します。
     * @return コンパイル済みパターンを返します。
     */
    public Pattern getPattern() {
        return pattern;
    }

    /**
     * 強調表示色を取得します。
     * @return 強調表示色を返します。
     */
    public Color getHighlightColor() {
        return highlightColor;
    }

    /**
     * 強調表示色をセットします。
     * @param highlightColor 強調表示色
     */
    public void setHighlightColor(Color highlightColor) {
        this.highlightColor = highlightColor;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "HighlightSetting [name=" + name + ", patternString="
                + patternString + "]";
    }
}
