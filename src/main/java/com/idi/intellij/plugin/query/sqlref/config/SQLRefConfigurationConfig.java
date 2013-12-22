package com.idi.intellij.plugin.query.sqlref.config;

import com.idi.intellij.plugin.query.sqlref.common.SQLRefConstants;
import com.idi.intellij.plugin.query.sqlref.persist.SQLRefConfigSettings;
import com.idi.intellij.plugin.query.sqlref.persist.SQLRefSettings;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.PopupBorder;
import com.intellij.ui.components.JBLabel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 6/8/13
 * Time: 7:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class SQLRefConfigurationConfig extends SearchableConfigurable.Parent.Abstract {
	public static final String ANNO_REF_NAME = "AnnoRef";
	private static final Logger logger = Logger.getInstance(SQLRefConfigurationConfig.class.getName());
	private final CollectionListModel<String> myModel = new CollectionListModel<String>();
	private final Project project;
	private JCheckBox enableAnnotationFQNOverrideCheckBox;
	private JTextField annoRefFQN;
	private JPanel SQLRefPanel;
	private JCheckBox autosyncProjectRootCheckBox;
	private JTextField annoRefAttributeId;
	private JTextField annoFQN;
	private JCheckBox enableConversion;
	private JTextArea xmlSchemaTextArea;
	private JCheckBox annoRefEnableSuper;
	private JTextField annoRefSuperFQN;
	private SQLRefSettings settings;
	private SQLRefSettings settingsClone;

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

	@Nls
	@Override
	public String getDisplayName() {
		return null;
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
		autosyncProjectRootCheckBox.setSelected(settings.ENABLE_AUTO_SYNC);
		annoRefFQN.setText(settings.ANNOREF_ANNOTATION_FQN);
		annoFQN.setText(settings.ANNO_ANNOTATION_FQN);
		annoRefAttributeId.setText(settings.ANNOREF_ANNOTATION_ATTRIBUTE_ID);
		annoRefEnableSuper.setEnabled(true);
		annoRefEnableSuper.setSelected(settings.ENABLE_ANNO_SUPER);
		annoRefSuperFQNEnableDisable(annoRefEnableSuper.isSelected());
		annoRefSuperFQN.setText(settings.ANNO_REF_SUPER_INTERFACE);
		return SQLRefPanel;
	}

	@Override
	public boolean isModified() {
		boolean isModified = false;

		if (SQLRefConfigSettings.getInstance(project).getSqlRefState().ENABLE_AUTO_SYNC) {
			logger.info("isModified(): ENABLE_AUTO_SYNC=" + SQLRefConfigSettings.getInstance(project).getSqlRefState().ENABLE_AUTO_SYNC);
		}
		if (enableAnnotationFQNOverrideCheckBox.isSelected()) {
			annoRefFQNTextBoxesEnableDisable(true);
			if (!settings.ANNOREF_ANNOTATION_FQN.equals(annoRefFQN.getText().trim()) ||
					!settings.ANNO_ANNOTATION_FQN.equals(annoFQN.getText().trim()) ||
					!settings.ANNOREF_ANNOTATION_ATTRIBUTE_ID.equals(annoRefAttributeId.getText().trim())) {
				settings.ANNOREF_ANNOTATION_FQN = annoRefFQN.getText().trim();
				settings.ANNO_ANNOTATION_FQN = annoFQN.getText().trim();
				settings.ANNOREF_ANNOTATION_ATTRIBUTE_ID = annoRefAttributeId.getText().trim();
				isModified = true;
			}
		} else {
			annoRefFQNTextBoxesEnableDisable(false);
		}
		if (settings.ENABLE_SQLREF_FQN_OVERRIDE != enableAnnotationFQNOverrideCheckBox.isSelected()) {
			isModified = true;
		}
		settings.ENABLE_SQLREF_FQN_OVERRIDE = enableAnnotationFQNOverrideCheckBox.isSelected();
		/*****************************************************************************************************************/
		annoRefSuperFQNEnableDisable(annoRefEnableSuper.isSelected());
		if (settings.ENABLE_ANNO_SUPER != annoRefEnableSuper.isSelected()) {
			isModified = true;
		}
		settings.ENABLE_ANNO_SUPER = annoRefEnableSuper.isSelected();
		/*****************************************************************************************************************/

		if (!annoRefSuperFQN.getText().trim().equals(settings.ANNO_REF_SUPER_INTERFACE)) {
			settings.ANNO_REF_SUPER_INTERFACE = annoRefSuperFQN.getText().trim();
			isModified = true;
		}
		if (autosyncProjectRootCheckBox.isSelected() != settings.ENABLE_AUTO_SYNC) {
			settings.ENABLE_AUTO_SYNC = autosyncProjectRootCheckBox.isSelected();
			isModified = true;
		}
		return isModified;
	}

	private void annoRefSuperFQNEnableDisable(boolean value) {
		annoRefSuperFQN.setEnabled(value);
		annoRefSuperFQN.setEditable(value);
	}

	private void annoRefFQNTextBoxesEnableDisable(boolean value) {
		annoRefFQN.setEnabled(value);
		annoRefFQN.setEditable(value);
		annoFQN.setEnabled(value);
		annoFQN.setEditable(value);
		annoRefAttributeId.setEnabled(value);
		annoRefAttributeId.setEditable(value);
	}


	@NotNull
	private JComponent constructAMessagePopUp(String message, Icon icon) {
		final PopupBorder popupBorder = PopupBorder.Factory.create(true, true);

		JPanel panel = new JPanel(new BorderLayout(500, 900));
		panel.setBorder(popupBorder);
		final JLabel messageLabel = new JBLabel(icon);
		messageLabel.setText(message);
		panel.add(messageLabel);
		panel.setBackground(SQLRefConstants.MessagePopupBackgroundColor);
		return panel;
	}

	@Override
	public void apply() throws ConfigurationException {
		SQLRefConfigSettings instance = SQLRefConfigSettings.getInstance(project);
		instance.loadState(settings);
	}

	@Override
	public void reset() {
		enableAnnotationFQNOverrideCheckBox.setSelected(settingsClone.ENABLE_SQLREF_FQN_OVERRIDE);
		annoRefFQN.setText(settingsClone.ANNOREF_ANNOTATION_FQN);
		annoRefAttributeId.setText(settingsClone.ANNOREF_ANNOTATION_ATTRIBUTE_ID);
		annoFQN.setText(settingsClone.ANNO_ANNOTATION_FQN);
		autosyncProjectRootCheckBox.setSelected(settingsClone.ENABLE_AUTO_SYNC);
		annoRefSuperFQN.setText(settingsClone.ANNO_REF_SUPER_INTERFACE);
		annoRefEnableSuper.setSelected(settingsClone.ENABLE_ANNO_SUPER);
	}

	@Override
	public void disposeUIResources() {
	}

	@NotNull
	@Override
	public String getId() {
		return ANNO_REF_NAME;
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
