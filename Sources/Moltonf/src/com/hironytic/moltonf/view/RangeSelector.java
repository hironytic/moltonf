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

package com.hironytic.moltonf.view;

import java.awt.geom.Point2D;

import javax.swing.JComponent;

/**
 * 範囲選択を司るオブジェクトが実装するインタフェース
 */
public interface RangeSelector {

    /**
     * 指定したコンポーネントのある点を選択範囲の開始点にします。
     * @param component コンポーネント
     * @param pt 開始点。第1引数で指定したコンポーネントの座標系で指定します。
     */
    public void selectFrom(JComponent component, Point2D pt);
    
    /**
     * 指定したコンポーネントのある点を選択範囲の終了点にします。
     * @param component コンポーネント
     * @param pt 終了点。第1引数で指定したコンポーネントの座標系で指定します。
     */
    public void selectTo(JComponent component, Point2D pt);
    
    /**
     * マウスドラッグによる範囲選択を開始します。
     * @param component
     * @param pt
     */
    public void beginDragging(JComponent component, Point2D pt);
    
    /**
     * 選択範囲があるかどうかを返します。
     * @return 選択範囲があれば true を返します。
     */
    public boolean isSelected();
}
