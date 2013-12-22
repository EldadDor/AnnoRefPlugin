package com.idi.intellij.plugin.query.sqlref.repo.model;

import com.google.common.collect.Sets;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 8/9/13
 * Time: 6:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class SQLRefProjectModulesCollection implements ProjectComponent {
	private static final Logger logger = Logger.getInstance(SQLRefProjectModulesCollection.class.getName());

	private Set<Module> projectModules = Sets.newHashSet();

	@Override
	public void projectOpened() {
		final Project project = ProjectManager.getInstance().getOpenProjects()[ProjectManager.getInstance().getOpenProjects().length - 1];
		Module[] sortedModules = ModuleManager.getInstance(project).getSortedModules();
		Collections.addAll(projectModules, sortedModules);
		logger.info("projectOpened(): projectModules=" + Arrays.toString(projectModules.toArray()));
	}


	public Pair<Set<Module>, Set<Module>> differentiate(Set<Module> currentModules) {
		Sets.SetView<Module> newModules = Sets.difference(currentModules, projectModules);
		Sets.SetView<Module> oldModules = Sets.difference(projectModules, currentModules);
		Set<Module> tempOldModules = Sets.newHashSet();
		Set<Module> tempNewModules = Sets.newHashSet();
		tempOldModules.addAll(oldModules);
		tempNewModules.addAll(newModules);
		projectModules.clear();
		for (Module newModule : newModules) {
			projectModules.add(newModule);
		}
		for (Module module : newModules) {
			if (logger.isDebugEnabled()) {
				logger.debug("differentiate(): module=" + module.getName());
			}
		}
		logger.info("differentiate(): projectModules=" + Arrays.toString(projectModules.toArray()));
		return new Pair<Set<Module>, Set<Module>>(tempOldModules, tempNewModules);
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
