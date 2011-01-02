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

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hironytic.moltonfdroid.model.StoryElement;
import com.hironytic.moltonfdroid.model.StoryEvent;
import com.hironytic.moltonfdroid.model.Talk;
import com.hironytic.moltonfdroid.util.TimePart;

/**
 * ストーリー要素の一覧のためのアダプタクラス
 */
public class StoryElementListAdapter extends ArrayAdapter<StoryElement> {

    /** ビューを生成するためのオブジェクト */
    private LayoutInflater inflater;
    
    /** リソース */
    private Resources res;

    /**
     * 発言を示すアイテムのビュー情報
     */
    private static class TalkItemViews {
        /** 発言に燗する情報を表示するビュー */
        public TextView infoView;
        
        /** メッセージを表示するビュー */
        public TextView messageView;
    }
    
    /**
     * ストーリー中のイベントを示すアイテムのビュー情報
     */
    private static class EventItemViews {
        /** メッセージを表示するビュー */
        public TextView messageView;
    }
    
    /**
     * コンストラクタ
     * @param context 現在のコンテキスト
     * @param objects 表示するオブジェクトのリスト
     */
    public StoryElementListAdapter(Context context, List<StoryElement> objects) {
        super(context, 0, objects);

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        res = context.getResources();
    }

    /**
     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StoryElement item = this.getItem(position);
        
        View retView = convertView;
        if (item instanceof Talk) {
            Talk talkItem = (Talk)item;
            TalkItemViews talkItemViews;
            if (retView == null || !(retView.getTag() instanceof TalkItemViews)) {
                retView = inflater.inflate(R.layout.listitem_talk, null);
                talkItemViews = new TalkItemViews();
                talkItemViews.infoView = (TextView)retView.findViewById(R.id.talk_info);
                talkItemViews.messageView = (TextView)retView.findViewById(R.id.talk_message);
                retView.setTag(talkItemViews);
            } else {
                talkItemViews = (TalkItemViews)retView.getTag();
            }
            
            talkItemViews.infoView.setText(makeTalkInfo(talkItem));
            talkItemViews.messageView.setText(makeMessageSequence(talkItem));
            switch (talkItem.getTalkType()) {
            case PUBLIC:
                talkItemViews.messageView.setBackgroundColor(res.getColor(R.color.talk_bg_public));
                talkItemViews.messageView.setTextColor(res.getColor(R.color.talk_text_public));
                break;
            case PRIVATE:
                talkItemViews.messageView.setBackgroundColor(res.getColor(R.color.talk_bg_private));
                talkItemViews.messageView.setTextColor(res.getColor(R.color.talk_text_private));
                break;
            case WOLF:
                talkItemViews.messageView.setBackgroundColor(res.getColor(R.color.talk_bg_wolf));
                talkItemViews.messageView.setTextColor(res.getColor(R.color.talk_text_wolf));
                break;
            case GRAVE:
                talkItemViews.messageView.setBackgroundColor(res.getColor(R.color.talk_bg_grave));
                talkItemViews.messageView.setTextColor(res.getColor(R.color.talk_text_grave));
                break;
            }
        } else if (item instanceof StoryEvent) {
            StoryEvent eventItem = (StoryEvent)item;
            EventItemViews eventItemViews;
            if (retView == null || !(retView.getTag() instanceof EventItemViews)) {
                retView = inflater.inflate(R.layout.listitem_story_event, null);
                eventItemViews = new EventItemViews();
                eventItemViews.messageView = (TextView)retView.findViewById(R.id.story_event_message);
                retView.setTag(eventItemViews);
            } else {
                eventItemViews = (EventItemViews)retView.getTag();
            }
            
            eventItemViews.messageView.setText(makeMessageSequence(eventItem));
            switch (eventItem.getEventFamily()) {
            case ANNOUNCE:
                eventItemViews.messageView.setBackgroundResource(R.drawable.announce_frame);
                eventItemViews.messageView.setTextColor(res.getColor(R.color.story_event_announce_text));
                break;
            case ORDER:
                eventItemViews.messageView.setBackgroundResource(R.drawable.order_frame);
                eventItemViews.messageView.setTextColor(res.getColor(R.color.story_event_order_text));
                break;
            case EXTRA:
                eventItemViews.messageView.setBackgroundResource(R.drawable.extra_frame);
                eventItemViews.messageView.setTextColor(res.getColor(R.color.story_event_extra_text));
                break;
            }
        }
        
        return retView;
    }
    
    /**
     * メッセージの表示用 CharSequence を生成します。
     * @param storyElement メッセージを持つ StoryElement
     * @return 表示用の CharSequence
     */
    private CharSequence makeMessageSequence(StoryElement storyElement) {
        SpannableStringBuilder buf = new SpannableStringBuilder();
        boolean isFirstLine = true;
        for (String line : storyElement.getMessageLines()) {
            if (!isFirstLine) {
                buf.append("\n");
            }
            buf.append(line);
            isFirstLine = false;
        }
        
        return buf;
    }
    
    /**
     * 発言情報の表示用 CharSequence を生成します。
     * @param talk 発言
     * @return 表示用の CharSequence
     */
    private CharSequence makeTalkInfo(Talk talk) {
        SpannableStringBuilder buf = new SpannableStringBuilder();
        int start;
        int end;
        int color;
        
        start = buf.length();
        buf.append(talk.getSpeaker().getFullName());
        buf.append(" ");
        end = buf.length();
        color = res.getColor(R.color.talk_speaker_info_text);
        buf.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        start = buf.length();
        TimePart time = talk.getTime();
        String timeString = String.format("%02d:%02d", time.getHourPart(), time.getMinutePart());
        buf.append(timeString);
        end = buf.length();
        color = res.getColor(R.color.talk_time_info_text);
        buf.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        return buf;
    }
}
