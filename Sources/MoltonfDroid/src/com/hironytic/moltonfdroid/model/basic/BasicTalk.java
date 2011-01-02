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

import com.hironytic.moltonfdroid.model.Avatar;
import com.hironytic.moltonfdroid.model.Talk;
import com.hironytic.moltonfdroid.model.TalkType;
import com.hironytic.moltonfdroid.util.TimePart;

/**
 * 発言の情報を保持するクラス。
 */
public class BasicTalk extends BasicStoryElement implements Talk {

    /** 発言種別 */
    private TalkType talkType;
    
    /** 発言を行った人物 */
    private Avatar speaker;
    
    /** 発言時刻 */
    private TimePart time;
    
    /** 発言回数 */
    private int talkCount;
    
    /**
     * コンストラクタ
     */
    public BasicTalk() {
        
    }
    
    /**
     * 発言の種別を返します。
     * @return 発言の種別
     */
    @Override
    public TalkType getTalkType() {
        return talkType;
    }

    /**
     * 発言を行った人物を返します。
     * @return 発言を行った人物。
     */
    @Override
    public Avatar getSpeaker() {
        return speaker;
    }

    /**
     * 発言時刻を返します。
     * @return 発言時刻。
     */
    @Override
    public TimePart getTime() {
        return time;
    }

    /**
     * 発言回数を返します。
     * 発言回数は、発言種別毎、発言を行った人物毎、1 つの StoryPeriod 毎に存在します。
     */
    @Override
    public int getTalkCount() {
        return talkCount;
    }
    
    /**
     * この発言が狼の襲撃発言 (○○ ！ 今日がお前の命日だ！) かどうかを返します。
     * @return 襲撃発言なら true、そうでなければ false が返ります。
     */
    @Override
    public boolean isWolfAttack() {
        return false;
    }

    /**
     * 発言の種別をセットします。
     * @param talkType セットしたい発言の種別
     */
    @Override
    public void setTalkType(TalkType talkType) {
        this.talkType = talkType;
    }

    /**
     * 発言を行った人物をセットします。
     * @param speaker セットしたい発言を行った人物を表す Avatar オブジェクト
     */
    @Override
    public void setSpeaker(Avatar speaker) {
        this.speaker = speaker;
    }

    /**
     * 発言時刻をセットします。
     * @param time セットしたい発言時刻の値
     */
    @Override
    public void setTime(TimePart time) {
        this.time = time;
    }

    /**
     * 発言回数をセットします。
     * @param talkCount セットしたい発言回数の値
     */
    @Override
    public void setTalkCount(int talkCount) {
        this.talkCount = talkCount;
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
        if (time != null) {
            builder.append("time=");
            builder.append(time);
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
