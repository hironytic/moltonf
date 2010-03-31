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

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
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
public class MessagePanel extends JPanel {
    
    /** 表示するメッセージを持った StoryElement */
    private StoryElement storyElement;
    
    /** 各行の TextLayout */
    private List<TextLayout> textLayouts = null;
    
    /**
     * コンストラクタ
     */
    public MessagePanel(StoryElement storyElement) {
        this.storyElement = storyElement;
    }

    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;
        
        readyTextLayouts(g2);

        float top = 0;
        for (TextLayout lineLayout : textLayouts) {
            top += lineLayout.getAscent();
            lineLayout.draw(g2, 0, top);
            top += lineLayout.getDescent() + lineLayout.getLeading();
        }
    }
    
    /**
     * 各行の TextLayout が作成されていなければ作成します。
     * @param g2 Graphics2D オブジェクト
     */
    private void readyTextLayouts(Graphics2D g2) {
        if (textLayouts != null)
            return;
        
        textLayouts = new ArrayList<TextLayout>();
        Font font = new Font("ＭＳ Ｐゴシック", Font.PLAIN, 12);
        for (String line : storyElement.getMessageLines()) {
            AttributedString attributedString = new AttributedString(line);
            attributedString.addAttribute(TextAttribute.FONT, font);
            
            AttributedCharacterIterator charItr = attributedString.getIterator();
            FontRenderContext frContext = g2.getFontRenderContext();
            LineBreakMeasurer measurer = new LineBreakMeasurer(charItr, frContext);
            while (measurer.getPosition() < line.length()) {
                TextLayout oneLineLayout = measurer.nextLayout(100);
                textLayouts.add(oneLineLayout);
            }
        }
    }
}
