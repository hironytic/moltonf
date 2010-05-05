/*
 * Moltonf
 *
 * Copyright (c) 2010 Project Moltonf
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

package com.hironytic.moltonf.model;

import java.io.File;

/**
 * Moltonf ワークスペースの情報を保持するクラス
 */
public class Workspace {

    /** このワークスペースが扱うストーリーが記述されたプレイデータアーカイブの XML ファイル */
    private File archivedStoryFile;
    
    /** このワークスペースが扱うストーリー */
    private Story story;
    
    /**
     * コンストラクタ
     */
    public Workspace() {
        
    }
    
    /**
     * このワークスペースが扱うストーリーが記述されたプレイデータアーカイブの XML ファイルを取得します。
     * @return プレイデータアーカイブのファイル
     */
    public File getArchivedStoryFile() {
        return archivedStoryFile;
    }

    /**
     * このワークスペースが扱うストーリーを返します。
     * @return ストーリー
     */
    public Story getStory() {
        return story;
    }
    
    /**
     * このワークスペースが扱うストーリーが記述されたプレイデータアーカイブの XML ファイルをセットします。
     * @param archivedStoryFile プレイデータアーカイブのファイル
     */
    public void setArchivedStoryFile(File archivedStoryFile) {
        this.archivedStoryFile = archivedStoryFile;
    }

    /**
     * このワークスペースが扱うストーリーをセットします。
     * @param story ストーリー
     */
    public void setStory(Story story) {
        this.story = story;
    }
}
