/*
 * User: eldad.Dor
 * Date: 02/07/2014 10:50
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.codeInsight;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.idi.intellij.plugin.query.annoref.common.AnnoRefIcons;
import com.idi.intellij.plugin.query.annoref.index.SQLRefRepository;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefConfigSettings;
import com.idi.intellij.plugin.query.annoref.repo.model.SQLRefReference;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * @author eldad
 * @date 02/07/2014
 */
public class AnnoRefMethodArgsCallLineMarkerProvider implements LineMarkerProvider {
	private static final Logger log = Logger.getInstance(AnnoRefMethodArgsCallLineMarkerProvider.class.getName());


	@Nullable
	@Override
	public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement psiElement) {
		final SQLRefReference refReference = isValidMethodCall(psiElement);
		if (refReference != null) {
			if (!refReference.getXmlSmartPointersElements().isEmpty()) {
				final List<PsiElement> psiElements = Lists.newArrayList();
				for (final SmartPsiElementPointer<PsiElement> elementPointer : refReference.getXmlSmartPointersElements()) {
					psiElements.add(elementPointer.getElement());
				}
				for (final SmartPsiElementPointer<PsiElement> elementPointer : refReference.getClassSmartPointersElements()) {
					psiElements.add(elementPointer.getElement());
				}
				PsiElement[] elements = new PsiElement[psiElements.size()];
				psiElements.toArray(elements);
				Key<PsiElement> key = Key.create("ANNO_REF_METHOD_UTIL_ELEMENT");
//				final DataKey<PsiElement> key = CommonDataKeys.PSI_ELEMENT.create(AnnoRefDataKey.ANNO_REF_METHOD_UTIL_ELEMENT.toString());
				Editor editor;
//				if (ApplicationManager.getApplication().isDispatchThread()) {
				/*	final FileEditorManager instance = FileEditorManager.getInstance(psiElement.getProject());*/
				/*	if (instance.getEditors(psiElement.getContainingFile().getVirtualFile()) != null) {
						FileEditor[] fileEditors = instance.getEditors(psiElement.getContainingFile().getVirtualFile());
						for (FileEditor fileEditor : fileEditors) {
							if (fileEditor instanceof TextEditor) {
								editor = ((TextEditor) fileEditor).getEditor();
								((UserDataHolderEx) editor).putUserDataIfAbsent(key, psiElement);
								break;
							}
						}
					}*/
//				}

			/*	if (psiElement.getUserData(AnnoRefDataKey.ANNO_REF_METHOD_UTIL_ELEMENT) == null) {
					psiElement.putUserData(AnnoRefDataKey.ANNO_REF_METHOD_UTIL_ELEMENT, psiElement);
				}*/
				return SQLRefIdLineMarkerInfo.create(psiElement, elements, AnnoRefIcons.Patterns.ANNO_REF_UTIL_CLASS_ICON_CLASS, null);
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("constructLineMarkerProvider(): psiElement=" + psiElement + " text=" + psiElement.getText());
		}
		return null;
	}

	@Override
	public void collectSlowLineMarkers(@NotNull List<PsiElement> psiElementList, @NotNull Collection<LineMarkerInfo> lineMarkerInfos) {
		if (log.isDebugEnabled()) {
			log.debug("collectSlowLineMarkers():");
		}
		for (final PsiElement psiElement : psiElementList) {
			if (isAcceptedMethodCall(psiElement)) {
				final PsiType type = ((PsiMethodCallExpression) psiElement).getMethodExpression().getQualifierExpression().getType();
				if (isAcceptedClassTypeReference(type)) {
					final Project project = psiElement.getProject();
					final String validAnnoRefId = getValidAnnoRefId(psiElement, type, project);
					if (validAnnoRefId != null) {
						final SQLRefReference refReference = ServiceManager.getService(project, SQLRefRepository.class).getSQLRefReferenceForID(validAnnoRefId);
						if (refReference != null && !refReference.getUtilClassSmartPointersElements().isEmpty()) {
							if (log.isDebugEnabled()) {
								log.info("collectSlowLineMarkers(): Cleaning PsiElement=" + psiElement + " for File=" + psiElement.getContainingFile().getName());
							}
							refReference.getUtilClassSmartPointersElements().clear();
							refReference.addUtilClassCallInformation(psiElement.getContainingFile().getName(), psiElement);
						}
					}
				}
			}
		}
	}


	private SQLRefReference isValidMethodCall(PsiElement psiElement) {
		if (isAcceptedMethodCall(psiElement)) {
			final PsiType type = ((PsiMethodCallExpression) psiElement).getMethodExpression().getQualifierExpression().getType();
			if (isAcceptedClassTypeReference(type)) {
				final Project project = psiElement.getProject();
				final String refId = getValidAnnoRefId(psiElement, type, project);
				if (log.isDebugEnabled()) {
					log.debug("constructLineMarkerProvider(): RefId=" + refId);
				}
				if (!Strings.isNullOrEmpty(refId)) {
					final SQLRefReference referenceForID = ServiceManager.getService(project, SQLRefRepository.class).getSQLRefReferenceForID(refId);
					if (referenceForID != null) {
					/*	CommonDataKeys.PSI_FILE.
						psiElement.putUserData(Key.create("ANNO_REF_METHOD_UTIL_ELEMENT"),psiElement);*/
					}
					return referenceForID;
				}
			}
		}
		return null;
	}


	private boolean isAcceptedClassTypeReference(PsiType type) {
		return type != null && type instanceof PsiClassReferenceType && ((PsiClassReferenceType) type).getReference() != null;
	}

	private boolean isAcceptedMethodCall(PsiElement psiElement) {
		return psiElement instanceof PsiMethodCallExpression && ((PsiMethodCallExpression) psiElement).getMethodExpression() != null &&
				((PsiMethodCallExpression) psiElement).getMethodExpression().getQualifierExpression() != null &&
				((PsiMethodCallExpression) psiElement).getMethodExpression().getQualifierExpression().getType() != null;
	}


	private String getValidAnnoRefId(@NotNull PsiElement element, @NotNull PsiType psiType, @NotNull Project project) {
		if (AnnoRefConfigSettings.getInstance(project).getAnnoRefState().ANNOREF_UTIL_CLASS_FQN.equals(((PsiClassReferenceType) psiType).getReference().getQualifiedName())) {
			if (((PsiCall) element).getArgumentList().getExpressions().length == 1) {
				final PsiExpression psiExpression = ((PsiCall) element).getArgumentList().getExpressions()[0];
				if (psiExpression instanceof PsiLiteral) {
					return String.valueOf(((PsiLiteral) psiExpression).getValue());
				}
				if (psiExpression instanceof PsiReferenceExpression) {
					final PsiElement resolvedElement = ((PsiReference) psiExpression).resolve();
					if (resolvedElement instanceof PsiLocalVariable) {
						final String[] declarationAndAssignment = resolvedElement.getText().split("=");
						return declarationAndAssignment[1].trim().replaceAll("\"", "").replaceAll(";", "");
					}
					return psiExpression.getText();
				}
			}
		}
		return null;
	}

}