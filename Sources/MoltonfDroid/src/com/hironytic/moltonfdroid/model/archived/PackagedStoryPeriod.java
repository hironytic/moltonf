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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.hironytic.moltonfdroid.Moltonf;
import com.hironytic.moltonfdroid.MoltonfException;
import com.hironytic.moltonfdroid.model.Avatar;
import com.hironytic.moltonfdroid.model.EventFamily;
import com.hironytic.moltonfdroid.model.Role;
import com.hironytic.moltonfdroid.model.StoryElement;
import com.hironytic.moltonfdroid.model.StoryEvent;
import com.hironytic.moltonfdroid.model.StoryPeriod;
import com.hironytic.moltonfdroid.model.Talk;
import com.hironytic.moltonfdroid.model.TalkType;
import com.hironytic.moltonfdroid.model.basic.BasicStoryEvent;
import com.hironytic.moltonfdroid.model.basic.BasicStoryPeriod;
import com.hironytic.moltonfdroid.model.basic.BasicTalk;
import com.hironytic.moltonfdroid.model.basic.BasicWolfAttackTalk;
import com.hironytic.moltonfdroid.util.QName;
import com.hironytic.moltonfdroid.util.TimePart;
import com.hironytic.moltonfdroid.util.XmlUtils;

/**
 * パッケージ化されたプレイデータアーカイブ XML ファイルで表された
 * 一つ分の period を表す StoryPeriod。
 */
public class PackagedStoryPeriod extends BasicStoryPeriod implements StoryPeriod {

    /** データを読み込んでいるなら true */
    private boolean isReady = false;
    
    /** periodデータのファイル */
    private File periodFile;
    
    public PackagedStoryPeriod(File periodFile) {
        this.periodFile = periodFile;
    }
    
    /**
     * @see com.hironytic.moltonfdroid.model.StoryPeriod#isReady()
     */
    @Override
    public boolean isReady() {
        return isReady;
    }

    /**
     * @see com.hironytic.moltonfdroid.model.StoryPeriod#ready()
     */
    @Override
    public void ready() {
        if (isReady) {
            return;
        }
        
        super.ready();
        
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(true);
            XmlPullParser staxReader = factory.newPullParser();
            Reader fileReader = new BufferedReader(new FileReader(periodFile));
            staxReader.setInput(fileReader);
            try {
                for (int eventType = staxReader.next(); eventType != XmlPullParser.END_DOCUMENT; eventType = staxReader.next()) {
                    if (eventType == XmlPullParser.START_TAG) {
                        QName elemName = new QName(staxReader.getNamespace(), staxReader.getName());
                        if (SchemaConstants.NAME_PERIOD.equals(elemName)) {
                            loadPeriod(staxReader);
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
     * period 要素以下を読み込みます。
     * このメソッドが呼ばれたとき staxReader は period 要素の START_ELEMENT にいることが前提です。
     * @param staxReader XML パーサ
     * @throws XmlPullParserException 読み込み中にエラーが発生した場合
     * @throws IOException 読み込み中にエラーが発生した場合
     */
    private void loadPeriod(XmlPullParser staxReader) throws XmlPullParserException, IOException {
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
        });
        
        // 子ノード
        for (int eventType = staxReader.next(); eventType != XmlPullParser.END_DOCUMENT; eventType = staxReader.next()) {
            if (eventType == XmlPullParser.END_TAG) {
                break;
            } else if (eventType == XmlPullParser.START_TAG) {
                QName elemName = new QName(staxReader.getNamespace(), staxReader.getName());
                if (SchemaConstants.NAME_TALK.equals(elemName)) {
                    elementList.add(loadTalk(staxReader));
                } else if (eventAnnounceGroup.contains(elemName)) {
                    elementList.add(loadStoryEvent(staxReader, EventFamily.ANNOUNCE));
                } else if (eventOrderGroup.contains(elemName)) {
                    elementList.add(loadStoryEvent(staxReader, EventFamily.ORDER));
                } else if (eventExtraGroup.contains(elemName)) {
                    elementList.add(loadStoryEvent(staxReader, EventFamily.EXTRA));
                } else if (SchemaConstants.NAME_ASSAULT.equals(elemName)) {
                    elementList.add(loadAssault(staxReader));
                } else {
                    XmlUtils.skipElement(staxReader);
                }
            }
        }
        
        setStoryElements(elementList);
    }
    
    /**
     * talk 要素以下を読み込みます。
     * このメソッドが呼ばれたとき staxReader は talk 要素の START_ELEMENT にいることが前提です。
     * @param staxReader XML パーサ
     * @return 読み込んだ結果の Talk
     * @throws XmlPullParserException 読み込み中にエラーが発生した場合
     * @throws IOException 読み込み中にエラーが発生した場合
     */
    private Talk loadTalk(XmlPullParser staxReader) throws XmlPullParserException, IOException {
        Talk talk = new BasicTalk();
        talk.setStoryPeriod(this);
        
        // 属性
        for (int ix = 0; ix < staxReader.getAttributeCount(); ++ix) {
            QName attrName = new QName(staxReader.getAttributeNamespace(ix), staxReader.getAttributeName(ix));
            if (SchemaConstants.NAME_TYPE.equals(attrName)) {
                String talkTypeString = staxReader.getAttributeValue(ix);
                talk.setTalkType(toTalkType(talkTypeString));
            } else if (SchemaConstants.NAME_AVATAR_ID.equals(attrName)) {
                // ここに来る時点で Story の avatarList が作成済みであることを想定
                String avatarId = staxReader.getAttributeValue(ix);
                Avatar avatar = getStory().getAvatar(avatarId);
                talk.setSpeaker(avatar);
            } else if (SchemaConstants.NAME_TIME.equals(attrName)) {
                String timeString = staxReader.getAttributeValue(ix);
                TimePart timePart = XmlUtils.parseTime(timeString);
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
                    XmlUtils.skipElement(staxReader);
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
     * @param eventFamily イベントの種別
     * @return 読み込んだ結果の StoryEvent
     * @throws XmlPullParserException 読み込み中にエラーが発生した場合
     * @throws IOException 読み込み中にエラーが発生した場合
     */
    private StoryEvent loadStoryEvent(XmlPullParser staxReader, EventFamily eventFamily) throws XmlPullParserException, IOException {
        StoryEvent storyEvent = new BasicStoryEvent();
        storyEvent.setStoryPeriod(this);
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
                    XmlUtils.skipElement(staxReader);
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
            // ここに来る時点で Story の avatarList が作成済みであることを想定
            Avatar avatar = getStory().getAvatar(avatarId);
            if (avatar != null) {
                avatar.setRole(toRole(roleString));
            }
        }
        
        XmlUtils.skipElement(staxReader);
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
     * @return 読み込んだ結果の WolfAttackTalk
     * @throws XmlPullParserException 読み込み中にエラーが発生した場合
     * @throws IOException 読み込み中にエラーが発生した場合
     */
    private Talk loadAssault(XmlPullParser staxReader) throws XmlPullParserException, IOException {
        Talk talk = new BasicWolfAttackTalk();
        talk.setStoryPeriod(this);
        talk.setTalkType(TalkType.WOLF);
        
        // 属性
        for (int ix = 0; ix < staxReader.getAttributeCount(); ++ix) {
            QName attrName = new QName(staxReader.getAttributeNamespace(ix), staxReader.getAttributeName(ix));
            if (SchemaConstants.NAME_BY_WHOM.equals(attrName)) {
                // ここに来る時点で Story の avatarList が作成済みであることを想定
                String avatarId = staxReader.getAttributeValue(ix);
                Avatar avatar = getStory().getAvatar(avatarId);
                talk.setSpeaker(avatar);
            } else if (SchemaConstants.NAME_TIME.equals(attrName)) {
                String timeString = staxReader.getAttributeValue(ix);
                TimePart timePart = XmlUtils.parseTime(timeString);
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
                    XmlUtils.skipElement(staxReader);
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
                    XmlUtils.skipElement(staxReader);
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
                XmlUtils.skipElement(staxReader);
            } else if (eventType == XmlPullParser.TEXT) {
                String text = staxReader.getText();
                // 全角チルダ(U+FF5E, FULLWIDTH TILDE)と波ダッシュ(U+301C, WAVE DASH)の変換。やるならここ
                //text = text.replace('\uff5e', '\u301c');
                //text = text.replace('\u301c', '\uff5e');
                buf.append(text);
            }
        }
    }
    
}
