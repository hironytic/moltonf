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

package com.hironytic.moltonf.controller;

import java.util.Set;

import com.hironytic.moltonf.model.Avatar;
import com.hironytic.moltonf.model.EventFamily;
import com.hironytic.moltonf.model.TalkType;
import com.hironytic.moltonf.view.PeriodView;
import com.hironytic.moltonf.view.sidebar.FilterSideBar;
import com.hironytic.moltonf.view.sidebar.FilterSideBar.FilterType;

/**
 * フィルタ状態を管理するクラス
 */
public class FilterManager implements FilterSideBar.FilterChangeListener {
    
    /** コントローラー */
    private MoltonfController controller;
    
    /**
     * コンストラクタ
     */
    public FilterManager(MoltonfController controller) {
        this.controller = controller;
    }

    /**
     * @see com.hironytic.moltonf.view.sidebar.FilterSideBar.FilterChangeListener#filterChanged(com.hironytic.moltonf.view.sidebar.FilterSideBar.FilterType)
     */
    @Override
    public void filterChanged(FilterType filterType) {
        FilterSideBar sideBar = controller.getFilterSideBar();
        PeriodView periodView = controller.getCurrentPeriodView();
        if (sideBar == null || periodView == null) {
            return;
        }
        
        if (filterType == FilterType.TALK_KIND) {
            Set<TalkType> filterValue = sideBar.getTalkTypeFilter();
            if (filterValue.isEmpty()) {
                periodView.setTalkTypeFilter(null);
            } else {
                periodView.setTalkTypeFilter(filterValue);
            }
        } else if (filterType == FilterType.EVENT_FAMILY) {
            Set<EventFamily> filterValue = sideBar.getEventFamilyFilter();
            if (filterValue.isEmpty()) {
                periodView.setEventFamilyFilter(null);
            } else {
                periodView.setEventFamilyFilter(filterValue);
            }
        } else if (filterType == FilterType.SPEAKER) {
            Set<Avatar> filterValue = sideBar.getSpeakerFilter();
            if (filterValue.isEmpty()) {
                periodView.setSpeakerFilter(null);
            } else {
                periodView.setSpeakerFilter(filterValue);
            }
        }
        
        periodView.updateView();
    }
}
