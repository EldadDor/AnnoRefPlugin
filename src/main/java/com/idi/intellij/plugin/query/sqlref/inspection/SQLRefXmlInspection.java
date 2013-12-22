/*
 * User: eldad.Dor
 * Date: 26/08/13
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.sqlref.inspection;

import com.idi.intellij.plugin.query.sqlref.index.SQLRefRepository;
import com.idi.intellij.plugin.query.sqlref.index.listeners.XmlVisitorListener;
import com.idi.intellij.plugin.query.sqlref.inspection.fix.CreateAnnoRefClassForUnusedTagFix;
import com.idi.intellij.plugin.query.sqlref.repo.model.SQLRefReference;
import com.idi.intellij.plugin.query.sqlref.util.AnnoRefBundle;
import com.idi.intellij.plugin.query.sqlref.util.SQLRefNamingUtil;
import com.idi.intellij.plugin.query.sqlref.util.SQLRefXmlVisitor;
import com.intellij.codeInspection.*;
import com.intellij.ide.util.PackageUtil;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlFile;
import com.intellij.ui.components.JBLabel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author eldad
 * @date 26/08/13
 */
public class SQLRefXmlInspection extends LocalInspectionTool {
	private static final Logger logger = Logger.getInstance(SQLRefXmlInspection.class.getName());

	@NotNull
	@Override
	public String getID() {
		return SQLRefXmlInspection.class.getName();
	}

	@Nullable
	@Override
	public String getAlternativeID() {
		return super.getAlternativeID();
	}

	@Nullable
	@Override
	public ProblemDescriptor[] checkFile(@NotNull final PsiFile file, @NotNull final InspectionManager manager, boolean isOnTheFly) {
		if (file instanceof XmlFile) {
			logger.info("checkFile(): file=" + file);
			final List<ProblemDescriptor> problemDescriptorList = new ArrayList<ProblemDescriptor>();
			if (SQLRefNamingUtil.isMatchFileName(file.getName())) {
				XmlVisitorListener xmlVisitorListener = new XmlVisitorListener() {
					@Override
					public void foundValidRefId(String refID, PsiElement xmlAttributeElement) {
						SQLRefReference sqlRefReferenceForID = ServiceManager.getService(file.getProject(), SQLRefRepository.class).getSQLRefReferenceForID(refID);
						addProblemDescriptorForMultipleTags(xmlAttributeElement, sqlRefReferenceForID, manager, problemDescriptorList);
						addProblemDescriptorForUnUsedTag(xmlAttributeElement, sqlRefReferenceForID, manager, problemDescriptorList);
					}
				};

				SQLRefXmlVisitor.getInstance(file.getProject()).setXmlVisitorListener(xmlVisitorListener).setInspector(true);
				SQLRefXmlVisitor.getInstance(file.getProject()).visitFile(file);
				return problemDescriptorList.toArray(new ProblemDescriptor[problemDescriptorList.size()]);
			}
		}
		return super.checkFile(file, manager, isOnTheFly);    //To change body of overridden methods use File | Settings | File Templates.
	}

	private void addProblemDescriptorForMultipleTags(PsiElement xmlAttributeElement, SQLRefReference sqlRefReferenceForID, InspectionManager manager, List<ProblemDescriptor> problemDescriptorList) {
		if (sqlRefReferenceForID.getClassAnnoElements().size() > 1) {
			ProblemDescriptor problemDescriptor = manager.createProblemDescriptor(xmlAttributeElement.getContext(),
					AnnoRefBundle.message("annoRef.xml.inspection.multi.class"), new SQLRefMultipleRefIdQuickFix(), ProblemHighlightType.GENERIC_ERROR_OR_WARNING, true);
			problemDescriptorList.add(problemDescriptor);
		}
	}

	private void addProblemDescriptorForUnUsedTag(PsiElement xmlAttributeElement, SQLRefReference sqlRefReferenceForID, InspectionManager manager, List<ProblemDescriptor> problemDescriptorList) {
		if (sqlRefReferenceForID.getClassAnnoElements().isEmpty()) {
			Module moduleForPsiElement = ModuleUtil.findModuleForPsiElement(xmlAttributeElement.getContainingFile());
			String classTargetName = StringUtil.capitalizeWithJavaBeanConvention(sqlRefReferenceForID.getSqlRefId());
			String packageName = "com.idi";
			PsiDirectory directoryInModule = PackageUtil.findPossiblePackageDirectoryInModule(moduleForPsiElement, packageName);
			ProblemDescriptor problemDescriptor = manager.createProblemDescriptor(xmlAttributeElement.getContext(),
					AnnoRefBundle.message("annoRef.xml.inspection.unused.tag"),
					new CreateAnnoRefClassForUnusedTagFix("Create AnnoRef Class for unused ref", "AnnoRef", packageName, classTargetName, moduleForPsiElement, directoryInModule),
					ProblemHighlightType.LIKE_UNUSED_SYMBOL, true);
			problemDescriptorList.add(problemDescriptor);
		}
	}

	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
		if (logger.isDebugEnabled()) {
			logger.debug("buildVisitor():");
		}
		PsiFile file = session.getFile();
		logger.info("buildVisitor(): file=" + file.getName());
		return super.buildVisitor(holder, isOnTheFly, session);    //To change body of overridden methods use File | Settings | File Templates.
	}

	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
		if (logger.isDebugEnabled()) {
			logger.debug("buildVisitor():");
		}
		return super.buildVisitor(holder, isOnTheFly);    //To change body of overridden methods use File | Settings | File Templates.
	}

	@Nullable
	@Override
	public PsiNamedElement getProblemElement(PsiElement psiElement) {
		return super.getProblemElement(psiElement);    //To change body of overridden methods use File | Settings | File Templates.
	}

	@Override
	public void inspectionStarted(LocalInspectionToolSession session, boolean isOnTheFly) {
		super.inspectionStarted(session, isOnTheFly);    //To change body of overridden methods use File | Settings | File Templates.
	}

	@Nullable
	@Override
	public JComponent createOptionsPanel() {
		final JPanel panel = new JPanel(new BorderLayout(2, 2));
		panel.add(new JBLabel("Still In Testing Phases"), BorderLayout.CENTER);
		return panel;
	}
}