package com.idi.intellij.plugin.query.annoref.util;

import org.jetbrains.annotations.NonNls;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 8/17/13
 * Time: 11:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class AnnoRefBundle {
	@NonNls
	private static final String BUNDLE = "annoRefMessages";
	private static final String DATASOURCE = "dataSourcesInfo";
	private static Reference<ResourceBundle> annoRefBundle;

	public static String message(String key, Object... params) {
		final String string = getBundle().getString(key);
		return String.format(string, params);
	}


	private static ResourceBundle getBundle() {
		ResourceBundle bundle = null;
		if (annoRefBundle != null) bundle = annoRefBundle.get();
		if (bundle == null) {
			bundle = ResourceBundle.getBundle(BUNDLE, Locale.ENGLISH);
			annoRefBundle = new SoftReference<ResourceBundle>(bundle);
		}
		return bundle;
	}
}
