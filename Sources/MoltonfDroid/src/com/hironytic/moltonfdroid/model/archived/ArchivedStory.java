/*
 * Moltonf
 *
 * Copyright (c) 2011 Hironori Ichimiya <hiron@hironytic.com>
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

package com.hironytic.moltonfdroid.model.archived;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.hironytic.moltonfdroid.Moltonf;
import com.hironytic.moltonfdroid.MoltonfException;
import com.hironytic.moltonfdroid.model.Avatar;
import com.hironytic.moltonfdroid.model.EventFamily;
import com.hironytic.moltonfdroid.model.PeriodType;
import com.hironytic.moltonfdroid.model.Role;
import com.hironytic.moltonfdroid.model.Story;
import com.hironytic.moltonfdroid.model.StoryElement;
import com.hironytic.moltonfdroid.model.StoryEvent;
import com.hironytic.moltonfdroid.model.StoryPeriod;
import com.hironytic.moltonfdroid.model.Talk;
import com.hironytic.moltonfdroid.model.TalkType;
import com.hironytic.moltonfdroid.model.VillageState;
import com.hironytic.moltonfdroid.model.basic.BasicAvatar;
import com.hironytic.moltonfdroid.model.basic.BasicStory;
import com.hironytic.moltonfdroid.model.basic.BasicStoryEvent;
import com.hironytic.moltonfdroid.model.basic.BasicStoryPeriod;
import com.hironytic.moltonfdroid.model.basic.BasicTalk;
import com.hironytic.moltonfdroid.model.basic.BasicWolfAttackTalk;
import com.hironytic.moltonfdroid.util.QName;
import com.hironytic.moltonfdroid.util.SmartUtils;
import com.hironytic.moltonfdroid.util.TimePart;

/**
 * プレイデータアーカイブ XML ファイルを元にした Story クラス
 * 
 * @see <a href="http://wolfbbs.jp/%B6%A6%C4%CC%A5%A2%A1%BC%A5%AB%A5%A4%A5%D6%B4%F0%C8%D7%C0%B0%C8%F7%B7%D7%B2%E8.html">共通アーカイブ基盤整備計画</a>
 */
public class ArchivedStory extends BasicStory implements Story {

    /** ドキュメントのベース URI */
    private URI baseUri;

    /** 登場人物の識別子から Avatar オブジェクトを得るマップ */
    private Map<String, Avatar> avatarMap;
    
    /**
     * コンストラクタ
     * @param archiveFile プレイデータアーカイブファイル
     */
    public ArchivedStory(File archiveFile) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(true);
            XmlPullParser staxReader = factory.newPullParser();
            Reader fileReader = new BufferedReader(new FileReader(archiveFile));
            staxReader.setInput(fileReader);
            try {
                for (int eventType = staxReader.next(); eventType != XmlPullParser.END_DOCUMENT; eventType = staxReader.next()) {
                    if (eventType == XmlPullParser.START_TAG) {
                        QName elemName = new QName(staxReader.getNamespace(), staxReader.getName());
                        if (SchemaConstants.NAME_VILLAGE.equals(elemName)) {
                            loadVillageElement(staxReader);
                        } else {
                            throw new MoltonfException("Not a bbs play-data archive.");
                        }
                    }
                }
            } finally {
                try {
                    fileReader.close();
                } catch (IOException ex) {
                    // ignore
                }
            }
        } catch (XmlPullParserException ex) {
            throw new MoltonfException(ex);
        } catch (FileNotFoundException ex) {
            throw new MoltonfException(ex);
        } catch (IOException ex) {
            throw new MoltonfException(ex);
        }
    }
    
    /**
     * village 要素以下を読み込みます。
     * このメソッドが呼ばれたとき staxReader は village 要素の START_TAG にいることが前提です。
     * @param staxReader XML パーサ
     * @throws XmlPullParserException 読み込み中にエラーが発生した場合
     * @throws IOException 読み込み中にエラーが発生した場合
     */
    private void loadVillageElement(XmlPullParser staxReader) throws XmlPullParserException, IOException {
        // 属性
        for (int ix = 0; ix < staxReader.getAttributeCount(); ++ix) {
            QName attrName = new QName(staxReader.getAttributeNamespace(ix), staxReader.getAttributeName(ix));
            if (SchemaConstants.NAME_BASE.equals(attrName)) {
                String baseUriString = staxReader.getAttributeValue(ix);
                if (!SmartUtils.isStringEmpty(baseUriString)) {
                    try {
                        baseUri = new URI(baseUriString);
                    } catch (URISyntaxException ex) {
                        baseUri = null;
                    }
                }
            } else if (SchemaConstants.NAME_STATE.equals(attrName)) {
                String villageStateString = staxReader.getAttributeValue(ix);
                setVillageState(toVillageState(villageStateString));
            } else if (SchemaConstants.NAME_FULL_NAME.equals(attrName)) {
                String villageFullName = staxReader.getAttributeValue(ix);
                setVillageFullName(villageFullName);
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
                setGraveIconUri(graveIconUri);
            }
        }
        
        List<StoryPeriod> periodList = new ArrayList<StoryPeriod>();

        // 子ノード
        for (int eventType = staxReader.next(); eventType != XmlPullParser.END_DOCUMENT; eventType = staxReader.next()) {
            if (eventType == XmlPullParser.END_TAG) {
                break;
            } else if (eventType == XmlPullParser.START_TAG) {
                QName elemName = new QName(staxReader.getNamespace(), staxReader.getName());
                if (SchemaConstants.NAME_AVATAR_LIST.equals(elemName)) {
                    setAvatarList(loadAvatarList(staxReader));
                } else if (SchemaConstants.NAME_PERIOD.equals(elemName)) {
                    periodList.add(loadPeriod(staxReader));
                } else {
                    skipElement(staxReader);
                }
            }
        }
        
        setPeriods(periodList);
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
     * @param staxReader XML パーサ
     * @return 読み込んだ結果の Avatar のリスト
     * @throws XmlPullParserException 読み込み中にエラーが発生した場合
     * @throws IOException 読み込み中にエラーが発生した場合
     */
    private List<Avatar> loadAvatarList(XmlPullParser staxReader) throws XmlPullParserException, IOException {
        avatarMap = new HashMap<String, Avatar>();
        List<Avatar> avatarList = new ArrayList<Avatar>();

        // 子ノード
        for (int eventType = staxReader.next(); eventType != XmlPullParser.END_DOCUMENT; eventType = staxReader.next()) {
            if (eventType == XmlPullParser.END_TAG) {
                break;
            } else if (eventType == XmlPullParser.START_TAG) {
                QName elemName = new QName(staxReader.getNamespace(), staxReader.getName());
                if (SchemaConstants.NAME_AVATAR.equals(elemName)) {
                    avatarList.add(loadAvatar(staxReader));
                } else {
                    skipElement(staxReader);
                }
            }
        }
        return avatarList;
    }

    /**
     * avatar 要素以下を読み込みます。
     * このメソッドが呼ばれたとき staxReader は avatar 要素の START_ELEMENT にいることが前提です。
     * @param staxReader XML パーサ
     * @return 読み込んだ結果の Avatar
     * @throws XmlPullParserException 読み込み中にエラーが発生した場合
     * @throws IOException 読み込み中にエラーが発生した場合
     */
    private Avatar loadAvatar(XmlPullParser staxReader) throws XmlPullParserException, IOException {
        Avatar avatar = new BasicAvatar();
        avatar.setStory(this);
        
        // 属性
        for (int ix = 0; ix < staxReader.getAttributeCount(); ++ix) {
            QName attrName = new QName(staxReader.getAttributeNamespace(ix), staxReader.getAttributeName(ix));
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
        for (int eventType = staxReader.next(); eventType != XmlPullParser.END_DOCUMENT; eventType = staxReader.next()) {
            if (eventType == XmlPullParser.END_TAG) {
                break;
            } else if (eventType == XmlPullParser.START_TAG) {
                skipElement(staxReader);
            }
        }
        
        avatarMap.put(avatar.getAvatarId(), avatar);
        return avatar;
    }

    /**
     * period 要素以下を読み込みます。
     * このメソッドが呼ばれたとき staxReader は period 要素の START_ELEMENT にいることが前提です。
     * @param staxReader XML パーサ
     * @return 読み込んだ結果の StoryPeriod
     * @throws XmlPullParserException 読み込み中にエラーが発生した場合
     * @throws IOException 読み込み中にエラーが発生した場合
     */
    private StoryPeriod loadPeriod(XmlPullParser staxReader) throws XmlPullParserException, IOException {
        StoryPeriod period = new BasicStoryPeriod();
        period.setStory(this);
        List<StoryElement> elementList = new ArrayList<StoryElement>();
        
        // 属性
        for (int ix = 0; ix < staxReader.getAttributeCount(); ++ix) {
            QName attrName = new QName(staxReader.getAttributeNamespace(ix), staxReader.getAttributeName(ix));
            if (SchemaConstants.NAME_TYPE.equals(attrName)) {
                String periodTypeString = staxReader.getAttributeValue(ix);
                period.setPeriodType(toPeriodType(periodTypeString));
            } else if (SchemaConstants.NAME_DAY.equals(attrName)) {
                String dayString = staxReader.getAttributeValue(ix);
                try {
                    int day = Integer.parseInt(dayString);
                    period.setPeriodNumber(day);
                } catch (NumberFormatException ex) {
                    Moltonf.getLogger().warning("invalid period day: " + dayString);
                }
            }
        }
        
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
        });
        
        // 子ノード
        for (int eventType = staxReader.next(); eventType != XmlPullParser.END_DOCUMENT; eventType = staxReader.next()) {
            if (eventType == XmlPullParser.END_TAG) {
                break;
            } else if (eventType == XmlPullParser.START_TAG) {
                QName elemName = new QName(staxReader.getNamespace(), staxReader.getName());
                if (SchemaConstants.NAME_TALK.equals(elemName)) {
                    elementList.add(loadTalk(staxReader, period));
                } else if (eventAnnounceGroup.contains(elemName)) {
                    elementList.add(loadStoryEvent(staxReader, period, EventFamily.ANNOUNCE));
                } else if (eventOrderGroup.contains(elemName)) {
                    elementList.add(loadStoryEvent(staxReader, period, EventFamily.ORDER));
                } else if (eventExtraGroup.contains(elemName)) {
                    elementList.add(loadStoryEvent(staxReader, period, EventFamily.EXTRA));
                } else if (SchemaConstants.NAME_ASSAULT.equals(elemName)) {
                    elementList.add(loadAssault(staxReader, period));
                } else {
                    skipElement(staxReader);
                }
            }
        }
        
        period.setStoryElements(elementList);
        return period;
    }
    
    /**
     * ピリオド種別の文字列を PeriodType に変換します。
     * @param periodTypeString ピリオド種別の文字列 (type属性の値)
     * @return PeriodType。該当するものがなければ null。
     */
    private PeriodType toPeriodType(String periodTypeString) {
        PeriodType periodType = null;
        if (SchemaConstants.VAL_PERIOD_TYPE_PROLOGUE.equals(periodTypeString)) {
            periodType = PeriodType.PROLOGUE;
        } else if (SchemaConstants.VAL_PERIOD_TYPE_PROGRESS.equals(periodTypeString)) {
            periodType = PeriodType.PROGRESS;
        } else if (SchemaConstants.VAL_PERIOD_TYPE_EPILOGUE.equals(periodTypeString)) {
            periodType = PeriodType.EPILOGUE;
        } else {
            Moltonf.getLogger().warning("invalid village state : <period type=\"" + periodTypeString + "\">");
        }
        return periodType;
    }
    
    /**
     * talk 要素以下を読み込みます。
     * このメソッドが呼ばれたとき staxReader は talk 要素の START_ELEMENT にいることが前提です。
     * @param staxReader XML パーサ
     * @param period 読み込んだ Talk が所属する StoryPeriod。
     * @return 読み込んだ結果の Talk
     * @throws XmlPullParserException 読み込み中にエラーが発生した場合
     * @throws IOException 読み込み中にエラーが発生した場合
     */
    private Talk loadTalk(XmlPullParser staxReader, StoryPeriod period) throws XmlPullParserException, IOException {
        Talk talk = new BasicTalk();
        talk.setStoryPeriod(period);
        
        // 属性
        for (int ix = 0; ix < staxReader.getAttributeCount(); ++ix) {
            QName attrName = new QName(staxReader.getAttributeNamespace(ix), staxReader.getAttributeName(ix));
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
        for (int eventType = staxReader.next(); eventType != XmlPullParser.END_DOCUMENT; eventType = staxReader.next()) {
            if (eventType == XmlPullParser.END_TAG) {
                break;
            } else if (eventType == XmlPullParser.START_TAG) {
                QName elemName = new QName(staxReader.getNamespace(), staxReader.getName());
                if (SchemaConstants.NAME_LI.equals(elemName)) {
                    messageLines.add(loadLi(staxReader));
                } else {
                    skipElement(staxReader);
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
     * @param staxReader XML パーサ
     * @param period 読み込んだ StoryEvent が所属する StoryPeriod。
     * @param eventFamily イベントの種別
     * @return 読み込んだ結果の StoryEvent
     * @throws XmlPullParserException 読み込み中にエラーが発生した場合
     * @throws IOException 読み込み中にエラーが発生した場合
     */
    private StoryEvent loadStoryEvent(XmlPullParser staxReader, StoryPeriod period, EventFamily eventFamily) throws XmlPullParserException, IOException {
        StoryEvent storyEvent = new BasicStoryEvent();
        storyEvent.setStoryPeriod(period);
        storyEvent.setEventFamily(eventFamily);

        List<String> messageLines = new ArrayList<String>();
        QName storyEventElemName = new QName(staxReader.getNamespace(), staxReader.getName());
        
        // 子ノード
        for (int eventType = staxReader.next(); eventType != XmlPullParser.END_DOCUMENT; eventType = staxReader.next()) {
            if (eventType == XmlPullParser.END_TAG) {
                break;
            } else if (eventType == XmlPullParser.START_TAG) {
                QName elemName = new QName(staxReader.getNamespace(), staxReader.getName());
                if (SchemaConstants.NAME_LI.equals(elemName)) {
                    messageLines.add(loadLi(staxReader));
                } else if (SchemaConstants.NAME_PLAYER_LIST.equals(storyEventElemName) &&
                        SchemaConstants.NAME_PLAYER_INFO.equals(elemName)) {
                    loadPlayerInfo(staxReader);
                } else {
                    skipElement(staxReader);
                }
            }
        }
        
        storyEvent.setMessageLines(messageLines);
        return storyEvent;
    }
    
    /**
     * playerInfo 要素以下を読み込みます。
     * このメソッドが呼ばれたとき staxReader は playerInfo 要素の START_ELEMENT にいることが前提です。
     * @param staxReader XML パーサ
     * @throws XmlPullParserException 読み込み中にエラーが発生した場合
     * @throws IOException 読み込み中にエラーが発生した場合
     */
    private void loadPlayerInfo(XmlPullParser staxReader) throws XmlPullParserException, IOException {
        // playerInfo のうち、role 属性で示される役職を読み込んで
        // avatarId 属性で示されるアバターの Avatar オブジェクトに役職をセットする
        
        String avatarId = null;
        String roleString = null;
        
        // 属性
        for (int ix = 0; ix < staxReader.getAttributeCount(); ++ix) {
            QName attrName = new QName(staxReader.getAttributeNamespace(ix), staxReader.getAttributeName(ix));
            if (SchemaConstants.NAME_AVATAR_ID.equals(attrName)) {
                avatarId = staxReader.getAttributeValue(ix);
            } else if (SchemaConstants.NAME_ROLE.equals(attrName)) {
                roleString = staxReader.getAttributeValue(ix);
            }
            if (avatarId != null && roleString != null) {
                break;
            }
        }
        
        if (avatarId != null && roleString != null) {
            // スキーマとして avatarList が先に登場することが保証されているので
            // 正しいデータならこの時点で avatarMap は作成済み。
            Avatar avatar = avatarMap.get(avatarId);
            if (avatar != null) {
                avatar.setRole(toRole(roleString));
            }
        }
        
        skipElement(staxReader);
    }
    
    /**
     * 役職の値文字列を Role に変換します。
     * @param roleString 役職の値文字列 (role 属性の値)
     * @return Role。該当するものがなければ null。
     */
    private Role toRole(String roleString) {
        Role role = null;
        if (SchemaConstants.VAL_ROLE_INNOCENT.equals(roleString)) {
            role = Role.INNOCENT;
        } else if (SchemaConstants.VAL_ROLE_WOLF.equals(roleString)) {
            role = Role.WOLF;
        } else if (SchemaConstants.VAL_ROLE_SEER.equals(roleString)) {
            role = Role.SEER;
        } else if (SchemaConstants.VAL_ROLE_SHAMAN.equals(roleString)) {
            role = Role.SHAMAN;
        } else if (SchemaConstants.VAL_ROLE_MADMAN.equals(roleString)) {
            role = Role.MADMAN;
        } else if (SchemaConstants.VAL_ROLE_HUNTER.equals(roleString)) {
            role = Role.HUNTER;
        } else if (SchemaConstants.VAL_ROLE_FRATER.equals(roleString)) {
            role = Role.FRATER;
        } else if (SchemaConstants.VAL_ROLE_HAMSTER.equals(roleString)) {
            role = Role.HAMSTER;
        } else {
            Moltonf.getLogger().warning("invalid role : role=\"" + roleString + "\"");
        }
        return role;
    }
    
    /**
     * assault 要素以下を読み込みます。
     * このメソッドが呼ばれたとき staxReader は assault 要素の START_ELEMENT にいることが前提です。
     * @param staxReader XML パーサ
     * @param period 読み込んだ WolfAttackTalk が所属する StoryPeriod。
     * @return 読み込んだ結果の WolfAttackTalk
     * @throws XmlPullParserException 読み込み中にエラーが発生した場合
     * @throws IOException 読み込み中にエラーが発生した場合
     */
    private Talk loadAssault(XmlPullParser staxReader, StoryPeriod period) throws XmlPullParserException, IOException {
        Talk talk = new BasicWolfAttackTalk();
        talk.setStoryPeriod(period);
        talk.setTalkType(TalkType.WOLF);
        
        // 属性
        for (int ix = 0; ix < staxReader.getAttributeCount(); ++ix) {
            QName attrName = new QName(staxReader.getAttributeNamespace(ix), staxReader.getAttributeName(ix));
            if (SchemaConstants.NAME_BY_WHOM.equals(attrName)) {
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
        for (int eventType = staxReader.next(); eventType != XmlPullParser.END_DOCUMENT; eventType = staxReader.next()) {
            if (eventType == XmlPullParser.END_TAG) {
                break;
            } else if (eventType == XmlPullParser.START_TAG) {
                QName elemName = new QName(staxReader.getNamespace(), staxReader.getName());
                if (SchemaConstants.NAME_LI.equals(elemName)) {
                    messageLines.add(loadLi(staxReader));
                } else {
                    skipElement(staxReader);
                }
            }
        }
        
        talk.setMessageLines(messageLines);
        return talk;
    }
    
    /**
     * li 要素以下を読み込みます。
     * このメソッドが呼ばれたとき staxReader は li 要素の START_ELEMENT にいることが前提です。
     * @param staxReader XML パーサ
     * @return 読み込んだ結果の 1 行分の文字列
     * @throws XmlPullParserException 読み込み中にエラーが発生した場合
     * @throws IOException 読み込み中にエラーが発生した場合
     */
    private String loadLi(XmlPullParser staxReader) throws XmlPullParserException, IOException {
        StringBuilder buf = new StringBuilder();
        
        // 子ノード
        for (int eventType = staxReader.next(); eventType != XmlPullParser.END_DOCUMENT; eventType = staxReader.next()) {
            if (eventType == XmlPullParser.END_TAG) {
                break;
            } else if (eventType == XmlPullParser.START_TAG) {
                QName elemName = new QName(staxReader.getNamespace(), staxReader.getName());
                if (SchemaConstants.NAME_RAWDATA.equals(elemName)) {
                    loadRawdata(staxReader, buf);
                } else {
                    skipElement(staxReader);
                }
            } else if (eventType == XmlPullParser.TEXT) {
                String text = staxReader.getText();
                // 全角チルダ(U+FF5E, FULLWIDTH TILDE)と波ダッシュ(U+301C, WAVE DASH)の変換。やるならここ
                //text = text.replace('\uff5e', '\u301c');
                //text = text.replace('\u301c', '\uff5e');
                buf.append(text);
            }
        }
        
        return buf.toString();
    }

    /**
     * rawdata要素以下を読み込んで、引数の StringBuilder に追加します。
     * @param staxReader XML パーサ
     * @param buf 結果の文字列をここに追加します。
     * @throws XmlPullParserException 読み込み中にエラーが発生した場合
     * @throws IOException 読み込み中にエラーが発生した場合
     */
    private void loadRawdata(XmlPullParser staxReader, StringBuilder buf) throws XmlPullParserException, IOException {
        
        // 子ノード
        for (int eventType = staxReader.next(); eventType != XmlPullParser.END_DOCUMENT; eventType = staxReader.next()) {
            if (eventType == XmlPullParser.END_TAG) {
                break;
            } else if (eventType == XmlPullParser.START_TAG) {
                skipElement(staxReader);
            } else if (eventType == XmlPullParser.TEXT) {
                String text = staxReader.getText();
                // 全角チルダ(U+FF5E, FULLWIDTH TILDE)と波ダッシュ(U+301C, WAVE DASH)の変換。やるならここ
                //text = text.replace('\uff5e', '\u301c');
                //text = text.replace('\u301c', '\uff5e');
                buf.append(text);
            }
        }
    }
    
    /**
     * 時刻文字列を解析します。
     * @param timeString 時刻文字列
     * @return 解析結果を格納した TimePart オブジェクト
     */
    private TimePart parseTime(String timeString) {
// DatatypeFactory は Level-8 の API なので使えない。
//        if (datatypeFactory == null) {
//            try {
//                datatypeFactory = DatatypeFactory.newInstance();
//            } catch (DatatypeConfigurationException ex) {
//                throw new MoltonfException(ex);
//            }
//        }
//        
//        XMLGregorianCalendar xmlCalendar;
//        try {
//            xmlCalendar = datatypeFactory.newXMLGregorianCalendar(timeString);
//        } catch (IllegalArgumentException ex) {
//            return null;
//        }
//        
//        int hour = xmlCalendar.getHour();
//        if (hour == DatatypeConstants.FIELD_UNDEFINED) {
//            hour = 0;
//        }
//        int minute = xmlCalendar.getMinute();
//        if (minute == DatatypeConstants.FIELD_UNDEFINED) {
//            minute = 0;
//        }
//        int second = xmlCalendar.getSecond();
//        if (second == DatatypeConstants.FIELD_UNDEFINED) {
//            second = 0;
//        }
//        int millisecond = xmlCalendar.getMillisecond();
//        if (millisecond == DatatypeConstants.FIELD_UNDEFINED) {
//            millisecond = 0;
//        }
//        return new TimePart(hour, minute, second, millisecond);
        
        // ある程度適当に解析
        // hh ':' mm ':' ss ('.' s+)? (zzzzzz)?
        //        -- see http://www.w3.org/TR/xmlschema-2/#dateTime
        if (timeString.length() < 8) {
            return null;
        }
        try {
            int hour = Integer.parseInt(timeString.substring(0, 2));
            int minute = Integer.parseInt(timeString.substring(3, 5));
            int second = Integer.parseInt(timeString.substring(6, 8));
            int millisecond = 0;
            if (timeString.length() > 8 && timeString.charAt(8) == '.') {
                int ix = 9;
                for (int factor = 1000; factor > 0; factor /= 10) {
                    if (timeString.length() < ix) {
                        break;
                    }
                    char ch = timeString.charAt(ix);
                    if (ch < '0' || ch > '9') {
                        break;
                    }
                    millisecond += (int)(ch - '0') * factor;
                }
            }
            return new TimePart(hour, minute, second, millisecond);
        } catch (NumberFormatException ex) {
            return null;
        }
    }    

    /**
     * 現在の要素以下をスキップします
     * @throws XmlPullParserException 読み込み中にエラーが発生した場合
     * @throws IOException 読み込み中にエラーが発生した場合
     */
    private void skipElement(XmlPullParser staxReader) throws XmlPullParserException, IOException {
        for (int eventType = staxReader.next(); eventType != XmlPullParser.END_DOCUMENT; eventType = staxReader.next()) {
            if (eventType == XmlPullParser.END_TAG) {
                break;
            } else if (eventType == XmlPullParser.START_TAG) {
                skipElement(staxReader);
            }
        }
    }
}
