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

package com.hironytic.moltonf.controller;

import java.util.Set;

import com.hironytic.moltonf.model.Avatar;
import com.hironytic.moltonf.model.EventFamily;
import com.hironytic.moltonf.model.TalkType;
import com.hironytic.moltonf.view.PeriodView;
import com.hironytic.moltonf.view.event.FilterChangeListener;
import com.hironytic.moltonf.view.event.FilterChangedEvent;
import com.hironytic.moltonf.view.sidebar.FilterSideBar;

/**
 * フィルタ状態を管理するクラス
 */
public class FilterManager implements FilterChangeListener {
    
    /** コントローラー */
    private MoltonfController controller;
    
    /**
     * コンストラクタ
     */
    public FilterManager(MoltonfController controller) {
        this.controller = controller;
    }

    /**
     * @see com.hironytic.moltonf.view.event.FilterChangeListener#filterChanged(com.hironytic.moltonf.view.event.FilterChangedEvent)
     */
    @Override
    public void filterChanged(FilterChangedEvent e) {
        PeriodView periodView = controller.getCurrentPeriodView();
        FilterSideBar filterSideBar = controller.getFilterSideBar();
        boolean isDoWithPeriodView = (periodView != null);
        boolean isDoWithFilterSideBar = (!(e.getSource() instanceof FilterSideBar) && filterSideBar != null);
        
        switch (e.getFilterType()) {
        case TALK_TYPE:
            {
                Set<TalkType> filterValue = e.getTalkTypeFilterValue();
                if (isDoWithPeriodView) {
                    if (filterValue.isEmpty()) {
                        periodView.setTalkTypeFilter(null);
                    } else {
                        periodView.setTalkTypeFilter(filterValue);
                    }
                }
                if (isDoWithFilterSideBar) {
                    filterSideBar.setTalkTypeFilter(filterValue);
                }
            }
            break;
        case EVENT_FAMILY:
            {
                Set<EventFamily> filterValue = e.getEventFamilyFilterValue();
                if (isDoWithPeriodView) {
                    if (filterValue.isEmpty()) {
                        periodView.setEventFamilyFilter(null);
                    } else {
                        periodView.setEventFamilyFilter(filterValue);
                    }
                }
                if (isDoWithFilterSideBar) {
                    filterSideBar.setEventFamilyFilter(filterValue);
                }
            }
            break;
        case SPEAKER:
            {
                Set<Avatar> filterValue = e.getSpeakerFilterValue();
                if (isDoWithPeriodView) {
                    if (filterValue.isEmpty()) {
                        periodView.setSpeakerFilter(null);
                    } else {
                        periodView.setSpeakerFilter(filterValue);
                    }
                }
                if (isDoWithFilterSideBar) {
                    filterSideBar.setSpeakerFilter(filterValue);
                }
            }
            break;
        }
        if (isDoWithPeriodView) {
            periodView.updateView();
        }
        if (isDoWithFilterSideBar) {
            filterSideBar.updateView();
        }
    }
}
