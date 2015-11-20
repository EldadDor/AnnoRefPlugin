package com.idi.intellij.plugin.query.annoref.connection;

import com.idi.intellij.plugin.query.annoref.notification.AnnoRefNotifications;
import com.idi.intellij.plugin.query.annoref.util.AnnoRefBundle;
import com.idi.intellij.plugin.query.annoref.util.TimeUtil;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;

import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConnectionPool implements Disposable {
	private static final Logger LOGGER = Logger.getInstance(ConnectionPool.class.getName());
	private long lastAccessTimestamp;
	private int peakPoolSize;
	private boolean isDisposed;
	private int maxConnectionPoolSize = 5;
	private List<ConnectionWrapper> poolConnections = new CopyOnWriteArrayList();
	private ConnectionWrapper standaloneConnection;
	private ConnectionWrapper tempConnection;
	private static ConnectionPoolCleanTask POOL_CLEANER_TASK = new ConnectionPoolCleanTask();
	private final Project project;

	public ConnectionPool(Project project) {
		this.project = project;
		POOL_CLEANER_TASK.registerConnectionPool(this);
	}

	static {
		Timer poolCleaner = new Timer("AnnoRef Connection pool cleaner");
		poolCleaner.schedule(POOL_CLEANER_TASK, TimeUtil.ONE_MINUTE, TimeUtil.ONE_MINUTE);
	}


	public synchronized Connection getTemporaryConnection(final String dbEnv) {
		LOGGER.info("getTemporaryConnection(): TemporaryConnection=" + tempConnection);
		if (tempConnection == null || !tempConnection.isValid()) {
			LOGGER.info("getTemporaryConnection(): connecting it");
			try {
				Connection connection = ConnectionUtil.connectTemporary(project, dbEnv);
				if (connection != null) {
					tempConnection = new ConnectionWrapper(connection);
					ServiceManager.getService(project, AnnoRefNotifications.class).notifyAnnoRefInfo(project, AnnoRefBundle.message("annoRef.datasource.configured.success", ""), "Temporary connection was made ");
				} else {
					return null;
				}
			} finally {
				notifyStatusChange();
			}
		}
		LOGGER.info("getTemporaryConnection(): getting it");
		return tempConnection.getConnection();
	}

	public synchronized Connection getStandaloneConnection(boolean recover) throws SQLException {
		LOGGER.info("getStandaloneConnection(): " + String.format("StandaloneConnection=" + standaloneConnection + " Recover=%b ", recover));
		if ((standaloneConnection != null) && (recover) && (!standaloneConnection.isValid())) {
			standaloneConnection = null;
		}
		if (standaloneConnection == null || !standaloneConnection.isValid()) {
			try {
				LOGGER.info("getStandaloneConnection(): connecting it");
				Connection connection = ConnectionUtil.connect(project);
				if (connection != null) {
					standaloneConnection = new ConnectionWrapper(connection);
					ServiceManager.getService(project, AnnoRefNotifications.class).notifyAnnoRefInfo(project, AnnoRefBundle.message("annoRef.datasource.configured.success", ""), "Standalone connection was made ");
				} else {
					return null;
				}
			} finally {
				notifyStatusChange();
			}
		}
		LOGGER.info("getStandaloneConnection(): getting it");
		return standaloneConnection.getConnection();
	}

	private void notifyStatusChange() {
	/*	if (!this.isDisposed) {
			ConnectionStatusListener changeListener = (ConnectionStatusListener) EventManager.notify(this.connectionHandler.getProject(), ConnectionStatusListener.TOPIC);
			changeListener.statusChanged(this.connectionHandler.getId());
		}*/
	}


	public synchronized Connection allocateConnection() throws SQLException {
		lastAccessTimestamp = System.currentTimeMillis();
//		ConnectionStatus connectionStatus = this.connectionHandler.getConnectionStatus();
		for (ConnectionWrapper connectionWrapper : poolConnections) {
			if (!connectionWrapper.isBusy()) {
				connectionWrapper.setBusy(true);
				if (connectionWrapper.isValid()) {
					return connectionWrapper.getConnection();
				}
				connectionWrapper.closeConnection();
				poolConnections.remove(connectionWrapper);
			}
		}
		if (poolConnections.size() >= maxConnectionPoolSize) {
			try {
				Thread.currentThread();
				Thread.sleep(TimeUtil.ONE_SECOND);
				return allocateConnection();
			} catch (InterruptedException e) {
				throw new SQLException("Could not allocate connection");
			}
		}
		Connection connection = ConnectionUtil.connect(project);
//		connection.setAutoCommit(true);
		ConnectionWrapper connectionWrapper = new ConnectionWrapper(connection);
		connectionWrapper.setBusy(true);
		poolConnections.add(connectionWrapper);
		int size = poolConnections.size();
		if (size > peakPoolSize) {
			peakPoolSize = size;
		}
		lastAccessTimestamp = System.currentTimeMillis();
//		if (LOGGER.isDebugEnabled()) {
		if (connection == null || connection.getCatalog() == null) {
			LOGGER.error("Could not allocate connection");
			return null;
		}
		LOGGER.info("Pool connection for '" + connection.getCatalog() + "' created. Pool size = " + getSize());
//		}
		return connection;
	}


	public void releaseConnection(Connection connection) {
		LOGGER.info("releaseConnection(): connectionPool current size=" + poolConnections.size());
		if (connection != null) {
			for (ConnectionWrapper connectionWrapper : poolConnections) {
				if (connectionWrapper.getConnection() == connection) {
//					ConnectionUtil.rollback(connection);
//					ConnectionUtil.setAutocommit(connection, true);
					connectionWrapper.setBusy(false);
					break;
				}
			}
		}
		lastAccessTimestamp = System.currentTimeMillis();
	}

	public void closeConnectionsSilently() {
		for (ConnectionWrapper connectionWrapper : poolConnections) {
			connectionWrapper.closeConnection();
		}
		poolConnections.clear();
		if (standaloneConnection != null) {
			standaloneConnection.closeConnection();
			standaloneConnection = null;
		}
	}

	public void closeConnections() throws SQLException {
		SQLException exception = null;
		for (ConnectionWrapper connectionWrapper : poolConnections) {
			try {
				connectionWrapper.getConnection().close();
			} catch (SQLException e) {
				exception = e;
			}
		}
		poolConnections.clear();
		if (standaloneConnection != null) {
			try {
				standaloneConnection.getConnection().close();
			} catch (SQLException e) {
				exception = e;
			}
			standaloneConnection = null;
		}
		if (exception != null) {
			throw exception;
		}
	}

	public int getIdleMinutes() {
		return standaloneConnection == null ? 0 : standaloneConnection.getIdleMinutes();
	}

	public void keepAlive(boolean check) {
		if (standaloneConnection != null) {
			if (check) {
				standaloneConnection.isValid();
			}
			standaloneConnection.keepAlive();
		}
	}

	public int getSize() {
		return poolConnections.size();
	}

	public int getPeakPoolSize() {
		return peakPoolSize;
	}

	@Override
	public void dispose() {
		if (!isDisposed) {
			isDisposed = true;
			closeConnectionsSilently();
		}
	}

	public void setAutoCommit(boolean autoCommit) throws SQLException {
		if ((standaloneConnection != null) && (!standaloneConnection.isClosed())) {
			standaloneConnection.setAutoCommit(autoCommit);
		}
	}

	private class ConnectionWrapper {
		private Connection connection;
		private long lastCheckTimestamp;
		private long lastAccessTimestamp;
		private boolean isValid = true;
		private boolean isBusy;

		private ConnectionWrapper(Connection connection) {
			this.connection = connection;
			long currentTimeMillis = System.currentTimeMillis();
			lastCheckTimestamp = currentTimeMillis;
			lastAccessTimestamp = currentTimeMillis;
		}

		public boolean isValid() {
			long currentTimeMillis = System.currentTimeMillis();
			if (TimeUtil.isOlderThan(lastAccessTimestamp, TimeUtil.TEN_SECONDS)) {
				lastCheckTimestamp = currentTimeMillis;
				try {
					if (connection == null || connection.isClosed()) {
						isValid = false;
					}
				} catch (SQLException e) {
					isValid = false;
				}
			}
			return isValid;
		}

		public int getIdleMinutes() {
			long idleTimeMillis = System.currentTimeMillis() - lastAccessTimestamp;
			return (int) (idleTimeMillis / TimeUtil.FIVE_MINUTES);
		}

		public Connection getConnection() {
			lastAccessTimestamp = System.currentTimeMillis();
			return connection;
		}

		public void closeConnection() {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("closeConnection(): connection=" + connection);
			}
			ConnectionUtil.closeConnection(connection, project);
		}

		public void setAutoCommit(boolean autoCommit) throws SQLException {
			connection.setAutoCommit(autoCommit);
		}

		public boolean isClosed() throws SQLException {
			return connection.isClosed();
		}

		public boolean isBusy() {
			return isBusy;
		}

		public void setBusy(boolean isFree) {
			isBusy = isFree;
		}

		public void keepAlive() {
			lastAccessTimestamp = System.currentTimeMillis();
		}
	}

	private static class ConnectionPoolCleanTask extends TimerTask {
		List<WeakReference<ConnectionPool>> connectionPools = new CopyOnWriteArrayList();

		@Override
		public void run() {
			for (WeakReference connectionPoolRef : connectionPools) {
				ConnectionPool connectionPool = (ConnectionPool) connectionPoolRef.get();
				if ((connectionPool != null) && (TimeUtil.isOlderThan(connectionPool.lastAccessTimestamp, TimeUtil.THREE_MINUTES))) {
					for (ConnectionPool.ConnectionWrapper connection : connectionPool.poolConnections) {
						if (connection.isBusy()) {
							return;
						}
					}
//					if (LOGGER.isDebugEnabled()) {
					if (!connectionPool.poolConnections.isEmpty()) {
						LOGGER.info("run(): cleaning connection pool, poolSize=" + connectionPool.poolConnections.size());
					}
//					}
					for (ConnectionPool.ConnectionWrapper connection : connectionPool.poolConnections) {
						connection.closeConnection();
					}
					connectionPool.poolConnections.clear();
				}
			}
		}

		public void registerConnectionPool(ConnectionPool connectionPool) {
			connectionPools.add(new WeakReference(connectionPool));
		}
	}
}
