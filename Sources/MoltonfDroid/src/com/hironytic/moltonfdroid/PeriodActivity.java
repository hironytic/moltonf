/*
 * Moltonf
 *
 * Copyright (c) 2010,2011 Hironori Ichimiya <hiron@hironytic.com>
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

package com.hironytic.moltonfdroid;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.hironytic.moltonfdroid.model.HighlightSetting;
import com.hironytic.moltonfdroid.model.StoryElement;
import com.hironytic.moltonfdroid.model.StoryPeriod;

/**
 * 1 単位期間のストーリーを表示する Activity
 */
public class PeriodActivity extends ListActivity {
    
    /** Period オブジェクトを受け取るためのチケット ID */
    public static final String EXTRA_KEY_PERIOD_TICKET_ID = "PeriodTID";
    
    /**
     * ストーリー中の画像を読み込むためのタスク
     */
    private LoadStoryImageTask loadStoryImageTask;
    
    /**
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.period);

        Moltonf app = (Moltonf)getApplication();
        StoryPeriod period = null;
        
        Intent intent = getIntent();
        if (intent != null) {
            int periodTicketID = intent.getIntExtra(EXTRA_KEY_PERIOD_TICKET_ID, 0);
            period = (StoryPeriod)app.getObjectBank().getObject(periodTicketID);
        }
        
        
//        File moltonfDir = app.getWorkDir();
//        File archiveFile = new File(moltonfDir, "jin_wolff_01999_small.xml");
//        Story story = new ArchivedStory(archiveFile);

        if (period != null) {
            loadStoryImageTask = new LoadStoryImageTask(app);
            loadStoryImageTask.execute(period.getStory());
    
            // TODO: 必ず 0 ってわけでもないはず。
            List<StoryElement> elemList = period.getStoryElements();
    
            // TODO: 強調表示設定はアプリ設定か
            List<HighlightSetting> highlightSettingList = new ArrayList<HighlightSetting>();
            HighlightSetting hlSetting;
            hlSetting = new HighlightSetting(); hlSetting.setPatternString("【.*?】");    hlSetting.setHighlightColor(Color.RED);   highlightSettingList.add(hlSetting);
            hlSetting = new HighlightSetting(); hlSetting.setPatternString("★");    hlSetting.setHighlightColor(Color.GREEN);   highlightSettingList.add(hlSetting);
            hlSetting = new HighlightSetting(); hlSetting.setPatternString("☆");    hlSetting.setHighlightColor(Color.GREEN);   highlightSettingList.add(hlSetting);
            hlSetting = new HighlightSetting(); hlSetting.setPatternString("●");    hlSetting.setHighlightColor(Color.MAGENTA);   highlightSettingList.add(hlSetting);
            hlSetting = new HighlightSetting(); hlSetting.setPatternString("○");    hlSetting.setHighlightColor(Color.MAGENTA);   highlightSettingList.add(hlSetting);
            hlSetting = new HighlightSetting(); hlSetting.setPatternString("▼");    hlSetting.setHighlightColor(Color.CYAN);   highlightSettingList.add(hlSetting);
            hlSetting = new HighlightSetting(); hlSetting.setPatternString("▽");    hlSetting.setHighlightColor(Color.CYAN);   highlightSettingList.add(hlSetting);
            hlSetting = new HighlightSetting(); hlSetting.setPatternString("■");    hlSetting.setHighlightColor(0xffffc800);   highlightSettingList.add(hlSetting);
            hlSetting = new HighlightSetting(); hlSetting.setPatternString("□");    hlSetting.setHighlightColor(0xffffc800);   highlightSettingList.add(hlSetting);
            
            StoryElementListAdapter adapter = new StoryElementListAdapter(this, elemList, highlightSettingList);
            getListView().setRecyclerListener(adapter);
            setListAdapter(adapter);
        }
    }

    /**
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        StoryElementListAdapter adapter = (StoryElementListAdapter)getListAdapter();
        setListAdapter(null);
        getListView().setRecyclerListener(null);

        if (adapter != null) {
            adapter.destroy();
        }
        if (loadStoryImageTask != null) {
            loadStoryImageTask.cancel(true);
        }
        
        super.onDestroy();
    }
    
}