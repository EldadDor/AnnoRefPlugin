package com.idi.intellij.plugin.query.sqlref.index;

import com.idi.intellij.plugin.query.sqlref.util.SQLRefApplication;
import com.intellij.openapi.project.Project;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 7/6/13
 * Time: 3:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class SQLRefIndexProjectRunnable implements Runnable {

	private final Project project;

	public SQLRefIndexProjectRunnable(Project project) {
		this.project = project;
	}


	@Override
	public void run() {
		SQLRefXmlFileIndex refXmlFileIndex = new SQLRefXmlFileIndex(project);
		SQLRefApplication.addScanner();
		refXmlFileIndex.indexSQLRef();
		SQLRefApplication.removeScanner();
	/*	project.getMessageBus().connect().subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootAdapter() {
			public void rootsChanged(ModuleRootEvent event) {
				event.isCausedByFileTypesChange();
				event.getSource();
			}
		});
		//To change body of implemented methods use File | Settings | File Templates.*/
	}
}
