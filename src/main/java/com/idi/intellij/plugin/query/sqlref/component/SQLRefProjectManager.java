/*
 * User: eldad
 * Date: 22/03/11 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.sqlref.component;

import com.idi.intellij.plugin.query.sqlref.util.StringUtils;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.containers.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.Map;

/**
 *
 */
public class SQLRefProjectManager implements ApplicationComponent {
	private static final Logger LOGGER = Logger.getInstance(SQLRefProjectManager.class.getName());
	private Map<String, Project> projectCacheHolder = new ConcurrentHashMap<String, Project>();

	public void placeProjectToManagedCache(Project project) {
		projectCacheHolder.put(StringUtils.cleanPath(project.getBasePath()), project);
	}

	public Project getProjectByLocation(String location) {
		return projectCacheHolder.get(location);
	}

	public Map<String, Project> getProjectCacheHolder() {
		return projectCacheHolder;
	}

	@NotNull
	@Override
	public String getComponentName() {
		return MessageFormat.format("{0}.ProjectManager", XmlRepositorySyncComponent.COMPONENT_NAME);
	}

	@Override
	public void initComponent() {
		LOGGER.info("Initialized SQLRefProjectManager");
	}

	@Override
	public void disposeComponent() {
		LOGGER.info(MessageFormat.format("Disposing SQLRefProjectManager, number of projects at disposition: {0}", projectCacheHolder.size()));
	}
}