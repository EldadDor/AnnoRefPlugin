/*
 * User: eldad.Dor
 * Date: 27/01/14 11:16
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.sqlref.component;

import com.google.common.collect.Maps;
import com.idi.intellij.plugin.query.sqlref.common.SQLRefConstants;
import com.idi.intellij.plugin.query.sqlref.index.listeners.ProgressChangedListener;
import com.idi.intellij.plugin.query.sqlref.index.progress.DatabaseConnectorProgress;
import com.idi.intellij.plugin.query.sqlref.util.AnnoRefBundle;
import com.intellij.javaee.dataSource.DataSource;
import com.intellij.javaee.dataSource.DataSourceManager;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.notification.NotificationsAdapter;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.ui.PopupBorder;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author eldad
 * @date 27/01/14
 */
public class DataSourceAccessorComponent implements ProjectComponent {
	private static final Logger LOGGER = Logger.getInstance(DataSourceAccessorComponent.class.getName());
	private final Project project;

	private DataSource dataSource;

	@Override
	public void projectOpened() {

	}

	@Override
	public void projectClosed() {

	}

	@Override
	public void initComponent() {

	}

	@Override
	public void disposeComponent() {

	}

	@NotNull
	@Override
	public String getComponentName() {
		return DataSourceAccessorComponent.class.getName();
	}

	public DataSourceAccessorComponent(Project project) {
		this.project = project;
	}

	public String fetchSpForViewing(final String spName, final Project project) throws SQLException {
		LOGGER.info("fetchSpForViewing(): spName=" + spName);
		final StringBuilder spTextBuilder = new StringBuilder();
		final DatabaseConnectorProgress[] sqlRefProgressIndicator = {null};
		final ExecutorService executorService = Executors.newSingleThreadExecutor();
		if (ApplicationManager.getApplication().isDispatchThread()) {
			sqlRefProgressIndicator[0] = new DatabaseConnectorProgress(project, "Connection to DB sp_helptext", PerformInBackgroundOption.ALWAYS_BACKGROUND, "Cancel Task", "", true);
			final DatabaseConnectorProgress finalSqlRefProgressIndicator = sqlRefProgressIndicator[0];
			final ProgressChangedListener progressListener = new ProgressChangedListener() {
				@Override
				public synchronized void changeMade(boolean isChanged) {
					if (isChanged) {
						finalSqlRefProgressIndicator.setFraction(finalSqlRefProgressIndicator.getFraction() + 0.5d);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("changeMade(): currentFraction()= " + finalSqlRefProgressIndicator.getFraction());
						}
					}
				}

				@Override
				public void failedProcess() {
					LOGGER.info("failedProcess(): finalSqlRefProgressIndicator=" + finalSqlRefProgressIndicator.isRunning());
					LOGGER.info("failedProcess(): sqlRefProgressIndicator[0]=" + sqlRefProgressIndicator[0].isRunning());
					finalSqlRefProgressIndicator.cancel();
					LOGGER.info("failedProcess(): finalSqlRefProgressIndicator=" + finalSqlRefProgressIndicator.isRunning());
					LOGGER.info("failedProcess(): sqlRefProgressIndicator[0]=" + sqlRefProgressIndicator[0].isRunning());
				}
			};
			final SPConnectionRunnable finalSpConnectionRunnable = new SPConnectionRunnable(executorService, progressListener, spName, spTextBuilder);
			final Task.Backgroundable backgroundable = new Task.Backgroundable(project, "Connection to DB sp_helptext", true, PerformInBackgroundOption.ALWAYS_BACKGROUND) {
				@Override
				public void run(@NotNull ProgressIndicator progressIndicator) {
					sqlRefProgressIndicator[0].start();
					executorService.submit(finalSpConnectionRunnable);
				}
			};
			backgroundable.run(sqlRefProgressIndicator[0]);
		}
		while (sqlRefProgressIndicator[0] == null || sqlRefProgressIndicator[0].isRunning()) {
		}
		return spTextBuilder.toString();

	}


	private String spHelpTextInvokeLater(String spName, Project project) throws Exception {
		if (dataSource != null) {
			PreparedStatement stmt = null;
			ResultSet resultSet = null;
			try {
				final String query = AnnoRefBundle.message("annoRef.sp.helpText");
				final Connection connection = dataSource.getConnection(project);
				LOGGER.info("fetchSpForViewing(): Database=" + connection.getCatalog());
				stmt = connection.prepareStatement(query);
				stmt.setString(1, spName);
				resultSet = stmt.executeQuery();
				StringBuilder b = new StringBuilder();
				while (resultSet.next()) {
					b.append(resultSet.getString("text"));
				}
				return b.toString();
			} finally {
				if (resultSet != null) {
					resultSet.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			}
		}
		final Notification notification = new Notification(NotificationsAdapter.SYSTEM_MESSAGES_GROUP_ID, "SPViewer connection failure", "Datasource is " + dataSource + ", can't connect to fetch SP", NotificationType.ERROR);

//		SQLRefApplication.getInstance().register(NotificationsAdapter.SYSTEM_MESSAGES_GROUP_ID, NotificationDisplayType.BALLOON);

		final MessageBusConnection connect = project.getMessageBus().connect();

		final Notifications notifications = project.getMessageBus().syncPublisher(Notifications.TOPIC);
		connect.deliverImmediately();
//		NotificationsUtil.wrapListener(SQLRefApplication.getInstance(), MessageBus.class)
//		final EventLog.LogEntry logEntry = EventLog.formatForLog(notification, "");
//		EventLog.getLogModel(project).getNotifications().add(notification);
//		final Trinity<Notification, String, Long> statusMessage = EventLog.getStatusMessage(project);
//		NotificationsConfigurationImpl.getNotificationsConfiguration().notify(notification);
//		final AnnoRefNotifications annoRefNotifications = new AnnoRefNotifications();


		/*ApplicationManager.getApplication().executeOnPooledThread(new Runnable(localMessageBus, localNotification) {
			@Override
			public void run() {

			}
		}*/
//		annoRefNotifications.notify(notification);
//		final String s = NotificationsUtil.buildHtml(notification, "Hello...lalalala");

//		NotificationsUtil.wrapListener(notification);
		LOGGER.info("fetchSpForViewing(): DataSource=" + dataSource);
		return null;
	}


	@NotNull
	private JComponent constructAMessagePopUp(String message, Icon icon) {
		final PopupBorder popupBorder = PopupBorder.Factory.create(true, true);

		JPanel panel = new JPanel(new BorderLayout(500, 900));
		panel.setBorder(popupBorder);
		final JLabel messageLabel = new JBLabel(icon);
		messageLabel.setText(message);
		panel.add(messageLabel);
		panel.setBackground(SQLRefConstants.MessagePopupBackgroundColor);
		return panel;
	}


	public String getConfiguredDataSourceName() {
		if (dataSource != null) {
			return dataSource.getName();
		}
		return null;
	}


	public boolean initDataSource(Project project, String dataSourceName) {
		if (dataSourceName.isEmpty()) {
			dataSource = null;
			return false;
		}
		if (dataSource == null) {
			DataSourceManager dataSourceManager = DataSourceManager.getInstance(project);
			dataSource = dataSourceManager.getDataSourceByName(dataSourceName);
			if (dataSource == null) {
				LOGGER.info("initDataSource(): No DataSource with name=" + dataSourceName + " was found!");
				return false;
			}
		}
		return true;
	}

	public boolean testConnection(Project project) {
		if (dataSource != null) {
			PreparedStatement stmt = null;
			ResultSet resultSet = null;
			try {
				final String query = AnnoRefBundle.message("annoRef.sp.testConnection");
				stmt = dataSource.getConnection(project).prepareStatement(query);
				resultSet = stmt.executeQuery();
				if (resultSet.next()) {
					return true;
				}
			} catch (Exception e) {
				LOGGER.error("testConnection(): Exception=" + e.getMessage(), e);
			} finally {
				try {
					resultSet.close();
					stmt.close();
				} catch (SQLException e) {
					LOGGER.error("testConnection(): Exception=" + e.getMessage(), e);
				}
			}
		}
		return false;
	}

	public Collection<String> getAvailableConnections(Project project) {
		DataSourceManager dataSourceManager = DataSourceManager.getInstance(project);
		final Map<String, String> dataSourceNames = Maps.newHashMap();
		final List<DataSource> dataSources = dataSourceManager.getDataSources();
		for (final DataSource source : dataSources) {
			dataSourceNames.put(source.getName(), source.getName());
		}
		return dataSourceNames.values();
	}

	class SPConnectionRunnable implements Runnable {
		private ExecutorService executor;
		private final ProgressChangedListener progressListener;
		private final String spName;
		private final StringBuilder spTextBuilder;

		SPConnectionRunnable(ExecutorService executor, ProgressChangedListener progressListener, String spName, StringBuilder spTextBuilder) {
			this.executor = executor;
			this.progressListener = progressListener;
			this.spName = spName;
			this.spTextBuilder = spTextBuilder;
		}

		@Override
		public void run() {
			try {
				progressListener.changeMade(true);
				LOGGER.info("run(): spName=" + spName);
				final String spText = spHelpTextInvokeLater(spName, project);
				if (spText == null || spText.isEmpty()) {
					progressListener.failedProcess();
				}
				spTextBuilder.append(spText);
				progressListener.changeMade(true);
			} catch (Exception e) {
				LOGGER.error("run(): ERROR=" + e.getMessage(), e);
				progressListener.failedProcess();
				if (!executor.isShutdown() || !executor.isTerminated()) {
					executor.shutdown();
				}
			}
		}
	}
}