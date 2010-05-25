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

/**
 * メッセージの一部の範囲を表すクラス
 */
public class MessageRange {

    /** 何行目のメッセージか */
    private int lineIndex = -1;
    
    /** 行内の開始インデックス(このインデックスの文字は範囲に含む) */
    private int start = 0;
    
    /** 行内の終了インデックス(このインデックスの文字は範囲に含まない) */
    private int end = 0;
    
    /**
     * デフォルトコンストラクタ
     */
    public MessageRange() {
    }
    
    /**
     * 位置を指定するコンストラクタ
     * @param lineIndex 何行目に対する情報か
     * @param start 行内の開始インデックス
     * @param end 行内の終了インデックス
     */
    public MessageRange(int lineIndex, int start, int end) {
        this.lineIndex = lineIndex;
        this.start = start;
        this.end = end;
    }

    /**
     * 何行目かを取得します。
     * @return lineIndex を返します。
     */
    public int getLineIndex() {
        return lineIndex;
    }

    /**
     * 行内の開始インデックスを取得します。
     * このインデックスの文字は範囲に含みます。
     * @return 開始インデックスを返します。
     */
    public int getStart() {
        return start;
    }

    /**
     * 行内の終了インデックスを取得します。
     * このインデックスの文字は範囲に含みません。
     * @return 終了インデックスを返します。
     */
    public int getEnd() {
        return end;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + end;
        result = prime * result + lineIndex;
        result = prime * result + start;
        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MessageRange other = (MessageRange) obj;
        if (end != other.end)
            return false;
        if (lineIndex != other.lineIndex)
            return false;
        if (start != other.start)
            return false;
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "MessageRange [L" + lineIndex + ":" + start + ", " + end + "]";
    }
}
