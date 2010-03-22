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

package com.hironytic.moltonf.util;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * DOM 関連のユーティリティ
 */
public class DomUtils {
    private DomUtils() {
    }

    /**
     * 指定した名前空間 URI、ローカル名に一致する兄弟要素を前方方向へ探します。
     * @param node 探し始める開始点となるノード。条件を満たせばこのノードが返ることもあります。
     * @param namespaceUri 名前空間 URI
     * @param localName ローカル名
     * @return 見つかった要素。見つからなければ null。
     */
    public static Element searchSiblingElementForward(Node node, String namespaceUri, String localName) {
        while (node != null) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (isMatchNode(node, namespaceUri, localName)) {
                    break;
                }
            }
            node = node.getNextSibling();
        }
        return (Element)node;
    }
    
    /**
     * 指定した名前空間 URI、ローカル名に一致するかどうかを返します。
     * @param node 判定したいノード
     * @param namespaceUri 名前空間 URI
     * @param localName ローカル名
     * @return 一致するなら true、そうでなければ false
     */
    public static boolean isMatchNode(Node node, String namespaceUri, String localName) {
        if (SmartUtils.equals(node.getNamespaceURI(), namespaceUri) &&
                SmartUtils.equals(node.getLocalName(), localName)) {
            return true;
        } else {
            return false;
        }
    }
}
