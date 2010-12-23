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

package com.hironytic.moltonf.model.basic;

import java.awt.Image;
import java.net.URI;

import com.hironytic.moltonf.model.Avatar;
import com.hironytic.moltonf.model.Role;
import com.hironytic.moltonf.model.Story;

/**
 * 登場人物の情報を保持するクラスです。
 */
public class BasicAvatar implements Avatar {

    /** このオブジェクトが属する Story */
    private Story story;
    
    /** 省略名 */
    private String abbreviatedName;
 
    /** アバターの ID */
    private String avatarId;
    
    /** 顔アイコン画像への URI */
    private URI faceIconUri;
    
    /** 顔アイコン画像 */
    private Image faceIconImage;
    
    /** フルネーム */
    private String fullName;
    
    /** 短い名前 */
    private String shortName;
    
    /** 役職 */
    private Role role;
    
    /**
     * コンストラクタ
     */
    public BasicAvatar() {
        
    }
    
    /**
     * このオブジェクトが属する Story を取得します。
     * @return このオブジェクトが属する Story
     */
    @Override
    public Story getStory() {
        return story;
    }

    /**
     * 登場人物の識別子を返します。 
     * @return 識別子
     */
    @Override
    public String getAvatarId() {
        return avatarId;
    }

    /**
     * フルネームを返します。
     * 
     * 例：「楽天家 ゲルト」
     * @return フルネーム 
     */
    @Override
    public String getFullName() {
        return fullName;
    }

    /**
     * 短い名前を返します。
     * 
     * 例：「ゲルト」
     * @return 短い名前
     */
    @Override
    public String getShortName() {
        return shortName;
    }

    /**
     * 短縮名を返します。
     * 
     * 例：「楽」
     * @return 短縮名
     */
    @Override
    public String getAbbreviatedName() {
        return abbreviatedName;
    }

    /**
     * 顔アイコン画像のある URI を返します。
     * @return 顔アイコンへの URI
     */
    @Override
    public URI getFaceIconUri() {
        return faceIconUri;
    }
    
    /**
     * 顔アイコン画像を返します。
     * @return 顔アイコン画像
     */
    @Override
    public Image getFaceIconImage() {
        return faceIconImage;
    }
    
    /**
     * 役職を返します。
     * @return 役職
     */
    @Override
    public Role getRole() {
        return role;
    }
    
    /**
     * このオブジェクトが属する Story をセットします。
     * @param story このオブジェクトが属する Story
     */
    @Override
    public void setStory(Story story) {
        this.story = story;
    }
    
    /**
     * 登場人物の識別子をセットします。
     * @param avatarId セットしたい登場人物の識別子の値
     */
    @Override
    public void setAvatarId(String avatarId) {
        this.avatarId = avatarId;
    }

    /**
     * フルネームをセットします。
     * @param fullName セットしたいフルネームの値
     */
    @Override
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * 短い名前をセットします。
     * @param shortName セットしたい短い名前の値
     */
    @Override
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * 短縮名をセットします。
     * @param abbreviatedName セットしたい短縮名の値
     */
    @Override
    public void setAbbreviatedName(String abbreviatedName) {
        this.abbreviatedName = abbreviatedName;
    }

    /**
     * 顔アイコン画像のある URI をセットします。
     * @param faceIconUri セットしたい URI の値
     */
    @Override
    public void setFaceIconUri(URI faceIconUri) {
        this.faceIconUri = faceIconUri;
    }

    /**
     * 顔アイコン画像をセットします。
     * @param faceIconImage セットしたい顔アイコン画像の値
     */
    @Override
    public void setFaceIconImage(Image faceIconImage) {
        this.faceIconImage = faceIconImage;
    }
    
    /**
     * 役職をセットします。
     * @param role セットしたい役職の値
     */
    @Override
    public void setRole(Role role) {
        this.role = role;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "BasicAvatar [shortName=" + shortName + "]";
    }
}
