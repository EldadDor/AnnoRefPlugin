/*
 * User: eldad.Dor
 * Date: 19/04/2015 13:00
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.task;

import com.idi.intellij.plugin.query.annoref.persist.AnnoRefConfigSettings;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefSettings;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.GlobalSearchScopesCore;
import com.intellij.psi.search.ProjectScope;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * @author eldad
 * @date 19/04/2015
 */
public abstract class IDIAbstractTask implements IDITask {
	private final Logger logger = Logger.getInstance(getClass().getCanonicalName());
	protected Project project;
	protected int filesCount;
	protected IndicateProgressListener progressListener;
	protected ProgressIndicator ideaProgressIndicator;
	protected Collection<VirtualFile> filesCollection;

	protected IDIAbstractTask(Project project, @Nullable IndicateProgressListener progressListener) {
		this.project = project;
		filesCollection = FileTypeIndex.getFiles(JavaFileType.INSTANCE, GlobalSearchScope.projectScope(project));
		this.progressListener = progressListener;
	}

	protected IDIAbstractTask(Project project, LanguageFileType fileType) {
		final AnnoRefSettings annoRefState = AnnoRefConfigSettings.getInstance(project).getAnnoRefState();
		this.project = project;
		long start = System.currentTimeMillis();
		if (annoRefState.DEEP_SCAN_ENABLED) {
			GlobalSearchScope librariesScope = ProjectScope.getLibrariesScope(project);
//			PsiClass contextBaseClass = JavaPsiFacade.getInstance(project).findClass(annoRefState.ANNO_REF_SUPER_INTERFACE, GlobalSearchScope.projectScope(project));
			GlobalSearchScope searchScope = GlobalSearchScopesCore.projectProductionScope(project).union(librariesScope);
			filesCollection = FileTypeIndex.getFiles(fileType, searchScope);
		} else {
			filesCollection = FileTypeIndex.getFiles(fileType, GlobalSearchScope.projectScope(project));
		}
		long end = System.currentTimeMillis();
		logger.info("IDITask scan time: " + (end - start) + "ms #" + filesCollection.size());
//		Query allContextImplementations = ClassInheritorsSearch.search(contextBaseClass, searchScope, true);
//		Collection all = allContextImplementations.findAll();
//		filesCollection = FileTypeIndex.getFiles(fileType, GlobalSearchScope.projectScope(project));
	}

	@Override
	public void setProgressListener(IndicateProgressListener progressListener) {
		this.progressListener = progressListener;
	}

}