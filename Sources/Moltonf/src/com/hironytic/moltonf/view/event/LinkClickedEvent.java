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

package com.hironytic.moltonf.view.event;

import java.awt.event.MouseEvent;
import java.util.EventObject;

import com.hironytic.moltonf.model.Link;

/**
 * リンクがクリックされたときの情報を保持するイベント
 */
@SuppressWarnings("serial")
public class LinkClickedEvent extends EventObject {

    /** クリックされたリンク */
    private Link link;
    
    /** クリックの引き金になったマウスイベント */
    private MouseEvent mouseEvent;
    
    /**
     * コンストラクタ
     * @param source イベントが最初に発生したオブジェクト
     */
    public LinkClickedEvent(Object source) {
        super(source);
    }

    /**
     * クリックされたリンクを取得します。
     * @return リンク
     */
    public Link getLink() {
        return link;
    }

    /**
     * クリックされたリンクをセットします。
     * @param link リンク
     */
    public void setLink(Link link) {
        this.link = link;
    }

    /**
     * クリックの引き金になったマウスイベントを取得します。
     * @return マウスイベント
     */
    public MouseEvent getMouseEvent() {
        return mouseEvent;
    }

    /**
     * クリックの引き金になったマウスイベントをセットします。
     * @param mouseEvent マウスイベント
     */
    public void setMouseEvent(MouseEvent mouseEvent) {
        this.mouseEvent = mouseEvent;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "LinkClickedEvent [link=" + link + "]";
    }
}
