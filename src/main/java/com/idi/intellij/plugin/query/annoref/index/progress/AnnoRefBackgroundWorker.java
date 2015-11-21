/*
 * User: eldad.Dor
 * Date: 14/07/2014 11:59
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.index.progress;

import com.idi.intellij.plugin.query.annoref.task.ClassScanningTask;
import com.idi.intellij.plugin.query.annoref.task.IDITaskManager;
import com.idi.intellij.plugin.query.annoref.task.XmlScanningTask;
import com.idi.intellij.plugin.query.annoref.util.AnnoRefBundle;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author eldad
 * @date 14/07/2014
 */
public class AnnoRefBackgroundWorker {
	private static final Logger log = Logger.getInstance(AnnoRefBackgroundWorker.class.getName());

	public void runInBackground(@NotNull final Project project) {
		runInBackgroundDispatcherThread(project);
	}


	public void runInBackgroundDispatcherThread(Project project) {
		try {
//			IDITaskManager.runInBackground(project, "xml scanning", false, new XmlScanningTask(project));
//			IDITaskManager.runInBackground(project, "class scanning", false, new ClassScanningTask(project));
			IDITaskManager.runCompute(project, AnnoRefBundle.message("annoRef.progress.reindex"), new XmlScanningTask(project));
			IDITaskManager.runCompute(project, AnnoRefBundle.message("annoRef.progress.reindex"), new ClassScanningTask(project));
		} catch (Exception e) {
			log.error(e);
		}

	/*	final DispatchThreadProgressWindow progressWindow = new DispatchThreadProgressWindow(true, project);
//		final ProgressWindowWithNotification progressWindow = new ProgressWindowWithNotification(true,true, project);
//		progressWindow.setBackgroundHandler(runnable);
		final IndexProgressChangedListener progressListener = new IndexingProgressListener(project, progressWindow);
		final Runnable runnable = new AnnoRefIndexingBackgroundRunnable(project, progressListener, null);
		progressWindow.setRunnable(runnable);
		progressWindow.start();*/
	}
/*
	@Deprecated
	public void runInBackgroundWithIndicator(final ExecutorService executorService, final IndexProgressChangedListener progressListener, final Project project,
	                                         ProgressIndicator[] sqlRefProgressIndicator) {
		final Runnable runnable = new AnnoRefIndexingBackgroundRunnable(project, progressListener, executorService);
		final AnnoRefIndexingBackgroundTask backgroundTask = new AnnoRefIndexingBackgroundTask(project, AnnoRefBundle.message("annoRef.progress.reindex"), true, sqlRefProgressIndicator[0], executorService, progressListener);
		final Task.Backgroundable backGroundAble = new Task.Backgroundable(project, AnnoRefBundle.message("annoRef.progress.reindex"),
				true, PerformInBackgroundOption.ALWAYS_BACKGROUND) {
			@Override
			public void run(@NotNull ProgressIndicator progressIndicator) {
				log.info("run(): progressIndicator=" + progressIndicator);
				progressIndicator.start();
				log.info("run(): progressIndicator=" + progressIndicator);
				executorService.submit(runnable);
			}
		};
		if (ApplicationManager.getApplication().isDispatchThread()) {
			UIUtil.invokeLaterIfNeeded(runnable);
		} else {
			backGroundAble.run(sqlRefProgressIndicator[0]);
		}
	}*/
}