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

package com.hironytic.moltonf.view.sidebar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.EventListener;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.event.EventListenerList;

import com.hironytic.moltonf.Moltonf;
import com.hironytic.moltonf.model.Avatar;
import com.hironytic.moltonf.model.EventFamily;
import com.hironytic.moltonf.model.TalkType;
import com.hironytic.moltonf.util.DialogHelper;

/**
 * サイドバー「フィルタ－」のクラス
 */
@SuppressWarnings("serial")
public class FilterSideBar extends SideBar {

    /** チェックボックスに発言者を関連づけるキー */
    private static final String KEY_SPEAKER = "Moltonf.speaker";
    
    /** 発言者フィルタのペイン */
    private Box speakerFilterPane;
    
    /** ストーリーに登場する人物のリスト */
    private List<Avatar> speakerList;
    
    /** speakerList が変更されたかどうか */
    private boolean isSpeakerListModified = false;
    
    /** 発言種別フィルタの設定値 */
    private Set<TalkType> talkTypeFilter = EnumSet.noneOf(TalkType.class);

    /** talkTypeFilter が変更されたかどうか */
    private boolean isTalkTypeFilterModified = false;
    
    /** イベント種別フィルタの設定値 */
    private Set<EventFamily> eventFamilyFilter = EnumSet.noneOf(EventFamily.class);

    /** eventFamilyFilter が変更されたかどうか */
    private boolean isEventFamilyFilterModified = false;
    
    /** 発言者フィルタの設定値 */
    private Set<Avatar> speakerFilter = new HashSet<Avatar>();
    
    /** speakerFilter が変更されたかどうか */
    private boolean isSpeakerFilterModified = false;
    
    /** 通常発言チェックボックス */
    private JCheckBox cbTalkPublic;
    
    /** 狼発言チェックボックス */
    private JCheckBox cbTalkWolf;
    
    /** 独り言チェックボックス */
    private JCheckBox cbTalkPrivate;
    
    /** 墓下発言チェックボックス */
    private JCheckBox cbTalkGrave;
    
    /** 通常イベントチェックボックス */
    private JCheckBox cbEventAnnounce;
    
    /** 能力系イベントチェックボックス */
    private JCheckBox cbEventExtra;
    
    /** 操作系イベントチェックボックス */
    private JCheckBox cbEventOrder;
    
    /** 発言者チェックボックスのリスト */
    private final List<JCheckBox> cbSpeakerList = new ArrayList<JCheckBox>();
    
    /** フィルタの種別 */
    public enum FilterType {
        /** 発言種別 */
        TALK_KIND,
        
        /** イベント種別 */
        EVENT_FAMILY,
        
        /** 発言者 */
        SPEAKER,
    }
    
    /** フィルタの状態が変化したときに通知を受け取るインタフェース */
    public interface FilterChangeListener extends EventListener {
        /**
         * フィルタの状態が変化したときに呼ばれます。
         * @param filterType どの種別のフィルタで状態が変わったかを示します。
         */
        void filterChanged(FilterType filterType);
    }
    
    /** フィルタ状態が変更された通知を受け取るリスナーのリスト */
    private final EventListenerList filterChangeListenerList = new EventListenerList();
    
    /**
     * コンストラクタ
     */
    public FilterSideBar() {
        ResourceBundle res = Moltonf.getResource();
        setContent(createContent(res));
    }
    
    /**
     * ストーリーに登場する人物のリストをセットします。
     * このあとに updateView() を呼び出すと実際の内容に反映されます。
     * @param speakerList 登場人物のリスト
     */
    public void setSpeakerList(List<Avatar> speakerList) {
        this.speakerList = speakerList;
        isSpeakerListModified = true;
        isSpeakerFilterModified = true;
    }
    
    /**
     * 発言種別フィルタの設定値を取得します。
     * @return 発言種別フィルタの設定値
     */
    public Set<TalkType> getTalkTypeFilter() {
        return talkTypeFilter;
    }

    /**
     * 発言種別フィルタの設定値をセットします。
     * このあとに updateView() を呼び出すと実際の内容に反映されます。
     * @param talkTypeFilter 発言種別フィルタの設定値
     */
    public void setTalkTypeFilter(Set<TalkType> talkTypeFilter) {
        this.talkTypeFilter = (talkTypeFilter != null) ? talkTypeFilter : EnumSet.noneOf(TalkType.class);
        isTalkTypeFilterModified = true;
    }

    /**
     * イベント種別フィルタの設定値を取得します。
     * @return イベント種別フィルタの設定値
     */
    public Set<EventFamily> getEventFamilyFilter() {
        return eventFamilyFilter;
    }

    /**
     * イベント種別フィルタの設定値をセットします。
     * このあとに updateView() を呼び出すと実際の内容に反映されます。
     * @param eventFamilyFilter イベント種別フィルタの設定値
     */
    public void setEventFamilyFilter(Set<EventFamily> eventFamilyFilter) {
        this.eventFamilyFilter = (eventFamilyFilter != null) ? eventFamilyFilter : EnumSet.noneOf(EventFamily.class);
        isEventFamilyFilterModified = true;
    }

    /**
     * 発言者フィルタの設定値を取得します。
     * @return 発言者フィルタの設定値
     */
    public Set<Avatar> getSpeakerFilter() {
        return speakerFilter;
    }

    /**
     * 発言者フィルタの設定値をセットします。
     * このあとに updateView() を呼び出すと実際の内容に反映されます。
     * @param speakerFilter 発言者フィルタの設定値
     */
    public void setSpeakerFilter(Set<Avatar> speakerFilter) {
        this.speakerFilter = (speakerFilter != null) ? speakerFilter : new HashSet<Avatar>();
        isSpeakerFilterModified = true;
    }

    /**
     * フィルタの状態が変化したことの通知を受け取るリスナーを追加します。
     * @param l リスナー
     */
    public void addFilterChangeListener(FilterChangeListener l) {
        filterChangeListenerList.add(FilterChangeListener.class, l);
    }

    /**
     * フィルタの状態が変化したことの通知を受け取るリスナーを削除します。
     * @param l リスナー
     */
    public void removeFilterChangeListener(FilterChangeListener l) {
        filterChangeListenerList.remove(FilterChangeListener.class, l);
    }
    
    /**
     * フィルタの状態が変化したことを通知します。
     * @param filterType どの種別のフィルタの状態が変化したか
     */
    private void fireFilterChanged(FilterType filterType) {
        // Guaranteed to return a non-null array
        Object[] listeners = filterChangeListenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==FilterChangeListener.class) {
                ((FilterChangeListener)listeners[i+1]).filterChanged(filterType);
            }          
        }
    }
    
    /** 発言種別チェックボックスのアクションリスナー */
    private final ActionListener talkKindCheckBoxActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            talkTypeFilter = EnumSet.noneOf(TalkType.class);
            if (cbTalkPublic.isSelected()) {
                talkTypeFilter.add(TalkType.PUBLIC);
            }
            if (cbTalkWolf.isSelected()) {
                talkTypeFilter.add(TalkType.WOLF);
            }
            if (cbTalkPrivate.isSelected()) {
                talkTypeFilter.add(TalkType.PRIVATE);
            }
            if (cbTalkGrave.isSelected()) {
                talkTypeFilter.add(TalkType.GRAVE);
            }
            
            fireFilterChanged(FilterType.TALK_KIND);
        }
    };

    /** イベント種別チェックボックスのアクションリスナー */
    private final ActionListener eventFamilyCheckBoxActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            eventFamilyFilter = EnumSet.noneOf(EventFamily.class);
            if (cbEventAnnounce.isSelected()) {
                eventFamilyFilter.add(EventFamily.ANNOUNCE);
            }
            if (cbEventExtra.isSelected()) {
                eventFamilyFilter.add(EventFamily.EXTRA);
            }
            if (cbEventOrder.isSelected()) {
                eventFamilyFilter.add(EventFamily.ORDER);
            }
            
            fireFilterChanged(FilterType.EVENT_FAMILY);
        }
    };
    
    /** 発言者チェックボックスのアクションリスナー */
    private final ActionListener speakerCheckboxActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            speakerFilter = new HashSet<Avatar>();
            for (JCheckBox cbSpeaker : cbSpeakerList) {
                if (cbSpeaker.isSelected()) {
                    Avatar speaker = (Avatar)cbSpeaker.getClientProperty(KEY_SPEAKER);
                    speakerFilter.add(speaker);
                }
            }
            
            fireFilterChanged(FilterType.SPEAKER);
        }
    };

    /**
     * フィルタ用チェックボックスを生成します。
     * @param label ラベル文字列
     * @param listener アクションリスナー
     * @return 生成したチェックボックスを返します。
     */
    private JCheckBox createFilterCheckBox(String label, ActionListener listener) {
        JCheckBox checkBox = new JCheckBox(label);
        checkBox.addActionListener(listener);
        return checkBox;
    }
    
    /**
     * サイドバーの内容を作成します。
     * @param res リソースオブジェクト
     * @return 作成した内容を返します。
     */
    private JComponent createContent(ResourceBundle res) {
        speakerFilterPane = new Box(BoxLayout.Y_AXIS);
        speakerFilterPane.setBorder(DialogHelper.createTitledBorder(res.getString("filterSideBar.speakerFilter")));
        
        // 発言種別
        Box talkTypeFilterPane = new Box(BoxLayout.Y_AXIS);
        talkTypeFilterPane.setBorder(DialogHelper.createTitledBorder(res.getString("filterSideBar.talkTypeFilter")));
        cbTalkPublic = createFilterCheckBox(res.getString("filterSideBar.talkTypeFilter.public"), talkKindCheckBoxActionListener);
        cbTalkWolf = createFilterCheckBox(res.getString("filterSideBar.talkTypeFilter.wolf"), talkKindCheckBoxActionListener);
        cbTalkPrivate = createFilterCheckBox(res.getString("filterSideBar.talkTypeFilter.private"), talkKindCheckBoxActionListener);
        cbTalkGrave = createFilterCheckBox(res.getString("filterSideBar.talkTypeFilter.grave"), talkKindCheckBoxActionListener);
        talkTypeFilterPane.add(cbTalkPublic);
        talkTypeFilterPane.add(cbTalkWolf);
        talkTypeFilterPane.add(cbTalkPrivate);
        talkTypeFilterPane.add(cbTalkGrave);
     
        // イベント種別
        Box eventFamilyFilterPane = new Box(BoxLayout.Y_AXIS);
        eventFamilyFilterPane.setBorder(DialogHelper.createTitledBorder(res.getString("filterSideBar.eventFamilyFilter")));
        cbEventAnnounce = createFilterCheckBox(res.getString("filterSideBar.eventFamilyFilter.announce"), eventFamilyCheckBoxActionListener);
        cbEventExtra = createFilterCheckBox(res.getString("filterSideBar.eventFamilyFilter.extra"), eventFamilyCheckBoxActionListener);
        cbEventOrder = createFilterCheckBox(res.getString("filterSideBar.eventFamilyFilter.order"), eventFamilyCheckBoxActionListener);
        eventFamilyFilterPane.add(cbEventAnnounce);
        eventFamilyFilterPane.add(cbEventExtra);
        eventFamilyFilterPane.add(cbEventOrder);
        
        // 発言者
        Box content = new Box(BoxLayout.Y_AXIS);
        content.add(speakerFilterPane);
        content.add(talkTypeFilterPane);
        content.add(eventFamilyFilterPane);
        return content;
    }
    
    /**
     * @see com.hironytic.moltonf.view.MoltonfView#updateView()
     */
    @Override
    public void updateView() {
        boolean isNeedRevalidate = false;
        
        // 発言者リスト
        if (isSpeakerListModified) {
            isSpeakerListModified = false;
            speakerFilterPane.removeAll();
            cbSpeakerList.clear();
            if (speakerList != null) {
                for (Avatar speaker : speakerList) {
                    JCheckBox cbSpeaker = createFilterCheckBox(speaker.getShortName(), speakerCheckboxActionListener);
                    cbSpeaker.putClientProperty(KEY_SPEAKER, speaker);
                    cbSpeakerList.add(cbSpeaker);
                    speakerFilterPane.add(cbSpeaker);
                }
            }
            isNeedRevalidate = true;
        }
        
        // 発言種別
        if (isTalkTypeFilterModified) {
            isTalkTypeFilterModified = false;
            cbTalkPublic.setSelected(talkTypeFilter.contains(TalkType.PUBLIC));
            cbTalkWolf.setSelected(talkTypeFilter.contains(TalkType.WOLF));
            cbTalkPrivate.setSelected(talkTypeFilter.contains(TalkType.PRIVATE));
            cbTalkGrave.setSelected(talkTypeFilter.contains(TalkType.GRAVE));
        }
        
        // イベント種別
        if (isEventFamilyFilterModified) {
            isEventFamilyFilterModified = false;
            cbEventAnnounce.setSelected(eventFamilyFilter.contains(EventFamily.ANNOUNCE));
            cbEventExtra.setSelected(eventFamilyFilter.contains(EventFamily.EXTRA));
            cbEventOrder.setSelected(eventFamilyFilter.contains(EventFamily.ORDER));
        }
        
        // 発言者
        if (isSpeakerFilterModified) {
            isSpeakerFilterModified = false;
            for (JCheckBox cbSpeaker : cbSpeakerList) {
                Avatar speaker = (Avatar)cbSpeaker.getClientProperty(KEY_SPEAKER);
                cbSpeaker.setSelected(speakerFilter.contains(speaker));
            }
        }
        
        if (isNeedRevalidate) {
            revalidate();
        }
    }
}
