package com.idi.intellij.plugin.query.annoref.thread;

public abstract class SimpleBackgroundTask extends SynchronizedTask {
	public SimpleBackgroundTask() {
		super(null);
	}

	public SimpleBackgroundTask(Object syncObject) {
		super(syncObject);
	}

	public final void start() {
		Thread thread = new Thread(this);
		thread.setPriority(1);
		thread.start();
	}
}

/* Location:           C:\Config\.IntelliJIdea13\config\plugins\DBNavigator\lib\DBNavigator.jar
 * Qualified Name:     com.dci.intellij.dbn.common.thread.SimpleBackgroundTask
 * JD-Core Version:    0.6.0
 */