package com.idi.intellij.plugin.query.annoref.common;

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
	Icon ANNO_REF_COMPONENT_ICON_CLASS = IconLoader.findIcon("/icons/java_class2_16.png");
	Icon ANNO_REF_COMPONENT_ICON_XML = IconLoader.findIcon("/icons/xml_icon2_16.png");
	Icon ANNO_REF_UTIL_CLASS_ICON_CLASS = IconLoader.findIcon("icons/captainshield_4_13.png");
	Icon ANNO_REF_MESSAGE_ICON_D = IconLoader.findIcon("/icons/ex_mark_yellow_16.png");
	Icon ANNO_REF_MESSAGE_ICON_E = IconLoader.findIcon("/icons/ex_mark_red_16.png");


	Icon DB_TEST_ICON = IconLoader.findIcon("/icons/Turtle.png");
	Icon DB_UT_ICON = IconLoader.findIcon("/icons/starwars/darth.png");
	Icon DB_YEST_ICON = IconLoader.findIcon("/icons/starwars/deathstar.png");
	Icon DB_TRAIN_ICON = IconLoader.findIcon("icons/captainshield_4_13_dark.png");
	Icon DB_REP_ICON = IconLoader.findIcon("/icons/starwars/yoda.png");
	String SQL_REF_ANNOTATION_FQN = "com.idi.framework";

	String ANNO_REF_NOTIFICATION_GORUP = "AnnoRefNotifyGroup";

	String SQL_REF_ANNOTATION_VALUE = "refId";
	String SQL_REF_ANNOTATION = "SQLRef";
	String ANNO_REF_CLASS = "class";
	String ANNO_REF_UTIL_CLASS = "UtilClass";
	String ANNO_REF_XML = "xml";
	String ANNO_REF_NAME = "IDI Plugin Settings";
	String IDI_PLUGIN_SETTINGS = "IDI Plugin Settings";

	String GETTER_PROPERTY = "GETTER";
	String SETTER_PROPERTY = "SETTER";
	Color MessagePopupBackgroundColor = JBColor.getHSBColor(74, 116, 200);

}
