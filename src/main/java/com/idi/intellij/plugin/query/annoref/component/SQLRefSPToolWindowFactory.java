/*
 * User: eldad.Dor
 * Date: 26/01/14 17:44
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.component;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;

/**
 * @author eldad
 * @date 26/01/14
 */
public class SQLRefSPToolWindowFactory implements ToolWindowFactory, DumbAware {

	@Override
	public void createToolWindowContent(Project project, ToolWindow toolWindow) {
	/*	SQLRefSPView spView = SQLRefSPView.getInstance(project);
		ToolWindow spViewer = ToolWindowManager.getInstance(project).getToolWindow("SPViewer");
		if (spViewer == null) {
			spViewer = ToolWindowManager.getInstance(project).registerToolWindow("SPViewer", true, ToolWindowAnchor.BOTTOM);
		}
		final ContentManager contentManager = toolWindow.getContentManager();
		contentManager.addContent(ContentFactory.SERVICE.getInstance().createContent(spView, "", false));
		toolWindow.setToHideOnEmptyContent(true);
//		toolWindow.setAutoHide(true);
		new ContentManagerWatcher(toolWindow, contentManager);*/
	}
}