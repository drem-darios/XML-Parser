package com.seatavdisor.parser.xml.impl;


public enum XMLReadState {
	ATTRIBUTE_EQUALS,
	ATTRIBUTE_KEY, 
	ATTRIBUTE_VALUE,
	CDATA, 
	CLOSE_TAG,
	COMMENT,
	DOCTYPE,
	ENTITY,
	INSIDE_TAG,
	NEW_ELEMENT,
	OPEN_TAG,
	QUOTE,
	SINGLE_TAG,
	START_TAG,
	TEXT
}
