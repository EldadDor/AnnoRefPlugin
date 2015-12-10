/*
 * User: eldad.Dor
 * Date: 30/11/2015 15:38
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.codeInsight;

import com.idi.intellij.plugin.query.annoref.component.SPViewContentStateManager;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefConfigSettings;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefSettings;
import com.idi.intellij.plugin.query.annoref.util.SQLRefNamingUtil;
import com.idi.intellij.plugin.query.annoref.util.StringUtils;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.ui.content.Content;
import com.intellij.util.ui.UIUtil;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author eldad
 * @date 30/11/2015
 */
public class SPToolWindowDisplayHelper {
	private static final Logger logger = Logger.getInstance(SPToolWindowDisplayHelper.class.getName());
	private static final AtomicBoolean isCurrentlyRunning = new AtomicBoolean(false);

	public static void displayStorageProcedureText(PsiFile psiFile, final Project project) {
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

	public static void displaySPInToolWindow(Project project, final String cleanSpName, boolean dispatchThread) {
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