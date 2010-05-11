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

package com.hironytic.moltonf.view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * 1単位期間分のストーリーの内容を表示するクラス。 (スクロールバーを持つ外側のペイン)
 */
@SuppressWarnings("serial")
public class PeriodView extends JScrollPane implements MoltonfView {

    /** 背景色 */
    private static final Color BG_COLOR = new Color(0x776655);
    
    /** contentView をセンタリングするためのパネル */
    private final JPanel contentPanel;
    
    /** 内容を実際に表示するビュー */
    private final PeriodContentView contentView;
    
    /**
     * コンストラクタ
     */
    public PeriodView() {
        contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        contentPanel.setBackground(BG_COLOR);
        setViewportView(contentPanel);
        contentView = new PeriodContentView(this);
        contentPanel.add(contentView);
    }

    /**
     * 内容を実際に表示するビューを取得します。
     * @return 内容を実際に表示するビューを返します。
     */
    public PeriodContentView getContentView() {
        return contentView;
    }

    /**
     * @see javax.swing.JComponent#setFont(java.awt.Font)
     */
    @Override
    public void setFont(Font font) {
        super.setFont(font);
        if (contentView != null) {
            contentView.setFont(font);
        }
    }

    /**
     * @see com.hironytic.moltonf.view.MoltonfView#updateView()
     */
    @Override
    public void updateView() {
        if (contentView != null) {
            contentView.updateView();
        }
    }
    
    
    
    
}
