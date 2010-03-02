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

package com.hironytic.moltonf.model;

import java.net.URI;

/**
 * 登場人物のインタフェースです。
 */
public interface Avatar {

    /**
     * 登場人物の識別子を返します。 
     * @return 識別子
     */
    public String getAvatarId();

    /**
     * フルネームを返します。
     * 
     * 例：「楽天家 ゲルト」
     * @return フルネーム 
     */
    public String getFullName();

    /**
     * 短い名前を返します。
     * 
     * 例：「ゲルト」
     * @return 短い名前
     */
    public String getShortName();

    /**
     * 短縮名を返します。
     * 
     * 例：「楽」
     * @return 短縮名
     */
    public String getAbbreviatedName();

    /**
     * 顔アイコン画像のある URI を返します。
     * @return 顔アイコンへの URI
     */
    public URI getFaceIconUri();
}
