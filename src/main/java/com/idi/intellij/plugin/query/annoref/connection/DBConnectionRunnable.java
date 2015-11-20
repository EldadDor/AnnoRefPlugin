/*
 * User: eldad.Dor
 * Date: 13/12/2014 22:54
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.connection;

import com.idi.intellij.plugin.query.annoref.index.listeners.IndexProgressChangedListener;
import com.idi.intellij.plugin.query.annoref.notification.AnnoRefNotifications;
import com.idi.intellij.plugin.query.annoref.util.AnnoRefBundle;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;

import java.util.concurrent.ExecutorService;

/**
 * @author eldad
 * @date 13/12/2014
 */
public class DBConnectionRunnable implements Runnable {
	private static final Logger log = Logger.getInstance(DBConnectionRunnable.class.getName());
	private final Project project;
	private DataSourceAccessorComponent dataSourceAccessorComponent;
	private final IndexProgressChangedListener progressListener;
	private ExecutorService executor;

	DBConnectionRunnable(Project project, DataSourceAccessorComponent dataSourceAccessorComponent, ExecutorService executor, IndexProgressChangedListener progressListener) {
		this.dataSourceAccessorComponent = dataSourceAccessorComponent;
		this.executor = executor;
		this.progressListener = progressListener;
		this.project = project;
	}

	@Override
	public void run() {
		try {
			progressListener.changeMade(true);
			final boolean result = dataSourceAccessorComponent.testConnection(project);
			if (result) {
				progressListener.changeMade(true);
				dataSourceAccessorComponent.connectionStateful.set(true);
			} else {
				ServiceManager.getService(project, AnnoRefNotifications.class).notifyAnnoRefError(project, AnnoRefBundle.message("annoRef.datasource.test.connect.error", "",
						dataSourceAccessorComponent.getDataSource().getName()));
				progressListener.failedProcess(null);
				dataSourceAccessorComponent.connectionStateful.set(false);
			}
		} catch (Exception e) {
			log.error("run(): ERROR=" + e.getMessage(), e);
			ServiceManager.getService(project, AnnoRefNotifications.class).notifyAnnoRefError(project, AnnoRefBundle.message("annoRef.datasource.connect.error",
					dataSourceAccessorComponent.getDataSource().getName()));
			progressListener.failedProcess(null);
			if (!executor.isShutdown() || !executor.isTerminated()) {
				executor.shutdown();
			}
		} finally {
			progressListener.finishedProcess();
			dataSourceAccessorComponent.processFinished.set(true);
		}
	}
}