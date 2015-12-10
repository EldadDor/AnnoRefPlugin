/*
 * User: eldad
 * Date: 23/01/11 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.component;

import com.idi.intellij.plugin.query.annoref.index.progress.SQLRefProgressIndicator;
import com.idi.intellij.plugin.query.annoref.model.FileReferenceCollection;
import com.idi.intellij.plugin.query.annoref.model.ReferenceCollectionManager;
import com.idi.intellij.plugin.query.annoref.model.SQLRefReference;
import com.idi.intellij.plugin.query.annoref.util.AnnRefApplication;
import com.idi.intellij.plugin.query.annoref.util.QueriesXmlVisitor;
import com.idi.intellij.plugin.query.annoref.util.SQLRefDataAccessor;
import com.idi.intellij.plugin.query.annoref.util.SQLRefNamingUtil;
import com.intellij.diagnostic.PluginException;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileAdapter;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class SQLRefFileSystemListener extends VirtualFileAdapter {

	private static final Logger LOGGER = Logger.getInstance(SQLRefFileSystemListener.class.getName());
	private Task myTask;
	//	private boolean taskInitialized = false;
	private Project project;

	@Override
	public void contentsChanged(VirtualFileEvent event) {
		try {
			PsiFile psiFile = isContentsChangingContendent(event);
			if (psiFile == null) {
				return;
			}
			if (AnnRefApplication.getInstance(project, SQLRefDataAccessor.class).isPropitiousXmlFile(psiFile)) {
				processXMLFileChange(event);
			} else {
				String propitiousClassFile = AnnRefApplication.getInstance(project, SQLRefDataAccessor.class).isPropitiousClassFile(psiFile);
				if (propitiousClassFile != null) {
					processClassFileChange(propitiousClassFile, psiFile);
				}
			}
			super.contentsChanged(event);
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private PsiFile isContentsChangingContendent(VirtualFileEvent event) {
		PsiFile psiFile = null;        // todo kiss it
		if (event.isFromSave()) {
			psiFile = PsiManager.getInstance(initializeProjectForVF(event.getFile())).findFile(event.getFile());
//			if (psiFile == null || !SQLRefApplication.getInstance(project, SQLRefNamingUtil.class).isPropitiousXmlFile(psiFile)) {
			if (psiFile == null || !SQLRefNamingUtil.isPropitiousXmlFile(psiFile)) {
				return null;
			}
			LOGGER.info("Scanning file content changed : " + psiFile.getName());
		}
		return psiFile;
	}

	@Override
	public void beforeContentsChange(VirtualFileEvent event) {
		try {
			cloneBeforeContentsAndResetAfterMap(event);
		} catch (Exception e) {
			LOGGER.error(e);
		}
		super.beforeContentsChange(event);
	}

	@Override
	public void fileCreated(VirtualFileEvent event) {
		super.fileCreated(event);
	}

	@Override
	public void fileDeleted(VirtualFileEvent event) {
		super.fileDeleted(event);
	}

	@Override
	public void beforeFileDeletion(VirtualFileEvent event) {
		super.beforeFileDeletion(event);
	}

	private void processXMLFileChange(@NotNull final VirtualFileEvent virtualFileEvent) {
		try {
			initializeProjectForVF(virtualFileEvent.getFile());
			final PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(FileDocumentManager.getInstance().getDocument(virtualFileEvent.getFile()));
			final ProgressIndicator progress = new SQLRefProgressIndicator(project, "RexIndexing SQLRef usages...",
					PerformInBackgroundOption.DEAF, "Cancel SQLRef Indexing", "", true);
			if (ApplicationManager.getApplication().isDispatchThread()) {
				LOGGER.warn("Trying to invoke Runnable on the MainDispatchThread, execution with invokeLater is redundant !");
				ProgressManager.getInstance().runProcess(new Runnable() {
					@Override
					public void run() {
						try {
							processXMLFileChangeInternal(virtualFileEvent, psiFile);
						} catch (Exception e) {
							LOGGER.error(e);
						}
					}
				}, progress);
			} else {
				ApplicationManager.getApplication().invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							ProgressManager.getInstance().runProcess(new Runnable() {
								@Override
								public void run() {
									try {
										processXMLFileChangeInternal(virtualFileEvent, psiFile);
									} catch (Exception e) {
										LOGGER.error(e);
									}
								}
							}, progress);
						} catch (Exception e) {
							LOGGER.error(e);
						}
					}
				});
			}
		} catch (Exception e) {
			LOGGER.error(e);
			System.out.println("e = " + e.getMessage());
		}
	}

	private void processXMLFileChangeInternal(VirtualFileEvent virtualFileEvent, PsiFile psiFile) throws Exception {
		LOGGER.info("ReVisiting xml file : " + virtualFileEvent.getFileName());
		QueriesXmlVisitor.getInstance().visitFile(psiFile);
		FileReferenceCollection newReferenceCollection = ReferenceCollectionManager.getInstance(project).getQueriesCollection(virtualFileEvent.getFileName(), false);
		LOGGER.info("Cloning RangeHighlighters");
		ReferenceCollectionManager.getInstance(project).cloneBeforeRangeHighlighterToAfter(newReferenceCollection);
		LOGGER.info("Revisiting XML References to reference classes from Cache");
		ReferenceCollectionManager.getInstance(project).revisitXMLReferenceCollectionAfterContentsChangedViaCache(project, newReferenceCollection);
	}

	private void cloneBeforeContentsAndResetAfterMap(VirtualFileEvent virtualFileEvent) throws Exception {
		if (isContentsChangingContendent(virtualFileEvent) != null) {
			ReferenceCollectionManager.getInstance(project).resetXMLSQLReferencesInCollection(virtualFileEvent.getFileName());
		}
	}

	private void processClassFileChange(@NotNull String classAnnoRef, @NotNull PsiFile psiFile) {
		SQLRefReference referenceInCollections = ReferenceCollectionManager.getInstance(project).findReferenceInCollections(classAnnoRef);
		if (referenceInCollections != null) {
			referenceInCollections.removeClassAnnoReference(psiFile.getName());
			LOGGER.info("ReVisiting class file : " + psiFile.getName());
			AnnRefApplication.getInstance(project, SQLRefDataAccessor.class).lookForConventionalAnnotatedClassReference(psiFile);
		}
	}

/*	private void initializeProjectForVFViaCache(VirtualFile vfPath) {
		project = SQLRefApplication.getFilesCorrespondingProject(vfPath.getPath());
	}*/

	private Project initializeProjectForVF(VirtualFile vf) {
		Project guessedProject;
		try {
			guessedProject = ProjectUtil.guessProjectForFile(vf);
			if (guessedProject != null) {
				Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
				for (final Project openProject : openProjects) {
					if (guessedProject.equals(openProject)) {
						LOGGER.info("FileSystemListener Guessed Project : " + guessedProject.getName());
						project = guessedProject;
					}
				}
			}
		} catch (Exception e) {
			throw new PluginException(e.getMessage(), e.getCause(), PluginId.getId(XmlRepositorySyncComponent.COMPONENT_NAME));
		}
		if (guessedProject == null) {
			throw new PluginException("Failed to guess Project for VirtualFile:" + vf.getPath(), PluginId.getId(XmlRepositorySyncComponent.COMPONENT_NAME));
		}
		return guessedProject;
	}
}