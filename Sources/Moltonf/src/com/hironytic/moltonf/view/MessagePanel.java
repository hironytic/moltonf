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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.hironytic.moltonf.model.StoryElement;

/**
 * テキストのメッセージを表示するパネル
 */
@SuppressWarnings("serial")
public abstract class MessagePanel extends JPanel {
    
    /** 表示するメッセージを持った StoryElement */
    private StoryElement storyElement;
    
    /** 各行の TextLayout */
    private List<TextLayout> textLayouts = null;
    
    /** メッセージを表示するためのフォント */
    private Font messageFont = null;
    
    /** メッセージ表示領域の矩形 */
    private Rectangle2D.Float messageAreaRect = new Rectangle2D.Float(0, 0, 0, 0);
    
    /**
     * コンストラクタ
     * @param storyElement このパネルが対象とするメッセージを持った StoryElement オブジェクト
     */
    public MessagePanel(StoryElement storyElement) {
        this.storyElement = storyElement;
    }

    /**
     * storyElement を取得します。
     * @return storyElement を返します。
     */
    public StoryElement getStoryElement() {
        return storyElement;
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
     * ビューの更新を行います。
     */
    public void updateView() {
        textLayouts = null;

        Graphics2D g2 = (Graphics2D)getGraphics();
        if (g2 == null) {
            return;
        }
        if (messageFont == null) {
            return;
        }
        float messageAreaWidth = getMessageAreaWidth();
        if (messageAreaWidth <= 0.0f) {
            return;
        }
        
        Point2D.Float topLeft = getMessageAreaTopLeft(new Point2D.Float());
        messageAreaRect.x = topLeft.x;
        messageAreaRect.y = topLeft.y;
        messageAreaRect.width = messageAreaWidth;
        messageAreaRect.height = 0;
        
        textLayouts = new ArrayList<TextLayout>();
        for (String line : storyElement.getMessageLines()) {
            if (line.isEmpty()) {   // TODO:
                line = "\n";
            }
            AttributedString attributedString = new AttributedString(line);
            attributedString.addAttribute(TextAttribute.FONT, messageFont);
            attributedString.addAttribute(TextAttribute.BACKGROUND, Paint.TRANSLUCENT);
            attributedString.addAttribute(TextAttribute.FOREGROUND, Color.BLACK);
            
            AttributedCharacterIterator charItr = attributedString.getIterator();
            FontRenderContext frContext = g2.getFontRenderContext();
            LineBreakMeasurer measurer = new LineBreakMeasurer(charItr, frContext);
            while (measurer.getPosition() < line.length()) {
                TextLayout oneLineLayout = measurer.nextLayout(messageAreaWidth);
                textLayouts.add(oneLineLayout);
                messageAreaRect.height += getLineHeight(oneLineLayout);
            }
        }
        
        revalidate();
    }
    
    private float getLineHeight(TextLayout lineLayout) {
        float lineHeight = lineLayout.getAscent() +
        lineLayout.getDescent() +
        lineLayout.getLeading();
        return lineHeight;  /* TODO: G国なら lineHeight * 1.5 */
    }
    
    /**
     * メッセージの文字色を返します。
     * @return 文字色
     */
    protected abstract Color getMessageTextColor();

    /**
     * テキストのメッセージを表示するエリアの幅を返します。
     * サブクラスでオーバーライドされます。
     * @return メッセージ表示エリアの幅
     */
    protected abstract float getMessageAreaWidth();
    
    /**
     * メッセージ表示領域の左上位置を返します。
     * @param pt このオブジェクトに左上位置を設定します。
     * @return pt を返します。
     */
    protected Point2D.Float getMessageAreaTopLeft(Point2D.Float pt) {
        pt.x = 0;
        pt.y = 0;
        return pt;
    }
    
    /**
     * メッセージ表示領域の矩形を返します。
     * @return メッセージ表示領域の矩形
     */
    protected Rectangle2D.Float getMessageAreaRect() {
        return messageAreaRect;
    }
    
    /**
     * メッセージを描画する前の背景を描画します。
     * @param g
     */
    protected void paintMessageBackground(Graphics g) {
    }
    
    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 背景の描画
        paintMessageBackground(g);
        
        Graphics2D g2 = (Graphics2D)g;
        
        if (textLayouts != null) {
            float top = messageAreaRect.y;
            for (TextLayout lineLayout : textLayouts) {
                lineLayout.draw(g2, messageAreaRect.x, top + lineLayout.getAscent());
                top += getLineHeight(lineLayout);
            }
        }
    }
}
