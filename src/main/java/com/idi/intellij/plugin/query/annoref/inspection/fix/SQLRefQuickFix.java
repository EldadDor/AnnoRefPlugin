package com.idi.intellij.plugin.query.annoref.inspection.fix;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiModifierList;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 8/2/13
 * Time: 2:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class SQLRefQuickFix implements LocalQuickFix {


	@NotNull
	@Override
	public String getName() {
		return "Create A @SQLRef and move sql to xml file";
	}

	@NotNull
	@Override
	public String getFamilyName() {
		return "SQLRef conversion";
	}

	@Override
	public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
		PsiElement psiElement = descriptor.getPsiElement();
		final TextRange textRange = psiElement.getTextRange();

		if (psiElement instanceof PsiModifierList) {
			final PsiAnnotation[] annotations = ((PsiModifierList) psiElement).getAnnotations();
			for (PsiAnnotation annotation : annotations) {

			}
			annotations[0].getParameterList().getAttributes()[0].getValue();
		}
		//To change body of implemented methods use File | Settings | File Templates.
	}
}
