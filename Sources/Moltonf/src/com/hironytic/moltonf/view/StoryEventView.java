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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import com.hironytic.moltonf.model.StoryEvent;

/**
 * ストーリー中のイベントの表示を担当するビュー
 */
@SuppressWarnings("serial")
public class StoryEventView extends JComponent implements MoltonfView {
    
    /** ビューの左の余白 */
    private static final float VIEW_PADDING_LEFT = 16;
    
    /** ビューの右の余白 */
    private static final float VIEW_PADDING_RIGHT = 16;

    /** ビューの上の余白 */
    private static final float VIEW_PADDING_TOP = 8;
    
    /** ビューの下の余白 */
    private static final float VIEW_PADDING_BOTTOM = 8;
    
    /** メッセージの枠線の幅 */
    private static final float MESSAGE_BORDER_WIDTH = 1;

    /** メッセージの左の余白 */
    private static final float MESSAGE_PADDING_LEFT = MESSAGE_BORDER_WIDTH + 8;
    
    /** メッセージの右の余白 */
    private static final float MESSAGE_PADDING_RIGHT = MESSAGE_BORDER_WIDTH + 8;
    
    /** メッセージの上の余白 */
    private static final float MESSAGE_PADDING_TOP = MESSAGE_BORDER_WIDTH + 8;
    
    /** メッセージの下の余白 */
    private static final float MESSAGE_PADDING_BOTTOM = MESSAGE_BORDER_WIDTH + 8;
    
    /** アナウンスメッセージの文字色 */
    private static final Color MESSAGE_TEXT_COLOR_ANNOUNCE = new Color(0xdddddd);

    /** 操作系メッセージの文字色 */
    private static final Color MESSAGE_TEXT_COLOR_ORDER = new Color(0xff4444);

    /** 能力系メッセージの文字色 */
    private static final Color MESSAGE_TEXT_COLOR_EXTRA = new Color(0x888888);

    /** このビューが扱う発言 */
    private StoryEvent storyEvent;
    
    /** コンポーネントに必要な領域のサイズ */
    private Dimension2DFloat areaSize = new Dimension2DFloat();
    
    /** メッセージ領域を表示する子コンポーネント */
    private MessageComponent eventMessageComponent = new MessageComponent();
    
    /**
     * コンストラクタ
     */
    public StoryEventView() {
        this.add(eventMessageComponent);
    }

    /**
     * このビューが表示している StoryEvent オブジェクトを返します。
     * @return StoryEvent オブジェクト
     */
    public StoryEvent getStoryEvent() {
        return storyEvent;
    }
    
    /**
     * このビューが表示する StoryEvent オブジェクトをセットします。
     * セットしたあとに、updateView() を呼び出すことでレイアウトが整います。
     * @param storyEvent StoryEvent オブジェクト
     */
    public void setStoryEvent(StoryEvent storyEvent) {
        this.storyEvent = storyEvent;
    }
    
    /**
     * このビューの幅をセットします。
     * セットしたあとに、updateView() を呼び出すことでレイアウトが整います。
     * @param width 幅
     */
    public void setAreaWidth(float width) {
        areaSize.width = width;
    }
    
    /**
     * メッセージの文字色を返します。
     * @return 文字色
     */
    private Color getMessageTextColor() {
        Color color;
        switch (getStoryEvent().getEventFamily()) {
        case ANNOUNCE:
            color = MESSAGE_TEXT_COLOR_ANNOUNCE;
            break;
        case ORDER:
            color = MESSAGE_TEXT_COLOR_ORDER;
            break;
        case EXTRA:
            color = MESSAGE_TEXT_COLOR_EXTRA;
            break;
        default:
            color = MESSAGE_TEXT_COLOR_ANNOUNCE;
            break;
        }
        return color;
    }

    private Color getBorderColor() {
        return getMessageTextColor();
    }
    
    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (storyEvent == null) {
            return;
        }
        
        Rectangle2D messageAreaRect = eventMessageComponent.getBounds();
        Rectangle2D.Float drawRect = new Rectangle2D.Float(
                (float)(messageAreaRect.getX() - MESSAGE_PADDING_LEFT),
                (float)(messageAreaRect.getY() - MESSAGE_PADDING_TOP),
                (float)(messageAreaRect.getWidth() + MESSAGE_PADDING_LEFT + MESSAGE_PADDING_RIGHT),
                (float)(messageAreaRect.getHeight() + MESSAGE_PADDING_TOP + MESSAGE_PADDING_BOTTOM));

        Graphics2D g2d = (Graphics2D)g;
        
        Color oldColor = g2d.getColor();
        Object oldAntialiasingHint = g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        Stroke oldStroke = g2d.getStroke();
        try {
            g.setColor(getBorderColor());
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setStroke(new BasicStroke(1));

            g2d.draw(drawRect);
        } finally {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAntialiasingHint);
            g2d.setColor(oldColor);
            g2d.setStroke(oldStroke);
        }
    }
    
    /**
     * ビューの更新を行います。
     */
    @Override
    public void updateView() {
        if (storyEvent == null) {
            return;
        }
        
        areaSize.height = 0f;
        
        // イベントメッセージ
        eventMessageComponent.setMessage(storyEvent.getMessageLines());
        eventMessageComponent.setForeground(getMessageTextColor());
        eventMessageComponent.updateLayout(areaSize.width - (VIEW_PADDING_LEFT + MESSAGE_PADDING_LEFT + MESSAGE_PADDING_RIGHT + VIEW_PADDING_RIGHT));
        Dimension2D messageAreaSize = eventMessageComponent.getAreaSize();
        Rectangle2D messageAreaRect = new Rectangle2D.Float(
                VIEW_PADDING_LEFT + MESSAGE_PADDING_LEFT,
                VIEW_PADDING_TOP + MESSAGE_PADDING_TOP,
                (float)messageAreaSize.getWidth(),
                (float)messageAreaSize.getHeight());
        Rectangle messageAreaRectInt = new Rectangle();
        messageAreaRectInt.setRect(messageAreaRect);
        eventMessageComponent.setBounds(messageAreaRectInt);
        
        areaSize.height += VIEW_PADDING_TOP +
                           MESSAGE_PADDING_TOP +
                           (float)messageAreaSize.getHeight() +
                           MESSAGE_PADDING_BOTTOM +
                           VIEW_PADDING_BOTTOM;
        
        Dimension size = new Dimension();
        size.setSize(areaSize);
        setPreferredSize(size);
        
        revalidate();
    }

    /**
     * @see javax.swing.JComponent#setFont(java.awt.Font)
     */
    @Override
    public void setFont(Font font) {
        super.setFont(font);
        
        eventMessageComponent.setFont(font);
    }
}
