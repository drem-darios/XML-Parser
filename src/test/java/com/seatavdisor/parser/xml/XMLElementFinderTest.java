package com.seatavdisor.parser.xml;

import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.seatavdisor.parser.xml.XMLParser;
import com.seatavdisor.parser.xml.model.Element;
import com.seatavdisor.parser.xml.query.XMLElementFinder;

public class XMLElementFinderTest {

	private static final String TEST_FILE = "bookstore.xml";
	private String xml;
	
	@Before
	public void setUp() throws Exception {
		this.xml = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(TEST_FILE),"UTF-8");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetXMLTagValues() {
		String expectedTagName = "";
		
//		Vector<Element> elements = XMLElementFinder.getXMLTagValues(xmlString, tagName);
//		for (Element element: elements) {
//			
//		}
	}
	
	@Test
	public void testXMLParser() {
		String xmlFile = XMLParser.getXMLString(TEST_FILE);
		try {
			Element element = XMLParser.getElement(xmlFile);
			// TODO: Add more test content here!
			System.out.println(element);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
