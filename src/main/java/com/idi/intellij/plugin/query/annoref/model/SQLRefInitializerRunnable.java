package com.idi.intellij.plugin.query.annoref.model;

import com.idi.intellij.plugin.query.annoref.component.SQLRefFileEditorListener;
import com.idi.intellij.plugin.query.annoref.component.SQLRefFileSystemListener;
import com.idi.intellij.plugin.query.annoref.component.SQLRefMessageBusAccessor;
import com.idi.intellij.plugin.query.annoref.util.AnnRefApplication;
import com.idi.intellij.plugin.query.annoref.util.SQLRefDataAccessor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.PackageIndex;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * Created by IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 26/03/2011
 * Time: 12:24:02
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class SQLRefInitializerRunnable implements Runnable {

	private static final Logger LOGGER = Logger.getInstance(SQLRefInitializerRunnable.class.getName());

	private Project project;

	public SQLRefInitializerRunnable(Project project) {
		this.project = project;
	}

	@Override
	public void run() {
		AnnRefApplication.getInstance(project, SQLRefDataAccessor.class).setProject(project);
		AnnRefApplication.getInstance().initializeManagersForProject(project);
		final VirtualFile[] xmlVfToScan = PackageIndex.getInstance(project).getDirectoriesByPackageName("queries", true);
		AnnRefApplication.getInstance(project, SQLRefDataAccessor.class).lookForConventionalReferencedFile(project, xmlVfToScan);
		final VirtualFile[] classVfToScan = PackageIndex.getInstance(project).getDirectoriesByPackageName("com.idi", true);
		AnnRefApplication.getInstance(project, SQLRefDataAccessor.class).lookForConventionalReferencedFile(project, classVfToScan);
		LOGGER.info("Starting SQLRef correlation");
		AnnRefApplication.getInstance(project, SQLRefDataAccessor.class).findNonCorrelatingSQLRefsInXmlFiles();
		initBusListeners();
	}

	private void initBusListeners() {
		final SQLRefMessageBusAccessor busAccessor = ApplicationManager.getApplication().getComponent(SQLRefMessageBusAccessor.class);
		busAccessor.initializeMessageBusAccessor(project);
	/*    project.getMessageBus().connect().subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootAdapter() {
		    public void rootsChanged(ModuleRootEvent event) {
			    event.isCausedByFileTypesChange();
			    event.getSource();
		    }
	    });*/
		busAccessor.registerListener(AnnRefApplication.getInstance(project, SQLRefFileSystemListener.class));
		busAccessor.registerListener(AnnRefApplication.getInstance(project, SQLRefFileEditorListener.class));
	}
}
