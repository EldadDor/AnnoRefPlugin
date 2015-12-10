/*
 * User: eldad.Dor
 * Date: 09/07/2014 11:41
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.filter;

import com.google.common.collect.Maps;
import com.idi.intellij.plugin.query.annoref.index.SQLRefRepository;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefConfigSettings;
import com.idi.intellij.plugin.query.annoref.util.SQLRefNamingUtil;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.PomNamedTarget;
import com.intellij.psi.*;
import com.intellij.psi.filters.ElementFilter;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.impl.source.tree.java.PsiLiteralExpressionImpl;
import com.intellij.psi.impl.source.tree.java.PsiReferenceExpressionImpl;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author eldad
 * @date 09/07/2014
 */
public class StringLiteralElementFilter implements ElementFilter {
	private static final Logger log = Logger.getInstance(StringLiteralElementFilter.class.getName());
	private final Project project;
	private final VirtualFile virtualFile;
	private Map<String, String> localVariablesAnnoRefs = Maps.newHashMap();

	public StringLiteralElementFilter(Project project, VirtualFile virtualFile) {
		this.project = project;
		this.virtualFile = virtualFile;
	}

	@Override
	public boolean isAcceptable(Object instance, @Nullable PsiElement psiElement) {
		if (psiElement instanceof PsiJavaToken && ((PsiJavaToken) psiElement).getTokenType().equals(JavaTokenType.STRING_LITERAL)) {
			if (log.isDebugEnabled()) {
				log.debug("isAcceptable(): getContainingFile=" + psiElement.getContainingFile());
			}
			return true;
		}
		if (psiElement instanceof PsiField && instance instanceof PsiLiteralExpression) {
			final PsiLiteralExpression childOfType = PsiTreeUtil.findChildOfType(psiElement, PsiLiteralExpression.class, true);
			if (childOfType != null) {
				final String refId = ((PsiLiteralExpressionImpl) childOfType).getInnerText();
				localVariablesAnnoRefs.put(((PomNamedTarget) psiElement).getName(), refId);
				log.info("isAcceptable():");
			}
		}
		if (instance instanceof PsiMethodCallExpression) {
			if (SQLRefNamingUtil.isAcceptedMethodCall((PsiElement) instance)) {
				final PsiType type = ((PsiMethodCallExpression) instance).getMethodExpression().getQualifierExpression().getType();
				if (type != null && type instanceof PsiClassReferenceType) {
					final SQLRefRepository refRepository = ServiceManager.getService(project, SQLRefRepository.class);
					if (AnnoRefConfigSettings.getInstance(project).getAnnoRefState().ANNOREF_UTIL_CLASS_FQN.equals(((PsiClassReferenceType) type).getReference().getQualifiedName())) {
						if (log.isDebugEnabled()) {
							log.debug("isAcceptable(): psiElement=" + psiElement);
						}
						final PsiExpression psiExpression = ((PsiCall) instance).getArgumentList().getExpressions()[0];
						if (psiExpression instanceof PsiLiteral) {
							final String refId = String.valueOf(((PsiLiteral) psiExpression).getValue());
							if (log.isDebugEnabled()) {
								log.debug("isAcceptable(): refId=" + refId);
							}
							refRepository.addUtilClassMethodCallInformationToRepository(refId, virtualFile, psiExpression);
						}
						if (psiExpression instanceof PsiReferenceExpressionImpl && psiExpression.getType() != null && ((PsiClassType) psiExpression.getType()).getClassName().equals(String.class.getSimpleName())) {
//							((PsiCall) instance).getArgumentList().getExpressions()[0].getUserData(new Key<String>("VALUE"));
							if (localVariablesAnnoRefs.containsKey(psiExpression.getText())) {
								final String refId = localVariablesAnnoRefs.get(psiExpression.getText());
								refRepository.addUtilClassMethodCallInformationToRepository(refId, virtualFile, psiExpression);
							}
//							((PsiReferenceExpressionImpl) ((PsiCall) instance).getArgumentList().getExpressions()[0]).getUserMap().getKeys()[0];
//							final PsiReferenceExpressionImpl psiReferenceExp = (PsiReferenceExpressionImpl) ((PsiCall) instance).getArgumentList().getExpressions()[0];
//							psiReferenceExp.getUserMap().get(((PsiReferenceExpressionImpl) paramPsiElement.getParent()).getUserMap().getKeys()[0])
//							psiReferenceExp.getUserData(psiReferenceExp.getUserDataString().getKeys()[0])
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
//		return aClass.isAssignableFrom(PsiMethodCallExpressionImpl.class);
		return true;
	}
}