package com.idi.intellij.plugin.query.sqlref.component;

import java.util.EventListener;

/**
 * Created by IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 21/03/2011
 * Time: 22:48:38
 * To change this template use File | Settings | File Templates.
 */
public interface MessageBusAccessor {
	void initializeMessageBusAccessor();

	boolean registerListener(EventListener eventListener);

	boolean removeListener(EventListener eventListener);
}
