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
	public String SQLREF_ANNOTATION_FQN = "com.idi.astro.server.annotation.SQLRef";
	public String SQLREF_ANNOTATION_ATTRIBUTE_ID = "refId";

	@Override
	public String toString() {
		return "SQLRefSettings{" +
				"ENABLE_SQLREF_FQN_OVERRIDE=" + ENABLE_SQLREF_FQN_OVERRIDE +
				", ENABLE_AUTO_SYNC=" + ENABLE_AUTO_SYNC +
				", SQLREF_ANNOTATION_FQN='" + SQLREF_ANNOTATION_FQN + '\'' +
				", SQLREF_ANNOTATION_ATTRIBUTE_ID='" + SQLREF_ANNOTATION_ATTRIBUTE_ID + '\'' +
				'}';
	}

	@Override
	public SQLRefSettings clone() throws CloneNotSupportedException {
		SQLRefSettings clone = new SQLRefSettings();
		clone.SQLREF_ANNOTATION_FQN = SQLREF_ANNOTATION_FQN;
		clone.ENABLE_SQLREF_FQN_OVERRIDE = ENABLE_SQLREF_FQN_OVERRIDE;
		clone.ENABLE_AUTO_SYNC = ENABLE_AUTO_SYNC;
		clone.SQLREF_ANNOTATION_ATTRIBUTE_ID = SQLREF_ANNOTATION_ATTRIBUTE_ID;
		return clone;
	}
}

