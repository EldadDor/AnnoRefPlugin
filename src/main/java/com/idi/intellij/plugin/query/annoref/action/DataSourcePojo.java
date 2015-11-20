/*
 * User: eldad.Dor
 * Date: 23/02/2015 18:27
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.action;

/**
 * @author eldad
 * @date 23/02/2015
 */
public class DataSourcePojo {
	private String dbName;
	private String description;

	public DataSourcePojo(String dbName, String description) {
		this.dbName = dbName;
		this.description = description;
	}

	public DataSourcePojo(String dbName) {
		this.dbName = dbName;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}