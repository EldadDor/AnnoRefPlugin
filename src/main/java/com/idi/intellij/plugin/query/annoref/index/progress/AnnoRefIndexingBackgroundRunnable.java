/*
 * User: eldad.Dor
 * Date: 14/07/2014 11:27
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.index.progress;

import com.idi.intellij.plugin.query.annoref.index.SQLRefRepository;
import com.idi.intellij.plugin.query.annoref.index.listeners.IndexProgressChangedListener;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;

import java.util.concurrent.ExecutorService;

/**
 * @author eldad
 * @date 14/07/2014
 */
public class AnnoRefIndexingBackgroundRunnable implements Runnable {
	private static final Logger logger = Logger.getInstance(AnnoRefIndexingBackgroundRunnable.class.getName());
	private final Project project;
	private final IndexProgressChangedListener progressListener;
	private final ExecutorService executor;

	public AnnoRefIndexingBackgroundRunnable(Project project, IndexProgressChangedListener progressListener, ExecutorService executor) {
		this.project = project;
		this.progressListener = progressListener;
		this.executor = executor;
	}


	@Override
	public void run() {
		ServiceManager.getService(project, SQLRefRepository.class).resetAllProjectOnModulesChange();

		try {
//			IDITaskManager.run(project, "xml scanning", new XmlScanningTask(project));
//			IDITaskManager.run(project, "class scanning", new ClassScanningTask(project));
//		} catch (IDIProcessCancelledException e) {
		} catch (Exception e) {
			logger.error(e);
		}


//		SQLRefXmlFileIndex refXmlFileIndex = new SQLRefXmlFileIndex(project, progressListener);
//		SQLRefClassFileIndex sqlRefClassFileIndex = new SQLRefClassFileIndex(project, progressListener);
//		refXmlFileIndex.indexSQLRef();
//		sqlRefClassFileIndex.indexSQLRef();
	}
}