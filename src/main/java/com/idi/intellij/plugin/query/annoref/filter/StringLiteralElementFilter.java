/*
 * User: eldad.Dor
 * Date: 09/07/2014 11:41
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.filter;

import com.idi.intellij.plugin.query.annoref.persist.AnnoRefConfigSettings;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.filters.ElementFilter;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.impl.source.tree.java.PsiMethodCallExpressionImpl;
import org.jetbrains.annotations.Nullable;

/**
 * @author eldad
 * @date 09/07/2014
 */
public class StringLiteralElementFilter implements ElementFilter {
	private static final Logger log = Logger.getInstance(StringLiteralElementFilter.class.getName());
/*	private final IElementType elementType;

	public StringLiteralElementFilter(IElementType elementType) {
		this.elementType = elementType;
	}*/

	@Override
	public boolean isAcceptable(Object instance, @Nullable PsiElement psiElement) {
		if (psiElement instanceof PsiJavaToken && ((PsiJavaToken) psiElement).getTokenType().toString().equals("STRING_LITERAL")) {
			if (log.isDebugEnabled()) {
				log.debug("isAcceptable(): getContainingFile=" + psiElement.getContainingFile());
			}
			return true;
		}
		if (instance instanceof PsiMethodCallExpression) {
			if ((((PsiMethodCallExpression) instance).getMethodExpression().getQualifierExpression() != null) && (((PsiMethodCallExpression) instance).getMethodExpression().getQualifierExpression().getType() != null)) {
				final PsiType type = ((PsiMethodCallExpression) instance).getMethodExpression().getQualifierExpression().getType();
				if (type != null && type instanceof PsiClassReferenceType) {
					final Project project = psiElement.getProject();
					if (AnnoRefConfigSettings.getInstance(project).getAnnoRefState().ANNOREF_UTIL_CLASS_FQN.equals(((PsiClassReferenceType) type).getReference().getQualifiedName())) {
						if (log.isDebugEnabled()) {
							log.debug("isAcceptable(): psiElement=" + psiElement);
						}
						final PsiExpression psiExpression = ((PsiCall) instance).getArgumentList().getExpressions()[0];
						if (psiExpression instanceof PsiLiteral) {
							final String refId = String.valueOf(((PsiLiteral) psiExpression).getValue());
						}
						return true;
					}
				}

			}
		}
		return false;
	}

	@Override
	public boolean isClassAcceptable(Class aClass) {
		return aClass.isAssignableFrom(PsiMethodCallExpressionImpl.class);
	}
}