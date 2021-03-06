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

import com.hironytic.moltonfdroid.model.EventFamily;
import com.hironytic.moltonfdroid.model.StoryEvent;

/**
 * 発言以外でストーリー中に発生するイベントの情報を保持するクラス
 */
public class BasicStoryEvent extends BasicStoryElement implements StoryEvent {

    /** イベントの種別 */
    private EventFamily eventFamily;
    
    /**
     * コンストラクタ
     */
    public BasicStoryEvent() {
        
    }
    
    /**
     * イベントの種別を返します。
     * @return イベントの種別
     */
    @Override
    public EventFamily getEventFamily() {
        return eventFamily;
    }
    
    /**
     * イベントの種別をセットします。
     * @param eventFamily セットしたいイベントの種別
     */
    @Override
    public void setEventFamily(EventFamily eventFamily) {
        this.eventFamily = eventFamily;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final int maxLen = 3;
        StringBuilder builder = new StringBuilder();
        builder.append("BasicStoryEvent [");
        if (eventFamily != null) {
            builder.append("eventFamily=");
            builder.append(eventFamily);
            builder.append(", ");
        }
        if (getMessageLines() != null) {
            builder.append("getMessageLines()=");
            builder.append(getMessageLines().subList(0,
                    Math.min(getMessageLines().size(), maxLen)));
        }
        builder.append("]");
        return builder.toString();
    }
}
