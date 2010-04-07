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
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Float;

import com.hironytic.moltonf.model.Talk;

/**
 * 
 */
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
    
    /** メッセージの背景色 */
    private static Color MESSAGE_BACKGROUND_COLOR = Color.WHITE;
    
    /** メッセージの文字色 */
    private static Color MESSAGE_FORE_COLOR = Color.BLACK;
    
    /**
     * コンストラクタ
     * @param talk このパネルで表示する Talk オブジェクト
     */
    public TalkPanel(Talk talk) {
        super(talk);
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
        return MESSAGE_FORE_COLOR;
    }

    /* (non-Javadoc)
     * @see com.hironytic.moltonf.view.MessagePanel#paintMessageBackground(java.awt.Graphics)
     */
    @Override
    protected void paintMessageBackground(Graphics g) {
        Color oldColor = g.getColor();
        try {
            g.setColor(MESSAGE_BACKGROUND_COLOR);
            Rectangle2D.Float messageAreaRect = getMessageAreaRect();
            g.fillRoundRect((int)(messageAreaRect.x - MESSAGE_PADDING_LEFT),
                    (int)(messageAreaRect.y - MESSAGE_PADDING_TOP),
                    (int)(messageAreaRect.width + MESSAGE_PADDING_LEFT + MESSAGE_PADDING_RIGHT),
                    (int)(messageAreaRect.height + MESSAGE_PADDING_TOP + MESSAGE_PADDING_BOTTOM),
                    16, 16);    // TODO: このあたりまだてきとー
        } finally {
            g.setColor(oldColor);
        }
        
        super.paintMessageBackground(g);
    }
    
    
}
