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

package com.hironytic.moltonf.profile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 外部データ取得クラス
 * 取得したデータはファイルにキャッシュして再利用します。
 */
public class ExternalDataRetriever {

    /** 取得したデータをキャッシュとしておくフォルダの頂点 */
    private File cacheRoot;
    
    /**
     * コンストラクタ
     * @param cacheRoot 取得したデータをキャッシュしておくフォルダの頂点
     */
    public ExternalDataRetriever(File cacheRoot) {
        this.cacheRoot = cacheRoot;
    }
    
    /**
     * 外部データを取得します。
     * @param url 外部データの URL
     * @return 取得した外部データの入力ストリーム
     * @throws IOException 入出力処理でエラーが発生したとき 
     */
    public InputStream retrieve(URL url) throws IOException {
        // TODO:
        return url.openStream();
    }
    
}
