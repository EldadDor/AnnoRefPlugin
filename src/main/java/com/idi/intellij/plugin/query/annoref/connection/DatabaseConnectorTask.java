/*
 * User: eldad.Dor
 * Date: 14/12/2014 10:48
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.connection;

import com.idi.intellij.plugin.query.annoref.notification.AnnoRefNotifications;
import com.idi.intellij.plugin.query.annoref.thread.BackgroundTask;
import com.idi.intellij.plugin.query.annoref.util.AnnoRefBundle;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author eldad
 * @date 14/12/2014
 */
public class DatabaseConnectorTask extends BackgroundTask {
	private static final Logger log = Logger.getInstance(DatabaseConnectorTask.class.getName());

	private DataSourceAccessorComponent dataSourceAccessorComponent;

	DatabaseConnectorTask(DataSourceAccessorComponent dataSourceAccessorComponent, @Nullable Project project, @NotNull String title, boolean canBeCancelled, ProgressIndicator progressIndicator) {
		super(project, title, canBeCancelled, progressIndicator);
		this.dataSourceAccessorComponent = dataSourceAccessorComponent;

	}

	DatabaseConnectorTask(DataSourceAccessorComponent dataSourceAccessorComponent, @Nullable Project project, @NotNull String title, boolean startInBackground, boolean canBeCancelled) {
		super(project, title, startInBackground, canBeCancelled);
		this.dataSourceAccessorComponent = dataSourceAccessorComponent;
	}

	@Override
	protected void execute(@NotNull ProgressIndicator paramProgressIndicator) throws InterruptedException {
		try {
			paramProgressIndicator.start();
			boolean result = dataSourceAccessorComponent.testConnection(getProject());
			if (result) {
				dataSourceAccessorComponent.connectionStateful.set(true);
			} else {
				ServiceManager.getService(getProject(), AnnoRefNotifications.class).notifyAnnoRefError(getProject(),
						AnnoRefBundle.message("annoRef.datasource.test.connect.error", "", dataSourceAccessorComponent.getDataSource().getName()));
				dataSourceAccessorComponent.connectionStateful.set(false);
			}
		} catch (Exception e) {
			log.error("run(): ERROR=" + e.getMessage(), e);
			ServiceManager.getService(getProject(), AnnoRefNotifications.class).notifyAnnoRefError(getProject(),
					AnnoRefBundle.message("annoRef.datasource.connect.error", dataSourceAccessorComponent.getDataSource().getName()));
		} finally {
			paramProgressIndicator.stop();
			paramProgressIndicator.cancel();
			dataSourceAccessorComponent.processFinished.set(true);
		}
	}
}