/*
 * User: eldad.Dor
 * Date: 11/02/14 18:33
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.codeInsight;

import com.idi.intellij.plugin.query.annoref.common.SPViewIndexHelper;
import com.idi.intellij.plugin.query.annoref.component.SPViewContentStateManager;
import com.idi.intellij.plugin.query.annoref.index.SQLRefRepository;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefConfigSettings;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefSettings;
import com.idi.intellij.plugin.query.annoref.util.SQLRefApplication;
import com.idi.intellij.plugin.query.annoref.util.SQLRefNamingUtil;
import com.idi.intellij.plugin.query.annoref.util.StringUtils;
import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandlerBase;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.sql.psi.SqlFile;
import com.intellij.sql.psi.impl.SqlTokenType;
import com.intellij.ui.content.Content;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author eldad
 * @date 11/02/14
 */
public class StoreProcExecViewHandler extends GotoDeclarationHandlerBase {

	private static final Logger logger = Logger.getInstance(StoreProcExecViewHandler.class.getName());
	private AtomicBoolean isCurrentlyRunning = new AtomicBoolean(false);

	@Nullable
	@Override
	public PsiElement getGotoDeclarationTarget(final PsiElement paramPsiElement, Editor paramEditor) {
		if (paramPsiElement == null || paramPsiElement instanceof PsiWhiteSpace) {
			return null;
		}
		final PsiFile containingFile = paramPsiElement.getContainingFile();
		if (containingFile != null && containingFile instanceof PsiJavaFile) {
			final PsiElement firstParent = PsiTreeUtil.findFirstParent(paramPsiElement, new Condition<PsiElement>() {
				@Override
				public boolean value(PsiElement psiElement) {
					return psiElement instanceof PsiModifierListOwner;
				}
			});
			if (paramPsiElement instanceof PsiJavaToken && ((PsiJavaToken) paramPsiElement).getTokenType() == JavaTokenType.STRING_LITERAL
					&& ((PsiModifierListOwner) firstParent).getModifierList().getAnnotations() != null
					&& ((PsiModifierListOwner) firstParent).getModifierList().getAnnotations().length > 0) {
				final PsiAnnotation[] annotations = ((PsiModifierListOwner) firstParent).getModifierList().getAnnotations();
				if (annotations != null && annotations.length > 0) {
					for (final PsiAnnotation annotation : annotations) {
						final AnnoRefSettings sqlRefState = AnnoRefConfigSettings.getInstance(paramPsiElement.getProject()).getAnnoRefState();
						if (annotation.getQualifiedName().equals(sqlRefState.SP_VIEW_ANNOTATION_FQN)) {
							if (annotation.getParameterList().getAttributes().length > 0) {
								SPAnnoRefHighlighter spAnnoRefHighlighter = null;
								if (sqlRefState.ANNO_ALL_SYNTAX_HIGHLIGHT_ENABLE) {
									spAnnoRefHighlighter = new SPAnnoRefHighlighter(paramEditor, sqlRefState.ANNO_ALL_SYNTAX_HIGHLIGHT_ENABLE);
								}
								final SPAnnoRefHighlighter finalSpAnnoRefHighlighter = spAnnoRefHighlighter;
								UIUtil.invokeAndWaitIfNeeded(new Runnable() {
									@Override
									public void run() {
										displayStorageProcedureText(containingFile, paramPsiElement.getProject(), finalSpAnnoRefHighlighter);
									}
								});
								return null;
							}
						}
					}
				}
			}
			return null;
		}
		try {
			if (ApplicationManager.getApplication().isDispatchThread()) {
				if (paramPsiElement.getNode().getElementType() instanceof SqlTokenType && (containingFile != null && containingFile instanceof SqlFile)) {
					if (ToolWindowManager.getInstance(paramPsiElement.getProject()).getActiveToolWindowId() != null) {
						final String[] activeToolWindowId = new String[1];
						activeToolWindowId[0] = ToolWindowManager.getInstance(paramPsiElement.getProject()).getActiveToolWindowId();
						final ToolWindow[] toolWindow = new ToolWindow[1];
						toolWindow[0] = ToolWindowManager.getInstance(paramPsiElement.getProject()).getToolWindow(activeToolWindowId[0]);
						final Content[] selectedContent = new Content[1];
						selectedContent[0] = toolWindow[0].getContentManager().getSelectedContent();
						String selectedContentDisplayName = null;
						if (selectedContent[0] != null) {
							selectedContentDisplayName = selectedContent[0].getDisplayName();
						}
						if (paramPsiElement.getText() != null && !paramPsiElement.getText().isEmpty() && !(paramPsiElement.getText().contains("\n") || paramPsiElement.getText().contains("\t"))) {
							final SPViewIndexHelper indexHelper = SQLRefApplication.getInstance(paramPsiElement.getProject(), SQLRefRepository.class).getSPViewIndexByName(selectedContentDisplayName);
							if (indexHelper != null && !indexHelper.getIndices().isEmpty()) {
								final List<VisualPosition> indices = indexHelper.getIndices();
								final VisualPosition visualPosition = paramEditor.offsetToVisualPosition(paramPsiElement.getTextRange().getStartOffset());
								for (final VisualPosition position : indices) {
									if (position.getLine() == visualPosition.getLine()) {
										displaySPInToolWindow(paramPsiElement.getProject(), paramPsiElement.getText(), false);
										return null;
									}
								}
							}
						}
					}
				}
			}
		} catch (Throwable e) {
			logger.error("getGotoDeclarationTarget(): error=" + e.getMessage());

		}
		return null;
	}

	@Nullable
	@Override
	public String getActionText(DataContext paramDataContext) {
		return null;
	}


	private void displayStorageProcedureText(PsiFile psiFile, final Project project, SPAnnoRefHighlighter spAnnoRefHighlighter) {
		final AnnoRefSettings sqlRefState = AnnoRefConfigSettings.getInstance(project).getAnnoRefState();
		if (logger.isDebugEnabled()) {
			logger.debug("displayStorageProcedureText(): PsiFile=" + psiFile.getName());
		}
		PsiAnnotation psiAnnotation = SQLRefNamingUtil.getAnnotationForConfiguredClassFile(psiFile, sqlRefState.
				SP_VIEW_ANNOTATION_FQN);
		if (psiAnnotation != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("displayStorageProcedureText(): psiAnnotation=" + psiAnnotation.getQualifiedName());
			}
			final PsiNameValuePair psiNameValuePair = psiAnnotation.getParameterList().getAttributes()[0];
			final String spName = psiNameValuePair.getValue().getText();
			final String cleanSpName = StringUtils.cleanQuote(spName);
			if (spAnnoRefHighlighter != null) {
				spAnnoRefHighlighter.browse(SPAnnoRefHighlighter.BrowseDirection.NEXT, cleanSpName);
			}
			if (isCurrentlyRunning.compareAndSet(false, true)) {
				displaySPInToolWindow(project, cleanSpName, false);
			}
		}
	}

	private void displaySPInToolWindow(Project project, final String cleanSpName, boolean dispatchThread) {
		try {
			final String contentName = cleanSpName + "_" + AnnoRefConfigSettings.getInstance(project).getAnnoRefState().SP_DATA_SOURCE_NAME;
			final SPViewContentStateManager contentStateManager = ServiceManager.getService(project, SPViewContentStateManager.class);
			final Pair<Boolean, Content> contentPair = contentStateManager.fetchSpForContentDisplay(project, cleanSpName, contentName, dispatchThread);
			if (contentPair.getSecond() == null) {
				return;
			}
			UIUtil.invokeAndWaitIfNeeded(new Runnable() {
				@Override
				public void run() {
					if (contentPair.getFirst()) {
						logger.info("run(): reactivating content, spName=" + cleanSpName);
						contentStateManager.reactivateContent(contentPair.getSecond());
					} else {
						logger.info("run(): adding content, spName=" + cleanSpName);
						contentStateManager.addContent(contentPair.second, cleanSpName, contentName);
					}
				}
			});
		} finally {
			isCurrentlyRunning.set(false);
		}
	}

}