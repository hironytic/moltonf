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

package com.hironytic.moltonf.model.archive;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.hironytic.moltonf.Moltonf;
import com.hironytic.moltonf.MoltonfException;
import com.hironytic.moltonf.model.Avatar;
import com.hironytic.moltonf.model.EventFamily;
import com.hironytic.moltonf.model.Story;
import com.hironytic.moltonf.model.StoryElement;
import com.hironytic.moltonf.model.StoryEvent;
import com.hironytic.moltonf.model.StoryPeriod;
import com.hironytic.moltonf.model.Talk;
import com.hironytic.moltonf.model.TalkType;
import com.hironytic.moltonf.model.VillageState;
import com.hironytic.moltonf.util.TimePart;

/**
 * 共通アーカイブ基盤のプレイデータアーカイブ用スキーマの XML を読み込んで Story を得るためのクラス
 * 
 * @see <a href="http://wolfbbs.jp/%B6%A6%C4%CC%A5%A2%A1%BC%A5%AB%A5%A4%A5%D6%B4%F0%C8%D7%C0%B0%C8%F7%B7%D7%B2%E8.html">共通アーカイブ基盤整備計画</a>
 */
public class ArchivedStoryLoaderStax {

    /** 読み込みに用いる StAX リーダー */
    private XMLStreamReader staxReader;
    
    /** ドキュメントのベース URI */
    private URI baseUri;
    
    /** 登場人物の識別子から Avatar オブジェクトを得るマップ */
    private Map<String, Avatar> avatarMap;
    
    /** DatatypeFactory インスタンス */
    private DatatypeFactory datatypeFactory = null;
    
    /**
     * 共通アーカイブ基盤用スキーマの XML を読み込む入力ストリームから Story を得ます。
     * @param inStream 共通アーカイブ基盤用スキーマで記述された XML の入力ストリーム
     * @return Story オブジェクト
     * @return MoltonfException 読み込みに失敗した場合
     */
    public static Story load(InputStream inStream) throws MoltonfException {
        return new ArchivedStoryLoaderStax().doload(inStream);
    }
    /**
     * コンストラクタ
     */
    private ArchivedStoryLoaderStax() {
        
    }

    /**
     * 共通アーカイブ基盤用スキーマの XML を読み込む入力ストリームから Story を得ます。(内部メソッド)
     * @param inStream 共通アーカイブ基盤用スキーマで記述された XML の入力ストリーム
     * @return Story オブジェクト
     * @return MoltonfException 読み込みに失敗した場合
     */
    private Story doload(InputStream inStream) throws MoltonfException {
        Story story = null;
        
        try {
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
            xmlInputFactory.setProperty(XMLInputFactory.IS_VALIDATING, false);
            xmlInputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, true);
            staxReader = xmlInputFactory.createXMLStreamReader(inStream);
            
            while (staxReader.hasNext()) {
                int eventType = staxReader.next();
                if (eventType == XMLStreamReader.START_ELEMENT) {
                    QName elemName = staxReader.getName();
                    if (SchemaConstants.NAME_VILLAGE.equals(elemName)) {
                        story = loadVillageElement();
                    } else {
                        throw new MoltonfException("Not a bbs play-data archive.");
                    }
                }
            }
        } catch (XMLStreamException ex) {
            throw new MoltonfException(ex);
        }
        
        return story;
    }
    
    /**
     * village 要素以下を読み込みます。
     * このメソッドが呼ばれたとき staxReader は village 要素の START_ELEMENT にいることが前提です。
     * @return 読み込んだ結果の Story
     * @throws XMLStreamException 読み込み時にエラーが発生した場合
     */
    private Story loadVillageElement() throws XMLStreamException {
        Story story = new Story();
        
        // 属性
        for (int ix = 0; ix < staxReader.getAttributeCount(); ++ix) {
            QName attrName = staxReader.getAttributeName(ix);
            if (SchemaConstants.NAME_BASE.equals(attrName)) {
                String baseUriString = staxReader.getAttributeValue(ix);
                if (!baseUriString.isEmpty()) {
                    try {
                        baseUri = new URI(baseUriString);
                    } catch (URISyntaxException ex) {
                        baseUri = null;
                    }
                }
            } else if (SchemaConstants.NAME_STATE.equals(attrName)) {
                String villageStateString = staxReader.getAttributeValue(ix);
                story.setVillageState(toVillageState(villageStateString));
            } else if (SchemaConstants.NAME_FULL_NAME.equals(attrName)) {
                String villageFullName = staxReader.getAttributeValue(ix);
                story.setVillageFullName(villageFullName);
            } else if (SchemaConstants.NAME_GRAVE_ICON_URI.equals(attrName)) {
                String graveIconUriString = staxReader.getAttributeValue(ix);
                URI graveIconUri;
                try {
                    if (baseUri != null) {
                        graveIconUri = baseUri.resolve(new URI(graveIconUriString));
                    } else {
                        graveIconUri = new URI(graveIconUriString);
                    }
                } catch (URISyntaxException ex) {
                    Moltonf.getLogger().warning("graveIconUri is not valid : " + graveIconUriString, ex);
                    graveIconUri = null;
                }
                story.setGraveIconUri(graveIconUri);
            }
        }
        
        List<StoryPeriod> periodList = new ArrayList<StoryPeriod>();
        
        // 子ノード
        while (staxReader.hasNext()) {
            int eventType = staxReader.next();
            if (eventType == XMLStreamReader.END_ELEMENT) {
                break;
            } else if (eventType == XMLStreamReader.START_ELEMENT) {
                QName elemName = staxReader.getName();
                if (SchemaConstants.NAME_AVATAR_LIST.equals(elemName)) {
                    story.setAvatarList(loadAvatarList(story));
                } else if (SchemaConstants.NAME_PERIOD.equals(elemName)) {
                    periodList.add(loadPeriod(story));
                } else {
                    skipElement();
                }
            }
        }
        
        story.setPeriods(periodList);
        return story;
    }
    
    /**
     * 村の状態の値文字列を VillageState に変換します。
     * @param villageStateString 村の状態の値文字列 (state属性の値)
     * @return VillageState。該当するものがなければ null。
     */
    private VillageState toVillageState(String villageStateString) {
        VillageState state = null;
        if (SchemaConstants.VAL_VILLAGE_STATE_GAMEOVER.equals(villageStateString)) {
            state = VillageState.GAMEOVER;
        } else if (SchemaConstants.VAL_VILLAGE_STATE_EPILOGUE.equals(villageStateString)) {
            state = VillageState.EPILOGUE;
        } else if (SchemaConstants.VAL_VILLAGE_STATE_PROGRESS.equals(villageStateString)) {
            state = VillageState.PROGRESS;
        } else if (SchemaConstants.VAL_VILLAGE_STATE_PROLOGUE.equals(villageStateString)) {
            state = VillageState.PROLOGUE;
        } else {
            Moltonf.getLogger().warning("invalid village state : <village state=\"" + villageStateString + "\">");
        }
        return state;
    }
    
    /**
     * avatarList 要素以下を読み込みます。
     * このメソッドが呼ばれたとき staxReader は avatarList 要素の START_ELEMENT にいることが前提です。
     * @param story 読み込む Avatar たちが所属する Story
     * @return 読み込んだ結果の Avatar のリスト
     * @throws XMLStreamException 読み込み時にエラーが発生した場合
     */
    private List<Avatar> loadAvatarList(Story story) throws XMLStreamException {
        avatarMap = new HashMap<String, Avatar>();
        List<Avatar> avatarList = new ArrayList<Avatar>();

        // 子ノード
        while (staxReader.hasNext()) {
            int eventType = staxReader.next();
            if (eventType == XMLStreamReader.END_ELEMENT) {
                break;
            } else if (eventType == XMLStreamReader.START_ELEMENT) {
                QName elemName = staxReader.getName();
                if (SchemaConstants.NAME_AVATAR.equals(elemName)) {
                    avatarList.add(loadAvatar(story));
                } else {
                    skipElement();
                }
            }
        }
        return avatarList;
    }
    
    /**
     * avatar 要素以下を読み込みます。
     * このメソッドが呼ばれたとき staxReader は avatar 要素の START_ELEMENT にいることが前提です。
     * @param story 読み込む Avatar が所属する Story
     * @return 読み込んだ結果の Avatar
     * @throws XMLStreamException 読み込み時にエラーが発生した場合
     */
    private Avatar loadAvatar(Story story) throws XMLStreamException {
        Avatar avatar = new Avatar();
        avatar.setStory(story);
        
        // 属性
        for (int ix = 0; ix < staxReader.getAttributeCount(); ++ix) {
            QName attrName = staxReader.getAttributeName(ix);
            if (SchemaConstants.NAME_AVATAR_ID.equals(attrName)) {
                avatar.setAvatarId(staxReader.getAttributeValue(ix));
            } else if (SchemaConstants.NAME_FULL_NAME.equals(attrName)) {
                avatar.setFullName(staxReader.getAttributeValue(ix));
            } else if (SchemaConstants.NAME_SHORT_NAME.equals(attrName)) {
                avatar.setShortName(staxReader.getAttributeValue(ix));
            } else if (SchemaConstants.NAME_FACE_ICON_URI.equals(attrName)) {
                String faceIconUriString = staxReader.getAttributeValue(ix);
                URI faceIconUri;
                try {
                    if (baseUri != null) {
                        faceIconUri = baseUri.resolve(new URI(faceIconUriString));
                    } else {
                        faceIconUri = new URI(faceIconUriString);
                    }
                } catch (URISyntaxException ex) {
                    Moltonf.getLogger().warning("faceIconUri is not valid : " + faceIconUriString, ex);
                    faceIconUri = null;
                }
                avatar.setFaceIconUri(faceIconUri);
            }
        }

        // 子ノード
        while (staxReader.hasNext()) {
            int eventType = staxReader.next();
            if (eventType == XMLStreamReader.END_ELEMENT) {
                break;
            } else if (eventType == XMLStreamReader.START_ELEMENT) {
                skipElement();
            }
        }
        
        avatarMap.put(avatar.getAvatarId(), avatar);
        return avatar;
    }
    
    /**
     * period 要素以下を読み込みます。
     * このメソッドが呼ばれたとき staxReader は period 要素の START_ELEMENT にいることが前提です。
     * @param story 読み込んだ StoryPeriod が所属する Story
     * @return 読み込んだ結果の StoryPeriod
     * @throws XMLStreamException 読み込み時にエラーが発生した場合
     */
    private StoryPeriod loadPeriod(Story story) throws XMLStreamException {
        StoryPeriod period = new StoryPeriod();
        period.setStory(story);
        List<StoryElement> elementList = new ArrayList<StoryElement>();
        
        // 属性
        // TODO:
        
        final List<QName> eventAnnounceGroup = Arrays.asList(new QName[] {
            SchemaConstants.NAME_START_ENTRY, SchemaConstants.NAME_ON_STAGE,
            SchemaConstants.NAME_START_MIRROR, SchemaConstants.NAME_OPEN_ROLE,
            SchemaConstants.NAME_MURDERED, SchemaConstants.NAME_START_ASSAULT,
            SchemaConstants.NAME_SURVIVOR, SchemaConstants.NAME_COUNTING,
            SchemaConstants.NAME_SUDDEN_DEATH, SchemaConstants.NAME_NO_MURDER,
            SchemaConstants.NAME_WIN_VILLAGE, SchemaConstants.NAME_WIN_WOLF,
            SchemaConstants.NAME_WIN_HAMSTER, SchemaConstants.NAME_PLAYER_LIST,
            SchemaConstants.NAME_PANIC,
        });
        final List<QName> eventOrderGroup = Arrays.asList(new QName[] {
            SchemaConstants.NAME_ASK_ENTRY, SchemaConstants.NAME_ASK_COMMIT,
            SchemaConstants.NAME_NO_COMMENT, SchemaConstants.NAME_STAY_EPILOGUE,
            SchemaConstants.NAME_GAME_OVER,
        });
        final List<QName> eventExtraGroup = Arrays.asList(new QName[] {
            SchemaConstants.NAME_JUDGE, SchemaConstants.NAME_GUARD,
            SchemaConstants.NAME_ASSAULT,
        });
        
        // 子ノード
        while (staxReader.hasNext()) {
            int eventType = staxReader.next();
            if (eventType == XMLStreamReader.END_ELEMENT) {
                break;
            } else if (eventType == XMLStreamReader.START_ELEMENT) {
                QName elemName = staxReader.getName();
                if (SchemaConstants.NAME_TALK.equals(elemName)) {
                    elementList.add(loadTalk(period));
                } else if (eventAnnounceGroup.contains(elemName)) {
                    elementList.add(loadStoryEvent(period, EventFamily.ANNOUNCE));
                } else if (eventOrderGroup.contains(elemName)) {
                    elementList.add(loadStoryEvent(period, EventFamily.ORDER));
                } else if (eventExtraGroup.contains(elemName)) {
                    elementList.add(loadStoryEvent(period, EventFamily.EXTRA));
                } else {
                    skipElement();
                }
            }
        }
        
        period.setStoryElements(elementList);
        return period;
    }
    
    /**
     * talk 要素以下を読み込みます。
     * このメソッドが呼ばれたとき staxReader は talk 要素の START_ELEMENT にいることが前提です。
     * @param period 読み込んだ Talk が所属する StoryPeriod。
     * @return 読み込んだ結果の Talk
     * @throws XMLStreamException 読み込み時にエラーが発生した場合
     */
    private Talk loadTalk(StoryPeriod period) throws XMLStreamException {
        Talk talk = new Talk();
        talk.setStoryPeriod(period);
        
        // 属性
        for (int ix = 0; ix < staxReader.getAttributeCount(); ++ix) {
            QName attrName = staxReader.getAttributeName(ix);
            if (SchemaConstants.NAME_TYPE.equals(attrName)) {
                String talkTypeString = staxReader.getAttributeValue(ix);
                talk.setTalkType(toTalkType(talkTypeString));
            } else if (SchemaConstants.NAME_AVATAR_ID.equals(attrName)) {
                // スキーマとして avatarList が先に登場することが保証されているので
                // 正しいデータならこの時点で avatarMap は作成済み。
                String avatarId = staxReader.getAttributeValue(ix);
                Avatar avatar = avatarMap.get(avatarId);
                talk.setSpeaker(avatar);
            } else if (SchemaConstants.NAME_TIME.equals(attrName)) {
                String timeString = staxReader.getAttributeValue(ix);
                TimePart timePart = parseTime(timeString);
                talk.setTime(timePart);
            }
        }
        
        List<String> messageLines = new ArrayList<String>();        
        
        // 子ノード
        while (staxReader.hasNext()) {
            int eventType = staxReader.next();
            if (eventType == XMLStreamReader.END_ELEMENT) {
                break;
            } else if (eventType == XMLStreamReader.START_ELEMENT) {
                QName elemName = staxReader.getName();
                if (SchemaConstants.NAME_LI.equals(elemName)) {
                    messageLines.add(loadLi());
                } else {
                    skipElement();
                }
            }
        }
        
        talk.setMessageLines(messageLines);
        return talk;
    }

    /**
     * 発言種別の文字列を TalkType に変換します。
     * @param talkTypeString 発言種別の文字列 (type属性の値)
     * @return TalkType。該当するものがなければ null。
     */
    private TalkType toTalkType(String talkTypeString) {
        TalkType talkType = null;
        if (SchemaConstants.VAL_TALK_TYPE_PUBLIC.equals(talkTypeString)) {
            talkType = TalkType.PUBLIC;
        } else if (SchemaConstants.VAL_TALK_TYPE_WOLF.equals(talkTypeString)) {
            talkType = TalkType.WOLF;
        } else if (SchemaConstants.VAL_TALK_TYPE_PRIVATE.equals(talkTypeString)) {
            talkType = TalkType.PRIVATE;
        } else if (SchemaConstants.VAL_TALK_TYPE_GRAVE.equals(talkTypeString)) {
            talkType = TalkType.GRAVE;
        } else {
            Moltonf.getLogger().warning("invalid village state : <talk type=\"" + talkTypeString + "\">");
        }
        return talkType;
    }
    
    /**
     * イベント系の要素以下を読み込みます。
     * このメソッドが呼ばれたとき staxReader は対象要素の START_ELEMENT にいることが前提です。
     * @param period 読み込んだ StoryEvent が所属する StoryPeriod。
     * @param eventFamily イベントの種別
     * @return 読み込んだ結果の StoryEvent
     * @throws XMLStreamException 読み込み時にエラーが発生した場合
     */
    private StoryEvent loadStoryEvent(StoryPeriod period, EventFamily eventFamily) throws XMLStreamException {
        StoryEvent storyEvent = new StoryEvent();
        storyEvent.setStoryPeriod(period);
        storyEvent.setEventFamily(eventFamily);

        List<String> messageLines = new ArrayList<String>();        
        
        // 子ノード
        while (staxReader.hasNext()) {
            int eventType = staxReader.next();
            if (eventType == XMLStreamReader.END_ELEMENT) {
                break;
            } else if (eventType == XMLStreamReader.START_ELEMENT) {
                QName elemName = staxReader.getName();
                if (SchemaConstants.NAME_LI.equals(elemName)) {
                    messageLines.add(loadLi());
                } else {
                    skipElement();
                }
            }
        }
        
        storyEvent.setMessageLines(messageLines);
        return storyEvent;
    }
    
    /**
     * li 要素以下を読み込みます。
     * このメソッドが呼ばれたとき staxReader は li 要素の START_ELEMENT にいることが前提です。
     * @return 読み込んだ結果の 1 行分の文字列
     * @throws XMLStreamException
     */
    private String loadLi() throws XMLStreamException {
        StringBuilder buf = new StringBuilder();
        
        // 子ノード
        while (staxReader.hasNext()) {
            int eventType = staxReader.next();
            if (eventType == XMLStreamReader.END_ELEMENT) {
                break;
            } else if (eventType == XMLStreamReader.START_ELEMENT) {
// TODO: rawData 対応
//                QName elemName = staxReader.getName();
//                if (SchemaConstants.NAME_RAW_DATA.equals(elemName)) {
//                    
//                } else {
                    skipElement();
//                }
            } else if (eventType == XMLStreamReader.CHARACTERS) {
                buf.append(staxReader.getText());
            }
        }
        
        return buf.toString();
    }

    /**
     * 時刻文字列を解析します。
     * @param timeString 時刻文字列
     * @return 解析結果を格納した TimePart オブジェクト
     */
    private TimePart parseTime(String timeString) {
        if (datatypeFactory == null) {
            try {
                datatypeFactory = DatatypeFactory.newInstance();
            } catch (DatatypeConfigurationException ex) {
                throw new MoltonfException(ex);
            }
        }
        
        XMLGregorianCalendar xmlCalendar;
        try {
            xmlCalendar = datatypeFactory.newXMLGregorianCalendar(timeString);
        } catch (IllegalArgumentException ex) {
            return null;
        }
        
        int hour = xmlCalendar.getHour();
        if (hour == DatatypeConstants.FIELD_UNDEFINED) {
            hour = 0;
        }
        int minute = xmlCalendar.getMinute();
        if (minute == DatatypeConstants.FIELD_UNDEFINED) {
            minute = 0;
        }
        int second = xmlCalendar.getSecond();
        if (second == DatatypeConstants.FIELD_UNDEFINED) {
            second = 0;
        }
        int millisecond = xmlCalendar.getMillisecond();
        if (millisecond == DatatypeConstants.FIELD_UNDEFINED) {
            millisecond = 0;
        }
        return new TimePart(hour, minute, second, millisecond);
    }    
    
    /**
     * 現在の要素以下をスキップします
     * @throws XMLStreamException 読み込み時にエラーが発生した場合
     */
    private void skipElement() throws XMLStreamException {
        while (staxReader.hasNext()) {
            int eventType = staxReader.next();
            if (eventType == XMLStreamReader.END_ELEMENT) {
                break;
            } else if (eventType == XMLStreamReader.START_ELEMENT) {
                skipElement();
            }
        }
    }
}
