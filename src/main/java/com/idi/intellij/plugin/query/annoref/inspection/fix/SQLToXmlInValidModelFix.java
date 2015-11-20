/*
 * User: eldad.Dor
 * Date: 06/11/2014 23:42
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.inspection.fix;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author eldad
 * @date 06/11/2014
 */
public class SQLToXmlInValidModelFix implements LocalQuickFix {

	@NotNull
	@Override
	public String getName() {
		return null;
	}

	@NotNull
	@Override
	public String getFamilyName() {
		return null;
	}

	@Override
	public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {

	}
}