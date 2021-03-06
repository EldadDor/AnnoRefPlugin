/*
 * User: eldad.Dor
 * Date: 03/01/2015 20:12
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.task;

/**
 * @author eldad
 * @date 03/01/2015
 */
public class IDIProcessCancelledException extends RuntimeException {

	public IDIProcessCancelledException(Throwable cause) {
		super(cause);
	}

	public IDIProcessCancelledException(String message, Throwable cause) {
		super(message, cause);
	}

	public IDIProcessCancelledException(String message) {
		super(message);
	}

	public IDIProcessCancelledException() {
	}
}