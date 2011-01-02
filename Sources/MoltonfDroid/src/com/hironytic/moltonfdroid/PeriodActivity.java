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

package com.hironytic.moltonfdroid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hironytic.moltonfdroid.R;
import com.hironytic.moltonfdroid.model.Avatar;
import com.hironytic.moltonfdroid.model.EventFamily;
import com.hironytic.moltonfdroid.model.StoryElement;
import com.hironytic.moltonfdroid.model.StoryEvent;
import com.hironytic.moltonfdroid.model.Talk;
import com.hironytic.moltonfdroid.model.TalkType;
import com.hironytic.moltonfdroid.model.basic.BasicAvatar;
import com.hironytic.moltonfdroid.model.basic.BasicStoryEvent;
import com.hironytic.moltonfdroid.model.basic.BasicTalk;
import com.hironytic.moltonfdroid.util.TimePart;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListAdapter;

/**
 * 1 単位期間のストーリーを表示する Activity
 */
public class PeriodActivity extends ListActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.period);

        List<StoryElement> elemList = new ArrayList<StoryElement>();

        StoryEvent event = new BasicStoryEvent();
        event.setEventFamily(EventFamily.ANNOUNCE);
        event.setMessageLines(Arrays.asList("昼間は人間のふりをして、夜に正体を現すという人狼。", "その人狼が、この村に紛れ込んでいるという噂が広がった。", "", "村人達は半信半疑ながらも、村はずれの宿に集められることになった。"));
        elemList.add(event);
        
        Avatar gerd = new BasicAvatar();
        gerd.setAvatarId("gerd");
        gerd.setFullName("楽天家 ゲルト");
        
        Talk talk = new BasicTalk();
        TimePart timePart = new TimePart(23, 38, 12, 0);
        talk.setTime(timePart);
        talk.setSpeaker(gerd);
        talk.setMessageLines(Arrays.asList("人狼なんているわけないじゃん。みんな大げさだなあ"));
        talk.setTalkType(TalkType.PUBLIC);
        elemList.add(talk);
        
        talk = new BasicTalk();
        timePart = new TimePart(23, 52, 10, 0);
        talk.setTime(timePart);
        talk.setSpeaker(gerd);
        talk.setMessageLines(Arrays.asList("議題をどうぞです。", "■1.おもな参加時間帯", "■2.仮/本決定時間の希望", "■3.投票ＣＯの可否", "□4.自己紹介"));
        talk.setTalkType(TalkType.WOLF);
        elemList.add(talk);
        
        ListAdapter adapter = new StoryElementListAdapter(getApplicationContext(), elemList);
        setListAdapter(adapter);
        
        
    }
}