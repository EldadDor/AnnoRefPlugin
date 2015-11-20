/*
 * User: eldad.Dor
 * Date: 13/11/2014 13:03
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.connection;

import com.idi.intellij.plugin.query.annoref.thread.SynchronizedTask;

/**
 * @author eldad
 * @date 13/11/2014
 */

public abstract class SimpleBackgroundTask extends SynchronizedTask {
	protected SimpleBackgroundTask() {
		super(null);
	}

	protected SimpleBackgroundTask(Object syncObject) {
		super(syncObject);
	}

	@Override
	public final void start() {
		Thread thread = new Thread(this);
		thread.setPriority(1);
		thread.start();
	}
}