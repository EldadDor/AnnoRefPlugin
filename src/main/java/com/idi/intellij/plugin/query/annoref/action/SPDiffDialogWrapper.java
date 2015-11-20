/*
 * User: eldad.Dor
 * Date: 24/02/2015 11:53
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.action;

import com.intellij.openapi.diff.impl.dir.FrameDialogWrapper;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author eldad
 * @date 24/02/2015
 */
public class SPDiffDialogWrapper extends FrameDialogWrapper {
	private final JComponent diffComponent;
	private final String diffDialogTitle;

	public SPDiffDialogWrapper(JComponent diffComponent, String diffDialogTitle) {
		this.diffComponent = diffComponent;
		this.diffDialogTitle = diffDialogTitle;
	}

	@NotNull
	@Override
	protected JComponent getPanel() {
		return diffComponent;
	}

	@Nullable
	@Override
	protected String getTitle() {
		return diffDialogTitle;
	}

	@Nullable
	@Override
	protected Project getProject() {
		return super.getProject();
	}

	@NotNull
	@Override
	protected Mode getMode() {
		return super.getMode();
	}
}