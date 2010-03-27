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

package com.hironytic.moltonf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.hironytic.moltonf.model.Story;
import com.hironytic.moltonf.model.archive.ArchivedStoryLoader;
import com.hironytic.moltonf.view.MainFrame;

/**
 * Moltonf アプリケーションのスタートアップクラス
 */
public class Moltonf {

    /**
     * アプリケーションのエントリポイント
     * @param args アプリケーションの引数
     */
    public static void main(String[] args) {

        try {
            // 今は試しに第1引数で与えられたものを共通アーカイブ基盤のプレイデータとして読み込んでみる
            if (args.length == 0) {
                return;
            }
            String path = args[0];
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilderFactory.setNamespaceAware(true);
            docBuilderFactory.setValidating(false);
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            docBuilder.setEntityResolver(new EntityResolver() {
                @Override
                public InputSource resolveEntity(String publicId, String systemId)
                        throws SAXException, IOException {
                    if ("http://jindolf.sourceforge.jp/xml/dtd/bbsArchive-091001.dtd".equals(systemId)) {
                        InputSource source = new InputSource(new FileInputStream("/home/hiron/develop/Moltonf/EclipseWorkspace/資料/共通アーカイブ基盤整備計画/bbsArchive-091001.dtd"));
                        source.setPublicId(publicId);
                        source.setSystemId(systemId);
                        return source;
                    }
                    return null;
                }
            });
            Document doc = docBuilder.parse(new File(path));
            Story story = ArchivedStoryLoader.load(doc);
            
            //test
            ResourceBundle res = ResourceBundle.getBundle("com.hironytic.moltonf.resource.Resources");
            String title = res.getString("app.title");
            //test
            
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
            
            return;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }        
    }

}
