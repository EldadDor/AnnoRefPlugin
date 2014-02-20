package com.idi.intellij.plugin.query.sqlref.common;

import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 28/07/12
 * Time: 01:57
 * To change this template use File | Settings | File Templates.
 */
public interface SQLRefConstants {
	Icon ANNO_REF_COMPONENT_ICON_CLASS = IconLoader.findIcon("/icons/class_icon_16.png");
	Icon ANNO_REF_COMPONENT_ICON_XML = IconLoader.findIcon("/icons/xml_icon_red_12.png");
	Icon ANNO_REF_MESSAGE_ICON_D = IconLoader.findIcon("/icons/ex_mark_yellow_16.png");
	Icon ANNO_REF_MESSAGE_ICON_E = IconLoader.findIcon("/icons/ex_mark_red_16.png");
	String SQL_REF_ANNOTATION_FQN = "com.idi.framework";
	String SQL_REF_ANNOTATION_VALUE = "refId";
	String ANNO_REF_NAME = "AnnoRef";
	Color MessagePopupBackgroundColor = JBColor.getHSBColor(74, 116, 200);
}
