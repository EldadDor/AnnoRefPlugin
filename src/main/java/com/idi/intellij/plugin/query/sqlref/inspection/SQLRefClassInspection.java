package com.idi.intellij.plugin.query.sqlref.inspection;

import com.idi.intellij.plugin.query.sqlref.util.SQLRefNamingUtil;
import com.intellij.codeInspection.*;
import com.intellij.idea.LoggerFactory;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 8/2/13
 * Time: 1:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class SQLRefClassInspection extends LocalInspectionTool {
	private final static Logger logger = LoggerFactory.getInstance().getLoggerInstance(SQLRefClassInspection.class.getName());

	@NotNull
	@Override
	public String getID() {
		return SQLRefClassInspection.class.getName();
	}

	@Nullable
	@Override
	public String getAlternativeID() {
		return super.getAlternativeID();    //To change body of overridden methods use File | Settings | File Templates.
	}

	@Override
	public boolean runForWholeFile() {
		return super.runForWholeFile();    //To change body of overridden methods use File | Settings | File Templates.
	}

	@Nullable
	@Override
	public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
		PsiAnnotation psiAnnotation = SQLRefNamingUtil.getPropitiousAnnotationForFile(file);
		ProblemDescriptor problemDescriptor = manager.createProblemDescriptor(psiAnnotation.getContext(),
				"SQLRef conversion", new SQLRefQuickFix(), ProblemHighlightType.INFORMATION, true);

		return new ProblemDescriptor[]{problemDescriptor};
//		return super.checkFile(file, manager, isOnTheFly);    //To change body of overridden methods use File | Settings | File Templates.
	}

	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
		if (logger.isDebugEnabled()) {
			logger.debug("buildVisitor():");
		}
		PsiFile file = session.getFile();
		logger.info("buildVisitor(): file=" + file.getName());
		PsiAnnotation psiAnnotation = SQLRefNamingUtil.getPropitiousAnnotationForFile(file);
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
}
