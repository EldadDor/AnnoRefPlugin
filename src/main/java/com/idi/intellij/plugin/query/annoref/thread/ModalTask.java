package com.idi.intellij.plugin.query.annoref.thread;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;

public abstract class ModalTask extends Task.Modal {
	public ModalTask(Project project, String title, boolean canBeCancelled) {
		super(project, title, canBeCancelled);
	}

	public ModalTask(Project project, String title) {
		super(project, title, false);
	}

	public void start() {
		final ProgressManager progressManager = ProgressManager.getInstance();
		Application application = ApplicationManager.getApplication();

		if (application.isDispatchThread()) {
			progressManager.run(this);
		} else {
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					progressManager.run(ModalTask.this);
				}
			};
			application.invokeLater(runnable, ModalityState.NON_MODAL);
		}
	}
}

