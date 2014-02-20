/*
 * User: eldad.Dor
 * Date: 12/02/14 13:50
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.sqlref.index.progress;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * @author eldad
 * @date 12/02/14
 */
public class DatabaseConnectorProgress extends BackgroundableProcessIndicator implements ProgressIndicator {
	private static final Logger log = Logger.getInstance(DatabaseConnectorProgress.class.getName());

	public DatabaseConnectorProgress(Project project, @Nls String progressTitle,
	                                 @NotNull PerformInBackgroundOption option, @Nls String cancelButtonText,
	                                 @Nls String backgroundStopTooltip, boolean cancellable) {
		super(project, progressTitle, option, cancelButtonText, backgroundStopTooltip, cancellable);
		log.info("DatabaseConnectorProgress():");
	}

	@Override
	public boolean isRunning() {
		if (isCanceled() || getFraction() >= 1) {
			processFinish();
			return false;
		} else {
			return super.isRunning();
		}
	}

	@Override
	protected void onProgressChange() {
		super.onProgressChange();
	}

	@Override
	public void processFinish() {
		super.processFinish();
	}

	@Override
	public void setFraction(double v) {
		if (v >= 1) {
			processFinish();
		}
		super.setFraction(v);
	}


	@Override
	protected boolean isCancelable() {
		return super.isCancelable();
	}
}