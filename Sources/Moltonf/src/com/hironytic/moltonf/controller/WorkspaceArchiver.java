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

package com.hironytic.moltonf.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.error.YAMLException;

import com.hironytic.moltonf.Moltonf;
import com.hironytic.moltonf.MoltonfException;
import com.hironytic.moltonf.model.Workspace;

/**
 * ワークスペースをファイルに保存/ファイルから復元するために用いられるクラス
 */
public class WorkspaceArchiver {

    /** UTF-8 文字セットの名前 */
    private static final String CHARSET_UTF8 = "UTF-8";
    
    /** ファイルのバージョンを示すキー */
    private static final String KEY_VERSION = "version";
    
    /** ファイルバージョン値「1」 */
    private static final String VAL_VERSION_1 = "1";
    
    /** プレイデータアーカイブのファイルを示すキー */
    private static final String KEY_ARCHIVE_FILE = "archiveFile";
    
    /**
     * 指定されたファイルにワークスペースの内容を保存します。
     * @param archiveFile 保存するファイル
     * @param workspace 保存したいワークススペース
     */
    public static void save(File archiveFile, Workspace workspace) {
        // 書き出す内容を作成
        Map<String, Object> rootMap = new LinkedHashMap<String, Object>();
        rootMap.put(KEY_VERSION, VAL_VERSION_1);
        rootMap.put(KEY_ARCHIVE_FILE, workspace.getArchivedStoryFile().getPath());
        
        // 書き出し
        OutputStream outStream;
        try {
            outStream = new FileOutputStream(archiveFile);
            OutputStreamWriter outStreamWriter = new OutputStreamWriter(outStream, Charset.forName(CHARSET_UTF8));
            try {
                outStreamWriter.write("# Moltonf workspace file. Created by Moltonf version " + Moltonf.getVersion() + "\n");
                
                DumperOptions opt = new DumperOptions();
                opt.setDefaultFlowStyle(FlowStyle.BLOCK);
                Yaml yamlDumper = new Yaml(opt);
                yamlDumper.dump(rootMap, outStreamWriter);
            } finally {
                outStreamWriter.close();
            }
        } catch (FileNotFoundException ex) {
            throw new MoltonfException("failed to save workspace", ex);
        } catch (IOException ex) {
            throw new MoltonfException("failed to save workspace", ex);
        } catch (YAMLException ex) {
            throw new MoltonfException("failed to save workspace", ex);
        }
    }
    
    /**
     * 指定されたファイルからワークススペースの内容を読み込みます。
     * @param archiveFile 保存されているファイル
     * @return 読み込んだワークスペース
     */
    @SuppressWarnings("unchecked")
    public static Workspace load(File archiveFile) {
        // 読み込み
        Map<String, Object> rootMap;
        try {
            Yaml yamlLoader = new Yaml();
            rootMap = (Map<String, Object>)yamlLoader.load(new FileReader(archiveFile));
        } catch (FileNotFoundException ex) {
            throw new MoltonfException("failed to load workspace", ex);
        } catch (YAMLException ex) {
            throw new MoltonfException("failed to load workspace", ex);
        } 
        
        // 読み込んだ内容から Workspace を復元
        try {
            // バージョンによって処理を変更
            if (VAL_VERSION_1.equals(rootMap.get(KEY_VERSION))) {
                return loadFromVersion1(rootMap);
            } else {
                throw new MoltonfException("failed to load workspace; unknown file version");
            }
        } catch (ClassCastException ex) {
            throw new MoltonfException("failed to load workspace", ex);
        }
    }
    
    /**
     * バージョン1のファイル内容からワークスペースを復元します。
     * @param rootMap バージョン1のファイルを読み込んだ内容のマップ
     * @return 読み込んだワークスペース
     */
    private static Workspace loadFromVersion1(Map<String, Object> rootMap) {
        Workspace workspace = new Workspace();
        
        String archivedStoryPath = (String)rootMap.get(KEY_ARCHIVE_FILE);
        workspace.setArchivedStoryFile(new File(archivedStoryPath));
        
        return workspace;
    }
}
