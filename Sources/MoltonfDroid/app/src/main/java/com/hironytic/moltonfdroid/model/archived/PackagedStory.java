/*
 * Moltonf
 *
 * Copyright (c) 2011-2013 Hironori Ichimiya <hiron@hironytic.com>
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.hironytic.moltonfdroid.Moltonf;
import com.hironytic.moltonfdroid.MoltonfException;
import com.hironytic.moltonfdroid.model.Avatar;
import com.hironytic.moltonfdroid.model.Story;
import com.hironytic.moltonfdroid.model.StoryPeriod;
import com.hironytic.moltonfdroid.model.VillageState;
import com.hironytic.moltonfdroid.model.basic.BasicAvatar;
import com.hironytic.moltonfdroid.model.basic.BasicStory;
import com.hironytic.moltonfdroid.util.QName;
import com.hironytic.moltonfdroid.util.SmartUtils;
import com.hironytic.moltonfdroid.util.XmlUtils;

/**
 * パッケージ化されたプレイデータアーカイブ XML ファイル群を元にした Story クラス
 * 
 * @see <a href="http://wolfbbs.jp/%B6%A6%C4%CC%A5%A2%A1%BC%A5%AB%A5%A4%A5%D6%B4%F0%C8%D7%C0%B0%C8%F7%B7%D7%B2%E8.html">共通アーカイブ基盤整備計画</a>
 */
public class PackagedStory extends BasicStory implements Story {
    /** データを読み込んでいるなら true */
    private boolean isReady = false;
    
    /** パッケージディレクトリ */
    private File packageDir;

    /** ドキュメントのベース URI */
    private URI baseUri;

    /** 登場人物の識別子から Avatar オブジェクトを得るマップ */
    private Map<String, Avatar> avatarMap;

    /**
     * コンストラクタ
     * @param packageDir プレイデータのパッケージディレクトリ
     */
    public PackagedStory(File packageDir) {
        this.packageDir = packageDir;
    }
    
    /**
     * データの準備ができているかどうかを調べます。
     * @return 準備ができているなら true
     */
    public boolean isReady() {
        return isReady;
    }
    
    /**
     * データの準備ができていなければ準備を行います。
     */
    public void ready() {
        if (isReady) {
            return;
        }
        
        super.ready();
        
        try {
            // village
            File villageFile = new File(packageDir, PackageConstants.FILENAME_VILLAGE);
            
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(true);
            XmlPullParser staxReader = factory.newPullParser();
            Reader fileReader = new BufferedReader(new FileReader(villageFile));
            staxReader.setInput(fileReader);
            try {
                for (int eventType = staxReader.next(); eventType != XmlPullParser.END_DOCUMENT; eventType = staxReader.next()) {
                    if (eventType == XmlPullParser.START_TAG) {
                        QName elemName = new QName(staxReader.getNamespace(), staxReader.getName());
                        if (SchemaConstants.NAME_VILLAGE.contains(elemName)) {
                            loadVillageElement(staxReader, villageFile);
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
                        
            isReady = true;
            
        } catch (XmlPullParserException ex) {
            throw new MoltonfException(ex);
        } catch (FileNotFoundException ex) {
            throw new MoltonfException(ex);
        } catch (IOException ex) {
            throw new MoltonfException(ex);
        }
    }

    /**
     * 登場人物の識別子に対応する Avatar オブジェクトを返します。
     * @param avatarId 識別子
     * @return Avatar オブジェクト。対応するものがなければ null。
     */
    @Override
    public Avatar getAvatar(String avatarId) {
        return avatarMap.get(avatarId);
    }
    
    /**
     * village 要素以下を読み込みます。
     * このメソッドが呼ばれたとき staxReader は village 要素の START_TAG にいることが前提です。
     * @param staxReader XML パーサ
     * @param villageFle 読み込み中のvillage.xmlのファイルパス
     * @throws XmlPullParserException 読み込み中にエラーが発生した場合
     * @throws IOException 読み込み中にエラーが発生した場合
     */
    private void loadVillageElement(XmlPullParser staxReader, File villageFile) throws XmlPullParserException, IOException {
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
                    Moltonf.getInstance().getLogger().warning("graveIconUri is not valid : " + graveIconUriString, ex);
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
                if (SchemaConstants.NAME_AVATAR_LIST.contains(elemName)) {
                    setAvatarList(loadAvatarList(staxReader));
                } else if (SchemaConstants.NAME_PERIOD.contains(elemName)) {
                    StoryPeriod period = PackagedStoryPeriod.loadVillagePeriod(villageFile, staxReader);
                    period.setStory(this);
                    periodList.add(period);
                } else {
                    XmlUtils.skipElement(staxReader);
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
            Moltonf.getInstance().getLogger().warning("invalid village state : <village state=\"" + villageStateString + "\">");
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
                if (SchemaConstants.NAME_AVATAR.contains(elemName)) {
                    avatarList.add(loadAvatar(staxReader));
                } else {
                    XmlUtils.skipElement(staxReader);
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
                    Moltonf.getInstance().getLogger().warning("faceIconUri is not valid : " + faceIconUriString, ex);
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
                XmlUtils.skipElement(staxReader);
            }
        }
        
        avatarMap.put(avatar.getAvatarId(), avatar);
        return avatar;
    }
}
