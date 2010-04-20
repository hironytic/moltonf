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

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.JScrollPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import com.hironytic.moltonf.model.Story;
import com.hironytic.moltonf.model.archive.ArchivedStoryLoader;
import com.hironytic.moltonf.resource.ResourceEntityResolver;
import com.hironytic.moltonf.view.MainFrame;
import com.hironytic.moltonf.view.PeriodContentView;

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
            docBuilder.setEntityResolver(new ResourceEntityResolver());
            Document doc = docBuilder.parse(new File(path));
            Story story = ArchivedStoryLoader.load(doc);
            
            ResourceBundle res = ResourceBundle.getBundle("com.hironytic.moltonf.resource.Resources");
            String title = res.getString("app.title");

//            StoryElement storyElement = story.getPeriods().get(1).getStoryElements().get(22);
//            MessagePanel panel = new TalkPanel((Talk) storyElement);
            Font font = new Font("ＭＳ Ｐゴシック", Font.PLAIN, 16);
//            panel.setMessageFont(font);
            
            MainFrame mainFrame = new MainFrame();
            mainFrame.setTitle(title);
            mainFrame.setLocationByPlatform(true);
            
            JScrollPane scrollPane = new JScrollPane();
            mainFrame.add(scrollPane, BorderLayout.CENTER);
            
            mainFrame.pack();
            mainFrame.setBounds(100, 100, 500, 400);

            PeriodContentView periodContent = new PeriodContentView();
            periodContent.setFont(font);
            scrollPane.setViewportView(periodContent);
            
            periodContent.setStoryPeriod(story.getPeriods().get(1));
            periodContent.updateView();

//            mainFrame.add(panel, BorderLayout.CENTER);
//            mainFrame.pack();
//            panel.setSize(468, 400);
//            panel.updateView();
//            mainFrame.setBounds(100, 100, 500, 400);
            
            mainFrame.setVisible(true);
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }        
    }

}
