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

import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.hironytic.moltonf.Moltonf;
import com.hironytic.moltonf.MoltonfException;
import com.hironytic.moltonf.model.Avatar;
import com.hironytic.moltonf.model.Story;
import com.hironytic.moltonf.model.Workspace;
import com.hironytic.moltonf.model.archive.ArchivedStoryLoader;
import com.hironytic.moltonf.view.MainFrame;
import com.hironytic.moltonf.view.PeriodView;
import com.hironytic.moltonf.view.dialog.NewWorkspaceDialog;

/**
 * Moltonf アプリケーションのコントローラ
 */
public class MoltonfController {
    /** デフォルトのユーザー設定格納フォルダの名前 */
    private static final String DEFAULT_PROFILE_FOLDER_NAME = ".moltonf";
    
    /** ワークスペースファイルの拡張子 */
    private static final String WORKSPACE_FILE_EXTENSION = "mtfws";
    
    /** ユーザー設定等の管理を行うオブジェクト */
    private ProfileManager profileManager;
    
    /** メインウィンドウ */
    private MainFrame mainFrame;

    /** ストーリーを表示している部分のタブペイン */
    private JTabbedPane periodTabbedPane;
    
    /** 現在開いているワークスペース */
    private Workspace currentWorkspace;
    
    /** MoltonfController のコマンドハンドラとして用いる ActionListener */
    private abstract class CommandActionListener implements ActionListener {
        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public final void actionPerformed(ActionEvent e) {
            try {
                commandExecuted(e);
            } catch (MoltonfException ex) {
                // TODO: メッセージを出す？
            }
        }
    
        /**
         * コマンドが実行されたときに呼び出されます。
         * @param e
         */
        protected abstract void commandExecuted(ActionEvent e);
    }
    
    /**
     * Moltonf アプリケーションを実行します。
     * @param args アプリケーションの引数
     */
    public void run(String[] args) {
        try {
            createProfileManager();
            
            // TODO:
            // システムのルックアンドフィールにしておく
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            mainFrame = new MainFrame();
            mainFrame.getCommandActionNewWorkspace().addCommandListener(new CommandActionListener() {
                @Override
                public void commandExecuted(ActionEvent e) {
                    performNewWorkspace();
                }
            });
            mainFrame.getCommandActionOpenWorkspace().addCommandListener(new CommandActionListener() {
                @Override
                protected void commandExecuted(ActionEvent e) {
                    performOpenWorkspace();
                }
            });
            mainFrame.getCommandActionExit().addCommandListener(new CommandActionListener() {
                @Override
                public void commandExecuted(ActionEvent e) {
                    performExit();
                }
            });
            
            mainFrame.setLocationByPlatform(true);
            mainFrame.pack();
            mainFrame.setBounds(100, 100, 600, 400);    //TODO: アプリ設定
            mainFrame.setVisible(true);
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }        
    }

    private void fillUpAvatar(Avatar avatar) {
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
            InputStream inStream = getProfileManager().getExternalData(faceIconUrl);
            try {
                faceIconImage = ImageIO.read(inStream);
            } finally {
                inStream.close();
            }
        } catch (IllegalArgumentException ex) {
        } catch (MalformedURLException ex) {
        } catch (IOException ex) {
        } catch (MoltonfException ex) {
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
        profileManager.load();
    }
    
    /**
     * ユーザー設定等を管理するオブジェクトを返します。
     * @return ProfileManager オブジェクト
     */
    public ProfileManager getProfileManager() {
        return profileManager;
    }
    
    /**
     * ユーザーが新規ワークスペースを選択したときの処理
     */
    private void performNewWorkspace() {
        // ワークスペースの情報を設定
        NewWorkspaceDialog newWorkspaceDialog = new NewWorkspaceDialog();
        if (!newWorkspaceDialog.showModally(mainFrame)) {
            return;
        }
        
        Workspace workspace = new Workspace();
        workspace.setArchivedStoryFile(newWorkspaceDialog.getPlayDataFile());
        
        // ワークスペースの保存先を選択
        // TODO: デフォルトファイル名 村名+拡張子はどうか (例: F1999.mtfws)
        ResourceBundle res = Moltonf.getResource();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(res.getString("workspaceSaveDialog.title"));
        FileFilter xmlFilter = new FileNameExtensionFilter(
                res.getString("workspaceFilter.title"),
                WORKSPACE_FILE_EXTENSION);
        fileChooser.addChoosableFileFilter(xmlFilter);
        if (fileChooser.showSaveDialog(mainFrame) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        // ワークスペースファイルへ保存
        File workspaceFile = fileChooser.getSelectedFile();
        WorkspaceArchiver.save(workspaceFile, workspace);
        
        openWorkspace(workspaceFile);
    }
    
    /**
     * ワークスペースを開くときの処理
     */
    private void performOpenWorkspace() {
        ResourceBundle res = Moltonf.getResource();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(res.getString("workspaceOpenDialog.title"));
        FileFilter xmlFilter = new FileNameExtensionFilter(
                res.getString("workspaceFilter.title"),
                WORKSPACE_FILE_EXTENSION);
        fileChooser.addChoosableFileFilter(xmlFilter);
        if (fileChooser.showOpenDialog(mainFrame) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File workspaceFile = fileChooser.getSelectedFile();
        
        // TODO: ファイルが読めることの確認が必要
        
        openWorkspace(workspaceFile);
    }
    
    /**
     * ワークスペースを開きます。
     * @param workspaceFile ワークスペースファイル
     */
    private void openWorkspace(File workspaceFile) {
        closeWorkspace();

        // ワークスペース設定の読み込み
        Workspace workspace = WorkspaceArchiver.load(workspaceFile);
        
        // プレイデータ読み込み
        Story story;
        try {
            File playDataFile = workspace.getArchivedStoryFile();
            InputStream inStream = new BufferedInputStream(new FileInputStream(playDataFile));
            story = ArchivedStoryLoader.load(inStream);
        } catch (FileNotFoundException ex) {
            throw new MoltonfException("Failed to open workspace file", ex);
        }
        workspace.setStory(story);
        
        // プレイデータの補完
        story.setGraveIconImage(loadFaceIconImage(story.getGraveIconUri()));
        List<Avatar> avatarList = story.getAvatarList();
        for (Avatar avatar : avatarList) {
            fillUpAvatar(avatar);
        }
        getProfileManager().save(); // 画像キャッシュが変わったかもしれないので

        currentWorkspace = workspace;
        
        // ピリオドビュー作成
        PeriodView periodView = new PeriodView();
        periodTabbedPane = new JTabbedPane();
        
        periodTabbedPane.addTab("てすと", periodView); // TODO:
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JPanel(), periodTabbedPane); // TODO: 左側ペイン
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(100);  // TODO:
        mainFrame.setMainPane(splitPane);
        
        Font font = new Font("ＭＳ Ｐゴシック", Font.PLAIN, 16);   // TODO: これはアプリ設定から
        periodView.setFont(font);
        
        periodView.getContentView().setStoryPeriod(currentWorkspace.getStory().getPeriods().get(3));
        periodView.updateView();
        
        mainFrame.validate();
    }
    
    /**
     * ワークスペースを閉じます。
     */
    private void closeWorkspace() {
        if (currentWorkspace != null) {
            // TODO: ワークスペースの保存

            currentWorkspace = null;
        }
        
        mainFrame.setMainPane(null);
        periodTabbedPane = null;
    }
    
    /**
     * ユーザーが終了を選択したときの処理
     */
    private void performExit() {
        mainFrame.dispose();
        System.exit(0);
    }
}
