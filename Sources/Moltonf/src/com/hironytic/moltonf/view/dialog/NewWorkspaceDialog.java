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

package com.hironytic.moltonf.view.dialog;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.Group;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.hironytic.moltonf.Moltonf;
import com.hironytic.moltonf.util.DialogHelper;

/**
 * 新規ワークスペース作成ダイアログ
 */
public class NewWorkspaceDialog {

    /** プレイデータのファイル */
    private File playDataFile;

    /** OKボタンで閉じられたら true になります */
    private boolean isClosedByOk;
    
    /** このダイアログ内で参照するコンポーネント */
    private class DialogComponents {
        public JDialog dialog;
        public JTextField textPlayDataFile;
        public JButton buttonBrowse;
        public JButton buttonOk;
        public JButton buttonCancel;
    }
    
    /** ダイアログ内のコンポーネント */
    private DialogComponents comps = new DialogComponents();
    
    /**
     * コンストラクタ
     */
    public NewWorkspaceDialog() {
        
    }
    
    /**
     * プレイデータのファイルを取得します。
     * @return プレイデータのファイルを返します。
     */
    public File getPlayDataFile() {
        return playDataFile;
    }

    /**
     * プレイデータのファイルをセットします。
     * @param playDataFile プレイデータのファイル
     */
    public void setPlayDataFile(File playDataFile) {
        this.playDataFile = playDataFile;
    }

    /**
     * モーダルダイアログを表示します。
     * @param owner ダイアログのオーナー
     * @return ユーザーが OK でダイアログを閉じたなら true、そうでなければ false
     */
    public boolean showModally(Window owner) {
        ResourceBundle res = Moltonf.getResource();

        isClosedByOk = false;
        comps.dialog = DialogHelper.createModalDialog(owner);

        DialogHelper.setDialogContent(comps.dialog, createContent(res));
        comps.dialog.getRootPane().setDefaultButton(comps.buttonOk);
        addActionListeners();
        updateValuesToDialog();

        comps.dialog.setTitle(res.getString("newWorkspaceDialog.title"));
        comps.dialog.pack();
        comps.dialog.setLocationRelativeTo(owner);
        
        comps.dialog.setVisible(true);

        comps.dialog.dispose();
        
        return isClosedByOk;
    }
    
    /**
     * ダイアログの内容を作成します。
     * @return
     */
    private JComponent createContent(ResourceBundle res) {
        // プレイデータファイル選択部分
        Component selectContent = createContentOfPlayData(res);
        
        // OK、キャンセルボタン
        Component buttonContent = createContentOfButtons(res);
        
        // レイアウト
        Box content = new Box(BoxLayout.Y_AXIS);
        content.add(selectContent);
        content.add(DialogHelper.createVertivalRigidArea(2));
        content.add(Box.createVerticalGlue());
        content.add(buttonContent);
        
        return content;
    }
    
    /**
     * プレイデータ選択部分を作成します。
     * @return
     */
    private JComponent createContentOfPlayData(ResourceBundle res) {
        comps.textPlayDataFile = new JTextField(35);
        comps.buttonBrowse = new JButton(res.getString("newWorkspaceDialog.browse"));
        comps.buttonBrowse.setMnemonic(KeyEvent.VK_B);

        Box eyeCatchContent = new Box(BoxLayout.LINE_AXIS);
        eyeCatchContent.add(new JLabel(res.getString("newWorkspaceDialog.playData.eyeCatch")));
        eyeCatchContent.add(Box.createHorizontalGlue());
        
        JPanel fileSelectContent = new JPanel();
        GroupLayout layout = new GroupLayout(fileSelectContent);
        fileSelectContent.setLayout(layout);
        layout.setAutoCreateGaps(false);
        layout.setAutoCreateContainerGaps(false);
        Component rigidArea = DialogHelper.createHorizontalRigidArea(1);
        // --水平方向
        Group hGroup = layout.createSequentialGroup();
        Group hGroup1 = layout.createParallelGroup();
        hGroup1.addComponent(comps.textPlayDataFile);
        hGroup.addGroup(hGroup1);
        Group hGroup2 = layout.createParallelGroup();
        hGroup2.addComponent(rigidArea);
        hGroup.addGroup(hGroup2);
        Group hGroup3 = layout.createParallelGroup();
        hGroup3.addComponent(comps.buttonBrowse);
        hGroup.addGroup(hGroup3);
        layout.setHorizontalGroup(hGroup);
        // --垂直方向
        Group vGroup = layout.createSequentialGroup();
        Group vGroup1 = layout.createParallelGroup(Alignment.BASELINE);
        vGroup1.addComponent(comps.textPlayDataFile);
        vGroup1.addComponent(rigidArea);
        vGroup1.addComponent(comps.buttonBrowse);
        vGroup.addGroup(vGroup1);
        layout.setVerticalGroup(vGroup);

        Box content = new Box(BoxLayout.Y_AXIS);
        content.setBorder(DialogHelper.createTitledBorder(res.getString("newWorkspaceDialog.playData")));
        content.add(eyeCatchContent);
        content.add(DialogHelper.createVertivalRigidArea(1));
        content.add(fileSelectContent);
        
        return content;
    }

    private JComponent createContentOfButtons(ResourceBundle res) {
        comps.buttonOk = new JButton(res.getString("dialog.buttonOk"));
        comps.buttonCancel = new JButton(res.getString("dialog.buttonCancel"));
        
        return DialogHelper.createHorizontalButtons(comps.buttonOk, comps.buttonCancel);
    }

    /**
     * ダイアログのコンポーネントに各アクションのリスナー登録を行います。
     */
    private void addActionListeners() {
        comps.dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        comps.dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                performCancel();
            }
        });
        
        comps.buttonOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performOk();
            }
        });
        
        comps.buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performCancel();
            }
        });
        
        comps.buttonBrowse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performBrowseArchive();
            }
        });
    }
    
    /**
     * OK ボタンが押されたときの処理
     */
    private void performOk() {
        if (updateValuesFromDialog()) {
            isClosedByOk = true;
            comps.dialog.setVisible(false);
        }
    }
    
    /**
     * キャンセルボタンが押されたときの処理
     */
    private void performCancel() {
        isClosedByOk = false;
        comps.dialog.setVisible(false);
    }
    
    /**
     * プレイデータ アーカイブファイルの参照ボタンが押されたときの処理
     */
    private void performBrowseArchive() {
        ResourceBundle res = Moltonf.getResource();
        File initialFile = null;
        String playDataFilePath = comps.textPlayDataFile.getText();
        if (!playDataFilePath.isEmpty()) {
            initialFile = new File(playDataFilePath);
        }
        JFileChooser fileChooser = new JFileChooser(initialFile);
        fileChooser.setDialogTitle(res.getString("newWorkspaceDialog.browse.title"));
        fileChooser.setApproveButtonText(res.getString("newWorkspaceDialog.browse.btnApprove"));
        FileFilter xmlFilter = new FileNameExtensionFilter(
                res.getString("newWorkspaceDialog.browser.xmlFilter"),
                "xml");
        fileChooser.addChoosableFileFilter(xmlFilter);
        if (fileChooser.showOpenDialog(comps.dialog) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            comps.textPlayDataFile.setText(selectedFile.getAbsolutePath());
        }
    }
    
    /**
     * フィールド変数の内容をダイアログのコンポーネントに反映します。
     */
    private void updateValuesToDialog() {
        if (playDataFile != null) {
            comps.textPlayDataFile.setText(playDataFile.getPath());
        } else {
            comps.textPlayDataFile.setText("");
        }
    }
    
    /**
     * フィールド変数の内容をダイアログのコンポーネントの値で上書きします。
     * その際に入力チェックも行います。入力値が不正な場合、このメソッド内で
     * ユーザーにメッセージを表示して、false を返します。
     * @return 入力値が不正な場合は false、そうでなければ true
     */
    private boolean updateValuesFromDialog() {
        ResourceBundle res = Moltonf.getResource();
        String playDataFilePath = comps.textPlayDataFile.getText();
        playDataFile = new File(playDataFilePath);
        if (!playDataFile.isFile() || !playDataFile.canRead()) {
            JOptionPane.showMessageDialog(comps.dialog,
                    res.getString("newWorkspaceDialog.warning.cannotReadPlayData"),
                    res.getString("newWorkspaceDialog.title"),
                    JOptionPane.WARNING_MESSAGE);
            comps.textPlayDataFile.requestFocusInWindow();
            return false;
        }
        
        return true;
    }
}
