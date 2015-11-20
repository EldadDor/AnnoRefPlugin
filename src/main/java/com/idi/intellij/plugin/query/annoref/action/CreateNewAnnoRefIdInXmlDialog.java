package com.idi.intellij.plugin.query.annoref.action;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.ide.util.TreeFileChooser;
import com.intellij.ide.util.TreeFileChooserFactory;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.PathChooserDialog;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.FixedSizeButton;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPackage;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 10/4/13
 * Time: 1:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class CreateNewAnnoRefIdInXmlDialog extends DialogWrapper {
	private static final Logger logger = Logger.getInstance(CreateNewAnnoRefIdInXmlDialog.class.getName());

	private String packageName;
	private Project project;
	private Module module;
	private String annoRefId;
	private CentralPanel centralPanel;

	public CreateNewAnnoRefIdInXmlDialog(@Nullable Project project, Module module, String packageName, String annoRefId) {
		super(project, true);
		this.project = project;
		this.module = module;
		this.packageName = packageName;
		this.annoRefId = annoRefId;
		createPanelForAnnoRefDialog();
		init();
		show();
	}

	protected CreateNewAnnoRefIdInXmlDialog(@Nullable Project project) {
		super(project);
	}

	private void createPanelForAnnoRefDialog() {
		centralPanel = new CentralPanel();
		final FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(false, false, true, false, false, false);
		FileChooserDialog localFileChooserDialog = FileChooserFactory.getInstance().createFileChooser(fileChooserDescriptor, project, centralPanel);
		final PathChooserDialog pathChooser = FileChooserFactory.getInstance().createPathChooser(fileChooserDescriptor, project, centralPanel);
//		pathChooser.choose();
//		localFileChooserDialog.choose()
		PackageChooserDialog packageDialog = new PackageChooserDialog("Choose Destination Package", module);
		packageDialog.selectPackage(packageName);
		packageDialog.show();
		PsiPackage aPackage = packageDialog.getSelectedPackage();
		if (aPackage != null) {
			centralPanel.fPackageText.setText(aPackage.getQualifiedName());
		}
		final PsiDirectory[] psiDirectories = aPackage.getDirectories();
		for (PsiDirectory psiDirectory : psiDirectories) {
			final Module moduleForPsiElement = ModuleUtil.findModuleForPsiElement(psiDirectory);
			if (moduleForPsiElement.getName().equals(module.getName())) {
				logger.info(psiDirectory.getName());
			}
		}
		final PsiFile containingFile = aPackage.getContainingFile();

		final TreeFileChooser.PsiFileFilter psiFileFilter = new TreeFileChooser.PsiFileFilter() {
			@Override
			public boolean accept(PsiFile file) {
				return true;  //To change body of implemented methods use File | Settings | File Templates.
			}
		};

		centralPanel.fFileSelectionButton.addActionListener(new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final TreeFileChooser fileChooser = TreeFileChooserFactory.getInstance(project).createFileChooser("Select File", containingFile, XmlFileType.INSTANCE, psiFileFilter, true);
				fileChooser.showDialog();
			}
		});

//		final VirtualFile[] choose = fileChooserDialog.choose(null, project);
		centralPanel.fAnnoRefIdText.setText(annoRefId);
	}

	@Nullable
	@Override
	protected JComponent createCenterPanel() {
		centralPanel.initCenterPanel();
		centralPanel.setVisible(true);
		return centralPanel;
	}

	class CentralPanel extends JPanel {
		GridBagLayout gridBagLayout1 = new GridBagLayout();
		JLabel fAnnoRefFileLabel = new JLabel();
		JTextField fAnnoRefFileText = new JTextField();
		JLabel fAnnoRefIdLabel = new JLabel();
		JTextField fAnnoRefIdText = new JTextField();
		JTextField fPackageText = new JTextField();
		FixedSizeButton fFileSelectionButton = new FixedSizeButton(fPackageText);
		JLabel fPackageNameLabel = new JLabel();


		private void initCenterPanel() {
			setLayout(this.gridBagLayout1);
			fPackageText.setMinimumSize(new Dimension(350, 21));
			fPackageText.setPreferredSize(new Dimension(130, 21));
			fAnnoRefIdLabel.setText("AnnoRef ID:");
			fAnnoRefFileLabel.setText("AnnoRef Selected File:");
			fPackageNameLabel.setText("Package Name:");
			add(fAnnoRefFileLabel, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 15, 0, new Insets(5, 5, 0, 5), 0, 0));
			add(fAnnoRefFileText, new GridBagConstraints(1, 0, 1, 1, 0.0D, 0.0D, 15, 1, new Insets(5, 5, 0, 5), 0, 0));
			add(fFileSelectionButton, new GridBagConstraints(2, 0, 1, 1, 0.0D, 0.0D, 15, 1, new Insets(5, 5, 0, 5), 0, 0));

			add(fPackageNameLabel, new GridBagConstraints(0, 2, 1, 1, 0.0D, 0.0D, 10, 1, new Insets(5, 5, 0, 5), 0, 0));
			add(fPackageText, new GridBagConstraints(1, 2, 1, 1, 1.0D, 0.0D, 10, 2, new Insets(5, 0, 0, 0), 0, 0));
//			add(this.fPackageSelectionButton, new GridBagConstraints(2, 2, 1, 1, 0.0D, 0.0D, 10, 1, new Insets(5, 5, 0, 5), 0, 0));
			add(fAnnoRefIdLabel, new GridBagConstraints(0, 3, 1, 1, 0.0D, 0.0D, 10, 1, new Insets(5, 5, 0, 5), 0, 0));
			add(fAnnoRefIdText, new GridBagConstraints(1, 3, 1, 1, 0.0D, 0.0D, 10, 1, new Insets(5, 5, 0, 5), 0, 0));
		}
	}

}
