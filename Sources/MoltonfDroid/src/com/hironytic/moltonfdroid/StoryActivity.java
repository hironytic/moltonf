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

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.hironytic.moltonfdroid.model.PeriodType;
import com.hironytic.moltonfdroid.model.Story;
import com.hironytic.moltonfdroid.model.StoryElement;
import com.hironytic.moltonfdroid.model.StoryPeriod;
import com.hironytic.moltonfdroid.model.Workspace;
import com.hironytic.moltonfdroid.model.WorkspaceManager;

/**
 * ストーリーを表示するActivity
 */
public class StoryActivity extends Activity {

    /** 表示するワークスペースのID */
    public static final String EXTRA_KEY_WORKSPACE_ID = "Moltonf.WorkspaceID";
    
    /** ストーリーを表示するメインとなるリストビュー */
    private ListView storyListView = null;
    
    /** 表示中のワークスペース */
    private Workspace workspace = null;
    
    /** 現在のピリオドのインデックス */
    private int currentPeriodIndex = -1;
    
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
            long workspaceId = intent.getLongExtra(EXTRA_KEY_WORKSPACE_ID, 0);
            if (workspaceId > 0) {
                WorkspaceManager wsManager = new WorkspaceManager(getApplicationContext());
                workspace = wsManager.load(workspaceId);
                Story story = workspace.getStory();
                if (story != null) {
                    if (story.isReady()) {
                        onStoryIsReady();
                    } else {
                        ReadyStoryTask readyStoryTask = new ReadyStoryTask();
                        readyStoryTask.execute();
                    }
                }
            }
        }
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
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.story_option, menu);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            menu.removeItem(R.id.menu_story_select_period);
        }            
        
        return menu.hasVisibleItems();
    }

    /**
     * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean result = super.onPrepareOptionsMenu(menu);
        
        MenuItem selectPeriodMenu = menu.findItem(R.id.menu_story_select_period);
        if (selectPeriodMenu != null) {
            SubMenu selectPeriodSubMenu = selectPeriodMenu.getSubMenu();
            selectPeriodSubMenu.clear();
            List<String> periodList = makePeriodStringList();
            if (periodList != null) {
                for (int ix = 0; ix < periodList.size(); ++ix) {
                    MenuItem mi = selectPeriodSubMenu.add(R.id.menugroup_story_select_period, Menu.FIRST + ix, ix, periodList.get(ix));
                    mi.setChecked(ix == currentPeriodIndex);
                }
            }
            selectPeriodSubMenu.setGroupCheckable(R.id.menugroup_story_select_period, true, true);
        }        
        return result;
    }
    
    /**
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getGroupId() == R.id.menugroup_story_select_period) {
            onPeriodIndexChange(item.getItemId() - Menu.FIRST);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * ピリオドの選択リストをアクションバーにセットします。（アクションバーが使える環境なら）
     */
    @TargetApi(11)
    private void setPeriodListToActionBar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return;
        }
        
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        ActionBar.OnNavigationListener onNavigationListener = new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int position, long itemId) {
                onPeriodIndexChange(position);
                return true;
            }
        };
        ArrayAdapter<String> periodListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, makePeriodStringList());        
        actionBar.setListNavigationCallbacks(periodListAdapter, onNavigationListener);
    }
    
    /**
     * アクションバー上の日の選択を更新します。
     * @param position 選択する日
     */
    @TargetApi(11)
    private void setSelectedPeriodOnActionBar(int position) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return;
        }

        ActionBar actionBar = getActionBar();
        actionBar.setSelectedNavigationItem(position);
    }
    
    /**
     * ストーリーのロードが終わったら呼ばれます。
     */
    private void onStoryIsReady() {
        Story story = workspace.getStory();
        if (story == null) {
            return;
        }
        
        // 村の名前をタイトルにセット
        setTitle(story.getVillageFullName());
        
        // バックグラウンドで画像を用意
        loadStoryImageTask = new LoadStoryImageTask();
        loadStoryImageTask.execute(story);

        currentPeriodIndex = -1;

        // ActionBarにピリオド切り替えを追加
        setPeriodListToActionBar();

        // TODO: とりあえず初日を出しとくか
        onPeriodIndexChange(0);
    }

    /**
     * ピリオドの一覧の選択肢を入れたリストを作ります。Storyに含まれるPeriodの順番を保ちます。
     * @return
     */
    private List<String> makePeriodStringList() {
        Story story = workspace.getStory();
        if (story == null) {
            return null;
        }
        
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
        return list;
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
     * ストーリーピリオドのロード中にエラーが発生したら呼ばれます。
     * @param ex
     */
    private void onStoryPeriodLoadError(MoltonfException ex) {
        String message = getString(R.string.failed_to_load_story);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 表示するピリオドが変更されるときに呼ばれます。
     * @param periodIndex ピリオドのインデックス
     * @return
     */
    private void onPeriodIndexChange(int periodIndex) {
        if (currentPeriodIndex == periodIndex) {
            return;
        }
        
        Story story = workspace.getStory();
        if (story == null) {
            return;
        }
        
        if (story.getPeriodCount() > periodIndex) {
            currentPeriodIndex = periodIndex;
            setSelectedPeriodOnActionBar(periodIndex);
            
            StoryPeriod period = story.getPeriod(periodIndex);
            if (period.isReady()) {
                onStoryPeriodIsReady(period);
            } else {
                ReadyStoryPeriodTask task = new ReadyStoryPeriodTask();
                task.execute(period);
            }
        }
    }
 
    /**
     * ピリオドの準備ができたら呼ばれます。
     */
    private void onStoryPeriodIsReady(StoryPeriod period) {
        StoryPeriod currentPeriod = workspace.getStory().getPeriod(currentPeriodIndex);
        if (period == currentPeriod) {
            StoryElementListAdapter adapter;
            
            if (storyListView == null) {
                // ストーリーを表示するListViewの準備
                storyListView = (ListView)findViewById(R.id.story_list);
                View emptyView = findViewById(R.id.story_list_empty);
                if (emptyView != null) {
                    storyListView.setEmptyView(emptyView);
                }
                
                adapter = new StoryElementListAdapter(this, Moltonf.getInstance().getHighlightSettings(getApplicationContext()));
                storyListView.setRecyclerListener(adapter);
                storyListView.setAdapter(adapter);
            } else {
                adapter = (StoryElementListAdapter)storyListView.getAdapter();
            }
            
            List<StoryElement> elemList = period.getStoryElements();
            adapter.replaceStoryElements(elemList);
        }
    }
    
    /**
     * Storyをready状態にするタスク
     */
    private class ReadyStoryTask extends AsyncTask<Void, Void, MoltonfException> {
        /** 読み込み中に表示するプログレスダイアログ */
        private ProgressDialog progressDialog;
        
        /**
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected MoltonfException doInBackground(Void... params) {
            try {
                StoryActivity.this.workspace.getStory().ready();
                return null;
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
        protected void onPostExecute(MoltonfException result) {
            progressDialog.dismiss();

            if (result == null) {
                StoryActivity.this.onStoryIsReady();
            } else {
                // MoltonfException が発生したとき
                StoryActivity.this.onStoryLoadError(result);
            }
        }
    }
    
    /**
     * Storyをready状態にするタスク
     */
    private class ReadyStoryPeriodTask extends AsyncTask<StoryPeriod, Void, Object> {
        /** 読み込み中に表示するプログレスダイアログ */
        private ProgressDialog progressDialog;
        
        /**
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Object doInBackground(StoryPeriod... params) {
            try {
                StoryPeriod period = params[0];
                period.ready();
                return period;
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

            if (result instanceof StoryPeriod) {
                StoryActivity.this.onStoryPeriodIsReady((StoryPeriod)result);
            } else if (result instanceof MoltonfException) {
                // MoltonfException が発生したとき
                StoryActivity.this.onStoryPeriodLoadError((MoltonfException)result);
            }
        }
    }
    
}
