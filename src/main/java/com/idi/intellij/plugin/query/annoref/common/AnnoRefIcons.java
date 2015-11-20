/*
 * User: eldad.Dor
 * Date: 13/07/2014 15:59
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.common;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * @author eldad
 * @date 13/07/2014
 */
public class AnnoRefIcons {

	static {
		IconLoader.setUseDarkIcons(true);
	}

	private static Icon load(String path) {
		return IconLoader.getIcon(path, AnnoRefIcons.class);
	}

	public static class Patterns {
		public static final Icon ANNO_REF_COMPONENT_ICON_CLASS = AnnoRefIcons.load("/icons/java_class2_16.png");
		public static final Icon ANNO_REF_COMPONENT_ICON_XML = AnnoRefIcons.load("/icons/xml_icon2_16.png");
		public static final Icon ANNO_REF_UTIL_CLASS_ICON_CLASS = AnnoRefIcons.load("/icons/captainshield_4_13.png");
	}

}

