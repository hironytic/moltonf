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

package com.hironytic.moltonf.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * アプリケーションのリソースで持っているエンティティを解決する EntityResolver です。
 */
public class ResourceEntityResolver implements EntityResolver {
    /** リソースに持っている DTD */
    private static final Map<String, String> DTD_MAP;
    static {
        DTD_MAP = new HashMap<String, String>();
        DTD_MAP.put("http://jindolf.sourceforge.jp/xml/dtd/bbsArchive-091001.dtd", "bbsArchive-091001.dtd");
    }
    
    /**
     * コンストラクタ
     */
    public ResourceEntityResolver() {
        
    }
    
    /**
     * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
     */
    @Override
    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException, IOException {
        String resourceName = DTD_MAP.get(systemId);
        if (resourceName != null) {
            InputStream istream = ResourceEntityResolver.class.getResourceAsStream("dtd/" + resourceName);
            InputSource source = new InputSource(istream);
            source.setPublicId(publicId);
            source.setSystemId(systemId);
            return source;            
        }
        
        return null;
    }
}
