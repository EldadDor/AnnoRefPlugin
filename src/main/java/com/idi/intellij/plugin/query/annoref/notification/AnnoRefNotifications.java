/*
 * User: eldad.Dor
 * Date: 20/02/14 12:55
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.notification;

import com.idi.intellij.plugin.query.annoref.util.AnnoRefBundle;
import com.intellij.notification.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.HyperlinkEvent;

/**
 * @author eldad
 * @date 20/02/14
 */
public class AnnoRefNotifications implements ProjectComponent {
	private static final Logger log = Logger.getInstance(AnnoRefNotifications.class.getName());
	private static final String annoRefGroupId = "AnnoRef Index";


	public void notifyAnnoRefIndex(final Project project, final String annoRefType, final int filesCount) {
		log.info("notifyAnnoRefIndex(): annRefType=" + annoRefType + " filesCount=" + filesCount);
		if (filesCount > 0) {
			ApplicationManager.getApplication().invokeLater(new Runnable() {
				@Override
				public void run() {
					final NotificationGroup notificationGroup = NotificationGroup.findRegisteredGroup(annoRefGroupId);
					Notification notification;
					if (notificationGroup == null) {
						notification = getNotification(NotificationGroup.balloonGroup(annoRefGroupId));
					} else {
						notification = getNotification(notificationGroup);
					}
					Notifications.Bus.notify(notification, project);
				}

				private Notification getNotification(NotificationGroup notificationGroup) {
					return notificationGroup.createNotification("AnnoRef Indexing process",
							AnnoRefBundle.message("annoRef.progress.index.finish", filesCount, annoRefType), NotificationType.INFORMATION, new NotificationListener() {
								@Override
								public void hyperlinkUpdate(@NotNull Notification notification, @NotNull HyperlinkEvent hyperlinkEvent) {
									log.info("hyperlinkUpdate():");
								}
							});
				}
			});
		}
	}

	public void notifyAnnoRefError(final Project project, final String message) {
		ApplicationManager.getApplication().invokeLater(new Runnable() {
			@Override
			public void run() {
				Notification notification = new Notification(annoRefGroupId, "AnnoRef SPViewer error",
						message, NotificationType.ERROR);
				Notifications.Bus.notify(notification, project);
			}
		});
	}

	public void notifyAnnoRefInfo(final Project project, final String message, final String title) {
		ApplicationManager.getApplication().invokeLater(new Runnable() {
			@Override
			public void run() {
				Notification notification = new Notification(annoRefGroupId, title,
						message, NotificationType.INFORMATION);
				Notifications.Bus.notify(notification, project);
			}
		});
	}

	@Override
	public void projectOpened() {

	}

	@Override
	public void projectClosed() {

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