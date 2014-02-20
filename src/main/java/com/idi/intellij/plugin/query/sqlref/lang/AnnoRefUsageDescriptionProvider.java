/*
 * User: eldad.Dor
 * Date: 16/02/14 12:15
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.sqlref.lang;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.ElementDescriptionLocation;
import com.intellij.psi.ElementDescriptionProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author eldad
 * @date 16/02/14
 */
public class AnnoRefUsageDescriptionProvider implements ElementDescriptionProvider {
	private static final Logger logger = Logger.getInstance(AnnoRefUsageDescriptionProvider.class.getName());

	@Nullable
	@Override
	public String getElementDescription(@NotNull PsiElement psiElement, @NotNull ElementDescriptionLocation elementDescriptionLocation) {
		logger.info("getElementDescription():");
		return null;
	}
}