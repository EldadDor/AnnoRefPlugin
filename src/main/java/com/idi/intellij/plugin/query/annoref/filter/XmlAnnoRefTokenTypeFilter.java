/*
 * User: eldad.Dor
 * Date: 10/07/2014 11:05
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.filter;

import com.idi.intellij.plugin.query.annoref.persist.AnnoRefConfigSettings;
import com.intellij.psi.PsiElement;
import com.intellij.psi.filters.position.XmlTokenTypeFilter;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.ReflectionCache;

/**
 * @author eldad
 * @date 10/07/2014
 */
public class XmlAnnoRefTokenTypeFilter extends XmlTokenTypeFilter {


	public XmlAnnoRefTokenTypeFilter(IElementType paramIElementType) {
		super(paramIElementType);
	}

	@Override
	public boolean isClassAcceptable(Class paramClass) {
		return ReflectionCache.isAssignable(XmlAttributeValue.class, paramClass);
	}

	@Override
	public boolean isAcceptable(Object paramObject, PsiElement paramPsiElement) {
		return super.isAcceptable(paramObject, paramPsiElement) && isAcceptableAttribute(paramPsiElement);
	}

	private boolean isAcceptableAttribute(PsiElement psiElement) {
		if (((XmlAttribute) psiElement).getNameElement() != null && ((XmlAttribute) psiElement).getNameElement().getText().
				equals(AnnoRefConfigSettings.getInstance(psiElement.getProject()).getAnnoRefState().XML_ELEMENT_ATTRIBUTE_ID)) {
			return true;
		}
		return false;

	}

}