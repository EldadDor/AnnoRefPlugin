package com.idi.intellij.plugin.query.annoref.thread;

import com.intellij.openapi.application.ApplicationManager;

public abstract class WriteActionRunner {
	public final void start() {
		new ConditionalLaterInvocator() {
			@Override
			public void execute() {
				Runnable writeAction = new Runnable() {
					@Override
					public void run() {
						WriteActionRunner.this.run();
					}
				};
				ApplicationManager.getApplication().runWriteAction(writeAction);
			}
		}.start();
	}

	public abstract void run();
}
