/*
 * Moltonf
 *
 * Copyright (c) 2011 Hironori Ichimiya <hiron@hironytic.com>
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

package com.hironytic.moltonfdroid.model;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import com.hironytic.moltonfdroid.Moltonf;
import com.hironytic.moltonfdroid.MoltonfException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Moltonf ワークスペースを管理するオブジェクト
 */
public class WorkspaceManager {

    // SQLite Database
    private static final String DB_NAME = "Workspace.db";
    private static final String TABLE_WORKSPACE = "WORKSPACE";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_DATA = "data";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_UPDATED = "updated";
    
    // JSON key
    private static final String KEY_VERSION = "version";
    private static final String KEY_PACKAGE = "package";
    private static final String KEY_TITLE = "title";

    private static final int VALUE_VERSION_UNDEFINED = 0;
    private static final int VALUE_VERSION_1 = 1;
    
    /** データベースの生成とバージョンの管理を行うオブジェクト */
    private DatabaseOpenHelper openHelper;
    
    /**
     * データベースの生成とバージョンの管理を行うオブジェクト
     */
    private class DatabaseOpenHelper extends SQLiteOpenHelper {
        public DatabaseOpenHelper(Context context) {
            super(context, DB_NAME, null, 1);
        }

        /**
         * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "CREATE TABLE " + TABLE_WORKSPACE + " (" +
                            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            COLUMN_DATA + " TEXT NOT NULL, " +
                            COLUMN_NAME + " TEXT NOT NULL, " +
                            COLUMN_UPDATED + " INTEGER NOT NULL" +
                            ");";
            db.execSQL(sql);
        }

        /**
         * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // 現バージョンではアップグレードはないはず
        }
    }
    
    /**
     * コンストラクタ
     */
    public WorkspaceManager(Context context) {
        openHelper = new DatabaseOpenHelper(context);
    }
    
    /**
     * このオブジェクトを不要になったときに呼び出します。
     */
    public void close() {
        openHelper.close();
    }
    
    /**
     * IDで指定したワークスペースを読み込みます。
     * @param workspaceId ワークスペースのID
     * @return ワークスペース
     * @throws MoltonfException 読み込みができなかったとき
     */
    public Workspace load(long workspaceId) {
        // DBからJSONデータを取り出す
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_WORKSPACE,
                new String[] {COLUMN_DATA},
                COLUMN_ID + " = ?",
                new String[] {Long.toString(workspaceId)},
                null,
                null,
                null);
        if (!cursor.moveToFirst()) {
            throw new MoltonfException("failed to load workspace; workspace id " + Long.toString(workspaceId) + " is not found.");
        }
        String data = cursor.getString(0);
        cursor.close();
        
        // JSONデータからWorkspaceを生成
        Workspace workspace = new Workspace();
        workspace.setWorkspaceId(workspaceId);
        try {
            JSONObject rootObj = new JSONObject(data);
            int version = rootObj.optInt(KEY_VERSION, VALUE_VERSION_UNDEFINED);
            if (version == VALUE_VERSION_1) {
                String title  = rootObj.optString(KEY_TITLE, "");
                workspace.setTitle(title);
                
                String packageDirPath = rootObj.optString(KEY_PACKAGE, null);
                if (packageDirPath != null) {
                    // FIXME: MoltonfDroid/playdata 以下にあるものは相対パスにしたいな...
                    workspace.setPackageDir(new File(packageDirPath));
                }
            } else {
                throw new MoltonfException("failed to load workspace: unknown version");
            }
        } catch (JSONException ex) {
            throw new MoltonfException("failed to load workspace.", ex);
        }
        
        workspace.setModified(false);
        return workspace;
    }
    
    /**
     * ワークスペースを保存します。
     * このメソッドによって新しくIDが振られる場合があります。新しく振られたIDはworkspaceにセットされます。
     * @param workspace ワークスペース
     * @throws MoltonfException 保存ができなかったとき
     */
    public void save(Workspace workspace) {
        // WorkspaceのデータをJSON形式に変換
        String data;
        try {
            JSONObject rootObj = new JSONObject();
            rootObj.put(KEY_VERSION, VALUE_VERSION_1);
            
            rootObj.put(KEY_TITLE, workspace.getTitle());
            
            File packageDir = workspace.getPackageDir();
            if (packageDir != null) {
                // FIXME: MoltonfDroid/playdata 以下にあるものは相対パスにしたいな...
                rootObj.put(KEY_PACKAGE, packageDir.getAbsolutePath());
            }
            
            data = rootObj.toString();
        } catch (JSONException ex) {
            throw new MoltonfException("failed to save workspace.", ex);
        }
        
        // DBに格納
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATA, data);
        values.put(COLUMN_NAME, workspace.getTitle());
        values.put(COLUMN_UPDATED, System.currentTimeMillis() / 1000);
        SQLiteDatabase db = openHelper.getWritableDatabase();
        boolean doInsert = true;
        if (workspace.getWorkspaceId() > 0) {
            // 保存済みワークスペース
            int affectedRowCount = db.update(TABLE_WORKSPACE, values, COLUMN_ID + " = ?", new String[] { Long.toString(workspace.getWorkspaceId()) });
            if (affectedRowCount > 0) {
                doInsert = false;
            } else {
                Moltonf.getLogger().info("failed to update workspace data on db. trying insert a new row.");
            }
        }
        if (doInsert) {
            // まだ保存されていないワークスペース
            long id = db.insert(TABLE_WORKSPACE, null, values);
            if (id <= 0) {
                throw new MoltonfException("failed to insert workspace data to db.");
            }
            workspace.setWorkspaceId(id);
        }
        
        workspace.setModified(false);
    }
    
    /**
     * IDで指定したワークスペースのデータを削除します。
     * @param workspaceId ワークスペースのID
     */
    public void delete(long workspaceId) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        int affectedRowCount = db.delete(TABLE_WORKSPACE, COLUMN_ID + " = ?", new String[] { Long.toString(workspaceId) });
        if (affectedRowCount <= 0) {
            Moltonf.getLogger().info("failed to delete workspace data on db.");
        }
    }
    
    /**
     * ワークスペースデータの一覧を列挙する Cursor を返します。
     * @return
     */
    public Cursor list() {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_WORKSPACE,    // table
                new String[] {COLUMN_ID, COLUMN_NAME, COLUMN_UPDATED},  // columns
                null,   // selection
                null,   // selectionArgs,
                null,   // groupBy,
                null,   // having,
                COLUMN_UPDATED + " DESC"    // orderBy
        );
        return cursor;
    }
}
