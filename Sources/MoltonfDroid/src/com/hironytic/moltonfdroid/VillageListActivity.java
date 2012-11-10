/*
 * Moltonf
 *
 * Copyright (c) 2011,2012 Hironori Ichimiya <hiron@hironytic.com>
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
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * 村一覧のアクティビティ
 */
public class VillageListActivity extends ListActivity {

    /**
     * 村一覧に表示するアーカイブファイルのアイテム
     */
    private static class ArchiveFileItem {
        /** アーカイブファイル */
        private File archiveFile;
        
        public ArchiveFileItem(File archiveFile) {
            this.archiveFile = archiveFile;
        }

        /**
         * @return the archiveFile
         */
        public File getArchiveFile() {
            return archiveFile;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return archiveFile.getName();
        }
    }
    
    /**
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.village_list);
        
        File moltonfDir = Moltonf.getInstance().getWorkDir();
        File[] archiveFileList = moltonfDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (!pathname.isDirectory()) {
                    if (pathname.getName().endsWith(".xml")) {
                        return true;
                    }
                }
                return false;
            }
        });
        List<ArchiveFileItem> archiveFileItemList = new ArrayList<ArchiveFileItem>(archiveFileList.length);
        for (File archiveFile : archiveFileList) {
            archiveFileItemList.add(new ArchiveFileItem(archiveFile));
        }
        
        ArrayAdapter<ArchiveFileItem> adapter = new ArrayAdapter<ArchiveFileItem>(this, android.R.layout.simple_list_item_1, archiveFileItemList);
        setListAdapter(adapter);
    }

    /**
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
    @Override
    protected void onListItemClick(ListView listView, View v, int position, long id) {
        super.onListItemClick(listView, v, position, id);
        
        ArchiveFileItem item = (ArchiveFileItem)listView.getItemAtPosition(position);
        Moltonf.getInstance().getLogger().info(item.toString());

        Intent intent = new Intent(VillageListActivity.this, StoryActivity.class);
        intent.putExtra(StoryActivity.EXTRA_KEY_ARCHIVE_FILE, item.getArchiveFile());
        startActivity(intent);
    }
}
