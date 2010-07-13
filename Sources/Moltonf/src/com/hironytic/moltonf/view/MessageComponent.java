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

import java.awt.AWTEvent;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.event.EventListenerList;

import com.hironytic.moltonf.Moltonf;
import com.hironytic.moltonf.model.HighlightSetting;
import com.hironytic.moltonf.model.Link;
import com.hironytic.moltonf.model.MessageRange;
import com.hironytic.moltonf.view.event.LinkClickListener;
import com.hironytic.moltonf.view.event.LinkClickedEvent;

/**
 * ストーリー中のテキストを表示するコンポーネント
 */
@SuppressWarnings("serial")
public class MessageComponent extends JComponent implements Selectable {
    
    /** 表示するメッセージ */
    private List<String> messageLines;

    /** 1行の高さの係数（何倍するか) */
    private float lineHeightFactor;
    
    /** 強調表示設定のリスト */
    private List<HighlightSetting> highlightSettingList;

    /** テキスト属性情報のリスト */
    private List<AttributedAreaInfo> attributedAreaInfoList;
    
    /** リンク情報 */
    private List<LinkInfo> linkInfoList;
    
    /** 各行の TextLayout */
    private List<LineLayout> lineLayouts = null;
    
    /** メッセージ表示領域の矩形 */
    private Rectangle2D.Float messageAreaRect = new Rectangle2D.Float(0, 0, 0, 0);

    /** コンポーネントに必要な領域のサイズ */
    private Dimension2DFloat areaSize = new Dimension2DFloat();

    /** イベント通知を受け取るリスナーのリスト */
    private final EventListenerList eventListenerList = new EventListenerList();
    
    /** 範囲選択オブジェクト */
    private RangeSelector rangeSelector = null;
    
    /** 選択範囲の開始点 */
    private Position startOfSelectedRange = null;
    
    /** 選択範囲の終了点 */
    private Position endOfSelectedRange = null;
    
    /**
     * テキストの属性が設定された箇所の情報
     */
    public static class AttributedAreaInfo {
        
        /** 属性の範囲 */
        private MessageRange range;
        
        /** 色 */
        private Color color = null;
        
        /**
         * デフォルトコンストラクタ
         */
        public AttributedAreaInfo() {
            
        }
        
        /**
         * 範囲を指定するコンストラクタ
         * @param range 属性の範囲
         */
        public AttributedAreaInfo(MessageRange range) {
            this.range = range;
        }
        
        /**
         * 範囲と色を指定するコンストラクタ
         * @param range 属性の範囲
         * @param color 色
         */
        public AttributedAreaInfo(MessageRange range, Color color) {
            this.range = range;
            this.color = color;
        }
    
        /**
         * 属性の範囲を取得します。
         * @return 属性の範囲
         */
        public MessageRange getRange() {
            return range;
        }
        
        /**
         * 色を取得します。
         * @return 色を返します。
         */
        public Color getColor() {
            return color;
        }
    
        /**
         * 属性の範囲を設定します。
         * @param range 属性の範囲
         */
        public void setRange(MessageRange range) {
            this.range = range;
        }
    
        /**
         * 色をセットします。
         * @param color 色
         */
        public void setColor(Color color) {
            this.color = color;
        }
    }
    
    /**
     * リンクの情報
     */
    public static class LinkInfo {
        /** リンク */
        private Link link = null;
        
        /** 表示色 */
        private Color color = null;
        
        /**
         * デフォルトコンストラクタ
         */
        public LinkInfo() {
        }
        
        /**
         * リンクと色を指定するコンストラクタ
         * @param link リンク
         * @param color 表示色
         */
        public LinkInfo(Link link, Color color) {
            this.link = link;
            this.color = color;
        }

        /**
         * link を取得します。
         * @return link を返します。
         */
        public Link getLink() {
            return link;
        }

        /**
         * color を取得します。
         * @return color を返します。
         */
        public Color getColor() {
            return color;
        }

        /**
         * link をセットします。
         * @param link セットしたい link の値
         */
        public void setLink(Link link) {
            this.link = link;
        }

        /**
         * color をセットします。
         * @param color セットしたい color の値
         */
        public void setColor(Color color) {
            this.color = color;
        }
    }

    /** 見た目の1行分のレイアウト情報 */
    private class LineLayout {
        /** 行の上端座標 */
        private float top;
        
        /** 高さ */
        private float height;
        
        /** 行の TextLayout */
        private TextLayout textLayout;
        
        /** 元となった行のインデックス */
        private int lineIndex;
        
        /** この見た目の行の先頭文字が元となった行の何文字目か */
        private int startCharIndex;
        
        /**
         * コンストラクタ
         * @param lineIndex 元となった行のインデックス
         * @param startCharIndex この見た目の行の先頭文字が元となった行の何文字目か
         * @param top 行の上端座標
         * @param textLayout 行の TextLayout
         */
        public LineLayout(int lineIndex, int startCharIndex, float top, float height, TextLayout textLayout) {
            this.lineIndex = lineIndex;
            this.startCharIndex = startCharIndex;
            this.top = top;
            this.height = height;
            this.textLayout = textLayout;
        }

        /**
         * lineIndex を取得します。
         * @return lineIndex を返します。
         */
        public int getLineIndex() {
            return lineIndex;
        }

        /**
         * startCharIndex を取得します。
         * @return startCharIndex を返します。
         */
        public int getStartCharIndex() {
            return startCharIndex;
        }

        /**
         * top を取得します。
         * @return top を返します。
         */
        public float getTop() {
            return top;
        }

        /**
         * height を取得します。
         * @return height を返します。
         */
        public float getHeight() {
            return height;
        }

        /**
         * textLayout を取得します。
         * @return textLayout を返します。
         */
        public TextLayout getTextLayout() {
            return textLayout;
        }
    }

    /**
     * コンストラクタ
     */
    public MessageComponent() {
        lineHeightFactor = 1.3f; /* TODO: G国なら1.5f */
        
        enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
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
     * メッセージの強調表示設定をセットします。
     * @param highlightSettingList 強調表示設定のリスト
     */
    public void setHighlightSettingList(List<HighlightSetting> highlightSettingList) {
        this.highlightSettingList = highlightSettingList;
    }

    /**
     * 属性付けを行った範囲に関する情報をセットします。
     * @param attributedAreaInfoList 属性付け範囲の情報のリスト
     */
    public void setAttributedAreaInfoList(List<AttributedAreaInfo> attributedAreaInfoList) {
        this.attributedAreaInfoList = attributedAreaInfoList;
    }
    
    /**
     * メッセージ中のリンクに関する情報をセットします。
     * @param linkInfoList リンク情報のリスト
     */
    public void setLinkInfoList(List<LinkInfo> linkInfoList) {
        this.linkInfoList = linkInfoList;
    }
    
    /**
     * コンポーネントに必要な領域のサイズを取得します。
     * @return コンポーネントに必要な領域のサイズを返します。
     */
    public Dimension2D getAreaSize() {
        return areaSize;
    }

    /**
     * リンククリックの通知を受け取るリスナーを追加します。
     * @param listener リスナー
     */
    public void addLinkClickListener(LinkClickListener listener) {
        eventListenerList.add(LinkClickListener.class, listener);
    }
    
    /**
     * リンククリックの通知を受け取るリスナーを削除します。
     * @param listener リスナー
     */
    public void removeLinkClickListener(LinkClickListener listener) {
        eventListenerList.remove(LinkClickListener.class, listener);
    }
    
    /**
     * 範囲選択オブジェクトをセットします。
     * @param rangeSelector 範囲選択オブジェクト
     */
    public void setRangeSelector(RangeSelector rangeSelector) {
        this.rangeSelector = rangeSelector;
    }
    
    /**
     * メッセージ中の特定の位置を指すためのクラス
     */
    private static class Position implements Comparable<Position> {
        /** 見た目の行。textLayouts のインデックスと等しくなります。*/
        private int lineLayoutIndex;
        
        /** lineLayoutIndex で示される見た目の行内の、位置情報 */
        private TextHitInfo textHitInfo;
        
        /**
         * コンストラクタ
         * @param lineLayoutIndex 見た目の行。
         * @param textHitInfolineLayoutIndex で示される見た目の行内の、位置情報。
         */
        public Position(int lineLayoutIndex, TextHitInfo textHitInfo) {
            this.lineLayoutIndex = lineLayoutIndex;
            this.textHitInfo = textHitInfo;
        }

        /**
         * 見た目の行を取得します。
         * @return ヒットした見た目の行。textLayouts のインデックスと等しくなります。
         */
        public int getLineLayoutIndex() {
            return lineLayoutIndex;
        }

        /**
         * getLineLayoutIndex() が返す見た目の行内の、位置情報を取得します。
         * @return 位置情報を返します。
         */
        public TextHitInfo getTextHitInfo() {
            return textHitInfo;
        }

        /**
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(Position other) {
            if (lineLayoutIndex < other.lineLayoutIndex) {
                return -1;
            } else if (lineLayoutIndex > other.lineLayoutIndex) {
                return 1;
            } else {
                return (textHitInfo.getInsertionIndex() - other.textHitInfo.getInsertionIndex());
            }
        }
        
        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Position other = (Position) obj;
            if (lineLayoutIndex != other.lineLayoutIndex)
                return false;
            if (textHitInfo == null) {
                if (other.textHitInfo != null)
                    return false;
            } else if (!textHitInfo.equals(other.textHitInfo))
                return false;
            return true;
        }
        
        /**
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + lineLayoutIndex;
            result = prime * result
                    + ((textHitInfo == null) ? 0 : textHitInfo.hashCode());
            return result;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "Position [lineLayoutIndex=" + lineLayoutIndex
                    + ", textHitInfo=" + textHitInfo + "]";
        }
    }
    
    /** ヒットした位置 */
    enum HitArea {
        /** 範囲外 */
        NONE,
        
        /** 通常の文字の上 */
        CHARACTER,
        
        /** リンクの上 */
        LINK,
    }
    
    /** ヒットテストの結果を格納するクラス */
    private static class HitResult {
        /** ヒットした位置にあるもの */
        private HitArea hitArea = HitArea.NONE;
        
        /** ヒットした位置 */
        private Position hitPosition = null;
        
        /** ヒットしたリンクの情報 */
        private LinkInfo hitLinkInfo = null;

        /**
         * hitArea を取得します。
         * @return hitArea を返します。
         */
        public HitArea getHitArea() {
            return hitArea;
        }

        /**
         * hitPosition を取得します。
         * @return hitPosition を返します。
         */
        public Position getHitPosition() {
            return hitPosition;
        }

        /**
         * hitLinkInfo を取得します。
         * @return hitLinkInfo を返します。
         */
        public LinkInfo getHitLinkInfo() {
            return hitLinkInfo;
        }

        /**
         * hitArea をセットします。
         * @param hitArea セットしたい hitArea の値
         */
        public void setHitArea(HitArea hitArea) {
            this.hitArea = hitArea;
        }

        /**
         * hitPosition をセットします。
         * @param hitPosition セットしたい hitPosition の値
         */
        public void setHitPosition(Position hitPosition) {
            this.hitPosition = hitPosition;
        }

        /**
         * hitLinkInfo をセットします。
         * @param hitLinkInfo セットしたい hitLinkInfo の値
         */
        public void setHitLinkInfo(LinkInfo hitLinkInfo) {
            this.hitLinkInfo = hitLinkInfo;
        }

    }
    
    /**
     * 指定された座標のヒットテストを行います。
     * @param x X座標
     * @param y Y座標
     * @return ヒットテストの結果
     */
    private HitResult hitTest(float x, float y) {
        HitResult result = new HitResult();
        
        if (lineLayouts == null ||
                x < 0 || x > areaSize.width || y < 0 || y > areaSize.height) {
            // エリア外
            result.setHitArea(HitArea.NONE);
        } else {
            LineLayout lineLayout = null;
            int lineLayoutIndex = -1;
            for (int ix = lineLayouts.size() - 1; ix >= 0; --ix) {
                lineLayout = lineLayouts.get(ix);
                if (lineLayout.getTop() < y) {
                    lineLayoutIndex = ix;
                    break;
                }
            }
            if (lineLayout == null) {
                // ここには来ないはず...
                Moltonf.getLogger().warning("unexpected result in hitTest()");
                assert false;
                result.setHitArea(HitArea.NONE);
                return result;
            }
            TextLayout textLayout = lineLayout.getTextLayout();
            if (textLayout == null) {
                // 空行の場合
                result.setHitArea(HitArea.NONE);
                result.setHitPosition(new Position(lineLayoutIndex, TextHitInfo.leading(0)));
            } else {
                TextHitInfo textHitInfo = textLayout.hitTestChar(x, y);
                result.setHitPosition(new Position(lineLayoutIndex, textHitInfo));
                int lineIndex = lineLayout.getLineIndex();
                int charIndex = lineLayout.getStartCharIndex() + textHitInfo.getCharIndex();
                
                // 文字上にあるかどうかの判定
                Rectangle2D lineBounds = textLayout.getBounds();
                lineBounds.setRect(
                        messageAreaRect.x + lineBounds.getX(),
                        lineLayout.getTop() + textLayout.getAscent() + lineBounds.getY(),
                        lineBounds.getWidth(),
                        lineBounds.getHeight());
                if (lineBounds.contains(x, y)) {
                    // リンク上にあるかどうかの判定
                    LinkInfo hitLinkInfo = null;
                    if (linkInfoList != null) {
                        for (LinkInfo linkInfo : linkInfoList) {
                            MessageRange range = linkInfo.getLink().getRange();
                            if (range.getLineIndex() == lineIndex &&
                                    range.getStart() <= charIndex && range.getEnd() > charIndex) {
                                hitLinkInfo = linkInfo;
                                break;
                            }
                        }
                    }
                    if (hitLinkInfo == null) {
                        result.setHitArea(HitArea.CHARACTER);
                    } else {
                        result.setHitArea(HitArea.LINK);
                        result.setHitLinkInfo(hitLinkInfo);
                    }
                } else {
                    result.setHitArea(HitArea.NONE);
                }
            }
        }
        
        return result;
    }
    
    /**
     * マウスイベントを処理している間の情報
     */
    private static class MouseEventProcessingContext {
        /** 対象のマウスイベント */
        private MouseEvent event;
        
        /** 範囲選択の処理を行うかどうか */
        private boolean isProcessRangeSelection = true;

        public MouseEventProcessingContext(MouseEvent event) {
            this.event = event;
        }
        
        public MouseEvent getEvent() {
            return event;
        }

        public boolean isProcessRangeSelection() {
            return isProcessRangeSelection;
        }

        public void setProcessRangeSelection(boolean isProcessRangeSelection) {
            this.isProcessRangeSelection = isProcessRangeSelection;
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
     * @param e
     */
    private void processMouseRelatedEvent(MouseEvent e) {
        MouseEventProcessingContext ctx = new MouseEventProcessingContext(e);
        int eventId = e.getID();
        if (eventId == MouseEvent.MOUSE_MOVED) {
            mouseMoved(ctx);
        } else if (eventId == MouseEvent.MOUSE_CLICKED) {
            mouseClicked(ctx);
        }
        
        if (ctx.isProcessRangeSelection() && rangeSelector != null) {
            rangeSelector.processMouseRelatedEvent(e);
        }
    }
    
    /**
     * マウスカーソルが動いたときの処理
     * @param ctx 処理コンテキスト
     */
    private void mouseMoved(MouseEventProcessingContext ctx) {
        MouseEvent event = ctx.getEvent();
        HitResult hitResult = hitTest(event.getX(), event.getY());
        HitArea hitArea = hitResult.getHitArea();
        if (hitArea == HitArea.LINK) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else if (hitArea == HitArea.CHARACTER) {
            setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }
    
    /**
     * マウスがクリックされたときにリスナーから呼ばれます。
     * @param e マウスイベント
     */
    private void mouseClicked(MouseEventProcessingContext ctx) {
        MouseEvent event = ctx.getEvent();
        HitResult hitResult = hitTest(event.getX(), event.getY());
        HitArea hitArea = hitResult.getHitArea();
        
        // リンクのクリックか？
        if (hitArea == HitArea.LINK){
            fireLinkClicked(hitResult.getHitLinkInfo().getLink(), event);
            ctx.setProcessRangeSelection(false);
        }
    }
    
    /**
     * リンクがクリックされたことを通知します。
     * @param link クリックされたリンク
     * @param mouseEvent 元となったマウスイベント
     */
    private void fireLinkClicked(Link link, MouseEvent mouseEvent) {
        // Guaranteed to return a non-null array
        Object[] listeners = eventListenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        LinkClickedEvent linkClickedEvent = null;
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==LinkClickListener.class) {
                // Lazily create the event:
                if (linkClickedEvent == null) {
                    linkClickedEvent = new LinkClickedEvent(this);
                    linkClickedEvent.setLink(link);
                }    
                ((LinkClickListener)listeners[i+1]).linkClicked(linkClickedEvent);
            }
        }
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
            for (int lineIndex = 0; lineIndex < lineList.size(); ++lineIndex) {
                String line = lineList.get(lineIndex);
                if (line.isEmpty()) {
                    FontMetrics fontMetrics = getFontMetrics(getFont());
                    addLineLayout(lineIndex, 0,
                            null,
                            fontMetrics.getAscent(),
                            fontMetrics.getDescent(),
                            fontMetrics.getLeading());
                } else {
                    AttributedString attributedString = makeAttributedString(line, lineIndex);
                    AttributedCharacterIterator charItr = attributedString.getIterator();
                    FontRenderContext frContext = g2.getFontRenderContext();
                    int startCharIndex = 0;
                    LineBreakMeasurer measurer = new LineBreakMeasurer(charItr, frContext);
                    while (measurer.getPosition() < line.length()) {
                        TextLayout oneLineLayout = measurer.nextLayout(width);
                        addLineLayout(lineIndex, startCharIndex,
                                oneLineLayout,
                                oneLineLayout.getAscent(),
                                oneLineLayout.getDescent(),
                                oneLineLayout.getLeading());
                        startCharIndex += oneLineLayout.getCharacterCount();
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
     * 1行分の AttributedString を生成します。
     * @param line 1行分のメッセージ文字列
     * @param lineIndex 行のインデックス
     * @return 生成した AttributedString
     */
    private AttributedString makeAttributedString(String line, int lineIndex) {
        AttributedString attributedString = new AttributedString(line);
        attributedString.addAttribute(TextAttribute.FONT, getFont());
        attributedString.addAttribute(TextAttribute.BACKGROUND, Paint.TRANSLUCENT);
        attributedString.addAttribute(TextAttribute.FOREGROUND, getForeground());

        // 強調表示
        if (highlightSettingList != null) {
            for (HighlightSetting highlightSetting : highlightSettingList) {
                if (!highlightSetting.isValid()) {
                    continue;
                }
                
                Pattern pattern = highlightSetting.getPattern();
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    attributedString.addAttribute(TextAttribute.FOREGROUND,
                            highlightSetting.getHighlightColor(),
                            matcher.start(),
                            matcher.end());
                }
            }
        }
        
        // リンク
        if (linkInfoList != null) {
            for (LinkInfo linkInfo : linkInfoList) {
                MessageRange range = linkInfo.getLink().getRange();
                if (range.getLineIndex() != lineIndex) {
                    continue;
                }
                
                attributedString.addAttribute(TextAttribute.FOREGROUND, linkInfo.getColor(),
                        range.getStart(), range.getEnd());
                attributedString.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON,
                        range.getStart(), range.getEnd());
            }
        }
        
        // 属性付け
        if (attributedAreaInfoList != null) {
            for (AttributedAreaInfo attrAreaInfo : attributedAreaInfoList) {
                if (attrAreaInfo.getRange().getLineIndex() != lineIndex) {
                    continue;
                }
                
                Map<AttributedCharacterIterator.Attribute, Object> attributes = new HashMap<AttributedCharacterIterator.Attribute, Object>();
                if (attrAreaInfo.getColor() != null) {
                    attributes.put(TextAttribute.FOREGROUND, attrAreaInfo.getColor());
                }
                
                if (!attributes.isEmpty()) {
                    attributedString.addAttributes(attributes, attrAreaInfo.getRange().getStart(), attrAreaInfo.getRange().getEnd());
                }
            }
        }
        
        return attributedString;
    }
    
    /**
     * 見た目の1行分のレイアウトを追加します。
     * updateLayoutから呼び出されるヘルパメソッドです。
     * @param lineIndex 元となる行のインデックス
     * @param startCharIndex 先頭文字の、元の行の文字インデックス
     * @param textLayout 1行の TextLayout。空行なら null。
     * @param ascent 行の ascent
     * @param descent 行の descent
     * @param leading 行の leading
     */
    private void addLineLayout(int lineIndex, int startCharIndex, TextLayout textLayout, float ascent, float descent, float leading) {
        float fontHeight = ascent + descent + leading;
        float lineHeight = fontHeight * lineHeightFactor;
        fontHeight = Math.round(fontHeight);
        lineHeight = Math.round(lineHeight);
        float lineTop = 0.0f;
        if (fontHeight < lineHeight) {
            lineTop += Math.round((lineHeight - fontHeight) / 2);
        }
        
        lineLayouts.add(new LineLayout(lineIndex, startCharIndex,
                areaSize.height + lineTop, lineHeight, textLayout));
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
            for (int lineLayoutIndex = 0; lineLayoutIndex < lineLayouts.size(); ++lineLayoutIndex) {
                LineLayout lineLayout = lineLayouts.get(lineLayoutIndex);
                TextLayout textLayout = lineLayout.getTextLayout();
                if (textLayout != null) {
                    // 選択範囲の描画
                    if (startOfSelectedRange != null && endOfSelectedRange != null) {
                        if (startOfSelectedRange.getLineLayoutIndex() <= lineLayoutIndex &&
                                endOfSelectedRange.getLineLayoutIndex() >= lineLayoutIndex) {
                            TextHitInfo beginInfo;
                            if (startOfSelectedRange.getLineLayoutIndex() == lineLayoutIndex) {
                                beginInfo = startOfSelectedRange.getTextHitInfo();
                            } else {
                                beginInfo = TextHitInfo.leading(0);
                            }
                            TextHitInfo endInfo;
                            if (endOfSelectedRange.getLineLayoutIndex() == lineLayoutIndex) {
                                endInfo = endOfSelectedRange.getTextHitInfo();
                            } else {
                                endInfo = TextHitInfo.trailing(textLayout.getCharacterCount() - 1);
                            }
                            Shape selectionShape = textLayout.getVisualHighlightShape(beginInfo, endInfo);
                            Color oldColor = g2.getColor();
                            Composite oldComposite = g2.getComposite();
                            g2.setPaintMode();
                            g2.setColor(new Color(0x69acfd));   // TODO: 選択範囲の色。どこで定義する？
                            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)); // TODO: 選択範囲のアルファ値。どこで定義する？
                            float tx = messageAreaRect.x;
                            float ty = lineLayout.getTop() + textLayout.getAscent();
                            g2.translate(tx, ty);
                            g2.fill(selectionShape);
                            g2.translate(-tx, -ty);
                            g2.setComposite(oldComposite);
                            g2.setColor(oldColor);
                        }
                    }
                    
                    // 文字列の描画
                    g2.setPaintMode();
                    textLayout.draw(g2, messageAreaRect.x,
                        lineLayout.getTop() + textLayout.getAscent());
                }
            }
        }
    }

    /**
     * @see com.hironytic.moltonf.view.Selectable#clearSelection()
     */
    @Override
    public void clearSelection() {
        if (startOfSelectedRange != null || endOfSelectedRange != null) {
            startOfSelectedRange = null;
            endOfSelectedRange = null;
            repaint();
        }
    }

    /**
     * @see com.hironytic.moltonf.view.Selectable#selectRange(java.awt.geom.Point2D, java.awt.geom.Point2D)
     */
    @Override
    public void selectRange(Point2D startPt, Point2D endPt) {
        if (lineLayouts == null) {
            return;
        }
        
        // startPt と endPt を Y座標の小さい順にする
        if (startPt.getY() > endPt.getY()){
            Point2D tempPt = endPt;
            endPt = startPt;
            startPt = tempPt;
        }
        float startX = (float)startPt.getX();
        float startY = (float)startPt.getY();
        float endX = (float)endPt.getX();
        float endY = (float)endPt.getY();
        
        // X座標について、範囲をはみ出したものを範囲内へ持ってくる
        if (startX < 0) {
            startX = 0;
        } else if (startX > areaSize.width) {
            startX = (int)areaSize.width;
        }
        if (endX < 0) {
            endX = 0;
        } else if (endX > areaSize.width) {
            endX = (int)areaSize.width;
        }
        
        Position startPos = null;
        if (startY < 0) {
            // 先頭から選択
            startPos = new Position(0, TextHitInfo.leading(0));
        } else if (startY > areaSize.height) {
            startPos = null;    // 選択範囲なし
        } else {
            startPos = hitTest(startX, startY).getHitPosition();
        }
        
        Position endPos = null;
        if (endY < 0) {
            startPos = null;    // 選択範囲なし
        } else if (endY > areaSize.height){
            // 末尾まで選択
            int lineLayoutIndex = lineLayouts.size() - 1;
            TextLayout textLayout = lineLayouts.get(lineLayoutIndex).getTextLayout();
            TextHitInfo textHitInfo;
            if (textLayout == null) {   // 最後が空行はあり得ないはずだが念のため
                textHitInfo = TextHitInfo.leading(0);
            } else {
                textHitInfo = TextHitInfo.trailing(textLayout.getCharacterCount() - 1);
            }
            endPos = new Position(lineLayoutIndex, textHitInfo);
        } else {
            endPos = hitTest(endX, endY).getHitPosition();
        }
        
        if (startPos == null || endPos == null) {
            clearSelection();
        } else {
            // 開始位置が終了位置より後にあれば入れ替える
            if (startPos.compareTo(endPos) > 0) {
                Position tempPos = endPos;
                endPos = startPos;
                startPos = tempPos;
            }
            
            if (!startPos.equals(startOfSelectedRange) || !endPos.equals(endOfSelectedRange)) {
                startOfSelectedRange = startPos;
                endOfSelectedRange = endPos;
                repaint();
            }
        }
    }
}
