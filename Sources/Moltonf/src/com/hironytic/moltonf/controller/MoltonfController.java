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

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.JScrollPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import com.hironytic.moltonf.model.Story;
import com.hironytic.moltonf.model.archive.ArchivedStoryLoader;
import com.hironytic.moltonf.model.basic.BasicAvatar;
import com.hironytic.moltonf.profile.ExternalDataRetriever;
import com.hironytic.moltonf.profile.ProfileManager;
import com.hironytic.moltonf.resource.ResourceEntityResolver;
import com.hironytic.moltonf.view.MainFrame;
import com.hironytic.moltonf.view.PeriodContentView;

/**
 * Moltonf アプリケーションのコントローラ
 */
public class MoltonfController {
    /** デフォルトのユーザー設定格納フォルダの名前 */
    private static final String DEFAULT_PROFILE_FOLDER_NAME = ".moltonf";
    
    /** ユーザー設定等の管理を行うオブジェクト */
    private ProfileManager profileManager;
    
    /**
     * Moltonf アプリケーションを実行します。
     * @param args アプリケーションの引数
     */
    public void run(String[] args) {
        try {
            createProfileManager();
            
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

            Font font = new Font("ＭＳ Ｐゴシック", Font.PLAIN, 16);
            
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
            
            periodContent.setStoryPeriod(story.getPeriods().get(2));
            periodContent.updateView();

            mainFrame.setVisible(true);
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }        
    }

    private void fillUpAvatar(BasicAvatar avatar) {
        // 顔画像
        Image faceIconImage = loadFaceIconImage(avatar.getFaceIconUri());
        avatar.setFaceIconImage(faceIconImage);
    }
    
    /**
     * 顔画像を読み込みます。
     * @param faceIconUri 顔画像の URI
     * @return 読み込んだ顔画像
     */
    private Image loadFaceIconImage(URI faceIconUri) {
        Image faceIconImage = null;
        try {
            URL faceIconUrl = faceIconUri.toURL();
            ExternalDataRetriever dataRetriever = getProfileManager().getExternalDataRetriever();
            InputStream inStream = dataRetriever.retrieve(faceIconUrl);
            try {
                faceIconImage = ImageIO.read(inStream);
            } finally {
                inStream.close();
            }
        } catch (IllegalArgumentException ex) {
        } catch (MalformedURLException ex) {
        } catch (IOException ex) {
        }
        
        // TODO: faceIconImage が null なら代替画像
        
        return faceIconImage;
    }
    
    /**
     * ユーザー設定等を管理する ProfileManager オブジェクトを生成します。
     */
    private void createProfileManager() {
        File profileFolder = null;
        String homeFolderPath = null;
        try {
            homeFolderPath = System.getProperty("user.home");
        } catch (SecurityException ex) {
            homeFolderPath = null;
        }
        if (homeFolderPath != null) {
            profileFolder = new File(homeFolderPath, DEFAULT_PROFILE_FOLDER_NAME);
        }
        
        profileManager = new ProfileManager(profileFolder);
    }
    
    /**
     * ユーザー設定等を管理するオブジェクトを返します。
     * @return ProfileManager オブジェクト
     */
    public ProfileManager getProfileManager() {
        return profileManager;
    }
    
}
