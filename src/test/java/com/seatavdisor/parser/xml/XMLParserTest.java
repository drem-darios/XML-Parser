package com.seatavdisor.parser.xml;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.seatavdisor.parser.xml.model.Element;

public class XMLParserTest {

	private static final String TEST_FILE = "students.xml";
	private String xmlFile;
	
	@Before
	public void setUp() throws Exception {
		this.xmlFile = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(TEST_FILE),"UTF-8");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testReadXMLFile() {
		String actual = XMLParser.getXMLString(TEST_FILE);
		Assert.assertEquals(xmlFile, actual);
	}
	
	@Test
	public void testXMLParser() {
		Element element = XMLParser.getElement(xmlFile);
		Assert.assertEquals(2, element.getElements().size()); // Two students
		for (Element el: element.getElements()) {
			Assert.assertEquals(3, el.getElements().size()); // name, age, class
		}
	}

}
