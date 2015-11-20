/*
 * User: eldad.Dor
 * Date: 14/12/2014 10:59
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.index.listeners;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.util.ProgressWindow;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author eldad
 * @date 14/12/2014
 */
public class IndexingProgressListener implements IndexProgressChangedListener {
	private static final Logger log = Logger.getInstance(IndexingProgressListener.class.getName());
	private AtomicInteger changesMade = new AtomicInteger(0);
	private final ProgressWindow progressWindow;
	private final double numOfFilesToScan;
	private final double fraction;

	public IndexingProgressListener(Project project, ProgressWindow progressWindow) {
		numOfFilesToScan = FileTypeIndex.getFiles(XmlFileType.INSTANCE, GlobalSearchScope.projectScope(project)).size() +
				FileTypeIndex.getFiles(JavaFileType.INSTANCE, GlobalSearchScope.projectScope(project)).size();
		this.progressWindow = progressWindow;
		fraction = 1 / numOfFilesToScan;
		log.info("IndexingProgressListener(): numOfFilesToScan=" + numOfFilesToScan);
	}


	@Override
	public void changeMade(boolean isChanged) {
		progressWindow.setFraction(fraction + progressWindow.getFraction());
	}

	@Override
	public void failedProcess(String errorMessage) {
		log.error("failedProcess(): error Indexing errorMessage=" + errorMessage);
	}

	@Override
	public void finishedProcess() {
		if (changesMade.incrementAndGet() == 2) {
			log.info("finishedProcess(): Stopping process");
			progressWindow.stop();
		}
	}

	public AtomicInteger getChangesMade() {
		return changesMade;
	}
}