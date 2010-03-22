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

package com.hironytic.moltonf.model.basic;

import java.net.URI;

import com.hironytic.moltonf.model.Avatar;

/**
 * 基本的な Avatar 実装
 */
public class BasicAvatar implements Avatar {

    /** 省略名 */
    private String abbreviatedName;
 
    /** アバターの ID */
    private String avatarId;
    
    /** 顔アイコン画像への URI */
    private URI faceIconUri;
    
    /** フルネーム */
    private String fullName;
    
    /** 短い名前 */
    private String shortName;
    
    /**
     * @see com.hironytic.moltonf.model.Avatar#getAbbreviatedName()
     */
    @Override
    public String getAbbreviatedName() {
        return abbreviatedName;
    }

    /**
     * @see com.hironytic.moltonf.model.Avatar#getAvatarId()
     */
    @Override
    public String getAvatarId() {
        return avatarId;
    }

    /**
     * @see com.hironytic.moltonf.model.Avatar#getFaceIconUri()
     */
    @Override
    public URI getFaceIconUri() {
        return faceIconUri;
    }

    /**
     * @see com.hironytic.moltonf.model.Avatar#getFullName()
     */
    @Override
    public String getFullName() {
        return fullName;
    }

    /**
     * @see com.hironytic.moltonf.model.Avatar#getShortName()
     */
    @Override
    public String getShortName() {
        return shortName;
    }

    /**
     * abbreviatedName をセットします。
     * @param abbreviatedName セットしたい abbreviatedName の値
     */
    public void setAbbreviatedName(String abbreviatedName) {
        this.abbreviatedName = abbreviatedName;
    }

    /**
     * avatarId をセットします。
     * @param avatarId セットしたい avatarId の値
     */
    public void setAvatarId(String avatarId) {
        this.avatarId = avatarId;
    }

    /**
     * faceIconUri をセットします。
     * @param faceIconUri セットしたい faceIconUri の値
     */
    public void setFaceIconUri(URI faceIconUri) {
        this.faceIconUri = faceIconUri;
    }

    /**
     * fullName をセットします。
     * @param fullName セットしたい fullName の値
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * shortName をセットします。
     * @param shortName セットしたい shortName の値
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "BasicAvatar [shortName=" + shortName + "]";
    }
}
