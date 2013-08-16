package com.idi.intellij.plugin.query.sqlref.config;

import com.idi.intellij.plugin.query.sqlref.persist.SQLRefConfigSettings;
import com.idi.intellij.plugin.query.sqlref.persist.SQLRefSettings;
import com.intellij.idea.LoggerFactory;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.CollectionListModel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 6/8/13
 * Time: 7:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class SQLRefConfigurationConfig extends SearchableConfigurable.Parent.Abstract {
	private final static Logger logger = LoggerFactory.getInstance().getLoggerInstance(SQLRefConfigurationConfig.class.getName());
	public static final String SQL_REF_NAME = "SQLRef";
	private final CollectionListModel<String> myModel = new CollectionListModel<String>();
	private final Project project;

	private JCheckBox enableAnnotationFQNOverrideCheckBox;
	private JTextField annolRefFQN;
	private JPanel SQLRefPanel;
	private JCheckBox autosyncProjectRootCheckBox;
	private JTextField annoRefAttributeId;
	private JTextField annoFQN;
	private JCheckBox enableConversion;
	private JTextArea textArea1;
	private SQLRefSettings settings;
	private SQLRefSettings settingsClone;

	@Nls
	@Override
	public String getDisplayName() {
		return null;
	}

	public SQLRefConfigurationConfig(Project project) {
		logger.info("SQLRefConfigurationConfig(): init");
		this.project = project;
		try {
			if (SQLRefConfigSettings.getInstance(project).getSqlRefState() == null) {
				settings = new SQLRefSettings();
			} else {
				settings = SQLRefConfigSettings.getInstance(project).getSqlRefState();
			}
			settingsClone = settings.clone();
		} catch (Exception e) {
			logger.error("SQLRefConfigurationConfig(): init Error=" + e.getMessage(), e);
		}
	}

	@Nullable
	@Override
	public String getHelpTopic() {
		return getId();
	}

	@Nullable
	@Override
	public JComponent createComponent() {
		enableAnnotationFQNOverrideCheckBox.setSelected(settings.ENABLE_SQLREF_FQN_OVERRIDE);
		annolRefFQN.setText(settings.SQLREF_ANNOTATION_FQN);
		annoFQN.setText(settings.ANNO_ANNOTATION_FQN);
		annoRefAttributeId.setText(settings.SQLREF_ANNOTATION_ATTRIBUTE_ID);
		return SQLRefPanel;
	}

	@Override
	public boolean isModified() {
		boolean isModified = false;
		logger.info("isModified():");
		if (!enableAnnotationFQNOverrideCheckBox.isSelected()) {
			logger.info("isModified(): not-selected");
			annolRefFQN.setEnabled(false);
			annoFQN.setEnabled(false);
			annoRefAttributeId.setEnabled(false);
			return isModified;
		} else {
			logger.info("isModified(): selected");
			annolRefFQN.setEnabled(true);
			annolRefFQN.setEditable(true);
			annoFQN.setEnabled(true);
			annoFQN.setEditable(true);
			annoRefAttributeId.setEnabled(true);
			annoRefAttributeId.setEditable(true);
			if (!annolRefFQN.getText().equalsIgnoreCase(settings.SQLREF_ANNOTATION_FQN)) {
				logger.info("isModified(): selected & TextChanged");
				settings.SQLREF_ANNOTATION_FQN = annolRefFQN.getText().trim();
				settings.SQLREF_ANNOTATION_ATTRIBUTE_ID = annoRefAttributeId.getText().trim();
				settings.ENABLE_SQLREF_FQN_OVERRIDE = enableAnnotationFQNOverrideCheckBox.isSelected();
				isModified = true;
			}
			if (settings.ENABLE_AUTO_SYNC != autosyncProjectRootCheckBox.isSelected()) {
				settings.ENABLE_AUTO_SYNC = autosyncProjectRootCheckBox.isSelected();
				isModified = true;
			}
			return isModified;
		}
	}

	@Override
	public void apply() throws ConfigurationException {
		SQLRefConfigSettings instance = SQLRefConfigSettings.getInstance(project);
		instance.loadState(settings);
	}

	@Override
	public void reset() {
		enableAnnotationFQNOverrideCheckBox.setSelected(settingsClone.ENABLE_SQLREF_FQN_OVERRIDE);
		annolRefFQN.setText(settingsClone.SQLREF_ANNOTATION_FQN);
		annoRefAttributeId.setText(settingsClone.SQLREF_ANNOTATION_ATTRIBUTE_ID);
		autosyncProjectRootCheckBox.setSelected(settingsClone.ENABLE_AUTO_SYNC);
	}

	@Override
	public void disposeUIResources() {
	}

	@NotNull
	@Override
	public String getId() {
		return SQL_REF_NAME;
	}

	@Nullable
	@Override
	public Runnable enableSearch(String option) {
		return null;
	}

	@Override
	public boolean hasOwnContent() {
		return true;
	}

	@Override
	public boolean isVisible() {
		return true;
	}
/*
	@Override
	public Configurable[] getConfigurables() {
		return new SQLRefConfigurationConfig[]{this};
	}*/

	@Override
	protected Configurable[] buildConfigurables() {
		return new SQLRefConfigurationConfig[]{this};
	}

}
