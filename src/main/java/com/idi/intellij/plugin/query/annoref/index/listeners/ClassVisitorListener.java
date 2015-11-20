package com.idi.intellij.plugin.query.annoref.index.listeners;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 8/3/13
 * Time: 5:02 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ClassVisitorListener {
	void foundValidAnnotation(PsiElement classRef);

	void foundValidMethodProperty(Map<String, Map<String, PsiMethod>> methodsPropertiesMap);
}
