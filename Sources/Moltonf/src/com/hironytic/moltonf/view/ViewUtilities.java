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

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.SwingUtilities;

/**
 * ビューで用いるユーティリティ群
 */
public class ViewUtilities {
    /**
     * source 座標体系の aPoint を destination  座標体系に変換します。
     * @param source
     * @param aPoint
     * @param destination
     * @return
     */
    public static Point2D convertPoint(Component source, Point2D aPoint, Component destination) {
        return SwingUtilities.convertPoint(source, (int)aPoint.getX(), (int)aPoint.getY(), destination);
    }
    
    /** 範囲選択のための動作 */
    public enum RangeSelectionGesture {
        /** 範囲選択のための動作ではない */
        NONE,
        
        /** 選択の開始位置をセット */
        SET_START_POSITION,
        
        /** 選択の終了位置をセット */
        SET_END_POSITION,
    }
    
    /**
     * マウスイベントに対応する範囲選択のための動作があれば返します。
     * @param event マウスイベント
     * @return 範囲選択のための動作。動作がなければ RangeSelectionGesture.NONE が返ります。
     */
    public static RangeSelectionGesture getRangeSelectionGesture(MouseEvent event) {
        int eventId = event.getID();
        if (eventId == MouseEvent.MOUSE_CLICKED) {
            if (event.getButton() == MouseEvent.BUTTON1) {
                if (event.isShiftDown()) {
                    return RangeSelectionGesture.SET_END_POSITION;
                } else {
                    return RangeSelectionGesture.SET_START_POSITION;
                }
            }
        }
        return RangeSelectionGesture.NONE;
    }
}
