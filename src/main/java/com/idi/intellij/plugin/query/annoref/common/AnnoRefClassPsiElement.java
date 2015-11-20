/*
 * User: eldad.Dor
 * Date: 28/06/2014 23:19
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.common;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author eldad
 * @date 28/06/2014
 */
public class AnnoRefClassPsiElement extends AnnoRefPsiElement {

	public AnnoRefClassPsiElement(PsiElement psiAnnotation) {
		super(psiAnnotation);
	}

	@Override
	public ItemPresentation getPresentation() {
		return new ItemPresentation() {
			@Nullable
			@Override
			public String getPresentableText() {
				return psiElement.getText();
			}

			@Nullable
			@Override
			public String getLocationString() {
				return psiElement.getParent().getText();
			}

			@Nullable
			@Override
			public Icon getIcon(boolean open) {
				return null;
			}
		};
	}
}