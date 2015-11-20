/*
 * User: eldad.Dor
 * Date: 01/04/2015 10:47
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.task;

import com.idi.intellij.plugin.query.annoref.persist.AnnoRefConfigSettings;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefSettings;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.jam.model.util.JamCommonUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.progress.util.ProgressWindow;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;

import java.util.Collection;

/**
 * @author eldad
 * @date 01/04/2015
 */
public class ModuleScanningTask extends IDIAbstractTask {
	private static final Logger logger = Logger.getInstance(ModuleScanningTask.class);


	public ModuleScanningTask(Project project) {
		super(project, JavaFileType.INSTANCE);
	}

	@Override
	public void run(IDIProgressIndicator progressIndicator) throws IDIProcessCancelledException {


		Module[] sortedModules = ModuleManager.getInstance(project).getSortedModules();
		final AnnoRefSettings annoRefState = AnnoRefConfigSettings.getInstance(project).getAnnoRefState();
		Collection<PsiClass> psiClasses;
		for (final Module sortedModule : sortedModules) {
			psiClasses = JamCommonUtil.getAnnotationTypesWithChildren(annoRefState.SP_VIEW_ANNOTATION_FQN, sortedModule);
			logger.info("projectOpened():");
		}
	}

	@Override
	public void run(ProgressWindow progressWindow) {

	}

	@Override
	public boolean runComputableTask(IDIProgressIndicator progressIndicator) throws IDIProcessCancelledException {
		return false;
	}



/*	public Set<VirtualFilePointer> getCodeConfigurationFiles() {
   Set<VirtualFilePointer> filePointers = new HashSet<VirtualFilePointer>();
   for (VirtualFilePointer virtualFilePointer : myFiles) {
     if (!virtualFilePointer.isValid()) continue;

     VirtualFile virtualFile = virtualFilePointer.getFile();
     if (virtualFile != null &&
         !isFileType(StdFileTypes.XML, virtualFile) &&
         !isFileType(StdFileTypes.PROPERTIES, virtualFile)) {
       PsiFile psiFile = PsiManager.getInstance(getFacet().getModule().getProject()).findFile(virtualFile);
       if (psiFile instanceof PsiClassOwner) {
         filePointers.add(virtualFilePointer);
       }
     }
   }
   return filePointers;
 }
	*/

/*
	protected VirtualFilePointer createVirtualFilePointer(@NotNull String url) {
   return VirtualFilePointerManager.getInstance().create(url, this, null);
 }
	*/

	@Override
	public int numOfFiles() {
		return 0;
	}

	@Override
	public String getTaskName() {
		return getClass().getSimpleName();
	}
}