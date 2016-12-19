package com.seatavdisor.parser.xml;

import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.seatavdisor.parser.xml.impl.XMLParser;
import com.seatavdisor.parser.xml.model.Element;

public class XMLParserTest {

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
	public void testReadXMLFile() {
		System.out.println(xml);
		String actual = XMLParser.getXMLString(TEST_FILE);
		Assert.assertEquals(xml, actual);
	}
	
	@Test
	public void testParseStudentElement() {
		String expectedAge = "19";
		int expectedSize = 2;
		
		String xmlFile = XMLParser.getXMLString(TEST_FILE);
		Vector studentVector = XMLParser.getXMLTagValue(xmlFile, "STUDENT");
		Assert.assertEquals(expectedSize, studentVector.size());
		Vector ageVector = XMLParser.getXMLTagValue((String) studentVector.elementAt(0), "AGE");
		System.out.println("Age is: " + ageVector.elementAt(0));
		Assert.assertEquals(expectedAge, ageVector.elementAt(0));
	}
	
	@Test
	public void testXMLParser() {
		String xmlFile = XMLParser.getXMLString(TEST_FILE);
		try {
			Element element = XMLParser.getElement(xmlFile);
			System.out.println(element);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
