package com.idi.intellij.plugin.query.sqlref.index.listeners;

import com.intellij.psi.PsiElement;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 8/3/13
 * Time: 5:02 PM
 * To change this template use File | Settings | File Templates.
 */
public interface XmlVisitorListener {
	void foundValidRefId(String refID, PsiElement xmlAttributeElement);
}
