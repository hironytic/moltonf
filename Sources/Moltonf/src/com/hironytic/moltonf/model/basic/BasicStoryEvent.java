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

import com.hironytic.moltonf.model.EventFamily;
import com.hironytic.moltonf.model.StoryEvent;

/**
 * 基本的な StoryEvent 実装
 */
public class BasicStoryEvent implements StoryEvent {

    /** イベントの種別 */
    private EventFamily eventFamily;

    /**
     * コンストラクタ
     */
    public BasicStoryEvent() {
    }
    
    /**
     * @see com.hironytic.moltonf.model.StoryEvent#getEventFamily()
     */
    @Override
    public EventFamily getEventFamily() {
        return eventFamily;
    }

    /**
     * eventFamily をセットします。
     * @param eventFamily セットしたい eventFamily の値
     */
    public void setEventFamily(EventFamily eventFamily) {
        this.eventFamily = eventFamily;
    }
}
