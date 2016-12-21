package com.seatavdisor.parser.xml.builder;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import java.util.logging.Logger;

import com.seatavdisor.parser.xml.api.XMLReadState;
import com.seatavdisor.parser.xml.model.Element;

/**
 * Uses builder pattern to create an <code>Element</code>
 * @author drem
 *
 */
public class ElementBuilder {
	private static Logger logger = Logger.getLogger(ElementBuilder.class.getName());
	public String tagName; // The name of the tag being built
	public String namespace; // The namespace prefix if any
	public String text; // The text in between tags
	public Map<String, String> attributes; // Attributes found 
	public Collection<Element> elements = new Vector<Element>(); // Sub elements
	private StringBuilder textBuilder = new StringBuilder();
	private StringBuilder etag = new StringBuilder();
	private String attributeKey; // The key of an attribute found
	private String attributeValue; // The value belonging to the key found
	private XMLReadState currentState = XMLReadState.NEW_ELEMENT; // The current state of reading the XML
	private int depth = 0; // Depth of nested elements
	private int doctypeTags = 0; // Keeps track of nested Doctype elements
	private int currentQuote = '"'; // The type of quote being processed
	private Stack<XMLReadState> readStack = new Stack<XMLReadState>(); // Stores previous read states
	private Stack<ElementBuilder> elementStack = new Stack<ElementBuilder>(); // Stores previous Elements being built
	
	public ElementBuilder() {
	
	}
	
	public ElementBuilder buildAttributeKey(int charRead) {
		logger.info("Building attribute key");
		if (Character.isWhitespace((char) charRead)) {
			logger.info("-- Found whitespace");
			attributeKey = textBuilder.toString();
			textBuilder.setLength(0);
			currentState = XMLReadState.ATTRIBUTE_EQUALS;
		} else if (charRead == '=') {
			logger.info("-- Found equals");
			attributeKey = textBuilder.toString();
			textBuilder.setLength(0);
			currentState = XMLReadState.ATTRIBUTE_VALUE;
		} else {
			textBuilder.append((char)charRead);
		}
		
		return this;
	}
	
	public ElementBuilder buildAttributeEquals(int charRead) throws ParseException {
		logger.info("Building attribute equals sign");
		if (charRead == '=') {
			logger.info("-- Found equals");
			currentState = XMLReadState.ATTRIBUTE_VALUE;
		} else if (!Character.isWhitespace((char)charRead)) {
			logger.info("-- Found whitespace");
			throw new ParseException("Could not parse attribute. Invalid character found when should be space or equals: " + (char)charRead, 0);
		}
		
		return this;
	}
	
	public ElementBuilder buildAttributeValue(int charRead) throws ParseException {
		logger.info("Building attribute value");
		if (charRead == '"') { // Double quote
			logger.info("-- Found double quote");
			currentQuote = '"';
			currentState = XMLReadState.QUOTE;
		} else if (charRead == '\'') { // Single quote
			logger.info("-- Found single quote");
			currentQuote = '\'';
			currentState = XMLReadState.QUOTE;
		} else if (!Character.isWhitespace((char)charRead)) { 
			logger.info("-- Found whitespace");
			throw new ParseException("Could not parse attribute. Invalid character found when should be space or quote!", 0);
		}
		
		return this;
	}
	
	public ElementBuilder buildCData(int charRead) {
		logger.info("Building CDATA");
		if (charRead == '>' && textBuilder.toString().endsWith("]]")) {
			logger.info("-- Found end of CDATA");
			textBuilder.setLength(textBuilder.length() - 2);
			text = textBuilder.toString();
			textBuilder.setLength(0);
			
			currentState = pop(readStack);
		} else {
			textBuilder.append((char) charRead);
		}
		
		return this;
	}
	
	public ElementBuilder buildCloseTag(int charRead) {
		logger.info("Building close tag");
		if (charRead == '>') {
			logger.info("-- Found end of close tag");
			// End of the end tag was found. This completes this tag.
			tagName = textBuilder.toString();
			textBuilder.setLength(0);
			currentState = pop(readStack);
			
			ElementBuilder previousBuilder = elementStack.pop();
			if (tagName.equals(previousBuilder.tagName)) {
				attributes = previousBuilder.attributes;
			}
			Element element = new Element(this);
			previousBuilder.elements.add(element);
			depth--;
			if (depth == 0) {
				return this;
			}
			return previousBuilder;
		} else {
			textBuilder.append((char)charRead); // Keep appending the text found
		}
		
		return this;
	}
	
	public ElementBuilder buildCommentTag(int charRead) {
		logger.info("Building comment");
		if (charRead == '>' && textBuilder.toString().endsWith("--")) {
			logger.info("-- Found end of comment");
			textBuilder.setLength(textBuilder.length() - 2);
			text = textBuilder.toString();
			textBuilder.setLength(0);
			currentState = pop(readStack);
		} else { 
			textBuilder.append((char) charRead);
		}
		return this;
	}
	
	public ElementBuilder buildDocType(int charRead) {
		logger.info("Building DocType");
		if (charRead == '>') {
			logger.info("-- Found end of DocType");
			if (doctypeTags == 0) {
				currentState = pop(readStack);
				if (currentState == XMLReadState.TEXT) {
					currentState = XMLReadState.NEW_ELEMENT; // We are done reading text from this component
				}	
			} else {
				doctypeTags--;
			}
			
		} else if (charRead == '<') {
			logger.info("-- Found new DocType nested tag");
			doctypeTags++;
		}
		
		return this;
	}
	
	public ElementBuilder buildEntity(int charRead) throws ParseException {
		logger.info("Building entity");
		if (charRead == ';') {
			currentState = pop(readStack);
			String currentEntity = etag.toString();
			etag.setLength(0);
			if (currentEntity.equals("lt")) {
				textBuilder.append('<');
			} else if (currentEntity.equals("gt")) {
				textBuilder.append('>');
			} else if (currentEntity.equals("amp")) {
				textBuilder.append('&');
			} else if (currentEntity.equals("quot")) {
				textBuilder.append('"');
			} else if (currentEntity.equals("apos")){
				textBuilder.append('\'');
			} else if (currentEntity.startsWith("#x")) {
				textBuilder.append((char) Integer.parseInt(currentEntity.substring(2), 16));
			} else if (currentEntity.charAt(0) == '#') {
				textBuilder.append((char) Integer.parseInt(currentEntity.substring(1)));
			} else {
				throw new ParseException("Unknown entity: " + currentEntity, 0);
			}
		} else {
			textBuilder.append((char) charRead);
		}
		return this;
	}
	
	public ElementBuilder buildInsideTag(int charRead) {
		logger.info("Building inside tag");
		if (charRead == '>') {
			logger.info("-- Found end of tag");
			currentState = pop(readStack);
			depth++;
			elementStack.push(this);
			ElementBuilder b = new ElementBuilder();
			b.currentState = currentState;
			b.elementStack = elementStack;
			b.depth = depth;
			return b;
		} else if (charRead == '/') {
			logger.info("-- Found close of tag");
			currentState = XMLReadState.SINGLE_TAG;
		} else {
			if (!Character.isWhitespace((char) charRead)) {
				logger.info("-- Found whitespace");
				currentState = XMLReadState.ATTRIBUTE_KEY;
				textBuilder.append((char) charRead);	
			}
			
		}
		
		return this;
	}

	public ElementBuilder buildNewElement(int charRead) {
		logger.info("Building new element");
		if (charRead == '<') {  // Starting a tag
			logger.info("-- Found start of new element");
			currentState = XMLReadState.TEXT;
			readStack.push(currentState);
			currentState = XMLReadState.START_TAG;
		}

		return this;
	}
	
	public ElementBuilder buildOpenTag(int charRead) {
		logger.info("Building open tag");
		if (charRead == '>') {
			logger.info("-- Found close of tag");
			if (tagName == null) {
				this.tagName = textBuilder.toString();
				elementStack.push(this);
			}
			
			depth++;
			textBuilder.setLength(0);
			currentState = pop(readStack);
			ElementBuilder b = new ElementBuilder();
			b.currentState = currentState;
			b.elementStack = elementStack;
			b.depth = depth;

			return b;
			
		} else if (charRead == '/') {
			currentState = XMLReadState.SINGLE_TAG;
		} else if (charRead == '-' && textBuilder.toString().equals("!-")) {
			currentState = XMLReadState.COMMENT;
		} else if (charRead == '[' && textBuilder.toString().equals("![CDATA")) {
			currentState = XMLReadState.CDATA;
			textBuilder.setLength(0);
		} else if (charRead == 'E' && textBuilder.toString().equals("!DOCTYP")) {
			currentState = XMLReadState.DOCTYPE;
			textBuilder.setLength(0);
		} else if (Character.isWhitespace((char)charRead)){
			currentState = XMLReadState.INSIDE_TAG;
			tagName = textBuilder.toString();
			textBuilder.setLength(0);
		} else if (charRead == ':') {
			namespace = textBuilder.toString();
			textBuilder.setLength(0);
		} else {
			textBuilder.append((char)charRead);
		}
		return this;
	}
	
	public ElementBuilder buildQuote(int charRead) {
		logger.info("Building quote");
		if (charRead == currentQuote) {
			attributeValue = textBuilder.toString();
			textBuilder.setLength(0);
			attributes.put(attributeKey, attributeValue);
			currentState = XMLReadState.INSIDE_TAG;
		} else if (charRead == '&') {
			readStack.push(currentState);
			currentState = XMLReadState.ENTITY;
			etag.setLength(0);
			
		} else {
			textBuilder.append((char) charRead);
		}
		
		return this;
	}
	
	public ElementBuilder buildSingleTag(int charRead) {
		logger.info("Building single tag");
		if (tagName == null) {
			tagName = textBuilder.toString();
		}
		if (charRead != '>') {
			// Throw exception
		} else {
			if (depth == 0) {
				return this;
			}
			textBuilder.setLength(0);
			tagName = null;
			currentState = pop(readStack);
		}
		return this;
	}

	public ElementBuilder buildStartTag(int charRead) {
		logger.info("Building start tag");
		if (charRead == '/') {
			currentState = XMLReadState.CLOSE_TAG;
		} else if (charRead == '?') {
			pop(readStack);
			currentState = XMLReadState.DOCTYPE;
		} else {
			currentState = XMLReadState.OPEN_TAG;
			tagName = null;
			attributes = new HashMap<String, String>();
			textBuilder.append((char)charRead);
		}
		
		return this;
	}
	
	public ElementBuilder buildText(int charRead) {
		logger.info("Building text");
		if (charRead == '<') {
			readStack.push(currentState);
			currentState = XMLReadState.START_TAG;
			if (textBuilder.length() > 0) {
				this.text = textBuilder.toString();
				textBuilder.setLength(0);
			}
		} else if (charRead == '&') {
			readStack.push(currentState);
			currentState = XMLReadState.ENTITY;
			etag.setLength(0);
		} else {
			textBuilder.append((char) charRead);
		}
		
		return this;
	}
	
	public Element buildElement() throws ParseException {
		return elements.iterator().next();
	}
	
	public XMLReadState getCurrentReadState() {
		return currentState;
	}
	
	private XMLReadState pop(Stack<XMLReadState> st) {
	    if (!st.empty())
	      return st.pop();
	    else
	      return XMLReadState.NEW_ELEMENT;
	  }
}
