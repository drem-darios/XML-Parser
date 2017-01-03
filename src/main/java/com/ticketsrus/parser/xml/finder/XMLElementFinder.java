package com.ticketsrus.parser.xml.finder;

import java.util.Vector;
import java.util.logging.Logger;

import com.ticketsrus.parser.xml.XMLParser;
import com.ticketsrus.parser.xml.model.Element;

/**
 * The XMLElementFinder can be used to search an XML document for elements with specific tag names. 
 * @author drem
 *
 */
public class XMLElementFinder {

	private static Logger logger = Logger.getLogger(XMLElementFinder.class.getName());
	
	/**
	 * Returns a <code>Vector</code> of <code>Element</code> Objects with the given tag name.
	 */
	public static Vector<Element> getXMLTagValues(Element xmlElement, String tagName) {
		Vector<Element> result = new Vector<Element>();
		for (Element element: xmlElement.getElements()) {
			if (element.getTagName().equals(tagName)) {
				result.add(element);
			}
		}
		return result;
	}
	
	/**
	 * Returns a <code>Vector</code> of the XML String representation of <code>Element</code> Objects
	 * with the given tag name.
	 */
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
