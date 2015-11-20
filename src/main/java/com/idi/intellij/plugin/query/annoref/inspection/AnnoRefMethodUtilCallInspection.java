/*
 * User: eldad.Dor
 * Date: 15/07/2014 11:34
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.inspection;

import com.idi.intellij.plugin.query.annoref.persist.AnnoRefConfigSettings;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.JavaElementType;
import com.intellij.psi.impl.source.tree.TreeUtil;
import com.intellij.psi.tree.IElementType;
import com.siyeh.ig.InspectionGadgetsPlugin;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author eldad
 * @date 15/07/2014
 */
public class AnnoRefMethodUtilCallInspection extends LocalInspectionTool {
	private static final Logger log = Logger.getInstance(AnnoRefMethodUtilCallInspection.class);
	private Project project;
	@NonNls
	private static final String INSPECTION = "Inspection";

	private String m_shortName = null;
	private final InspectionGadgetsPlugin inspectionGadgetsPlugin = InspectionGadgetsPlugin.getInstance();
	private long timestamp;

	@Pattern("[a-zA-Z_0-9.-]+")
	@NotNull
	@Override
	public String getID() {
		return AnnoRefMethodUtilCallInspection.class.getName();
	}

	@Override
	@NotNull
	public final String getShortName() {
		if (m_shortName == null) {
			final Class<? extends LocalInspectionTool> aClass = getClass();
			final String name = aClass.getName();
			assert name.endsWith(INSPECTION) : "class name must end with 'Inspection' to correctly" + " calculate the short name: " + name;
			m_shortName = name.substring(name.lastIndexOf((int) '.') + 1, name.length() - INSPECTION.length());
		}
		return m_shortName;
	}

	@Nullable
	@Override
	public String getAlternativeID() {
		return super.getAlternativeID();
	}


	@Nullable
	@Override
	public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
		return super.checkFile(file, manager, isOnTheFly);
	}

	@Override
	public boolean runForWholeFile() {
		return true;
	}

	@NotNull
	@Override
	public List<ProblemDescriptor> processFile(@NotNull PsiFile file, @NotNull InspectionManager manager) {
		return super.processFile(file, manager);
	}

	@Nls
	@NotNull
	@Override
	public String getDisplayName() {
		return getID();
	}


	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
		super.buildVisitor(holder, isOnTheFly);
		if (log.isDebugEnabled()) {
			log.debug("buildVisitor():");
		}
		return new AnnoRefMethodUtilUsageVisitor(holder, true);
	}

/*
	@Override
	public void inspectionStarted(@NotNull LocalInspectionToolSession session, boolean isOnTheFly) {
		super.inspectionStarted(session, isOnTheFly);
		inspectionGadgetsPlugin.getTelemetry().setEnabled(true);
		if (inspectionGadgetsPlugin.isTelemetryEnabled()) {
			timestamp = System.currentTimeMillis();
		}
	}

	@Override
	public void inspectionFinished(@NotNull LocalInspectionToolSession session,
	                               @NotNull ProblemsHolder problemsHolder) {
		super.inspectionFinished(session, problemsHolder);
		if (inspectionGadgetsPlugin.isTelemetryEnabled()) {
			if (timestamp < 0L) {
				log.warn("finish reported without corresponding start");
				return;
			}
			final long end = System.currentTimeMillis();
			final String displayName = getDisplayName();
			inspectionGadgetsPlugin.getTelemetry().reportRun(displayName, end - timestamp);
			timestamp = -1L;
		}
	}
*/

	private class AnnoRefMethodUtilUsageVisitor extends JavaElementVisitor {

		private boolean onTheFly;
		private ProblemsHolder holder;

		private AnnoRefMethodUtilUsageVisitor() {
		}

		private AnnoRefMethodUtilUsageVisitor(ProblemsHolder holder, final boolean isOnTheFly) {
			this.holder = holder;
			onTheFly = isOnTheFly;
		}


		@Override
		public void visitField(@NotNull PsiField psiField) {
			if (log.isDebugEnabled()) {
				log.debug("visitField():");
			}
			super.visitField(psiField);
		}

		@Override
		public void visitFile(PsiFile file) {
			addDescriptors(checkFile(file, holder.getManager(), onTheFly));

		}

		private void addDescriptors(final ProblemDescriptor[] descriptors) {
			if (descriptors != null) {
				for (ProblemDescriptor descriptor : descriptors) {
					log.assertTrue(descriptor != null, getClass().getName());
					holder.registerProblem(descriptor);
				}
			}
		}


		@Override
		public void visitMethodCallExpression(PsiMethodCallExpression expression) {
			if (log.isDebugEnabled()) {
				log.debug("visitMethodCallExpression():");
			}
			super.visitMethodCallExpression(expression);
		}

		@Override
		public void visitJavaToken(PsiJavaToken token) {
//			super.visitJavaToken(token);
			if (token.getTokenType().toString().equals("STRING_LITERAL")) {
				final IElementType methodCallExpression = JavaElementType.METHOD_CALL_EXPRESSION;
				final ASTNode parent = TreeUtil.findParent(token.getNode(), methodCallExpression);
				if (parent instanceof PsiMethodCallExpression && ((PsiMethodCallExpression) parent).getMethodExpression() != null
						&& ((PsiMethodCallExpression) parent).getMethodExpression().getQualifierExpression() != null
						&& ((PsiMethodCallExpression) parent).getMethodExpression().getQualifierExpression().getType() != null) {
					final String canonicalText = ((PsiMethodCallExpression) parent).getMethodExpression().getQualifierExpression().getType().getCanonicalText();
					if (AnnoRefConfigSettings.getInstance(token.getProject()).getAnnoRefState().ANNOREF_UTIL_CLASS_FQN.equals(canonicalText)) {
					}
				}
			}
		}

		@Override
		public void visitCallExpression(PsiCallExpression callExpression) {
			if (log.isDebugEnabled()) {
				log.debug("visitCallExpression():");
			}
			super.visitCallExpression(callExpression);
		}

		@Override
		public void visitMethod(PsiMethod method) {
			if (log.isDebugEnabled()) {
				log.debug("visitMethod():");
			}
			super.visitMethod(method);
		}
	}

}