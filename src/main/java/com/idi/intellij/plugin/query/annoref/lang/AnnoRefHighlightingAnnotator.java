/*
 * User: eldad.Dor
 * Date: 16/02/14 10:42
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.lang;

import com.google.common.collect.Lists;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefConfigSettings;
import com.idi.intellij.plugin.query.annoref.util.SQLRefNamingUtil;
import com.intellij.codeInsight.highlighting.HighlightManager;
import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.ide.IdeEventQueue;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.*;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.*;
import com.intellij.ui.JBColor;
import com.siyeh.InspectionGadgetsBundle;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

/**
 * @author eldad
 * @date 16/02/14
 * Not really using it but is running...
 */
public class AnnoRefHighlightingAnnotator implements Annotator {
	private static final Logger LOGGER = Logger.getInstance(AnnoRefHighlightingAnnotator.class.getName());

	private static void annotate(PsiElement element, TextAttributesKey key, AnnotationHolder holder) {
		if (element != null) {
			holder.createInfoAnnotation(element, null).setTextAttributes(key);
		}
	}
	/*	if (psiElement.getNode().getElementType() instanceof SqlTokenType && (psiElement.getContainingFile() != null && psiElement.getContainingFile() instanceof SqlFile)) {
			final SyntaxHighlighter syntaxHighlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(SybaseDialect.INSTANCE, psiElement.getProject(), SQLRefApplication.getVirtualFileFromPsiFile(psiElement.getContainingFile(), psiElement.getProject()));

		}*/

	private static void highlightElements(@NotNull final List<PsiElement> elementCollection) {
		if (elementCollection.isEmpty()) {
			return;
		}
		IdeEventQueue.invokeLater(new Runnable() {
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
//				highlightManager.addOccurrenceHighlights(editor, elements, null, true, null);
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

	public static void main(String[] args) {
		int hex = 0x5151255;
		int r = (hex & 0xFF0000) >> 16;
		int g = (hex & 0xFF00) >> 8;
		int b = (hex & 0xFF);
		final String toHexString = org.jdesktop.swingx.color.ColorUtil.toHexString(Color.RED);
		final Color decode = Color.decode("51-51-255");
	}

	@Override
	public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
		final PsiAnnotation psiAnnotation = SQLRefNamingUtil.isAnnoRefAnnotationValid(psiElement);
		if (psiAnnotation != null) {
			for (final PsiNameValuePair nameValuePair : psiAnnotation.getParameterList().getAttributes()) {
//			if (nameValuePair.getName().equals(AnnoRefConfigSettings.getInstance(psiElement.getProject()).getAnnoRefState().ANNOREF_ANNOTATION_ATTRIBUTE_ID)) {
				final PsiAnnotationMemberValue value = nameValuePair.getValue();
				TextAttributesKey ATTRIBUTE_VALUE_KEY = TextAttributesKey.createTextAttributesKey("annoRef.attributeValue", DefaultLanguageHighlighterColors.STRING);
				if (!AnnoRefConfigSettings.getInstance(psiElement.getProject()).getAnnoRefState().ANNO_ALL_SYNTAX_HIGHLIGHT_COLOR.isEmpty()) {
					final Color decode = Color.decode(AnnoRefConfigSettings.getInstance(psiElement.getProject()).getAnnoRefState().ANNO_ALL_SYNTAX_HIGHLIGHT_COLOR);
					JBColor annoSyntaxColor = new JBColor(decode, decode);
					ATTRIBUTE_VALUE_KEY.getDefaultAttributes().setForegroundColor(annoSyntaxColor);
				}
//						final TextAttributesKey ATTRIBUTE_VALUE_KEY = TextAttributesKey.createTextAttributesKey("annoRef.attributeValue", DefaultLanguageHighlighterColors.STRING);
//						ATTRIBUTE_VALUE_KEY.getDefaultAttributes().setForegroundColor(DefaultLanguageHighlighterColors.STRING.getDefaultAttributes().getForegroundColor().darker());
//				final JBColor foregroundColor = new JBColor(new Color(255, 255, 0), new Color(100, 28, 0));
				final ColorKey colorKey = EditorColors.MODIFIED_LINES_COLOR;
//				final JBColor backgroundColor = new JBColor(new Color(71, 143, 36), Gray._0);
//				ATTRIBUTE_VALUE_KEY.getDefaultAttributes().setForegroundColor(foregroundColor);
				annotate(value, ATTRIBUTE_VALUE_KEY, annotationHolder);
				final List<PsiElement> list = Lists.newArrayList();
				list.add(value);
				highlightElements(list);
				return;
//			}
			}
		}
	}

	private boolean isValidElement(PsiElement psiElement) {
		return psiElement instanceof PsiModifierListOwner
				&& ((PsiModifierListOwner) psiElement).getModifierList() != null
				&& ((PsiModifierListOwner) psiElement).getModifierList().getAnnotations() != null
				&& ((PsiModifierListOwner) psiElement).getModifierList().getAnnotations().length > 0;
	}

}