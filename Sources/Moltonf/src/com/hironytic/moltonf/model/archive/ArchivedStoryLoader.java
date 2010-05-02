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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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
import com.hironytic.moltonf.util.DomUtils;
import com.hironytic.moltonf.util.TimePart;

/**
 * 共通アーカイブ基盤のプレイデータアーカイブ用スキーマの XML を読み込んで Story を得るためのクラス
 * 
 * @see <a href="http://wolfbbs.jp/%B6%A6%C4%CC%A5%A2%A1%BC%A5%AB%A5%A4%A5%D6%B4%F0%C8%D7%C0%B0%C8%F7%B7%D7%B2%E8.html">共通アーカイブ基盤整備計画</a>
 */
public class ArchivedStoryLoader {

    /**
     * 共通アーカイブ基盤用スキーマの XML から Story を得ます。
     * @param archiveDoc 共通アーカイブ基盤用スキーマで記述された XML の DOM Document オブジェクト
     * @return Story オブジェクト
     * @throws MoltonfException 読み込みに失敗した場合
     */
    public static Story load(Document archiveDoc) throws MoltonfException {
        ArchivedStoryLoader loader = new ArchivedStoryLoader();
        loader.archiveDoc = archiveDoc;
        loader.doLoad();
        return loader.story;
    }

    /** 共通アーカイブ基盤用スキーマで記述された XML の DOM Document オブジェクト */
    private Document archiveDoc;
    
    /** 読み込んだ結果のストーリー */
    private Story story;
    
    /** ドキュメントのベース URI */
    private URI baseUri;
    
    /** 登場人物の識別子から Avatar オブジェクトを得るマップ */
    private Map<String, Avatar> avatarMap;
    
    /** DatatypeFactory インスタンス */
    private DatatypeFactory datatypeFactory = null;
    
    /**
     * コンストラクタ
     * 外部からはインスタンスを生成できません。
     * static メソッド load() を使ってください。
     */
    private ArchivedStoryLoader() {
    }
    
    /**
     * 実際に読み込みを行うメイン処理です。
     */
    private void doLoad() {
        story = new Story();
        
        // ドキュメントのベース URI
        loadBaseUri();
        
        // 村の情報
        loadVillageState();
        loadVillageFullName();
        
        // 登場人物
        loadAvatarList();
        
        // 日
        loadPeriods();
    }
    
    /**
     * ドキュメントのベース URI を読み込みます。
     */
    private void loadBaseUri() {
        baseUri = null;
        Element villageElem = archiveDoc.getDocumentElement();
        String baseUriString = villageElem.getAttributeNS(SchemaConstants.NS_XML, SchemaConstants.LN_BASE);
        if (!baseUriString.isEmpty()) {
            try {
                baseUri = new URI(baseUriString);
            } catch (URISyntaxException ex) {
                baseUri = null;
            }
        }
    }
    
    /**
     * 村の状態を読み込みます。
     */
    private void loadVillageState() {
        Element villageElem = archiveDoc.getDocumentElement();
        VillageState state = null;
        String villageStateString = villageElem.getAttributeNS(null, SchemaConstants.LN_STATE);
        if (SchemaConstants.VAL_VILLAGE_STATE_GAMEOVER.equals(villageStateString)) {
            state = VillageState.GAMEOVER;
        } else if (SchemaConstants.VAL_VILLAGE_STATE_EPILOGUE.equals(villageStateString)) {
            state = VillageState.EPILOGUE;
        } else if (SchemaConstants.VAL_VILLAGE_STATE_PROGRESS.equals(villageStateString)) {
            state = VillageState.PROGRESS;
        } else if (SchemaConstants.VAL_VILLAGE_STATE_PROLOGUE.equals(villageStateString)) {
            state = VillageState.PROLOGUE;
        }
        story.setVillageState(state);
    }
    
    /**
     * 村のフルネームを読み込みます。
     */
    private void loadVillageFullName() {
        Element villageElem = archiveDoc.getDocumentElement();
        String fullName = villageElem.getAttributeNS(null, SchemaConstants.LN_FULL_NAME);
        story.setVillageFullName(fullName);
    }
    
    /**
     * 登場人物のリストを読み込みます。
     */
    private void loadAvatarList() {
        Element villageElem = archiveDoc.getDocumentElement();
        
        avatarMap = new HashMap<String, Avatar>();
        List<Avatar> list = new ArrayList<Avatar>();
        Node avatarListElem = DomUtils.searchSiblingElementForward(villageElem.getFirstChild(),
                SchemaConstants.NS_ARCHIVE, SchemaConstants.LN_AVATAR_LIST);
        if (avatarListElem != null) {
            Element avatarElem = DomUtils.searchSiblingElementForward(avatarListElem.getFirstChild(),
                    SchemaConstants.NS_ARCHIVE, SchemaConstants.LN_AVATAR);
            while (avatarElem != null) {
                Avatar avatar = new Avatar();
                String avatarId =  avatarElem.getAttributeNS(null, SchemaConstants.LN_AVATAR_ID);
                avatar.setAvatarId(avatarId);
                avatar.setFullName(avatarElem.getAttributeNS(null, SchemaConstants.LN_FULL_NAME));
                avatar.setShortName(avatarElem.getAttributeNS(null, SchemaConstants.LN_SHORT_NAME));
                
                // 顔アイコン
                String faceIconUriString = avatarElem.getAttributeNS(null, SchemaConstants.LN_FACE_ICON_URI);
                URI faceIconUri;
                try {
                    if (baseUri != null) {
                        faceIconUri = baseUri.resolve(new URI(faceIconUriString));
                    } else {
                        faceIconUri = new URI(faceIconUriString);
                    }
                } catch (URISyntaxException e) {
                    faceIconUri = null;
                }
                avatar.setFaceIconUri(faceIconUri);
                
                /* TODO: 短縮名をどうにかしてセット */
                
                list.add(avatar);                
                avatarMap.put(avatarId, avatar);

                avatarElem = DomUtils.searchSiblingElementForward(avatarElem.getNextSibling(),
                        SchemaConstants.NS_ARCHIVE, SchemaConstants.LN_AVATAR);
            }
        }
        story.setAvatarList(list);
    }
    
    /**
     * それぞれの日の情報を読み込みます。
     */
    private void loadPeriods() {
        Element villageElem = archiveDoc.getDocumentElement();
        
        List<StoryPeriod> periodList = new ArrayList<StoryPeriod>();
        Element periodElem = DomUtils.searchSiblingElementForward(villageElem.getFirstChild(),
                SchemaConstants.NS_ARCHIVE, SchemaConstants.LN_PERIOD);
        while (periodElem != null) {
            StoryPeriod period = loadOnePeriod(periodElem);
            periodList.add(period);
            
            periodElem = DomUtils.searchSiblingElementForward(periodElem.getNextSibling(),
                    SchemaConstants.NS_ARCHIVE, SchemaConstants.LN_PERIOD);
        }
        story.setPeriods(periodList);
    }
    
    /**
     * 1 日分の情報を読み込みます。
     * @param periodElem period 要素
     * @return 読み込んだ結果の StoryPeriod オブジェクト
     */
    private StoryPeriod loadOnePeriod(Element periodElem) {
        StoryPeriod period = new StoryPeriod();
        List<StoryElement> elementList = new ArrayList<StoryElement>();
        
        Node child = periodElem.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElem = (Element)child;
                StoryElement storyElement = null;
                if (DomUtils.isMatchNode(childElem, SchemaConstants.NS_ARCHIVE, SchemaConstants.LN_TALK)) {
                    storyElement = loadTalk(childElem);
                }
                storyElement = (storyElement != null) ? storyElement : loadEventAnnounceGroupIfMaches(childElem);
                storyElement = (storyElement != null) ? storyElement : loadEventOrderGroupIfMatches(childElem);
                storyElement = (storyElement != null) ? storyElement : loadEventExtraGroupIfMathces(childElem);
                
                if (storyElement != null) {
                    elementList.add(storyElement);
                }
            }
            child = child.getNextSibling();
        }
        period.setStoryElements(elementList);
        return period;
    }
    
    /**
     * 指定された要素が EventAnounceGroup に一致するなら読み込みます。
     * @param elem 要素
     * @return 一致したら読み込んだ結果の StoryElement オブジェクトを返します。
     *          一致しなければ null を返します。
     */
    private StoryElement loadEventAnnounceGroupIfMaches(Element elem) {
        final String[] eventLocalNames = {
            SchemaConstants.LN_START_ENTRY, SchemaConstants.LN_ON_STAGE,
            SchemaConstants.LN_START_MIRROR, SchemaConstants.LN_OPEN_ROLE,
            SchemaConstants.LN_MURDERED, SchemaConstants.LN_START_ASSAULT,
            SchemaConstants.LN_SURVIVOR, SchemaConstants.LN_COUNTING,
            SchemaConstants.LN_SUDDEN_DEATH, SchemaConstants.LN_NO_MURDER,
            SchemaConstants.LN_WIN_VILLAGE, SchemaConstants.LN_WIN_WOLF,
            SchemaConstants.LN_WIN_HAMSTER, SchemaConstants.LN_PLAYER_LIST,
            SchemaConstants.LN_PANIC,
        };
        return loadEventIfMatches(elem, eventLocalNames, EventFamily.ANNOUNCE);
    }
    
    /**
     * 指定された要素が EventOrderGroup に一致するなら読み込みます。
     * @param elem 要素
     * @return 一致したら読み込んだ結果の StoryElement オブジェクトを返します。
     *          一致しなければ null を返します。
     */
    private StoryElement loadEventOrderGroupIfMatches(Element elem) {
        final String[] eventLocalNames = {
            SchemaConstants.LN_ASK_ENTRY, SchemaConstants.LN_ASK_COMMIT,
            SchemaConstants.LN_NO_COMMENT, SchemaConstants.LN_STAY_EPILOGUE,
            SchemaConstants.LN_GAME_OVER,
        };
        return loadEventIfMatches(elem, eventLocalNames, EventFamily.ORDER);        
    }
    
    /**
     * 指定された要素が EventExtraGroup に一致するなら読み込みます。
     * @param elem 要素
     * @return 一致したら読み込んだ結果の StoryElement オブジェクトを返します。
     *          一致しなければ null を返します。
     */
    private StoryElement loadEventExtraGroupIfMathces(Element elem) {
        // TODO: assault は Talk として扱ってやらないとマズいかも。
        // でも発言カウントに入れるわけにはいかず。
        final String[] eventLocalNames = {
            SchemaConstants.LN_JUDGE, SchemaConstants.LN_GUARD,
            SchemaConstants.LN_ASSAULT,
        };
        return loadEventIfMatches(elem, eventLocalNames, EventFamily.EXTRA);        
    }

    /**
     * 指定された要素がイベント要素に一致するなら読み込みます。
     * @param elem 要素
     * @param eventLocalNames 一致するイベントのローカル名の配列
     * @param eventFamily 一致した場合に StoryElement にセットする EventFamily
     * @return 一致したら読み込んだ結果の StoryElement オブジェクトを返します。
     *          一致しなければ null を返します。
     */
    private StoryElement loadEventIfMatches(Element elem, String[] eventLocalNames, EventFamily eventFamily) {
        for (String localName : eventLocalNames) {
            if (DomUtils.isMatchNode(elem, SchemaConstants.NS_ARCHIVE, localName)) {
                StoryEvent storyEvent = new StoryEvent();
                storyEvent.setEventFamily(eventFamily);
                loadMessageLines(elem, storyEvent);
                return storyEvent;
            }
        }
        return null;
    }

    /**
     * talk 要素を読み込んで StoryElement を返します。
     * @param talkElement talk 要素
     * @return
     */
    private StoryElement loadTalk(Element talkElement) {
        Talk talk = new Talk();
        
        // メッセージ
        loadMessageLines(talkElement, talk);
        
        // 発言種別
        TalkType talkType = null;
        String talkTypeString = talkElement.getAttributeNS(null, SchemaConstants.LN_TYPE);
        if (SchemaConstants.VAL_TALK_TYPE_PUBLIC.equals(talkTypeString)) {
            talkType = TalkType.PUBLIC;
        } else if (SchemaConstants.VAL_TALK_TYPE_WOLF.equals(talkTypeString)) {
            talkType = TalkType.WOLF;
        } else if (SchemaConstants.VAL_TALK_TYPE_PRIVATE.equals(talkTypeString)) {
            talkType = TalkType.PRIVATE;
        } else if (SchemaConstants.VAL_TALK_TYPE_GRAVE.equals(talkTypeString)) {
            talkType = TalkType.GRAVE;
        }
        talk.setTalkType(talkType);
        
        // 発言人物
        String avatarId = talkElement.getAttributeNS(null, SchemaConstants.LN_AVATAR_ID);
        Avatar avatar = avatarMap.get(avatarId);
        talk.setSpeaker(avatar);
        
        // TODO: 発言回数
        
        // 発言時刻
        String timeString = talkElement.getAttributeNS(null, SchemaConstants.LN_TIME);
        TimePart timePart = parseTime(timeString);
        talk.setTime(timePart);
        
        return talk;
    }
    
    /**
     * メッセージ行を読み込んで指定された StoryElement オブジェクトにセットします。
     * @param elem メッセージを含む要素
     * @param storyElement ここに読み込んだメッセージがセットされます。
     */
    private void loadMessageLines(Element elem, StoryElement storyElement) {
        List<String> messageLines = new ArrayList<String>();
        Element liElem = DomUtils.searchSiblingElementForward(elem.getFirstChild(),
                SchemaConstants.NS_ARCHIVE, SchemaConstants.LN_LI);
        while (liElem != null) {
            /* TODO: これでは <rawdata> が入ったときにうまくいかない */
            String line = liElem.getTextContent();
            messageLines.add(line);
            
            liElem = DomUtils.searchSiblingElementForward(liElem.getNextSibling(),
                    SchemaConstants.NS_ARCHIVE, SchemaConstants.LN_LI);
        }
        storyElement.setMessageLines(messageLines);
    }

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
}
