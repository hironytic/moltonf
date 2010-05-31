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

package com.hironytic.moltonf.model.basic;

import java.util.List;

import com.hironytic.moltonf.model.Story;
import com.hironytic.moltonf.model.StoryElement;
import com.hironytic.moltonf.model.StoryPeriod;

/**
 * ストーリーを構成する要素の基底クラス
 */
public abstract class BasicStoryElement implements StoryElement {

    /** このオブジェクトが属する StoryPeriod */
    private StoryPeriod storyPeriod;
    
    /** メッセージ */
    private List<String> messageLines;

    /**
     * コンストラクタ
     */
    public BasicStoryElement() {
        
    }
    
    /**
     * このオブジェクトが属する StoryPeriod を取得します。
     * @return このオブジェクトが属する StoryPeriod
     */
    @Override
    public StoryPeriod getStoryPeriod() {
        return storyPeriod;
    }

    /**
     * このオブジェクトが属する Story を取得します。
     * @return このオブジェクトが属する Story
     */
    @Override
    public Story getStory() {
        if (storyPeriod == null) {
            return null;
        } else {
            return storyPeriod.getStory();
        }
    }
    
    /**
     * メッセージを返します。
     * @return メッセージの行のリスト。
     */
    @Override
    public List<String> getMessageLines() {
        return messageLines;
    }
    
    /**
     * このオブジェクトが属する StoryPeriod をセットします。
     * @param storyPeriod このオブジェクトが属する StoryPeriod
     */
    @Override
    public void setStoryPeriod(StoryPeriod storyPeriod) {
        this.storyPeriod = storyPeriod;
    }
    
    /**
     * メッセージをセットします。
     * @param messageLines メッセージの行のリスト
     */
    @Override
    public void setMessageLines(List<String> messageLines) {
        this.messageLines = messageLines;
    }
}
