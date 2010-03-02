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
    public static final String NS_TNS = "http://jindolf.sourceforge.jp/xml/ns/401";

    /** xml:base 属性のローカル名 */
    public static final String LN_BASE = "base";

    /** formalName 属性のローカル名 */
    public static final String LN_FORMAL_NAME = "formalName";

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

    /** tns:avatarList 要素のローカル名 */
    public static final String LN_AVATAR_LIST = "avatarList";

    /** tns:avatar 要素のローカル名 */
    public static final String LN_AVATAR = "avatar";
}
