/*
 * Moltonf
 *
 * Copyright (c) 2011 Hironori Ichimiya <hiron@hironytic.com>
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

package com.hironytic.moltonfdroid.util;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

/**
 * 画像を保持するオブジェクト。
 * 画像をまだ保持していないときに画像を要求しておくと、
 * 画像を保持したときに呼び出してくれる機能を持ちます。
 */
public class BitmapHolder {
    /** 保持している画像 */
    private Bitmap bitmap = null;
    
    /** 画像を要求して待っている処理のリスト */
    private List<Proc1<Bitmap>> requestList = null;
    
    /**
     * コンストラクタ
     */
    public BitmapHolder() {
    }
    
    /**
     * 画像を要求します。
     * 画像を保持していれば、receiveProc が実行されます。
     * 画像をまだ保持していなければ要求リストに追加されます。
     * 画像を保持したタイミングで receiveProc が実行されます。
     * @param receiveProc 画像を受け取る処理のオブジェクト
     */
    public void requestBitmap(Proc1<Bitmap> receiveProc) {
        if (bitmap == null) {
            if (requestList == null) {
                requestList = new ArrayList<Proc1<Bitmap>>();
            }
            requestList.add(receiveProc);
        } else {
            receiveProc.perform(bitmap);
        }
    }
    
    /**
     * 要求リストに入っている特定の処理をキャンセルします。
     * @param receiveProc キャンセルしたい画像を受け取る処理のオブジェクト
     */
    public void cancelRequest(Proc1<Bitmap> receiveProc) {
        if (requestList != null) {
            requestList.remove(receiveProc);
        }
    }
    
    /**
     * 要求リストに入っている処理をすべてキャンセルします。
     */
    public void cancelAllRequest() {
        requestList = null;
    }
    
    /**
     * 画像を保持させます。
     * @param bitmap 保持させる画像
     */
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        if (requestList != null) {
            for (Proc1<Bitmap> receiveProc : requestList) {
                receiveProc.perform(bitmap);
            }
            requestList = null;
        }
    }
    
    /**
     * 画像を保持しているかどうかを返します。
     * @return 画像を保持しているなら true、そうでなければ false
     */
    public boolean hasBitmap() {
        return (bitmap != null);
    }
}
