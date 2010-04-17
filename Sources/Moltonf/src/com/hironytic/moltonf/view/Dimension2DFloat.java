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

import java.awt.geom.Dimension2D;

/**
 * float 精度の Dimension2D クラス。 
 */
public class Dimension2DFloat extends Dimension2D {

    /** 幅 */
    public float width;
    
    /** 高さ */
    public float height;

    /**
     * 幅・高さを 0 で初期化するコンストラクタ
     */
    public Dimension2DFloat() {
        this(0f, 0f);
    }
    
    /**
     * 幅と高さを指定したコンストラクタ
     * @param width 幅
     * @param height 高さ
     */
    public Dimension2DFloat(float width, float height) {
        this.width = width;
        this.height = height;
    }
    
    /**
     * @see java.awt.geom.Dimension2D#getHeight()
     */
    @Override
    public double getHeight() {
        return height;
    }

    /**
     * @see java.awt.geom.Dimension2D#getWidth()
     */
    @Override
    public double getWidth() {
        return width;
    }

    /**
     * @see java.awt.geom.Dimension2D#setSize(double, double)
     */
    @Override
    public void setSize(double width, double height) {
        this.width = (float)width;
        this.height = (float)height;
    }
}
