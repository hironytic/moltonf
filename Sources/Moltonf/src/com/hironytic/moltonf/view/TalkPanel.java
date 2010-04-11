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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Point2D.Float;

import com.hironytic.moltonf.model.Talk;

/**
 * 会話の表示を担当するパネル
 */
@SuppressWarnings("serial")
public class TalkPanel extends MessagePanel {

    /** メッセージの左の余白 */
    private static float MESSAGE_PADDING_LEFT = 12;
    
    /** メッセージの右の余白 */
    private static float MESSAGE_PADDING_RIGHT = 12;
    
    /** メッセージの上の余白 */
    private static float MESSAGE_PADDING_TOP = 8;
    
    /** メッセージの下の余白 */
    private static float MESSAGE_PADDING_BOTTOM = 8;
    
    /** メッセージ領域の左端 */
    private static float MESSAGE_LEFT = 56;
    
    /** メッセージの角を描画するときの半径 */
    private static float MESSAGE_CORNER_RADIUS = 5;
    
    /** 通常メッセージの背景色 */
    private static Color MESSAGE_BG_COLOR_PUBLIC = new Color(0xffffff);
    
    /** 通常メッセージの文字色 */
    private static Color MESSAGE_TEXT_COLOR_PUBLIC = new Color(0x000000);

    /** 狼メッセージの背景色 */
    private static Color MESSAGE_BG_COLOR_WOLF = new Color(0xff7777);
    
    /** 狼メッセージの文字色 */
    private static Color MESSAGE_TEXT_COLOR_WOLF = new Color(0x000000);

    /** 独り言メッセージの背景色 */
    private static Color MESSAGE_BG_COLOR_PRIVATE = new Color(0x939393);
    
    /** 独り言メッセージの文字色 */
    private static Color MESSAGE_TEXT_COLOR_PRIVATE = new Color(0x000000);

    /** 墓下メッセージの背景色 */
    private static Color MESSAGE_BG_COLOR_GRAVE = new Color(0x9fb7cf);
    
    /** 墓下メッセージの文字色 */
    private static Color MESSAGE_TEXT_COLOR_GRAVE = new Color(0x000000);
    
    /**
     * コンストラクタ
     * @param talk このパネルで表示する Talk オブジェクト
     */
    public TalkPanel(Talk talk) {
        super(talk);
    }

    /**
     * このパネルが表示している Talk オブジェクトを返します。
     * @return Talk オブジェクト
     */
    public Talk getTalk() {
        return (Talk)getStoryElement();
    }
    
    /**
     * @see com.hironytic.moltonf.view.MessagePanel#getMessageAreaWidth()
     */
    @Override
    protected float getMessageAreaWidth() {
        return getWidth() - (MESSAGE_LEFT + MESSAGE_PADDING_LEFT + MESSAGE_PADDING_RIGHT);
    }

    /**
     * @see com.hironytic.moltonf.view.MessagePanel#getMessageAreaTopLeft(java.awt.geom.Point2D.Float)
     */
    @Override
    protected Float getMessageAreaTopLeft(Float pt) {
        pt.x = MESSAGE_LEFT + MESSAGE_PADDING_LEFT;
        pt.y = MESSAGE_PADDING_TOP;   // TODO: 本当は発言者、発言時刻部分があるはず。
        return pt;
    }

    /**
     * @see com.hironytic.moltonf.view.MessagePanel#getMessageTextColor()
     */
    @Override
    protected Color getMessageTextColor() {
        Color color = null;
        switch (getTalk().getTalkType()) {
        case PUBLIC:
            color = MESSAGE_TEXT_COLOR_PUBLIC;
            break;
        case WOLF:
            color = MESSAGE_TEXT_COLOR_WOLF;
            break;
        case PRIVATE:
            color = MESSAGE_TEXT_COLOR_PRIVATE;
            break;
        case GRAVE:
            color = MESSAGE_TEXT_COLOR_GRAVE;
            break;
        }
        return color;
    }

    /**
     * メッセージの背景色を返します。
     * @return メッセージの背景色
     */
    private Color getMessageBackgroundColor() {
        Color color = null;
        switch (getTalk().getTalkType()) {
        case PUBLIC:
            color = MESSAGE_BG_COLOR_PUBLIC;
            break;
        case WOLF:
            color = MESSAGE_BG_COLOR_WOLF;
            break;
        case PRIVATE:
            color = MESSAGE_BG_COLOR_PRIVATE;
            break;
        case GRAVE:
            color = MESSAGE_BG_COLOR_GRAVE;
            break;
        }
        return color;
    }
    
    /**
     * @see com.hironytic.moltonf.view.MessagePanel#paintMessageBackground(java.awt.Graphics)
     */
    @Override
    protected void paintMessageBackground(Graphics g) {
        Rectangle2D.Float messageAreaRect = getMessageAreaRect();
        Rectangle2D.Float drawRect = new Rectangle2D.Float(
                messageAreaRect.x - MESSAGE_PADDING_LEFT,
                messageAreaRect.y - MESSAGE_PADDING_TOP,
                messageAreaRect.width + MESSAGE_PADDING_LEFT + MESSAGE_PADDING_RIGHT,
                messageAreaRect.height + MESSAGE_PADDING_TOP + MESSAGE_PADDING_BOTTOM);

        Graphics2D g2d = (Graphics2D)g;
        
        Color oldColor = g2d.getColor();
        Object oldAntialiasingHint = g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        try {
            g.setColor(getMessageBackgroundColor());
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
            g2d.fill(new RoundRectangle2D.Float(
                    drawRect.x, drawRect.y,
                    drawRect.width, drawRect.height,
                    MESSAGE_CORNER_RADIUS * 2, MESSAGE_CORNER_RADIUS * 2));
        } finally {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAntialiasingHint);
            g2d.setColor(oldColor);
        }
        
        super.paintMessageBackground(g);
    }
    
}
