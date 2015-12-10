/*
 * User: eldad.Dor
 * Date: 03/01/2015 19:56
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.task;

/**
 * @author eldad
 * @date 03/01/2015
 */
public interface IDITask {
	void run(IDIProgressIndicator progressIndicator) throws IDIProcessCancelledException;

	boolean runComputableTask(IDIProgressIndicator progressIndicator) throws IDIProcessCancelledException;

	void runTask();

	int numOfFiles();

	void setProgressListener(IndicateProgressListener progressListener);

	String getTaskName();
}