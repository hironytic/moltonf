/*
 * Moltonf
 *
 * Copyright (c) 2010 Hironori Ichimiya <hiron@hironytic.com>
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

package com.hironytic.moltonf.model;

import java.awt.Image;
import java.net.URI;

/**
 * 登場人物の情報を保持するインタフェース。
 */
public interface Avatar {

    /**
     * このオブジェクトが属する Story を取得します。
     * @return このオブジェクトが属する Story
     */
    public Story getStory();
    
    /**
     * 登場人物の識別子を返します。 
     * @return 識別子
     */
    public String getAvatarId();

    /**
     * フルネームを返します。
     * 
     * 例：「楽天家 ゲルト」
     * @return フルネーム 
     */
    public String getFullName();

    /**
     * 短い名前を返します。
     * 
     * 例：「ゲルト」
     * @return 短い名前
     */
    public String getShortName();

    /**
     * 短縮名を返します。
     * 
     * 例：「楽」
     * @return 短縮名
     */
    public String getAbbreviatedName();

    /**
     * 顔アイコン画像のある URI を返します。
     * @return 顔アイコンへの URI
     */
    public URI getFaceIconUri();
    
    /**
     * 顔アイコン画像を返します。
     * @return 顔アイコン画像
     */
    public Image getFaceIconImage();
    
    /**
     * 役職を返します。
     * @return 役職
     */
    public Role getRole();
    
    /**
     * このオブジェクトが属する Story をセットします。
     * @param story このオブジェクトが属する Story
     */
    public void setStory(Story story);
    
    /**
     * 登場人物の識別子をセットします。
     * @param avatarId セットしたい登場人物の識別子の値
     */
    public void setAvatarId(String avatarId);

    /**
     * フルネームをセットします。
     * @param fullName セットしたいフルネームの値
     */
    public void setFullName(String fullName);

    /**
     * 短い名前をセットします。
     * @param shortName セットしたい短い名前の値
     */
    public void setShortName(String shortName);

    /**
     * 短縮名をセットします。
     * @param abbreviatedName セットしたい短縮名の値
     */
    public void setAbbreviatedName(String abbreviatedName);

    /**
     * 顔アイコン画像のある URI をセットします。
     * @param faceIconUri セットしたい URI の値
     */
    public void setFaceIconUri(URI faceIconUri);

    /**
     * 顔アイコン画像をセットします。
     * @param faceIconImage セットしたい顔アイコン画像の値
     */
    public void setFaceIconImage(Image faceIconImage);
    
    /**
     * 役職をセットします。
     * @param role セットしたい役職の値
     */
    public void setRole(Role role);
}
