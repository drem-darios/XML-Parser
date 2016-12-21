package com.seatavdisor.parser.xml.model;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.seatavdisor.parser.xml.builder.ElementBuilder;

/**
 * An Object that makes up an XML element (i.e <tag>FOOBAR</tag>). Elements can
 * have text as a value or nested children elements (i.e. <a> <b>FOOBAR</b>
 * </a>). Elements can also have attributes (i.e. category is an attribute in
 * <book category="fiction">Harry Potter</book>)
 * 
 * @author drem
 *
 */
public class Element {
	private final Map<String, String> attributes;
	private final Collection<Element> elements;
	private final String text;
	private final String tagName;
	private final String namespace;

	/**
	 * Uses an <code>ElementBuilder</code> to initialize fields
	 */
	public Element(ElementBuilder builder) {
		this(builder.tagName, builder.namespace, builder.text, builder.attributes, builder.elements);
	}

	/**
	 * Creates a new <code>Element</code> with the information provided
	 */
	public Element(String tagName, String namespace, String text, Map<String, String> attributes,
			Collection<Element> elements) {
		this.tagName = tagName;
		this.namespace = namespace;
		this.text = text;
		this.attributes = attributes;
		this.elements = elements;
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

	/**
	 * Converts this <code>Element</code> into its original XML String representation.
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("<");
		if (StringUtils.isNotEmpty(namespace)) { // Namespace
			result.append(namespace);
			result.append(':');
		}
		result.append(this.tagName);

		if (!this.attributes.isEmpty()) { // Attributes
			result.append(' ');
			for (Entry<String, String> entries : this.attributes.entrySet()) {
				result.append(entries.getKey());
				result.append("=");
				result.append("\"");
				result.append(entries.getValue());
				result.append("\"");
			}
		}
		result.append('>');
		if (!this.elements.isEmpty()) {
			for (Element element : elements) { // Subelements
				result.append('\n');
				result.append(element.toString());
			}
		}
		result.append(this.text); // Text

		// Close tag
		result.append('<');
		result.append('/');
		if (StringUtils.isNotEmpty(namespace)) { // Namespace
			result.append(namespace);
			result.append(':');
		}
		result.append(this.tagName);
		result.append('>');
		return result.toString();
	}

}
