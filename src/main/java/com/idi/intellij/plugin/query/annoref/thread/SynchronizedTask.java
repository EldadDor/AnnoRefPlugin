package com.idi.intellij.plugin.query.annoref.thread;

public abstract class SynchronizedTask extends SimpleTask {
	private final Object syncObject;

	protected SynchronizedTask(Object syncObject) {
		this.syncObject = syncObject;
	}

	@Override
	public void start() {
		run();
	}

	@Override
	public final void run() {
		if (syncObject == null) {
			execute();
		} else {
			synchronized (this.syncObject) {
				execute();
			}
		}
	}

	@Override
	protected abstract void execute();
}