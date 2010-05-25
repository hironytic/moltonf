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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;

import com.hironytic.moltonf.model.HighlightSetting;
import com.hironytic.moltonf.model.Link;
import com.hironytic.moltonf.model.MessageRange;
import com.hironytic.moltonf.model.Talk;
import com.hironytic.moltonf.model.TalkType;
import com.hironytic.moltonf.util.TimePart;

/**
 * 発言の表示を担当するビュー
 */
@SuppressWarnings("serial")
public class TalkView extends JComponent implements MoltonfView {

    /** ビューの左の余白 */
    private static final float VIEW_PADDING_LEFT = 16;
    
    /** ビューの右の余白 */
    private static final float VIEW_PADDING_RIGHT = 16;

    /** ビューの上の余白 */
    private static final float VIEW_PADDING_TOP = 8;
    
    /** ビューの下の余白 */
    private static final float VIEW_PADDING_BOTTOM = 8;
    
    /** メッセージの上のマージン */
    private static final float MESSAGE_MARGIN_TOP = 4;
    
    /** メッセージの左の余白 */
    private static final float MESSAGE_PADDING_LEFT = 12;
    
    /** メッセージの右の余白 */
    private static final float MESSAGE_PADDING_RIGHT = 12;
    
    /** メッセージの上の余白 */
    private static final float MESSAGE_PADDING_TOP = 8;
    
    /** メッセージの下の余白 */
    private static final float MESSAGE_PADDING_BOTTOM = 8;
    
    /** メッセージ領域の左端 */
    private static final float MESSAGE_LEFT = 56;
    
    /** メッセージの角を描画するときの半径 */
    private static final float MESSAGE_CORNER_RADIUS = 8;
    
    /** 顔アイコン画像の左端 */
    private static final float FACE_ICON_IMAGE_LEFT = 0;
    
    /** 情報テキスト (発言者など) の文字色 */
    private static final Color INFO_TEXT_COLOR = new Color(0xdddddd);
    
    /** 情報テキストのうち発言時刻の文字色 */
    private static final Color INFO_TIME_TEXT_COLOR = new Color(0x888888); 
    
    /** 通常メッセージの背景色 */
    private static final Color MESSAGE_BG_COLOR_PUBLIC = new Color(0xffffff);
    
    /** 通常メッセージの文字色 */
    private static final Color MESSAGE_TEXT_COLOR_PUBLIC = new Color(0x000000);

    /** 狼メッセージの背景色 */
    private static final Color MESSAGE_BG_COLOR_WOLF = new Color(0xff7777);
    
    /** 狼メッセージの文字色 */
    private static final Color MESSAGE_TEXT_COLOR_WOLF = new Color(0x000000);

    /** 独り言メッセージの背景色 */
    private static final Color MESSAGE_BG_COLOR_PRIVATE = new Color(0x939393);
    
    /** 独り言メッセージの文字色 */
    private static final Color MESSAGE_TEXT_COLOR_PRIVATE = new Color(0x000000);

    /** 墓下メッセージの背景色 */
    private static final Color MESSAGE_BG_COLOR_GRAVE = new Color(0x9fb7cf);
    
    /** 墓下メッセージの文字色 */
    private static final Color MESSAGE_TEXT_COLOR_GRAVE = new Color(0x000000);
    
    /** このビューが扱う発言 */
    private Talk talk;
    
    /** ビューの更新が必要かどうか */
    private boolean isUpdateRequired = false;
    
    /** コンポーネントに必要な領域のサイズ */
    private Dimension2DFloat areaSize = new Dimension2DFloat();
    
    /** 会話情報（発言者、発言時刻など）を表示する子コンポーネント */
    private MessageComponent talkInfoComponent = new MessageComponent();
    
    /** メッセージ領域を表示する子コンポーネント */
    private MessageComponent talkMessageComponent = new MessageComponent();
    
    /** 何も行わない ImageObserver */
    private static final ImageObserver nullObserver = new ImageObserver() {
        @Override
        public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
            return false;
        }
    };
    
    /**
     * コンストラクタ
     */
    public TalkView() {
        this.add(talkInfoComponent);
        this.add(talkMessageComponent);
    }

    /**
     * このビューが表示している Talk オブジェクトを返します。
     * @return Talk オブジェクト
     */
    public Talk getTalk() {
        return talk;
    }
    
    /**
     * このビューが表示する Talk オブジェクトをセットします。
     * セットしたあとに、updateView() を呼び出すことでレイアウトが整います。
     * @param talk Talk オブジェクト
     */
    public void setTalk(Talk talk) {
        this.talk = talk;
        isUpdateRequired = true;
    }

    /**
     * 発言の強調表示設定をセットします。
     * @param highlightSettingList 強調表示設定のリスト
     */
    public void setHighlightSettingList(List<HighlightSetting> highlightSettingList) {
        talkMessageComponent.setHighlightSettingList(highlightSettingList);
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
        switch (talk.getTalkType()) {
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
        default:
            color = MESSAGE_TEXT_COLOR_PUBLIC;
            break;
        }
        return color;
    }

    /**
     * メッセージの背景色を返します。
     * @return メッセージの背景色
     */
    private Color getMessageBackgroundColor() {
        Color color;
        switch (talk.getTalkType()) {
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
        default:
            color = MESSAGE_BG_COLOR_PUBLIC;
            break;
        }
        return color;
    }
    
    /**
     * 発言者の顔アイコン画像を得ます
     * @return 顔アイコン画像
     */
    private Image getFaceIconImage() {
        if (talk.getTalkType() == TalkType.GRAVE) {
            return talk.getStory().getGraveIconImage();
        } else {
            return talk.getSpeaker().getFaceIconImage();
        }
    }
    
    /**
     * 画像の幅と高さを取得します。
     * @param image 画像
     * @return 幅と高さ
     */
    private Dimension2D getImageDimension(Image image) {
        float width = image.getWidth(nullObserver);
        float height = image.getHeight(nullObserver);
        return new Dimension2DFloat(width, height);
    }
    
    /**
     * メッセージの吹き出しの「しっぽ」の形を返します。
     * @return
     */
    private Shape getMessageBaloonShippoShape(Rectangle2D.Float baloonRect) {
        Path2D.Float path = new Path2D.Float();
        if (talk.getTalkType() == TalkType.PUBLIC) {
            float x1 = baloonRect.x;
            float y1 = baloonRect.y + (baloonRect.height - 8) / 2;
            path.moveTo(x1, y1);
            float y2 = y1 + 8;
            path.lineTo(x1, y2);
            float x2 = x1 - 8;
            path.lineTo(x2, y2);
            path.lineTo(x1, y1);
            return new Area(path);
        } else {
            float y1 = baloonRect.y + (baloonRect.height - 6) / 2;
            Ellipse2D.Float bigBubble = new Ellipse2D.Float(
                    baloonRect.x - 8,
                    y1,
                    6,
                    6);
            Ellipse2D.Float smallBubble = new Ellipse2D.Float(
                    baloonRect.x - 14,
                    y1 + 2,
                    4,
                    4);
            Area area = new Area(bigBubble);
            area.add(new Area(smallBubble));
            return area;
        }
    }
    
    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (talk == null) {
            return;
        }
        
        // メッセージの吹き出しの領域を計算
        Rectangle2D messageAreaRect = talkMessageComponent.getBounds();
        Rectangle2D.Float drawRect = new Rectangle2D.Float(
                (float)(messageAreaRect.getX() - MESSAGE_PADDING_LEFT),
                (float)(messageAreaRect.getY() - MESSAGE_PADDING_TOP),
                (float)(messageAreaRect.getWidth() + MESSAGE_PADDING_LEFT + MESSAGE_PADDING_RIGHT),
                (float)(messageAreaRect.getHeight() + MESSAGE_PADDING_TOP + MESSAGE_PADDING_BOTTOM));

        Graphics2D g2d = (Graphics2D)g;
        
        Color oldColor = g2d.getColor();
        Object oldAntialiasingHint = g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        try {
            // 吹き出しの描画
            g.setColor(getMessageBackgroundColor());
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.fill(new RoundRectangle2D.Float(
                    drawRect.x, drawRect.y,
                    drawRect.width, drawRect.height,
                    MESSAGE_CORNER_RADIUS * 2, MESSAGE_CORNER_RADIUS * 2));
            g2d.fill(getMessageBaloonShippoShape(drawRect));
            
            // 顔アイコン画像の描画
            Image faceIconImage = getFaceIconImage();
            if (faceIconImage != null) {
                Dimension2D faceIconSize = getImageDimension(faceIconImage);
                float faceIconHeight = (float)faceIconSize.getHeight();
                float faceIconTop;
                if (faceIconHeight > drawRect.height) {
                    faceIconTop = drawRect.y;
                } else {
                    faceIconTop = drawRect.y + (drawRect.height - faceIconHeight) / 2;
                }
                g2d.drawImage(faceIconImage,
                        (int)(VIEW_PADDING_LEFT + FACE_ICON_IMAGE_LEFT),
                        (int)faceIconTop,
                        nullObserver);
            }
            
        } finally {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAntialiasingHint);
            g2d.setColor(oldColor);
        }
    }

    /**
     * ビューの更新を行います。
     */
    @Override
    public void updateView() {
        if (!isUpdateRequired) {
            return;
        }
        isUpdateRequired = false;
        
        if (talk == null) {
            return;
        }
        
        areaSize.height = 0f;
        
        // 発言情報 (発言者名、発言時刻など)
        int start, end;
        StringBuilder infoTextBuilder = new StringBuilder();
        String speakerName = talk.getSpeaker().getFullName();
        infoTextBuilder.append(speakerName);
        TimePart time = talk.getTime();
        String timeString = String.format("%02d:%02d", time.getHourPart(), time.getMinutePart());
        infoTextBuilder.append(" ");
        start = infoTextBuilder.length();
        infoTextBuilder.append(timeString);
        end = start + timeString.length();
        talkInfoComponent.setMessage(Collections.singletonList(infoTextBuilder.toString()));  // TODO: 発言回数なども
        talkInfoComponent.setForeground(INFO_TEXT_COLOR);
        talkInfoComponent.setAttributedAreaInfoList(Arrays.asList(
            new MessageComponent.AttributedAreaInfo(
                    new MessageRange(0, start, end),
                    INFO_TIME_TEXT_COLOR)
        ));
        talkInfoComponent.updateLayout(areaSize.width - (VIEW_PADDING_LEFT + VIEW_PADDING_RIGHT));
        Dimension2D infoAreaSize = talkInfoComponent.getAreaSize();
        Rectangle2D infoAreaRect = new Rectangle2D.Float(
                VIEW_PADDING_LEFT,
                VIEW_PADDING_TOP,
                (float)infoAreaSize.getWidth(),
                (float)infoAreaSize.getHeight());
        Rectangle infoAreaRectInt = new Rectangle();
        infoAreaRectInt.setRect(infoAreaRect);
        talkInfoComponent.setBounds(infoAreaRectInt);
        
        areaSize.height += (float)infoAreaSize.getHeight();

        // 顔アイコン
        Dimension2D faceIconSize;
        Image faceIconImage = getFaceIconImage();
        if (faceIconImage != null) {
            faceIconSize = getImageDimension(faceIconImage);
        } else {
            faceIconSize = new Dimension2DFloat(0f, 0f);
        }
        
        // 発言
        talkMessageComponent.setMessage(talk.getMessageLines());
        talkMessageComponent.setForeground(getMessageTextColor());
//        //test
//        class DummyLink extends Link {
//            DummyLink(MessageRange range) { super(range); }
//        }
//        List<Link> linkList = new ArrayList<Link>();
//        linkList.add(new DummyLink(new MessageRange(0, 0, 2)));
//        talkMessageComponent.setLinkList(linkList);
//        //test
        talkMessageComponent.updateLayout(areaSize.width - (VIEW_PADDING_LEFT + MESSAGE_LEFT + MESSAGE_PADDING_LEFT + MESSAGE_PADDING_RIGHT + VIEW_PADDING_RIGHT));
        Dimension2D messageAreaSize = talkMessageComponent.getAreaSize();
        Rectangle2D messageAreaRect = new Rectangle2D.Float(
                VIEW_PADDING_LEFT + MESSAGE_LEFT + MESSAGE_PADDING_LEFT,
                VIEW_PADDING_TOP + (float)infoAreaSize.getHeight() + MESSAGE_MARGIN_TOP + MESSAGE_PADDING_TOP,
                (float)messageAreaSize.getWidth(),
                (float)messageAreaSize.getHeight());
        Rectangle messageAreaRectInt = new Rectangle();
        messageAreaRectInt.setRect(messageAreaRect);
        talkMessageComponent.setBounds(messageAreaRectInt);
        float talkMessageHeight = MESSAGE_PADDING_TOP +
                                  (float)messageAreaSize.getHeight() +
                                  MESSAGE_PADDING_BOTTOM;

        areaSize.height += VIEW_PADDING_TOP +
                           Math.max(talkMessageHeight, faceIconSize.getHeight()) +
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
        
        talkInfoComponent.setFont(font);
        talkMessageComponent.setFont(font);
    }
}
