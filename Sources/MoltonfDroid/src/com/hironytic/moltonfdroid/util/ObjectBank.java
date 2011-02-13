/*
 * Moltonf
 *
 * Copyright (c) 2011 Hironori Ichimiya <hiron@hironytic.com>
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

import java.util.HashMap;
import java.util.Map;

/**
 * 同一アプリケーション内でのオブジェクトの引き渡しに用いるクラス
 */
public class ObjectBank {
    /** 次に預けるオブジェクトに振られる予定のチケットID */
    private int nextTicketID = 1;

    /** チケットIDからオブジェクトを得るためのマップ */
    private Map<Integer, Object> ticketToObjectMap = new HashMap<Integer, Object>(); 
    
    /**
     * オブジェクトを預けます。
     * @param obj 預けるオブジェクト
     * @return オブジェクトを受けとるためのチケットID
     */
    public int putObject(Object obj) {
        int ticketID = nextTicketID;
        ++nextTicketID;
        ticketToObjectMap.put(ticketID, obj);
        return ticketID;
    }
    
    /**
     * 預けたオブジェクトを受けとります。
     * @param ticketID チケットID（{@link ObjectBank#putObject} の戻り値）
     * @return 預けたオブジェクト。該当するものがなければ null が返ります。
     */
    public Object getObject(int ticketID) {
        return ticketToObjectMap.remove(ticketID);
    }
}
