package com.seatavdisor.parser.xml.impl;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.Stack;
import java.util.Vector;

import org.apache.commons.io.IOUtils;

import com.seatavdisor.parser.xml.model.Element;
import com.seatavdisor.parser.xml.model.Element.ElementBuilder;

public class XMLParser {
	
	public static Vector getXMLTagValue(String xmlString, String tagName) {
//		Element rootElement = parse(xmlString);
		return null;
	}
	
	
	public static String getXMLString(String fileLocation) {
		String result = null;
		try {
			result = IOUtils.toString(XMLParser.class.getClassLoader().getResourceAsStream(fileLocation),"UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static Element getElement(String xmlString) throws IOException, ParseException {
		StringReader reader = new StringReader(xmlString);
		int charRead = 0;
		boolean eol = false;
		ElementBuilder result = new Element.ElementBuilder();
		while((charRead = reader.read()) != -1) {
			
			if (charRead == '\n' && eol) {
				eol = false;
				continue;
			} else if (eol) {
				eol = false;
			} else if (charRead == '\r') {
				charRead = '\n';
				eol = true;
			}
			
			switch(result.getCurrentReadState()) {
			case ATTRIBUTE_EQUALS:
				result = result.buildAttributeEquals(charRead);
				break;
			case ATTRIBUTE_KEY:
				result = result.buildAttributeKey(charRead);
				break;
			case ATTRIBUTE_VALUE:
				result = result.buildAttributeValue(charRead);
				break;
			case CDATA:
				result = result.buildCData(charRead);
				break;
			case CLOSE_TAG:
				result = result.buildCloseTag(charRead);
				break;
			case COMMENT:
				result = result.buildCommentTag(charRead);
				break;
			case DOCTYPE:
				result = result.buildDocType(charRead);
				break;
			case ENTITY:
				result = result.buildEntity(charRead);
				break;
			case INSIDE_TAG:
				result = result.buildInsideTag(charRead);
				break;
			case NEW_ELEMENT:
				result = result.buildNewElement(charRead);
				break;
			case OPEN_TAG:
				result = result.buildOpenTag(charRead);
				break;
			case QUOTE:
				result = result.buildQuote(charRead);
				break;
			case SINGLE_TAG:
				result = result.buildSingleTag(charRead);
				break;
			case START_TAG:
				result = result.buildStartTag(charRead);
				break;
			case TEXT:
				result = result.buildText(charRead);
				break;
			}
		}
		return result.buildElement();
			
	}
	
}
