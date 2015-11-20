/*
 * User: eldad.Dor
 * Date: 26/08/13
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.inspection.fix;

import com.idi.intellij.plugin.query.annoref.util.AnnoRefBundle;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * @author eldad
 * @date 26/08/13
 */
public class SQLRefMultipleRefIdQuickFix implements LocalQuickFix {


	@NotNull
	@Override
	public String getName() {
		return AnnoRefBundle.message("annoRef.xml.inspection.quickfix");
	}

	@NotNull
	@Override
	public String getFamilyName() {
		return "AnnoRef";
	}

	@Override
	public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
		PsiElement psiElement = descriptor.getPsiElement();
		//To change body of implemented methods use File | Settings | File Templates.
	}

}