/*
 * Moltonf
 *
 * Copyright (c) 2010 Hironori Ichimiya <hiron@hironytic.com>
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

package com.hironytic.moltonf.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import com.hironytic.moltonf.Moltonf;

/**
 * Moltonf のメインウィンドウ
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

    /** [新規ワークスペース] コマンドの Action */
    private final CommandAction commandActionNewWorkspace = new CommandAction("mainFrame.command.newWorkspace", KeyEvent.VK_N);
    
    /** [ワークスペースを開く] コマンドの Action */
    private final CommandAction commandActionOpenWorkspace = new CommandAction("mainFrame.command.openWorkspace", KeyEvent.VK_O);
    
    /** [終了] コマンドの Action */
    private final CommandAction commandActionExit = new CommandAction("mainFrame.command.exit", KeyEvent.VK_X);
    
    /** [バージョン情報] コマンドの Action */
    private final CommandAction commandActionAbout = new CommandAction("mainFrame.command.about", KeyEvent.VK_A);
    
    /** メインのコンポーネント */
    private Component mainPane;
    
    /**
     * コンストラクタ
     */
    public MainFrame() {
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                commandActionExit.fireCommand(e.getSource());
            }
        });
        
        // タイトル
        ResourceBundle res = Moltonf.getResource();
        setTitle(res.getString("app.title"));
        
        // メニュー
        setJMenuBar(createJMenuBar());
    }
    
    /**
     * メニューバーを作成します。
     * @return メニューバー
     */
    private JMenuBar createJMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu;

        ResourceBundle res = Moltonf.getResource();

        // [ファイル]
        menu = new JMenu(res.getString("mainFrame.menu.file"));
        menu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(menu);
        menu.add(commandActionNewWorkspace);
        menu.add(commandActionOpenWorkspace);
        menu.addSeparator();
        menu.add(commandActionExit);

        // [ヘルプ]
        menu = new JMenu(res.getString("mainFrame.menu.help"));
        menu.setMnemonic(KeyEvent.VK_H);
        menuBar.add(menu);
        menu.add(commandActionAbout);
        
        return menuBar;
    }

    /**
     * メインのコンポーネントをセットします。
     * @param mainPane メインのコンポーネント。null を指定するとメインのコンポーネントを解除します。
     */
    public void setMainPane(Component mainPane) {
        if (this.mainPane != null) {
            remove(this.mainPane);
        }
        this.mainPane = mainPane;
        if (mainPane != null) {
            add(this.mainPane, BorderLayout.CENTER);
        }
        validate();
    }
    
    /**
     * [新規ワークスペース] コマンドの CommandAction を取得します。
     * @return CommandAction オブジェクト
     */
    public CommandAction getCommandActionNewWorkspace() {
        return commandActionNewWorkspace;
    }

    /**
     * [ワークスペースを開く] コマンドの CommandAction を取得します。
     * @return CommandAction オブジェクト
     */
    public CommandAction getCommandActionOpenWorkspace() {
        return commandActionOpenWorkspace;
    }

    /**
     * [終了] コマンドの CommandAction を取得します。
     * @return CommandAction オブジェクト
     */
    public CommandAction getCommandActionExit() {
        return commandActionExit;
    }

    /**
     * [バージョン情報] コマンドの CommandAction を取得します。
     * @return CommandAction オブジェクト
     */
    public CommandAction getCommandActionAbout() {
        return commandActionAbout;
    }
    
}
