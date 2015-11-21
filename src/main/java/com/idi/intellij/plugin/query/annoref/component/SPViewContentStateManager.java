/*
 * User: eldad.Dor
 * Date: 02/02/14 12:52
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.component;

import com.idi.intellij.plugin.query.annoref.config.SPViewPanelForm;
import com.idi.intellij.plugin.query.annoref.connection.ConnectionUtil;
import com.idi.intellij.plugin.query.annoref.connection.DataSourceAccessorComponent;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefConfigSettings;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefSettings;
import com.idi.intellij.plugin.query.annoref.util.SQLRefApplication;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.diff.DiffContent;
import com.intellij.openapi.diff.DiffManager;
import com.intellij.openapi.diff.DocumentContent;
import com.intellij.openapi.diff.impl.mergeTool.MergeTool;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.impl.ToolWindowImpl;
import com.intellij.openapi.wm.impl.content.ToolWindowContentUi;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

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
	private static final String SP_VIEW_TOOL_WINDOW_ID = "SPViewer";
	private Project myProject;
	private ContentManager contentManager;
	private State myState;

	public SPViewContentStateManager(final Project project) {
		myProject = project;
		StartupManager.getInstance(myProject).runWhenProjectIsInitialized(new Runnable() {
			@Override
			public void run() {
				ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(SP_VIEW_TOOL_WINDOW_ID);
				if (toolWindow == null) {
					toolWindow = ToolWindowManager.getInstance(project).registerToolWindow(SP_VIEW_TOOL_WINDOW_ID, true, ToolWindowAnchor.BOTTOM);
				}
				toolWindow.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/syBaseLogo_3_sm.png")));
				contentManager = toolWindow.getContentManager();
//				final ToolWindowContentUi windowContentUi = new ToolWindowContentUi((ToolWindowImpl) toolWindow);
//				windowContentUi.
				toolWindow.setTitle("");
				toolWindow.installWatcher(contentManager);
//				new ContentManagerWatcher(toolWindow, contentManager);
			}
		});
	}

	public void popOutToolWindowContent(final Project project) {

	}

	public void addContent(Content content, String spName, String contentName) {
		contentManager.addContent(content);
		contentManager.setSelectedContent(content);
		content.setDisplayName(spName);
		content.setTabName(contentName);
		final JComponent component = content.getComponent();
		((JLabel) ((Container) component.getComponent(0)).getComponent(3)).setText(AnnoRefConfigSettings.getInstance(myProject).getAnnoRefState().SP_DATA_SOURCE_NAME);
		final ToolWindow toolWindow = ToolWindowManager.getInstance(myProject).getToolWindow(SP_VIEW_TOOL_WINDOW_ID);
//		toolWindow.setTitle(spName);
		toolWindow.activate(null);
	}

	public void reactivateContent(Content content) {
		contentManager.setSelectedContent(content);
		final ToolWindow toolWindow = ToolWindowManager.getInstance(myProject).getToolWindow(SP_VIEW_TOOL_WINDOW_ID);
//		toolWindow.show(null);
		toolWindow.activate(null, false);
	}

	public void closeContent(Content content) {
		contentManager.removeContent(content, true);
		if (contentManager.getContents().length == 0) {
			SQLRefApplication.getInstance(myProject, DataSourceAccessorComponent.class).getConnectionPool().closeConnectionsSilently();
		}
	}

	public Content getAlreadyOpenContent(String contentName) {
		final Content[] contents = contentManager.getContents();
		if (contents.length == 0) {

			return null;
		}
		if (contentName.isEmpty()) {
			return Arrays.asList(contents).get(contents.length - 1);
		}
		for (final Content content : contents) {
			if (content.getTabName().equals(contentName)) {
				return content;
			}
			if (content.getDisplayName().isEmpty()) {
				contentManager.removeContent(content, true);
			}
		}
		return null;
	}

	public void diffStuff(Document document) {
		final DocumentContent documentContent = DiffContent.fromDocument(myProject, document);
		DiffManager.getInstance().createDiffPanel(null, null, new MergeTool());
	}

	public String fetchSpTextForDBEnvironment(final Project project, final String spName, final String dbEnv) throws Exception {
		final AnnoRefSettings sqlRefState = AnnoRefConfigSettings.getInstance(project).getAnnoRefState();
		Connection connectTemporary = null;
		try {
			ConnectionUtil.initializeTempDataSourceSimpleBackgroundTask(project, new AtomicBoolean(false));
			if (ConnectionUtil.initTempDataSource(project, dbEnv)) {
				final DataSourceAccessorComponent dbAccessor = SQLRefApplication.getInstance(project, DataSourceAccessorComponent.class);
				connectTemporary = ConnectionUtil.connectTemporary(project, dbEnv);
				return dbAccessor.fetchSpForViewing(spName, project, true, connectTemporary);
			}
		} finally {
			if (connectTemporary != null && !connectTemporary.isClosed()) {
				logger.info("fetchSpTextForDBEnvironment(): closing temp connection");
				connectTemporary.close();
			}
		}
		return null;
	}

	public Pair<Boolean, Content> fetchSpForContentDisplay(final Project project, final String spName, final String contentName, boolean dispatchThread) {
		final AnnoRefSettings sqlRefState = AnnoRefConfigSettings.getInstance(project).getAnnoRefState();
		final Content alreadyOpenContent = getAlreadyOpenContent(contentName);
		if (alreadyOpenContent == null) {
			final DataSourceAccessorComponent dbAccessor = SQLRefApplication.getInstance(project, DataSourceAccessorComponent.class);
//			dbAccessor.initDataSource(project, sqlRefState.SP_DATA_SOURCE_NAME, false);
			try {
				logger.info("displayStorageProcedureText(): SP_Name=" + spName);
				if (ConnectionUtil.initDataSource(project, sqlRefState.SP_DATA_SOURCE_NAME)) {
					String spText = dbAccessor.fetchSpForViewing(spName, project, dispatchThread, null);
					if (!spText.isEmpty()) {
						return new Pair<Boolean, Content>(false, getSPViewContent(project, spName, spText, contentName));
					}
					return new Pair<Boolean, Content>(false, null);
				}
			} catch (SQLException e) {
				logger.error("displayStorageProcedureText(): Error=" + e.getMessage(), e);
			}
		}
		return new Pair<Boolean, Content>(true, alreadyOpenContent);
	}

	private Content getSPViewContent(Project project, String spName, String spText, String contentName) {
		final SPViewPanelForm spPanel = new SPViewPanelForm(spName, project);
//		final String spDataSourceName = AnnoRefConfigSettings.getInstance(project).getAnnoRefState().SP_DATA_SOURCE_NAME;
		final Content newContent = ContentFactory.SERVICE.getInstance().createContent(spPanel.getMainPanel(), contentName, false);
		newContent.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/syBaseLogo_3_sm.png")));
		spPanel.setContent(newContent);
		spPanel.setTextForViewing(spText);
		newContent.setDisposer(spPanel);
		return newContent;
	}

	@Override
	public State getState() {
		return myState;
	}

	@Override
	public void loadState(final State state) {
		myState = state;
	}

	public static class State {
		public boolean myForwardDirection;
	}
}