package com.seatavdisor.parser.xml;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.seatavdisor.parser.xml.model.Element;

public class XMLParserTest {

	private static final String TEST_FILE = "students.xml";
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
