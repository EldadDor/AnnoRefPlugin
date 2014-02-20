package com.idi.intellij.plugin.query.sqlref.config;

import com.idi.intellij.plugin.query.sqlref.common.SQLRefConstants;
import com.idi.intellij.plugin.query.sqlref.component.DataSourceAccessorComponent;
import com.idi.intellij.plugin.query.sqlref.persist.SQLRefConfigSettings;
import com.idi.intellij.plugin.query.sqlref.persist.SQLRefSettings;
import com.idi.intellij.plugin.query.sqlref.util.SQLRefApplication;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.AsyncResult;
import com.intellij.openapi.vcs.VcsShowConfirmationOption;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.JBColor;
import com.intellij.ui.PopupBorder;
import com.intellij.ui.components.JBLabel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.intellij.util.JavaeeIcons;
import com.intellij.util.ui.ConfirmationDialog;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

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
	boolean isModified;
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
	private JTextField spViewText;
	private JLabel spViewFQNLabel;
	private JLabel spViewDefaultLabel;
	private JComboBox spDataSourceComboBox;
	private JButton testConnectionBtn;
	private SQLRefSettings settings;
	private SQLRefSettings settingsClone;
	private String selectDataSource;

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
		return ANNO_REF_NAME;
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
		spViewText.setText(settings.SP_VIEW_ANNOTATION_FQN);
		spDataSourceComboBox.setModel(getDataSourcesModel());
		addDataSourceComboBoxListener();
		addTestConnectionListener();
		selectedConfiguredDataSource();
		return SQLRefPanel;
	}


	/*private ListModel spDataSourceModel() {
		final ArrayListModel<String> listModel = new ArrayListModel<String>();
		listModel.add(0, settings.SP_DATA_SOURCE_NAME);
		return listModel;
	}*/

	/*private ComboBoxModel getDataSourcesModel() {
		String[] dataSources = new String[0];
		if (settings.SP_DATA_SOURCE_NAME.contains(",")) {
			dataSources = settings.SP_DATA_SOURCE_NAME.split(",");
		} else {
			dataSources = new String[]{settings.SP_DATA_SOURCE_NAME};
		}
		final ArrayList<String> dataSourceList = new ArrayList<String>();
		dataSourceList.add("");
		Collections.addAll(dataSourceList, dataSources);
		return new CollectionComboBoxModel(dataSourceList);
	}*/

	private void selectedConfiguredDataSource() {
		UIUtil.invokeAndWaitIfNeeded(new Runnable() {
			@Override
			public void run() {
				logger.info("run(): settings.SP_DATA_SOURCE_NAME=" + settings.SP_DATA_SOURCE_NAME);
				spDataSourceComboBox.getModel().setSelectedItem(settings.SP_DATA_SOURCE_NAME);
				logger.info("run(): spDataSourceComboBox.getModel()=" + spDataSourceComboBox.getModel().getSelectedItem());

			}
		});
	}

	private ComboBoxModel getDataSourcesModel() {
		final Collection<String> availableConnections = SQLRefApplication.getInstance(project, DataSourceAccessorComponent.class).getAvailableConnections(project);
		final ArrayList<String> dataSourceList = new ArrayList<String>();
		dataSourceList.add("");
		for (final String availableConnection : availableConnections) {
			dataSourceList.add(availableConnection);
		}
		return new CollectionComboBoxModel(dataSourceList);
	}


	private void addDataSourceComboBoxListener() {
		spDataSourceComboBox.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox) e.getSource();
				selectDataSource = (String) cb.getSelectedItem();
				logger.info("actionPerformed(): selectDataSource=" + selectDataSource);

			}
		});
	}

	private void addTestConnectionListener() {
		testConnectionBtn.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final DataSourceAccessorComponent dbAccessor = SQLRefApplication.getInstance(project, DataSourceAccessorComponent.class);
				if (selectDataSource == null) {
					selectDataSource = settings.SP_DATA_SOURCE_NAME;
				}
				dbAccessor.initDataSource(project, selectDataSource);
				final String dataSource = String.valueOf(spDataSourceComboBox.getSelectedItem());
				if (dataSource != null && !dataSource.isEmpty()) {
					final String configuredDataSourceName = dbAccessor.getConfiguredDataSourceName();
					if (configuredDataSourceName != null && dbAccessor.testConnection(project)) {
						showTestConnectionMessageDialog("Connection to " + dataSource + " Successful");
					} else {
						showTestConnectionMessageDialog("Connection to " + dataSource + " Failed");
					}
				} else {
					showTestConnectionMessageDialog("No Connection Selected");
				}
			}
		});
	}


	private void showTestConnectionMessageDialog(String message) {
		final ConfirmationDialog confirmationDialog = new ConfirmationDialog(project, message, "DataSource Connection Test",
				AllIcons.RunConfigurations.TestPassed, VcsShowConfirmationOption.STATIC_SHOW_CONFIRMATION);
		UIUtil.invokeAndWaitIfNeeded(new Runnable() {
			@Override
			public void run() {
				final AsyncResult<Boolean> result = confirmationDialog.showAndGetOk();
				final Boolean booleanResult = result.getResult();
			}
		});
	}


	@Override
	public boolean isModified() {
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
		/*if (settings.SP_DATA_SOURCE_NAME != spDataSourceList.getModel().getElementAt(0)) {
//			settings.SP_DATA_SOURCE_NAME = spViewDataSourceText.getText().trim();
			final boolean dataSource = ServiceManager.getService(project, DataSourceAccessorComponent.class).initDataSource(project, settings.SP_DATA_SOURCE_NAME);
			if (dataSource) {
				constructAMessagePopUp("DataSource " + dataSource + " configured successfully", null);
			} else {
				constructAMessagePopUp("DataSource " + dataSource + " wasn't found in DataSources configuration", null);
			}
			isModified = true;
		}*/
		if (!spViewText.getText().trim().equals(settings.SP_VIEW_ANNOTATION_FQN)) {
			settings.SP_VIEW_ANNOTATION_FQN = spViewText.getText().trim();
			isModified = true;
		}
		/*****************************************************************************************************************/
		if (!settings.SP_DATA_SOURCE_NAME.equals(String.valueOf(spDataSourceComboBox.getModel().getSelectedItem()))) {
			settings.SP_DATA_SOURCE_NAME = String.valueOf(spDataSourceComboBox.getModel().getSelectedItem());
			isModified = true;
		}

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
		if (isModified) {
			if (selectDataSource != null) {
				final DataSourceAccessorComponent dbAccessor = SQLRefApplication.getInstance(project, DataSourceAccessorComponent.class);
				final boolean configured = dbAccessor.initDataSource(project, selectDataSource);
				spDataSourceComboBox.getModel().setSelectedItem(selectDataSource);
				if (configured) {
					WindowManager.getInstance().getStatusBar(project).fireNotificationPopup(constructAMessagePopUp("Selected DataSource " + selectDataSource + " Configured!", JavaeeIcons.DATASOURCE_REMOTE_INSTANCE),
							JBColor.GREEN);
				} else {
					WindowManager.getInstance().getStatusBar(project).fireNotificationPopup(constructAMessagePopUp("DataSource Not Configured!", JavaeeIcons.DATASOURCE_REMOTE_INSTANCE),
							JBColor.GREEN);
				}
			}
			SQLRefConfigSettings instance = SQLRefConfigSettings.getInstance(project);
			instance.loadState(settings);
		}
		isModified = false;
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
//		spDataSourceComboBox.setModel(getDataSourcesModel());
		spViewText.setText(settingsClone.SP_VIEW_ANNOTATION_FQN);
//		spDataSourceComboBox.getModel().setSelectedItem(settingsClone.SP_DATA_SOURCE_NAME);
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

	{
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
		$$$setupUI$$$();
	}

	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$() {
		SQLRefPanel = new JPanel();
		SQLRefPanel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
		final JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayoutManager(3, 3, new Insets(2, 5, 2, 10), -1, -1));
		SQLRefPanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		enableAnnotationFQNOverrideCheckBox = new JCheckBox();
		enableAnnotationFQNOverrideCheckBox.setActionCommand("Enable Annotations' FQN Override");
		enableAnnotationFQNOverrideCheckBox.setText("Enable Annotations' FQN Overide");
		enableAnnotationFQNOverrideCheckBox.setToolTipText("When disabled, the FQN for the SQLRef FQN will be the default com.idi");
		panel1.add(enableAnnotationFQNOverrideCheckBox, new GridConstraints(2, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 2, false));
		autosyncProjectRootCheckBox = new JCheckBox();
		autosyncProjectRootCheckBox.setLabel("Autosync Project Root");
		autosyncProjectRootCheckBox.setText("Autosync Project Root");
		autosyncProjectRootCheckBox.setToolTipText("autosync the project when root changes, like when adding or removing module via maven pom structure");
		panel1.add(autosyncProjectRootCheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 2, false));
		enableConversion = new JCheckBox();
		enableConversion.setEnabled(false);
		enableConversion.setText("Enable Anno To AnnoRef Conversion");
		panel1.add(enableConversion, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 2, false));
		final Spacer spacer1 = new Spacer();
		SQLRefPanel.add(spacer1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayoutManager(13, 5, new Insets(2, 2, 2, 2), -1, -1));
		SQLRefPanel.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		annoRefFQN = new JTextField();
		annoRefFQN.setBackground(new Color(-1));
		annoRefFQN.setEditable(false);
		annoRefFQN.setEnabled(false);
		annoRefFQN.setMargin(new Insets(2, 2, 2, 2));
		annoRefFQN.setToolTipText("The FQN for the annotation to use");
		panel2.add(annoRefFQN, new GridConstraints(1, 0, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		final JLabel label1 = new JLabel();
		label1.setText("AnnoRef annotation's fully qualifed name: ");
		panel2.add(label1, new GridConstraints(0, 0, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		annoRefAttributeId = new JTextField();
		annoRefAttributeId.setToolTipText("The annotation attribute name to use for reference in the corresponding xml file id");
		panel2.add(annoRefAttributeId, new GridConstraints(5, 0, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		final JLabel label2 = new JLabel();
		label2.setText("Annotation Attribute Id name:");
		label2.setToolTipText("");
		panel2.add(label2, new GridConstraints(4, 0, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		annoFQN = new JTextField();
		annoFQN.setBackground(new Color(-1));
		annoFQN.setEditable(false);
		annoFQN.setEnabled(false);
		panel2.add(annoFQN, new GridConstraints(3, 0, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		final JLabel label3 = new JLabel();
		label3.setText("Initial annotation's fully qualifed name:");
		panel2.add(label3, new GridConstraints(2, 0, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label4 = new JLabel();
		label4.setText("Xml structure baselines: ");
		label4.setToolTipText(ResourceBundle.getBundle("annoconfig").getString("xml.structure.example"));
		panel2.add(label4, new GridConstraints(11, 0, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		xmlSchemaTextArea = new JTextArea();
		xmlSchemaTextArea.setBackground(new Color(-6500));
		panel2.add(xmlSchemaTextArea, new GridConstraints(12, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
		annoRefEnableSuper = new JCheckBox();
		annoRefEnableSuper.setSelected(true);
		annoRefEnableSuper.setText("Enable Super interface/class ");
		panel2.add(annoRefEnableSuper, new GridConstraints(6, 0, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 2, false));
		annoRefSuperFQN = new JTextField();
		panel2.add(annoRefSuperFQN, new GridConstraints(7, 0, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		spViewText = new JTextField();
		panel2.add(spViewText, new GridConstraints(9, 0, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		spViewFQNLabel = new JLabel();
		spViewFQNLabel.setText("SP Viewer fully qualified name:");
		panel2.add(spViewFQNLabel, new GridConstraints(8, 0, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		spViewDefaultLabel = new JLabel();
		spViewDefaultLabel.setText("SP DataSource value:");
		panel2.add(spViewDefaultLabel, new GridConstraints(10, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		spDataSourceComboBox = new JComboBox();
		final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
		spDataSourceComboBox.setModel(defaultComboBoxModel1);
		panel2.add(spDataSourceComboBox, new GridConstraints(10, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		testConnectionBtn = new JButton();
		testConnectionBtn.setText("Test Connection");
		panel2.add(testConnectionBtn, new GridConstraints(10, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return SQLRefPanel;
	}
}
