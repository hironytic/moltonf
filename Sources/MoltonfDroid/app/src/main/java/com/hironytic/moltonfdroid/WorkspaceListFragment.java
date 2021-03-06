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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
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
import com.hironytic.moltonfdroid.model.archived.ArchiveToPackageConverter;
import com.hironytic.moltonfdroid.model.archived.PackagedStory;
import com.hironytic.moltonfdroid.util.RetainedDialogFragment;

/**
 * ワークスペース一覧アクティビティのメインフラグメント
 */
public class WorkspaceListFragment extends ListFragment {

    private static final int REQUEST_SELECT_ARCHIVE_FILE = 100;
    
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
    private WorkspaceManager workspaceManager = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (workspaceManager == null) {
            workspaceManager = new WorkspaceManager(getActivity().getApplicationContext());

            setEmptyText(getActivity().getString(R.string.no_workspace_data));
            
            // ワークスペースの一覧を更新
            reloadList();
        }
        registerForContextMenu(getListView());            
    }
    
    @Override
    public void onDestroy() {
        if (workspaceManager != null) {
            workspaceManager.close();
            workspaceManager = null;
        }
        
        super.onDestroy();
    }

    /**
     * @see android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater)
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        
        inflater.inflate(R.menu.workspace_option, menu);
    }

    /**
     * @see android.support.v4.app.Fragment#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = false;
        int id = item.getItemId();
        switch (id) {
        case R.id.menu_workspace_new_data:
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
                result = processMenuNewDataV5();
            } else {
                result = processMenuNewData();
            }
            break;
        default:
            result = super.onOptionsItemSelected(item);
            break;
        }
        
        return result;
    }

    /**
     * @see android.support.v4.app.Fragment#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        if (v == getListView()) {
            AdapterContextMenuInfo listMenuInfo = (AdapterContextMenuInfo)menuInfo;
            WorkspaceListItem listItem = (WorkspaceListItem)getListAdapter().getItem(listMenuInfo.position);
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.workspace_item_option, menu);
            menu.setHeaderTitle(listItem.getTitle());
        } else {        
            super.onCreateContextMenu(menu, v, menuInfo);
        }
    }

    /**
     * @see android.support.v4.app.Fragment#onContextItemSelected(android.view.MenuItem)
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

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        
        WorkspaceListItem item = (WorkspaceListItem)listView.getItemAtPosition(position);
        Intent intent = new Intent(getActivity(), StoryActivity.class);
        intent.putExtra(StoryActivity.EXTRA_KEY_WORKSPACE_ID, item.getWorkspaceId());
        startActivity(intent);
    }

    /**
     * @see android.support.v4.app.Fragment#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_SELECT_ARCHIVE_FILE:
            processSelectArchiveFileRequest(resultCode, data);
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
        ArrayAdapter<WorkspaceListItem> adapter = new ArrayAdapter<WorkspaceListItem>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, listItems);
        setListAdapter(adapter);
    }

    /**
     * 新しい観戦データ作成メニューが選ばれたときの処理 (Android 1.6用)
     * @return 処理したらtrue
     */
    private boolean processMenuNewData() {
        // ユーザーにプレイデータアーカイブを選択させる
        
        Intent intent = new Intent(getActivity(), FileListActivity.class);
        startActivityForResult(intent, REQUEST_SELECT_ARCHIVE_FILE);
        return true;
    }
    
    /**
     * 新しい観戦データ作成メニューが選ばれたときの処理 (Android 2.0以降)
     * @return 処理したらtrue
     */
    @TargetApi(5)
    private boolean processMenuNewDataV5() {
        // ユーザーにプレイデータアーカイブを選択させる
        
        // ACTION_GET_CONTENTで開けるものも対象にする（他のアプリ）
        Intent getContentIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getContentIntent.setType("*/*");

        // 適切な他のアプリがいなくてもいいように、ファイルを選択するアクティビティも追加
        Intent fileListIntent = new Intent(getActivity(), FileListActivity.class);
        
        Intent chooserIntent = Intent.createChooser(getContentIntent, getString(R.string.title_select_play_data_archive));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {fileListIntent});
        startActivityForResult(chooserIntent, REQUEST_SELECT_ARCHIVE_FILE);
        return true;
    }

    /**
     * 新しい観戦データ作成メニューでアーカイブファイル選択から戻ってきたときの処理
     * @param resultCode
     * @param data
     */
    private void processSelectArchiveFileRequest(int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        
        CreateNewWorkspaceTask task = new CreateNewWorkspaceTask();
        File archiveFile = (File)data.getSerializableExtra(FileListActivity.EXTRA_KEY_FILE);
        if (archiveFile != null) {
            task.execute(archiveFile);
        } else {
            Uri archiveUri = data.getData();
            task.execute(archiveUri);
        }
    }
        
    /**
     * プレイデータアーカイブから新規ワークスペースを作成するタスク
     */
    private class CreateNewWorkspaceTask extends AsyncTask<Object, Void, Boolean> {
        /** 読み込み中に表示するプログレスダイアログのフラグメント */
        private DialogFragment progressDialogFragment;
        
        /** 変換結果のパッケージディレクトリ */
        private File packageDir;
        
        /** ワークスペースのタイトル */
        private String workspaceTitle;

        /**
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Boolean doInBackground(Object... params) {
            try {
                // パッケージはMoltonfのplaydataディレクトリに作成
                File playDataDir = Moltonf.getInstance().getPlayDataDir();
                if (playDataDir == null) {
                    return Boolean.FALSE;
                }
                if (!playDataDir.exists()) {
                    if (!playDataDir.mkdir()) {
                        return Boolean.FALSE;
                    }
                }

                // 引数にはFileかUriが入ってくる
                File archiveFile = (params[0] instanceof File) ? (File)params[0] : null;
                Uri archiveUri = (params[0] instanceof Uri) ? (Uri)params[0] : null;
                
                // パッケージのディレクトリ名の元になる名前を取得
                String packageDirName = null;
                if (archiveFile != null) {
                    String archiveFileName = archiveFile.getName();
                    int extIndex = archiveFileName.lastIndexOf(".");
                    packageDirName = archiveFileName.substring(0, extIndex);
                } else if (archiveUri != null) {
                    Cursor cursor = getActivity().getContentResolver().query(archiveUri, null, null, null, null);
                    if (cursor != null) {
                        try {
                            cursor.moveToFirst();
                            String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            int extIndex = displayName.lastIndexOf(".");
                            packageDirName = displayName.substring(0, extIndex);
                        } finally {
                            cursor.close();
                        }
                    } else {
                        packageDirName = "newdata";
                    }
                }
                if (packageDirName == null) {
                    return Boolean.FALSE;
                }

                // パッケージディレクトリ名を決定
                // すでに同じ名前のディレクトリが存在する場合は連番を付けて衝突を回避する。
                int seq = 1;
                packageDir = new File(playDataDir, packageDirName);
                while (packageDir.exists()) {
                    packageDir = new File(playDataDir, String.format("%s-%d", packageDirName, seq));
                    ++seq;
                    if (seq > 9999) {   // まあ念のため...
                        throw new MoltonfException("Couldn't create unique package directory");
                    }
                }
                
                // アーカイブからパッケージに変換
                ArchiveToPackageConverter converter = new ArchiveToPackageConverter();
                if (archiveFile != null) {
                    converter.convert(archiveFile, packageDir);
                } else if (archiveUri != null) {
                    try {
                        InputStream inputStream = getActivity().getContentResolver().openInputStream(archiveUri);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        try {
                            converter.convert(reader, packageDir);
                        } finally {
                            try {
                                reader.close();
                            } catch (IOException ex) {
                                // ignore
                            }
                        }
                    } catch (FileNotFoundException ex) {
                        throw new MoltonfException(ex);
                    }
                }
                
                // 村の名前を取得
                PackagedStory story = new PackagedStory(packageDir);
                story.ready();
                workspaceTitle = story.getVillageFullName();
                
                return Boolean.TRUE;
            } catch (MoltonfException ex) {
                // TODO: packageDirNameがnullじゃなければ、そのディレクトリを消さないと
                
                return Boolean.FALSE;
            }
        }

        /**
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            final String message = WorkspaceListFragment.this.getString(R.string.message_creating_new_workspace);
            RetainedDialogFragment dialogFragment = new RetainedDialogFragment();
            dialogFragment.setDialogCreator(new RetainedDialogFragment.DialogCreator() {
                @Override
                public Dialog createDialog() {
                    ProgressDialog dialog = new ProgressDialog(getActivity());
                    dialog.setMessage(message);
                    return dialog;
                }
            });
            progressDialogFragment = dialogFragment;
            progressDialogFragment.show(getFragmentManager(), "createNewWorkspace");
        }

        /**
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Boolean result) {
            progressDialogFragment.dismiss();

            if (result.booleanValue()) {
                Workspace ws = new Workspace();
                ws.setPackageDir(packageDir);
                ws.setTitle(workspaceTitle);
                
                workspaceManager.save(ws);
                reloadList();
            } else {
                // 作成失敗
                RetainedDialogFragment dialogFragment = new RetainedDialogFragment();
                dialogFragment.setDialogCreator(new RetainedDialogFragment.DialogCreator() {
                    @Override
                    public Dialog createDialog() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(R.string.message_failed_to_create_new_workspace);
                        builder.setNeutralButton(R.string.ok, new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 何もしない
                            }
                        });
                        return builder.create();
                    }
                });
                dialogFragment.show(getFragmentManager(), "createNewWorkspaceFailedAlert");
            }
        }
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
        RetainedDialogFragment dialogFragment = new RetainedDialogFragment();
        dialogFragment.setDialogCreator(new RetainedDialogFragment.DialogCreator() {
            @Override
            public Dialog createDialog() {
                String message = getResources().getString(R.string.message_workspace_remove_data_alert, listItem.getTitle());
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                        workspaceManager.delete(listItem.getWorkspaceId());
                        reloadList();
                    }
                });
                return builder.create();
            }
        });
        dialogFragment.show(getFragmentManager(), "deleteAlert");
        return true;
    }
}
