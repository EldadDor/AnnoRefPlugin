package com.idi.intellij.plugin.query.annoref.index;

import com.idi.intellij.plugin.query.annoref.util.AnnRefApplication;
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
		try {
			SQLRefXmlFileIndex refXmlFileIndex = new SQLRefXmlFileIndex(project);
			AnnRefApplication.addScanner();
			refXmlFileIndex.indexSQLRef();
		} finally {
			AnnRefApplication.removeScanner();
		}
	}
}
