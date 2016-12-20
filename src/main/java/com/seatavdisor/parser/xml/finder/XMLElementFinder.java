package com.seatavdisor.parser.xml.finder;

import java.util.Vector;

import com.seatavdisor.parser.xml.XMLParser;
import com.seatavdisor.parser.xml.model.Element;

public class XMLElementFinder {

	public static Vector<Element> getXMLTagValues(Element xmlElement, String tagName) {
		Vector<Element> result = new Vector<Element>();
		for (Element element: xmlElement.getElements()) {
			if (element.getTagName().equals(tagName)) {
				result.add(element);
			}
		}
		return result;
	}
	
	public static Vector<String> getXMLTagValues(String xmlString, String tagName) {
		Element xmlElement = XMLParser.getElement(xmlString);
		Vector<String> result = new Vector<String>();
		for (Element element: xmlElement.getElements()) {
			if (element.getTagName().equals(tagName)) {
				if (!element.getElements().isEmpty()) {
					result.add(element.toString());	
				} else {
					result.add(element.getText());
				}
			}
		}
		return result;
	}
}
