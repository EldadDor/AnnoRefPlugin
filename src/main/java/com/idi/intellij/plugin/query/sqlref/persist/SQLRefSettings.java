package com.idi.intellij.plugin.query.sqlref.persist;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 6/22/13
 * Time: 9:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class SQLRefSettings {
	public Boolean ENABLE_SQLREF_FQN_OVERRIDE = false;
	public Boolean ENABLE_AUTO_SYNC = true;
	public Boolean ENABLE_ANNO_SUPER = true;
	public String ANNOREF_ANNOTATION_FQN = "com.idi.astro.server.annotation.SQLRef";
	public String ANNO_ANNOTATION_FQN = "com.idi.astro.server.annotation.SQL";
	public String SP_VIEW_ANNOTATION_FQN = "com.idi.astro.server.annotation.SP";
	public String SP_DATA_SOURCE_NAME = "";
	public String ANNOREF_ANNOTATION_ATTRIBUTE_ID = "refId";
	public String ANNO_REF_SUPER_INTERFACE = "com.idi.astro.train.ifc.Query";
	public String XML_SCHEMA_TEXT = "";


/*	@Override
	public String toString() {
		return "SQLRefSettings{" +
				"ENABLE_SQLREF_FQN_OVERRIDE=" + ENABLE_SQLREF_FQN_OVERRIDE +
				", ENABLE_AUTO_SYNC=" + ENABLE_AUTO_SYNC +
				", ANNOREF_ANNOTATION_FQN='" + ANNOREF_ANNOTATION_FQN + '\'' +
				", ANNO_ANNOTATION_FQN='" + ANNO_ANNOTATION_FQN + '\'' +
				", ANNOREF_ANNOTATION_ATTRIBUTE_ID='" + ANNOREF_ANNOTATION_ATTRIBUTE_ID + '\'' +
				", XML_SCHEMA_TEXT='" + XML_SCHEMA_TEXT + '\'' +
				'}';
	}*/

	@Override
	public String toString() {
		return "SQLRefSettings{" +
				"ENABLE_SQLREF_FQN_OVERRIDE=" + ENABLE_SQLREF_FQN_OVERRIDE +
				", ENABLE_AUTO_SYNC=" + ENABLE_AUTO_SYNC +
				", ENABLE_ANNO_SUPER=" + ENABLE_ANNO_SUPER +
				", ANNOREF_ANNOTATION_FQN='" + ANNOREF_ANNOTATION_FQN + '\'' +
				", ANNO_ANNOTATION_FQN='" + ANNO_ANNOTATION_FQN + '\'' +
				", SP_VIEW_ANNOTATION_FQN='" + SP_VIEW_ANNOTATION_FQN + '\'' +
				", ANNOREF_ANNOTATION_ATTRIBUTE_ID='" + ANNOREF_ANNOTATION_ATTRIBUTE_ID + '\'' +
				", ANNO_REF_SUPER_INTERFACE='" + ANNO_REF_SUPER_INTERFACE + '\'' +
				", XML_SCHEMA_TEXT='" + XML_SCHEMA_TEXT + '\'' +
				'}';
	}

	@Override
	public SQLRefSettings clone() throws CloneNotSupportedException {
		SQLRefSettings clone = new SQLRefSettings();
		clone.ANNOREF_ANNOTATION_FQN = ANNOREF_ANNOTATION_FQN;
		clone.ENABLE_SQLREF_FQN_OVERRIDE = ENABLE_SQLREF_FQN_OVERRIDE;
		clone.ENABLE_AUTO_SYNC = ENABLE_AUTO_SYNC;
		clone.ANNOREF_ANNOTATION_ATTRIBUTE_ID = ANNOREF_ANNOTATION_ATTRIBUTE_ID;
		clone.ANNO_ANNOTATION_FQN = ANNO_ANNOTATION_FQN;
		clone.XML_SCHEMA_TEXT = XML_SCHEMA_TEXT;
		clone.SP_VIEW_ANNOTATION_FQN = SP_VIEW_ANNOTATION_FQN;
		clone.ANNO_REF_SUPER_INTERFACE = ANNO_REF_SUPER_INTERFACE;
		return clone;
	}
}

