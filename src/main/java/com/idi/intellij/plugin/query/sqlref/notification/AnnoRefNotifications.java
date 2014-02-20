/*
 * User: eldad.Dor
 * Date: 20/02/14 12:55
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.sqlref.notification;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationsAdapter;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * @author eldad
 * @date 20/02/14
 */
public class AnnoRefNotifications extends NotificationsAdapter implements ApplicationComponent {
	private static final Logger log = Logger.getInstance(AnnoRefNotifications.class.getName());

	@Override
	public void notify(@NotNull Notification notification) {
		log.info("notify():");
		super.notify(notification);
	}

	@Override
	public void register(@NotNull String s, @NotNull NotificationDisplayType notificationDisplayType) {
		log.info("register():");
		super.register(s, notificationDisplayType);
	}

	@Override
	public void register(@NotNull String s, @NotNull NotificationDisplayType notificationDisplayType, boolean b) {
		log.info("register():");
		super.register(s, notificationDisplayType, b);
	}

	@Override
	public void register(@NotNull String s, @NotNull NotificationDisplayType notificationDisplayType, boolean b, boolean b2) {
		log.info("register():");
		super.register(s, notificationDisplayType, b, b2);
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
		return AnnoRefNotifications.class.getName();
	}
}