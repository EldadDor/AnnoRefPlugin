package com.idi.intellij.plugin.query.annoref.util;

/**
 * Created by IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 12/12/10
 * Time: 20:55
 * To change this template use File | Settings | File Templates.
 */
public enum FileTypeEnum {
	XML_FILE(0), JAVA_FILE(1);
	private int value;

	FileTypeEnum(int type) {
		value = type;
	}

	public int getValue() {
		return value;
	}
}
