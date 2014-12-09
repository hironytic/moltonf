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

package com.hironytic.moltonfdroid.util;

/**
 * 特定の日時やタイムゾーンを意識しない時刻を保持するクラス
 */
public class TimePart {

    /** 午前 0 時を起点とした経過ミリ秒 */
    private int milliseconds;
    
    /**
     * 時、分、秒、ミリ秒を指定してオブジェクトを構築します。
     * @param hour 時
     * @param minute 分
     * @param second 秒
     * @param millisecond ミリ秒
     */
    public TimePart(int hour, int minute, int second, int millisecond) {
        this((((hour * 60 + minute) * 60) + second) * 1000 + millisecond);
    }
    
    /**
     * 経過ミリ秒を指定してオブジェクトを構築します。
     * @param milliseconds 経過ミリ秒 例えば 60000 と指定すると 0 時 1 分 0 秒 000 を表します。
     */
    public TimePart(int milliseconds) {
        final int MILLISECONDS_IN_A_DAY = 60 * 60 * 1000 * 24;
        this.milliseconds = milliseconds % MILLISECONDS_IN_A_DAY;
        if (this.milliseconds < 0) {
            this.milliseconds += MILLISECONDS_IN_A_DAY;
        }
    }
    
    /**
     * 「時」を表す数値 (0 - 23) を返します。
     * @return 「時」
     */
    public int getHourPart() {
        return milliseconds / (1000 * 60 * 60);
    }
    
    /**
     * 「分」を表す数値 (0 - 59) を返します。
     * @return 「分」
     */
    public int getMinutePart() {
        return (milliseconds / (1000 * 60)) % 60;
    }
    
    /**
     * 「秒」を表す数値 (0 - 59) を返します。
     * @return 「秒」
     */
    public int getSecondPart() {
        return (milliseconds / 1000) % 60;
    }
    
    /**
     * 「ミリ秒」を表す数値 (0 - 999) を返します。
     * @return 「ミリ秒」
     */
    public int getMilliSecondPart() {
        return milliseconds % 1000;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d.%03d",
                getHourPart(),
                getMinutePart(),
                getSecondPart(),
                getMilliSecondPart());
    }
}
