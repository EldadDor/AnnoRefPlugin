package com.dci.intellij.dbn.common.thread;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;

public abstract class ReadActionRunner<T> {
	public final T start() {
		Computable readAction = new Computable() {
			public T compute() {
				return run();
			}
		};
		return (T) ApplicationManager.getApplication().runReadAction(readAction);
	}

	protected abstract T run();
}
