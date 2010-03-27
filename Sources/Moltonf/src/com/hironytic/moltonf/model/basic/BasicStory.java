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

import java.util.List;

import com.hironytic.moltonf.model.Avatar;
import com.hironytic.moltonf.model.Story;
import com.hironytic.moltonf.model.StoryPeriod;
import com.hironytic.moltonf.model.VillageState;

/**
 * 基本的な Story 実装。
 */
public class BasicStory implements Story {

    /** 登場人物のリスト */
    private List<Avatar> avatarList;
    
    /** この Story に含まれる StoryPeriod のリスト */
    private List<StoryPeriod> periods;
    
    /** 村の完全名 */
    private String villageFullName;
    
    /** 村の状態 */
    private VillageState villageState;
    
    /**
     * コンストラクタ
     */
    public BasicStory() {
    }
    
    /**
     * @see com.hironytic.moltonf.model.Story#getAvatarList()
     */
    @Override
    public List<Avatar> getAvatarList() {
        return avatarList;
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
        return villageFullName;
    }

    /**
     * @see com.hironytic.moltonf.model.Story#getVillageState()
     */
    @Override
    public VillageState getVillageState() {
        return villageState;
    }

    /**
     * avatarList をセットします。
     * @param avatarList セットしたい avatarList の値
     */
    public void setAvatarList(List<Avatar> avatarList) {
        this.avatarList = avatarList;
    }

    /**
     * periods をセットします。
     * @param periods セットしたい periods の値
     */
    public void setPeriods(List<StoryPeriod> periods) {
        this.periods = periods;
    }

    /**
     * villageFullName をセットします。
     * @param villageFullName セットしたい villageFullName の値
     */
    public void setVillageFullName(String villageFullName) {
        this.villageFullName = villageFullName;
    }

    /**
     * villageState をセットします。
     * @param villageState セットしたい villageState の値
     */
    public void setVillageState(VillageState villageState) {
        this.villageState = villageState;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "BasicStory [villageFullName=" + villageFullName + "]";
    }
}
