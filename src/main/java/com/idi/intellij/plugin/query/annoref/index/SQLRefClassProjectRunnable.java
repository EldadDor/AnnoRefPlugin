package com.idi.intellij.plugin.query.annoref.index;

import com.idi.intellij.plugin.query.annoref.util.AnnRefApplication;
import com.intellij.openapi.project.Project;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 7/11/13
 * Time: 1:04 AM
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class SQLRefClassProjectRunnable implements Runnable {
	private final Project project;

	@Deprecated
	public SQLRefClassProjectRunnable(Project project) {
		this.project = project;
	}

	@Override
	public void run() {
		try {
//			SQLRefClassFileIndex sqlRefClassFileIndex = new SQLRefClassFileIndex(project);
//			AnnRefApplication.addScanner();
//			sqlRefClassFileIndex.indexSQLRef();
		} finally {
			AnnRefApplication.removeScanner();
		}
	}
}
