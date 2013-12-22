package com.idi.intellij.plugin.query.sqlref.inspection;

import com.idi.intellij.plugin.query.sqlref.index.SQLRefRepository;
import com.idi.intellij.plugin.query.sqlref.inspection.fix.CreateNewAnnoRefInFileFix;
import com.idi.intellij.plugin.query.sqlref.persist.SQLRefConfigSettings;
import com.idi.intellij.plugin.query.sqlref.util.AnnoRefBundle;
import com.idi.intellij.plugin.query.sqlref.util.SQLRefNamingUtil;
import com.intellij.codeInspection.*;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.*;
import com.intellij.ui.components.JBLabel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 8/2/13
 * Time: 1:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class SQLRefClassInspection extends LocalInspectionTool {
	private static final Logger logger = Logger.getInstance(SQLRefClassInspection.class.getName());

	@NotNull
	@Override
	public String getID() {
		return SQLRefClassInspection.class.getName();
	}

	@Nullable
	@Override
	public String getAlternativeID() {
		return super.getAlternativeID();
	}

	@Nullable
	@Override
	public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
		if (file instanceof PsiJavaFile) {
			logger.info("checkFile(): file=" + file);
			final PsiAnnotation psiAnnotation = SQLRefNamingUtil.getAnnotationForPropitiousClassFile(file, null, SQLRefConfigSettings.getInstance(file.getProject()).getSqlRefState().
					ANNO_ANNOTATION_FQN);
			if (psiAnnotation != null) {

				final Module module = ModuleUtil.findModuleForPsiElement(psiAnnotation.getContainingFile());
				final PsiNameValuePair psiNameValuePair = psiAnnotation.getParameterList().getAttributes()[0];
				ServiceManager.getService(file.getProject(), SQLRefRepository.class).getSQLRefReferenceForID(psiNameValuePair.getValue().getText());
				ProblemDescriptor problemDescriptor = manager.createProblemDescriptor(psiAnnotation.getContext(),
						AnnoRefBundle.message("annoRef.conversion"),
						new CreateNewAnnoRefInFileFix("Create A @SQLRef and move sql to xml file", psiAnnotation, module, ((PsiJavaFile) file).getPackageName()), ProblemHighlightType.INFORMATION, true);
				return new ProblemDescriptor[]{problemDescriptor};
			}
		}
		return super.checkFile(file, manager, isOnTheFly);    //To change body of overridden methods use File | Settings | File Templates.
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
