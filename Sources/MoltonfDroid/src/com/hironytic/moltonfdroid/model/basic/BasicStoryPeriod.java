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

package com.hironytic.moltonfdroid.model.basic;

import java.util.List;

import com.hironytic.moltonfdroid.model.PeriodType;
import com.hironytic.moltonfdroid.model.Story;
import com.hironytic.moltonfdroid.model.StoryElement;
import com.hironytic.moltonfdroid.model.StoryPeriod;

/**
 * ストーリー中の1単位期間のデータを保持するクラス。
 */
public class BasicStoryPeriod implements StoryPeriod {

    /** このオブジェクトが属する Story */    
    private Story story;
    
    /** このストーリーを構成する要素の一覧 */
    private List<StoryElement> storyElements;
    
    /** このピリオドの種別 */
    private PeriodType periodType;
    
    /** このピリオドの番号 */
    private int periodNumber;
    
    /**
     * コンストラクタ
     */
    public BasicStoryPeriod() {
        
    }

    /**
     * データの準備ができているかどうかを調べます。
     * このクラスの実装では常に true を返します。
     * @return 準備ができているなら true
     */
    @Override
    public boolean isReady() {
        return true;
    }

    /**
     * データの準備ができていなければ準備を行います。
     */
    @Override
    public void ready() {
    }
    
    /**
     * このピリオドの種類を返します。
     * @return ピリオドの種類
     */
    @Override
    public PeriodType getPeriodType() {
        return periodType;
    }

    /**
     * このピリオドの番号を返します。最初のピリオドが0、次のピリオドが1、…
     * @return ピリオドの番号
     */
    @Override
    public int getPeriodNumber() {
        return periodNumber;
    }
    
    /**
     * このオブジェクトが属する Story を取得します。
     * @return このオブジェクトが属する Story
     */
    @Override
    public Story getStory() {
        return story;
    }

    /**
     * ストーリーを構成する要素のリストを取得します。
     * @return ストーリーを構成している要素たち
     */
    @Override
    public List<StoryElement> getStoryElements() {
        return storyElements;
    }
    
    /**
     * このピリオドの種別を設定します。
     * @param periodType ピリオドの種別
     */
    @Override
    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
    }

    /**
     * このピリオドの番号を返します。最初のピリオドが0、次のピリオドが1、…
     * @param periodNumber ピリオドの番号
     */
    @Override
    public void setPeriodNumber(int periodNumber) {
        this.periodNumber = periodNumber;
    }
    
    /**
     * このオブジェクトが属する Story をセットします。
     * @param story このオブジェクトが属する Story
     */
    @Override
    public void setStory(Story story) {
        this.story = story;
    }
    
    /**
     * ストーリーを構成する要素のリストをセットします。
     * @param storyElements StoryElement のリスト
     */
    @Override
    public void setStoryElements(List<StoryElement> storyElements) {
        this.storyElements = storyElements;
    }
}
