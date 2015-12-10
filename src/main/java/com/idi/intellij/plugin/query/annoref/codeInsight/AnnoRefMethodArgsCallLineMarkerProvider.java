/*
 * User: eldad.Dor
 * Date: 02/07/2014 10:50
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.codeInsight;

import com.google.common.collect.Lists;
import com.idi.intellij.plugin.query.annoref.common.AnnoRefIcons;
import com.idi.intellij.plugin.query.annoref.index.SQLRefRepository;
import com.idi.intellij.plugin.query.annoref.repo.model.SQLRefReference;
import com.idi.intellij.plugin.query.annoref.util.SQLRefNamingUtil;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiType;
import com.intellij.psi.SmartPsiElementPointer;
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
	public LineMarkerInfo<PsiElement> getLineMarkerInfo(@NotNull PsiElement psiElement) {
		final SQLRefReference refReference = SQLRefNamingUtil.isValidMethodCall(psiElement);
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
			if (SQLRefNamingUtil.isAcceptedMethodCall(psiElement)) {
				final PsiType type = ((PsiMethodCallExpression) psiElement).getMethodExpression().getQualifierExpression().getType();
				if (SQLRefNamingUtil.isAcceptedClassTypeReference(type)) {
					final Project project = psiElement.getProject();
					final String validAnnoRefId = SQLRefNamingUtil.getValidAnnoRefId(psiElement, type, project);
					if (validAnnoRefId != null) {
						final SQLRefReference refReference = ServiceManager.getService(project, SQLRefRepository.class).getSQLRefReferenceForID(validAnnoRefId);
						if (refReference != null && !refReference.getUtilClassSmartPointersElements().isEmpty()) {
							if (log.isDebugEnabled()) {
								log.info("collectSlowLineMarkers(): Cleaning PsiElement=" + psiElement + " for File=" + psiElement.getContainingFile().getName());
							}
							refReference.getUtilClassSmartPointersElements().clear();
//							refReference.addUtilClassCallInformation(psiElement.getContainingFile().getName(), psiElement);
						}
					}
				}
			}
		}
	}


}