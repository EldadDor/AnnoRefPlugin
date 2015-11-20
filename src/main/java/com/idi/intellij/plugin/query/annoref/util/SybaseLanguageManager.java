/*
 * User: eldad.Dor
 * Date: 23/02/2015 10:35
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.util;

import com.idi.intellij.plugin.query.annoref.persist.AnnoRefConfigSettings;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefSettings;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.sql.dialects.sybase.SybaseDialect;
import com.intellij.sql.psi.SqlFileType;
import org.jetbrains.annotations.NotNull;

/**
 * @author eldad
 * @date 23/02/2015
 */
public class SybaseLanguageManager implements ProjectComponent {
	private static Logger logger = Logger.getInstance(SybaseLanguageManager.class);

	private final Project project;

	public SybaseLanguageManager(Project project) {
		this.project = project;
	}

	public PsiFile initializeSqlSyntaxForPsiFile(String text) {
		return PsiFileFactory.getInstance(project).createFileFromText(text, SybaseDialect.INSTANCE, text, true, true);
	}


	public Editor initializeSqlSyntaxForEditor(Project project, String text) {
		PsiFile file = PsiFileFactory.getInstance(project).createFileFromText(text, SybaseDialect.INSTANCE, text, true, true);
//		final SyntaxHighlighterLanguageFactory languageFactory = SyntaxHighlighterFactory.LANGUAGE_FACTORY;
//		final SyntaxHighlighter syntaxHighlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(SybaseDialect.INSTANCE, project, SQLRefApplication.getVirtualFileFromPsiFile(file, project));
		Document document = PsiDocumentManager.getInstance(project).getDocument(file);
		return EditorFactory.getInstance().createEditor(document, project, SqlFileType.INSTANCE, true);

	}


	public void getSpTextForDBEnvironment(String dbEnv, String spName) {
		final AnnoRefSettings sqlRefState = AnnoRefConfigSettings.getInstance(project).getAnnoRefState();
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
		return SybaseLanguageManager.class.getSimpleName();
	}
}