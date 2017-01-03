package com.ticketsrus.parser.xml.api;

/**
 * Enumeration of the possible states during reading XML
 * @author drem
 */
public enum XMLReadState {
	ATTRIBUTE_EQUALS, // '='
	ATTRIBUTE_KEY, // 'key'=value
	ATTRIBUTE_VALUE, // key='value'
	CDATA, // ![CDATA
	CLOSE_TAG, // '>'
	COMMENT, // <!
	DOCTYPE, // <!DOCTYPE
	ENTITY, // '&'
	INSIDE_TAG, // <tag 'You Are Here'></tag>
	NEW_ELEMENT, // You Are Here <tag>
	OPEN_TAG, // '<'
	QUOTE, // ' or "
	SINGLE_TAG, // '<tag />'
	START_TAG, // '<'
	TEXT // <tag>'You Are Here'</tag>
}
