package com.aliao.litepal.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by ��˫ on 2015/6/9.
 */
public class LitePalContentHandler  extends DefaultHandler {

    private LitePalAttr litePalAttr;

    @Override
    public void startDocument() throws SAXException {
        litePalAttr = LitePalAttr.getIntance();
        litePalAttr.getClassNames().clear();//��ΪlitePalAttr�Ǿ�̬�ģ���������ٴν��н���Ҫ���֮ǰ������
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if ("dbname".equalsIgnoreCase(localName)){
            for (int i = 0; i<attributes.getLength(); i++){
                if ("value".equalsIgnoreCase(attributes.getLocalName(i))){
                    litePalAttr.setDbName(attributes.getValue(i).trim());
                }
            }
        }else if ("version".equalsIgnoreCase(localName)){
            for (int i = 0; i<attributes.getLength();i++){
                if ("value".equalsIgnoreCase(attributes.getLocalName(i))){
                    litePalAttr.setVersion(Integer.parseInt(attributes.getValue(i).trim()));
                }
            }
        }else if ("mapping".equalsIgnoreCase(localName)){
            for (int i = 0; i<attributes.getLength();i++){
                if ("class".equalsIgnoreCase(attributes.getLocalName(i))){
                    litePalAttr.addClassName(attributes.getValue(i).trim());
                }
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
    }
}
