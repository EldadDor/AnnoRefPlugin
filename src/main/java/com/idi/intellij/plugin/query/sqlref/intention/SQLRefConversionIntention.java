package com.idi.intellij.plugin.query.sqlref.intention;

import com.idi.intellij.plugin.query.sqlref.util.SQLRefNamingUtil;
import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.idea.LoggerFactory;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 8/15/13
 * Time: 11:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class SQLRefConversionIntention extends BaseIntentionAction {
	private final static Logger logger = LoggerFactory.getInstance().getLoggerInstance(SQLRefConversionIntention.class.getName());

	@NotNull
	@Override
	public String getFamilyName() {
		return getText();
	}

	@NotNull
	@Override
	public String getText() {
		return "Test SQLRef move";
	}

	@Override
	public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
		final CaretModel caretModel = editor.getCaretModel();
		final int position = caretModel.getOffset();
		PsiElement element = file.findElementAt(position);
		PsiClass psiClass = PsiTreeUtil.getParentOfType(element, PsiClass.class);
		return psiClass != null && (SQLRefNamingUtil.isPropitiousClassElement(psiClass));
	}

	@Override
	public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
		logger.info("Invoke():");
	}
}
