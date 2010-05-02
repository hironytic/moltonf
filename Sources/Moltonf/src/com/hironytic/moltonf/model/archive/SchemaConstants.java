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

/**
 * 共通アーカイブ基盤用スキーマに関連する定数定義
 */
public class SchemaConstants {

    /** 名前空間接頭辞 xml の名前空間URI */
    public static final String NS_XML = "http://www.w3.org/XML/1998/namespace";

    /** 共通アーカイブ基盤用スキーマの名前空間URI */
    public static final String NS_ARCHIVE = "http://jindolf.sourceforge.jp/xml/ns/401";

    /* 
     * --------------------------------------------------
     * village
     * --------------------------------------------------
     */
    
    /** village 要素のローカル名 */
    public static final String LN_VILLAGE = "village";
    
    /** xml:base 属性のローカル名 */
    public static final String LN_BASE = "base";

    /** fullName 属性のローカル名 */
    public static final String LN_FULL_NAME = "fullName";

    /** state 属性のローカル名 */
    public static final String LN_STATE = "state";

    /** 村の進行状態: プロローグ中 */
    public static final String VAL_VILLAGE_STATE_PROLOGUE = "prologue";

    /** 村の進行状態: ゲーム中 */
    public static final String VAL_VILLAGE_STATE_PROGRESS = "progress";

    /** 村の進行状態: エピローグ中 */
    public static final String VAL_VILLAGE_STATE_EPILOGUE = "epilogue";

    /** 村の進行状態: ゲーム終了 */
    public static final String VAL_VILLAGE_STATE_GAMEOVER = "gameover";
    
    /** graveIconURI 属性のローカル名 */
    public static final String LN_GRAVE_ICON_URI = "graveIconURI";

    /* 
     * --------------------------------------------------
     * avatarList
     * --------------------------------------------------
     */
    
    /** avatarList 要素のローカル名 */
    public static final String LN_AVATAR_LIST = "avatarList";

    /* 
     * --------------------------------------------------
     * avatar
     * --------------------------------------------------
     */
    
    /** avatar 要素のローカル名 */
    public static final String LN_AVATAR = "avatar";
    
    /** avatarId 属性のローカル名 */
    public static final String LN_AVATAR_ID = "avatarId";
    
    /** fullName 属性のローカル名 */
    //public static final String LN_FULL_NAME = "fullName";     // 既に定義されているので省略
    
    /** shortName 属性のローカル名 */
    public static final String LN_SHORT_NAME = "shortName";
    
    /** faceIconURI 属性のローカル名 */
    public static final String LN_FACE_ICON_URI = "faceIconURI";
    
    /* 
     * --------------------------------------------------
     * period
     * --------------------------------------------------
     */
    
    /** period 要素のローカル名 */
    public static final String LN_PERIOD = "period";
    
    /* 
     * --------------------------------------------------
     * EventAnnounceGroup の要素
     * --------------------------------------------------
     */
    
    /** startEntry 要素のローカル名 */
    public static final String LN_START_ENTRY = "startEntry";
    
    /** onStage 要素のローカル名 */
    public static final String LN_ON_STAGE = "onStage";

    /** startMirror 要素のローカル名 */
    public static final String LN_START_MIRROR = "startMirror";

    /** openRole 要素のローカル名 */
    public static final String LN_OPEN_ROLE = "openRole";

    /** murdered 要素のローカル名 */
    public static final String LN_MURDERED = "murdered";

    /** startAssault 要素のローカル名 */
    public static final String LN_START_ASSAULT = "startAssault";

    /** survivor 要素のローカル名 */
    public static final String LN_SURVIVOR = "survivor";

    /** counting 要素のローカル名 */
    public static final String LN_COUNTING = "counting";

    /** suddenDeath 要素のローカル名 */
    public static final String LN_SUDDEN_DEATH = "suddenDeath";

    /** noMurder 要素のローカル名 */
    public static final String LN_NO_MURDER = "noMurder";

    /** winVillage 要素のローカル名 */
    public static final String LN_WIN_VILLAGE = "winVillage";

    /** winWolf 要素のローカル名 */
    public static final String LN_WIN_WOLF = "winWolf";

    /** winHamster 要素のローカル名 */
    public static final String LN_WIN_HAMSTER = "winHamster";

    /** playerList 要素のローカル名 */
    public static final String LN_PLAYER_LIST = "playerList";

    /** panic 要素のローカル名 */
    public static final String LN_PANIC = "panic";

    /* 
     * --------------------------------------------------
     * EventOrderGroup の要素
     * --------------------------------------------------
     */

    /** askEntry 要素のローカル名 */
    public static final String LN_ASK_ENTRY = "askEntry";

    /** askCommit 要素のローカル名 */
    public static final String LN_ASK_COMMIT = "askCommit";
    
    /** noComment 要素のローカル名 */
    public static final String LN_NO_COMMENT = "noComment";
    
    /** stayEpilogue 要素のローカル名 */
    public static final String LN_STAY_EPILOGUE = "stayEpilogue";
    
    /** gameOver 要素のローカル名 */
    public static final String LN_GAME_OVER = "gameOver";
    
    /* 
     * --------------------------------------------------
     * EventExtraGroup の要素
     * --------------------------------------------------
     */

    /** judge 要素のローカル名 */
    public static final String LN_JUDGE = "judge";
    
    /** guard 要素のローカル名 */
    public static final String LN_GUARD = "guard";
    
    /** assault 要素のローカル名 */
    public static final String LN_ASSAULT = "assault";
    
    /* 
     * --------------------------------------------------
     * talk
     * --------------------------------------------------
     */
    
    /** talk 要素のローカル名 */
    public static final String LN_TALK = "talk";
    
    /** type 属性のローカル名 */
    public static final String LN_TYPE = "type";

    /** 発言種別：白発言 */
    public static final String VAL_TALK_TYPE_PUBLIC = "public";
    
    /** 発言種別：赤発言 */
    public static final String VAL_TALK_TYPE_WOLF = "wolf";
    
    /** 発言種別：灰発言 */
    public static final String VAL_TALK_TYPE_PRIVATE = "private";
    
    /** 発言種別：青発言 */
    public static final String VAL_TALK_TYPE_GRAVE = "grave";
    
    /** avatarId 属性のローカル名 */
    //public static final String LN_AVATAR_ID = "avatarId";     // 既に定義されているので省略
    
    /** time 属性のローカル名 */
    public static final String LN_TIME = "time";
    
    /* 
     * --------------------------------------------------
     * li
     * --------------------------------------------------
     */
    
    /** li 要素のローカル名 */
    public static final String LN_LI = "li";
}
