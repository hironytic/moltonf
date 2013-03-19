/*
 * Moltonf
 *
 * Copyright (c) 2013 Hironori Ichimiya <hiron@hironytic.com>
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

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hironytic.moltonfdroid.model.Workspace;
import com.hironytic.moltonfdroid.model.WorkspaceManager;

/**
 * 
 */
public class WorkspaceListActivity extends ListActivity {

    private static final int REQUEST_SELECT_PACKAGE_DIR = 100;
    
    /**
     * ワークスペース一覧に表示する1つ分のデータ
     */
    private static class WorkspaceListItem {
        private long workspaceId;
        private String title;
// 今は使わないので消しておく
//        private long updated;
        
        public WorkspaceListItem(Cursor cursor) {
            this.workspaceId = cursor.getLong(WorkspaceManager.LIST_COLUMN_INDEX_ID);
            this.title = cursor.getString(WorkspaceManager.LIST_COLUMN_INDEX_TITLE);
// 今は使わないので消しておく
//            this.updated = cursor.getLong(WorkspaceManager.LIST_COLUMN_INDEX_UPDATED);
        }

        /**
         * @return the workspaceId
         */
        public long getWorkspaceId() {
            return workspaceId;
        }

        /**
         * @return the title
         */
        public String getTitle() {
            return title;
        }

// 今は使わないので消しておく
//        /**
//         * @return the updated
//         */
//        public long getUpdated() {
//            return updated;
//        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return getTitle();
        }
    }
    
    /** ワークスペース管理オブジェクト */
    private WorkspaceManager workspaceManager;

    /**
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workspace_list);

        // ワークスペースの一覧を更新
        workspaceManager = new WorkspaceManager(getApplicationContext());
        reloadList();
        
        registerForContextMenu(getListView());
    }

    /**
     * @see android.app.ListActivity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        if (workspaceManager != null) {
            workspaceManager.close();
            workspaceManager = null;
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
        inflater.inflate(R.menu.workspace_option, menu);

        return menu.hasVisibleItems();
    }

    /**
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = false;
        int id = item.getItemId();
        switch (id) {
        case R.id.menu_workspace_new_data:
            result = processMenuNewData();
            break;
        default:
            result = super.onOptionsItemSelected(item);
            break;
        }
        
        return result;
    }

    /**
     * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        if (v == getListView()) {
            AdapterContextMenuInfo listMenuInfo = (AdapterContextMenuInfo)menuInfo;
            WorkspaceListItem listItem = (WorkspaceListItem)getListAdapter().getItem(listMenuInfo.position);
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.workspace_item_option, menu);
            menu.setHeaderTitle(listItem.getTitle());
        } else {        
            super.onCreateContextMenu(menu, v, menuInfo);
        }
    }

    /**
     * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean result = false;
        int itemId = item.getItemId();
        switch (itemId) {
        case R.id.menu_workspace_remove_data:
            result = processMenuRemoveData(item);
            break;
        default:
            result = super.onContextItemSelected(item);
            break;
        }
        return result;
    }

    /**
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        
        WorkspaceListItem item = (WorkspaceListItem)listView.getItemAtPosition(position);
        Intent intent = new Intent(this, StoryActivity.class);
        intent.putExtra(StoryActivity.EXTRA_KEY_WORKSPACE_ID, item.getWorkspaceId());
        startActivity(intent);
    }
    
    /**
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_SELECT_PACKAGE_DIR:
            processSelectPackageDirRequest(resultCode, data);
            break;
        default:
            super.onActivityResult(requestCode, resultCode, data);
            break;
        }
    }

    /**
     * ワークスペースの一覧を更新します。
     */
    private void reloadList() {
        List<WorkspaceListItem> listItems = new ArrayList<WorkspaceListItem>();
        Cursor cursor = workspaceManager.list();
        try {
            boolean hasData = cursor.moveToFirst();
            while (hasData) {
                listItems.add(new WorkspaceListItem(cursor));
                hasData = cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        ArrayAdapter<WorkspaceListItem> adapter = new ArrayAdapter<WorkspaceListItem>(this, android.R.layout.simple_list_item_1, listItems);
        setListAdapter(adapter);
    }

    /**
     * 新しい観戦データ作成メニューが選ばれたときの処理
     * @return 処理したらtrue
     */
    private boolean processMenuNewData() {
        // 観戦するプレイデータパッケージを選択させる
        Intent intent = new Intent(this, VillageListActivity.class);
        startActivityForResult(intent, REQUEST_SELECT_PACKAGE_DIR);
        return true;
    }

    /**
     * 観戦するプレイデータパッケージ選択から戻ってきたときの処理
     * @param resultCode
     * @param data
     */
    private void processSelectPackageDirRequest(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        
        File packageDir = (File)data.getSerializableExtra(VillageListActivity.EXTRA_KEY_PACKAGE_DIR);
        String title = packageDir.getName();
        
        Workspace ws = new Workspace();
        ws.setPackageDir(packageDir);
        ws.setTitle(title);
        
        workspaceManager.save(ws);
        
        reloadList();
    }

    /**
     * 観戦データの削除メニューが選ばれたときの処理
     * @param item メニュー
     * @return 処理したらtrue
     */
    private boolean processMenuRemoveData(MenuItem item) {
        AdapterContextMenuInfo listMenuInfo = (AdapterContextMenuInfo)item.getMenuInfo();
        int position = listMenuInfo.position;
        final WorkspaceListItem listItem = (WorkspaceListItem)getListAdapter().getItem(position);

        // 削除していいですか？
        String message = getResources().getString(R.string.message_workspace_remove_data_alert, listItem.getTitle());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setNegativeButton(R.string.no, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 何もしない
            }
        });
        builder.setPositiveButton(R.string.yes, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                WorkspaceListActivity.this.workspaceManager.delete(listItem.getWorkspaceId());
                reloadList();
            }
        });
        builder.show();
        return true;
    }
}
