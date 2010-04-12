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

import java.awt.Component;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import com.hironytic.moltonf.model.StoryElement;
import com.hironytic.moltonf.model.StoryEvent;
import com.hironytic.moltonf.model.StoryPeriod;
import com.hironytic.moltonf.model.Talk;
import com.hironytic.moltonf.model.TalkType;

/**
 * 1単位期間分のストーリーの内容を表示するパネル
 */
@SuppressWarnings("serial")
public class PeriodContentPanel extends JPanel {

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
    public PeriodContentPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    /**
     * このパネルに表示する StoryPeriod オブジェクトをセットします。
     * @param storyPeriod パネルに表示する StoryPeriod オブジェクト
     */
    public void setModel(StoryPeriod storyPeriod) {
        setModel(storyPeriod, FILTER_ALL);
    }
    
    /**
     * このパネルに表示する StoryPeriod オブジェクトをセットします。
     * @param storyPeriod パネルに表示する StoryPeriod オブジェクト
     * @param displayFilters フィルタリング定数の組み合わせ。
     *                       指定したものが表示され、指定しなかったものが非表示になります。
     */
    public void setModel(StoryPeriod storyPeriod, int displayFilter) {
        this.storyPeriod = storyPeriod;
        filterContent(displayFilter);
    }
    
    /**
     * @see javax.swing.JComponent#setFont(java.awt.Font)
     */
    @Override
    public void setFont(Font font) {
        super.setFont(font);
        // TODO: 全MessagePanelにもフォントをセット
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
                    TalkPanel talkPanel = new TalkPanel(talk);
                    talkPanel.setSize(480, 200);    // TODO:
                    talkPanel.setMessageFont(getFont());
                    add(talkPanel);
                }
            } else if (element instanceof StoryEvent) {
                // TODO: システムイベント
            }
        }
    }
    
    /**
     * ビューの更新を行います。
     */
    public void updateView() {
        for (Component child : getComponents()) {
            if (child instanceof TalkPanel) {   // TODO: 共通のインタフェースとかいる
                ((TalkPanel)child).updateView();
            }
        }
    }    
}
