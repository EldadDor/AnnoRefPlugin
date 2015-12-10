package com.idi.intellij.plugin.query.annoref.component;

import com.google.common.collect.Sets;
import com.idi.intellij.plugin.query.annoref.task.ClassScanningTask;
import com.idi.intellij.plugin.query.annoref.task.IDITaskManager;
import com.idi.intellij.plugin.query.annoref.task.ModuleScanningTask;
import com.idi.intellij.plugin.query.annoref.task.XmlScanningTask;
import com.idi.intellij.plugin.query.annoref.util.AnnRefApplication;
import com.idi.intellij.plugin.query.annoref.util.AnnoRefBundle;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 7/6/13
 * Time: 2:53 PM
 * To change this template use File | Settings | File Templates.
 */
//public class SQLRefBootStrapComponent implements ProjectComponent {
public class SQLRefBootStrapComponent implements StartupActivity {
	private static final Logger logger = Logger.getInstance(SQLRefBootStrapComponent.class.getName());
	private Set<Module> projectModules = Sets.newHashSet();

/*
	//	@Override
	public void projectOpened() {
		final Project project = ProjectManager.getInstance().getOpenProjects()[ProjectManager.getInstance().getOpenProjects().length - 1];
//		SQLRefIndexProjectRunnable xmlIndexRunnable = new SQLRefIndexProjectRunnable(project);
		SQLRefApplication.isLastIndexTimeIfLapsed(project, 0);
		final AccessToken accessToken = ApplicationManager.getApplication().acquireReadActionLock();

		try {
//			StartupManager.getInstance(project).registerPostStartupActivity(wrapModulesTaskWithRunnable(project));
			StartupManager.getInstance(project).registerPostStartupActivity(wrapXmlTaskWithRunnable(project));
			StartupManager.getInstance(project).registerPostStartupActivity(wrapClassTaskWithRunnable(project));
		} finally {
			accessToken.finish();
		}
//		SQLRefClassProjectRunnable classRunnable = new SQLRefClassProjectRunnable(project);
//		StartupManager.getInstance(project).registerPostStartupActivity(wrapClassTaskWithRunnable(project));
	*/
/*	Thread xmlThread = new Thread(xmlIndexRunnable);
		Thread classThread = new Thread(classRunnable);*//*

		final Stopwatch stopwatch = Stopwatch.createStarted();
	*/
/*	try {
			xmlThread.join();
			if (logger.isDebugEnabled()) {
				logger.debug("projectOpened(): xmlRunnable-Finish");
			}
			logger.info("STOPWATCH elapsed=" + stopwatch.elapsedMillis());
			classThread.join();
			if (logger.isDebugEnabled()) {
				logger.debug("projectOpened(): classRunnable-Finish");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*//*

		logger.info("STOPWATCH first elapsed=" + stopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000 + "ms");
		StartupManager.getInstance(project).registerPostStartupActivity(new Runnable() {
			@Override
			public void run() {
				SQLRefApplication.getInstance().initializeManagersForProject(project);
				stopwatch.stop();
				logger.info("STOPWATCH run elapsed=" + stopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000 + "ms");
			}
		});

		logger.info("STOPWATCH last elapsed=" + stopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000 + "ms");
		ServiceManager.getService(project, SPViewContentStateManager.class);
	}
*/

	@Override
	public void runActivity(@NotNull Project project) {
//		StartupManager.getInstance(project).registerPostStartupActivity(wrapXmlTaskWithRunnable(project));

//		StartupManager.getInstance(project).registerPostStartupActivity(wrapClassTaskWithRunnable(project));
		final boolean classScan = wrapClassTaskWithRunnable(project);
		final boolean xmlScan = wrapXmlTaskWithRunnable(project);
		boolean result = classScan ^ xmlScan;
		logger.info("runActivity(): running result=" + result);
		AnnRefApplication.getInstance().initializeManagersForProject(project);
		AnnRefApplication.isLastIndexTimeIfLapsed(project, 0);
		ServiceManager.getService(project, SPViewContentStateManager.class);
	}


	private boolean wrapXmlTaskWithRunnable(final Project project) {
		try {
			IDITaskManager.runCompute(project, AnnoRefBundle.message("annoRef.progress.index"), new XmlScanningTask(project));
//			logger.info("wrapXmlTaskWithRunnable(): xmlResult=" + aBoolean);
//			return aBoolean;
		} catch (Exception e) {
			logger.error(e);
		}
		return false;
	}

	private Runnable wrapModulesTaskWithRunnable(final Project project) {
		return new Runnable() {
			@Override
			public void run() {
				try {
					IDITaskManager.run(project, AnnoRefBundle.message("annoRef.progress.index"), new ModuleScanningTask(project));
				} catch (Exception e) {
					logger.error(e);
				}
			}
		};
	}

	private boolean wrapClassTaskWithRunnable(final Project project) {
		try {
			IDITaskManager.runCompute(project, AnnoRefBundle.message("annoRef.progress.index"), new ClassScanningTask(project));
//			logger.info("wrapClassTaskWithRunnable(): classResult=" + aBoolean);
		} catch (Exception e) {
			logger.error(e);
		}
		return false;
	}
}
