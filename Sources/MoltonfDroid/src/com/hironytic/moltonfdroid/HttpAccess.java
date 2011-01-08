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

package com.hironytic.moltonfdroid;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.os.Build;

import com.hironytic.moltonfdroid.util.Proc1;

/**
 * HTTP プロトコルでデータにアクセスするためのクラス。
 */
public class HttpAccess {

    private static final String USER_AGENT = "MoltonfDroid/0.0.0";   // TODO:
    
    /**
     * コンストラクタ
     */
    public HttpAccess() {
    }
    
    /**
     * 指定した URI のデータを GET メソッドで取得するための InputStream を返します。
     * @param uri データの URI
     * @param getProc 取得したデータを処理するコールバック
     * @throws MoltonfException エラー発生時
     */
    public static void doGet(URI uri, Proc1<InputStream> getProc) throws MoltonfException {
        HttpGet httpGet = new HttpGet(uri);
        HttpClient httpClient = createHttpClient();
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode / 100 == 2) {
                HttpEntity httpEntity = httpResponse.getEntity();
                if (httpEntity != null) {
                    InputStream inStream = httpEntity.getContent();
                    getProc.perform(inStream);
                    inStream.close();
                }
            } else {
                Moltonf.getLogger().info("HTTP GET error : " + statusLine.toString());
            }
        } catch (IOException ex) {
            httpGet.abort();
            throw new MoltonfException("failed to HTTP GET", ex);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }
    
    /**
     * HttpClient を生成します。
     * @return HttpClient
     */
    private static HttpClient createHttpClient() {
        HttpClient httpClient = null;
        if (Build.VERSION.SDK_INT >= 8) {
            try {
                Class<?> clazz = Class.forName("android.net.http.AndroidHttpClient");
                Method method = clazz.getMethod("newInstance", new Class[]{String.class});
                httpClient = (HttpClient)method.invoke(null, new Object[]{USER_AGENT});
                HttpParams params = httpClient.getParams();
                HttpClientParams.setRedirecting(params, true);  // リダイレクトを follow するようにしておく            
            } catch (Exception ex) {
            }
        }
        
        if (httpClient == null) {
            httpClient = new DefaultHttpClient();
            HttpParams params = httpClient.getParams();
            HttpProtocolParams.setUserAgent(params, USER_AGENT);
            
            // 以下の設定は AndroidHttpClient に合わせてある
            HttpConnectionParams.setStaleCheckingEnabled(params, false);
            HttpConnectionParams.setConnectionTimeout(params, 20 * 1000);
            HttpConnectionParams.setSoTimeout(params, 20 * 1000);
            HttpConnectionParams.setSocketBufferSize(params, 8192);
        }
        return httpClient;
    }
}
