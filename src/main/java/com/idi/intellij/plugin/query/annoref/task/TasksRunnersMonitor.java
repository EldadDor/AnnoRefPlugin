/*
 * User: eldad.Dor
 * Date: 21/11/2015 00:59
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.task;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author eldad
 * @date 21/11/2015
 */
public class TasksRunnersMonitor {
	private final Map<String, IndicateProgressListener> indicateProgressListenerMap = Maps.newConcurrentMap();

	public void addProgressListener(String type, IndicateProgressListener listener) {
		if (!indicateProgressListenerMap.containsKey(type)) {
			indicateProgressListenerMap.put(type, listener);
		}
	}

	public boolean isProgressListenerActive(String type) {
//		if(indicateProgressListenerMap.containsKey(type) && indicateProgressListenerMap.get(type).indicateChange();)
		return true;

	}
}