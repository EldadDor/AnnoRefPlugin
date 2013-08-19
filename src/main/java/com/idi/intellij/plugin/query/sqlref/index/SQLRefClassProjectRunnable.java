package com.idi.intellij.plugin.query.sqlref.index;

import com.idi.intellij.plugin.query.sqlref.util.SQLRefApplication;
import com.intellij.openapi.project.Project;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 7/11/13
 * Time: 1:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class SQLRefClassProjectRunnable implements Runnable {
	private final Project project;

	public SQLRefClassProjectRunnable(Project project) {
		this.project = project;
	}

	@Override
	public void run() {
		SQLRefClassFileIndex sqlRefClassFileIndex = new SQLRefClassFileIndex(project);
		SQLRefApplication.addScanner();
		sqlRefClassFileIndex.indexSQLRef();
		SQLRefApplication.removeScanner();
	}
}
