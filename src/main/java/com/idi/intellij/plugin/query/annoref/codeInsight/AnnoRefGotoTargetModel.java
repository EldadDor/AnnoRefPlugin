/*
 * User: eldad.Dor
 * Date: 30/11/2015 16:16
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.codeInsight;

import com.idi.intellij.plugin.query.annoref.common.AnnoReferencesOptionsEnum;

/**
 * @author eldad
 * @date 30/11/2015
 */
public class AnnoRefGotoTargetModel {
	private final AnnoReferencesOptionsEnum option;
	private String quickDocMessage;

	public AnnoRefGotoTargetModel(AnnoReferencesOptionsEnum option) {
		this.option = option;
	}

	public AnnoReferencesOptionsEnum getOption() {
		return option;
	}

	public String getQuickDocMessage() {
		return quickDocMessage;
	}

	public void setQuickDocMessage(String quickDocMessage) {
		this.quickDocMessage = quickDocMessage;
	}
}