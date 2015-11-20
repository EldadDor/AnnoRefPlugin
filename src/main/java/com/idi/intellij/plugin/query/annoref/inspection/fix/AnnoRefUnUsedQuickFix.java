/*
 * User: eldad.Dor
 * Date: 27/08/13
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.inspection.fix;

import com.idi.intellij.plugin.query.annoref.util.AnnoRefBundle;
import com.intellij.codeInsight.daemon.impl.quickfix.CreateClassKind;
import com.intellij.codeInsight.daemon.impl.quickfix.CreateFromUsageUtils;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;

/**
 * @author eldad
 * @date 27/08/13
 */
public class AnnoRefUnUsedQuickFix implements LocalQuickFix {

	private PsiDirectory psiDirectory;

	public AnnoRefUnUsedQuickFix(PsiDirectory psiDirectory) {
		this.psiDirectory = psiDirectory;
	}

	@NotNull
	@Override
	public String getName() {
		return AnnoRefBundle.message("annoRef.xml.inspection.unused.fix");
	}

	@NotNull
	@Override
	public String getFamilyName() {
		return "AnnoRef";
	}

	@Override
	public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
		PsiElement psiElement = descriptor.getPsiElement();
		PsiManager psiManager = PsiManager.getInstance(project);
		PsiClass aClass = CreateFromUsageUtils.createClass(CreateClassKind.CLASS, psiDirectory, "Create AnnoRef Class", psiManager, psiElement, null, null);
	}


}