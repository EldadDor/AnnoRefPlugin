/*
 * User: eldad.Dor
 * Date: 04/02/2015 13:10
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.index.visitors;

import com.intellij.psi.PsiElement;
import com.intellij.psi.search.PsiElementProcessor;

/**
 * @author eldad
 * @date 04/02/2015
 */
public class AnnoRefPsiElementProcessor implements PsiElementProcessor<PsiElement> {


	@Override
	public boolean execute(PsiElement element) {
		return false;
	}
}