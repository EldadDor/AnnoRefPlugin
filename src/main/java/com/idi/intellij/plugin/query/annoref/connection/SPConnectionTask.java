/*
 * User: eldad.Dor
 * Date: 11/12/2014 17:31
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.connection;

import com.idi.intellij.plugin.query.annoref.annotations.NotInUse;
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
 * @date 11/12/2014
 */
@NotInUse
public class SPConnectionTask extends BackgroundTask {
	private static final Logger logger = Logger.getInstance(SPConnectionTask.class.getName());

	private final StringBuilder spTextBuilder;
	private final String spName;
	private final DataSourceAccessorComponent dataSourceAccessorComponent;

	protected SPConnectionTask(@Nullable Project project, @NotNull String title, boolean canBeCancelled, ProgressIndicator progressIndicator, StringBuilder spTextBuilder, String spName, DataSourceAccessorComponent dataSourceAccessorComponent) {
		super(project, title, canBeCancelled, progressIndicator);
		this.spTextBuilder = spTextBuilder;
		this.spName = spName;
		this.dataSourceAccessorComponent = dataSourceAccessorComponent;
	}

	@Override
	protected void execute(@NotNull ProgressIndicator paramProgressIndicator) throws InterruptedException {
		try {
			progressIndicator.start();
			logger.info("execute(): spName=" + spName);
			final String spText = dataSourceAccessorComponent.spHelpTextInvokeLater(getProject(), spName);
			if (spText == null || spText.isEmpty()) {
				ServiceManager.getService(getProject(), AnnoRefNotifications.class).notifyAnnoRefError(getProject(), AnnoRefBundle.message("annoRef.datasource.spview.notfound",
						spName, ConnectionUtil.getDataSourceCatalog()));
			}
			spTextBuilder.append(spText);
		} catch (Exception e) {
			logger.error("execute(): ERROR=" + e.getMessage(), e);
			ServiceManager.getService(getProject(), AnnoRefNotifications.class).notifyAnnoRefError(getProject(), AnnoRefBundle.message("annoRef.datasource.connect.error",
					ConnectionUtil.getDataSourceCatalog()));
		} finally {
			dataSourceAccessorComponent.processFinished.set(true);
			if (progressIndicator.isRunning()) {
				progressIndicator.stop();
			}
		}
	}
}