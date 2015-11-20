package com.idi.intellij.plugin.query.annoref.thread;

import com.intellij.openapi.application.ApplicationManager;

public abstract class SimpleLaterInvocator extends SynchronizedTask {
	public SimpleLaterInvocator() {
		super(null);
	}

	public SimpleLaterInvocator(Object syncObject) {
		super(syncObject);
	}

	public void start() {
		ApplicationManager.getApplication().invokeLater(this);
	}
}

/* Location:           C:\Config\.IntelliJIdea13\config\plugins\DBNavigator\lib\DBNavigator.jar
 * Qualified Name:     com.dci.intellij.dbn.common.thread.SimpleLaterInvocator
 * JD-Core Version:    0.6.0
 */