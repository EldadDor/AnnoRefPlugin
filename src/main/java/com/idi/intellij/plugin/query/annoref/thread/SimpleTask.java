package com.idi.intellij.plugin.query.annoref.thread;

public abstract class SimpleTask
		implements RunnableTask {
	private int option;

	public int getOption() {
		return this.option;
	}

	public void setOption(int option) {
		this.option = option;
	}

	public void start() {
		run();
	}

	public void run() {
		execute();
	}

	protected abstract void execute();
}

/* Location:           C:\Config\.IntelliJIdea13\config\plugins\DBNavigator\lib\DBNavigator.jar
 * Qualified Name:     com.dci.intellij.dbn.common.thread.SimpleTask
 * JD-Core Version:    0.6.0
 */