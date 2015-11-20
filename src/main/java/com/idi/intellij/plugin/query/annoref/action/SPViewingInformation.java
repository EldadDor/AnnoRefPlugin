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
public class SPViewingInformation {
	private String dbName;
	private String spName;

	public SPViewingInformation(String dbName) {
		this.dbName = dbName;
	}

	public SPViewingInformation(String dbName, String spName) {
		this.dbName = dbName;
		this.spName = spName;
	}

	public String getSpName() {
		return spName;
	}

	public void setSpName(String spName) {
		this.spName = spName;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
}