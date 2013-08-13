package com.idi.intellij.plugin.query.sqlref.component;

import com.idi.intellij.plugin.query.sqlref.util.StringUtils;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 30/10/2010
 * Time: 12:54:44
 * To change this template use File | Settings | File Templates.
 */
public class XmlRepositorySyncComponent implements ProjectComponent {
	private static final Logger LOGGER = Logger.getInstance(XmlRepositorySyncComponent.class.getName());
	@NotNull
	public static final String COMPONENT_NAME = "sqlRefUsage";

	@NotNull
	@Override
	public String getComponentName() {
		return COMPONENT_NAME + ".RepoSync";
	}

	@Override
	public void initComponent() {
	}


	@Override
	public void disposeComponent() {
	}


	@Override
	public void projectOpened() {
		final Project project = ProjectManager.getInstance().getOpenProjects()[ProjectManager.getInstance().getOpenProjects().length - 1];
		String projectMainParentPath = StringUtils.cleanPath(project.getBasePath());
		LOGGER.info("Project Loading: " + project.getName() + " location parent main path: " + projectMainParentPath);
		String parentPath = PathUtil.getParentPath(projectMainParentPath);
		System.out.println("parentPath = " + parentPath);
		ApplicationManager.getApplication().getComponent(SQLRefProjectManager.class).placeProjectToManagedCache(project);
//		StartupManager.getInstance(project).runWhenProjectIsInitialized(new SQLRefInitializerRunnable(project));
	}

	@Override
	public void projectClosed() {
	}
}
