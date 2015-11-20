/*
 * User: eldad.Dor
 * Date: 14/12/2014 10:51
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.index.progress;

import com.idi.intellij.plugin.query.annoref.index.SQLRefClassFileIndex;
import com.idi.intellij.plugin.query.annoref.index.SQLRefRepository;
import com.idi.intellij.plugin.query.annoref.index.SQLRefXmlFileIndex;
import com.idi.intellij.plugin.query.annoref.index.listeners.IndexProgressChangedListener;
import com.idi.intellij.plugin.query.annoref.thread.BackgroundTask;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutorService;

/**
 * @author eldad
 * @date 14/12/2014
 */
public class AnnoRefIndexingBackgroundTask extends BackgroundTask {

	private final ExecutorService executor;
	private final IndexProgressChangedListener indexProgressChangedListener;

	protected AnnoRefIndexingBackgroundTask(@Nullable Project project, @NotNull String title, boolean canBeCancelled, @Nullable PerformInBackgroundOption backgroundOption, ProgressIndicator progressIndicator, ExecutorService executor, IndexProgressChangedListener indexProgressChangedListener) {
		super(project, title, canBeCancelled, backgroundOption, progressIndicator);
		this.executor = executor;
		this.indexProgressChangedListener = indexProgressChangedListener;
		initProgressIndicator(progressIndicator, true, title);
	}

	public AnnoRefIndexingBackgroundTask(@Nullable Project project, @NotNull String title, boolean canBeCancelled, ProgressIndicator progressIndicator, ExecutorService executor, IndexProgressChangedListener indexProgressChangedListener) {
		super(project, title, canBeCancelled, progressIndicator);
		this.executor = executor;
		this.indexProgressChangedListener = indexProgressChangedListener;
	}


	@Override
	protected void execute(@NotNull ProgressIndicator progressIndicator) throws InterruptedException {
		//		progressIndicator.start();
		ServiceManager.getService(getProject(), SQLRefRepository.class).resetAllProjectOnModulesChange();
		SQLRefXmlFileIndex refXmlFileIndex = new SQLRefXmlFileIndex(getProject(), indexProgressChangedListener);
		SQLRefClassFileIndex sqlRefClassFileIndex = new SQLRefClassFileIndex(getProject(), indexProgressChangedListener);
		refXmlFileIndex.indexSQLRef();
		sqlRefClassFileIndex.indexSQLRef();
	/*	if (progressIndicator.isRunning()) {
			progressIndicator.stop();
		}*/
	}
}