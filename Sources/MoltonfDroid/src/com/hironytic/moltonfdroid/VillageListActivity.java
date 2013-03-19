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
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.hironytic.moltonfdroid.model.archived.ArchiveToPackageConverter;

/**
 * 村一覧のアクティビティ
 */
public class VillageListActivity extends ListActivity {
    
    public static final String EXTRA_KEY_PACKAGE_DIR = "Moltonf.PackageDir";

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
        
        Button cancelButton = (Button)findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        
        File moltonfDir = Moltonf.getInstance().getWorkDir();
        File playdatasDir = new File(moltonfDir, "playdatas");
        if (!playdatasDir.exists()) {
            if (moltonfDir.canWrite()) {
                playdatasDir.mkdir();
            }
        }
        File[] packageDirList = playdatasDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) {
                    return true;
                } else {
                    if (pathname.getName().endsWith(".xml")) {
                        return true;
                    }
                }
                return false;
            }
        });
        if (null != packageDirList) {
            List<PackageDirItem> packageDirItemList = new ArrayList<PackageDirItem>(packageDirList.length);
            for (File packageDir : packageDirList) {
                packageDirItemList.add(new PackageDirItem(packageDir));
            }
        
            ArrayAdapter<PackageDirItem> adapter = new ArrayAdapter<PackageDirItem>(this, android.R.layout.simple_list_item_1, packageDirItemList);
            setListAdapter(adapter);
        }
    }

    /**
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
    @Override
    protected void onListItemClick(ListView listView, View v, int position, long id) {
        super.onListItemClick(listView, v, position, id);
        
        PackageDirItem item = (PackageDirItem)listView.getItemAtPosition(position);
        Moltonf.getInstance().getLogger().info(item.toString());

        File packageDir = item.getPackageDir();
        if (packageDir.isDirectory()) {
//            Intent intent = new Intent(VillageListActivity.this, StoryActivity.class);
//            intent.putExtra(StoryActivity.EXTRA_KEY_PACKAGE_DIR, packageDir);
//            startActivity(intent);
            
            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_KEY_PACKAGE_DIR, packageDir);
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            new ConvertArchiveToPackageTask().execute(packageDir);
        }
    }
    
    /**
     * プレイデータアーカイブをパッケージディレクトリに変換するタスク
     */
    private class ConvertArchiveToPackageTask extends AsyncTask<File, Void, Boolean> {
        /** 読み込み中に表示するプログレスダイアログ */
        private ProgressDialog progressDialog;
        
        /**
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Boolean doInBackground(File... params) {
            try {
                File archiveFile = params[0];
                File parentDir = archiveFile.getParentFile();
                if (!parentDir.canWrite()) {
                    return Boolean.FALSE;
                }
                String archiveFileName = archiveFile.getName();
                int extIndex = archiveFileName.lastIndexOf(".");
                String packageDirName = archiveFileName.substring(0, extIndex);
                File packageDir = new File(parentDir, packageDirName);
                
                ArchiveToPackageConverter converter = new ArchiveToPackageConverter();
                converter.convert(archiveFile, packageDir);
                return Boolean.TRUE;
            } catch (MoltonfException ex) {
                return Boolean.FALSE;
            }
        }

        /**
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            String message = "変換中";
            progressDialog = ProgressDialog.show(VillageListActivity.this, "", message);
        }

        /**
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Boolean result) {
            progressDialog.dismiss();

            // TODO: resultみて何かする？
        }
    }
}
