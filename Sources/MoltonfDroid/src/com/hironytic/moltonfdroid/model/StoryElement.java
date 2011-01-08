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
 * ストーリーを構成する要素のインタフェース。
 */
public interface StoryElement {

    /**
     * このオブジェクトが属する StoryPeriod を取得します。
     * @return このオブジェクトが属する StoryPeriod
     */
    public StoryPeriod getStoryPeriod();

    /**
     * このオブジェクトが属する Story を取得します。
     * @return このオブジェクトが属する Story
     */
    public Story getStory();
    
    /**
     * メッセージを返します。
     * @return メッセージの行のリスト。
     */
    public List<String> getMessageLines();
    
    /**
     * このオブジェクトが属する StoryPeriod をセットします。
     * @param storyPeriod このオブジェクトが属する StoryPeriod
     */
    public void setStoryPeriod(StoryPeriod storyPeriod);
    
    /**
     * メッセージをセットします。
     * @param messageLines メッセージの行のリスト
     */
    public void setMessageLines(List<String> messageLines);
}