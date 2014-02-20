package com.idi.intellij.plugin.query.sqlref.inspection.fix;

import com.idi.intellij.plugin.query.sqlref.action.CreateNewAnnoRefIdInXmlDialog;
import com.intellij.codeInspection.LocalQuickFixBase;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 10/4/13
 * Time: 1:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class CreateNewAnnoRefInFileFix extends LocalQuickFixBase {
	private String annoRefId;
	private String classPackageName;
	private Module classModule;
	private PsiAnnotation annoRefAnnotation;

	protected CreateNewAnnoRefInFileFix(@NotNull String name, @NotNull String familyName) {
		super(name, familyName);
	}

	public CreateNewAnnoRefInFileFix(@NotNull String name, PsiAnnotation annoRefAnnotation, Module classTargetModule, String classPackageName) {
		super(name);
		this.annoRefAnnotation = annoRefAnnotation;
		classModule = classTargetModule;
		this.classPackageName = classPackageName;
	}

	@Override
	public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor) {
		PsiElement psiElement = problemDescriptor.getPsiElement();
		final TextRange textRange = psiElement.getTextRange();

//		if (psiElement instanceof PsiModifierList) {
			/*final PsiAnnotation[] annotations = ((PsiModifierList) psiElement).getAnnotations();
			for (PsiAnnotation annotation : annotations) {*/
		final String moduleFilePath = classModule.getModuleFilePath();
		FileUtil.findFileInProvidedPath(moduleFilePath, "");
		annoRefId = String.valueOf(annoRefAnnotation.getParameterList().getAttributes()[0].getValue());
		final CreateNewAnnoRefIdInXmlDialog annoRefIdInXmlDialog = new CreateNewAnnoRefIdInXmlDialog(project,
				classModule, classPackageName, annoRefId);
//		}
	}
}

