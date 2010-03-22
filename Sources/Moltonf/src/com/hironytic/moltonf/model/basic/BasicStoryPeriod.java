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

import com.hironytic.moltonf.model.StoryElement;
import com.hironytic.moltonf.model.StoryPeriod;

/**
 * 基本的な StoryPeriod 実装。
 */
public class BasicStoryPeriod implements StoryPeriod {

    /** このストーリーを構成する要素の一覧 */
    private List<StoryElement> storyElements;
    
    /**
     * コンストラクタ
     */
    public BasicStoryPeriod() {
        
    }
    
    /**
     * @see com.hironytic.moltonf.model.StoryPeriod#getStoryElements()
     */
    @Override
    public List<StoryElement> getStoryElements() {
        return storyElements;
    }

    /**
     * storyElements をセットします。
     * @param storyElements セットしたい storyElements の値
     */
    public void setStoryElements(List<StoryElement> storyElements) {
        this.storyElements = storyElements;
    }
}
