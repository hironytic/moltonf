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
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hironytic.moltonfdroid.model.HighlightSetting;
import com.hironytic.moltonfdroid.model.StoryElement;
import com.hironytic.moltonfdroid.model.StoryEvent;
import com.hironytic.moltonfdroid.model.Talk;
import com.hironytic.moltonfdroid.util.BitmapHolder;
import com.hironytic.moltonfdroid.util.Proc1;
import com.hironytic.moltonfdroid.util.TimePart;

/**
 * ストーリー要素の一覧のためのアダプタクラス
 */
public class StoryElementListAdapter extends ArrayAdapter<StoryElement> implements AbsListView.RecyclerListener {

    /** ビューを生成するためのオブジェクト */
    private LayoutInflater inflater;
    
    /** リソース */
    private Resources res;
    
    /** デフォルトの顔アイコン */
    private Bitmap defaultFaceIcon;

    /** 強調表示設定のリスト */
    private List<HighlightSetting> highlightSettingList;
    
    /** ビュー情報 */
    private final Map<View, ViewInfo> viewInfoMap = new WeakHashMap<View, ViewInfo>();
    
    /**
     * 各ビューの情報
     */
    private static class ViewInfo {
    }
    
    /**
     * 発言を示すアイテムのビュー情報
     */
    private static class TalkItemViewInfo extends ViewInfo {
        /** 発言に燗する情報を表示するビュー */
        public TextView infoView;
        
        /** 顔アイコンを表示するビュー */
        public ImageView faceIconView;
        
        /** メッセージを表示するビュー */
        public TextView messageView;
        
        /** 顔アイコンを要求したビットマップホルダー */
        public BitmapHolder faceBitmapHolder;
        
        /** 顔アイコンの要求を受け取るプロシージャ */
        public Proc1<Bitmap> faceBitmapRequestProc;
    }
    
    /**
     * ストーリー中のイベントを示すアイテムのビュー情報
     */
    private static class EventItemViewInfo extends ViewInfo {
        /** メッセージを表示するビュー */
        public TextView messageView;
    }
    
    /**
     * コンストラクタ
     * @param context 現在のコンテキスト
     */
    public StoryElementListAdapter(Context context, List<HighlightSetting> highlightSettingList) {
        super(context, 0);

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        res = context.getResources();
        defaultFaceIcon = BitmapFactory.decodeResource(res, R.drawable.default_face);
        this.highlightSettingList = highlightSettingList;
    }

    /**
     * これ以上このオブジェクトを利用しないときに呼び出します。
     */
    public void destroy() {
        // 画像を要求中のものがあれば要求をキャンセル
        for (ViewInfo viewInfo : viewInfoMap.values()) {
            if (viewInfo instanceof TalkItemViewInfo) {
                TalkItemViewInfo talkItemViewInfo = (TalkItemViewInfo)viewInfo;
                if (talkItemViewInfo.faceBitmapHolder != null) {
                    talkItemViewInfo.faceBitmapHolder.cancelRequest(talkItemViewInfo.faceBitmapRequestProc);
                    talkItemViewInfo.faceBitmapHolder = null;
                }
            }        
        }
        
        viewInfoMap.clear();
    }    
    
    /**
     * 表示するStoryElementをすべて差し替えます。
     * @param objects
     */
    @TargetApi(11)
    public void replaceStoryElements(List<StoryElement> objects) {
        clear();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            for (StoryElement element : objects) {
                add(element);
            }
        } else {
            addAll(objects);
        }
        notifyDataSetChanged();
    }
    
    /**
     * @see android.widget.AbsListView.RecyclerListener#onMovedToScrapHeap(android.view.View)
     */
    @Override
    public void onMovedToScrapHeap(View view) {
        ViewInfo viewInfo = viewInfoMap.get(view);
        if (viewInfo instanceof TalkItemViewInfo) {
            TalkItemViewInfo talkItemViewInfo = (TalkItemViewInfo)viewInfo;

            // ビューが顔画像を要求中かもしれないので要求をキャンセル。
            if (talkItemViewInfo.faceBitmapHolder != null) {
                talkItemViewInfo.faceBitmapHolder.cancelRequest(talkItemViewInfo.faceBitmapRequestProc);
                talkItemViewInfo.faceBitmapHolder = null;
            }
        }        
    }

    private static int VIEW_TYPE_TALK = 0;
    private static int VIEW_TYPE_STORY_EVENT = 1;
    private static int NUM_VIEW_TYPE = 2;
    
    /**
     * @see android.widget.BaseAdapter#getViewTypeCount()
     */
    @Override
    public int getViewTypeCount() {
        return NUM_VIEW_TYPE;
    }

    /**
     * @see android.widget.BaseAdapter#getItemViewType(int)
     */
    @Override
    public int getItemViewType(int position) {
        StoryElement item = this.getItem(position);
        if (item instanceof Talk) {
            return VIEW_TYPE_TALK;
        } else if (item instanceof StoryEvent) {
            return VIEW_TYPE_STORY_EVENT;
        }
        
        return IGNORE_ITEM_VIEW_TYPE;
    }

    /**
     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StoryElement item = this.getItem(position);
        
        View retView = convertView;
        ViewInfo viewInfo = viewInfoMap.get(retView);
        if (item instanceof Talk) {
            Talk talkItem = (Talk)item;
            TalkItemViewInfo talkItemViewInfo;
            if (retView == null || !(viewInfo instanceof TalkItemViewInfo)) {
                retView = inflater.inflate(R.layout.listitem_talk, null);
                talkItemViewInfo = new TalkItemViewInfo();
                talkItemViewInfo.infoView = (TextView)retView.findViewById(R.id.talk_info);
                talkItemViewInfo.faceIconView = (ImageView)retView.findViewById(R.id.face_icon);
                talkItemViewInfo.messageView = (TextView)retView.findViewById(R.id.talk_message);
                viewInfoMap.put(retView, talkItemViewInfo);
            } else {
                talkItemViewInfo = (TalkItemViewInfo)viewInfo;
            }

            // 情報テキスト
            talkItemViewInfo.infoView.setText(makeTalkInfo(talkItem));
            
            // メッセージ
            talkItemViewInfo.messageView.setText(makeMessageSequence(talkItem));
            
            // 吹き出しの色と顔アイコン
            BitmapHolder faceBitmapHolder = talkItem.getSpeaker().getFaceIconHolder();
            switch (talkItem.getTalkType()) {
            case PUBLIC:
                talkItemViewInfo.messageView.setBackgroundColor(res.getColor(R.color.talk_bg_public));
                talkItemViewInfo.messageView.setTextColor(res.getColor(R.color.talk_text_public));
                break;
            case PRIVATE:
                talkItemViewInfo.messageView.setBackgroundColor(res.getColor(R.color.talk_bg_private));
                talkItemViewInfo.messageView.setTextColor(res.getColor(R.color.talk_text_private));
                break;
            case WOLF:
                talkItemViewInfo.messageView.setBackgroundColor(res.getColor(R.color.talk_bg_wolf));
                talkItemViewInfo.messageView.setTextColor(res.getColor(R.color.talk_text_wolf));
                break;
            case GRAVE:
                talkItemViewInfo.messageView.setBackgroundColor(res.getColor(R.color.talk_bg_grave));
                talkItemViewInfo.messageView.setTextColor(res.getColor(R.color.talk_text_grave));
                faceBitmapHolder = talkItem.getStory().getGraveIconHolder();
                break;
            }
            
            // デフォルト画像に変更しておく
            talkItemViewInfo.faceIconView.setImageBitmap(defaultFaceIcon);

            // 実際の顔画像を要求
            final ImageView faceIconView = talkItemViewInfo.faceIconView;
            talkItemViewInfo.faceBitmapHolder = faceBitmapHolder;
            talkItemViewInfo.faceBitmapRequestProc = new Proc1<Bitmap>() {
                @Override
                public void perform(Bitmap arg) {
                    faceIconView.setImageBitmap(arg);
                }
            };
            talkItemViewInfo.faceBitmapHolder.requestBitmap(talkItemViewInfo.faceBitmapRequestProc);
        } else if (item instanceof StoryEvent) {
            StoryEvent eventItem = (StoryEvent)item;
            EventItemViewInfo eventItemViewInfo;
            if (retView == null || !(viewInfo instanceof EventItemViewInfo)) {
                retView = inflater.inflate(R.layout.listitem_story_event, null);
                eventItemViewInfo = new EventItemViewInfo();
                eventItemViewInfo.messageView = (TextView)retView.findViewById(R.id.story_event_message);
                viewInfoMap.put(retView, eventItemViewInfo);
            } else {
                eventItemViewInfo = (EventItemViewInfo)viewInfo;
            }
            
            // メッセージ
            eventItemViewInfo.messageView.setText(makeMessageSequence(eventItem));
            
            // メッセージの色
            switch (eventItem.getEventFamily()) {
            case ANNOUNCE:
                eventItemViewInfo.messageView.setBackgroundResource(R.drawable.announce_frame);
                eventItemViewInfo.messageView.setTextColor(res.getColor(R.color.story_event_announce_text));
                break;
            case ORDER:
                eventItemViewInfo.messageView.setBackgroundResource(R.drawable.order_frame);
                eventItemViewInfo.messageView.setTextColor(res.getColor(R.color.story_event_order_text));
                break;
            case EXTRA:
                eventItemViewInfo.messageView.setBackgroundResource(R.drawable.extra_frame);
                eventItemViewInfo.messageView.setTextColor(res.getColor(R.color.story_event_extra_text));
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
            int lineStart = buf.length();
            buf.append(line);
            
            if (highlightSettingList != null) {
                for (HighlightSetting highlightSetting : highlightSettingList) {
                    if (!highlightSetting.isValid()) {
                        continue;
                    }
                    
                    Pattern pattern = highlightSetting.getPattern();
                    Matcher matcher = pattern.matcher(line);
                    while (matcher.find()) {
                        buf.setSpan(new ForegroundColorSpan(highlightSetting.getHighlightColor()),
                                matcher.start() + lineStart, matcher.end() + lineStart, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
            
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
