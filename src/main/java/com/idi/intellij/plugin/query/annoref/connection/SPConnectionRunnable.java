/*
 * User: eldad.Dor
 * Date: 13/11/2014 12:47
 
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
 * @date 13/11/2014
 */
public class SPConnectionRunnable implements Runnable {
	private static final Logger LOGGER = Logger.getInstance(SPConnectionRunnable.class.getName());
	private final Project project;
	private DataSourceAccessorComponent dataSourceAccessorComponent;
	private final IndexProgressChangedListener progressListener;
	private final String spName;
	private final StringBuilder spTextBuilder;
	private ExecutorService executor;

	SPConnectionRunnable(DataSourceAccessorComponent dataSourceAccessorComponent, Project project, ExecutorService executor,
	                     IndexProgressChangedListener progressListener, String spName, StringBuilder spTextBuilder) {
		this.dataSourceAccessorComponent = dataSourceAccessorComponent;
		this.executor = executor;
		this.progressListener = progressListener;
		this.spName = spName;
		this.spTextBuilder = spTextBuilder;
		this.project = project;
	}

	@Override
	public void run() {
		try {
			progressListener.changeMade(true);
			LOGGER.info("run(): spName=" + spName);
			final String spText = dataSourceAccessorComponent.spHelpTextInvokeLater(project, spName);
			if (spText == null || spText.isEmpty()) {
				ServiceManager.getService(project, AnnoRefNotifications.class).notifyAnnoRefError(project, AnnoRefBundle.message("annoRef.datasource.spview.notfound",
						spName, ConnectionUtil.getDataSourceCatalog()));
				progressListener.failedProcess(null);
			}
			spTextBuilder.append(spText);
			progressListener.changeMade(true);

		} catch (Exception e) {
			LOGGER.error("run(): ERROR=" + e.getMessage(), e);

			ServiceManager.getService(project, AnnoRefNotifications.class).notifyAnnoRefError(project, AnnoRefBundle.message("annoRef.datasource.connect.error",
					ConnectionUtil.getDataSourceCatalog()));
			progressListener.failedProcess(null);
			if (!executor.isShutdown() || !executor.isTerminated()) {
				executor.shutdown();
			}
		} finally {
			dataSourceAccessorComponent.processFinished.set(true);
			progressListener.finishedProcess();
		}
	}
}