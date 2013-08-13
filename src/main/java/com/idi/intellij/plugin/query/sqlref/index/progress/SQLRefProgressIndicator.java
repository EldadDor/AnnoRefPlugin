package com.idi.intellij.plugin.query.sqlref.index.progress;

import com.intellij.idea.LoggerFactory;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.PerformInBackgroundOption;
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
public class SQLRefProgressIndicator extends BackgroundableProcessIndicator {
	private final static Logger logger = LoggerFactory.getInstance().getLoggerInstance(SQLRefProgressIndicator.class.getName());

	private Project project;

	public SQLRefProgressIndicator(Project project, @Nls String progressTitle,
	                               @NotNull PerformInBackgroundOption option, @Nls String cancelButtonText,
	                               @Nls String backgroundStopTooltip, boolean cancellable) {
		super(project, progressTitle, option, cancelButtonText, backgroundStopTooltip, cancellable);
		this.project = project;

	}



	/*public class SQLRefProgressRunnable implements Runnable {

		Set<Module> modules;

		public SQLRefProgressRunnable(Set<Module> modulesToScan) {
			this.modules=modulesToScan;
		}

		@Override
		public void run() {
			if (!isRunning()) {
				start();
			}
			startScan(modules);
		}
	}*/

}
