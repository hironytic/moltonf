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

package com.hironytic.moltonf.view.event;

import java.util.EventObject;
import java.util.Set;

import com.hironytic.moltonf.model.Avatar;
import com.hironytic.moltonf.model.EventFamily;
import com.hironytic.moltonf.model.TalkType;

/**
 * フィルタの設定値が変更されたときの情報を保持するイベント
 */
@SuppressWarnings("serial")
public class FilterChangedEvent extends EventObject {
    /** フィルタの種別 */
    public enum FilterType {
        /** 発言種別 */
        TALK_TYPE,
        
        /** イベント種別 */
        EVENT_FAMILY,
        
        /** 発言者 */
        SPEAKER,
    }
    
    /** 変更されたフィルタの種別 */
    private FilterType filterType;
    
    /** 変更されたフィルタの設定値 */
    private Set<?> filterValue;
    
    /**
     * コンストラクタ
     * @param source イベントが最初に発生したオブジェクト
     */
    public FilterChangedEvent(Object source) {
        super(source);
    }
    
    /**
     * 変更されたフィルタの種別を返します。
     * @return 変更されたフィルタの種別
     */
    public FilterType getFilterType() {
        return filterType;
    }
    
    /**
     * 発言種別のフィルタ設定値を返します。
     * {@link #getFilterType()} が TALK_TYPE を返すときにのみ有効です。
     * @return 表示する発言種別が入った Set
     */
    @SuppressWarnings("unchecked")
    public Set<TalkType> getTalkTypeFilterValue() {
        if (filterType == FilterType.TALK_TYPE) {
            return (Set<TalkType>)filterValue;
        } else {
            return null;
        }
    }
    
    /**
     * イベント種別のフィルタ設定値を返します。
     * {@link #getFilterType()} が EVENT_FAMILY を返すときにのみ有効です。
     * @return 表示するイベント種別が入った Set
     */
    @SuppressWarnings("unchecked")
    public Set<EventFamily> getEventFamilyFilterValue() {
        if (filterType == FilterType.EVENT_FAMILY) {
            return (Set<EventFamily>)filterValue;
        } else {
            return null;
        }
    }
    
    /**
     * 発言人物のフィルタ設定値を返します。
     * {@link #getFilterType()} が SPEAKER を返すときにのみ有効です。
     * @return 表示する発言人物が入った Set
     */
    @SuppressWarnings("unchecked")
    public Set<Avatar> getSpeakerFilterValue() {
        if (filterType == FilterType.SPEAKER) {
            return (Set<Avatar>)filterValue;
        } else {
            return null;
        }
    }
    
    /**
     * 発言種別のフィルタ設定値をセットします。
     * @param filterValue 表示する発言種別が入った Set
     */
    public void setTalkTypeFilterValue(Set<TalkType> filterValue) {
        this.filterType = FilterType.TALK_TYPE;
        this.filterValue = filterValue;
    }
    
    /**
     * イベント種別のフィルタ設定値をセットします。
     * @param filterValue 表示するイベント種別が入った Set
     */
    public void setEventFamilyFilterValue(Set<EventFamily> filterValue) {
        this.filterType = FilterType.EVENT_FAMILY;
        this.filterValue = filterValue;
    }
    
    /**
     * 発言人物のフィルタ設定値をセットします。
     * @param filterValue 表示する発言人物が入った Set
     */
    public void setSpeakerFilterValue(Set<Avatar> filterValue) {
        this.filterType = FilterType.SPEAKER;
        this.filterValue = filterValue;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "FilterChangedEvent [filterType=" + filterType
                + ", filterValue=" + filterValue + "]";
    }
}
