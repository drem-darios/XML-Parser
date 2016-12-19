package com.seatavdisor.parser.xml.model;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import com.seatavdisor.parser.xml.impl.XMLReadState;

public class Element {
	private final Map<String, String> attributes;
	private final Collection<Element> elements;
	private final String text;
	private final String tagName;
	private final String namespace;
	
	private Element(ElementBuilder builder) {
		this.tagName = builder.tagName;
		this.namespace = builder.namespace;
		this.text = builder.text;
		this.attributes = builder.attributes;
		this.elements = builder.elements;
	}
	
	
	public Map<String, String> getAttributes() {
		return attributes;
	}
	
	public Collection<Element> getElements() {
		return elements;
	}
	
	public String getText() {
		return text;
	}
	
	public String getTagName() {
		return tagName;
	}
	
	public String getNamespace() {
		return namespace;
	}

	public static class ElementBuilder {
		private String tagName;
		private String namespace;
		private String text;
		private StringBuilder textBuilder = new StringBuilder();
		private StringBuilder etag = new StringBuilder();
		private Map<String, String> attributes;
		private String attributeKey;
		private String attributeValue;
		private Collection<Element> elements = new Vector<Element>();
		private XMLReadState currentState = XMLReadState.NEW_ELEMENT;
		private int depth = 0;
		private int doctypeTags = 0;
		private int currentQuote = '"';
		private Stack<XMLReadState> readStack = new Stack<XMLReadState>();
		private Stack<ElementBuilder> elementStack = new Stack<ElementBuilder>();
		
		public ElementBuilder() {
		
		}
		
		public ElementBuilder buildAttributeKey(int charRead) {
			if (Character.isWhitespace((char) charRead)) {
				attributeKey = textBuilder.toString();
				textBuilder.setLength(0);
				currentState = XMLReadState.ATTRIBUTE_EQUALS;
			} else if (charRead == '=') {
				attributeKey = textBuilder.toString();
				textBuilder.setLength(0);
				currentState = XMLReadState.ATTRIBUTE_VALUE;
			} else {
				textBuilder.append((char)charRead);
			}
			
			return this;
		}
		
		public ElementBuilder buildAttributeEquals(int charRead) throws ParseException {
			if (charRead == '=') {
				currentState = XMLReadState.ATTRIBUTE_VALUE;
			} else if (!Character.isWhitespace((char)charRead)) {
				throw new ParseException("Could not parse attribute. Invalid character found when should be space or equals: " + (char)charRead, 0);
			}
			
			return this;
		}
		
		public ElementBuilder buildAttributeValue(int charRead) throws ParseException {
			if (charRead == '"') { // Double quote
				currentQuote = '"';
				currentState = XMLReadState.QUOTE;
			} else if (charRead == '\'') { // Single quote
				currentQuote = '\'';
				currentState = XMLReadState.QUOTE;
			} else if (!Character.isWhitespace((char)charRead)) { 
				throw new ParseException("Could not parse attribute. Invalid character found when should be space or quote!", 0);
			}
			
			return this;
		}
		
		public ElementBuilder buildCData(int charRead) {
			if (charRead == '>' && textBuilder.toString().endsWith("]]")) {
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
			// Found the end of a tag </blah>
			if (charRead == '>') {
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
			if (charRead == '>' && textBuilder.toString().endsWith("--")) {
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
			if (charRead == '>') {
				if (doctypeTags == 0) {
					currentState = pop(readStack);
					if (currentState == XMLReadState.TEXT) {
						currentState = XMLReadState.NEW_ELEMENT; // We are done reading text from this component
					}	
				} else {
					doctypeTags--;
				}
				
			} else if (charRead == '<') {
				doctypeTags++;
			}
			
			return this;
		}
		
		public ElementBuilder buildEntity(int charRead) throws ParseException {
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
			if (charRead == '>') {
				currentState = pop(readStack);
				depth++;
				elementStack.push(this);
				ElementBuilder b = new ElementBuilder();
				b.currentState = currentState;
				b.elementStack = elementStack;
				b.depth = depth;
				return b;
			} else if (charRead == '/') {
				currentState = XMLReadState.SINGLE_TAG;
			} else {
				if (!Character.isWhitespace((char) charRead)) {
					currentState = XMLReadState.ATTRIBUTE_KEY;
					textBuilder.append((char) charRead);	
				}
				
			}
			
			return this;
		}

		public ElementBuilder buildNewElement(int charRead) {
			if (charRead == '<') {  // Starting a tag
				currentState = XMLReadState.TEXT;
				readStack.push(currentState);
				currentState = XMLReadState.START_TAG;
			}

			return this;
		}
		
		public ElementBuilder buildOpenTag(int charRead) {
			if (charRead == '>') {
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
}
