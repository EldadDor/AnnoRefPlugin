/*
 * User: eldad.Dor
 * Date: 02/02/14 12:52
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.sqlref.component;

import com.idi.intellij.plugin.query.sqlref.config.SPViewPanelForm;
import com.idi.intellij.plugin.query.sqlref.persist.SQLRefConfigSettings;
import com.idi.intellij.plugin.query.sqlref.persist.SQLRefSettings;
import com.idi.intellij.plugin.query.sqlref.util.SQLRefApplication;
import com.intellij.ide.impl.ContentManagerWatcher;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;

import java.sql.SQLException;

/**
 * @author eldad
 * @date 02/02/14
 */
@State(
		name = "SPViewContentStateManager",
		storages = {@Storage(file = StoragePathMacros.WORKSPACE_FILE)}
)
public class SPViewContentStateManager implements PersistentStateComponent<SPViewContentStateManager.State> {
	private static final Logger logger = Logger.getInstance(SPViewContentStateManager.class.getName());
	private Project myProject;
	private ContentManager contentManager;
	private static final String SP_VIEW_TOOL_WINDOW_ID = "SPViewer";

	public SPViewContentStateManager(final Project project) {
		myProject = project;
		StartupManager.getInstance(myProject).runWhenProjectIsInitialized(new Runnable() {
			@Override
			public void run() {
				SQLRefSPView spView = SQLRefSPView.getInstance(project);
				ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("SPViewer");
				if (toolWindow == null) {
					toolWindow = ToolWindowManager.getInstance(project).registerToolWindow("SPViewer", true, ToolWindowAnchor.BOTTOM);
				}
				toolWindow.setIcon((IconLoader.findIcon("icons/syBaseLogo_36.png")));
				contentManager = toolWindow.getContentManager();

				new ContentManagerWatcher(toolWindow, contentManager);
			}
		});
	}

	public void popOutToolWindowContent(final Project project) {

	}

	public void addContent(Content content, String spName) {
		contentManager.addContent(content);
		contentManager.setSelectedContent(content);
		content.setDisplayName(spName);
		final ToolWindow toolWindow = ToolWindowManager.getInstance(myProject).getToolWindow(SP_VIEW_TOOL_WINDOW_ID);
		toolWindow.activate(null);
	}


	public void reactivateContent(Content content) {
		contentManager.setSelectedContent(content);
		final ToolWindow toolWindow = ToolWindowManager.getInstance(myProject).getToolWindow(SP_VIEW_TOOL_WINDOW_ID);
		toolWindow.show(null);
	}

	public void closeContent(Content content) {
		contentManager.removeContent(content, true);
	}

	public Content getAlreadyOpenContent(String spName) {
		final Content[] contents = contentManager.getContents();
		if (contents.length == 0) {
			return null;
		}
		for (final Content content : contents) {
			if (content.getDisplayName().equals(spName)) {
				return content;
			}
			if (content.getDisplayName().isEmpty()) {
				contentManager.removeContent(content, true);
			}
		}
		return null;
	}

	public Pair<Boolean, Content> fetchSpForContentDisplay(final Project project, final String spName) {
		final SQLRefSettings sqlRefState = SQLRefConfigSettings.getInstance(project).getSqlRefState();
		final Content alreadyOpenContent = getAlreadyOpenContent(spName);
		if (alreadyOpenContent == null) {
			final DataSourceAccessorComponent dbAccessor = SQLRefApplication.getInstance(project, DataSourceAccessorComponent.class);
			dbAccessor.initDataSource(project, sqlRefState.SP_DATA_SOURCE_NAME);
			try {
				logger.info("displayStorageProcedureText(): SP_Name=" + spName);
				String spText = dbAccessor.fetchSpForViewing(spName, project);
				if (spText.isEmpty()) {
					return new Pair<Boolean, Content>(false, null);
				}
				return new Pair<Boolean, Content>(false, getSPViewContent(project, spName, spText));
			} catch (SQLException e) {
				logger.error("displayStorageProcedureText(): Error=" + e.getMessage(), e);
			}
		}
		return new Pair<Boolean, Content>(true, alreadyOpenContent);
	}


	private Content getSPViewContent(Project project, String spName, String spText) {
		final SPViewPanelForm spPanel = new SPViewPanelForm(spName, project);
		final Content newContent = ContentFactory.SERVICE.getInstance().createContent(spPanel.getMainPanel(), "", false);
		newContent.setIcon(IconLoader.findIcon("icons/syBaseLogo_36.png"));
		spPanel.setContent(newContent);
		spPanel.setTextForViewing(spText);
		newContent.setDisposer(spPanel);
		return newContent;
	}

	public static class State {
		public boolean myForwardDirection;
	}


	private State myState;

	@Override
	public State getState() {
		return myState;
	}

	@Override
	public void loadState(final State state) {
		myState = state;
	}
}