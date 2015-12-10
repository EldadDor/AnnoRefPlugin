/*
 * User: eldad.Dor
 * Date: 05/12/2015 00:32
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.common;

import com.intellij.lang.Language;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.mock.MockPsiElement;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

/**
 * @author eldad
 * @date 05/12/2015
 */
public class AnnoRefMockPsiElement extends MockPsiElement {

	private Project realProject;
	private String text;
	private PsiElement originalPsiElement;

	public AnnoRefMockPsiElement(Disposable parentDisposable, Project realProject, String text, PsiElement originalPsiElement) {
		super(parentDisposable);
		this.realProject = realProject;
		this.text = text;
		this.originalPsiElement = originalPsiElement;
	}

	public AnnoRefMockPsiElement(Disposable parentDisposable, Project realProject, String text) {
		super(parentDisposable);
		this.realProject = realProject;
		this.text = text;
	}


	@Override public PsiElement getNavigationElement() {
		final PsiElementFactory elementFactory = JavaPsiFacade.getInstance(realProject).getElementFactory();
		final PsiExpression expressionFromText = elementFactory.createExpressionFromText(text.replace("@",""), this);
		return expressionFromText;
	}

	@Override public PsiFile getContainingFile() throws PsiInvalidElementAccessException {
		return originalPsiElement.getContainingFile();
	}

	@NotNull @Override public Language getLanguage() {
		return JavaLanguage.INSTANCE;
	}
}