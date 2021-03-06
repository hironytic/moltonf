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

package com.hironytic.moltonfdroid.model.basic;

import java.net.URI;
import java.util.List;

import com.hironytic.moltonfdroid.model.Avatar;
import com.hironytic.moltonfdroid.model.Story;
import com.hironytic.moltonfdroid.model.StoryPeriod;
import com.hironytic.moltonfdroid.model.VillageState;
import com.hironytic.moltonfdroid.util.BitmapHolder;
import com.hironytic.moltonfdroid.util.SmartUtils;

/**
 * ストーリーの情報を保持するクラス。
 */
public class BasicStory implements Story {

    /** この Story に含まれる StoryPeriod のリスト */
    private List<StoryPeriod> periods;
    
    /** 登場人物のリスト */
    private List<Avatar> avatarList;
    
    /** 村の完全名 */
    private String villageFullName;

    /** 村の状態 */
    private VillageState villageState;
    
    /** 墓アイコン画像への URI */
    private URI graveIconUri;

    /**
     * 墓アイコン画像ホルダー
     */
    private BitmapHolder graveIconHolder = new BitmapHolder();
    
    /**
     * コンストラクタ
     */
    public BasicStory() {
        
    }
    
    /**
     * データの準備ができているかどうかを調べます。
     * このクラスの実装では常に true を返します。
     * @return 準備ができているなら true
     */
    @Override
    public boolean isReady() {
        return true;
    }

    /**
     * データの準備ができていなければ準備を行います。
     */
    @Override
    public void ready() {
    }
    
    /**
     * @see com.hironytic.moltonfdroid.model.Story#getPeriodCount()
     */
    @Override
    public int getPeriodCount() {
        return periods.size();
    }

    /**
     * @see com.hironytic.moltonfdroid.model.Story#getPeriod(int)
     */
    @Override
    public StoryPeriod getPeriod(int index) {
        return periods.get(index);
    }

    /**
     * このストーリーに登場する人物のリストを返します。
     * @return 登場人物のリスト
     */
    @Override
    public List<Avatar> getAvatarList() {
        return avatarList;
    }

    /**
     * 登場人物の識別子に対応する Avatar オブジェクトを返します。
     * @param avatarId 識別子
     * @return Avatar オブジェクト。対応するものがなければ null。
     */
    @Override
    public Avatar getAvatar(String avatarId) {
        if (avatarList != null) {
            for (Avatar avatar : avatarList) {
                if (avatar != null && SmartUtils.equals(avatar.getAvatarId(), avatarId)) {
                    return avatar;
                }
            }
        }
        return null;
    }
    
    /**
     * 村のフルネームを返します。
     * @return 村のフルネーム
     */
    @Override
    public String getVillageFullName() {
        return villageFullName;
    }

    /**
     * 村の状態を返します。
     * @return 村の状態
     */
    @Override
    public VillageState getVillageState() {
        return villageState;
    }
    
    /**
     * 墓アイコン画像の URI を取得します。
     * @return 墓アイコン画像の URI
     */
    @Override
    public URI getGraveIconUri() {
        return graveIconUri;
    }

    /**
     * 墓アイコン画像を保持するオブジェクトを取得します。
     * @return 墓アイコン画像ホルダー
     */
    @Override
    public BitmapHolder getGraveIconHolder() {
        return graveIconHolder;
    }
    
    /**
     * このストーリーに含まれる StoryPeriod をセットします。
     * @param periods セットしたい StoryPeriod のリスト
     */
    @Override
    public void setPeriods(List<StoryPeriod> periods) {
        this.periods = periods;
    }

    /**
     * 登場人物のリストをセットします。
     * @param avatarList セットしたい Avatar のリスト
     */
    @Override
    public void setAvatarList(List<Avatar> avatarList) {
        this.avatarList = avatarList;
    }

    /**
     * 村のフルネームをセットします。
     * @param villageFullName セットしたいフルネーム
     */
    @Override
    public void setVillageFullName(String villageFullName) {
        this.villageFullName = villageFullName;
    }

    /**
     * 村の状態をセットします。
     * @param villageState セットしたい村の状態
     */
    @Override
    public void setVillageState(VillageState villageState) {
        this.villageState = villageState;
    }

    /**
     * 墓アイコン画像の URIをセットします。
     * @param graveIconUri 墓アイコン画像の URI
     */
    @Override
    public void setGraveIconUri(URI graveIconUri) {
        this.graveIconUri = graveIconUri;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "BasicStory [villageFullName=" + villageFullName + "]";
    }
}
