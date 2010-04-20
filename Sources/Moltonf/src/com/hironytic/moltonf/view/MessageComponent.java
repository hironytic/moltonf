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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

/**
 * ストーリー中のテキストを表示するコンポーネント
 */
@SuppressWarnings("serial")
public class MessageComponent extends JComponent {
    
    /** 表示するメッセージ */
    private List<String> messageLines;
    
    /** 各行の TextLayout */
    private List<TextLayout> textLayouts = null;
    
    /** メッセージを表示するためのフォント */
    private Font messageFont = null;
    
    /** メッセージ表示領域の矩形 */
    private Rectangle2D.Float messageAreaRect = new Rectangle2D.Float(0, 0, 0, 0);

    /** コンポーネントに必要な領域のサイズ */
    private Dimension2DFloat areaSize = new Dimension2DFloat();
    
    /**
     * コンストラクタ
     */
    public MessageComponent() {
    }

    /**
     * このコンポーネントで表示するメッセージをセットします。
     * @param message メッセージ行のリスト
     */
    public void setMessage(List<String> messageLines) {
        this.messageLines = messageLines;
    }
    
    /**
     * メッセージを表示するためのフォントを取得します。
     * @return messageFont を返します。
     */
    public Font getMessageFont() {
        return messageFont;
    }

    /**
     * メッセージを表示するためのフォントをセットします。
     * @param messageFont セットしたい messageFont の値
     */
    public void setMessageFont(Font messageFont) {
        this.messageFont = messageFont;
    }

    /**
     * コンポーネントに必要な領域のサイズを取得します。
     * @return コンポーネントに必要な領域のサイズを返します。
     */
    public Dimension2D getAreaSize() {
        return areaSize;
    }

    /**
     * レイアウトの更新を行います。
     * @param width 表示幅
     */
    public void updateLayout(float width) {
        textLayouts = null;
        areaSize.width = width;
        areaSize.height = 0f;

        Graphics2D g2 = (Graphics2D)getGraphics();
        if (g2 != null && width > 0f) {
            textLayouts = new ArrayList<TextLayout>();
            for (String line : messageLines) {
                if (line.isEmpty()) {
                    line = "\n";    // TODO: 1文字存在しないと困るので。しかし本来はどうしよう。
                }
                AttributedString attributedString = new AttributedString(line);
                attributedString.addAttribute(TextAttribute.FONT, getFont());
                attributedString.addAttribute(TextAttribute.BACKGROUND, Paint.TRANSLUCENT);
                attributedString.addAttribute(TextAttribute.FOREGROUND, Color.BLACK);   // TODO: 色
                
                AttributedCharacterIterator charItr = attributedString.getIterator();
                FontRenderContext frContext = g2.getFontRenderContext();
                LineBreakMeasurer measurer = new LineBreakMeasurer(charItr, frContext);
                while (measurer.getPosition() < line.length()) {
                    TextLayout oneLineLayout = measurer.nextLayout(width);
                    textLayouts.add(oneLineLayout);
                    areaSize.height += getLineHeight(oneLineLayout);
                }
            }
        }
        
        Dimension preferredSize = new Dimension();
        preferredSize.setSize(areaSize);
        setPreferredSize(preferredSize);
        
        revalidate();
    }
    
    /**
     * 指定された TextLayout の 1 行の幅を計算します。
     * @param lineLayout 計算したい TextLayout オブジェクト
     * @return 1行の幅
     */
    private float getLineHeight(TextLayout lineLayout) {
        float lineHeight = lineLayout.getAscent() +
        lineLayout.getDescent() +
        lineLayout.getLeading();
        return lineHeight * 1.3f;  /* TODO: G国なら lineHeight * 1.5 */
    }
    
    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;
        if (textLayouts != null) {
            float top = 0f;
            for (TextLayout lineLayout : textLayouts) {
                lineLayout.draw(g2, messageAreaRect.x, top + lineLayout.getAscent());
                top += getLineHeight(lineLayout);
            }
        }
    }
}
