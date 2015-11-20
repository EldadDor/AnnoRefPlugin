package com.idi.intellij.plugin.query.annoref.thread;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BackgroundTask extends Task.Backgroundable
		implements RunnableTask {

	protected ProgressIndicator progressIndicator;

	protected BackgroundTask(@Nullable Project project, @NotNull String title, boolean canBeCancelled, @Nullable PerformInBackgroundOption backgroundOption, ProgressIndicator progressIndicator) {
		super(project, title, canBeCancelled, backgroundOption);
		this.progressIndicator = progressIndicator;
	}

	protected BackgroundTask(@Nullable Project project, @NotNull String title, boolean canBeCancelled, ProgressIndicator progressIndicator) {
		super(project, title, canBeCancelled);
		this.progressIndicator = progressIndicator;
	}

	protected BackgroundTask(@Nullable Project project, @NotNull String title, ProgressIndicator progressIndicator) {
		super(project, title);
		this.progressIndicator = progressIndicator;
	}

	private static PerformInBackgroundOption START_IN_BACKGROUND = new PerformInBackgroundOption() {
		@Override
		public boolean shouldStartInBackground() {
			return true;
		}

		@Override
		public void processSentToBackground() {
		}
	};

	private static PerformInBackgroundOption DO_NOT_START_IN_BACKGROUND = new PerformInBackgroundOption() {
		@Override
		public boolean shouldStartInBackground() {
			return false;
		}

		@Override
		public void processSentToBackground() {
		}
	};

	protected BackgroundTask(@Nullable Project project, @NotNull String title, boolean startInBackground, boolean canBeCancelled) {
		super(project, "AnnoRef SQL - " + title, canBeCancelled, startInBackground ? START_IN_BACKGROUND : DO_NOT_START_IN_BACKGROUND);
	}

	protected BackgroundTask(@Nullable Project project, @NotNull String title, boolean startInBackground) {
		this(project, title, startInBackground, false);
	}

	@Override
	public final void run() {
//		ProgressIndicator progressIndicator = ProgressManager.getInstance().getProgressIndicator();
		run(progressIndicator);
	}

	@Override
	public final void run(@NotNull ProgressIndicator progressIndicator) {
		if (progressIndicator == null) {
			throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null",
					new Object[]{"progressIndicator", " com/idi/intellij/plugin/query/annoref/thread/BackgroundTask", "run"}));
		}
		int priority = Thread.currentThread().getPriority();
		try {
			Thread.currentThread().setPriority(1);
			execute(progressIndicator);
		} catch (InterruptedException e) {
		} catch (Exception e) {
		} finally {
			Thread.currentThread().setPriority(priority);
		}
	}

	protected abstract void execute(@NotNull ProgressIndicator paramProgressIndicator)
			throws InterruptedException;

	@Override
	public final void start() {
		final ProgressManager progressManager = ProgressManager.getInstance();
		final BackgroundTask task = this;
		final Application application = ApplicationManager.getApplication();

//		if (application.isDispatchThread()) {
//			progressManager.run(task);
//		} else {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				progressManager.run(task);
			}
		};
		application.runReadAction(runnable);
//					application.invokeLater(runnable, ModalityState.NON_MODAL);

//		}
	}

	public void initProgressIndicator(ProgressIndicator progressIndicator, boolean indeterminate) {
		initProgressIndicator(progressIndicator, indeterminate, null);
	}

	public void initProgressIndicator(final ProgressIndicator progressIndicator, final boolean indeterminate, @Nullable final String text) {
		new ConditionalLaterInvocator(this) {
			@Override
			public void execute() {
				if (progressIndicator.isRunning()) {
					progressIndicator.setIndeterminate(indeterminate);
					if (text != null) {
						progressIndicator.setText(text);
					}
				}
			}
		}.start();
	}

	public static boolean isProcessCancelled() {
		ProgressIndicator progressIndicator = ProgressManager.getInstance().getProgressIndicator();
		return (progressIndicator != null) && (progressIndicator.isCanceled());
	}
}
