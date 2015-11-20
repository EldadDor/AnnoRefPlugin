/*
 * User: eldad.Dor
 * Date: 24/02/14 13:33
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.inspection;

import com.intellij.codeInspection.*;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author eldad
 * @date 24/02/14
 */
public class AnnoRefSqlProposedConversionInspection extends GlobalSimpleInspectionTool {

	@Override
	public void checkFile(@NotNull PsiFile psiFile, @NotNull InspectionManager inspectionManager,
	                      @NotNull ProblemsHolder problemsHolder, @NotNull GlobalInspectionContext globalInspectionContext,
	                      @NotNull ProblemDescriptionsProcessor problemDescriptionsProcessor) {
//		problemDescriptionsProcessor.addProblemElement();
//		ServiceManager.getService(psiFile.getProject(), PsiSearchHelper.class).processAllFilesWithWord();

	}
}