/*
 * User: eldad.Dor
 * Date: 12/01/2015 10:44
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.config;

import com.intellij.openapi.project.Project;
import com.intellij.ui.TextFieldWithAutoCompletion;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.Collection;

/**
 * @author eldad
 * @date 12/01/2015
 */
public class SPTTextFieldWithAutoCompletion extends TextFieldWithAutoCompletion {
	private static Project project;
	private SPViewSubmitListener submitListener;

	public SPTTextFieldWithAutoCompletion(Project project, StringsCompletionProvider stringsCompletionProvider, boolean b, String s, SPViewSubmitListener submitListener) {
		super(project, stringsCompletionProvider, b, s);
		this.submitListener = submitListener;
	}


	public static void setProject(Project project) {
		SPTTextFieldWithAutoCompletion.project = project;
	}

	public static TextFieldWithAutoCompletion createWithAutoCompletion(@NotNull Project project, Collection<String> collection, SPViewSubmitListener spViewPanelForm) {
		return new SPTTextFieldWithAutoCompletion(project, new TextFieldWithAutoCompletion.StringsCompletionProvider(collection, null), true, "", spViewPanelForm);

	}


	@Override
	protected boolean processKeyBinding(KeyStroke var1, KeyEvent var2, int var3, boolean var4) {
		if (var2.getKeyChar() == KeyEvent.VK_ENTER || var2.getKeyCode() == KeyEvent.VK_ENTER) {
			submitListener.spSubmitAction();
			return true;
		}
		return false;
	}

}