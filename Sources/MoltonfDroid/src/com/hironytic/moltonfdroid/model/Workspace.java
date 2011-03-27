/*
 * Moltonf
 *
 * Copyright (c) 2010,2011 Hironori Ichimiya <hiron@hironytic.com>
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

import com.hironytic.moltonfdroid.model.archived.PackagedStory;

/**
 * Moltonf ワークスペースの情報を保持するクラス
 */
public class Workspace {

    /** このワークスペースのID */
    private long workspaceId = 0;
    
    /** 最後に保存してから情報が変更されているかどうか */
    private boolean isModified = false;
    
    /** このワークスペースが扱うストーリーが記述されたプレイデータアーカイブのパッケージディレクトリ */
    private File packageDir = null;
    
    /** このワークスペースが扱うストーリー */
    private Story story = null;
    
    /** このワークスペースのタイトル */
    private String title = null;
    
    /**
     * コンストラクタ
     */
    public Workspace() {
    }

    /**
     * @return the workspaceId
     */
    public long getWorkspaceId() {
        return workspaceId;
    }

    /**
     * @param workspaceId the workspaceId to set
     */
    public void setWorkspaceId(long workspaceId) {
        this.workspaceId = workspaceId;
    }

    /**
     * @return the isModified
     */
    public boolean isModified() {
        return isModified;
    }

    /**
     * @param isModified the isModified to set
     */
    public void setModified(boolean isModified) {
        this.isModified = isModified;
    }

    /**
     * このワークスペースが扱うストーリーが記述されたプレイデータアーカイブのパッケージディレクトリを取得します。
     * @return プレイデータアーカイブのパッケージディレクトリ
     */    

    public File getPackageDir() {
        return packageDir;
    }

    /**
     * このワークスペースが扱うストーリーが記述されたプレイデータアーカイブのパッケージディレクトリをセットします。
     * @param packageDir プレイデータアーカイブのパッケージディレクトリ
     */
    public void setPackageDir(File packageDir) {
        this.packageDir = packageDir;
        this.story = new PackagedStory(this.packageDir);
        isModified = true;
    }

    /**
     * このワークスペースが扱うストーリーを返します。
     * @return ストーリー
     */
    public Story getStory() {
        return story;
    }
    
    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
        isModified = true;
    }
}
