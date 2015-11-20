/*
 * User: eldad.Dor
 * Date: 09/06/2015 11:47
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.index.progress;

import com.idi.intellij.plugin.query.annoref.task.IDIProcessCancelledException;
import com.idi.intellij.plugin.query.annoref.task.IDIProgressIndicator;
import com.idi.intellij.plugin.query.annoref.task.IDITask;
import com.idi.intellij.plugin.query.annoref.task.IDITaskManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author eldad
 * @date 09/06/2015
 */
public class AnnoRefSimpleBackgroundTask extends Task.Backgroundable {
	private static final Logger log = Logger.getInstance(IDITaskManager.class);
	private final DumbService dumbService;
	private IDITask task;

	public AnnoRefSimpleBackgroundTask(Project project, String title, boolean canBeCancelled, PerformInBackgroundOption backgroundOption) {
		super(project, title, canBeCancelled, backgroundOption);
		dumbService = DumbService.getInstance(getProject());

	}

	public AnnoRefSimpleBackgroundTask(Project project, String title, boolean canBeCancelled, PerformInBackgroundOption backgroundOption, IDITask task) {
		super(project, title, canBeCancelled, backgroundOption);
		this.task = task;
		dumbService = DumbService.getInstance(getProject());
	}

	@Override
	public void onSuccess() {
		super.onSuccess();
		log.info("onSuccess():");
	}

	@Override
	public void run(@NotNull ProgressIndicator progressIndicator) {
		try {
			task.run(new IDIProgressIndicator(progressIndicator, task.numOfFiles()));
		} catch (IDIProcessCancelledException e) {
			log.error(e);
		}
	/*	new ClassScanningTask(
		ProgressManager.getInstance().runProcess(new Runnable() {
			@Override
			public void run() {
				dumbService.runReadActionInSmartMode(new Runnable() {
					@Override
					public void run() {

					}
				});
			}
		}, progressIndicator)
	}*/
	}
}