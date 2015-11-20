/*
 * User: eldad.Dor
 * Date: 22/07/2014 15:36
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.component;

import com.idi.intellij.plugin.query.annoref.action.SPViewingInformation;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;

/**
 * @author eldad
 * @date 22/07/2014
 */
public class AnnoRefDataKey {
	//	public static final DataKey<PsiElement> ANNO_REF_METHOD_UTIL_ELEMENT = DataKey.create("ANNO_REF_METHOD_UTIL_ELEMENT");
	public static final Key<PsiElement> ANNO_REF_METHOD_UTIL_ELEMENT = new Key("ANNO_REF_METHOD_UTIL_ELEMENT");
	public static final Key<SPViewingInformation> DATA_SOURCE_NAME_DATA_KEY = new Key("DATA_SOURCE_NAME_DATA_KEY");
//	public static final DataKey<DataSourceName> DATA_SOURCE_NAME_DATA_KEY=DataKey.create("DATA_SOURCE_NAME_DATA_KEY");
//	public static final DataKey<PsiElement> ANNO_REF_METHOD_UTIL_ELEMENT = DataKey.create("ANNO_REF_METHOD_UTIL_ELEMENT");
}