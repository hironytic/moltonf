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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.error.YAMLException;

import com.hironytic.moltonf.Moltonf;
import com.hironytic.moltonf.MoltonfException;

/**
 * ユーザーの設定等を管理するクラス
 */
public class ProfileManager {

    /** 取得した外部データを保存するフォルダの名前 */
    private static final String EXTERNAL_DATA_FOLDER_NAME = "extdata";
    
    /** 取得済み外部データの URL とファイル名の対応を保存するファイルの名前 */
    private static final String EXTERNAL_DATA_MAP_FILE_NAME = "extdata.yaml";
    
    /** UTF-8 文字セットの名前 */
    private static final String CHARSET_UTF8 = "UTF-8";
    
    /** 設定等を保存するフォルダ */
    private File profileFolder;

    /** 取得した外部データを保存するフォルダ */
    private File externalDataFolder;
    
    /** 取得した外部データの URL から保存済みファイルのファイル名を得るマップ */
    private Map<String, String> externalDataMap;
    
    /** externalDataMap が変更されているかどうか */
    private boolean externalDataMapModified = false;
    
    /**
     * コンストラクタ
     * @param profileDir 設定等を保存するフォルダ
     */
    public ProfileManager(File profileFolder) {
        this.profileFolder = profileFolder;
        
        if (profileFolder != null) {
            this.externalDataFolder = new File(this.profileFolder, EXTERNAL_DATA_FOLDER_NAME);
        }

        if (profileFolder != null) {
            try {
                this.profileFolder.mkdirs();
            } catch (SecurityException ex) {
                this.profileFolder = null;
            }
        }
        
        if (externalDataFolder != null) {
            try {
                this.externalDataFolder.mkdirs();
            } catch (SecurityException ex) {
                this.externalDataFolder = null;
            }
        }
        
        if (externalDataFolder != null) {
            externalDataMap = new LinkedHashMap<String, String>();
        }
    }
    
    /**
     * 外部データを取得します。
     * 一度取得された外部データはプロファイルフォルダ内に保存されます。
     * 次に同じ URL の外部データを求められた場合、保存したプロファイルフォルダ内の
     * ファイルデータを返します。
     * @param url 外部データの URL
     * @return 読み込んだ InputStream。データがない場合に null を返すことがあります。
     */
    public InputStream getExternalData(URL url) {
        try {
            // プロファイルフォルダに取得しない場合はそのまま返す
            if (externalDataFolder == null || externalDataMap == null) {
                return url.openStream();
            }
            
            String urlString = url.toString();
            String fileName = externalDataMap.get(urlString);
            if (fileName == null) {
                // 未取得の場合。保存するファイル名を作成
                fileName = url.getPath();
                if (!fileName.isEmpty()) {
                    int dirSepIndex = fileName.lastIndexOf('/');
                    if (dirSepIndex > 0) {
                        fileName = fileName.substring(dirSepIndex + 1);
                    }
                }
                File saveFile = new File(externalDataFolder, fileName);
                if (saveFile.exists()) {
                    String baseName = fileName;
                    String extName = "";
                    int extSepIndex = fileName.lastIndexOf('.');
                    if (extSepIndex > 0) {
                        baseName = fileName.substring(0, extSepIndex);
                        extName = fileName.substring(extSepIndex + 1);
                    }
                    
                    long seqNum = new Date().getTime();
                    do {
                        fileName = baseName + "." + String.format("%x", seqNum) + "." + extName;
                        saveFile = new File(externalDataFolder, fileName);
                        ++seqNum;
                    } while (saveFile.exists());
                }
        
                // ファイルに保存
                HttpAccess httpAccess = new HttpAccess();
                final int BUFFER_SIZE = 4096;
                byte[] data = new byte[BUFFER_SIZE];
                InputStream externalInStream = httpAccess.doGet(url);
                try {
                    OutputStream fileOutStream = new FileOutputStream(saveFile);
                    try {
                        int readSize = externalInStream.read(data);
                        while (readSize > 0) {
                            fileOutStream.write(data, 0, readSize);
                            readSize = externalInStream.read(data);
                        }
                    } finally {
                        fileOutStream.close();
                    }
                } finally {
                    externalInStream.close();
                }
                
                // 保存したファイルを記憶
                externalDataMap.put(urlString, fileName);
                externalDataMapModified = true;
            }
            
            File storedFile = new File(externalDataFolder, fileName);
            try {
                return new FileInputStream(storedFile);
            } catch (FileNotFoundException ex) {
                throw new MoltonfException("failed to load saved external data:" + urlString, ex);
            } catch (SecurityException ex) {
                throw new MoltonfException("failed to load saved external data:" + urlString, ex);
            }
        } catch (IOException ex) {
            throw new MoltonfException("failed to load external data", ex);
        }
    }

    /**
     * 設定を読み込みます。
     */
    @SuppressWarnings("unchecked")
    public void load() {
        if (profileFolder == null) {
            return;
        }
        
        try {
            File extMapFile = new File(profileFolder, EXTERNAL_DATA_MAP_FILE_NAME);
            if (extMapFile.exists()) {
                Yaml yamlLoader = new Yaml();
                InputStream inStream = new FileInputStream(extMapFile);
                InputStreamReader inStreamReader = new InputStreamReader(inStream, Charset.forName(CHARSET_UTF8));
                try {
                    externalDataMap = (Map<String, String>)yamlLoader.load(inStreamReader);
                    externalDataMapModified = false;
                } finally {
                    inStreamReader.close();
                }
            }
        } catch (FileNotFoundException ex) {
            Moltonf.getLogger().warning("failed to load external data map file", ex);
        } catch (SecurityException ex) {
            Moltonf.getLogger().warning("failed to load external data map file", ex);
        } catch (IOException ex) {
            Moltonf.getLogger().warning("failed to load external data map file", ex);
        } catch (YAMLException ex) {
            Moltonf.getLogger().warning("failed to load external data map file", ex);
        } catch (ClassCastException ex) {
            Moltonf.getLogger().warning("failed to load external data map file", ex);
        }
    }
    
    /**
     * 設定を必要に応じて書き出します。
     */
    public void save() {
        if (profileFolder == null) {
            return;
        }

        if (externalDataMapModified) {
            try {
                File extMapFile = new File(profileFolder, EXTERNAL_DATA_MAP_FILE_NAME);
                OutputStream outStream = new FileOutputStream(extMapFile);
                OutputStreamWriter outStreamWriter = new OutputStreamWriter(outStream, Charset.forName(CHARSET_UTF8));
                try {
                    DumperOptions opt = new DumperOptions();
                    opt.setDefaultFlowStyle(FlowStyle.BLOCK);
                    Yaml yamlDumper = new Yaml(opt);
                    yamlDumper.dump(externalDataMap, outStreamWriter);
                } finally {
                    outStreamWriter.close();
                }
            } catch (FileNotFoundException ex) {
                Moltonf.getLogger().warning("failed to save external data map file", ex);
            } catch (SecurityException ex) {
                Moltonf.getLogger().warning("failed to save external data map file", ex);
            } catch (IOException ex) {
                Moltonf.getLogger().warning("failed to save external data map file", ex);
            } catch (YAMLException ex) {
                Moltonf.getLogger().warning("failed to save external data map file", ex);
            }
            externalDataMapModified = false;
        }
    }
}
