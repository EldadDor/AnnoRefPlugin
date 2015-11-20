/*
 * User: eldad.Dor
 * Date: 03/01/2015 20:31
 
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
public interface IndicateProgressListener {
	/**
	 * Maybe redundant since Jetbrains changed the API, making the cancel obsolete
	 */
	void cancel();

	void indicateChange();
}