/*
 * User: eldad.Dor
 * Date: 08/12/2014 17:46
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.util;

import com.intellij.ide.diff.DiffElement;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diff.DirDiffManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.fileTypes.SyntaxHighlighterLanguageFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.sql.dialects.sybase.SybaseDialect;
import org.jetbrains.annotations.NotNull;

/**
 * @author eldad
 * @date 08/12/2014
 */
public class SPDiffManager implements ProjectComponent {
	private final Project project;

	public SPDiffManager(Project project) {
		this.project = project;
	}

	public void diff(String text1, String text2) {
		PsiFile file1 = PsiFileFactory.getInstance(project).createFileFromText(text1, SybaseDialect.INSTANCE, text1, true, true);
		PsiFile file2 = PsiFileFactory.getInstance(project).createFileFromText(text2, SybaseDialect.INSTANCE, text2, true, true);
		final SyntaxHighlighterLanguageFactory languageFactory = SyntaxHighlighterFactory.LANGUAGE_FACTORY;
		Document document = PsiDocumentManager.getInstance(project).getDocument(file1);
		final VirtualFile virtualFile1 = SQLRefApplication.getVirtualFileFromPsiFile(file1, project);
		final VirtualFile virtualFile2 = SQLRefApplication.getVirtualFileFromPsiFile(file2, project);
		DirDiffManager diffManager = DirDiffManager.getInstance(ProjectManager.getInstance().getDefaultProject());
		final DiffElement diffElement1 = diffManager.createDiffElement(virtualFile1);
		final DiffElement diffElement2 = diffManager.createDiffElement(virtualFile2);
		diffManager.showDiff(diffElement1, diffElement2);
	}

	@Override
	public void projectOpened() {

	}

	@Override
	public void projectClosed() {

	}

	@Override
	public void initComponent() {

	}

	@Override
	public void disposeComponent() {

	}

	@NotNull
	@Override
	public String getComponentName() {
		return SPDiffManager.class.getSimpleName();
	}
}