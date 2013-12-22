package com.idi.intellij.plugin.query.sqlref.component;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;
import com.idi.intellij.plugin.query.sqlref.index.SQLRefClassProjectRunnable;
import com.idi.intellij.plugin.query.sqlref.index.SQLRefIndexProjectRunnable;
import com.idi.intellij.plugin.query.sqlref.util.SQLRefApplication;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.startup.StartupManager;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 7/6/13
 * Time: 2:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class SQLRefBootStrapComponent implements ProjectComponent {
	private static final Logger logger = Logger.getInstance(SQLRefBootStrapComponent.class.getName());
	private Set<Module> projectModules = Sets.newHashSet();

	@Override
	public void projectOpened() {
		final Project project = ProjectManager.getInstance().getOpenProjects()[ProjectManager.getInstance().getOpenProjects().length - 1];
		SQLRefIndexProjectRunnable xmlIndexRunnable = new SQLRefIndexProjectRunnable(project);
		StartupManager.getInstance(project).registerStartupActivity(xmlIndexRunnable);
		SQLRefClassProjectRunnable classRunnable = new SQLRefClassProjectRunnable(project);
		StartupManager.getInstance(project).registerStartupActivity(classRunnable);
	/*	Thread xmlThread = new Thread(xmlIndexRunnable);
		Thread classThread = new Thread(classRunnable);*/
		final Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();
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
		}*/
		logger.info("STOPWATCH first elapsed=" + stopwatch.elapsedMillis());

		StartupManager.getInstance(project).registerPostStartupActivity(new Runnable() {
			@Override
			public void run() {
				SQLRefApplication.initializeManagersForProject(project);
				stopwatch.stop();
				logger.info("STOPWATCH run elapsed=" + stopwatch.elapsedMillis());
			}
		});

		logger.info("STOPWATCH last elapsed=" + stopwatch.elapsedMillis());
	}

	@Override
	public void projectClosed() {
	}

	@Override
	public void initComponent() {

	}

	@Override
	public void disposeComponent() {
	}

	@NotNull
	@Override
	public String getComponentName() {
		return getClass().getName();
	}
}
