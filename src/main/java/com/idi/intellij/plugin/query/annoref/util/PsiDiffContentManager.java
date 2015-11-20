/*
 * User: eldad.Dor
 * Date: 23/02/2015 23:52
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.util;

import com.idi.intellij.plugin.query.annoref.action.DiffSPViewAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.diff.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.text.ElementPresentation;
import org.jetbrains.annotations.Nullable;

/**
 * @author eldad
 * @date 23/02/2015
 */
public class PsiDiffContentManager {
	private static Logger log = Logger.getInstance(DiffSPViewAction.class);

	private PsiDiffContentManager() {
	}

	@SuppressWarnings("OverlyStrongTypeCast")
	@Nullable
	private static DiffContent fromPsiElement(PsiElement psiElement) {
		if (psiElement instanceof PsiFile) {
			return DiffContent.fromFile(psiElement.getProject(), ((PsiFile) psiElement).getVirtualFile());
		} else if (psiElement instanceof PsiDirectory) {
			return DiffContent.fromFile(psiElement.getProject(), ((PsiDirectory) psiElement).getVirtualFile());
		}
		PsiFile containingFile = psiElement.getContainingFile();
		if (containingFile == null) {
			String text = psiElement.getText();
			return text != null ? new SimpleContent(text) : null;
		}
		DiffContent wholeFileContent = DiffContent.fromFile(psiElement.getProject(), containingFile.getVirtualFile());
		if (wholeFileContent == null || wholeFileContent.getDocument() == null) {
			return null;
		}
		Project project = psiElement.getProject();
		return new FragmentContent(wholeFileContent, psiElement.getTextRange(), project);
	}

	@Nullable
	public static SimpleDiffRequest comparePsiElements(PsiElement psiElement1, PsiElement psiElement2) {
		if (!psiElement1.isValid() || !psiElement2.isValid()) {
			return null;
		}
		Project project = psiElement1.getProject();
		log.assertTrue(project == psiElement2.getProject());
		DiffContent content1 = fromPsiElement(psiElement1);
		DiffContent content2 = fromPsiElement(psiElement2);
		if (content1 == null || content2 == null) {
			return null;
		}
		final ElementPresentation presentation1 = ElementPresentation.forElement(psiElement1);
		final ElementPresentation presentation2 = ElementPresentation.forElement(psiElement2);
		String title = DiffBundle.message("diff.element.qualified.name.vs.element.qualified.name.dialog.title",
				presentation1.getQualifiedName(), presentation2.getQualifiedName());
		SimpleDiffRequest diffRequest = new SimpleDiffRequest(project, title);
		diffRequest.setContents(content1, content2);
		diffRequest.setContentTitles(presentation1.getQualifiedName(), presentation2.getQualifiedName());
		return diffRequest;
	}
}