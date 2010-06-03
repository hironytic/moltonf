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

package com.hironytic.moltonf.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.hironytic.moltonf.MoltonfException;

/**
 * HTTP プロトコルでデータにアクセスするためのクラス。
 */
public class HttpAccess {

    private static final String USER_AGENT = "Moltonf/0.0.0";   // TODO:
    
    /**
     * コンストラクタ
     */
    public HttpAccess() {
    }
    
    /**
     * 指定した URL のデータを GET メソッドで取得するための InputStream を返します。
     * @param url データの URL
     * @return データにアクセスするための InputStream
     * @throws MoltonfException エラー発生時
     */
    public InputStream doGet(URL url) throws MoltonfException {
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection)url.openConnection();
        } catch (IOException ex) {
            throw new MoltonfException("failed to URL#openConnection() : " + url.toString(), ex);
        } catch (ClassCastException ex) {
            throw new MoltonfException("not HTTP protocol? : " + url.toString(), ex);
        }
        
        try {
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setDoInput(true);
            
            connection.connect();
            return connection.getInputStream();
        } catch (IOException ex) {
            throw new MoltonfException("failed to GET request : " + url.toString(), ex);
        }
    }
}
