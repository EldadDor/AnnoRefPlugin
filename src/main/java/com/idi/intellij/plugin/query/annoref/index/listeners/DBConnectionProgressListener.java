/*
 * User: eldad.Dor
 * Date: 14/12/2014 19:05
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.index.listeners;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.util.ProgressWindow;

/**
 * @author eldad
 * @date 14/12/2014
 */
public class DBConnectionProgressListener implements IndexProgressChangedListener {
	private static final Logger log = Logger.getInstance(DBConnectionProgressListener.class.getName());
	private final ProgressWindow progressWindow;

	public DBConnectionProgressListener(ProgressWindow progressWindow) {
		this.progressWindow = progressWindow;
	}

	@Override
	public void changeMade(boolean isChanged) {
		log.info("changeMade():");
		progressWindow.setFraction(0.01 + progressWindow.getFraction());
	}

	@Override
	public void failedProcess(String errorMessage) {
		log.error("failedProcess():");
	}

	@Override
	public void finishedProcess() {
		log.info("finishedProcess():");
		if (progressWindow.getFraction() < 1) {
			progressWindow.setFraction(1);
		}
		if (progressWindow.isRunning()) {
			log.info("finishedProcess(): stopping");
			progressWindow.stop();
		}
	}
}