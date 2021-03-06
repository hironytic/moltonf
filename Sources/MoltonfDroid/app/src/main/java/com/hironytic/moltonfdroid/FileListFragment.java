/*
 * Moltonf
 *
 * Copyright (c) 2011-2014 Hironori Ichimiya <hiron@hironytic.com>
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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

/**
 * ファイル選択アクティビティのメインフラグメント
 */
public class FileListFragment extends ListFragment {

    private boolean listInitialized = false; 
    
    /**
     * 一覧に表示するアイテム
     */
    private static class ListItem {
        /** ファイル */
        private File file;
        
        /** 表示名 特別な表示名がなければnullでも可 */
        private String displayName;
        
        public ListItem(File file) {
            this(file, null);
        }
        
        public ListItem(File file, String displayName) {
            this.file = file;
            this.displayName = displayName;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            if (displayName == null) {
                return file.getName();
            } else {
                return displayName;
            }
        }

        /**
         * @return the file
         */
        public File getFile() {
            return file;
        }
    }
    
    /**
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    /**
     * @see android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.file_list, container, false);

        Button cancelButton = (Button)view.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setResult(Activity.RESULT_CANCELED);
                getActivity().finish();
            }
        });

        return view;
    }

    /**
     * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        if (!listInitialized) {
            File extStrageDir = Environment.getExternalStorageDirectory();
            reloadList(extStrageDir);        
            listInitialized = true;
        }
    }

    /**
     * リストを更新します。
     * @param dir 表示するディレクトリ
     */
    private void reloadList(File dir) {
        List<ListItem> itemList = new ArrayList<ListItem>();
        if (dir != null) {
            File parentFile = dir.getParentFile();
            if (parentFile != null) {
                String displayName = getString(R.string.select_file_to_parent);
                itemList.add(new ListItem(parentFile, displayName));
            }
            File[] fileList = dir.listFiles();
            if (fileList != null) {
                for (File file : fileList) {
                    itemList.add(new ListItem(file));
                }
            }
        }
        ArrayAdapter<ListItem> adapter = new ArrayAdapter<ListItem>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, itemList);
        setListAdapter(adapter);
    }
    
    /**
     * @see android.support.v4.app.ListFragment#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
    @Override
    public void onListItemClick(ListView listView, View v, int position, long id) {
        super.onListItemClick(listView, v, position, id);

        ListItem item = (ListItem)listView.getItemAtPosition(position);
        File file = item.getFile();
        if (file.isFile()) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(FileListActivity.EXTRA_KEY_FILE, file);
            getActivity().setResult(Activity.RESULT_OK, resultIntent);
            getActivity().finish();
        } else if (file.isDirectory()) {
            reloadList(file);
        }        
    }
}
