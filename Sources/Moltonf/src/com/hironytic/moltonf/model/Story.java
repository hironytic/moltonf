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
import java.util.List;

/**
 * ストーリーの情報を保持するインタフェースです。
 * 通常、ストーリーは1回のプレイデータ全体に相当します。
 */
public interface Story {

    /**
     * このストーリーに含まれる StoryPeriod を返します。
     * @return StoryPeriod のリスト
     */
    public List<StoryPeriod> getPeriods();

    /**
     * このストーリーに登場する人物のリストを返します。
     * @return 登場人物のリスト
     */
    public List<Avatar> getAvatarList();

    /**
     * 村のフルネームを返します。
     * @return 村のフルネーム
     */
    public String getVillageFullName();

    /**
     * 村の状態を返します。
     * @return 村の状態
     */
    public VillageState getVillageState();
    
    /**
     * 墓アイコン画像の URI を取得します。
     * @return 墓アイコン画像の URI
     */
    public URI getGraveIconUri();

    /**
     * 墓アイコン画像を取得します。
     * @return 墓アイコン画像
     */
    public Image getGraveIconImage();
    
    /**
     * このストーリーに含まれる StoryPeriod をセットします。
     * @param periods セットしたい StoryPeriod のリスト
     */
    public void setPeriods(List<StoryPeriod> periods);
    
    /**
     * 登場人物のリストをセットします。
     * @param avatarList セットしたい Avatar のリスト
     */
    public void setAvatarList(List<Avatar> avatarList);

    /**
     * 村のフルネームをセットします。
     * @param villageFullName セットしたいフルネーム
     */
    public void setVillageFullName(String villageFullName);

    /**
     * 村の状態をセットします。
     * @param villageState セットしたい村の状態
     */
    public void setVillageState(VillageState villageState);

    /**
     * 墓アイコン画像の URIをセットします。
     * @param graveIconUri 墓アイコン画像の URI
     */
    public void setGraveIconUri(URI graveIconUri);

    /**
     * 墓アイコン画像をセットします。
     * @param graveIconImage 墓アイコン画像
     */
    public void setGraveIconImage(Image graveIconImage);
}
