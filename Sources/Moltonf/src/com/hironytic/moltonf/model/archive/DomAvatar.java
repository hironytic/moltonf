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

package com.hironytic.moltonf.model.archive;

import java.net.URI;

import org.w3c.dom.Element;

import com.hironytic.moltonf.model.Avatar;

/**
 * 共通アーカイブ基盤用スキーマにおける avatar 要素による Avatar 実装
 */
public class DomAvatar implements Avatar {

    /** DOMツリー上で、このオブジェクトが対象とする avatar 要素 */
    private Element avatarElement;
    
    public DomAvatar(Element avatarElement) {
        this.avatarElement = avatarElement;
    }
    
    /* (non-Javadoc)
     * @see com.hironytic.moltonf.model.Avatar#getAbbreviatedName()
     */
    @Override
    public String getAbbreviatedName() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.hironytic.moltonf.model.Avatar#getAvatarId()
     */
    @Override
    public String getAvatarId() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.hironytic.moltonf.model.Avatar#getFaceIconUri()
     */
    @Override
    public URI getFaceIconUri() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.hironytic.moltonf.model.Avatar#getFullName()
     */
    @Override
    public String getFullName() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.hironytic.moltonf.model.Avatar#getShortName()
     */
    @Override
    public String getShortName() {
        // TODO Auto-generated method stub
        return null;
    }

}
