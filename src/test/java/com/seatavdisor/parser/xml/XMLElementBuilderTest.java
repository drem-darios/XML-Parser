package com.seatavdisor.parser.xml;

import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.util.Stack;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.seatavdisor.parser.xml.model.Element;
import com.seatavdisor.parser.xml.model.Element.ElementBuilder;

public class XMLElementBuilderTest {

	private static final String TEST_FILE = "students.xml";
	private Reader reader;
	private Stack stack;
	
	@Before
	public void setUp() throws Exception {
		this.reader = new StringReader(IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(TEST_FILE),"UTF-8"));
		this.stack = new Stack();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testXMLParser() throws ParseException {
		ElementBuilder builder = new Element.ElementBuilder();
//		Element result = builder.buildTagName(reader, stack).buildSubElement().buildElement();
//		System.out.println(result);
	}

}
