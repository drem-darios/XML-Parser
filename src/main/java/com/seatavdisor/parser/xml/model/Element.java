package com.seatavdisor.parser.xml.model;

import java.util.Collection;
import java.util.Map;

import com.seatavdisor.parser.xml.builder.ElementBuilder;

public class Element {
	private final Map<String, String> attributes;
	private final Collection<Element> elements;
	private final String text;
	private final String tagName;
	private final String namespace;

	public Element(ElementBuilder builder) {
		this(builder.tagName, builder.namespace, builder.text, builder.attributes, builder.elements);
	}

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

}
