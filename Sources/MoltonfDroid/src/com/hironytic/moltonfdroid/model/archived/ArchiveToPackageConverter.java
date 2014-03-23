/*
 * Moltonf
 *
 * Copyright (c) 2013 Hironori Ichimiya <hiron@hironytic.com>
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

package com.hironytic.moltonfdroid.model.archived;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import com.hironytic.moltonfdroid.MoltonfException;
import com.hironytic.moltonfdroid.util.QName;

/**
 * プレイデータアーカイブXMLファイルをパッケージ化されたプレイデータアーカイブXMLファイル群に変換するクラス
 */
public class ArchiveToPackageConverter {
    /** 変換先のパッケージディレクトリ */
    private File packageDir;
    
    /** XmlPullParserFactory */
    private XmlPullParserFactory parserFactory;

    /**
     * 変換を行います。
     * @param archiveFile 変換元のアーカイブXML
     * @param packageDir 変換先のパッケージディレクトリ
     */
    public void convert(File archiveFile, File packageDir) {
        try {
            Reader fileReader = new BufferedReader(new FileReader(archiveFile));
            try {
                convert(fileReader, packageDir);
            } finally {
                try {
                    fileReader.close();
                } catch (IOException ex) {
                    // ignore
                }
            }
        } catch (IOException ex) {
            throw new MoltonfException(ex);
        }
    }

    /**
     * 変換を行います。
     * @param archiveReader 変換元のアーカイブXMLのReader
     * @param packageDir 変換先のパッケージディレクトリ
     */
    public void convert(Reader archiveReader, File packageDir) {
        this.packageDir = packageDir;
        
        if (!packageDir.exists()) {
            if (!packageDir.mkdirs()) {
                throw new MoltonfException("could not make package directiory.");
            }
        }

        try {
            parserFactory = XmlPullParserFactory.newInstance();
            parserFactory.setValidating(false);
            parserFactory.setNamespaceAware(true);
            XmlPullParser staxReader = parserFactory.newPullParser();
            staxReader.setInput(archiveReader);
            for (int eventType = staxReader.next(); eventType != XmlPullParser.END_DOCUMENT; eventType = staxReader.next()) {
                if (eventType == XmlPullParser.START_TAG) {
                    QName elemName = new QName(staxReader.getNamespace(), staxReader.getName());
                    if (SchemaConstants.NAME_VILLAGE.contains(elemName)) {
                        convertVillageElement(staxReader);
                    } else {
                        throw new MoltonfException("Not a bbs play-data archive.");
                    }
                }
            }
        } catch (XmlPullParserException ex) {
            throw new MoltonfException(ex);
        } catch (FileNotFoundException ex) {
            throw new MoltonfException(ex);
        } catch (IOException ex) {
            throw new MoltonfException(ex);
        }
    }
    
    /**
     * village 要素以下を変換します。
     * このメソッドが呼ばれたとき staxReader は village 要素の START_TAG にいることが前提です。
     * @param staxReader XML パーサ
     * @throws XmlPullParserException 読み込み中にエラーが発生した場合
     * @throws IOException 読み込み中にエラーが発生した場合
     */
    private void convertVillageElement(XmlPullParser staxReader) throws XmlPullParserException, IOException {
        // village.xmlを準備する
        File villageFile = new File(packageDir, PackageConstants.FILENAME_VILLAGE);
        Writer fileWriter = new BufferedWriter(new FileWriter(villageFile));
        XmlSerializer villageSerializer = parserFactory.newSerializer();
        villageSerializer.setOutput(fileWriter);
        
        villageSerializer.startDocument("UTF-8", true);

        String namespace = staxReader.getNamespace();
        String name = staxReader.getName();
        
        convertCurrentNamespacePrefix(staxReader, villageSerializer);
        villageSerializer.startTag(namespace, name);
        convertCurrentElementAttribute(staxReader, villageSerializer);
        
        // 子ノード
        for (int eventType = staxReader.next(); eventType != XmlPullParser.END_DOCUMENT; eventType = staxReader.next()) {
            if (eventType == XmlPullParser.END_TAG) {
                break;
            } else if (eventType == XmlPullParser.START_TAG) {
                QName elemName = new QName(staxReader.getNamespace(), staxReader.getName());
                if (SchemaConstants.NAME_PERIOD.contains(elemName)) {
                    convertPeriodElement(staxReader, villageSerializer);
                } else {
                    convertGeneralElement(staxReader, villageSerializer);
                }
            }
        }
        
        villageSerializer.endTag(namespace, name);
        villageSerializer.endDocument();
    }

    /**
     * period要素以下を変換します。
     * このメソッドが呼ばれたとき staxReader は period 要素の START_TAG にいることが前提です。
     * @param staxReader XML パーサ
     * @param villageSerializer village.xml用シリアライザ
     * @throws XmlPullParserException 読み込み中にエラーが発生した場合
     * @throws IOException 読み込み中にエラーが発生した場合
     */
    private void convertPeriodElement(XmlPullParser staxReader, XmlSerializer villageSerializer) throws XmlPullParserException, IOException {
        String dayString = staxReader.getAttributeValue(SchemaConstants.NAME_DAY.getNamespaceUri(), SchemaConstants.NAME_DAY.getLocalName());
        int day = Integer.parseInt(dayString);
        String periodFileName = String.format(Locale.US, PackageConstants.FILENAME_PERIOD_FMT, day);
        File periodFile = new File(packageDir, periodFileName);
        Writer fileWriter = new BufferedWriter(new FileWriter(periodFile));
        XmlSerializer periodSerializer = parserFactory.newSerializer();
        periodSerializer.setOutput(fileWriter);
        
        periodSerializer.startDocument("UTF-8", true);

        String namespace = staxReader.getNamespace();
        String name = staxReader.getName();

        convertCurrentNamespacePrefix(staxReader, villageSerializer);
        villageSerializer.startTag(namespace, name);
        convertCurrentElementAttribute(staxReader, villageSerializer);
        villageSerializer.attribute(SchemaConstants.NAME_XLINK_TYPE.getNamespaceUri(), SchemaConstants.NAME_XLINK_TYPE.getLocalName(), SchemaConstants.VAL_XLINK_TYPE_SIMPLE);
        villageSerializer.attribute(SchemaConstants.NAME_XLINK_HREF.getNamespaceUri(), SchemaConstants.NAME_XLINK_HREF.getLocalName(), periodFileName);
        
        int nsEnd = staxReader.getNamespaceCount(staxReader.getDepth());
        for (int ix = 0; ix < nsEnd; ++ix) {
           String prefix = staxReader.getNamespacePrefix(ix);
           String namespaceURI = staxReader.getNamespaceUri(ix);
           periodSerializer.setPrefix((prefix == null) ? "" : prefix, namespaceURI);
        }
        periodSerializer.startTag(namespace, name);
        convertCurrentElementAttribute(staxReader, periodSerializer);
        
        // 子ノード
        for (int eventType = staxReader.next(); eventType != XmlPullParser.END_DOCUMENT; eventType = staxReader.next()) {
            if (eventType == XmlPullParser.END_TAG) {
                break;
            } else if (eventType == XmlPullParser.START_TAG) {
                convertGeneralElement(staxReader, periodSerializer);
            }
        }
        
        villageSerializer.endTag(namespace, name);
        periodSerializer.endTag(namespace, name);
        
        periodSerializer.endDocument();
    }
    
    /**
     * 一般的な要素とその子供をコンバートします。
     * @param staxReader XML パーサ
     * @param serializer XML シリアライザ
     * @throws XmlPullParserException 読み込み中にエラーが発生した場合
     * @throws IOException 読み込み中にエラーが発生した場合
     */
    private void convertGeneralElement(XmlPullParser staxReader, XmlSerializer serializer) throws XmlPullParserException, IOException {
        String namespace = staxReader.getNamespace();
        String name = staxReader.getName();
        
        convertCurrentNamespacePrefix(staxReader, serializer);
        serializer.startTag(namespace, name);
        convertCurrentElementAttribute(staxReader, serializer);
        
        // 子ノード
        for (int eventType = staxReader.next(); eventType != XmlPullParser.END_DOCUMENT; eventType = staxReader.next()) {
            if (eventType == XmlPullParser.END_TAG) {
                break;
            } else if (eventType == XmlPullParser.START_TAG) {
                convertGeneralElement(staxReader, serializer);
            } else if (eventType == XmlPullParser.TEXT) {
                serializer.text(staxReader.getText());
            } else if (eventType == XmlPullParser.CDSECT) {
                serializer.cdsect(staxReader.getText());
            } else if (eventType == XmlPullParser.ENTITY_REF) {
                serializer.entityRef(staxReader.getName());
            } else if (eventType == XmlPullParser.IGNORABLE_WHITESPACE) {
                serializer.ignorableWhitespace(staxReader.getText());
            } else if (eventType == XmlPullParser.PROCESSING_INSTRUCTION) {
                serializer.processingInstruction(staxReader.getText());
            } else if (eventType == XmlPullParser.COMMENT) {
                serializer.comment(staxReader.getText());
            }
        }
        
        serializer.endTag(namespace, name);
    }
    
    /**
     * 現在の要素についたネームスペース接頭辞をコンバートします。
     * @param staxReader XML パーサ
     * @param serializer XML シリアライザ
     * @throws XmlPullParserException 読み込み中にエラーが発生した場合
     * @throws IOException 読み込み中にエラーが発生した場合
     */
    private void convertCurrentNamespacePrefix(XmlPullParser staxReader, XmlSerializer serializer) throws XmlPullParserException, IOException {
        int nsStart = staxReader.getNamespaceCount(staxReader.getDepth() - 1);
        int nsEnd = staxReader.getNamespaceCount(staxReader.getDepth());
        for (int ix = nsStart; ix < nsEnd; ++ix) {
           String prefix = staxReader.getNamespacePrefix(ix);
           String namespaceURI = staxReader.getNamespaceUri(ix);
           serializer.setPrefix((prefix == null) ? "" : prefix, namespaceURI);
        }
    }
    
    /**
     * 現在の要素についた属性をコンバートします。 
     * @param staxReader XML パーサ
     * @param serializer XML シリアライザ
     * @throws XmlPullParserException 読み込み中にエラーが発生した場合
     * @throws IOException 読み込み中にエラーが発生した場合
     */
    private void convertCurrentElementAttribute(XmlPullParser staxReader, XmlSerializer serializer) throws XmlPullParserException, IOException {
        // 属性
        for (int ix = 0; ix < staxReader.getAttributeCount(); ++ix) {
            String namespace = staxReader.getAttributeNamespace(ix);
            String name = staxReader.getAttributeName(ix);
            String value = staxReader.getAttributeValue(ix);
            serializer.attribute(namespace, name, value);
        }
    }
}
