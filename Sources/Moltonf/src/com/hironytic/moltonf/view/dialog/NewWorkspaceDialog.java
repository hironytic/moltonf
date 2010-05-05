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

import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.Group;

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
        isClosedByOk = false;
        comps.dialog = DialogHelper.createModalDialog(owner);
        
        Container contentPane = comps.dialog.getContentPane();
        createContent(contentPane);

        addActionListeners();
        
        updateValuesToDialog();
        
        comps.dialog.pack();
        comps.dialog.setVisible(true);
        
        return isClosedByOk;
    }
    
    private void createContent(Container container) {
        // アイキャッチテキスト
        JLabel labelEyeCatch = new JLabel("プレイデータのアーカイブファイルを選択してください。");

        // プレイデータファイル選択部分
        JComponent selectPanel = createContentOfFileSelection();
        
        // OK、キャンセルボタン
        JComponent buttonPanel = createContentOfButtons();
        
        // レイアウト
        GroupLayout contentLayout = new GroupLayout(container);
        contentLayout.setAutoCreateGaps(true);
        contentLayout.setAutoCreateContainerGaps(true);
        container.setLayout(contentLayout);
        // -- 水平
        Group hGroup = contentLayout.createSequentialGroup();
        Group hGroup1 = contentLayout.createParallelGroup();
        hGroup1.addComponent(labelEyeCatch);
        hGroup1.addComponent(selectPanel);
        hGroup1.addComponent(buttonPanel);
        hGroup.addGroup(hGroup1);
        contentLayout.setHorizontalGroup(hGroup);
        // -- 垂直
        Group vGroup = contentLayout.createSequentialGroup();
        Group vGroup1 = contentLayout.createParallelGroup();
        vGroup1.addComponent(labelEyeCatch);
        vGroup.addGroup(vGroup1);
        Group vGroup2 = contentLayout.createParallelGroup();
        vGroup2.addComponent(selectPanel);
        vGroup.addGroup(vGroup2);
        Group vGroup3 = contentLayout.createParallelGroup();
        vGroup3.addComponent(buttonPanel);
        vGroup.addGroup(vGroup3);
        contentLayout.setVerticalGroup(vGroup);
        
    }
    
    private JComponent createContentOfFileSelection() {
        comps.textPlayDataFile = new JTextField();
        comps.buttonBrowse = new JButton("参照");

        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(false);
        // --水平方向
        Group hGroup = layout.createSequentialGroup();
        Group hGroup1 = layout.createParallelGroup();
        hGroup1.addComponent(comps.textPlayDataFile);
        Group hGroup2 = layout.createParallelGroup();
        hGroup2.addComponent(comps.buttonBrowse);
        hGroup.addGroup(hGroup1);
        hGroup.addGroup(hGroup2);
        layout.setHorizontalGroup(hGroup);
        // --垂直方向
        Group vGroup = layout.createSequentialGroup();
        Group vGroup1 = layout.createParallelGroup(Alignment.BASELINE);
        vGroup1.addComponent(comps.textPlayDataFile);
        vGroup1.addComponent(comps.buttonBrowse);
        vGroup.addGroup(vGroup1);
        layout.setVerticalGroup(vGroup);
        
        return panel;
    }
    
    private JComponent createContentOfButtons() {
        comps.buttonOk = new JButton("OK");
        comps.buttonCancel = new JButton("キャンセル");
        
        Box panel = new Box(BoxLayout.X_AXIS);
        panel.add(Box.createHorizontalGlue());
        panel.add(comps.buttonOk);
        panel.add(Box.createHorizontalStrut(5));
        panel.add(comps.buttonCancel);
        
        return panel;
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
        String playDataFilePath = comps.textPlayDataFile.getText();
        playDataFile = new File(playDataFilePath);
        if (!playDataFile.isFile() || !playDataFile.canRead()) {
            // TODO: よめんぞ
            return false;
        }
        
        return true;
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        NewWorkspaceDialog dialog = new NewWorkspaceDialog();
        dialog.setPlayDataFile(new File("C:\\Hiron\\Work"));
        dialog.showModally(null);
        System.exit(0);
    }
}
