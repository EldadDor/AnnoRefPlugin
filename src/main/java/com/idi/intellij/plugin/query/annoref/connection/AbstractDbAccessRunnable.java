/*
 * User: eldad.Dor
 * Date: 13/11/2014 12:51
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.connection;

import com.intellij.openapi.project.Project;

/**
 * @author eldad
 * @date 13/11/2014
 */
public abstract class AbstractDbAccessRunnable {

	private String spHelpTextInvokeLater(String spName, final Project project) throws Exception {

		/*	PreparedStatement stmt = null;
			ResultSet resultSet = null;
			try {
				new ConnectionPool(project).allocateConnection()
				final String query = AnnoRefBundle.message("annoRef.sp.helpText");
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
				closeConnection();
				if (resultSet != null) {
					resultSet.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			}
		}
		ServiceManager.getService(project, AnnoRefNotifications.class).notifyAnnoRefError(project, AnnoRefBundle.message("annoRef.datasource.connect.error", dataSource.getName()));*/
		return null;
	}
}