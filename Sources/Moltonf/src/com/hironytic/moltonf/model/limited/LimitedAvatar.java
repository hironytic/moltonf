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

package com.hironytic.moltonf.model.limited;

import java.awt.Image;
import java.net.URI;

import com.hironytic.moltonf.MoltonfException;
import com.hironytic.moltonf.model.Avatar;
import com.hironytic.moltonf.model.Role;
import com.hironytic.moltonf.model.Story;

/**
 * LimitedStory が保持する Avatar
 */
public class LimitedAvatar implements Avatar {

    /** このオブジェクトが属する LimitedStory */
    private LimitedStory story;
    
    /** 元となる Avatar */
    private Avatar baseAvatar;
    
    /**
     * コンストラクタ
     * @param story このオブジェクトが属する LimitedStory
     * @param baseAvatar 元となる Avatar
     */
    public LimitedAvatar(LimitedStory story, Avatar baseAvatar) {
        this.story = story;
        this.baseAvatar = baseAvatar;
    }
    
    /**
     * @see com.hironytic.moltonf.model.Avatar#getAbbreviatedName()
     */
    @Override
    public String getAbbreviatedName() {
        return baseAvatar.getAbbreviatedName();
    }

    /**
     * @see com.hironytic.moltonf.model.Avatar#getAvatarId()
     */
    @Override
    public String getAvatarId() {
        return baseAvatar.getAvatarId();
    }

    /**
     * @see com.hironytic.moltonf.model.Avatar#getFaceIconImage()
     */
    @Override
    public Image getFaceIconImage() {
        return baseAvatar.getFaceIconImage();
    }

    /**
     * @see com.hironytic.moltonf.model.Avatar#getFaceIconUri()
     */
    @Override
    public URI getFaceIconUri() {
        return baseAvatar.getFaceIconUri();
    }

    /**
     * @see com.hironytic.moltonf.model.Avatar#getFullName()
     */
    @Override
    public String getFullName() {
        return baseAvatar.getFullName();
    }

    /**
     * @see com.hironytic.moltonf.model.Avatar#getShortName()
     */
    @Override
    public String getShortName() {
        return baseAvatar.getShortName();
    }

    /**
     * @see com.hironytic.moltonf.model.Avatar#getStory()
     */
    @Override
    public Story getStory() {
        return story;
    }

    /**
     * @see com.hironytic.moltonf.model.Avatar#getRole()
     */
    @Override
    public Role getRole() {
        // TODO: システム的に確定して見える人以外は見えない方がいいんだろうか。
        // たとえば、狼なら、別の狼が狼だとわかるし、共有者なら、別の共有者がわかるけど
        // それ以外のものは unknown だとか。
        return baseAvatar.getRole();
    }

    /**
     * @see com.hironytic.moltonf.model.Avatar#setAbbreviatedName(java.lang.String)
     */
    @Override
    public void setAbbreviatedName(String abbreviatedName) {
        throw new MoltonfException("Don't set abbreviated name.", new UnsupportedOperationException("Modification is not allowed."));
    }

    /**
     * @see com.hironytic.moltonf.model.Avatar#setAvatarId(java.lang.String)
     */
    @Override
    public void setAvatarId(String avatarId) {
        throw new MoltonfException("Don't set avatar id.", new UnsupportedOperationException("Modification is not allowed."));
    }

    /**
     * @see com.hironytic.moltonf.model.Avatar#setFaceIconImage(java.awt.Image)
     */
    @Override
    public void setFaceIconImage(Image faceIconImage) {
        throw new MoltonfException("Don't set face icon image.", new UnsupportedOperationException("Modification is not allowed."));
    }

    /**
     * @see com.hironytic.moltonf.model.Avatar#setFaceIconUri(java.net.URI)
     */
    @Override
    public void setFaceIconUri(URI faceIconUri) {
        throw new MoltonfException("Don't set face icon uri.", new UnsupportedOperationException("Modification is not allowed."));
    }

    /**
     * @see com.hironytic.moltonf.model.Avatar#setFullName(java.lang.String)
     */
    @Override
    public void setFullName(String fullName) {
        throw new MoltonfException("Don't set full name.", new UnsupportedOperationException("Modification is not allowed."));
    }

    /**
     * @see com.hironytic.moltonf.model.Avatar#setShortName(java.lang.String)
     */
    @Override
    public void setShortName(String shortName) {
        throw new MoltonfException("Don't set short name.", new UnsupportedOperationException("Modification is not allowed."));
    }

    /**
     * @see com.hironytic.moltonf.model.Avatar#setStory(com.hironytic.moltonf.model.Story)
     */
    @Override
    public void setStory(Story story) {
        throw new MoltonfException("Don't set story.", new UnsupportedOperationException("Modification is not allowed."));
    }

    /**
     * @see com.hironytic.moltonf.model.Avatar#setRole(com.hironytic.moltonf.model.Role)
     */
    @Override
    public void setRole(Role role) {
        throw new MoltonfException("Don't set role.", new UnsupportedOperationException("Modification is not allowed."));
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "LimitedAvatar [baseAvatar=" + baseAvatar + "]";
    }
}
