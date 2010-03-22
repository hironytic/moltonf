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

import java.util.Date;
import java.util.List;

import com.hironytic.moltonf.model.Avatar;
import com.hironytic.moltonf.model.Talk;
import com.hironytic.moltonf.model.TalkType;

/**
 * 基本的な Talk 実装
 */
public class BasicTalk extends BasicStoryElement  implements Talk {

    /** 発言種別 */
    public TalkType talkType;
    
    /** 発言を行った人物 */
    public Avatar speaker;
    
    /** 発言回数 */
    public int talkCount;
    
    /** 発言時刻 */
    public Date date;
    
    /**
     * @see com.hironytic.moltonf.model.Talk#getDate()
     */
    @Override
    public Date getDate() {
        return date;
    }

    /**
     * @see com.hironytic.moltonf.model.Talk#getSpeaker()
     */
    @Override
    public Avatar getSpeaker() {
        return speaker;
    }

    /**
     * @see com.hironytic.moltonf.model.Talk#getTalkCount()
     */
    @Override
    public int getTalkCount() {
        return talkCount;
    }

    /**
     * @see com.hironytic.moltonf.model.Talk#getTalkType()
     */
    @Override
    public TalkType getTalkType() {
        return talkType;
    }

    /**
     * talkType をセットします。
     * @param talkType セットしたい talkType の値
     */
    public void setTalkType(TalkType talkType) {
        this.talkType = talkType;
    }

    /**
     * speaker をセットします。
     * @param speaker セットしたい speaker の値
     */
    public void setSpeaker(Avatar speaker) {
        this.speaker = speaker;
    }

    /**
     * talkCount をセットします。
     * @param talkCount セットしたい talkCount の値
     */
    public void setTalkCount(int talkCount) {
        this.talkCount = talkCount;
    }

    /**
     * date をセットします。
     * @param date セットしたい date の値
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final int maxLen = 3;
        StringBuilder builder = new StringBuilder();
        builder.append("BasicTalk [");
        if (speaker != null) {
            builder.append("speaker=");
            builder.append(speaker);
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
