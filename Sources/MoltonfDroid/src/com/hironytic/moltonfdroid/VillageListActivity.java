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
     * 村一覧に表示するパッケージディレクトリのアイテム
     */
    private static class PackageDirItem {
        /** パッケージディレクトリ */
        private File packageDir;
        
        public PackageDirItem(File packageDir) {
            this.packageDir = packageDir;
        }

        /**
         * @return the archiveFile
         */
        public File getPackageDir() {
            return packageDir;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return packageDir.getName();
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
        File playdatasDir = new File(moltonfDir, "playdatas");
        File[] packageDirList = playdatasDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) {
                    return true;
                }
                return false;
            }
        });
        List<PackageDirItem> packageDirItemList = new ArrayList<PackageDirItem>(packageDirList.length);
        for (File packageDir : packageDirList) {
            packageDirItemList.add(new PackageDirItem(packageDir));
        }
        
        ArrayAdapter<PackageDirItem> adapter = new ArrayAdapter<PackageDirItem>(this, android.R.layout.simple_list_item_1, packageDirItemList);
        setListAdapter(adapter);
    }

    /**
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
    @Override
    protected void onListItemClick(ListView listView, View v, int position, long id) {
        super.onListItemClick(listView, v, position, id);
        
        PackageDirItem item = (PackageDirItem)listView.getItemAtPosition(position);
        Moltonf.getInstance().getLogger().info(item.toString());

        Intent intent = new Intent(VillageListActivity.this, StoryActivity.class);
        intent.putExtra(StoryActivity.EXTRA_KEY_PACKAGE_DIR, item.getPackageDir());
        startActivity(intent);
    }
}
