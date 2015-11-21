/*
 * User: eldad.Dor
 * Date: 27/01/14 11:16
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.connection;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.idi.intellij.plugin.query.annoref.common.SQLRefConstants;
import com.idi.intellij.plugin.query.annoref.index.listeners.IndexProgressChangedListener;
import com.idi.intellij.plugin.query.annoref.index.progress.DatabaseConnectorProgress;
import com.idi.intellij.plugin.query.annoref.notification.AnnoRefNotifications;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefConfigSettings;
import com.idi.intellij.plugin.query.annoref.util.AnnoRefBundle;
import com.intellij.database.dataSource.DataSource;
import com.intellij.database.dataSource.DataSourceManager;
import com.intellij.database.model.info.DatabaseProcedureInfo;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.ui.PopupBorder;
import com.intellij.ui.components.JBLabel;
import org.jetbrains.annotations.Nls;
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
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author eldad
 * @date 27/01/14
 */
public class DataSourceAccessorComponent implements ProjectComponent {
	private static final Logger LOGGER = Logger.getInstance(DataSourceAccessorComponent.class.getName());
	private final Project project;
	public final AtomicBoolean processFinished = new AtomicBoolean(false);
	public final AtomicBoolean connectionStateful = new AtomicBoolean(false);
	private List<String> spNamesList;
	private DataSource dataSource;
	private final ConnectionPool connectionPool;

	public DataSourceAccessorComponent(Project project) {
		this.project = project;
		connectionPool = new ConnectionPool(project);
	}

	public DataSource getDataSource() {
		return dataSource;
	}

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

	public ConnectionPool getConnectionPool() {
		return connectionPool;
	}

	public void closeConnection() {
		try {
			connectionPool.closeConnections();
		/*	if (dataSource != null) {
				final Connection connection = dataSource.getConnection(project);
				if (!connection.isClosed()) {
					connection.close();
				}
			}*/
		} catch (Exception e) {
			LOGGER.error("closeConnection(): error=" + e.getMessage(), e);
		}
	}


	public String fetchSpForViewing(final String spName, final Project project, @Nls boolean dispatchThread, Connection connection) throws SQLException {
		LOGGER.info("fetchSpForViewing(): spName=" + spName);
		final StringBuilder spTextBuilder = new StringBuilder();
		final DatabaseConnectorProgress[] sqlRefProgressIndicator = {null};
		processFinished.set(false);
		if (dispatchThread) {
			try {
				if (connection != null) {
					spTextBuilder.append(spHelpTextForTemporaryConnection(project, spName, connection));
				} else {
					spTextBuilder.append(spHelpTextInvokeLater(project, spName));
				}
			} catch (Exception e) {
				LOGGER.error("fetchSpForViewing(): Error=" + e.getMessage(), e);
			} finally {
				processFinished.set(true);
			}
		} else {
			final ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newCachedThreadPool();
			final IndexProgressChangedListener progressListener = initializeProgressIndicator(project, "Connection to DB sp_helptext", sqlRefProgressIndicator, executorService);
			runInBackgroundWithIndicator(executorService, progressListener, project, spName, spTextBuilder, sqlRefProgressIndicator);
		}
		while (!processFinished.get()) {
		}

//		LOGGER.info("fetchSpForViewing(): returning content=" + spTextBuilder.toString());
		return spTextBuilder.toString();
	}

	public boolean testConnectionInBackground(Project project) {
		connectionStateful.set(false);
		LOGGER.info("testConnectionInBackground()");
		final DatabaseConnectorProgress[] sqlRefProgressIndicator = {null};
		processFinished.set(false);
		final ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		final IndexProgressChangedListener indexProgressChangedListener = initializeProgressIndicator(project, "Connection to DB Test", sqlRefProgressIndicator, executorService);
		runInBackgroundWithIndicator(executorService, indexProgressChangedListener, project, null, null, sqlRefProgressIndicator);
		while (!processFinished.get()) {
		}
		return connectionStateful.get();

	}

	public void runInBackgroundWithIndicator(final ExecutorService executorService, final IndexProgressChangedListener progressListener, final Project project, String spName,
	                                         StringBuilder spTextBuilder, DatabaseConnectorProgress[] sqlRefProgressIndicator) {
		final Runnable runnable;
		if (spTextBuilder == null) {
			runnable = new DBConnectionRunnable(project, this, executorService, progressListener);
		} else {
			runnable = new SPConnectionRunnable(this, project, executorService, progressListener, spName, spTextBuilder);
		}

//		final DispatchThreadProgressWindow progressWindow = new DispatchThreadProgressWindow(true, project);
//		final ProgressWindowWithNotification progressWindow = new ProgressWindowWithNotification(true,true, project);
//		progressWindow.setBackgroundHandler(runnable);
		/*final IndexProgressChangedListener progressListener1 = new DBConnectionProgressListener(progressWindow);
		progressWindow.setRunnable(runnable);
		progressWindow.start();*/
		final Task.Backgroundable backGroundAble = new Task.Backgroundable(project, "Connection to DB sp_helptext", true, PerformInBackgroundOption.ALWAYS_BACKGROUND) {
			@Override
			public void run(@NotNull ProgressIndicator progressIndicator) {
				LOGGER.info("run(): progressIndicator=" + progressIndicator);
				progressIndicator.start();
				LOGGER.info("run(): progressIndicator=" + progressIndicator);
				executorService.submit(runnable);
			}
		};

		runnable.run();
//		executorService.submit(runnable);
//		backGroundAble.run(sqlRefProgressIndicator[0]);

	}

	private IndexProgressChangedListener initializeProgressIndicator(Project project, String toolWindowDisplay, DatabaseConnectorProgress[] sqlRefProgressIndicator, ExecutorService executorService) {
		sqlRefProgressIndicator[0] = new DatabaseConnectorProgress(project, toolWindowDisplay, PerformInBackgroundOption.ALWAYS_BACKGROUND,
				"Cancel Task", "", true, executorService);
		final DatabaseConnectorProgress finalSqlRefProgressIndicator = sqlRefProgressIndicator[0];
		return getProgressChangedListener(finalSqlRefProgressIndicator);
	}

	private IndexProgressChangedListener getProgressChangedListener(final DatabaseConnectorProgress finalSqlRefProgressIndicator) {
		return new IndexProgressChangedListener() {
			@Override
			public synchronized void changeMade(boolean isChanged) {
				if (isChanged) {
					LOGGER.info("changeMade(): currentFraction()= " + finalSqlRefProgressIndicator.getFraction());
					finalSqlRefProgressIndicator.setFraction(finalSqlRefProgressIndicator.getFraction() + 0.00050d);

				}
			}

			@Override
			public void failedProcess(String errorMessage) {
				LOGGER.info("failedProcess(): finalSqlRefProgressIndicator=" + finalSqlRefProgressIndicator.isRunning());
				finalSqlRefProgressIndicator.cancel();
				LOGGER.info("failedProcess(): finalSqlRefProgressIndicator=" + finalSqlRefProgressIndicator.isRunning());
			}

			@Override
			public void finishedProcess() {
				finalSqlRefProgressIndicator.processFinish();
				if (finalSqlRefProgressIndicator.isRunning()) {
					LOGGER.info("finishedProcess(): progressIndicator isRunning=" + finalSqlRefProgressIndicator.isRunning());
					finalSqlRefProgressIndicator.setFraction(1);
					finalSqlRefProgressIndicator.processFinish();
				}
				LOGGER.info("finishedProcess():");
			}
		};
	}


	public boolean testConnection(Project project) throws SQLException {
		Connection connection = getConfiguredConnection(project);
		if (connection != null) {
			try {
				final String query = AnnoRefBundle.message("annoRef.sp.testConnection");
				PreparedStatement stmt = connection.prepareStatement(query);
				ResultSet resultSet = stmt.executeQuery();
				if (resultSet.next()) {
					return true;
				} else {
					ServiceManager.getService(project, AnnoRefNotifications.class).notifyAnnoRefError(project, AnnoRefBundle.message("annoRef.datasource.test.connect.error", dataSource.getName()));
				}
			} catch (Exception e) {
				LOGGER.error("testConnection(): Exception=" + e.getMessage(), e);
			} finally {
				try {
					connectionPool.releaseConnection(connection);
				} catch (Exception e) {
					ServiceManager.getService(project, AnnoRefNotifications.class).notifyAnnoRefError(project, e.getMessage());
					LOGGER.error("testConnection(): Exception=" + e.getMessage(), e);
				}
			}
		}
		return false;
	}

	public Connection getConfiguredConnection(Project project) throws SQLException {
		Connection connection = null;
		if (AnnoRefConfigSettings.getInstance(project).getAnnoRefState().CONNECTION_METHOD_WAY.equalsIgnoreCase("Single-Connection")) {
			connection = connectionPool.getStandaloneConnection(true);
		}
		if (AnnoRefConfigSettings.getInstance(project).getAnnoRefState().CONNECTION_METHOD_WAY.equalsIgnoreCase("Connection-Pool")) {
			connection = connectionPool.allocateConnection();
		}
		return connection;
	}


	public String spHelpTextForTemporaryConnection(final Project project, String spName, Connection connection) throws Exception {
		if (connection != null) {
			try {
				final String query = AnnoRefBundle.message("annoRef.sp.helpText");
				LOGGER.info("spHelpTextForTemporaryConnection(): Database=" + connection.getCatalog());
				PreparedStatement stmt = connection.prepareStatement(query);
				stmt.setString(1, spName);
				ResultSet resultSet = stmt.executeQuery();
				StringBuilder b = new StringBuilder();
				while (resultSet.next()) {
					b.append(resultSet.getString("text"));
				}
				return b.toString();
			} catch (Exception ex) {
				LOGGER.error("spHelpTextForTemporaryConnection(): error=" + ex.getMessage(), ex);
				throw ex;
			}
		} else {
			LOGGER.error("spHelpTextForTemporaryConnection(): Connection is null");
		}
		return null;
	}

	public String spHelpTextInvokeLater(final Project project, String spName) throws Exception {
		final Connection connection = getConfiguredConnection(project);
		if (connection != null) {
			try {
				final String query = AnnoRefBundle.message("annoRef.sp.helpText");
				LOGGER.info("fetchSpForViewing(): Database=" + connection.getCatalog());
				PreparedStatement stmt = connection.prepareStatement(query);
				stmt.setString(1, spName);
				ResultSet resultSet = stmt.executeQuery();
				StringBuilder b = new StringBuilder();
				while (resultSet.next()) {
					b.append(resultSet.getString("text"));
				}
				return b.toString();
			} finally {
				try {
					connectionPool.releaseConnection(connection);
				} catch (Exception e) {
					ServiceManager.getService(project, AnnoRefNotifications.class).notifyAnnoRefError(project, AnnoRefBundle.message("annoRef.datasource.connect.error", dataSource.getName()));
					LOGGER.error("spHelpTextInvokeLater(): Exception=" + e.getMessage(), e);
				}
			}
		} else {
			final String dataSourceName = AnnoRefConfigSettings.getInstance(project).getAnnoRefState().SP_DATA_SOURCE_NAME;
			ServiceManager.getService(project, AnnoRefNotifications.class).notifyAnnoRefError(project, AnnoRefBundle.message("annoRef.datasource.connect.error", dataSourceName));
		}
		LOGGER.info("fetchSpForViewing(): DataSource=" + dataSource);
		return null;
	}


	public List<String> getStoreProceduresNames() {
		if (dataSource == null) {
			dataSource = ConnectionUtil.getDataSource();
		}
		if (dataSource == null) {
			LOGGER.error("DataSource wasn't initialized");
		}
		if (spNamesList == null || spNamesList.size() != dataSource.getProcedures().size()) {
			spNamesList = Lists.newArrayListWithExpectedSize(dataSource.getProcedures().size());
			for (final DatabaseProcedureInfo databaseProcedure : dataSource.getProcedures()) {
				spNamesList.add(databaseProcedure.getName());
			}
		}
		return spNamesList;
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

	public boolean initDataSource(Project project, String dataSourceName, boolean initialize) {
		if (dataSourceName.isEmpty()) {
			dataSource = null;
			return false;
		}
		if (initialize || dataSource == null) {
			DataSourceManager dataSourceManager = DataSourceManager.getInstance(project);
			dataSource = dataSourceManager.getDataSourceByName(dataSourceName);
			if (dataSource == null) {
				LOGGER.info("initDataSource(): No DataSource with name=" + dataSourceName + " was found!");
				return false;
			}
		}
		return true;
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

}