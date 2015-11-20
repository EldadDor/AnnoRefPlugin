package com.idi.intellij.plugin.query.annoref.index.progress;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 8/10/13
 * Time: 10:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class SQLRefProgressIndicator extends BackgroundableProcessIndicator implements ProgressIndicator {
	private static final Logger logger = Logger.getInstance(SQLRefProgressIndicator.class.getName());

	private Project project;

	public SQLRefProgressIndicator(Project project, @Nls String progressTitle,
	                               @NotNull PerformInBackgroundOption option, @Nls String cancelButtonText,
	                               @Nls String backgroundStopTooltip, boolean cancellable) {
		super(project, progressTitle, option, cancelButtonText, backgroundStopTooltip, cancellable);
		this.project = project;
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
		return true;
	}


}
