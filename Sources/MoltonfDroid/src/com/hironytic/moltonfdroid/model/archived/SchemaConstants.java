/*
 * Moltonf
 *
 * Copyright (c) 2010,2011 Hironori Ichimiya <hiron@hironytic.com>
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

import com.hironytic.moltonfdroid.util.QName;

/**
 * 共通アーカイブ基盤用スキーマに関連する定数定義
 */
public class SchemaConstants {

    /** 名前空間接頭辞 xml の名前空間URI */
    public static final String NS_XML = "http://www.w3.org/XML/1998/namespace";
    
    /** 名前空間接頭辞 xml */
    public static final String PREFIX_XML = "xml";

    /** 共通アーカイブ基盤用スキーマの名前空間URI */
    public static final String NS_ARCHIVE = "http://jindolf.sourceforge.jp/xml/ns/401";

    /* 
     * --------------------------------------------------
     * village
     * --------------------------------------------------
     */

    /** village 要素の QName */
    public static final QName NAME_VILLAGE = new QName(NS_ARCHIVE, "village");

    /** xml:base 属性の QName */
    public static final QName NAME_BASE = new QName(NS_XML, "base", PREFIX_XML);

    /** fullName 属性の QName */
    public static final QName NAME_FULL_NAME = new QName("fullName");

    /** state 属性の QName */
    public static final QName NAME_STATE = new QName("state");

    /** 村の進行状態: プロローグ中 */
    public static final String VAL_VILLAGE_STATE_PROLOGUE = "prologue";

    /** 村の進行状態: ゲーム中 */
    public static final String VAL_VILLAGE_STATE_PROGRESS = "progress";

    /** 村の進行状態: エピローグ中 */
    public static final String VAL_VILLAGE_STATE_EPILOGUE = "epilogue";

    /** 村の進行状態: ゲーム終了 */
    public static final String VAL_VILLAGE_STATE_GAMEOVER = "gameover";
    
    /** graveIconURI 属性の QName */
    public static final QName NAME_GRAVE_ICON_URI = new QName("graveIconURI");
    
    /* 
     * --------------------------------------------------
     * avatarList
     * --------------------------------------------------
     */

    /** avatarList 要素の QName */
    public static final QName NAME_AVATAR_LIST = new QName(NS_ARCHIVE, "avatarList");
    
    /* 
     * --------------------------------------------------
     * avatar
     * --------------------------------------------------
     */

    /** avatar 要素の QName */
    public static final QName NAME_AVATAR = new QName(NS_ARCHIVE, "avatar");
    
    /** avatarId 属性の QName */
    public static final QName NAME_AVATAR_ID = new QName("avatarId");
    
//    /** fullName 属性の QName */
//    public static final QName NAME_FULL_NAME = new QName("fullName");     // 既に定義されているので省略
    
    /** shortName 属性の QName */
    public static final QName NAME_SHORT_NAME = new QName("shortName");
    
    /** faceIconURI 属性の QName */
    public static final QName NAME_FACE_ICON_URI = new QName("faceIconURI");
    
    /* 
     * --------------------------------------------------
     * period
     * --------------------------------------------------
     */

    /** period 要素の QName */
    public static final QName NAME_PERIOD = new QName(NS_ARCHIVE, "period");
    
//    /** type 属性の QName */
//    public static final QName NAME_TYPE = new QName("type");  // 別に定義されているので省略
    
    /** ピリオド種別：プロローグ */
    public static final String VAL_PERIOD_TYPE_PROLOGUE = "prologue";
    
    /** ピリオド種別：進行中の日 */
    public static final String VAL_PERIOD_TYPE_PROGRESS = "progress";
    
    /** ピリオド種別：エピローグ */
    public static final String VAL_PERIOD_TYPE_EPILOGUE = "epilogue";

    /** day 属性の QName */
    public static final QName NAME_DAY = new QName("day");
    
    /* 
     * --------------------------------------------------
     * EventAnnounceGroup の要素
     * --------------------------------------------------
     */

    /** startEntry 要素の QName */
    public static final QName NAME_START_ENTRY = new QName(NS_ARCHIVE, "startEntry");
    
    /** onStage 要素の QName */
    public static final QName NAME_ON_STAGE = new QName(NS_ARCHIVE, "onStage");

    /** startMirror 要素の QName */
    public static final QName NAME_START_MIRROR = new QName(NS_ARCHIVE, "startMirror");

    /** openRole 要素の QName */
    public static final QName NAME_OPEN_ROLE = new QName(NS_ARCHIVE, "openRole");

    /** murdered 要素の QName */
    public static final QName NAME_MURDERED = new QName(NS_ARCHIVE, "murdered");

    /** startAssault 要素の QName */
    public static final QName NAME_START_ASSAULT = new QName(NS_ARCHIVE, "startAssault");

    /** survivor 要素の QName */
    public static final QName NAME_SURVIVOR = new QName(NS_ARCHIVE, "survivor");

    /** counting 要素の QName */
    public static final QName NAME_COUNTING = new QName(NS_ARCHIVE, "counting");

    /** suddenDeath 要素の QName */
    public static final QName NAME_SUDDEN_DEATH = new QName(NS_ARCHIVE, "suddenDeath");

    /** noMurder 要素の QName */
    public static final QName NAME_NO_MURDER = new QName(NS_ARCHIVE, "noMurder");

    /** winVillage 要素の QName */
    public static final QName NAME_WIN_VILLAGE = new QName(NS_ARCHIVE, "winVillage");

    /** winWolf 要素の QName */
    public static final QName NAME_WIN_WOLF = new QName(NS_ARCHIVE, "winWolf");

    /** winHamster 要素の QName */
    public static final QName NAME_WIN_HAMSTER = new QName(NS_ARCHIVE, "winHamster");

    /** playerList 要素の QName */
    public static final QName NAME_PLAYER_LIST = new QName(NS_ARCHIVE, "playerList");

    /** panic 要素の QName */
    public static final QName NAME_PANIC = new QName(NS_ARCHIVE, "panic");
    
    /* 
     * --------------------------------------------------
     * EventOrderGroup の要素
     * --------------------------------------------------
     */

    /** askEntry 要素の QName */
    public static final QName NAME_ASK_ENTRY = new QName(NS_ARCHIVE, "askEntry");

    /** askCommit 要素の QName */
    public static final QName NAME_ASK_COMMIT = new QName(NS_ARCHIVE, "askCommit");
    
    /** noComment 要素の QName */
    public static final QName NAME_NO_COMMENT = new QName(NS_ARCHIVE, "noComment");
    
    /** stayEpilogue 要素の QName */
    public static final QName NAME_STAY_EPILOGUE = new QName(NS_ARCHIVE, "stayEpilogue");
    
    /** gameOver 要素の QName */
    public static final QName NAME_GAME_OVER = new QName(NS_ARCHIVE, "gameOver");
    
    /* 
     * --------------------------------------------------
     * EventExtraGroup の要素
     * --------------------------------------------------
     */

    /** judge 要素の QName */
    public static final QName NAME_JUDGE = new QName(NS_ARCHIVE, "judge");
    
    /** guard 要素の QName */
    public static final QName NAME_GUARD = new QName(NS_ARCHIVE, "guard");
    
    
    /* 
     * --------------------------------------------------
     * assault
     * --------------------------------------------------
     */
    
    /** assault 要素の QName */
    public static final QName NAME_ASSAULT = new QName(NS_ARCHIVE, "assault");

    /** byWhom 属性の QName */
    public static final QName NAME_BY_WHOM = new QName("byWhom");
    
    /* 
     * --------------------------------------------------
     * talk
     * --------------------------------------------------
     */
    
    /** talk 要素の QName */
    public static final QName NAME_TALK = new QName(NS_ARCHIVE, "talk");
    
    /** type 属性の QName */
    public static final QName NAME_TYPE = new QName("type");

    /** 発言種別：白発言 */
    public static final String VAL_TALK_TYPE_PUBLIC = "public";
    
    /** 発言種別：赤発言 */
    public static final String VAL_TALK_TYPE_WOLF = "wolf";
    
    /** 発言種別：灰発言 */
    public static final String VAL_TALK_TYPE_PRIVATE = "private";
    
    /** 発言種別：青発言 */
    public static final String VAL_TALK_TYPE_GRAVE = "grave";
    
//    /** avatarId 属性の QName */
//    public static final QName NAME_AVATAR_ID = new QName("avatarId");     // 既に定義されているので省略
    
    /** time 属性の QName */
    public static final QName NAME_TIME = new QName("time");
    
    /* 
     * --------------------------------------------------
     * li
     * --------------------------------------------------
     */

    /** li 要素の QName */
    public static final QName NAME_LI = new QName(NS_ARCHIVE, "li");
    
    /* 
     * --------------------------------------------------
     * rawdata
     * --------------------------------------------------
     */

    /** rawdata 要素の QName */
    public static final QName NAME_RAWDATA = new QName(NS_ARCHIVE, "rawdata");

    /* 
     * --------------------------------------------------
     * playerInfo
     * --------------------------------------------------
     */

    /** playerInfo 要素の QName */
    public static final QName NAME_PLAYER_INFO = new QName(NS_ARCHIVE, "playerInfo");
    
//    /** avatarId 属性の QName */
//    public static final QName NAME_AVATAR_ID = new QName("avatarId");     // 既に定義されているので省略
    
    /** role 要素の QName */
    public static final QName NAME_ROLE = new QName("role");
    
    /** 役職：村人 */
    public static final String VAL_ROLE_INNOCENT = "innocent";
    
    /** 役職：狼 */
    public static final String VAL_ROLE_WOLF = "wolf";
    
    /** 役職：占い師 */
    public static final String VAL_ROLE_SEER = "seer";
    
    /** 役職：霊能者 */
    public static final String VAL_ROLE_SHAMAN = "shaman";
    
    /** 役職：狂人 */
    public static final String VAL_ROLE_MADMAN = "madman";
    
    /** 役職：狩人 */
    public static final String VAL_ROLE_HUNTER = "hunter";
    
    /** 役職：共有者 */
    public static final String VAL_ROLE_FRATER = "frater";
    
    /** 役職：ハムスター人間 */
    public static final String VAL_ROLE_HAMSTER = "hamster";
}
