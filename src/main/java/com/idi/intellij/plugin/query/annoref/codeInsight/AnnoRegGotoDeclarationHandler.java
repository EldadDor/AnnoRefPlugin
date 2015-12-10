/*
 * User: eldad.Dor
 * Date: 11/02/14 18:33
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.codeInsight;

import com.idi.intellij.plugin.query.annoref.common.AnnoRefMockPsiElement;
import com.idi.intellij.plugin.query.annoref.common.AnnoReferencesOptionsEnum;
import com.idi.intellij.plugin.query.annoref.common.SPViewIndexHelper;
import com.idi.intellij.plugin.query.annoref.component.AnnoRefDataKey;
import com.idi.intellij.plugin.query.annoref.component.SPViewContentStateManager;
import com.idi.intellij.plugin.query.annoref.config.SPViewPanelForm;
import com.idi.intellij.plugin.query.annoref.connection.DataSourceAccessorComponent;
import com.idi.intellij.plugin.query.annoref.index.SQLRefRepository;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefConfigSettings;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefSettings;
import com.idi.intellij.plugin.query.annoref.repo.model.SQLRefReference;
import com.idi.intellij.plugin.query.annoref.util.AnnRefApplication;
import com.idi.intellij.plugin.query.annoref.util.SQLRefNamingUtil;
import com.idi.intellij.plugin.query.annoref.util.StringUtils;
import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandlerBase;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlToken;
import com.intellij.sql.dialects.sybase.SybaseDialect;
import com.intellij.sql.psi.SqlFile;
import com.intellij.sql.psi.impl.SqlTokenType;
import com.intellij.ui.content.Content;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;

/**
 * @author eldad
 * @date 11/02/14
 */
public class AnnoRegGotoDeclarationHandler extends GotoDeclarationHandlerBase {

	private static final Logger logger = Logger.getInstance(AnnoRegGotoDeclarationHandler.class.getName());
	private AtomicBoolean isCurrentlyRunning = new AtomicBoolean(false);

	@Nullable
	@Override
	public PsiElement getGotoDeclarationTarget(final PsiElement paramPsiElement, final Editor paramEditor) {
		if (paramPsiElement == null || paramPsiElement instanceof PsiWhiteSpace) {
			return null;
		}
		final PsiFile containingFile = paramPsiElement.getContainingFile();
		if (paramPsiElement instanceof XmlToken && SQLRefNamingUtil.isPropitiousXmlFile(containingFile)) {
			final SQLRefReference sqlRefReference = ServiceManager.getService(paramPsiElement.getProject(), SQLRefRepository.class).getSQLRefReferenceForID(paramPsiElement.getText());
			if (sqlRefReference != null && !(sqlRefReference.getClassSmartPointersElements().isEmpty() && sqlRefReference.getUtilClassSmartPointersElements().isEmpty())) {
				if (!sqlRefReference.getClassSmartPointersElements().isEmpty()) {
					final PsiElement psiElement = sqlRefReference.getClassSmartPointersElements().get(0).getElement();
					final AnnoRefGotoTargetModel targetModel = new AnnoRefGotoTargetModel(AnnoReferencesOptionsEnum.SQLREF_XML);
					if (psiElement != null) {
						psiElement.putUserData(AnnoRefDataKey.GOTO_DECLARATION_ANN_REF, targetModel);
					}
					return psiElement;
				}
				if (!sqlRefReference.getUtilClassSmartPointersElements().isEmpty()) {
					final PsiElement psiElement =((SmartPsiElementPointer) sqlRefReference.getUtilClassSmartPointersElements().values().toArray()[0]).getElement();
					final AnnoRefGotoTargetModel targetModel = new AnnoRefGotoTargetModel(AnnoReferencesOptionsEnum.SQLREF_QUERY);
					psiElement.putUserData(AnnoRefDataKey.GOTO_DECLARATION_ANN_REF, targetModel);
					return psiElement;
				}
			}
		}
		if (containingFile != null && containingFile instanceof PsiJavaFile) {
			if (paramPsiElement instanceof PsiJavaToken && ((PsiJavaToken) paramPsiElement).getTokenType() == JavaTokenType.STRING_LITERAL) {
				final PsiElement spElementParent = getPsiElementParent(paramPsiElement);
				if (spElementParent != null) {
					final PsiAnnotation[] allValidAnnoRefAnnotations = SQLRefNamingUtil.getAllValidAnnoRefAnnotations(spElementParent);
					if (allValidAnnoRefAnnotations.length > 0) {
						for (final PsiAnnotation annotation : allValidAnnoRefAnnotations) {
							final AnnoRefSettings sqlRefState = AnnoRefConfigSettings.getInstance(paramPsiElement.getProject()).getAnnoRefState();
							if (sqlRefState.SP_VIEW_ANNOTATION_FQN.equals(annotation.getQualifiedName())) {
								final AnnoRefGotoTargetModel targetModel = new AnnoRefGotoTargetModel(AnnoReferencesOptionsEnum.SP_ANNO);
								return returnProperPsiElementReference(paramPsiElement, targetModel);
							}
							if (sqlRefState.ANNOREF_ANNOTATION_FQN.equals(annotation.getQualifiedName())) {
								final AnnoRefGotoTargetModel targetModel = new AnnoRefGotoTargetModel(AnnoReferencesOptionsEnum.SQLREF_ANNO);
								final PsiElement psiElement = returnProperPsiElementReference(paramPsiElement, targetModel);
								final SQLRefReference referenceForID = ServiceManager.getService(psiElement.getProject(), SQLRefRepository.class).getSQLRefReferenceForID(StringUtils.cleanQuote(psiElement.getText()));
								final PsiElement xmlPsiElement = referenceForID.getXmlSmartPointersElements().get(0).getElement();
								final String xmlQuickDoc = null;
								if (xmlPsiElement != null) {
									xmlQuickDoc = "[ xml queries file = " + xmlPsiElement.getContainingFile().getName().trim() + " ]";
								}
								targetModel.setQuickDocMessage(xmlQuickDoc);
								xmlPsiElement.putUserData(AnnoRefDataKey.GOTO_DECLARATION_ANN_REF, targetModel);
								return xmlPsiElement;
							}
//							return paramPsiElement;
						}
					} else {
						final PsiElement firstParent = PsiTreeUtil.findFirstParent(paramPsiElement, new Condition<PsiElement>() {
							@Override public boolean value(PsiElement psiElement) {
								return psiElement instanceof PsiMethodCallExpression;
							}
						});
						final SQLRefReference refReference = SQLRefNamingUtil.isValidMethodCall(firstParent);
						if (refReference != null && !refReference.getXmlSmartPointersElements().isEmpty()) {
							return refReference.getXmlSmartPointersElements().get(0).getElement();
						}
					}
				}
			}
		}
		try {
			if (ApplicationManager.getApplication().isDispatchThread()) {
//				logger.info("getGotoDeclarationTarget(): dispatchThread");
				if ((containingFile != null && containingFile instanceof SqlFile) && paramPsiElement.getNode().getElementType() instanceof SqlTokenType) {
					final Matcher matcher = SPViewPanelForm.SP_EXEC_IN_SP_REGEX.matcher(paramPsiElement.getText());
					if (matcher.find()) {
//						logger.info("getGotoDeclarationTarget(): FOUND");
						if (ToolWindowManager.getInstance(paramPsiElement.getProject()).getActiveToolWindowId() != null) {
							final String selectedContentDisplayName = getSelectedContentDisplayName(paramPsiElement);
							if (paramPsiElement.getText() != null && !paramPsiElement.getText().isEmpty() && !(paramPsiElement.getText().contains("\n") || paramPsiElement.getText().contains("\t"))) {
								if (placeVisualIndicesAndDisplayToolWindow(paramPsiElement, paramEditor, selectedContentDisplayName)) {
//									logger.info("getGotoDeclarationTarget(): returning PsiElement=" + paramPsiElement);
									return JavaPsiFacade.getInstance(paramPsiElement.getProject()).getElementFactory().createExpressionFromText(paramPsiElement.getText(), paramPsiElement);
								}
							}
						}
					} else {
//						logger.info("getGotoDeclarationTarget(): MOCK");
						final Disposable disposable = paramEditor.getUserData(AnnoRefDataKey.SP_VIEW_COMPONENT);
						return new AnnoRefMockPsiElement(disposable, paramEditor.getProject(), paramPsiElement.getText(), paramPsiElement);
					}
				}
			} else if (paramPsiElement.getLanguage().equals(SybaseDialect.INSTANCE.getBaseLanguage()) && paramPsiElement instanceof LeafPsiElement) {
				if (ServiceManager.getService(paramPsiElement.getProject(), DataSourceAccessorComponent.class).getSpNames().containsKey(paramPsiElement.getText())) {
//					logger.info("getGotoDeclarationTarget(): NOT dispatchThread");
//					return paramPsiElement;
				}/* else {
					return null;
				}*/
			}
//			logger.info("getGotoDeclarationTarget():");
		} catch (Throwable e) {
			logger.error("getGotoDeclarationTarget(): error=" + e.getMessage());
		}
		return null;
	}

	private PsiElement getPsiElementParent(PsiElement paramPsiElement) {
		return PsiTreeUtil.findFirstParent(paramPsiElement, new Condition<PsiElement>() {
			@Override public boolean value(PsiElement psiElement) {
				if (psiElement instanceof PsiModifierListOwner) {
					return true;
				}
				return false;
			}
		});
	}

	private PsiElement returnProperPsiElementReference(final PsiElement paramPsiElement, final AnnoRefGotoTargetModel optionsEnum) {
		return UIUtil.invokeAndWaitIfNeeded(new Computable<PsiElement>() {
			@Override public PsiElement compute() {
				paramPsiElement.putUserData(AnnoRefDataKey.GOTO_DECLARATION_ANN_REF, optionsEnum);
				return paramPsiElement;
			}
		});
	}

	private boolean placeVisualIndicesAndDisplayToolWindow(PsiElement paramPsiElement, Editor paramEditor, String selectedContentDisplayName) {
		final SPViewIndexHelper indexHelper = AnnRefApplication.getInstance(paramPsiElement.getProject(), SQLRefRepository.class).getSPViewIndexByName(selectedContentDisplayName);
		if (indexHelper != null && !indexHelper.getIndices().isEmpty()) {
			final List<VisualPosition> indices = indexHelper.getIndices();
			final VisualPosition visualPosition = paramEditor.offsetToVisualPosition(paramPsiElement.getTextRange().getStartOffset());
			for (final VisualPosition position : indices) {
				if (position.getLine() == visualPosition.getLine()) {
					displaySPInToolWindow(paramPsiElement.getProject(), paramPsiElement.getText(), false);
					return true;
				}
			}
		}
		return false;
	}

	private String getSelectedContentDisplayName(PsiElement paramPsiElement) {
		final String[] activeToolWindowId = new String[1];
		activeToolWindowId[0] = ToolWindowManager.getInstance(paramPsiElement.getProject()).getActiveToolWindowId();
		final ToolWindow[] toolWindow = new ToolWindow[1];
		toolWindow[0] = ToolWindowManager.getInstance(paramPsiElement.getProject()).getToolWindow(activeToolWindowId[0]);
		final Content[] selectedContent = new Content[1];
		selectedContent[0] = toolWindow[0].getContentManager().getSelectedContent();
		return selectedContent[0].getDisplayName();
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
		PsiAnnotation psiAnnotation = SQLRefNamingUtil.getAnnotationForConfiguredClassFile(psiFile, sqlRefState.SP_VIEW_ANNOTATION_FQN);
		if (psiAnnotation != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("displayStorageProcedureText(): psiAnnotation=" + psiAnnotation.getQualifiedName());
			}
			final PsiNameValuePair psiNameValuePair = psiAnnotation.getParameterList().getAttributes()[0];
			final String spName = psiNameValuePair.getValue().getText();
			final String cleanSpName = StringUtils.cleanQuote(spName);
			if (isCurrentlyRunning.compareAndSet(false, true)) {
				displaySPInToolWindow(project, cleanSpName, false);
			}
		}
	}

	private void displaySPInToolWindow(Project project, final String cleanSpName, boolean dispatchThread) {
		try {
			isCurrentlyRunning.compareAndSet(true, false);
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