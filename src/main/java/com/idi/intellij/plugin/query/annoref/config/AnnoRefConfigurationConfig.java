package com.idi.intellij.plugin.query.annoref.config;

import com.google.common.collect.Maps;
import com.idi.intellij.plugin.query.annoref.common.SQLRefConstants;
import com.idi.intellij.plugin.query.annoref.connection.ConnectionUtil;
import com.idi.intellij.plugin.query.annoref.connection.DataSourceAccessorComponent;
import com.idi.intellij.plugin.query.annoref.notification.AnnoRefNotifications;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefConfigSettings;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefSettings;
import com.idi.intellij.plugin.query.annoref.util.AnnRefApplication;
import com.idi.intellij.plugin.query.annoref.util.AnnoRefBundle;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.JBColor;
import com.intellij.ui.PopupBorder;
import com.intellij.ui.components.JBLabel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.JavaeeIcons;
import com.intellij.util.ui.ColorIcon;
import com.intellij.util.ui.UIUtil;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.commons.lang.math.NumberUtils;
import org.jdesktop.swingx.color.ColorUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;

import static com.intellij.notification.NotificationDisplayType.STICKY_BALLOON;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 6/8/13
 * Time: 7:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class AnnoRefConfigurationConfig extends SearchableConfigurable.Parent.Abstract {
	public static final String IDI_PLUGIN_SETTINGS = "IDI Plugin Settings";
	private static final Logger logger = Logger.getInstance(AnnoRefConfigurationConfig.class.getName());
	private final CollectionListModel<String> myModel = new CollectionListModel<String>();
	private final Project project;
	private boolean isModified;
	private boolean isConnectionMethodChanged;
	JFileChooser jFileChooser;
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
	private JLabel colorChooserLabel;
	private JTextField colorSelectedTextBox;
	private JPanel mainSettingsPanel;
	private JTextField annoRefXmlAttributeId;
	private JCheckBox enableUtilsClassScanCheckBox;
	private JCheckBox enableSqlValidation;
	private JComboBox connectionPoolComboBox;
	private JLabel connectionPoolLabel;
	private JCheckBox colorSelectedHighlightCheckBox;
	private JCheckBox includeAllLibrariesCheckbox;
	private JTabbedPane tabbedPane1;
	private JComboBox uiPropertiesComboBox;
	private JLabel selectedUIPropertyColor;
	private JButton resetToDefaultsButton;
	private JTextField implementedByTextField;
	private JLabel implementedByLabel;
	private JTextField idiServiceTextField;
	private JLabel idiServiceLabel;
	private JTextField autosyncMaxIntervalTextBox;
	private AnnoRefSettings settings;
	private AnnoRefSettings settingsClone;
	private String selectDataSource;
	private String selectedConnectionMethod;
	private Map<String, Color> uiDefaultsMap = Maps.newHashMap();
	private Map<String, ColorIcon> uiNewColorsMap = Maps.newHashMap();

	public AnnoRefConfigurationConfig(Project project) {
		logger.info("AnnoRefConfigurationConfig(): init");
		this.project = project;
		try {
			if (AnnoRefConfigSettings.getInstance(project).getAnnoRefState() == null) {
				settings = new AnnoRefSettings();
			} else {
				settings = AnnoRefConfigSettings.getInstance(project).getAnnoRefState();
			}
			settingsClone = settings.clone();
		} catch (Exception e) {
			logger.error("AnnoRefConfigurationConfig(): init Error=" + e.getMessage(), e);
		}
		addColorSelectionListener();

	}

	private void addResetToDefaultListener() {
		resetToDefaultsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				UIDefaults uiDefaults = UIManager.getDefaults();
				for (final String prop : uiDefaultsMap.keySet()) {
					uiDefaults.put(prop, uiDefaultsMap.get(prop));
				}
				Notifications.Bus.notify(new Notification(STICKY_BALLOON.getTitle(), "Colors Reset", "Colors were reset to default", NotificationType.INFORMATION));
			}
		});
	}

	@Nls
	@Override
	public String getDisplayName() {
		return IDI_PLUGIN_SETTINGS;
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
		enableUtilsClassScanCheckBox.setSelected(settings.ENABLE_UTIL_CLASS_SCAN);
		annoRefFQN.setText(settings.ANNOREF_ANNOTATION_FQN);
		annoFQN.setText(settings.ANNO_ANNOTATION_FQN);
		annoRefAttributeId.setText(settings.ANNOREF_ANNOTATION_ATTRIBUTE_ID);
		annoRefXmlAttributeId.setText(settings.XML_ELEMENT_ATTRIBUTE_ID);
		annoRefEnableSuper.setEnabled(true);
		annoRefEnableSuper.setSelected(settings.ENABLE_ANNO_SUPER);
		enableSqlValidation.setSelected(settings.ENABLE_SQL_TO_MODEL_VALIDATION);
		annoRefSuperFQNEnableDisable(annoRefEnableSuper.isSelected());
		annoRefSuperFQN.setText(settings.ANNO_REF_SUPER_INTERFACE);
		spViewText.setText(settings.SP_VIEW_ANNOTATION_FQN);
		colorSelectedTextBox.setText(settings.ANNO_ALL_SYNTAX_HIGHLIGHT_COLOR);
		colorSelectedHighlightCheckBox.setSelected(settings.ANNO_ALL_SYNTAX_HIGHLIGHT_ENABLE);
		includeAllLibrariesCheckbox.setSelected(settings.DEEP_SCAN_ENABLED);
		implementedByTextField.setText(settings.IMPLEMENTED_BY_FRAMEWORK_FQN);
		idiServiceTextField.setText(settings.IDI_SERVICE_FRAMEWORK_FQN);
		spDataSourceComboBox.setModel(getDataSourcesModel());
		connectionPoolComboBox.setModel(getConnectionMethodModel());
		autosyncMaxIntervalTextBox.setText(settings.REINDEX_INTERVAL.toString());
//		uiPropertiesComboBox.setModel(getUIPropertiesModel());
		addDataSourceComboBoxListener();
		addSelectedUIPropertyColorListener();
		addTestConnectionListener();
		addConnectionMethodComboBoxListener();
		selectedConfiguredDataSource();
		addResetToDefaultListener();
		return SQLRefPanel;
	}


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

	private void selectedConfiguredConnectionMethod() {
		UIUtil.invokeAndWaitIfNeeded(new Runnable() {
			@Override
			public void run() {
				logger.info("run(): settings.CONNECTION_METHOD_WAY=" + settings.CONNECTION_METHOD_WAY);
				connectionPoolComboBox.getModel().setSelectedItem(settings.CONNECTION_METHOD_WAY);
				logger.info("run(): selectedConfiguredConnectionMethod.getModel()=" + spDataSourceComboBox.getModel().getSelectedItem());

			}
		});
	}

	private ComboBoxModel getDataSourcesModel() {
		final Collection<String> availableConnections = AnnRefApplication.getInstance(project, DataSourceAccessorComponent.class).getAvailableConnections(project);
		final ArrayList<String> dataSourceList = new ArrayList<String>();
		dataSourceList.add("");
		for (final String availableConnection : availableConnections) {
			dataSourceList.add(availableConnection);
		}
		return new CollectionComboBoxModel(dataSourceList);
	}

	private ComboBoxModel getConnectionMethodModel() {
		final LinkedList<String> connectionMethod = new LinkedList<String>();
		connectionMethod.add("Single-Connection");
		connectionMethod.add("Connection-Pool");
		return new CollectionComboBoxModel(connectionMethod);
	}


	private ComboBoxModel getUIPropertiesModel() {
		UIDefaults uiDefaults = UIManager.getDefaults();
		final LinkedList<String> uiPropertiesList = new LinkedList<String>();
		uiPropertiesList.add("");
		populateWhenConfigured(uiDefaults, uiPropertiesList, "Tree.selectionForeground");
		populateWhenConfigured(uiDefaults, uiPropertiesList, "Tree.textForeground");
		populateWhenConfigured(uiDefaults, uiPropertiesList, "Tree.textBackground");
		populateWhenConfigured(uiDefaults, uiPropertiesList, "Tree.foreground");
		populateWhenConfigured(uiDefaults, uiPropertiesList, "Tree.background");
		populateWhenConfigured(uiDefaults, uiPropertiesList, "Tree.selectionBorderColor");
		populateWhenConfigured(uiDefaults, uiPropertiesList, "Table.focusCellHighlightBorder");
		populateWhenConfigured(uiDefaults, uiPropertiesList, "List.focusCellHighlightBorder");
		for (final String prop : uiPropertiesList) {
			if (!prop.isEmpty() && uiDefaults.getColor(prop) != null) {
				uiDefaultsMap.put(prop, uiDefaults.getColor(prop));
			}
		}
		return new CollectionComboBoxModel(uiPropertiesList);
	}

	private void populateWhenConfigured(UIDefaults uiDefaults, List<String> uiPropertiesList, String uiProp) {
		if (uiDefaults.getColor(uiProp) != null) {
			uiPropertiesList.add(uiProp);
		}
	}


	private void addColorSelectionListener() {
		colorSelectedTextBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color defaultColor;
				if (settings.ANNO_ALL_SYNTAX_HIGHLIGHT_COLOR != null && !settings.ANNO_ALL_SYNTAX_HIGHLIGHT_COLOR.isEmpty()) {
					defaultColor = Color.decode(settings.ANNO_ALL_SYNTAX_HIGHLIGHT_COLOR);
				} else {
					defaultColor = DefaultLanguageHighlighterColors.STRING.getDefaultAttributes().getForegroundColor();
				}
				final Color color = JColorChooser.showDialog(SQLRefPanel, "Select Color For Annotation SyntaxHighlighting", defaultColor);
				final String toHexString = ColorUtil.toHexString(color);
				colorSelectedTextBox.setText(toHexString);
			}
		});
	}

	private void addSelectedUIPropertyColorListener() {
		selectedUIPropertyColor.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				final String prop = uiPropertiesComboBox.getSelectedItem().toString();
				if (uiNewColorsMap.get(prop) != null) {
					final Color color = JColorChooser.showDialog(uiPropertiesComboBox, "Select Color for property", uiNewColorsMap.get(prop).getIconColor());
					UIDefaults uiDefaults = UIManager.getDefaults();
					uiDefaults.put(prop, color);
//				final ColorIcon icon = new ColorIcon(25, UIManager.getColor(cb.getSelectedItem()));
					selectedUIPropertyColor.setIcon(new ColorIcon(25, color));
				}
			}
		});

		uiPropertiesComboBox.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				logger.info("propertyChange(): oldValue=" + evt.getOldValue() + " newValue=" + evt.getNewValue()
				);

			}

		});
	}

	private void addDataSourceComboBoxListener() {
		uiPropertiesComboBox.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox) e.getSource();
				if (cb != null && cb.getSelectedItem() != null) {
					final Color color = UIManager.getColor(cb.getSelectedItem());
					if (color != null) {
						final ColorIcon icon = new ColorIcon(25, color);
						selectedUIPropertyColor.setIcon(icon);
						uiNewColorsMap.put(cb.getSelectedItem().toString(), icon);
					}
				}

//				selectDataSource = (String) cb.getSelectedItem();
//				logger.info("actionPerformed(): selectDataSource=" + selectDataSource);

			}


		});
	}

	private void addUIPropertiesComboBoxListener() {
		uiPropertiesComboBox.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox) e.getSource();
				selectDataSource = (String) cb.getSelectedItem();
				logger.info("actionPerformed(): selectDataSource=" + selectDataSource);

			}
		});
	}

	private void addConnectionMethodComboBoxListener() {
		connectionPoolComboBox.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox) e.getSource();
				selectedConnectionMethod = (String) cb.getSelectedItem();
				logger.info("actionPerformed(): selectedConnectionMethod=" + selectedConnectionMethod);

			}
		});
	}

	private void addTestConnectionListener() {
		testConnectionBtn.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {

				UIUtil.invokeAndWaitIfNeeded(new Runnable() {
					@Override
					public void run() {
						invokeAndWait();
					}
				});
			}

			private void invokeAndWait() {
				final DataSourceAccessorComponent dbAccessor = AnnRefApplication.getInstance(project, DataSourceAccessorComponent.class);
				if (selectDataSource == null) {
					selectDataSource = settings.SP_DATA_SOURCE_NAME;
				}
				final String dataSource = String.valueOf(spDataSourceComboBox.getSelectedItem());
				if (dataSource != null && !dataSource.isEmpty()) {
					final String configuredDataSourceName = dbAccessor.getConfiguredDataSourceName();
					if (ConnectionUtil.initDataSource(project, selectDataSource) && dbAccessor.testConnectionInBackground(project)) {
						showTestConnectionMessageDialog("Connection to " + dataSource + " Successful", Messages.getInformationIcon());
					} else {
						showTestConnectionMessageDialog("Connection to " + dataSource + " Failed", Messages.getErrorIcon());
					}
				} else {
					showTestConnectionMessageDialog("No Connection Selected", Messages.getWarningIcon());
				}
			}
		});
	}


	private void showTestConnectionMessageDialog(final String message, final Icon messageIcon) {
		UIUtil.invokeAndWaitIfNeeded(new Runnable() {
			@Override
			public void run() {
				Messages.showMessageDialog(message, "DataSource Connection Test", messageIcon);
			}
		});
	}


	@Override
	public boolean isModified() {
		try {
			if (settingsClone.ENABLE_AUTO_SYNC) {
				logger.info("isModified(): ENABLE_AUTO_SYNC=" + settingsClone.ENABLE_AUTO_SYNC);
			}
			if (enableAnnotationFQNOverrideCheckBox.isSelected()) {
				annoRefFQNTextBoxesEnableDisable(true);
				if (!settingsClone.ANNOREF_ANNOTATION_FQN.equals(annoRefFQN.getText().trim()) ||
						!settingsClone.ANNO_ANNOTATION_FQN.equals(annoFQN.getText().trim()) ||
						!settingsClone.ANNOREF_ANNOTATION_ATTRIBUTE_ID.equals(annoRefAttributeId.getText().trim())) {
					settingsClone.ANNOREF_ANNOTATION_FQN = annoRefFQN.getText().trim();
					settingsClone.ANNO_ANNOTATION_FQN = annoFQN.getText().trim();
					settingsClone.ANNOREF_ANNOTATION_ATTRIBUTE_ID = annoRefAttributeId.getText().trim();
					isModified = true;
				}
			} else {
				annoRefFQNTextBoxesEnableDisable(false);
			}
			if (settingsClone.ENABLE_SQLREF_FQN_OVERRIDE != enableAnnotationFQNOverrideCheckBox.isSelected()) {
				isModified = true;
			}
			settingsClone.ENABLE_SQLREF_FQN_OVERRIDE = enableAnnotationFQNOverrideCheckBox.isSelected();
			/*****************************************************************************************************************/
			annoRefSuperFQNEnableDisable(annoRefEnableSuper.isSelected());
			if (settingsClone.ENABLE_ANNO_SUPER != annoRefEnableSuper.isSelected()) {
				isModified = true;
			}
			settingsClone.ENABLE_ANNO_SUPER = annoRefEnableSuper.isSelected();
			/*****************************************************************************************************************/
		/*if (settingsClone.SP_DATA_SOURCE_NAME != spDataSourceList.getModel().getElementAt(0)) {
//			settingsClone.SP_DATA_SOURCE_NAME = spViewDataSourceText.getText().trim();
			final boolean dataSource = ServiceManager.getService(project, DataSourceAccessorComponent.class).initDataSource(project, settingsClone.SP_DATA_SOURCE_NAME);
			if (dataSource) {
				constructAMessagePopUp("DataSource " + dataSource + " configured successfully", null);
			} else {
				constructAMessagePopUp("DataSource " + dataSource + " wasn't found in DataSources configuration", null);
			}
			isModified = true;
		}*/
			if (!spViewText.getText().trim().equals(settingsClone.SP_VIEW_ANNOTATION_FQN)) {
				settingsClone.SP_VIEW_ANNOTATION_FQN = spViewText.getText().trim();
				isModified = true;
			}
			/*****************************************************************************************************************/
			if (!settingsClone.SP_DATA_SOURCE_NAME.equals(String.valueOf(spDataSourceComboBox.getModel().getSelectedItem()))) {
				settingsClone.SP_DATA_SOURCE_NAME = String.valueOf(spDataSourceComboBox.getModel().getSelectedItem());
				isModified = true;
			}
			if (!settingsClone.CONNECTION_METHOD_WAY.equals(String.valueOf(connectionPoolComboBox.getModel().getSelectedItem()))) {
				settingsClone.CONNECTION_METHOD_WAY = String.valueOf(connectionPoolComboBox.getModel().getSelectedItem());
				isConnectionMethodChanged = true;
				isModified = true;
			}

			if (!annoRefSuperFQN.getText().trim().equals(settingsClone.ANNO_REF_SUPER_INTERFACE)) {
				settingsClone.ANNO_REF_SUPER_INTERFACE = annoRefSuperFQN.getText().trim();
				isModified = true;
			}
			if (!implementedByTextField.getText().trim().equals(settingsClone.IMPLEMENTED_BY_FRAMEWORK_FQN)) {
				settingsClone.IMPLEMENTED_BY_FRAMEWORK_FQN = implementedByTextField.getText().trim();
				isModified = true;
			}
			if (!idiServiceTextField.getText().trim().equals(settingsClone.IDI_SERVICE_FRAMEWORK_FQN)) {
				settingsClone.IDI_SERVICE_FRAMEWORK_FQN = idiServiceTextField.getText().trim();
				isModified = true;
			}
			if (autosyncProjectRootCheckBox.isSelected() != settingsClone.ENABLE_AUTO_SYNC) {
				settingsClone.ENABLE_AUTO_SYNC = autosyncProjectRootCheckBox.isSelected();
				isModified = true;
			}
			if (enableUtilsClassScanCheckBox.isSelected() != settingsClone.ENABLE_UTIL_CLASS_SCAN) {
				settingsClone.ENABLE_UTIL_CLASS_SCAN = enableUtilsClassScanCheckBox.isSelected();
				isModified = true;
			}
			if (includeAllLibrariesCheckbox.isSelected() != settingsClone.DEEP_SCAN_ENABLED) {
				settingsClone.DEEP_SCAN_ENABLED = includeAllLibrariesCheckbox.isSelected();
				isModified = true;
			}
			if (enableSqlValidation.isSelected() != settingsClone.ENABLE_SQL_TO_MODEL_VALIDATION) {
				settingsClone.ENABLE_SQL_TO_MODEL_VALIDATION = enableSqlValidation.isSelected();
				isModified = true;
			}
			if (!colorSelectedTextBox.getText().isEmpty() && !colorSelectedTextBox.getText().equals(settingsClone.ANNO_ALL_SYNTAX_HIGHLIGHT_COLOR)) {
				settingsClone.ANNO_ALL_SYNTAX_HIGHLIGHT_COLOR = colorSelectedTextBox.getText();
				isModified = true;
			}
			if (colorSelectedHighlightCheckBox.isSelected() != settingsClone.ANNO_ALL_SYNTAX_HIGHLIGHT_ENABLE) {
				settingsClone.ANNO_ALL_SYNTAX_HIGHLIGHT_ENABLE = colorSelectedHighlightCheckBox.isSelected();
				isModified = true;
			}
			if (NumberUtils.isNumber(autosyncMaxIntervalTextBox.getText())) {
				if (Integer.parseInt(autosyncMaxIntervalTextBox.getText()) != settingsClone.REINDEX_INTERVAL) {
					settingsClone.REINDEX_INTERVAL = Integer.parseInt(autosyncMaxIntervalTextBox.getText());
					isModified = true;
				}
			}
		} catch (Exception e) {
			ServiceManager.getService(project, AnnoRefNotifications.class).notifyAnnoRefError(project, AnnoRefBundle.message("annoRef.configuration.loading.error"));
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
				final DataSourceAccessorComponent dbAccessor = AnnRefApplication.getInstance(project, DataSourceAccessorComponent.class);
				final boolean configured = dbAccessor.initDataSource(project, selectDataSource, true);
				spDataSourceComboBox.getModel().setSelectedItem(selectDataSource);
				if (configured) {
					dbAccessor.getConnectionPool().closeConnectionsSilently();
					WindowManager.getInstance().getStatusBar(project).fireNotificationPopup(constructAMessagePopUp("Selected DataSource " + selectDataSource + " Configured!", JavaeeIcons.DATASOURCE_REMOTE_INSTANCE),
							JBColor.GREEN);
				} else {
					WindowManager.getInstance().getStatusBar(project).fireNotificationPopup(constructAMessagePopUp("DataSource Not Configured!", JavaeeIcons.DATASOURCE_REMOTE_INSTANCE),
							JBColor.GREEN);
				}
			}
			AnnoRefConfigSettings instance = AnnoRefConfigSettings.getInstance(project);
			settings.copyClone(settingsClone);
			instance.loadState(settings);
		}
		if (isConnectionMethodChanged) {
			final DataSourceAccessorComponent dbAccessor = AnnRefApplication.getInstance(project, DataSourceAccessorComponent.class);
			dbAccessor.getConnectionPool().closeConnectionsSilently();
			WindowManager.getInstance().getStatusBar(project).fireNotificationPopup(constructAMessagePopUp("Connection method changed to " + selectedConnectionMethod, JavaeeIcons.DATASOURCE_REMOTE_INSTANCE),
					JBColor.GREEN);
			isConnectionMethodChanged = false;
		}
		isModified = false;
	}

	@Override
	public void reset() {
		enableAnnotationFQNOverrideCheckBox.setSelected(settings.ENABLE_SQLREF_FQN_OVERRIDE);
		annoRefFQN.setText(settings.ANNOREF_ANNOTATION_FQN);
		annoRefAttributeId.setText(settings.ANNOREF_ANNOTATION_ATTRIBUTE_ID);
		annoRefXmlAttributeId.setText(settings.XML_ELEMENT_ATTRIBUTE_ID);
		annoFQN.setText(settings.ANNO_ANNOTATION_FQN);
		autosyncProjectRootCheckBox.setSelected(settings.ENABLE_AUTO_SYNC);
		annoRefSuperFQN.setText(settings.ANNO_REF_SUPER_INTERFACE);
		annoRefEnableSuper.setSelected(settings.ENABLE_ANNO_SUPER);
//		spDataSourceComboBox.setModel(getDataSourcesModel());
		spViewText.setText(settings.SP_VIEW_ANNOTATION_FQN);
		colorSelectedTextBox.setText(settings.ANNO_ALL_SYNTAX_HIGHLIGHT_COLOR);
		enableSqlValidation.setSelected(settings.ENABLE_SQL_TO_MODEL_VALIDATION);
		enableUtilsClassScanCheckBox.setSelected(settings.ENABLE_UTIL_CLASS_SCAN);
		connectionPoolComboBox.getModel().setSelectedItem(settings.CONNECTION_METHOD_WAY);
		colorSelectedHighlightCheckBox.setSelected(settings.ANNO_ALL_SYNTAX_HIGHLIGHT_ENABLE);
		includeAllLibrariesCheckbox.setSelected(settings.DEEP_SCAN_ENABLED);
		idiServiceTextField.setText(settings.IDI_SERVICE_FRAMEWORK_FQN);
		implementedByTextField.setText(settings.IMPLEMENTED_BY_FRAMEWORK_FQN);
		autosyncMaxIntervalTextBox.setText(String.valueOf(settings.REINDEX_INTERVAL));
		isModified = false;

//		spDataSourceComboBox.getModel().setSelectedItem(settingsClone.SP_DATA_SOURCE_NAME);
	}

	@Override
	public void disposeUIResources() {
	}

	@NotNull
	@Override
	public String getId() {
		return IDI_PLUGIN_SETTINGS;
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
		return new AnnoRefConfigurationConfig[]{this};
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
		SQLRefPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		tabbedPane1 = new JTabbedPane();
		SQLRefPanel.add(tabbedPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
		final JPanel panel1 = new JPanel();
		panel1.setLayout(new FormLayout("fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:d:grow", "center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
		tabbedPane1.addTab("General Settings", panel1);
		final JPanel panel2 = new JPanel();
		panel2.setLayout(new FormLayout("fill:d:grow,left:4dlu:noGrow,fill:d:grow,left:4dlu:noGrow,fill:max(d;4px):noGrow", "center:d:grow,top:4dlu:noGrow,center:d:grow"));
		CellConstraints cc = new CellConstraints();
		panel1.add(panel2, cc.xy(1, 1));
		enableConversion = new JCheckBox();
		enableConversion.setEnabled(false);
		enableConversion.setText("Enable Anno To AnnoRef Conversion");
		panel2.add(enableConversion, cc.xyw(1, 1, 3, CellConstraints.LEFT, CellConstraints.DEFAULT));
		enableUtilsClassScanCheckBox = new JCheckBox();
		enableUtilsClassScanCheckBox.setText("Enable Util Class Scan");
		enableUtilsClassScanCheckBox.setToolTipText("Scanning for Util class on project load (will be disabled if autosync is disabled) ");
		panel2.add(enableUtilsClassScanCheckBox, cc.xy(1, 3, CellConstraints.LEFT, CellConstraints.DEFAULT));
		autosyncProjectRootCheckBox = new JCheckBox();
		autosyncProjectRootCheckBox.setLabel("Autosync Project Root");
		autosyncProjectRootCheckBox.setText("Autosync Project Root");
		autosyncProjectRootCheckBox.setToolTipText("autosync the project when root changes, like when adding or removing module via maven pom structure");
		panel2.add(autosyncProjectRootCheckBox, cc.xy(3, 3, CellConstraints.LEFT, CellConstraints.DEFAULT));
		final JPanel panel3 = new JPanel();
		panel3.setLayout(new FormLayout("fill:d:grow,left:4dlu:noGrow,fill:d:grow", "center:d:grow,top:4dlu:noGrow,center:d:grow"));
		panel3.setMinimumSize(new Dimension(100, 27));
		panel1.add(panel3, cc.xyw(3, 1, 3));
		includeAllLibrariesCheckbox = new JCheckBox();
		includeAllLibrariesCheckbox.setText("Deep Scan (include all dependencies)");
		panel3.add(includeAllLibrariesCheckbox, cc.xy(1, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));
		enableSqlValidation = new JCheckBox();
		enableSqlValidation.setEnabled(false);
		enableSqlValidation.setText("Enable SQL to Model validation");
		enableSqlValidation.setToolTipText("Still in development stage");
		panel3.add(enableSqlValidation, cc.xy(3, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));
		enableAnnotationFQNOverrideCheckBox = new JCheckBox();
		enableAnnotationFQNOverrideCheckBox.setActionCommand("Enable Annotations' FQN Override");
		enableAnnotationFQNOverrideCheckBox.setText("Enable Annotations' FQN Overide");
		enableAnnotationFQNOverrideCheckBox.setToolTipText("When disabled, the FQN for the SQLRef FQN will be the default com.idi");
		panel3.add(enableAnnotationFQNOverrideCheckBox, cc.xy(1, 3, CellConstraints.LEFT, CellConstraints.DEFAULT));
		final JPanel panel4 = new JPanel();
		panel4.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
		panel1.add(panel4, cc.xy(1, 3));
		final JLabel label1 = new JLabel();
		label1.setText("Select UI Property");
		panel4.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		uiPropertiesComboBox = new JComboBox();
		panel4.add(uiPropertiesComboBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JPanel panel5 = new JPanel();
		panel5.setLayout(new FormLayout("fill:d:grow,left:4dlu:noGrow,fill:d:grow", "center:d:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
		panel5.setMinimumSize(new Dimension(71, 40));
		panel5.setPreferredSize(new Dimension(71, 80));
		panel1.add(panel5, new CellConstraints(1, 5, 1, 1, CellConstraints.DEFAULT, CellConstraints.DEFAULT, new Insets(5, 0, 5, 0)));
		final JLabel label2 = new JLabel();
		label2.setHorizontalAlignment(2);
		label2.setText("Current Color");
		panel5.add(label2, cc.xy(1, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));
		selectedUIPropertyColor = new JLabel();
		selectedUIPropertyColor.setHorizontalAlignment(2);
		selectedUIPropertyColor.setText("");
		panel5.add(selectedUIPropertyColor, cc.xy(3, 1));
		resetToDefaultsButton = new JButton();
		resetToDefaultsButton.setText("Reset To Defaults");
		panel5.add(resetToDefaultsButton, cc.xy(1, 3, CellConstraints.CENTER, CellConstraints.DEFAULT));
		final JLabel label3 = new JLabel();
		label3.setText("Autosync Mininmum Interval");
		label3.setToolTipText("Set the minimum interval for the plugin to reindex when noticing a change in the projects structure");
		panel1.add(label3, cc.xy(3, 3, CellConstraints.LEFT, CellConstraints.DEFAULT));
		autosyncMaxIntervalTextBox = new JTextField();
		autosyncMaxIntervalTextBox.setMinimumSize(new Dimension(100, 20));
		autosyncMaxIntervalTextBox.setPreferredSize(new Dimension(100, 20));
		panel1.add(autosyncMaxIntervalTextBox, cc.xy(5, 3, CellConstraints.LEFT, CellConstraints.DEFAULT));
		mainSettingsPanel = new JPanel();
		mainSettingsPanel.setLayout(new GridLayoutManager(21, 5, new Insets(2, 2, 2, 2), -1, -1));
		tabbedPane1.addTab("Advance Settings", mainSettingsPanel);
		annoRefFQN = new JTextField();
		annoRefFQN.setBackground(new Color(-1));
		annoRefFQN.setEditable(false);
		annoRefFQN.setEnabled(false);
		annoRefFQN.setMargin(new Insets(2, 2, 2, 2));
		annoRefFQN.setToolTipText("The FQN for the annotation to use");
		mainSettingsPanel.add(annoRefFQN, new GridConstraints(1, 0, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		final JLabel label4 = new JLabel();
		label4.setText("AnnoRef annotation's fully qualifed name: ");
		mainSettingsPanel.add(label4, new GridConstraints(0, 0, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		annoRefAttributeId = new JTextField();
		annoRefAttributeId.setToolTipText("The annotation attribute name to use for reference in the corresponding xml file id");
		mainSettingsPanel.add(annoRefAttributeId, new GridConstraints(5, 0, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		final JLabel label5 = new JLabel();
		label5.setText("Annotation Attribute Id name:");
		label5.setToolTipText("");
		mainSettingsPanel.add(label5, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		annoFQN = new JTextField();
		annoFQN.setBackground(new Color(-1));
		annoFQN.setEditable(false);
		annoFQN.setEnabled(false);
		mainSettingsPanel.add(annoFQN, new GridConstraints(3, 0, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		final JLabel label6 = new JLabel();
		label6.setText("Initial annotation's fully qualifed name:");
		mainSettingsPanel.add(label6, new GridConstraints(2, 0, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label7 = new JLabel();
		label7.setText("Xml structure baselines: ");
		label7.setToolTipText(ResourceBundle.getBundle("annoconfig").getString("xml.structure.example"));
		label7.setVisible(false);
		mainSettingsPanel.add(label7, new GridConstraints(19, 0, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		xmlSchemaTextArea = new JTextArea();
		xmlSchemaTextArea.setBackground(new Color(-6500));
		xmlSchemaTextArea.setVisible(false);
		mainSettingsPanel.add(xmlSchemaTextArea, new GridConstraints(20, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
		annoRefEnableSuper = new JCheckBox();
		annoRefEnableSuper.setSelected(true);
		annoRefEnableSuper.setText("Enable Super interface/class ");
		mainSettingsPanel.add(annoRefEnableSuper, new GridConstraints(12, 0, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 2, false));
		annoRefSuperFQN = new JTextField();
		mainSettingsPanel.add(annoRefSuperFQN, new GridConstraints(13, 0, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		spViewText = new JTextField();
		mainSettingsPanel.add(spViewText, new GridConstraints(15, 0, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		spViewFQNLabel = new JLabel();
		spViewFQNLabel.setText("SP Viewer fully qualified name:");
		mainSettingsPanel.add(spViewFQNLabel, new GridConstraints(14, 0, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		spViewDefaultLabel = new JLabel();
		spViewDefaultLabel.setText("SP DataSource value:");
		mainSettingsPanel.add(spViewDefaultLabel, new GridConstraints(16, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		spDataSourceComboBox = new JComboBox();
		final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
		spDataSourceComboBox.setModel(defaultComboBoxModel1);
		mainSettingsPanel.add(spDataSourceComboBox, new GridConstraints(16, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		testConnectionBtn = new JButton();
		testConnectionBtn.setText("Test Connection");
		mainSettingsPanel.add(testConnectionBtn, new GridConstraints(16, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		colorChooserLabel = new JLabel();
		colorChooserLabel.setText("Define color for annotation highlight:");
		mainSettingsPanel.add(colorChooserLabel, new GridConstraints(17, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		colorSelectedTextBox = new JTextField();
		colorSelectedTextBox.setEditable(true);
		colorSelectedTextBox.setText("");
		mainSettingsPanel.add(colorSelectedTextBox, new GridConstraints(17, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		annoRefXmlAttributeId = new JTextField();
		mainSettingsPanel.add(annoRefXmlAttributeId, new GridConstraints(11, 0, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		final JLabel label8 = new JLabel();
		label8.setText("Xml Attribute Id name:");
		mainSettingsPanel.add(label8, new GridConstraints(10, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		connectionPoolComboBox = new JComboBox();
		mainSettingsPanel.add(connectionPoolComboBox, new GridConstraints(18, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		connectionPoolLabel = new JLabel();
		connectionPoolLabel.setText("Choose Plugin connection method:");
		mainSettingsPanel.add(connectionPoolLabel, new GridConstraints(18, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		colorSelectedHighlightCheckBox = new JCheckBox();
		colorSelectedHighlightCheckBox.setEnabled(true);
		colorSelectedHighlightCheckBox.setText("Enable Highlighting");
		mainSettingsPanel.add(colorSelectedHighlightCheckBox, new GridConstraints(17, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		implementedByTextField = new JTextField();
		mainSettingsPanel.add(implementedByTextField, new GridConstraints(9, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		implementedByLabel = new JLabel();
		implementedByLabel.setText("ImplementedBy fully qualified name:");
		mainSettingsPanel.add(implementedByLabel, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		idiServiceTextField = new JTextField();
		mainSettingsPanel.add(idiServiceTextField, new GridConstraints(7, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		idiServiceLabel = new JLabel();
		idiServiceLabel.setText("IDIService fully qualified name:");
		mainSettingsPanel.add(idiServiceLabel, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return SQLRefPanel;
	}
}
