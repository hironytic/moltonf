/*
 * Moltonf
 *
 * Copyright (c) 2010 Hironori Ichimiya <hiron@hironytic.com>
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

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import com.hironytic.moltonf.Moltonf;
import com.hironytic.moltonf.model.Avatar;
import com.hironytic.moltonf.model.EventFamily;
import com.hironytic.moltonf.model.HighlightSetting;
import com.hironytic.moltonf.model.Link;
import com.hironytic.moltonf.model.MessageRange;
import com.hironytic.moltonf.model.StoryElement;
import com.hironytic.moltonf.model.StoryEvent;
import com.hironytic.moltonf.model.StoryPeriod;
import com.hironytic.moltonf.model.Talk;
import com.hironytic.moltonf.model.TalkType;

/**
 * 1単位期間分のストーリーの内容を表示するクラス。
 */
@SuppressWarnings("serial")
public class PeriodView extends JComponent implements MoltonfView {

    /** 背景色 */
    private static final Color BG_COLOR = new Color(0x000000);
    
    /** 外側の背景色 */
    private static final Color OUTSIDE_BG_COLOR = new Color(0x776655);
    
    /** リンクの色 */
    private static final Color LINK_COLOR = new Color(0xff8800);
    
    /** StoryElement を表示するビューに対して StoryElement のインデックスを client property に設定する際のキー */
    private static final String KEY_STORY_ELEMENT_INDEX = "Moltonf.storyElementIndex";
    
    /** このビューを表示するためのビューポートを持っているスクロールペイン */
    private final ScrollPane scrollPane;
    
    /** このパネルが表示する StoryPeriod */
    private StoryPeriod storyPeriod;

    /** 内容を再作成する必要があるかどうか */
    private boolean isRebuildContentRequired = false;
    
    /** 表示する発言種別の組み合わせ。ただし null なら発言種別によるフィルタを行わない（すべて表示する） */
    private Set<TalkType> talkTypeFilter = null;
    
    /** 表示するイベント種別の組み合わせ。ただし null ならイベント種別によるフィルタを行わない（すべて表示する） */
    private Set<EventFamily> eventFamilyFilter = null;
    
    /** 表示する発言者を含む Set。ただし null なら発言者によるフィルタを行わない（すべて表示する）*/
    private Set<Avatar> speakerFilter = null;
    
    /** 内容のフィルタリングの変更の必要があるかどうか */
    private boolean isRebuildFilterRequired = false;
    
    /** 強調表示設定 */
    private List<HighlightSetting> highlightSettingList;

    /** 範囲選択用オブジェクト */
    private RangeSelector rangeSelector = new PeriodViewRangeSelector();
    
    private MessageComponent nextDayLink;   // TODO: MoltonfView できちんと実装すれば持つ必要ない
    
    /**
     * PeriodView 用のスクロールペイン
     */
    public static class ScrollPane extends JScrollPane {
        private PeriodView periodView;
        
        public ScrollPane(PeriodView periodView) {
            this.periodView = periodView;
        }
        
        public PeriodView getPeriodView() {
            return periodView;
        }
    }
    
    /**
     * コンストラクタ
     */
    public PeriodView() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // スクロールペインの中に左右センタリングするためのパネルを挟む
        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        contentPanel.setBackground(OUTSIDE_BG_COLOR);
        contentPanel.add(this);
        scrollPane = new ScrollPane(this);
        scrollPane.setViewportView(contentPanel);
        
        enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }

    /**
     * スクロールペインを取得します。
     * @return
     */
    public ScrollPane getScrollPane() {
        return scrollPane;
    }
    
    /**
     * このビューが表示している StoryPeriod オブジェクトを返します。
     * @return StoryPeriod オブジェクト 
     */
    public StoryPeriod getStoryPeriod() {
        return storyPeriod;
    }

    /**
     * このビューに表示する StoryPeriod オブジェクトをセットします。
     * @param storyPeriod StoryPeriod オブジェクト
     */
    public void setStoryPeriod(StoryPeriod storyPeriod) {
        this.storyPeriod = storyPeriod;
        isRebuildContentRequired = true;
    }
    
    /**
     * 発言種別フィルタの設定値を取得します。
     * @return 表示する発言種別の組み合わせ。
     *         ただし、この値が null なら発言種別によるフィルタを行わないことを示します。
     */
    public Set<TalkType> getTalkTypeFilter() {
        return talkTypeFilter;
    }

    /**
     * 発言種別フィルタの値をセットします。
     * @param talkTypeFilter 表示する発言種別の組み合わせ。
     *                       ただし、発言種別によるフィルタを行わない場合は null を指定します。
     */
    public void setTalkTypeFilter(Set<TalkType> talkTypeFilter) {
        this.talkTypeFilter = talkTypeFilter;
        isRebuildFilterRequired = true;
    }

    /**
     * イベント種別フィルタの設定値を取得します。
     * @return 表示するイベント種別の組み合わせ。
     *         ただし、この値が null ならイベント種別によるフィルタを行わないことを示します。
     */
    public Set<EventFamily> getEventFamilyFilter() {
        return eventFamilyFilter;
    }

    /**
     * イベント種別フィルタの値をセットします。
     * @param eventFamilyFilter 表示するイベント種別の組み合わせ。
     *                          ただし、イベント種別によるフィルタを行わない場合は null を指定します。
     */
    public void setEventFamilyFilter(Set<EventFamily> eventFamilyFilter) {
        this.eventFamilyFilter = eventFamilyFilter;
        isRebuildFilterRequired = true;
    }

    /**
     * 発言者フィルタの設定値を取得します。
     * @return 表示する発言者が入った Set。
     *         ただし、この値が null なら発言者によるフィルタを行わないことを示します。
     */
    public Set<Avatar> getSpeakerFilter() {
        return speakerFilter;
    }

    /**
     * 発言者フィルタの値をセットします。
     * @param speakerFilter 表示する発言者が入った Set。
     *                      ただし、発言者によるフィルタを行わない場合は null を指定します。
     */
    public void setSpeakerFilter(Set<Avatar> speakerFilter) {
        this.speakerFilter = speakerFilter;
        isRebuildFilterRequired = true;
    }

    /**
     * このパネル内で表示する発言の強調表示設定をセットします。
     * @param highlightSettingList 強調表示設定のリスト
     */
    public void setHighlightSettingList(List<HighlightSetting> highlightSettingList) {
        this.highlightSettingList = highlightSettingList;
        
        for (Component child : getComponents()) {
            if (child instanceof TalkView) {
                ((TalkView)child).setHighlightSettingList(highlightSettingList);
            }
        }
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
            } else if (child instanceof MessageComponent) {
                ((MessageComponent)child).setFont(font);
            }
        }
    }

    /**
     * スクロールにより見えている範囲の左上の座標を返します。
     * @return 左上の座標。マイナスになることもあります。
     */
    public Point getScrollPosition() {
        JViewport viewPort = scrollPane.getViewport();
        Point topLeft = viewPort.getViewPosition();
        
        // topLeft は間に挟んだ JPanel の座標系なので、
        // これを PeriodView の座標系になおす
        Point periodViewLocation = getLocation();
        topLeft.translate(-periodViewLocation.x, -periodViewLocation.y);

        return topLeft;
    }
    
    /**
     * スクロールにより見えている範囲の先頭の StoryElement のインデックスを返します。
     * @return 見えている先頭の StoryElement のインデックス。見つからなければ -1。
     */
    public int getFirstVisibleStoryElementIndex() {
        Point topLeft = getScrollPosition();
        topLeft.x = 0;
        Component component = getComponentAt(topLeft);
        if (component instanceof JComponent) {
            Integer index = (Integer)((JComponent)component).getClientProperty(KEY_STORY_ELEMENT_INDEX);
            if (index != null) {
                return index;
            }
        }
        
        return -1;
    }

    /**
     * 指定した StoryElement が見えるようにスクロールします。
     * @param index StoryElement のインデックス
     */
    public void scrollToStoryElement(int index) {
        // TODO:
    }
    
    /**
     * 発言種別が発言系のフィルタにマッチするかどうか調べます。
     * @param talkType 発言種別 
     * @return マッチするなら true を返します。
     */
    private boolean isMatchFilterOfTalk(TalkType talkType) {
        boolean isMatch = false;
        if (talkTypeFilter == null) {
            isMatch = true;
        } else if (talkTypeFilter.contains(talkType)) {
            isMatch = true;
        }
        return isMatch;
    }
    
    /**
     * イベント種別がイベント系のフィルタにマッチするかどうか調べます。
     * @param eventFamily イベント種別
     * @return マッチするなら true を返します。
     */
    private boolean isMatchFilterOfEvent(EventFamily eventFamily) {
        boolean isMatch = false;
        if (eventFamilyFilter == null) {
            isMatch = true;
        } else if (eventFamilyFilter.contains(eventFamily)) {
            isMatch = true;
        }
        return isMatch;
    }
    
    /**
     * 発言者が発言者フィルタにマッチするかどうか調べます。
     * @param speaker 発言者
     * @return マッチするなら true を返します。
     */
    private boolean isMatchFilterOfSpeaker(Avatar speaker) {
        boolean isMatch = false;
        if (speakerFilter == null) {
            isMatch = true;
        } else if (speakerFilter.contains(speaker)) {
            isMatch = true;
        }
        return isMatch;
    }
    
    /**
     * 内容を再作成します。
     */
    private void rebuildContent() {
        ResourceBundle res = Moltonf.getResource();
        
        removeAll();

        if (storyPeriod == null)
            return;
        
        List<StoryElement> storyElements = storyPeriod.getStoryElements();
        int elementsCount = storyElements.size();
        for (int ix = 0; ix < elementsCount; ++ix) {
            JComponent storyElementComponent = null;
            boolean isVisible = false;
            StoryElement element = storyElements.get(ix);
            if (element instanceof Talk) {
                Talk talk = (Talk)element;
                TalkView talkView = new TalkView();
                add(talkView);
                storyElementComponent = talkView;
                talkView.setTalk(talk);
                talkView.setAreaWidth(500); //TODO:
                talkView.setHighlightSettingList(highlightSettingList);
                talkView.setFont(getFont());
                talkView.setRangeSelector(rangeSelector);
                TalkType talkType = talk.getTalkType();
                Avatar speaker = talk.getSpeaker();
                if (isMatchFilterOfTalk(talkType) && isMatchFilterOfSpeaker(speaker)) {
                    isVisible = true;
                }
            } else if (element instanceof StoryEvent) {
                StoryEvent storyEvent = (StoryEvent)element;
                StoryEventView storyEventView = new StoryEventView();
                add(storyEventView);
                storyElementComponent = storyEventView;
                storyEventView.setStoryEvent(storyEvent);
                storyEventView.setAreaWidth(500); // TODO:
                storyEventView.setFont(getFont());
                storyEventView.setRangeSelector(rangeSelector);
                EventFamily eventFamily = storyEvent.getEventFamily();
                if (isMatchFilterOfEvent(eventFamily)) {
                    isVisible = true;
                }
            }
            
            if (storyElementComponent != null) {
                // インデックスを記憶させておく
                storyElementComponent.putClientProperty(KEY_STORY_ELEMENT_INDEX, ix);
                
                storyElementComponent.setVisible(isVisible);
            }
        }
        
        // 次の日へリンク
        // TODO: エピには出さないようにする
        // TODO: リンクのクラスについて要検討 今はとにかく表示するだけ。というか今のはいろいろダメ。MoltonfView実装のJComponent継承クラスを1つ作る。
        class CommandLink extends Link {
            CommandLink(MessageRange range) {
                super(range);
            }
        }
        nextDayLink = new MessageComponent();
        String nextDayString = res.getString("periodView.nextDay");
        nextDayLink.setMessage(Arrays.asList(nextDayString));
        nextDayLink.setFont(getFont());
        nextDayLink.setLinkInfoList(Arrays.asList(new MessageComponent.LinkInfo(new CommandLink(new MessageRange(0, 0, nextDayString.length())), LINK_COLOR)));
        add(nextDayLink);
    }
    
    /**
     * フィルタリング状態を再構成します。
     */
    private void rebuildFilter() {
        // スクロール位置があまり変わらないようにするため
        // 元のスクロール位置にあるコンポーネントを記憶しておく
        int firstComponentIndex = -1;
        int firstComponentY = 0;
        Point topLeft = getScrollPosition();
        topLeft.x = 0;
        int componentCount = getComponentCount();
        for (int ix = 0; ix < componentCount; ++ix) {
            Component comp = getComponent(ix);
            if (comp.isVisible() && comp.contains(topLeft.x - comp.getX(), topLeft.y - comp.getY())) {
                firstComponentIndex = ix;
                firstComponentY = topLeft.y - comp.getY();
                break;
            }
        }
        
        JComponent scrollToComponent = null;
        JComponent lastComponent = null;
        int scrollToComponentTop =  0;
        
        if (storyPeriod == null)
            return;
        
        List<StoryElement> storyElements = storyPeriod.getStoryElements();
        for (int ix = 0; ix < componentCount; ++ix) {
            JComponent comp = (JComponent)getComponent(ix);
            Integer storyElementIndex = (Integer)comp.getClientProperty(KEY_STORY_ELEMENT_INDEX);
            if (storyElementIndex != null) {
                boolean isVisible = false;
                StoryElement element = storyElements.get(storyElementIndex);
                if (element instanceof Talk) {
                    Talk talk = (Talk)element;
                    TalkType talkType = talk.getTalkType();
                    Avatar speaker = talk.getSpeaker();
                    if (isMatchFilterOfTalk(talkType) && isMatchFilterOfSpeaker(speaker)) {
                        isVisible = true;
                    }
                } else if (element instanceof StoryEvent) {
                    StoryEvent storyEvent = (StoryEvent)element;
                    EventFamily eventFamily = storyEvent.getEventFamily();
                    if (isMatchFilterOfEvent(eventFamily)) {
                        isVisible = true;
                    }
                }
                comp.setVisible(isVisible);
            }
            
            // 更新前のスクロールして見えている先頭にあったもの以降で
            // 表示されたものが見えるようにスクロールする
            if (scrollToComponent == null && 
                    ix >= firstComponentIndex &&
                    comp.isVisible()) {
                scrollToComponent = comp;
                if (ix == firstComponentIndex) {
                    scrollToComponentTop = firstComponentY;
                }
            }
            
            lastComponent = comp;
        }
        
        revalidate();
        
        // スクロール位置を復元
        if (scrollToComponent == null) {
            scrollToComponent = lastComponent;
        }
        if (scrollToComponent != null) {
            final JComponent scrollComponent = scrollToComponent;
            final int top = scrollToComponentTop;
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    int viewTop = scrollComponent.getY() + top;
                    JViewport viewport = PeriodView.this.getScrollPane().getViewport();
                    Point viewPos = viewport.getViewPosition();
                    viewPos.y = viewTop;
                    viewport.setViewPosition(viewPos);
                }
            });
        }
    }
    
    /**
     * @see com.hironytic.moltonf.view.MoltonfView#updateView()
     */
    @Override
    public void updateView() {
        if (isRebuildContentRequired) {
            isRebuildContentRequired = false;
            isRebuildFilterRequired = false;
            rebuildContent();
        }
        
        if (isRebuildFilterRequired) {
            isRebuildFilterRequired = false;
            rebuildFilter();
        }
        
        for (Component child : getComponents()) {
            if (child instanceof MoltonfView) {
                ((MoltonfView)child).updateView();
            }
        }
        if (nextDayLink != null) {
            nextDayLink.updateLayout(500);  // TODO: 500
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
    
    /**
     * PeriodView 用の範囲選択用オブジェクトの実装
     */
    private class PeriodViewRangeSelector implements RangeSelector {
        /** 開始点の子コンポーネントのインデックス */
        private int startChildIndex = -1;
        
        /** 開始点の座標。PeriodView の座標系です。*/
        private Point2D startPt = null;
        
        /** 終了点の子コンポーネントのインデックス */
        private int endChildIndex = -1;
        
        /** 終了点の座標。PeriodView の座標系です。*/
        private Point2D endPt = null;
        
        public PeriodViewRangeSelector() {
        }

        /**
         * @see com.hironytic.moltonf.view.RangeSelector#processMouseRelatedEvent(java.awt.event.MouseEvent)
         */
        @Override
        public void processMouseRelatedEvent(MouseEvent event) {
            int eventId = event.getID();
            if (eventId == MouseEvent.MOUSE_PRESSED) {
                if (SwingUtilities.isLeftMouseButton(event) || SwingUtilities.isRightMouseButton(event)) {
                    if (event.isShiftDown()) {
                        selectTo((JComponent)event.getSource(), event.getPoint());
                    } else {
                        selectFrom((JComponent)event.getSource(), event.getPoint());
                    }
                }
            } else if (eventId == MouseEvent.MOUSE_DRAGGED) {
                if (SwingUtilities.isLeftMouseButton(event) || SwingUtilities.isRightMouseButton(event)) {
                    selectTo((JComponent)event.getSource(), event.getPoint());
                    
                    // TODO: ちょっと雑すぎる？
                    Point2D point = ViewUtilities.convertPoint((JComponent)event.getSource(), event.getPoint(), PeriodView.this);
                    Rectangle rect = new Rectangle((int)point.getX(), (int)point.getY(), 1, 1);
                    scrollRectToVisible(rect);
                }
            }
        }

        /**
         * 指定された位置にある子コンポーネントのインデックスを返します。
         * @param point 位置
         * @return 子コンポーネントのインデックス。
         *         その位置に可視の子コンポーネントが見つからなければ -1 を返します。
         */
        private int getChildComponent(Point2D point) {
            // コンポーネントは上から順に並んでいるという前提
            int lastItemIndex = -1;
            int componentCount = getComponentCount();
            for (int ix = 0; ix < componentCount; ++ix) {
                Component comp = getComponent(ix);
                if (comp.isVisible()) {
                    lastItemIndex = ix;
                    if (comp.getY() + comp.getHeight() >= point.getY()) {
                        return ix;
                    }
                }
            }
            
            // 見つからなければ最後のアイテムで
            return lastItemIndex;
        }
        
        /**
         * 選択範囲を消去します。
         */
        private void doClearSelection() {
            if (startChildIndex < 0 || endChildIndex < 0) {
                return;
            }
            
            int startIx = (startChildIndex < endChildIndex) ? startChildIndex : endChildIndex;
            int endIx = (startChildIndex < endChildIndex) ? endChildIndex : startChildIndex;
            for (int ix = startIx; ix <= endIx; ++ix) {
                Component child = getComponent(ix);
                if (child instanceof Selectable) {
                    Selectable selectableChild = (Selectable)child;
                    selectableChild.clearSelection();
                }
            }
            startPt = null;
            startChildIndex = -1;
        }
        
        /**
         * 指定したコンポーネントのある点を選択範囲の開始点にします。
         * @param component コンポーネント
         * @param pt 開始点。第1引数で指定したコンポーネントの座標系で指定します。
         */
        private void selectFrom(JComponent component, Point2D pt) {
            doClearSelection();
            
            startPt = ViewUtilities.convertPoint(component, pt, PeriodView.this);
            startChildIndex = getChildComponent(startPt);
            endPt = null;
            endChildIndex = -1;
        }

        /**
         * 指定したコンポーネントのある点を選択範囲の終了点にします。
         * @param component コンポーネント
         * @param pt 終了点。第1引数で指定したコンポーネントの座標系で指定します。
         */
        private void selectTo(JComponent component, Point2D pt) {
            if (startPt == null) {
                return;
            }
            
            int prevEndChildIndex = endChildIndex;
            
            endPt = ViewUtilities.convertPoint(component, pt, PeriodView.this);
            endChildIndex = getChildComponent(endPt);
            
            int startIx = (-1 == prevEndChildIndex) ? startChildIndex : prevEndChildIndex;
            int endIx = endChildIndex;
            if (startIx > endIx) {
                int tempIx = endIx;
                endIx = startIx;
                startIx = tempIx;
            }
            for (int ix = startIx; ix <= endIx; ++ix) {
                Component child = getComponent(ix);
                if (child instanceof Selectable) {
                    Selectable selectableChild = (Selectable)child;
                    Point2D childStart = ViewUtilities.convertPoint(PeriodView.this, startPt, child);
                    Point2D childEnd = ViewUtilities.convertPoint(PeriodView.this, endPt, child);
                    selectableChild.selectRange(childStart, childEnd);
                }
            }
        }
    }

    /**
     * @see javax.swing.JComponent#processMouseEvent(java.awt.event.MouseEvent)
     */
    @Override
    protected void processMouseEvent(MouseEvent e) {
        processMouseRelatedEvent(e);
        super.processMouseEvent(e);
    }

    /**
     * @see javax.swing.JComponent#processMouseMotionEvent(java.awt.event.MouseEvent)
     */
    @Override
    protected void processMouseMotionEvent(MouseEvent e) {
        processMouseRelatedEvent(e);
        super.processMouseMotionEvent(e);
    }
    
    /**
     * マウスに関連するイベントを処理します。
     * @param event マウス関連のイベント
     */
    private void processMouseRelatedEvent(MouseEvent event) {
        rangeSelector.processMouseRelatedEvent(event);
    }
}
