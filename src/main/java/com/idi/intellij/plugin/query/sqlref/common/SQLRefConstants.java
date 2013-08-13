package com.idi.intellij.plugin.query.sqlref.common;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 28/07/12
 * Time: 01:57
 * To change this template use File | Settings | File Templates.
 */
public interface SQLRefConstants {
	public Icon ANNO_REF_COMPONENT_ICON_CLASS = IconLoader.findIcon("/icons/class_icon_16.png");
	public Icon ANNO_REF_COMPONENT_ICON_XML = IconLoader.findIcon("/icons/xml_icon_red_16.png");
	public static final String SQL_REF_ANNOTATION_FQN = "com.idi.framework";
	public static final String SQL_REF_ANNOTATION_VALUE = "refId";
	public static final String SQL_REF_NAME = "SQLRef";


}
