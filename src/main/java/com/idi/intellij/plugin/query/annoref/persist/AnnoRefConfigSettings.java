package com.idi.intellij.plugin.query.annoref.persist;

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
		name = "AnnoRefSettings",
		storages = {
				@Storage(
						id = "other",
						file = StoragePathMacros.APP_CONFIG + "/AnnoRefConfig.xml", scheme = StorageScheme.DIRECTORY_BASED
				)}
)
public class AnnoRefConfigSettings implements PersistentStateComponent<AnnoRefSettings>, ApplicationComponent {
	private static final Logger logger = Logger.getInstance(AnnoRefConfigSettings.class.getName());

	private AnnoRefSettings annoRefState;

	public static AnnoRefConfigSettings getInstance(Project project) {
		AnnoRefConfigSettings service = ServiceManager.getService(project, AnnoRefConfigSettings.class);
		if (service == null) {
			logger.error("AnnoRefConfigSettings is null");
		}
		return service;
	}

	public AnnoRefSettings getAnnoRefState() {
		return annoRefState;
	}

	@Nullable
	@Override
	public AnnoRefSettings getState() {
		if (logger.isDebugEnabled()) {
			logger.debug("getState(): state=" + annoRefState);
		}
		return annoRefState;
	}

	@Override
	public void loadState(AnnoRefSettings state) {
		if (logger.isDebugEnabled()) {
			logger.debug("loadState(): state=" + state);
		}
		annoRefState = state;
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
		return getClass().getName();  //To change body of implemented methods use File | Settings | File Templates.
	}
}
