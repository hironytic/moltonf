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

package com.hironytic.moltonf.model.limited;

import java.awt.Image;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.hironytic.moltonf.MoltonfException;
import com.hironytic.moltonf.model.Avatar;
import com.hironytic.moltonf.model.Story;
import com.hironytic.moltonf.model.StoryPeriod;
import com.hironytic.moltonf.model.VillageState;

/**
 * 経過日数や人物によって得られる情報を制限した Story
 */
public class LimitedStory implements Story {

    /** 元になる（情報を制限する前の）Story */
    private Story baseStory;
    
    /**
     * 制限するための登場人物。
     * この登場人物の視点で見える情報だけに制限します。
     */
    private Avatar targetAvatar;
    
    /**
     * 何日目まで見えている状態か。
     * 例えばこの値が 1 ならインデックス 1 の StoryPeriod までが見えていることになります。
     */
    private int currentPeriod;
 
    /** この Story に含まれる StoryPeriod のリスト */
    private List<StoryPeriod> periods;
 
    /** 登場人物のリスト */
    private List<Avatar> avatarList;

    /**
     * コンストラクタ
     * @param baseStory 元になる Story
     * @param targetAvatar 制限するための登場人物。
     * @param currentPeriod 何日目まで見えている状態か。
     */
    public LimitedStory(Story baseStory, Avatar targetAvatar, int currentPeriod) {
        this.baseStory = baseStory;
        this.targetAvatar = targetAvatar;
        this.currentPeriod = currentPeriod;
        
        List<StoryPeriod> basePeriods = this.baseStory.getPeriods();
        if (basePeriods.size() <= this.currentPeriod) {
            this.currentPeriod = basePeriods.size() - 1;
        }
        
        this.periods = new ArrayList<StoryPeriod>(this.currentPeriod + 1);
        for (int ix = 0; ix <= this.currentPeriod; ++ix) {
            this.periods.add(new LimitedStoryPeriod(this, basePeriods.get(ix)));
        }
        
        List<Avatar> baseAvatarList = this.baseStory.getAvatarList();
        this.avatarList = new ArrayList<Avatar>(baseAvatarList.size());
        for (Avatar baseAvatar : baseAvatarList) {
            this.avatarList.add(new LimitedAvatar(this, baseAvatar));
        }
    }
    
    /**
     * @see com.hironytic.moltonf.model.Story#getAvatarList()
     */
    @Override
    public List<Avatar> getAvatarList() {
        return avatarList;
    }

    /**
     * @see com.hironytic.moltonf.model.Story#getGraveIconImage()
     */
    @Override
    public Image getGraveIconImage() {
        return baseStory.getGraveIconImage();
    }

    /**
     * @see com.hironytic.moltonf.model.Story#getGraveIconUri()
     */
    @Override
    public URI getGraveIconUri() {
        return baseStory.getGraveIconUri();
    }

    /**
     * @see com.hironytic.moltonf.model.Story#getPeriods()
     */
    @Override
    public List<StoryPeriod> getPeriods() {
        return periods;
    }

    /**
     * @see com.hironytic.moltonf.model.Story#getVillageFullName()
     */
    @Override
    public String getVillageFullName() {
        return baseStory.getVillageFullName();
    }

    /**
     * @see com.hironytic.moltonf.model.Story#getVillageState()
     */
    @Override
    public VillageState getVillageState() {
        // TODO: 制限されている範囲とは関係なく、baseStory のを返しているけどいいかな？
        return baseStory.getVillageState();
    }

    /**
     * @see com.hironytic.moltonf.model.Story#setAvatarList(java.util.List)
     */
    @Override
    public void setAvatarList(List<Avatar> avatarList) {
        throw new MoltonfException("Don't set avatar list.", new UnsupportedOperationException("Modification is not allowed."));
    }

    /**
     * @see com.hironytic.moltonf.model.Story#setGraveIconImage(java.awt.Image)
     */
    @Override
    public void setGraveIconImage(Image graveIconImage) {
        throw new MoltonfException("Don't set grave icon image.", new UnsupportedOperationException("Modification is not allowed."));
    }

    /**
     * @see com.hironytic.moltonf.model.Story#setGraveIconUri(java.net.URI)
     */
    @Override
    public void setGraveIconUri(URI graveIconUri) {
        throw new MoltonfException("Don't set grave icon uri.", new UnsupportedOperationException("Modification is not allowed."));
    }

    /**
     * @see com.hironytic.moltonf.model.Story#setPeriods(java.util.List)
     */
    @Override
    public void setPeriods(List<StoryPeriod> periods) {
        throw new MoltonfException("Don't set periods.", new UnsupportedOperationException("Modification is not allowed."));
    }

    /**
     * @see com.hironytic.moltonf.model.Story#setVillageFullName(java.lang.String)
     */
    @Override
    public void setVillageFullName(String villageFullName) {
        throw new MoltonfException("Don't set village full name.", new UnsupportedOperationException("Modification is not allowed."));
    }

    /**
     * @see com.hironytic.moltonf.model.Story#setVillageState(com.hironytic.moltonf.model.VillageState)
     */
    @Override
    public void setVillageState(VillageState villageState) {
        throw new MoltonfException("Don't set village state.", new UnsupportedOperationException("Modification is not allowed."));
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "LimitedStory [currentPeriod=" + currentPeriod + ", periods="
                + periods + ", targetAvatar=" + targetAvatar + ", baseStory="
                + baseStory + "]";
    }
}
