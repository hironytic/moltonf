/*
 * Moltonf
 *
 * Copyright (c) 2010 Hironori Ichimiya <hiron@hironytic.com>
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

import java.util.List;

/**
 * ストーリー中の1単位期間のデータを保持するインタフェース。
 * 通常1日（夜明け～夜明け）分の期間を表します。
 */
public interface StoryPeriod {

    /**
     * データの準備ができているかどうかを調べます。
     * @return 準備ができているなら true
     */
    public boolean isReady();
    
    /**
     * データの準備ができていなければ準備を行います。
     */
    public void ready();

    /**
     * このピリオドの種類を返します。
     * @return ピリオドの種類
     */
    public PeriodType getPeriodType();

    /**
     * このピリオドの番号を返します。最初のピリオドが0、次のピリオドが1、…
     * @return ピリオドの番号
     */
    public int getPeriodNumber();
    
    /**
     * このオブジェクトが属する Story を取得します。
     * @return このオブジェクトが属する Story
     */
    public Story getStory();

    /**
     * ストーリーを構成する要素のリストを取得します。
     * @return ストーリーを構成している要素たち
     */
    public List<StoryElement> getStoryElements();

    /**
     * このピリオドの種類を設定します。
     * @param periodType ピリオドの種類
     */
    public void setPeriodType(PeriodType periodType);
    
    /**
     * このピリオドの番号を返します。最初のピリオドが0、次のピリオドが1、…
     * @param periodNumber ピリオドの番号
     */
    public void setPeriodNumber(int periodNumber);
    
    /**
     * このオブジェクトが属する Story をセットします。
     * @param story このオブジェクトが属する Story
     */
    public void setStory(Story story);
    
    /**
     * ストーリーを構成する要素のリストをセットします。
     * @param storyElements StoryElement のリスト
     */
    public void setStoryElements(List<StoryElement> storyElements);
}
