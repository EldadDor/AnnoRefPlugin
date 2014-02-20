/*
 * User: eldad.Dor
 * Date: 16/02/14 10:42
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.sqlref.lang;

import com.idi.intellij.plugin.query.sqlref.persist.SQLRefConfigSettings;
import com.idi.intellij.plugin.query.sqlref.persist.SQLRefSettings;
import com.intellij.codeInsight.highlighting.HighlightManager;
import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiModifierListOwner;
import com.siyeh.InspectionGadgetsBundle;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * @author eldad
 * @date 16/02/14
 */
public class AnnoRefHighlightingAnnotator implements Annotator {
	private static final Logger LOGGER = Logger.getInstance(AnnoRefHighlightingAnnotator.class.getName());

	@Override
	public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
		if (psiElement instanceof PsiModifierListOwner && ((PsiModifierListOwner) psiElement).getModifierList().getAnnotations() != null
				&& ((PsiModifierListOwner) psiElement).getModifierList().getAnnotations().length > 0) {
			final PsiAnnotation[] annotations = ((PsiModifierListOwner) psiElement).getModifierList().getAnnotations();
			if (annotations != null && annotations.length > 0) {
				for (final PsiAnnotation annotation : annotations) {
					final SQLRefSettings sqlRefState = SQLRefConfigSettings.getInstance(psiElement.getProject()).getSqlRefState();
					if (annotation.getQualifiedName().equals(sqlRefState.SP_VIEW_ANNOTATION_FQN)) {
						if (annotation.getParameterList().getAttributes().length > 0) {
							final PsiAnnotationMemberValue value = annotation.getParameterList().getAttributes()[0].getValue();
//							final TextAttributesKey ATTRIBUTE_VALUE_KEY = TextAttributesKey.createTextAttributesKey("annoRef.attributeValue", DefaultLanguageHighlighterColors.STRING);
//							ATTRIBUTE_VALUE_KEY.getDefaultAttributes().setForegroundColor(DefaultLanguageHighlighterColors.STRING.getDefaultAttributes().getForegroundColor().darker());
//							annotate(value, ATTRIBUTE_VALUE_KEY, annotationHolder);
//							final ArrayList<Object> list = Lists.newArrayList();
//							list.add(value);
//							highlightElements(list);
						}
					}
				}
			}
		}
	/*	if (psiElement.getNode().getElementType() instanceof SqlTokenType && (psiElement.getContainingFile() != null && psiElement.getContainingFile() instanceof SqlFile)) {
			final SyntaxHighlighter syntaxHighlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(SybaseDialect.INSTANCE, psiElement.getProject(), SQLRefApplication.getVirtualFileFromPsiFile(psiElement.getContainingFile(), psiElement.getProject()));

		}*/
	}

	private static void annotate(PsiElement element, TextAttributesKey key, AnnotationHolder holder) {
		if (element != null) {
			holder.createInfoAnnotation(element, null).setTextAttributes(key);
		}
	}

	private static void highlightElements(@NotNull final ArrayList<Object> elementCollection) {
		if (elementCollection.isEmpty()) {
			return;
		}
		final Application application = ApplicationManager.getApplication();
		application.invokeLater(new Runnable() {
			@Override
			public void run() {
				final PsiElement[] elements = elementCollection.toArray(new PsiElement[elementCollection.size()]);
				final Project project = elements[0].getProject();
				final FileEditorManager editorManager = FileEditorManager.getInstance(project);
				final EditorColorsManager editorColorsManager = EditorColorsManager.getInstance();
				final Editor editor = editorManager.getSelectedTextEditor();
				if (editor == null) {
					return;
				}
				final EditorColorsScheme globalScheme = editorColorsManager.getGlobalScheme();
				final TextAttributes textattributes = globalScheme.getAttributes(EditorColors.SEARCH_RESULT_ATTRIBUTES);
				final HighlightManager highlightManager = HighlightManager.getInstance(project);
				highlightManager.addOccurrenceHighlights(editor, elements, textattributes, true, null);
				final WindowManager windowManager = WindowManager.getInstance();
				final StatusBar statusBar = windowManager.getStatusBar(project);
				statusBar.setInfo(InspectionGadgetsBundle.message("press.escape.to.remove.highlighting.message"));
				final FindManager findmanager = FindManager.getInstance(project);
				FindModel findmodel = findmanager.getFindNextModel();
				if (findmodel == null) {
					findmodel = findmanager.getFindInFileModel();
				}
				findmodel.setSearchHighlighters(true);
				findmanager.setFindWasPerformed();
				findmanager.setFindNextModel(findmodel);
			}
		});
	}

}