/*
 * User: eldad.Dor
 * Date: 06/07/2014 18:32
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.inspection;

import com.idi.intellij.plugin.query.annoref.repo.model.SQLRefReference;
import com.idi.intellij.plugin.query.annoref.util.AnnoRefModelUtil;
import com.intellij.find.impl.HelpID;
import com.intellij.lang.java.JavaFindUsagesProvider;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author eldad
 * @date 06/07/2014
 */
public class AnnoRefInJavaFindUsagesProvider extends JavaFindUsagesProvider {


	@Override
	public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
		if (psiElement instanceof PsiJavaToken && ((PsiJavaToken) psiElement).getTokenType().toString().equals("STRING_LITERAL")) {
			return true;
		}
		return false;
	}

	@Nullable
	@Override
	public String getHelpId(@NotNull PsiElement psiElement) {
		return HelpID.FIND_CLASS_USAGES;
	}

	@Override
	@NotNull
	public String getType(@NotNull PsiElement element) {
		if (element instanceof PsiAnnotation) {
			return "AnnoRef";
		}
		if (element instanceof PsiJavaToken && ((PsiJavaToken) element).getTokenType().toString().equals("STRING_LITERAL")) {
			return "QueryUtils";
		}
		if (element instanceof PsiMethodCallExpression) {
			final String methodExpression = AnnoRefModelUtil.isValidMethodExpression(element);
			return methodExpression;
		}
		if (element instanceof PsiMethod) {
			return super.getType(element);
//			return ((NavigationItem) element).getPresentation().getPresentableText();
		}
		return super.getType(element);
	}

	@NotNull
	@Override
	public String getDescriptiveName(@NotNull PsiElement element) {
		if (element instanceof PsiAnnotation) {
			return element.getText();
		}
		if (element instanceof PsiJavaToken && ((PsiJavaToken) element).getTokenType().toString().equals("STRING_LITERAL")) {
			return element.getText();
		}
		if (element instanceof PsiMethodCallExpression) {
			final String methodExpression = AnnoRefModelUtil.isValidMethodExpression(element);
		}
		return super.getDescriptiveName(element);
	}

	@NotNull
	@Override
	public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
		if (element instanceof PsiAnnotation) {
			final SQLRefReference validAnnoRef = AnnoRefModelUtil.isValidAnnoRef(element);
			if (!validAnnoRef.getClassSmartPointersElements().isEmpty()) {
				final PsiAnnotation element1 = (PsiAnnotation) element;
				return ((PsiClassOwner) element.getContainingFile()).getClasses()[0].getName() + " -> " + element.getText();
			}
			return element.getText();
		}
		if (element instanceof PsiJavaToken && ((PsiJavaToken) element).getTokenType().toString().equals("STRING_LITERAL")) {
			return "Usages of =" + element.getText();
		}
		if (element instanceof PsiMethodCallExpression) {
			if (AnnoRefModelUtil.isValidMethodExpression(element) != null) {
				final String qualifiedName = ((PsiClassOwner) ((PsiMethodCallExpression) element).getMethodExpression().getContainingFile()).getClasses()[0].getName();
				final String argument = (((PsiCall) element).getArgumentList().getExpressions()[0].getText());
				return qualifiedName + " -> " + ((PsiMethodCallExpression) element).getMethodExpression().getQualifiedName() + "(" + argument + ")";

//				return qualifiedName;
			}
		}
		return super.getNodeText(element, useFullName);

	}
}