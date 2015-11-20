package com.idi.intellij.plugin.query.annoref.persist;

import com.idi.intellij.plugin.query.annoref.util.TimeUtil;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 6/22/13
 * Time: 9:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class AnnoRefSettings {
	public Boolean ENABLE_SQLREF_FQN_OVERRIDE = false;
	public Boolean ENABLE_AUTO_SYNC = true;
	public Boolean ENABLE_ANNO_SUPER = true;
	public Boolean ENABLE_UTIL_CLASS_SCAN = true;
	public Boolean ENABLE_SQL_TO_MODEL_VALIDATION = false;
	public Boolean DEEP_SCAN_ENABLED = false;

	public String ANNOREF_UTIL_CLASS_FQN = "com.idi.framework.util.jdbc.QueryUtils";
	public String ANNOREF_ANNOTATION_FQN = "com.idi.astro.server.annotation.SQLRef";
	public String ANNO_ANNOTATION_FQN = "com.idi.astro.server.annotation.SQL";
	public String SP_VIEW_ANNOTATION_FQN = "com.idi.astro.server.annotation.SP";
	public String SP_DATA_SOURCE_NAME = "";
	public String ANNOREF_ANNOTATION_ATTRIBUTE_ID = "refId";
	public String XML_ELEMENT_ATTRIBUTE_ID = "id";
	public String ANNO_REF_SUPER_INTERFACE = "com.idi.astro.train.ifc.Query";
	public String XML_SCHEMA_TEXT = "";
	public String ANNO_ALL_SYNTAX_HIGHLIGHT_COLOR = "";
	public Boolean ANNO_ALL_SYNTAX_HIGHLIGHT_ENABLE = false;
	public String VO_PROPERTY_ANNOTATION_FQN = "com.idi.framework.vo.common.vo.annotations.Property";
	public String CONNECTION_METHOD_WAY = "single-connection";
	public Integer REINDEX_INTERVAL = TimeUtil.THREE_MINUTES;
	public String QUERIES_REGEX = "^(.*((-queries)|(-queries.xml))$)";

	@Override
	public String toString() {
		return "AnnoRefSettings{" +
				"ENABLE_SQLREF_FQN_OVERRIDE=" + ENABLE_SQLREF_FQN_OVERRIDE +
				", ENABLE_AUTO_SYNC=" + ENABLE_AUTO_SYNC +
				", ENABLE_ANNO_SUPER=" + ENABLE_ANNO_SUPER +
				", ENABLE_UTIL_CLASS_SCAN=" + ENABLE_UTIL_CLASS_SCAN +
				", ENABLE_SQL_TO_MODEL_VALIDATION=" + ENABLE_SQL_TO_MODEL_VALIDATION +
				", ANNOREF_UTIL_CLASS_FQN='" + ANNOREF_UTIL_CLASS_FQN + '\'' +
				", ANNOREF_ANNOTATION_FQN='" + ANNOREF_ANNOTATION_FQN + '\'' +
				", ANNO_ANNOTATION_FQN='" + ANNO_ANNOTATION_FQN + '\'' +
				", SP_VIEW_ANNOTATION_FQN='" + SP_VIEW_ANNOTATION_FQN + '\'' +
				", SP_DATA_SOURCE_NAME='" + SP_DATA_SOURCE_NAME + '\'' +
				", ANNOREF_ANNOTATION_ATTRIBUTE_ID='" + ANNOREF_ANNOTATION_ATTRIBUTE_ID + '\'' +
				", XML_ELEMENT_ATTRIBUTE_ID='" + XML_ELEMENT_ATTRIBUTE_ID + '\'' +
				", ANNO_REF_SUPER_INTERFACE='" + ANNO_REF_SUPER_INTERFACE + '\'' +
				", XML_SCHEMA_TEXT='" + XML_SCHEMA_TEXT + '\'' +
				", ANNO_ALL_SYNTAX_HIGHLIGHT_COLOR='" + ANNO_ALL_SYNTAX_HIGHLIGHT_COLOR + '\'' +
				", VO_PROPERTY_ANNOTATION_FQN='" + VO_PROPERTY_ANNOTATION_FQN + '\'' +
				", CONNECTION_METHOD_WAY='" + CONNECTION_METHOD_WAY + '\'' +
				", REINDEX_INTERVAL=" + REINDEX_INTERVAL +
				'}';
	}

	@Override
	public AnnoRefSettings clone() throws CloneNotSupportedException {
		AnnoRefSettings clone = new AnnoRefSettings();
		clone.ANNOREF_ANNOTATION_FQN = ANNOREF_ANNOTATION_FQN;
		clone.ENABLE_SQLREF_FQN_OVERRIDE = ENABLE_SQLREF_FQN_OVERRIDE;
		clone.ENABLE_AUTO_SYNC = ENABLE_AUTO_SYNC;
		clone.ENABLE_ANNO_SUPER = ENABLE_ANNO_SUPER;
		clone.ENABLE_UTIL_CLASS_SCAN = ENABLE_UTIL_CLASS_SCAN;
		clone.ANNOREF_ANNOTATION_ATTRIBUTE_ID = ANNOREF_ANNOTATION_ATTRIBUTE_ID;
		clone.XML_ELEMENT_ATTRIBUTE_ID = XML_ELEMENT_ATTRIBUTE_ID;
		clone.ANNO_ANNOTATION_FQN = ANNO_ANNOTATION_FQN;
		clone.XML_SCHEMA_TEXT = XML_SCHEMA_TEXT;
		clone.SP_VIEW_ANNOTATION_FQN = SP_VIEW_ANNOTATION_FQN;
		clone.ANNO_REF_SUPER_INTERFACE = ANNO_REF_SUPER_INTERFACE;
		clone.ANNO_ALL_SYNTAX_HIGHLIGHT_COLOR = ANNO_ALL_SYNTAX_HIGHLIGHT_COLOR;
		clone.ANNOREF_UTIL_CLASS_FQN = ANNOREF_UTIL_CLASS_FQN;
		clone.VO_PROPERTY_ANNOTATION_FQN = VO_PROPERTY_ANNOTATION_FQN;
		clone.ENABLE_SQL_TO_MODEL_VALIDATION = ENABLE_SQL_TO_MODEL_VALIDATION;
		clone.DEEP_SCAN_ENABLED = DEEP_SCAN_ENABLED;
		return clone;
	}
}

