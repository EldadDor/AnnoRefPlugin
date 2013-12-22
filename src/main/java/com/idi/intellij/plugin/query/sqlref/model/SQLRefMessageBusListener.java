/*
 * User: eldad
 * Date: 07/02/11 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.sqlref.model;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.ReflectionUtil;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.Topic;
import com.intellij.util.messages.impl.MessageListenerList;

import java.lang.reflect.Field;

/**
 *
 */
@Deprecated
public class SQLRefMessageBusListener extends MessageListenerList {
	private static final Logger LOGGER = Logger.getInstance(SQLRefMessageBusListener.class.getName());

	public SQLRefMessageBusListener(MessageBus messageBus, Topic topic) {
		super(messageBus, topic);
	}

	public Object getMyMessageBus() {
		try {
			Field myMessageBus = ReflectionUtil.findField(MessageListenerList.class, MessageBus.class, "myMessageBus");
			myMessageBus.setAccessible(true);
			return myMessageBus.get(this);
		} catch (NoSuchFieldException e) {
			LOGGER.warn("No such field error: " + e.getMessage(), e);
		} catch (IllegalAccessException e) {
			LOGGER.warn("No such field error: " + e.getMessage(), e);
		}
		return null;
	}
}