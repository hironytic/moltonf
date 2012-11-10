/*
 * Moltonf
 *
 * Copyright (c) 2012 Hironori Ichimiya <hiron@hironytic.com>
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.hironytic.moltonfdroid.model.HighlightSetting;
import com.hironytic.moltonfdroid.model.PeriodType;
import com.hironytic.moltonfdroid.model.Story;
import com.hironytic.moltonfdroid.model.StoryElement;
import com.hironytic.moltonfdroid.model.StoryPeriod;
import com.hironytic.moltonfdroid.model.archived.ArchivedStory;
import com.hironytic.moltonfdroid.util.Proc1;

/**
 * ストーリーを表示するActivity
 */
public class StoryActivity extends Activity {

    /** Period オブジェクトを受け取るためのチケット ID */
    public static final String EXTRA_KEY_ARCHIVE_FILE = "MLTArchiveFile";

    /** ストーリーを表示するメインとなるリストビュー */
    private ListView storyListView = null;
    
    /** 表示中のストーリー */
    private Story story = null;
    
    /**
     * ストーリー中の画像を読み込むためのタスク
     */
    private LoadStoryImageTask loadStoryImageTask = null;
    
    /**
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story);

        // 読み込むストーリーの受け渡し
        Intent intent = getIntent();
        if (intent != null) {
            File archiveFile = (File)intent.getSerializableExtra(EXTRA_KEY_ARCHIVE_FILE);
            if (archiveFile != null) {
                LoadArchivedStoryTask loadTask = new LoadArchivedStoryTask();
                loadTask.execute(archiveFile);
            }
        }
    }

    /**
     * ピリオドの選択リストをアクションバーにセットします。（アクションバーが使える環境なら）
     * @param adapter
     * @param selectedListener
     */
    @TargetApi(11)
    private void setPeriodListToActionBar(SpinnerAdapter adapter, final Proc1<Integer> selectedListener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return;
        }
        
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        ActionBar.OnNavigationListener onNavigationListener = new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int position, long itemId) {
                selectedListener.perform(position);
                return true;
            }
        };
        actionBar.setListNavigationCallbacks(adapter, onNavigationListener);
    }
    
    /**
     * ストーリーのロードが終わったら呼ばれます。
     * @param story
     */
    private void onStoryLoaded(Story story) {
        // Viewの準備
        storyListView = (ListView)findViewById(R.id.story_list);
        View emptyView = findViewById(R.id.story_list_empty);
        if (emptyView != null) {
            storyListView.setEmptyView(emptyView);
        }
        
        this.story = story;
        
        // バックグラウンドで画像を用意
        loadStoryImageTask = new LoadStoryImageTask((Moltonf)this.getApplication());
        loadStoryImageTask.execute(story);

        // ピリオド切り替え
        int periodCount = story.getPeriodCount();
        ArrayList<String> list = new ArrayList<String>(story.getPeriodCount());
        for (int ix = 0; ix < periodCount; ++ix) {
            StoryPeriod period = story.getPeriod(ix);
            if (period.getPeriodType() == PeriodType.PROLOGUE) {
                list.add(getString(R.string.period_list_prologue));
            } else if (period.getPeriodType() == PeriodType.EPILOGUE) {
                list.add(getString(R.string.period_list_epilogure));
            } else {
                int periodNumber = period.getPeriodNumber();
                list.add(getString(R.string.period_list_progress_format, periodNumber));
            }
        }
        Proc1<Integer> onPeriodSelected = new Proc1<Integer>() {
            @Override
            public void perform(Integer arg) {
                //TODO:
            }
        };
        
        ArrayAdapter<String> periodAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);        
        setPeriodListToActionBar(periodAdapter, onPeriodSelected);
        
        // TODO: とりあえず初日を出しとくか
        if (story.getPeriodCount() > 0) {
            StoryPeriod period = story.getPeriod(0);
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
            storyListView.setRecyclerListener(adapter);
            storyListView.setAdapter(adapter);
        }
    }
    
    /**
     * ストーリーのロード中にエラーが発生したら呼ばれます。
     * @param ex
     */
    private void onStoryLoadError(MoltonfException ex) {
        String message = getString(R.string.failed_to_load_story);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        if (storyListView != null) {
            StoryElementListAdapter adapter = (StoryElementListAdapter)storyListView.getAdapter();
            storyListView.setAdapter(null);
            storyListView.setRecyclerListener(null);
            
            if (adapter != null) {
                adapter.destroy();
            }
        }
        
        if (loadStoryImageTask != null) {
            loadStoryImageTask.cancel(true);
        }
        
        super.onDestroy();
    }

    /**
     * アーカイブファイルを読み込んでStoryを生成するタスク
     */
    private class LoadArchivedStoryTask extends AsyncTask<File, Void, Object> {
        /** 読み込み中に表示するプログレスダイアログ */
        private ProgressDialog progressDialog;
        
        /**
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Object doInBackground(File... params) {
            try {
                File archiveFile = params[0];
                Story story = new ArchivedStory(archiveFile);
                return story;
            } catch (MoltonfException ex) {
                return ex;
            }
        }

        /**
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            String message = getString(R.string.loading_story);
            progressDialog = ProgressDialog.show(StoryActivity.this, "", message);
        }

        /**
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Object result) {
            progressDialog.dismiss();

            if (result instanceof Story) {
                Story story = (Story)result;
                StoryActivity.this.onStoryLoaded(story);
            } else if (result instanceof MoltonfException) {
                // MoltonfException が発生したとき
                StoryActivity.this.onStoryLoadError((MoltonfException)result);
            }
        }
    }
    
}
