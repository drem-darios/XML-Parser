package com.seatavdisor.parser.xml;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import com.seatavdisor.parser.xml.builder.ElementBuilder;
import com.seatavdisor.parser.xml.model.Element;

/**
 * This class can be used to parse XML into an <code>Element</code> object or simply load an XML file as
 * a String.
 * @author drem
 *
 */
public class XMLParser {
	private static Logger logger = Logger.getLogger(XMLParser.class.getName());

	/**
	 * Gets the XML file at the location provided and returns the String contents of the file or null if
	 * there was a problem reading the file.
	 */
	public static String getXMLString(String fileLocation) {
		String result = null;
		try {
			result = IOUtils.toString(XMLParser.class.getClassLoader().getResourceAsStream(fileLocation), "UTF-8");
		} catch (IOException e) {
			logger.severe(e.getMessage());
		}
		return result;
	}
	
	/**
	 * Creates an <code>Element</code> Object from the valid XML String provided. Returns null if there
	 * was an issue parsing the XML string provided.
	 */
	public static Element getElement(String xmlString) {
		StringReader reader = new StringReader(xmlString);
		int charRead = 0;
		boolean eol = false;
		ElementBuilder result = new ElementBuilder();
		try {
			while((charRead = reader.read()) != -1) {
				
				if (charRead == '\n' && eol) {
					eol = false;
					continue; // Go to next iteration after return character
				} else if (eol) {
					eol = false; // Reset end of line
				} else if (charRead == '\r') {
					charRead = '\n';
					eol = true; // Return character found. This is end of line
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
		
		} catch (IOException e) {
			logger.severe(e.getMessage());
			return null;
		} catch(ParseException e) {
			logger.severe(e.getMessage());
			return null;
		}
			
	}
	
}
