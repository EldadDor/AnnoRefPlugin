package com.idi.intellij.plugin.query.sqlref.inspection;

import com.intellij.codeInsight.CodeInsightBundle;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiDirectory;
import com.intellij.refactoring.move.moveClassesOrPackages.DestinationFolderComboBox;
import com.intellij.refactoring.ui.PackageNameReferenceEditorCombo;
import com.intellij.ui.ReferenceEditorComboWithBrowseButton;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 8/17/13
 * Time: 2:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConvertToAnnoRefDialog extends DialogWrapper {

	private JLabel myInformationLabel = new JLabel("#");
	private JLabel myPackageLabel = new JLabel(CodeInsightBundle.message("dialog.create.class.destination.package.label"));
	private ReferenceEditorComboWithBrowseButton myPackageComponent;
	private JTextField myTfClassName = new MyTextField();
	private Project myProject;
	private PsiDirectory myTargetDirectory;
	private String myClassName;
	private boolean myClassNameEditable;
	private Module myModule;
	private final DestinationFolderComboBox myDestinationCB = new DestinationFolderComboBox() {
		@Override
		public String getTargetPackage() {
			return myPackageComponent.getText().trim();
		}

		@Override
		protected boolean reportBaseInTestSelectionInSource() {
			return ConvertToAnnoRefDialog.this.reportBaseInTestSelectionInSource();
		}

		@Override
		protected boolean reportBaseInSourceSelectionInTest() {
			return ConvertToAnnoRefDialog.this.reportBaseInSourceSelectionInTest();
		}
	};

	@NonNls
	private static final String RECENTS_KEY = "AnnoRefCreateClassDialog.RecentsKey";

	public ConvertToAnnoRefDialog(Project project, boolean canBeParent, String targetPackageName) {
		super(project, canBeParent);
		myPackageComponent = new PackageNameReferenceEditorCombo(targetPackageName, myProject, RECENTS_KEY, CodeInsightBundle.message("dialog.create.class.package.chooser.title"));
	}

	@Nullable
	@Override
	protected JComponent createCenterPanel() {
		return new JPanel(new BorderLayout());
	}

	private static class MyTextField extends JTextField {
		@NotNull
		@Override
		public Dimension getPreferredSize() {
			Dimension size = super.getPreferredSize();
			FontMetrics fontMetrics = getFontMetrics(getFont());
			size.width = fontMetrics.charWidth('a') * 40;
			return size;
		}
	}

	protected boolean reportBaseInTestSelectionInSource() {
		return false;
	}

	protected boolean reportBaseInSourceSelectionInTest() {
		return false;
	}

}
