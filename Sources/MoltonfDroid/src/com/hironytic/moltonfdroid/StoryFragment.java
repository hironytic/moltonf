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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.hironytic.moltonfdroid.model.PeriodType;
import com.hironytic.moltonfdroid.model.Story;
import com.hironytic.moltonfdroid.model.StoryElement;
import com.hironytic.moltonfdroid.model.StoryPeriod;
import com.hironytic.moltonfdroid.model.Workspace;
import com.hironytic.moltonfdroid.util.RetainedDialogFragment;

/**
 * ストーリーを表示するActivity
 */
public class StoryFragment extends Fragment {

    /** ストーリーを表示するメインとなるリストビュー */
    private ListView storyListView = null;
    
    /** 表示中のワークスペース */
    private Workspace workspace = null;
    
    /** 現在のピリオドのインデックス */
    private int currentPeriodIndex = -1;
    
    private StoryElementListAdapter storyElementListAdapter = null;    
    
    /**
     * ストーリー中の画像を読み込むためのタスク
     */
    private LoadStoryImageTask loadStoryImageTask = null;
    
    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }
    
    /**
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    /**
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.story, container, false);
        return view;
    }

    /**
     * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
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

    /**
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        if (storyListView != null) {
            storyListView.setAdapter(null);
            storyListView.setRecyclerListener(null);
            storyListView = null;
        }
        
        if (loadStoryImageTask != null) {
            loadStoryImageTask.cancel(true);
        }
        
        super.onDestroyView();
    }

    /**
     * @see android.support.v4.app.Fragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        if (storyElementListAdapter != null) {
            storyElementListAdapter.destroy();
        }
        
        super.onDestroy();
    }

    /**
     * @see android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater)
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        
        inflater.inflate(R.menu.story_option, menu);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            menu.removeItem(R.id.menu_story_select_period);
        }            
    }

    /**
     * @see android.support.v4.app.Fragment#onPrepareOptionsMenu(android.view.Menu)
     */
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

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
    }

    /**
     * @see android.support.v4.app.Fragment#onOptionsItemSelected(android.view.MenuItem)
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
        
        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        ActionBar.OnNavigationListener onNavigationListener = new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int position, long itemId) {
                onPeriodIndexChange(position);
                return true;
            }
        };
        ArrayAdapter<String> periodListAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, makePeriodStringList());        
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

        ActionBar actionBar = getActivity().getActionBar();
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
        getActivity().setTitle(story.getVillageFullName());
        
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
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * ストーリーピリオドのロード中にエラーが発生したら呼ばれます。
     * @param ex
     */
    private void onStoryPeriodLoadError(MoltonfException ex) {
        String message = getString(R.string.failed_to_load_story);
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
            if (storyListView == null) {
                // ストーリーを表示するListViewの準備
                storyListView = (ListView)getView().findViewById(R.id.story_list);
                View emptyView = getView().findViewById(R.id.story_list_empty);
                if (emptyView != null) {
                    storyListView.setEmptyView(emptyView);
                }
                
                if (storyElementListAdapter == null) {
                    storyElementListAdapter = new StoryElementListAdapter(getActivity(), Moltonf.getInstance().getHighlightSettings(getActivity().getApplicationContext()));
                }
                storyListView.setRecyclerListener(storyElementListAdapter);
                storyListView.setAdapter(storyElementListAdapter);
            }
            
            List<StoryElement> elemList = period.getStoryElements();
            storyElementListAdapter.replaceStoryElements(elemList);
        }
    }
    
    /**
     * Storyをready状態にするタスク
     */
    private class ReadyStoryTask extends AsyncTask<Void, Void, MoltonfException> {
        /** 読み込み中に表示するプログレスダイアログのフラグメント */
        private DialogFragment progressDialogFragment;
        
        /**
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected MoltonfException doInBackground(Void... params) {
            try {
                StoryFragment.this.workspace.getStory().ready();
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
            RetainedDialogFragment dialogFragment = new RetainedDialogFragment();
            dialogFragment.setDialogCreator(new RetainedDialogFragment.DialogCreator() {
                @Override
                public Dialog createDialog() {
                    String message = getString(R.string.loading_story);
                    ProgressDialog dialog = new ProgressDialog(getActivity());
                    dialog.setMessage(message);
                    return dialog;
                }
            });
            progressDialogFragment = dialogFragment;
            progressDialogFragment.show(getFragmentManager(), "readyStory");
        }

        /**
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(MoltonfException result) {
            progressDialogFragment.dismiss();

            if (result == null) {
                StoryFragment.this.onStoryIsReady();
            } else {
                // MoltonfException が発生したとき
                StoryFragment.this.onStoryLoadError(result);
            }
        }
    }
    
    /**
     * Storyをready状態にするタスク
     */
    private class ReadyStoryPeriodTask extends AsyncTask<StoryPeriod, Void, Object> {
        /** 読み込み中に表示するプログレスダイアログのフラグメント */
        private DialogFragment progressDialogFragment;
        
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
            RetainedDialogFragment dialogFragment = new RetainedDialogFragment();
            dialogFragment.setDialogCreator(new RetainedDialogFragment.DialogCreator() {
                @Override
                public Dialog createDialog() {
                    String message = getString(R.string.loading_story);
                    ProgressDialog dialog = new ProgressDialog(getActivity());
                    dialog.setMessage(message);
                    return dialog;
                }
            });
            progressDialogFragment = dialogFragment;
            progressDialogFragment.show(getFragmentManager(), "readyStoryPeriod");
        }

        /**
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Object result) {
            progressDialogFragment.dismiss();

            if (result instanceof StoryPeriod) {
                StoryFragment.this.onStoryPeriodIsReady((StoryPeriod)result);
            } else if (result instanceof MoltonfException) {
                // MoltonfException が発生したとき
                StoryFragment.this.onStoryPeriodLoadError((MoltonfException)result);
            }
        }
    }    
}
