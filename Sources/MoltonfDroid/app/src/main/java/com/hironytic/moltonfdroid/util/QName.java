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

package com.hironytic.moltonfdroid.util;

/**
 * XML の Qualified Name を表すクラス
 */
public class QName {

    /** デフォルトネームスペース URI */
    private static final String DEFAULT_NAMESPACE_URI = "";
    
    /** デフォルトプレフィックス */
    private static final String DEFAULT_PREFIX = "";
    
    /** ネームスペース URI */
    private String namespaceUri;
    
    /** ローカル名 */
    private String localName;
    
    /** プレフィックス */
    private String prefix;
    
    /**
     * ローカル名から QName を構築します。
     * @param localName ローカル名
     */
    public QName(String localName) {
        this(DEFAULT_NAMESPACE_URI, localName, DEFAULT_PREFIX);
    }
    
    /**
     * ネームスペース URI とローカル名から QName を構築します。
     * @param namespaceUri ネームスペース URI
     * @param localName ローカル名
     */
    public QName(String namespaceUri, String localName) {
        this(namespaceUri, localName, DEFAULT_PREFIX);
    }
    
    /**
     * ネームスペース URI、ローカル名、プレフィックスから QName を構築します。
     * @param namespaceUri ネームスペース URI
     * @param localName ローカル名
     * @param prefix プレフィックス
     */
    public QName(String namespaceUri, String localName, String prefix) {
        this.namespaceUri = namespaceUri;
        this.localName = localName;
        this.prefix = prefix;
    }

    /**
     * ネームスペース URI を返します。
     * @return ネームスペース URI
     */
    public String getNamespaceUri() {
        return namespaceUri;
    }

    /**
     * ローカル名を返します。
     * @return ローカル名
     */
    public String getLocalName() {
        return localName;
    }

    /**
     * プレフィックスを返します。
     * @return プレフィックス
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof QName)) {
            return false;
        }
        
        QName other = (QName)o;
        return SmartUtils.equals(this.namespaceUri, other.namespaceUri) &&
                SmartUtils.equals(this.localName, other.localName);
    }
    
    /**
     * 指定したネームスペース URI、ローカル名と等しければ true を返します。
     * @param namespaceUri ネームスペース URI
     * @param localName ローカル名
     * @return 等しければ true、そうでなければ false
     */
    public boolean equals(String namespaceUri, String localName) {
        return SmartUtils.equals(this.namespaceUri, namespaceUri) &&
        SmartUtils.equals(this.localName, localName);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int code = namespaceUri.hashCode();
        code *= 31;
        code += localName.hashCode();
        return code;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (DEFAULT_NAMESPACE_URI.equals(namespaceUri)) {
            return localName;
        } else if (DEFAULT_PREFIX.equals(prefix)){
            return "{" + namespaceUri + "}" + localName;
        } else {
            return "{" + namespaceUri + "}" + prefix + ":" + localName;
        }
    }
}
