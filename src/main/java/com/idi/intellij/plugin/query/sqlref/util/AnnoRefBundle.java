package com.idi.intellij.plugin.query.sqlref.util;

import org.jetbrains.annotations.NonNls;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 8/17/13
 * Time: 11:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class AnnoRefBundle {
	private static Reference<ResourceBundle> annoRefBundle;

	@NonNls
	private static final String BUNDLE = "annoRefBundle";

	public static String message(String key, Object... params) {
		return getBundle().getString(key);
	}


	private static ResourceBundle getBundle() {
		ResourceBundle bundle = null;
		if (annoRefBundle != null) bundle = annoRefBundle.get();
		if (bundle == null) {
			bundle = ResourceBundle.getBundle(BUNDLE);
			annoRefBundle = new SoftReference<ResourceBundle>(bundle);
		}
		return bundle;
	}
}
