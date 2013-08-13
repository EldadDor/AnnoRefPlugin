package com.idi.intellij.plugin.query.sqlref.persist;

import com.intellij.idea.LoggerFactory;
import com.intellij.openapi.components.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 6/22/13
 * Time: 8:28 PM
 * To change this template use File | Settings | File Templates.
 */
@State(
		name = "SQLRefSettings",
		storages = {
				@Storage(
						id = "other",
						file = StoragePathMacros.APP_CONFIG + "/other.xml", scheme = StorageScheme.DIRECTORY_BASED
				)}
)
public class SQLRefConfigSettings implements PersistentStateComponent<SQLRefSettings>, ApplicationComponent {
	private final static Logger logger = LoggerFactory.getInstance().getLoggerInstance(SQLRefConfigSettings.class.getName());

	private SQLRefSettings sqlRefState;


	public SQLRefSettings getSqlRefState() {
		return sqlRefState;
	}

	@Nullable
	@Override
	public SQLRefSettings getState() {
		logger.info("getState(): state=" + sqlRefState);
		return sqlRefState;
	}

	@Override
	public void loadState(SQLRefSettings state) {
		logger.info("loadState(): state=" + state);
		sqlRefState = state;
	}

	public static SQLRefConfigSettings getInstance(Project project) {
		SQLRefConfigSettings service = ServiceManager.getService(project, SQLRefConfigSettings.class);
		if (service == null) {
			logger.error("SQLRefConfigSettings is null");
		}
		return service;
	}

	@Override
	public void initComponent() {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void disposeComponent() {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@NotNull
	@Override
	public String getComponentName() {
		return getClass().getName();  //To change body of implemented methods use File | Settings | File Templates.
	}
}
