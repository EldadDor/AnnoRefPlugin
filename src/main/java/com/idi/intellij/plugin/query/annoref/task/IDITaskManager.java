/*
 * User: eldad.Dor
 * Date: 03/01/2015 20:14
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.task;

import com.google.common.collect.Maps;
import com.idi.intellij.plugin.query.annoref.util.AnnoRefBundle;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.application.impl.LaterInvocator;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.*;
import com.intellij.openapi.progress.util.ProgressWindow;
import com.intellij.openapi.progress.util.ProgressWindowWithNotification;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.util.DisposeAwareRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author eldad
 * @date 03/01/2015
 * This class holds all logic with OpenAPI Threading execution.
 * With ProgressIndicator and running in the EDT is not effective, and some hardcore methods started to throw exceptions when
 * accessing them while in the background (not on EDT)
 * Using the DumbService seems to solve this problem with current 14.1.1 API, but still in some rare occasions there are exceptions
 */
public class IDITaskManager {
	private static final Logger log = Logger.getInstance(IDITaskManager.class.getName());
	private static final BackgroundTaskQueue myUpdatingQueue = new BackgroundTaskQueue(null, AnnoRefBundle.message("annoRef.progress.reindex"));
	private static final Map<String, IndicateProgressListener> runningTasksMap = Maps.newConcurrentMap();

	public interface IDITaskHandler {
		void waitFor();
	}

	public static boolean isTaskActive(String task) {
		return (runningTasksMap.containsKey(task));
	}

	public static void clearRunningTask(String task) {
		runningTasksMap.remove(task);
	}

	/*public static void run(final Project project, String title, final IDITask task) throws IDIProcessCancelledException {
		final Exception[] canceledEx = new Exception[1];
		final RuntimeException[] runtimeEx = new RuntimeException[1];
		final Error[] errorEx = new Error[1];
		final IDITask[] dumbIDITask = new IDITask[1];
		final Task.Modal task1 = new Task.Modal(project, title, true) {
			@Override
			public void run(@NotNull final ProgressIndicator i) {
				try {
					dumbIDITask[0] = DumbService.getInstance(project).tryRunReadActionInSmartMode(new Computable<IDITask>() {
						@Override
						public IDITask compute() {
							final IDIProgressIndicator progressListener = new IDIProgressIndicator(i, task.numOfFiles());
							task.setProgressListener(progressListener);
							try {
								task.run(progressListener);
							} catch (IDIProcessCancelledException e) {
								return null;
							}
							return task;
						}
					}, "Task=" + task.getTaskName() + " couldn't run in SmartMode, project is still dumb");
				} catch (ProcessCanceledException e) {
					canceledEx[0] = e;
				} catch (RuntimeException e) {
					runtimeEx[0] = e;
				} catch (Error e) {
					errorEx[0] = e;
				}
			}
		};
		if (dumbIDITask[0] != null) {
			ProgressManager.getInstance().run(task1);
		}
		if (canceledEx[0] instanceof IDIProcessCancelledException) {
			throw (IDIProcessCancelledException) canceledEx[0];
		}
		if (canceledEx[0] instanceof ProcessCanceledException) {
			throw new IDIProcessCancelledException();
		}

		if (runtimeEx[0] != null) {
			throw runtimeEx[0];
		}
		if (errorEx[0] != null) {
			throw errorEx[0];
		}
	}*/


	public static Boolean runCompute(final Project project, final String title, final IDITask task) throws IDIProcessCancelledException {
		log.info("runCompute(): task=" + task.getTaskName());
//		final Task.Modal task1 = new Task.Modal(project, title, true) {
//			@Override
//			public void run(@NotNull final ProgressIndicator i) {
//		final AnnoRefSimpleBackgroundTask backgroundTask = new AnnoRefSimpleBackgroundTask(project, title, true, PerformInBackgroundOption.ALWAYS_BACKGROUND, task);


//		final ProgressIndicator indicator = new IDIProgressIndicator().getIndicator();
//		final ProgressIndicator indicator = new ProgressWindowWithNotification(true, true, project, title);
//		final IDIProgressIndicator idiProgressIndicator = new IDIProgressIndicator(indicator, task.numOfFiles());
	/*	final Computable<Boolean> computable = new Computable<Boolean>() {
			@Override
			public Boolean compute() {
				final IDIProgressIndicator progressListener = new IDIProgressIndicator(indicator, task.numOfFiles());
				task.setProgressListener(progressListener);
				boolean result = false;
				try {
					result = task.runComputableTask(progressListener);
				} catch (IDIProcessCancelledException e) {
					log.error("run(): IDIProcessCancelledException=" + e);
				}
				return result;
			}
		};*/

//		ProgressManager.getInstance().runProcessWithProgressAsynchronously(backgroundTask, indicator);
		final Task.Backgroundable task1 = new Task.Backgroundable(project, title, true, PerformInBackgroundOption.DEAF) {
			@Override
			public void run(@NotNull ProgressIndicator progressIndicator) {
				final IDIProgressIndicator idiProgressIndicator = new IDIProgressIndicator(progressIndicator, task.numOfFiles());
				runningTasksMap.put(task.getTaskName(), idiProgressIndicator);
				DumbService.getInstance(project).runReadActionInSmartMode(new Runnable() {
					@Override
					public void run() {
						try {
							log.info("run(): task=" + task.getTaskName());
							task.run(idiProgressIndicator);
						} catch (IDIProcessCancelledException e) {
							log.error(e);
						}
					}
				});
			}
		};
		if (!myUpdatingQueue.isTestMode()) {
			myUpdatingQueue.run(task1);
		}
//		final BackgroundableProcessIndicator backgroundableProcessIndicator = new BackgroundableProcessIndicator(task1);

	/*	final Task.Modal task1 = new Task.Modal(project, title, true) {
					@Override
					public void run(@NotNull final ProgressIndicator i) {
						DumbService.getInstance(project).runReadActionInSmartMode(new Runnable() {
							@Override
							public void run() {
								final IDIProgressIndicator progressListener = new IDIProgressIndicator(i, task.numOfFiles());
		//						final IDIProgressIndicator progressListener = new IDIProgressIndicator(i, task.numOfFiles());
								task.setProgressListener(progressListener);
								try {
									task.run(progressListener);
								} catch (IDIProcessCancelledException e) {
									log.error("run(): IDIProcessCancelledException=" + e);
								}
							}
						});
					}
				};
		*/
//		if (ApplicationManager.getApplication().isDispatchThread()) {
//
//		} else {

	/*	final DumbService dumbService = DumbService.getInstance(project);
		log.info("runCompute(): IDITask=" + task + " IsDumb=" + dumbService.isDumb());
		if (dumbService.isDumb()) {
//			final ProgressWindow indicator = new ProgressWindowWithNotification(true, true, project, title);
			dumbService.smartInvokeLater(new Runnable() {
				@Override
				public void run() {
//							task.run(backgroundableProcessIndicator);
					task.runTask();
				}
			});
		} else {
			log.info("runCompute(): running Smart");
			ProgressManager.getInstance().run(task1);
		}
		*//*	ProgressManager.getInstance().runProcess(new Runnable() {
				@Override
				public void run() {
					task.run(backgroundableProcessIndicator);
				}
			}, backgroundableProcessIndicator);
		}*//*
		*//*ApplicationManager.getApplication().invokeAndWait(new Runnable() {
			@Override
			public void run() {
				try {
					task.run(idiProgressIndicator);
				} catch (IDIProcessCancelledException e) {
					log.error("e=" + e.getMessage(), e);
				}
			}
		}, ModalityState.defaultModalityState());*//*
		*//*ProgressManager.getInstance().executeProcessUnderProgress(new Runnable() {
			@Override
			public void run() {
				try {

				} catch (IDIProcessCancelledException e) {
					log.error("e=" + e.getMessage(), e);
				}
			}
		}, indicator);*//*
//		return backgroundTask.is();*/
		return true;
//		return DumbService.getInstance(project).runReadActionInSmartMode(computable);
//			}
//		};

//		return ProgressManager.getInstance().runProcess(computable, indicator);
	}

	public static void run(final Project project, final String title, final IDITask task) throws IDIProcessCancelledException {
		final DumbService dumbService = DumbService.getInstance(project);
		log.info("run(): IDITask=" + task + " IsDumb=" + dumbService.isDumb());
		final Task.Modal task1 = new Task.Modal(project, title, true) {
			@Override
			public void run(@NotNull final ProgressIndicator i) {
				if (dumbService.isDumb()) {
					final ProgressWindow indicator = new ProgressWindowWithNotification(true, true, project, title);
					dumbService.smartInvokeLater(new Runnable() {
						@Override
						public void run() {
							task.run(indicator);
						}
					});
				} else {
					dumbService.runReadActionInSmartMode(new Runnable() {
						@Override
						public void run() {
							final IDIProgressIndicator progressListener = new IDIProgressIndicator(i, task.numOfFiles());
//						final IDIProgressIndicator progressListener = new IDIProgressIndicator(i, task.numOfFiles());
							task.setProgressListener(progressListener);
							try {
								task.run(progressListener);
							} catch (IDIProcessCancelledException e) {
								log.error("run(): IDIProcessCancelledException=" + e);
							}
						}
					});
				}
			}
		};
		ProgressManager.getInstance().run(task1);
	}


	public static IDITaskHandler runInBackground(final Project project, final String title, final boolean cancellable, final IDITask task) {
		final IDIProgressIndicator indicator = new IDIProgressIndicator();

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				if (project.isDisposed()) {
					return;
				}
				try {
					task.run(indicator);
				} catch (IDIProcessCancelledException ignore) {
					indicator.cancel();
				} catch (ProcessCanceledException ignore) {
					indicator.cancel();
				}
			}
		};
		if (isNoBackgroundMode()) {
			runnable.run();
			return new IDITaskHandler() {
				@Override
				public void waitFor() {

				}
			};
		} else {
			final Future<?> future = ApplicationManager.getApplication().executeOnPooledThread(runnable);
			final IDITaskHandler handler = new IDITaskHandler() {
				@Override
				public void waitFor() {
					try {
						future.get();
					} catch (InterruptedException e) {
						log.error(e);
					} catch (ExecutionException e) {
						log.error(e);
					}
				}
			};
			invokeLater(project, new Runnable() {
				@Override
				public void run() {
					if (future.isDone()) {
						return;
					}
					new Task.Backgroundable(project, title, cancellable) {
						@Override
						public void run(@NotNull ProgressIndicator i) {
							indicator.setIndicator(i);
							handler.waitFor();
						}
					}.queue();
				}
			});
			return handler;
		}
	}


	public static void invokeLater(Project p, Runnable r) {
		invokeLater(p, ModalityState.defaultModalityState(), r);
	}

	public static void invokeLater(final Project p, final ModalityState state, final Runnable r) {
		if (isNoBackgroundMode()) {
			r.run();
		} else {
			ApplicationManager.getApplication().invokeLater(DisposeAwareRunnable.create(r, p), state);
		}
	}

	public static void invokeAndWait(Project p, Runnable r) {
		invokeAndWait(p, ModalityState.defaultModalityState(), r);
	}

	public static void invokeAndWait(final Project p, final ModalityState state, final Runnable r) {
		if (isNoBackgroundMode()) {
			r.run();
		} else {
			if (ApplicationManager.getApplication().isDispatchThread()) {
				r.run();
			} else {
				ApplicationManager.getApplication().invokeAndWait(DisposeAwareRunnable.create(r, p), state);
			}
		}
	}

	public static void invokeAndWaitWriteAction(Project p, final Runnable r) {
		invokeAndWait(p, new Runnable() {
			@Override
			public void run() {
				ApplicationManager.getApplication().runWriteAction(r);
			}
		});
	}

	public static void runDumbAware(final Project project, final Runnable r) {
		if (DumbService.isDumbAware(r)) {
			r.run();
		} else {
			DumbService.getInstance(project).runWhenSmart(DisposeAwareRunnable.create(r, project));
		}
	}

	public static void runWhenInitialized(final Project project, final Runnable r) {
		if (project.isDisposed()) {
			return;
		}

		if (isNoBackgroundMode()) {
			r.run();
			return;
		}

		if (!project.isInitialized()) {
			StartupManager.getInstance(project).registerPostStartupActivity(DisposeAwareRunnable.create(r, project));
			return;
		}

		runDumbAware(project, r);
	}

	public static boolean isNoBackgroundMode() {
		return (ApplicationManager.getApplication().isUnitTestMode()
				|| ApplicationManager.getApplication().isHeadlessEnvironment());
	}

	public static boolean isInModalContext() {
		if (isNoBackgroundMode()) return false;
		return LaterInvocator.isInModalContext();
	}


//	private static class IDIComputabelTask implements Comparable<>

	/*
	public void runTask(Project projectOrNull) {
		myUpdatingQueue.run(new Task.Backgroundable(projectOrNull, AnnoRefBundle.message("annoRef.progress.reindex"), true) {
			public void run(@NotNull ProgressIndicator indicator) {
				try {
					doUpdateIndices(projectOrNull, toSchedule, fullUpdate, new IDIProgressIndicator(indicator));
				} catch (IDIProcessCancelledException ignore) {
				}
			}
		});
	}*/
}