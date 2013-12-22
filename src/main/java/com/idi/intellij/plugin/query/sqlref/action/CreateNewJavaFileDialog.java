package com.idi.intellij.plugin.query.sqlref.action;

import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonShortcuts;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.FixedSizeButton;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiPackage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CreateNewJavaFileDialog extends DialogWrapper {
	protected CentralPanel fPanel = new CentralPanel();
	protected PsiDirectory fDirectory = null;
	protected Project fProject = null;

	public CreateNewJavaFileDialog(final Project aProject, PsiDirectory aDirectory, String aCurrentPackage) {
		super(aProject, true);
		this.fProject = aProject;
		this.fDirectory = aDirectory;
		CentralPanel panel = new CentralPanel();
		customizeMainPanel(panel, aCurrentPackage);
		this.fPanel = panel;
		this.fPanel.fPackageSelectionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PackageChooserDialog packageDialog = new PackageChooserDialog("Choose Destination Package", aProject);
				packageDialog.selectPackage(CreateNewJavaFileDialog.this.fPanel.fPackageText.getText());
				packageDialog.show();
				PsiPackage aPackage = packageDialog.getSelectedPackage();
				if (aPackage != null) {
					CreateNewJavaFileDialog.this.fPanel.fPackageText.setText(aPackage.getQualifiedName());
				}
			}
		});
		setTitle("Create New Java File");
		init();
	}

	protected static void customizeMainPanel(CentralPanel aResult, String aPackageName) {
		DefaultComboBoxModel model = new DefaultComboBoxModel();

		List<TemplateDescriptor> descriptors = CreateUtils.getTemplateDescriptors();
		for (TemplateDescriptor descriptor : descriptors) {
			model.addElement(descriptor);
		}

		aResult.fTypeCombobox.setModel(model);

		aResult.fPackageText.setText(aPackageName);

		aResult.fTypeCombobox.setRenderer(new DefaultListCellRenderer() {
			protected JLabel fLabel = new JLabel();

			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				TemplateDescriptor descriptor = (TemplateDescriptor) value;
				setText(descriptor.getTemplateName());
				setIcon(descriptor.getIcon());
				return this;
			}
		});
	}

	public PsiDirectory getDirectory() {
		return this.fDirectory;
	}

	public void setDirectory(PsiDirectory aDirectory) {
		this.fDirectory = aDirectory;
	}

	public void setPanel(CentralPanel aPanel) {
		this.fPanel = aPanel;
	}

	protected JComponent createCenterPanel() {
		return this.fPanel;
	}

	protected void doOKAction() {
		String javaType = getTypeName();
		if (javaType == null) {
			Messages.showMessageDialog(this.fProject, "Please specify name of Java type", "Error", Messages.getErrorIcon());
		} else {
			super.doOKAction();
		}
	}

	public void doCancelAction() {
		super.doCancelAction();
	}

	public JComponent getPreferredFocusedComponent() {
		return this.fPanel.fJavaName;
	}

	public boolean shouldCloseOnCross() {
		return true;
	}

	public String getPackageName() {
		String result = this.fPanel.fPackageText.getText();
		if (result == null) {
			result = "";
		} else {
			result = result.trim();
		}
		return result;
	}

	public String getTemplateName() {
		TemplateDescriptor descriptor = (TemplateDescriptor) this.fPanel.fTypeCombobox.getSelectedItem();
		String result = descriptor.getTemplateName();
		return result;
	}

	public String getTypeName() {
		String result = this.fPanel.fJavaName.getText();
		if (result != null) {
			result = result.trim();
			if (result.length() == 0) {
				result = null;
			}
		}
		return result;
	}

	class CentralPanel extends JPanel {
		GridBagLayout gridBagLayout1 = new GridBagLayout();
		JLabel fNameLabel = new JLabel();
		JTextField fJavaName = new JTextField();
		JLabel fTypeName = new JLabel();
		JLabel fPackageNameLabel = new JLabel();
		ComboBox fTypeCombobox = new ComboBox();
		JTextField fPackageText = new JTextField();
		FixedSizeButton fPackageSelectionButton = new FixedSizeButton(this.fPackageText);

		public CentralPanel() {
			try {
				new AnAction() {
					public final void actionPerformed(AnActionEvent anactionevent) {
						CreateNewJavaFileDialog.CentralPanel.this.fPackageSelectionButton.doClick();
					}
				}
						.registerCustomShortcutSet(CommonShortcuts.ALT_ENTER, this.fPackageText);

				jbInit();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}

		private void jbInit()
				throws Exception {
			setLayout(this.gridBagLayout1);
			this.fNameLabel.setToolTipText("");
			this.fNameLabel.setText("Name:");
			this.fTypeName.setText("Create");
			this.fPackageNameLabel.setText("Package Name:");
			this.fPackageText.setMinimumSize(new Dimension(350, 21));
			this.fPackageText.setPreferredSize(new Dimension(130, 21));
			this.fPackageText.setText("jTextField1");

			add(this.fNameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(5, 5, 0, 5), 0, 0));

			add(this.fTypeName, new GridBagConstraints(0, 1, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(5, 5, 0, 5), 0, 0));

			add(this.fPackageNameLabel, new GridBagConstraints(0, 2, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(5, 5, 0, 5), 0, 0));

			add(this.fPackageSelectionButton, new GridBagConstraints(2, 2, 1, 1, 0.0D, 0.0D, 10, 1, new Insets(5, 5, 0, 5), 0, 0));

			add(this.fTypeCombobox, new GridBagConstraints(1, 1, 2, 1, 0.0D, 0.0D, 10, 2, new Insets(5, 0, 0, 5), 0, 0));

			add(this.fJavaName, new GridBagConstraints(1, 0, 2, 1, 1.0D, 0.0D, 17, 2, new Insets(5, 0, 0, 5), 0, 0));

			add(this.fPackageText, new GridBagConstraints(1, 2, 1, 1, 1.0D, 0.0D, 10, 2, new Insets(5, 0, 0, 0), 0, 0));
		}
	}
}
