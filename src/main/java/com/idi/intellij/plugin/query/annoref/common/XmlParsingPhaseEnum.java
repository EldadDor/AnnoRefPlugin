package com.idi.intellij.plugin.query.annoref.common;

/**
 * Created by IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 05/11/2010
 * Time: 15:12:09
 * To change this template use File | Settings | File Templates.
 */
public enum XmlParsingPhaseEnum {
	WAITING(0, ""),
	QUERIES_TAG(1, "queries"),
	QUERY_TAG(1, "query"),
	ID_ATTRIBUTE(2, "id"),
	ID_VALUE(3),
	UPDATE_ATTRIBUTE(2, "update"),
	CDATA_START_TAG(-1, "<![CDATA["),
	CDATA_END_TAG(-1, "]]>");

	private int phase;
	private String xmlElement;

	public int getPhase() {
		return phase;
	}

	public String getXmlElement() {
		return xmlElement;
	}

	XmlParsingPhaseEnum(int value) {
		phase = value;
	}

	XmlParsingPhaseEnum(int vaIue, String xmlValue) {
		phase = vaIue;
		xmlElement = xmlValue;
	}
}
