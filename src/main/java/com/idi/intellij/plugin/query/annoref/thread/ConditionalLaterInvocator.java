package com.idi.intellij.plugin.query.annoref.thread;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;

public abstract class ConditionalLaterInvocator extends SynchronizedTask {
	protected ConditionalLaterInvocator() {
		super(null);
	}

	protected ConditionalLaterInvocator(Object syncObject) {
		super(syncObject);
	}

	@Override
	public final void start() {
		Application application = ApplicationManager.getApplication();
		if (application.isDispatchThread()) {
			run();
		} else {
			application.invokeLater(this);
		}
	}
}
