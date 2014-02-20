/*
 * User: eldad.Dor
 * Date: 11/02/14 18:33
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.sqlref.codeInsight;

import com.idi.intellij.plugin.query.sqlref.common.SPViewIndexHelper;
import com.idi.intellij.plugin.query.sqlref.component.SPViewContentStateManager;
import com.idi.intellij.plugin.query.sqlref.index.SQLRefRepository;
import com.idi.intellij.plugin.query.sqlref.persist.SQLRefConfigSettings;
import com.idi.intellij.plugin.query.sqlref.persist.SQLRefSettings;
import com.idi.intellij.plugin.query.sqlref.util.SQLRefApplication;
import com.idi.intellij.plugin.query.sqlref.util.SQLRefNamingUtil;
import com.idi.intellij.plugin.query.sqlref.util.StringUtils;
import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandlerBase;
import com.intellij.openapi.actionSystem.DataContext;
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
					if (psiElement instanceof PsiModifierListOwner) {
						return true;
					}
					return false;
				}
			});
			if (paramPsiElement instanceof PsiJavaToken &&
					((PsiJavaToken) paramPsiElement).getTokenType() == JavaTokenType.STRING_LITERAL &&
					((PsiModifierListOwner) firstParent).getModifierList().getAnnotations() != null &&
					((PsiModifierListOwner) firstParent).getModifierList().getAnnotations().length > 0) {
				final PsiAnnotation[] annotations = ((PsiModifierListOwner) firstParent).getModifierList().getAnnotations();
				if (annotations != null && annotations.length > 0) {
					for (final PsiAnnotation annotation : annotations) {
						final SQLRefSettings sqlRefState = SQLRefConfigSettings.getInstance(paramPsiElement.getProject()).getSqlRefState();
						if (annotation.getQualifiedName().equals(sqlRefState.SP_VIEW_ANNOTATION_FQN)) {
							if (annotation.getParameterList().getAttributes().length > 0) {
								final SPAnnoRefHighlighter spAnnoRefHighlighter = new SPAnnoRefHighlighter(paramEditor, true);

								displayStorageProcedureText(containingFile, paramPsiElement.getProject(), spAnnoRefHighlighter);
								return null;
							}
						}
					}
				}
			}
			return null;
		}
		try {
//			if (!ApplicationManager.getApplication().isDispatchThread()) {
			if (paramPsiElement.getNode().getElementType() instanceof SqlTokenType && (containingFile != null && containingFile instanceof SqlFile)) {
				if (ToolWindowManager.getInstance(paramPsiElement.getProject()).getActiveToolWindowId() != null) {
					final String[] activeToolWindowId = new String[1];
					activeToolWindowId[0] = ToolWindowManager.getInstance(paramPsiElement.getProject()).getActiveToolWindowId();
					final ToolWindow[] toolWindow = new ToolWindow[1];
					toolWindow[0] = ToolWindowManager.getInstance(paramPsiElement.getProject()).getToolWindow(activeToolWindowId[0]);
					final Content[] selectedContent = new Content[1];
					selectedContent[0] = toolWindow[0].getContentManager().getSelectedContent();
					final String selectedContentDisplayName = selectedContent[0].getDisplayName();
					if (paramPsiElement.getText() != null && !paramPsiElement.getText().isEmpty() && !(paramPsiElement.getText().contains("\n") || paramPsiElement.getText().contains("\t"))) {
						final SPViewIndexHelper indexHelper = SQLRefApplication.getInstance(paramPsiElement.getProject(), SQLRefRepository.class).getSPViewIndexByName(selectedContentDisplayName);
						if (indexHelper != null && !indexHelper.getIndices().isEmpty()) {
							final List<VisualPosition> indices = indexHelper.getIndices();
							final VisualPosition visualPosition = paramEditor.offsetToVisualPosition(paramPsiElement.getTextRange().getStartOffset());
							for (final VisualPosition position : indices) {
								if (position.getLine() == visualPosition.getLine()) {
									displaySPInToolWindow(paramPsiElement.getProject(), paramPsiElement.getText());
									return null;
								}
							}
						}
					}
//					}
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
		final SQLRefSettings sqlRefState = SQLRefConfigSettings.getInstance(project).getSqlRefState();
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
			spAnnoRefHighlighter.browse(SPAnnoRefHighlighter.BrowseDirection.NEXT, cleanSpName);
			if (isCurrentlyRunning.compareAndSet(false, true)) {
				displaySPInToolWindow(project, cleanSpName);
			}
		}
	}

	private void displaySPInToolWindow(Project project, final String cleanSpName) {
		try {
			isCurrentlyRunning.compareAndSet(true, false);
			final SPViewContentStateManager contentStateManager = ServiceManager.getService(project, SPViewContentStateManager.class);
			final Pair<Boolean, Content> contentPair = contentStateManager.fetchSpForContentDisplay(project, cleanSpName);
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
						contentStateManager.addContent(contentPair.second, cleanSpName);
					}
				}
			});
		} finally {
			isCurrentlyRunning.set(false);
		}
	}
/*
	private void displayStorageProcedureText(PsiFile psiFile, final Project project) {

		final SQLRefSettings sqlRefState = SQLRefConfigSettings.getInstance(project).getSqlRefState();
		logger.info("displayStorageProcedureText(): PsiFile=" + psiFile.getName());
		PsiAnnotation psiAnnotation = SQLRefNamingUtil.getAnnotationForConfiguredClassFile(psiFile, sqlRefState.
				SP_VIEW_ANNOTATION_FQN);
		if (psiAnnotation != null) {
			logger.info("displayStorageProcedureText(): psiAnnotation=" + psiAnnotation.getQualifiedName());
			final PsiNameValuePair psiNameValuePair = psiAnnotation.getParameterList().getAttributes()[0];
			final String spName = psiNameValuePair.getValue().getText();
			final String cleanSpName = StringUtils.cleanQuote(spName);
			final SPViewContentStateManager contentStateManager = ServiceManager.getService(project, SPViewContentStateManager.class);
			if (!activateAlreadyOpenContent(spName, contentStateManager)) {
				UIUtil.invokeAndWaitIfNeeded(new Runnable() {
					@Override
					public void run() {
						final DataSourceAccessorComponent dbAccessor = SQLRefApplication.getInstance(project, DataSourceAccessorComponent.class);
						dbAccessor.initDataSource(project, sqlRefState.SP_DATA_SOURCE_NAME);
						try {
							String spText = dbAccessor.fetchSpForViewing(cleanSpName, project);
							contentStateManager.addContent(getSPViewContent(project, spText), spName);
							logger.info("displayStorageProcedureText(): SP_Name=" + cleanSpName);
						} catch (SQLException e) {
							logger.error("displayStorageProcedureText(): Error=" + e.getMessage(), e);
						}
					}
				});
			}
		}
	}*/
/*
	private boolean activateAlreadyOpenContent(String spName, final SPViewContentStateManager contentStateManager) {
		final Content alreadyOpenContent = contentStateManager.getAlreadyOpenContent(spName);
		if (alreadyOpenContent != null) {
			UIUtil.invokeAndWaitIfNeeded(new Runnable() {
				@Override
				public void run() {
					contentStateManager.reactivateContent(alreadyOpenContent);
				}
			});
			return true;
		}
		return false;
	}*/

/*
	private ToolWindow getRegisteredToolWindow(ToolWindowManager toolWindowManager) {
		ToolWindow toolWindow = toolWindowManager.getToolWindow("SPViewer");
		if (toolWindow == null) {
			toolWindow = toolWindowManager.registerToolWindow("SPViewer", true, ToolWindowAnchor.BOTTOM);
		}
		return toolWindow;
	}*/

	/*private Content getSPViewContent(Project project, String spText) {
		final SPViewPanelForm spPanel = new SPViewPanelForm(project);
		final Content newContent = ContentFactory.SERVICE.getInstance().createContent(spPanel.getMainPanel(), "", false);
		newContent.setIcon(IconLoader.findIcon("icons/syBaseLogo_36.png"));
		spPanel.setContent(newContent);
		spPanel.setTextForViewing(spText);
		newContent.setDisposer(spPanel);
		return newContent;
	}*/

}