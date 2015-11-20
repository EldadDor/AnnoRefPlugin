/*
 * User: eldad.Dor
 * Date: 02/07/2014 12:41
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.codeInsight;

import com.google.common.collect.MapMaker;
import com.intellij.ide.util.PsiElementListCellRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.presentation.java.SymbolPresentationUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

/**
 * @author eldad
 * @date 02/07/2014
 */
public class AnnoRefPsiElementCellRenderer extends PsiElementListCellRenderer {

	private final Icon cellIcon;
	private final Map<PsiElement, Icon> cellIcons = new MapMaker().makeMap();

	public AnnoRefPsiElementCellRenderer(Icon cellIcon) {
		this.cellIcon = cellIcon;
	}

	@Override
	public String getElementText(PsiElement paramT) {
		return SymbolPresentationUtil.getSymbolPresentableText(paramT);
	}

	@Nullable
	@Override
	protected String getContainerText(PsiElement paramT, String paramString) {
		return SymbolPresentationUtil.getSymbolContainerText(paramT);
	}

	@Override
	protected int getIconFlags() {
		return 0;
	}

	@Override
	protected Icon getIcon(PsiElement paramPsiElement) {
		return cellIcon;
//		return super.getIcon(paramPsiElement);
	}
}