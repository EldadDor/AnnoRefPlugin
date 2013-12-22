package com.idi.intellij.plugin.query.sqlref.action;

import com.idi.intellij.plugin.query.sqlref.util.SQLRefApplication;
import com.idi.intellij.plugin.query.sqlref.util.SQLRefDataAccessor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 29/10/2010
 * Time: 11:31:52
 * To change this template use File | Settings | File Templates.
 */
public class findSqlRefUsageAction extends AnAction {

	private static Logger loggerInstance = Logger.getInstance(findSqlRefUsageAction.class.getName());

	@Override
	public void update(@NotNull AnActionEvent e) {
		e.getPresentation().setEnabled(e.getDataContext().getData(PlatformDataKeys.EDITOR.getName()) != null);
	}

	@Override
	public void actionPerformed(@NotNull AnActionEvent event) {
		Project project = (Project) event.getDataContext().getData(PlatformDataKeys.PROJECT.getName());
		SQLRefApplication.getInstance(project, SQLRefDataAccessor.class).initializeSQLRefData(event.getDataContext());
	}

}
