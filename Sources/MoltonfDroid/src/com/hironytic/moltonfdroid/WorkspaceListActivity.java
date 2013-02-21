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

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.hironytic.moltonfdroid.model.WorkspaceManager;

/**
 * 
 */
public class WorkspaceListActivity extends ListActivity {

    /**
     * ワークスペース一覧に表示する1つ分のデータ
     */
    private static class WorkspaceListItem {
        private long workspaceId;
        private String title;
        private long updated;
        
        public WorkspaceListItem(Cursor cursor) {
            this.workspaceId = cursor.getLong(WorkspaceManager.LIST_COLUMN_INDEX_ID);
            this.title = cursor.getString(WorkspaceManager.LIST_COLUMN_INDEX_TITLE);
            this.updated = cursor.getLong(WorkspaceManager.LIST_COLUMN_INDEX_UPDATED);
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

        /**
         * @return the updated
         */
        public long getUpdated() {
            return updated;
        }

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
        workspaceManager = new WorkspaceManager(this);
        reloadList();
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
}
