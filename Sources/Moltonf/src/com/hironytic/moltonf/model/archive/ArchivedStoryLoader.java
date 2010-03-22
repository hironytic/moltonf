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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.hironytic.moltonf.MoltonfException;
import com.hironytic.moltonf.model.Avatar;
import com.hironytic.moltonf.model.Story;
import com.hironytic.moltonf.model.VillageState;
import com.hironytic.moltonf.model.basic.BasicAvatar;
import com.hironytic.moltonf.model.basic.BasicStory;
import com.hironytic.moltonf.util.DomUtils;

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
    private BasicStory story;
    
    /** ドキュメントのベース URI */
    private URI baseUri;
    
    /** 登場人物の識別子から Avatar オブジェクトを得るマップ */
    private Map<String, Avatar> avatarMap;
    
    /**
     * コンストラクタ
     * 外部からはインスタンスを生成できません。
     * static メソッド load() を使ってください。
     */
    private ArchivedStoryLoader() {
    }
    
    /**
     * 実際に読み込みを行うメイン処理です。
     * @throws MoltonfException 読み込みに失敗した場合
     */
    private void doLoad() throws MoltonfException {
        story = new BasicStory();
        
        // ドキュメントのベース URI
        loadBaseUri();
        
        // 村の情報
        loadVillageState();
        loadVillageFullName();
        
        // 登場人物
        loadAvatarList();
        
        // 日
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
    
    private void loadVillageFullName() {
        Element villageElem = archiveDoc.getDocumentElement();
        String fullName = villageElem.getAttributeNS(null, SchemaConstants.LN_FORMAL_NAME);
        story.setVillageFullName(fullName);
    }
    
    /**
     * 登場人物のリストを読み込みます。
     */
    private void loadAvatarList() {
        Element villageElem = archiveDoc.getDocumentElement();
        
        avatarMap = new HashMap<String, Avatar>();
        List<Avatar> list = new ArrayList<Avatar>();
        Node avatarListElem = DomUtils.searchSiblingForward(villageElem.getFirstChild(),
                SchemaConstants.NS_TNS, SchemaConstants.LN_AVATAR_LIST);
        if (avatarListElem != null) {
            Element avatarElem = (Element)DomUtils.searchSiblingForward(avatarListElem.getFirstChild(),
                    SchemaConstants.NS_TNS, SchemaConstants.LN_AVATAR);
            while (avatarElem != null) {
                BasicAvatar avatar = new BasicAvatar();
                String avatarId =  avatarElem.getAttributeNS(null, SchemaConstants.LN_AVATAR_ID);
                avatar.setAvatarId(avatarId);
                avatar.setFullName(avatarElem.getAttributeNS(null, SchemaConstants.LN_FULL_NAME));
                avatar.setShortName(avatarElem.getAttributeNS(null, SchemaConstants.LN_SHORT_NAME));
                
                /* TODO; faceIconURI をベース URI に連結したものを URI として格納 */
                /* TODO: 短縮名をどうにかしてセット */
                
                list.add(avatar);                
                avatarMap.put(avatarId, avatar);

                avatarElem = (Element)DomUtils.searchSiblingForward(avatarElem.getNextSibling(),
                        SchemaConstants.NS_TNS, SchemaConstants.LN_AVATAR);
            }
        }
        story.setAvatarList(list);
    }
}
