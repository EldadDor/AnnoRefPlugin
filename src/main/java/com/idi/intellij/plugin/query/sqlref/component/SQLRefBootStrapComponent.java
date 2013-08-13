package com.idi.intellij.plugin.query.sqlref.component;

import com.google.common.collect.Sets;
import com.idi.intellij.plugin.query.sqlref.index.SQLRefClassProjectRunnable;
import com.idi.intellij.plugin.query.sqlref.index.SQLRefIndexProjectRunnable;
import com.idi.intellij.plugin.query.sqlref.repo.model.SQLRefProjectModulesCollection;
import com.idi.intellij.plugin.query.sqlref.util.SQLRefApplication;
import com.intellij.idea.LoggerFactory;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.ServiceManager;
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
	private final static Logger logger = LoggerFactory.getInstance().getLoggerInstance(SQLRefBootStrapComponent.class.getName());
	private Set<Module> projectModules = Sets.newHashSet();

	@Override
	public void projectOpened() {
		final Project project = ProjectManager.getInstance().getOpenProjects()[ProjectManager.getInstance().getOpenProjects().length - 1];
		SQLRefIndexProjectRunnable xmlIndexRunnable = new SQLRefIndexProjectRunnable(project);
		StartupManager.getInstance(project).runWhenProjectIsInitialized(xmlIndexRunnable);
		SQLRefClassProjectRunnable classRunnable = new SQLRefClassProjectRunnable(project);
		StartupManager.getInstance(project).runWhenProjectIsInitialized(classRunnable);
		Thread xmlThread = new Thread(xmlIndexRunnable);
		Thread classThread = new Thread(classRunnable);
		try {
			xmlThread.join();
			if (logger.isDebugEnabled()) {
				logger.debug("projectOpened(): xmlRunnable-Finish");
			}
			classThread.join();
			if (logger.isDebugEnabled()) {
				logger.debug("projectOpened(): classRunnable-Finish");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		SQLRefProjectModulesCollection service = ServiceManager.getService(project, SQLRefProjectModulesCollection.class);
		SQLRefApplication.initializeManagersForProject(project);
	}

	@Override
	public void projectClosed() {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void initComponent() {

	}

	@Override
	public void disposeComponent() {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@NotNull
	@Override
	public String getComponentName() {
		return getClass().getName();
	}
}
