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

package com.hironytic.moltonf.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.border.Border;

/**
 * ダイアログの生成を手助けするユーティリティクラス
 */
public class DialogHelper {

    /**
     * モーダルダイアログを生成します。
     * @param owner ダイアログのオーナー
     * @return 生成したダイアログ
     */
    public static JDialog createModalDialog(Window owner) {
        if (owner instanceof Dialog) {
            return new JDialog((Dialog)owner, true);
        } else if (owner instanceof Frame) {
            return new JDialog((Frame)owner, true);
        } else {
            return new JDialog(owner, Dialog.DEFAULT_MODALITY_TYPE);
        }
    }
    
    /**
     * ダイアログの内容をセットします。
     * 内容の上下左右に自動的にマージンが取られます。
     * @param dialog ダイアログ
     * @param content ダイアログの内容
     */
    public static void setDialogContent(JDialog dialog, JComponent content) {
        content.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        dialog.setLayout(new BorderLayout());
        dialog.add(content, BorderLayout.CENTER);
    }
    
    /**
     * 水平に並べたボタンのコンポーネントを返します。
     * @param buttons 中に配置するボタン
     * @return ボタンを並べたコンポーネント
     */
    public static JComponent createHorizontalButtons(JComponent... buttons) {
        @SuppressWarnings("serial")
        class ButtonsBox extends Box {
            public ButtonsBox(int axis) {
                super(axis);
            }

            @Override
            public Dimension getMaximumSize() {
                Dimension size = getPreferredSize();
                size.width = Short.MAX_VALUE;
                return size;
            }
        }

        Box panel = new ButtonsBox(BoxLayout.X_AXIS);
        panel.add(Box.createHorizontalGlue());
        for (int ix = 0; ix < buttons.length; ++ix) {
            if (ix > 0) {
                panel.add(createHorizontalRigidArea(1));
            }
            panel.add(buttons[ix]);
        }
        return panel;
    }
    
    /**
     * 水平方向のアイテム間のマージンとなるコンポーネントを生成します。
     * @param unit マージン何個分か
     * @return 生成したコンポーネント 
     */
    public static Component createHorizontalRigidArea(int unit) {
        return Box.createRigidArea(new Dimension(4 * unit, 0));
    }
    
    /**
     * 垂直方向のアイテム間のマージンとなるコンポーネントを生成します。
     * @param unit マージン何個分か
     * @return 生成したコンポーネント 
     */
    public static Component createVertivalRigidArea(int unit) {
        return Box.createRigidArea(new Dimension(0, 4 * unit));
    }
    
    /**
     * タイトル付きのボーダーを適切なマージンと共に生成します。
     * @param title タイトル文字列
     * @return 生成したボーダー
     */
    public static Border createTitledBorder(String title) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(title),
                BorderFactory.createEmptyBorder(4, 8, 8, 8));
    }
}
