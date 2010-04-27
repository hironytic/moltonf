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

import java.awt.Dimension;
import java.awt.FontMetrics;
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

    /** 1行の高さの係数（何倍するか) */
    private float lineHeightFactor;
    
    /** 1行のレイアウト情報 */
    private class LineLayout {
        /** 行の上端座標 */
        private float lineTop;
        
        /** 行の TextLayout */
        private TextLayout textLayout;
        
        /**
         * コンストラクタ
         * @param lineTop 行の上端座標
         * @param textLayout 行の TextLayout
         */
        public LineLayout(float lineTop, TextLayout textLayout) {
            this.lineTop = lineTop;
            this.textLayout = textLayout;
        }

        /**
         * lineTop を取得します。
         * @return lineTop を返します。
         */
        public float getLineTop() {
            return lineTop;
        }

        /**
         * textLayout を取得します。
         * @return textLayout を返します。
         */
        public TextLayout getTextLayout() {
            return textLayout;
        }
    }
    
    /** 各行の TextLayout */
    private List<LineLayout> lineLayouts = null;
    
    /** メッセージ表示領域の矩形 */
    private Rectangle2D.Float messageAreaRect = new Rectangle2D.Float(0, 0, 0, 0);

    /** コンポーネントに必要な領域のサイズ */
    private Dimension2DFloat areaSize = new Dimension2DFloat();
    
    /**
     * コンストラクタ
     */
    public MessageComponent() {
        lineHeightFactor = 1.3f; /* TODO: G国なら1.5f */
    }

    /**
     * lineHeightFactor を取得します。
     * @return lineHeightFactor を返します。
     */
    public float getLineHeightFactor() {
        return lineHeightFactor;
    }

    /**
     * lineHeightFactor をセットします。
     * @param lineHeightFactor セットしたい lineHeightFactor の値
     */
    public void setLineHeightFactor(float lineHeightFactor) {
        this.lineHeightFactor = lineHeightFactor;
    }

    /**
     * このコンポーネントで表示するメッセージをセットします。
     * @param message メッセージ行のリスト
     */
    public void setMessage(List<String> messageLines) {
        this.messageLines = messageLines;
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
        lineLayouts = null;
        areaSize.width = width;
        areaSize.height = 0f;

        Graphics2D g2 = (Graphics2D)getGraphics();
        if (g2 != null && width > 0f) {
            List<String> lineList = messageLines;
            // 最終行が空行の場合、その空行は無視する
            if (lineList.size() > 0 && lineList.get(lineList.size() - 1).isEmpty()) {
                lineList = lineList.subList(0, lineList.size() - 1);
            }
            lineLayouts = new ArrayList<LineLayout>();
            for (String line : lineList) {
                if (line.isEmpty()) {
                    FontMetrics fontMetrics = getFontMetrics(getFont());
                    addLineLayout(null,
                            fontMetrics.getAscent(),
                            fontMetrics.getDescent(),
                            fontMetrics.getLeading());
                } else {
                    AttributedString attributedString = new AttributedString(line);
                    attributedString.addAttribute(TextAttribute.FONT, getFont());
                    attributedString.addAttribute(TextAttribute.BACKGROUND, Paint.TRANSLUCENT);
                    attributedString.addAttribute(TextAttribute.FOREGROUND, getForeground());
                    
                    AttributedCharacterIterator charItr = attributedString.getIterator();
                    FontRenderContext frContext = g2.getFontRenderContext();
                    LineBreakMeasurer measurer = new LineBreakMeasurer(charItr, frContext);
                    while (measurer.getPosition() < line.length()) {
                        TextLayout oneLineLayout = measurer.nextLayout(width);
                        addLineLayout(oneLineLayout,
                                oneLineLayout.getAscent(),
                                oneLineLayout.getDescent(),
                                oneLineLayout.getLeading());
                    }
                }
            }
        }

        Dimension preferredSize = new Dimension();
        preferredSize.setSize(areaSize);
        setPreferredSize(preferredSize);
        
        revalidate();
    }
    
    /**
     * 1行分のレイアウトを追加します。
     * updateLayoutから呼び出されるヘルパメソッドです。
     * @param textLayout 1行の TextLayout。空行なら null。
     * @param ascent 行の ascent
     * @param descent 行の descent
     * @param leading 行の leading
     */
    private void addLineLayout(TextLayout textLayout, float ascent, float descent, float leading) {
        float fontHeight = ascent + descent + leading;
        float lineHeight = fontHeight * lineHeightFactor;
        fontHeight = Math.round(fontHeight);
        lineHeight = Math.round(lineHeight);
        float lineTop = 0.0f;
        if (fontHeight < lineHeight) {
            lineTop += Math.round((lineHeight - fontHeight) / 2);
        }
        
        lineLayouts.add(new LineLayout(areaSize.height + lineTop, textLayout));
        areaSize.height += lineHeight;
    }
    
    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;
        if (lineLayouts != null) {
            for (LineLayout lineLayout : lineLayouts) {
                TextLayout textLayout = lineLayout.getTextLayout();
                if (textLayout != null) {
                    textLayout.draw(g2, messageAreaRect.x,
                        lineLayout.getLineTop() + textLayout.getAscent());
                }
            }
        }
    }
}
