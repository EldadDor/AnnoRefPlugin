package com.idi.intellij.plugin.query.annoref.connection;

import com.google.common.collect.Maps;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefConfigSettings;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefSettings;
import com.intellij.database.dataSource.DataSource;
import com.intellij.database.dataSource.DataSourceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;

import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConnectionUtil {
	private static final Logger logger = Logger.getInstance(ConnectionUtil.class.getName());
	public static final String[] OPTIONS_CONNECT_CANCEL = {"Connect", "Cancel"};
	private static DataSource dataSource;
	private static WeakReference<DataSource> tempDataSource;


	public static void closeResultSet(ResultSet resultSet) {
		try {
			if (resultSet != null && !resultSet.isClosed()) {
				resultSet.close();
			}
		} catch (Throwable e) {
			logger.warn("Error closing result set", e);
		}
	}

	public static void closeStatement(Statement statement) {
		try {
			if (statement != null && !statement.isClosed()) {
				statement.close();
			}
		} catch (Throwable e) {
			logger.warn("Error closing statement", e);
		}
	}

	public static void closeConnection(final Connection connection, final Project project) {
		if (connection != null) {
			new SimpleBackgroundTask(connection) {
				@Override
				public void execute() {
					try {
						connection.close();
					} catch (Throwable e) {
						ConnectionUtil.logger.warn("Error closing connection", e);
					}
				}
			}.start();
		}
	}

	public static boolean initializeDefaultDataSource(Project project) {
		if (dataSource == null) {
			final AnnoRefSettings sqlRefState = AnnoRefConfigSettings.getInstance(project).getAnnoRefState();
			logger.info("initializeDefaultDataSource(): for DB=" + sqlRefState.SP_DATA_SOURCE_NAME);
			return initDataSource(project, sqlRefState.SP_DATA_SOURCE_NAME);
		}
		return dataSource != null;
	}


	public static boolean initTempDataSource(Project project, String selectDataSource) {
		if (selectDataSource == null || selectDataSource.isEmpty()) {
			tempDataSource = null;
			return false;
		}
		if (tempDataSource == null || !tempDataSource.get().getName().equalsIgnoreCase(selectDataSource)) {
			DataSourceManager dataSourceManager = DataSourceManager.getInstance(project);
			tempDataSource = new WeakReference<DataSource>(dataSourceManager.getDataSourceByName(selectDataSource));
			if (tempDataSource == null) {
				logger.error("initTempDataSource(): No DataSource with name=" + selectDataSource + " was found!");
				return false;
			}
		}
		return true;
	}

	public static boolean initDataSource(Project project, String selectDataSource) {
		if (selectDataSource == null || selectDataSource.isEmpty()) {
			dataSource = null;
			return false;
		}
		if (dataSource == null || !dataSource.getName().equalsIgnoreCase(selectDataSource)) {
			DataSourceManager dataSourceManager = DataSourceManager.getInstance(project);
			dataSource = dataSourceManager.getDataSourceByName(selectDataSource);
			if (dataSource == null) {
				logger.error("initDataSource(): No DataSource with name=" + selectDataSource + " was found!");
				return false;
			}
		}
		logger.info("initDataSource(): DataSource with name=" + selectDataSource + " was initialized");
		return true;
	}

	public static DataSource getDataSource() {
		return dataSource;
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

	public static Connection connectTemporary(final Project project, String tempDbEnv) {
		try {
			boolean isInitialized = initTempDataSource(project, tempDbEnv);
			if (isInitialized) {
				return tempDataSource.get().getConnection(project);
			} else {
				logger.error("Temporary Connection for DB=" + tempDbEnv + " failed to establish");
			}
		} catch (Exception e) {
			logger.info("connect(): Error=" + e.getMessage());
		}
		return null;
	}

	public static Connection connect(final Project project) {
		int dbConnectionMaxTimeout = 0;
		try {
			final AtomicBoolean isInitialized = new AtomicBoolean(false);
			final AtomicBoolean isConnectionAllocated = new AtomicBoolean(false);
			while (dbConnectionMaxTimeout < 5) {
				if (!isInitialized.get()) {
					if (dataSource == null) {
						initializeDataSourceSimpleBackgroundTask(project, isInitialized);
					} else {
						isInitialized.set(true);
					}
				} else {
					break;
				}
				TimeUnit.MILLISECONDS.sleep(500);
				dbConnectionMaxTimeout++;
			}
			if (dataSource == null) {
				throw new SQLException("DataSource is null");
			}
			int connectionMaxTimeout = 0;
			final Connection[] connection = new Connection[1];
			while (connectionMaxTimeout < 3) {
				connectWithSimpleBackgroundTask(project, isConnectionAllocated, connection);
				logger.info("connect(): connectionMaxTimeout=" + connectionMaxTimeout);
				if (isConnectionAllocated.get()) {
					return connection[0];
				}
				connectionMaxTimeout++;
				TimeUnit.MILLISECONDS.sleep(500);
			}
		} catch (Exception e) {
			logger.info("connect(): Error=" + e.getMessage());
		}
		logger.error("DbConnectionTimeOut=" + dbConnectionMaxTimeout);
		return null;
	}

	private static void initializeDataSourceSimpleBackgroundTask(final Project project, final AtomicBoolean isInitialized) {
		new SimpleBackgroundTask(dataSource) {
			@Override
			protected void execute() {
				if (initDataSource(project, dataSource.getName())) {
					isInitialized.set(true);
				} else {
					logger.error("Error getting Connection from DataSource=" + dataSource);
				}
			}
		}.start();
	}

	public static void initializeTempDataSourceSimpleBackgroundTask(final Project project, final AtomicBoolean isInitialized) {
		new SimpleBackgroundTask(tempDataSource) {
			@Override
			protected void execute() {
				if (initTempDataSource(project, tempDataSource.get().getName())) {
					isInitialized.set(true);
				} else {
					logger.error("Error getting Connection from TempDataSource=" + tempDataSource.get());
				}
			}
		}.start();
	}

	private static void connectWithSimpleBackgroundTask(final Project project, final AtomicBoolean isConnectionAllocated, final Connection[] connection) {
		new SimpleBackgroundTask(dataSource) {
			@Override
			protected void execute() {
				try {
					logger.info("execute(): name=" + dataSource.getName());
					connection[0] = dataSource.getConnection(project);
					isConnectionAllocated.set(true);
					logger.info("execute(): isConnectionAllocated=" + isConnectionAllocated.get());
				} catch (Exception e) {
					isConnectionAllocated.set(false);
					logger.error("Connection Error=" + e.getMessage(), e);
				}
			}
		}.start();
	}

	public static String getDataSourceCatalog() {
		if (dataSource != null) {
			return dataSource.getName();
		}
		return "DataSource is null";
	}

	public static void commit(Connection connection) {
		try {
			if (connection != null) {
				connection.commit();
			}
		} catch (SQLException e) {
			logger.warn("Error committing connection", e);
		}
	}

	public static void rollback(Connection connection) {
		try {
			if ((connection != null) && (!connection.isClosed()) && (!connection.getAutoCommit())) {
				connection.rollback();
			}
		} catch (SQLException e) {
			logger.warn("Error rolling connection back", e);
		}
	}

	public static void setAutocommit(Connection connection, boolean autoCommit) {
		try {
			if ((connection != null) && (!connection.isClosed())) {
				connection.setAutoCommit(autoCommit);
			}
		} catch (SQLException e) {
			logger.warn("Error setting autocommit to connection", e);
		}
	}
}
