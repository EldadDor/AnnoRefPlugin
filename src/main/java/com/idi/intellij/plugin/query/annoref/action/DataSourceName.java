/*
 * User: eldad.Dor
 * Date: 22/02/2015 15:34
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.action;

/**
 * @author eldad
 * @date 22/02/2015
 */
public class DataSourceName {
private String dbName;

	public DataSourceName(String dbName) {
		this.dbName = dbName;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
}