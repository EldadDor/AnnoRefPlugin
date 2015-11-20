package com.idi.intellij.plugin.query.annoref.action;

import javax.swing.*;

public class TemplateDescriptor {
	protected String fTemplateName = null;
	protected Icon fIcon = null;

	public TemplateDescriptor() {
	}

	public TemplateDescriptor(String aTemplateName, Icon aIcon) {
		this.fTemplateName = aTemplateName;
		this.fIcon = aIcon;
	}

	public Icon getIcon() {
		return this.fIcon;
	}

	public void setIcon(Icon aIcon) {
		this.fIcon = aIcon;
	}

	public String getTemplateName() {
		return this.fTemplateName;
	}

	public void setTemplateName(String aTemplateName) {
		this.fTemplateName = aTemplateName;
	}
}
