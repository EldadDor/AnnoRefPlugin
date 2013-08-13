/*
 * User: eldad
 * Date: 23/01/11 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.sqlref.component;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileAdapter;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.messages.impl.MessageListenerList;
import org.jetbrains.annotations.NotNull;

import java.util.EventListener;

/**
 *
 */
public class SQLRefMessageBusAccessor implements ApplicationComponent {
	private static final Logger LOGGER = Logger.getInstance(SQLRefMessageBusAccessor.class.getName());
	private MessageListenerList<FileEditorManagerListener> myListenerList;
	private VirtualFileManager virtualFileManager;

	public void initializeMessageBusAccessor(Project project) {
		myListenerList = new MessageListenerList<FileEditorManagerListener>(project.getMessageBus(), FileEditorManagerListener.FILE_EDITOR_MANAGER);

	}

	public boolean registerListener(EventListener eventListener) {
		if (eventListener instanceof FileEditorManagerListener) {
			myListenerList.add((FileEditorManagerListener) eventListener);
			LOGGER.info("The EventListener " + eventListener.getClass().getName() + " was registered to the MessageBus");
			return true;
		} else if (eventListener instanceof VirtualFileAdapter && virtualFileManager == null) {
			virtualFileManager = VirtualFileManager.getInstance();
			virtualFileManager.addVirtualFileListener((VirtualFileListener) eventListener);

			LOGGER.info("The EventListener " + eventListener.getClass().getName() + " was registered to the VirtualFileManager");
		}
		return false;
	}

	public boolean removeListener(EventListener eventListener) {
		if (eventListener instanceof FileEditorManagerListener) {
			myListenerList.remove((FileEditorManagerListener) eventListener);
			LOGGER.info("The EventListener " + eventListener.getClass().getName() + " was removed from the MessageBus");
//			virtualFileManager.removeVirtualFileListener((VirtualFileListener) eventListener);
			return true;
		}
		return false;
	}


	@Override
	public void initComponent() {
	}

	@Override
	public void disposeComponent() {
	}

	@NotNull
	@Override
	public String getComponentName() {
		return XmlRepositorySyncComponent.COMPONENT_NAME + ".MessageBusAccessor";
	}
}