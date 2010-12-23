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

package com.hironytic.moltonf.util;

import java.util.logging.Level;

/**
 * ログ出力用オブジェクト
 */
public class Logger {

    /** 実際にログ出力を行うオブジェクト */
    private java.util.logging.Logger logger;
    
    /**
     * コンストラクタ
     * @param ロガー名
     */
    public Logger(String name) {
        this.logger = java.util.logging.Logger.getLogger(name);
    }

    /**
     * 情報メッセージをログ出力します。
     * @param message メッセージ
     */
    public void info(String message) {
        logger.info(message);
    }
    
    /**
     * Throwable を伴った情報メッセージをログ出力します。
     * @param message メッセージ
     * @param thrown Throwable オブジェクト
     */
    public void info(String message, Throwable thrown) {
        logger.log(Level.INFO, message, thrown);
    }
    
    /**
     * 警告メッセージをログ出力します。
     * @param message メッセージ
     */
    public void warning(String message) {
        logger.warning(message);
    }
    
    /**
     * Throwable を伴った警告メッセージをログ出力します。
     * @param message メッセージ
     * @param thrown Throwable オブジェクト
     */
    public void warning(String message, Throwable thrown) {
        logger.log(Level.WARNING, message, thrown);
    }
    
    /**
     * エラーメッセージをログ出力します。
     * @param message メッセージ
     */
    public void severe(String message) {
        logger.severe(message);
    }
    
    /**
     * Throwable を伴ったエラーメッセージをログ出力します。
     * @param message メッセージ
     * @param thrown Throwable オブジェクト
     */
    public void severe(String message, Throwable thrown) {
        logger.log(Level.SEVERE, message, thrown);
    }
}
