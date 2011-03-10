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

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * XML関連のユーティリティ
 */
public class XmlUtils {
    
    private XmlUtils() {
    }
    
    /**
     * XmlPullParser において、現在の要素以下をスキップします
     * @throws XmlPullParserException 読み込み中にエラーが発生した場合
     * @throws IOException 読み込み中にエラーが発生した場合
     */
    public static void skipElement(XmlPullParser staxReader) throws XmlPullParserException, IOException {
        for (int eventType = staxReader.next(); eventType != XmlPullParser.END_DOCUMENT; eventType = staxReader.next()) {
            if (eventType == XmlPullParser.END_TAG) {
                break;
            } else if (eventType == XmlPullParser.START_TAG) {
                skipElement(staxReader);
            }
        }
    }
    
    /**
     * xsd:time 型の時刻文字列を解析します。
     * @param timeString 時刻文字列
     * @return 解析結果を格納した TimePart オブジェクト
     */
    public static TimePart parseTime(String timeString) {
// DatatypeFactory は Level-8 の API なので使えない。
//        if (datatypeFactory == null) {
//            try {
//                datatypeFactory = DatatypeFactory.newInstance();
//            } catch (DatatypeConfigurationException ex) {
//                throw new MoltonfException(ex);
//            }
//        }
//        
//        XMLGregorianCalendar xmlCalendar;
//        try {
//            xmlCalendar = datatypeFactory.newXMLGregorianCalendar(timeString);
//        } catch (IllegalArgumentException ex) {
//            return null;
//        }
//        
//        int hour = xmlCalendar.getHour();
//        if (hour == DatatypeConstants.FIELD_UNDEFINED) {
//            hour = 0;
//        }
//        int minute = xmlCalendar.getMinute();
//        if (minute == DatatypeConstants.FIELD_UNDEFINED) {
//            minute = 0;
//        }
//        int second = xmlCalendar.getSecond();
//        if (second == DatatypeConstants.FIELD_UNDEFINED) {
//            second = 0;
//        }
//        int millisecond = xmlCalendar.getMillisecond();
//        if (millisecond == DatatypeConstants.FIELD_UNDEFINED) {
//            millisecond = 0;
//        }
//        return new TimePart(hour, minute, second, millisecond);
        
        // ある程度適当に解析
        // hh ':' mm ':' ss ('.' s+)? (zzzzzz)?
        //        -- see http://www.w3.org/TR/xmlschema-2/#dateTime
        if (timeString.length() < 8) {
            return null;
        }
        try {
            int hour = Integer.parseInt(timeString.substring(0, 2));
            int minute = Integer.parseInt(timeString.substring(3, 5));
            int second = Integer.parseInt(timeString.substring(6, 8));
            int millisecond = 0;
            if (timeString.length() > 8 && timeString.charAt(8) == '.') {
                int ix = 9;
                for (int factor = 1000; factor > 0; factor /= 10) {
                    if (timeString.length() < ix) {
                        break;
                    }
                    char ch = timeString.charAt(ix);
                    if (ch < '0' || ch > '9') {
                        break;
                    }
                    millisecond += (int)(ch - '0') * factor;
                }
            }
            return new TimePart(hour, minute, second, millisecond);
        } catch (NumberFormatException ex) {
            return null;
        }
    }    
}
