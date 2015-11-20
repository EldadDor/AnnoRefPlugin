/*
 * User: eldad.Dor
 * Date: 26/08/13
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.inspection;

import com.idi.intellij.plugin.query.annoref.index.SQLRefRepository;
import com.idi.intellij.plugin.query.annoref.index.listeners.XmlVisitorListener;
import com.idi.intellij.plugin.query.annoref.inspection.fix.CreateAnnoRefClassForUnusedTagFix;
import com.idi.intellij.plugin.query.annoref.inspection.fix.SQLRefMultipleRefIdQuickFix;
import com.idi.intellij.plugin.query.annoref.inspection.fix.SQLToXmlInValidModelFix;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefConfigSettings;
import com.idi.intellij.plugin.query.annoref.repo.model.SQLRefReference;
import com.idi.intellij.plugin.query.annoref.util.AnnoRefBundle;
import com.idi.intellij.plugin.query.annoref.util.SQLRefNamingUtil;
import com.idi.intellij.plugin.query.annoref.util.SQLRefXmlVisitor;
import com.intellij.codeInspection.*;
import com.intellij.ide.util.PackageUtil;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
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
			final Project project = file.getProject();
			final List<ProblemDescriptor> problemDescriptorList = new ArrayList<ProblemDescriptor>();
			if (SQLRefNamingUtil.isMatchFileName(file.getName(), AnnoRefConfigSettings.getInstance(project).getAnnoRefState().QUERIES_REGEX)) {
				XmlVisitorListener xmlVisitorListener = new XmlVisitorListener() {
					@Override
					public void foundValidRefId(String refID, PsiElement xmlAttributeElement) {
						SQLRefReference sqlRefReferenceForID = ServiceManager.getService(project, SQLRefRepository.class).getSQLRefReferenceForID(refID);
						if (sqlRefReferenceForID != null) {
							addProblemDescriptorForMultipleTags(xmlAttributeElement, sqlRefReferenceForID, manager, problemDescriptorList);
							addProblemDescriptorForInValidVoModel(xmlAttributeElement, sqlRefReferenceForID, manager, problemDescriptorList);
							addProblemDescriptorForUnUsedTag(xmlAttributeElement, sqlRefReferenceForID, manager, problemDescriptorList);
						} else {
							logger.warn("foundValidRefId=" + refID + " didn't find SQLRefReference for it!");
						}
					}
				};
				SQLRefXmlVisitor.getInstance(file.getProject()).setXmlVisitorListener(xmlVisitorListener).setInspector(true);
				SQLRefXmlVisitor.getInstance(file.getProject()).visitFile(file);
				return problemDescriptorList.toArray(new ProblemDescriptor[problemDescriptorList.size()]);
			}
		}
		return super.checkFile(file, manager, isOnTheFly);
	}

	private void addProblemDescriptorForMultipleTags(PsiElement xmlAttributeElement, SQLRefReference sqlRefReferenceForID, InspectionManager manager, List<ProblemDescriptor> problemDescriptorList) {
		if (sqlRefReferenceForID.getClassAnnoElements().size() > 1) {
			ProblemDescriptor problemDescriptor = manager.createProblemDescriptor(xmlAttributeElement.getContext(),
					AnnoRefBundle.message("annoRef.xml.inspection.multi.class"), new SQLRefMultipleRefIdQuickFix(), ProblemHighlightType.GENERIC_ERROR_OR_WARNING, true);
			problemDescriptorList.add(problemDescriptor);
		}
	}

	private void addProblemDescriptorForInValidVoModel(PsiElement xmlAttributeElement, SQLRefReference sqlRefReferenceForID, InspectionManager manager, List<ProblemDescriptor> problemDescriptorList) {
		if (!sqlRefReferenceForID.isVoToXmlValidModel()) {
			ProblemDescriptor problemDescriptor = manager.createProblemDescriptor(xmlAttributeElement.getContext(),
					AnnoRefBundle.message("annoRef.sql.validation.error"), new SQLToXmlInValidModelFix(), ProblemHighlightType.GENERIC_ERROR_OR_WARNING, true);
			problemDescriptorList.add(problemDescriptor);
		}
	}

	private void addProblemDescriptorForUnUsedTag(PsiElement xmlAttributeElement, SQLRefReference sqlRefReferenceForID, InspectionManager manager, List<ProblemDescriptor> problemDescriptorList) {
		if (sqlRefReferenceForID.getClassAnnoElements().isEmpty() && sqlRefReferenceForID.getUtilClassSmartPointersElements().isEmpty()) {
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
			PsiFile file = session.getFile();
			logger.info("buildVisitor(): file=" + file.getName());
		}
		return super.buildVisitor(holder, isOnTheFly, session);
	}

	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
		if (logger.isDebugEnabled()) {
			logger.debug("buildVisitor():");
		}
		return super.buildVisitor(holder, isOnTheFly);
	}

	@Nullable
	@Override
	public PsiNamedElement getProblemElement(PsiElement psiElement) {
		return super.getProblemElement(psiElement);
	}

	@Override
	public void inspectionStarted(LocalInspectionToolSession session, boolean isOnTheFly) {
		super.inspectionStarted(session, isOnTheFly);
	}

	@Nullable
	@Override
	public JComponent createOptionsPanel() {
		final JPanel panel = new JPanel(new BorderLayout(2, 2));
		panel.add(new JBLabel("Still In Testing Phases"), BorderLayout.CENTER);
		return panel;
	}
}