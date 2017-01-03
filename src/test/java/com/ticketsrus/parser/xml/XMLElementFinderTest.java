package com.ticketsrus.parser.xml;

import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ticketsrus.parser.xml.finder.XMLElementFinder;

public class XMLElementFinderTest {

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
	public void testGetXMLTagValues() {
		Vector<String> v = XMLElementFinder.getXMLTagValues(this.xml, "STUDENT");
		Assert.assertEquals(2, v.size());
		Vector<String> ageV = XMLElementFinder.getXMLTagValues(v.elementAt(0), "AGE");
		Assert.assertEquals("19", ageV.elementAt(0));
		System.out.println("Age is  : " + ageV.elementAt(0));
	}
	
}
