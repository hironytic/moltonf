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

package com.hironytic.moltonf;

import com.hironytic.moltonf.controller.MoltonfController;

/**
 * Moltonf アプリケーションのスタートアップクラス
 */
public class Moltonf {

    /** 唯一のコントローラーインスタンス */
    private static MoltonfController controler;
    
    private Moltonf() {
    }

    /**
     * アプリケーションのエントリポイント
     * @param args アプリケーションの引数
     */
    public static void main(String[] args) {
        controler = new MoltonfController();
        controler.run(args);
    }

    /**
     * コントローラーのインスタンスを得ます。
     * @return MoltonfController クラスのインスタンス
     */
    public static MoltonfController getController() {
        return controler;
    }
}
