/*
 * User: eldad.Dor
 * Date: 02/11/2014 15:48
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.index.listeners;

import com.intellij.psi.PsiElement;

/**
 * @author eldad
 * @date 02/11/2014
 */
public interface XmlSqlTwoFoldListener {
	void foundValidRefId(String refID, PsiElement xmlAttributeElement);
}