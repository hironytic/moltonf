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
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import javax.swing.BoxLayout;

import com.hironytic.moltonf.model.EventFamily;
import com.hironytic.moltonf.model.StoryElement;
import com.hironytic.moltonf.model.StoryEvent;
import com.hironytic.moltonf.model.StoryPeriod;
import com.hironytic.moltonf.model.Talk;
import com.hironytic.moltonf.model.TalkType;

/**
 * 1単位期間分のストーリーの内容を表示するパネル
 */
@SuppressWarnings("serial")
public class PeriodContentView extends MoltonfView {

    /** 背景色 */
    private static Color BG_COLOR = new Color(0x000000);
    
    /** フィルタリング定数：アナウンスイベント */
    public static int FILTER_EVENT_ANNOUNCE = 0x0001;
    
    /** フィルタリング定数：操作系イベント */
    public static int FILTER_EVENT_ORDER = 0x0002;
    
    /** フィルタリング定数：能力系イベント */
    public static int FILTER_EVENT_EXTRA = 0x0004;
    
    /** フィルタリング定数：通常発言 */
    public static int FILTER_TALK_PUBLIC = 0x0100;
    
    /** フィルタリング定数：狼発言 */
    public static int FILTER_TALK_WOLF = 0x0200;
    
    /** フィルタリング定数：独り言 */
    public static int FILTER_TALK_PRIVATE = 0x0400;
    
    /** フィルタリング定数：墓下発言 */
    public static int FILTER_TALK_GRAVE = 0x0800;
    
    /** フィルタリング定数：すべて */
    public static int FILTER_ALL =
        FILTER_EVENT_ANNOUNCE |
        FILTER_EVENT_ORDER |
        FILTER_EVENT_EXTRA |
        FILTER_TALK_PUBLIC |
        FILTER_TALK_WOLF |
        FILTER_TALK_PRIVATE |
        FILTER_TALK_GRAVE;
    
    /** このパネルが表示する StoryPeriod */
    private StoryPeriod storyPeriod;
    
    /**
     * コンストラクタ
     * @param storyPeriod パネルに表示する StoryPeriod オブジェクト
     */
    public PeriodContentView() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    /**
     * このパネルに表示する StoryPeriod オブジェクトをセットします。
     * @param storyPeriod パネルに表示する StoryPeriod オブジェクト
     */
    public void setStoryPeriod(StoryPeriod storyPeriod) {
        setStoryPeriod(storyPeriod, FILTER_ALL);
    }
    
    /**
     * このパネルに表示する StoryPeriod オブジェクトをセットします。
     * @param storyPeriod パネルに表示する StoryPeriod オブジェクト
     * @param displayFilters フィルタリング定数の組み合わせ。
     *                       指定したものが表示され、指定しなかったものが非表示になります。
     */
    public void setStoryPeriod(StoryPeriod storyPeriod, int displayFilter) {
        this.storyPeriod = storyPeriod;
        filterContent(displayFilter);
    }
    
    /**
     * @see javax.swing.JComponent#setFont(java.awt.Font)
     */
    @Override
    public void setFont(Font font) {
        super.setFont(font);
        
        for (Component child : getComponents()) {
            if (child instanceof MoltonfView) {
                ((MoltonfView)child).setFont(font);
            }
        }
    }

    /**
     * 発言種別が発言系のフィルタにマッチするかどうか調べます。
     * @param talkType 発言種別 
     * @param displayFilters 発言フィルタ（フィルタリング定数の組み合わせ）
     * @return マッチするなら true を返します。
     */
    private boolean isMatchFilterOfTalk(TalkType talkType, int displayFilters) {
        boolean isMatch = false;
        if (talkType == TalkType.PUBLIC && (displayFilters & FILTER_TALK_PUBLIC) != 0) {
            isMatch = true;
        } else if (talkType == TalkType.WOLF && (displayFilters & FILTER_TALK_WOLF) != 0) {
            isMatch = true;
        } else if (talkType == TalkType.PRIVATE && (displayFilters & FILTER_TALK_PRIVATE) != 0) {
            isMatch = true;
        } else if (talkType == TalkType.GRAVE && (displayFilters & FILTER_TALK_GRAVE) != 0) {
            isMatch = true;
        }
        return isMatch;
    }
    
    /**
     * イベント種別がイベント系のフィルタにマッチするかどうか調べます。
     * @param eventFamily イベント種別
     * @param displayFilters イベントフィルタ (フィルタリング定数の組み合わせ)
     * @return マッチするなら true を返します。
     */
    private boolean isMatchFilterOfEvent(EventFamily eventFamily, int displayFilters) {
        boolean isMatch = false;
        if (eventFamily == EventFamily.ANNOUNCE && (displayFilters & FILTER_EVENT_ANNOUNCE) != 0) {
            isMatch = true;
        } else if (eventFamily == EventFamily.ORDER && (displayFilters & FILTER_EVENT_ORDER) != 0) {
            isMatch = true;
        } else if (eventFamily == EventFamily.EXTRA && (displayFilters & FILTER_EVENT_EXTRA) != 0) {
            isMatch = true;
        }
        return isMatch;
    }
    
    /**
     * 指定したフィルタで内容を更新します。
     * @param displayFilters フィルタリング定数の組み合わせ。
     *                       指定したものが表示され、指定しなかったものが非表示になります。
     */
    public void filterContent(int displayFilters) {
        removeAll();

        if (storyPeriod == null)
            return;
        
        for (StoryElement element : storyPeriod.getStoryElements()) {
            if (element instanceof Talk) {
                Talk talk = (Talk)element;
                TalkType talkType = talk.getTalkType();
                if (isMatchFilterOfTalk(talkType, displayFilters)) {
                    TalkView talkView = new TalkView();
                    add(talkView);
                    talkView.setTalk(talk);
                    talkView.setAreaWidth(500); //TODO:
                    talkView.setFont(getFont());
                }
            } else if (element instanceof StoryEvent) {
                StoryEvent storyEvent = (StoryEvent)element;
                if (isMatchFilterOfEvent(storyEvent.getEventFamily(), displayFilters)) {
                    StoryEventView storyEventView = new StoryEventView();
                    add(storyEventView);
                    storyEventView.setStoryEvent(storyEvent);
                    storyEventView.setAreaWidth(500); // TODO:
                    storyEventView.setFont(getFont());
                }
            }
        }
    }
    
    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        
        Dimension2D componentSize = getSize();
        Rectangle2D componentRect = new Rectangle2D.Float(
                0, 0,
                (float)componentSize.getWidth(), (float)componentSize.getHeight());
        Rectangle2D paintRect = new Rectangle2D.Float();
        Rectangle2D.intersect(componentRect, g2d.getClipBounds(), paintRect);
        
        Color oldColor = g2d.getColor();
        g2d.setColor(BG_COLOR);
        g2d.fill(paintRect);
        g2d.setColor(oldColor);
    }
}
