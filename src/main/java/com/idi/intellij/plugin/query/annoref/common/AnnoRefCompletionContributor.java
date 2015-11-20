package com.idi.intellij.plugin.query.annoref.common;

import com.idi.intellij.plugin.query.annoref.index.SQLRefRepository;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefConfigSettings;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Created by EAD-MASTER on 7/17/2014.
 */
public class AnnoRefCompletionContributor extends CompletionContributor {

	public AnnoRefCompletionContributor() {
		extend(CompletionType.BASIC, PlatformPatterns.psiElement(), new AnnoRefCompletionProvider());
		extend(null, PlatformPatterns.psiElement(), new CompletionProvider<CompletionParameters>() {
			@Override
			protected void addCompletions(@NotNull CompletionParameters parameters,
			                              ProcessingContext context,
			                              @NotNull CompletionResultSet result) {
				doAdd(parameters, result);
			}
		});
	}

	private static void doAdd(CompletionParameters parameters, final CompletionResultSet result) {
		PsiElement position = parameters.getPosition();
		PsiReference[] references = ArrayUtil.mergeArrays(position.getReferences(), position.getParent().getReferences());
	}

	@Override
	public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
		final PsiExpressionList parentOfType = PsiTreeUtil.getParentOfType(parameters.getPosition(), PsiExpressionList.class);
		if (parentOfType != null) {
			if (isUtilClassMethodCall(parentOfType)) {
				final PsiElement[] children = parentOfType.getChildren();
				if (!(children[0] instanceof PsiJavaToken && ((ASTNode) children[0]).getElementType() == JavaTokenType.LPARENTH)) {
					return;
				}
				if (!(children[2] instanceof PsiJavaToken && ((ASTNode) children[2]).getElementType() == JavaTokenType.RPARENTH)) {
					return;
				}
				final PsiExpression child = (PsiExpression) children[1];
				if ((children[1] instanceof PsiLiteralExpression && child.getType() instanceof PsiClassReferenceType)) {
					if (child.getType() != null && ((PsiClassType) child.getType()).getClassName().equals("String")) {
						PsiFile psiFile = parameters.getOriginalFile();
						Project project = psiFile.getProject();
						addVariants(ServiceManager.getService(project, SQLRefRepository.class).getAllReferencesIDs(), result);
					}
				}
			}
		}




	/*	String text = psiFile.getText();
		int offset = parameters.getOffset();
		int braceOffset = findOpenBrace(text, offset);
		if (braceOffset == -1) return;

		TextRange range = TextRange.create(braceOffset, offset);
		String prefix = range.substring(text);*/
//		addVariants(Arrays.asList(ref.getVariants()), result.withPrefixMatcher(prefix));
	}

	private boolean isUtilClassMethodCall(PsiExpressionList parentOfType) {
		if (parentOfType.getParent() instanceof PsiMethodCallExpression) {
			final PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) parentOfType.getParent();
			if (methodCallExpression.getMethodExpression().getQualifierExpression() != null && methodCallExpression.getMethodExpression().getQualifierExpression().getType() != null) {
				if ((methodCallExpression.getMethodExpression().getQualifierExpression().getType().getCanonicalText().
						equalsIgnoreCase(AnnoRefConfigSettings.getInstance(parentOfType.getProject()).getAnnoRefState().ANNOREF_UTIL_CLASS_FQN))) {
					return true;
				}
			}
		}
		return false;
	}

	public static void addVariants(Collection<?> variants, CompletionResultSet result) {
		for (Object each : variants) {
			LookupElement e;
			if (each instanceof LookupElement) {
				e = (LookupElement) each;
			} else if (each instanceof String) {
				e = LookupElementBuilder.create((String) each);
			} else if (each instanceof PsiNamedElement) {
				e = LookupElementBuilder.create((PsiNamedElement) each);
			} else {
				e = LookupElementBuilder.create(each, String.valueOf(each));
			}
			result.addElement(e);
		}
	}

	private static int findOpenBrace(CharSequence text, int offset) {
		for (int i = offset - 1; i > 0; i--) {
			char c = text.charAt(i);
			if (c == '{' && text.charAt(i - 1) == '$') {
				return i + 1;
			}
			if (!Character.isLetterOrDigit(c) && c != '.') {
				return -1;
			}
		}
		return -1;
	}
}
