package com.idi.intellij.plugin.query.annoref.util;

import com.idi.intellij.plugin.query.annoref.index.progress.AnnoRefBackgroundWorker;
import com.idi.intellij.plugin.query.annoref.model.ClassReferenceCache;
import com.idi.intellij.plugin.query.annoref.model.ReferenceCollectionManager;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefConfigSettings;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefSettings;
import com.intellij.ProjectTopics;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootAdapter;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.impl.ModulesOrderEnumerator;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.Processor;
import com.intellij.util.messages.impl.MessageListenerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import javax.swing.event.HyperlinkEvent;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static com.idi.intellij.plugin.query.annoref.common.SQLRefConstants.ANNO_REF_NOTIFICATION_GORUP;

/**
 * Created by IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 27/01/2011
 * Time: 23:32:31
 * To change this template use File | Settings | File Templates.
 */
public class AnnRefApplication {
	private static final Logger logger = Logger.getInstance(AnnRefApplication.class.getName());
	private static final Map<String, ReferenceCollectionManager> sqlRefProjectReferencesManager = new ConcurrentHashMap<String, ReferenceCollectionManager>();
	private static final Map<String, ClassReferenceCache> classesRefCacheProjectManager = new ConcurrentHashMap<String, ClassReferenceCache>();
	private static final AtomicInteger scannerCounter = new AtomicInteger(0);
	private static final AtomicBoolean isResetRunning = new AtomicBoolean(false);
	private static AnnRefApplication instance;
	private MessageListenerList<Notifications> notificationsMessageListenerList;
	private static AtomicLong lastIndexTime = new AtomicLong(0l);

	private static Notification myNotification;

	@Deprecated
	public static <T> T getInstance(Class<T> type) {
		return ServiceManager.getService(type);
	}

	public static AnnRefApplication getInstance() {
		if (instance == null) {
			instance = new AnnRefApplication();
		}
		return instance;
	}

	public static <T> T getInstance(Project project, Class<T> type) {
		return ServiceManager.getService(project, type);
	}

	public static PsiFile getPsiFileFromVirtualFile(VirtualFile virtualFile, Project project) {
		return PsiDocumentManager.getInstance(project).getPsiFile(FileDocumentManager.getInstance().getDocument(virtualFile));
	}

	public static VirtualFile getVirtualFileFromPsiFile(PsiFile psiFile, Project project) {
		return FileDocumentManager.getInstance().getFile(PsiDocumentManager.getInstance(project).getDocument(psiFile));
	}

	public static Editor getEditorFromVirtualFile(VirtualFile virtualFile, Project project) {
		return findFirstAvailableEditor(FileEditorManager.getInstance(project).getAllEditors(virtualFile));
	}

	@Nullable
	private static Editor findFirstAvailableEditor(@NotNull FileEditor[] fileEditors) {
		for (FileEditor fileEditor : fileEditors) {
			if (fileEditor instanceof TextEditor) {
				return ((TextEditor) fileEditor).getEditor();
			}
		}
		return null;
	}


	public static <T> T getInstanceComponent(Class<T> type) {
		return ApplicationManager.getApplication().getComponent(type);
	}

	public static boolean isNotScanning() {
		return scannerCounter.get() == 0;
	}

	public static void addScanner() {
		logger.info("addScannerCounter(): scannerCounter=" + scannerCounter.incrementAndGet());
	}

	public static void removeScanner() {
		logger.info("removeScanner(): scannerCounter=" + scannerCounter.decrementAndGet());
	}

	private static void registerForProjectRootChanges(final Project project) {
		project.getMessageBus().connect().subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootAdapter() {
			@Override
			public void rootsChanged(ModuleRootEvent event) {
				if (isLastIndexTimeIfLapsed(project, 1)) {
					final Project projectSource = (Project) event.getSource();
					if (AnnoRefConfigSettings.getInstance(project).getAnnoRefState().ENABLE_AUTO_SYNC) {
						logger.info("rootsChanged(): ENABLE_AUTO_SYNC is on");
						new AnnoRefBackgroundWorker().runInBackground(project);
					} else {
						logger.info("rootsChanged(): ENABLE_AUTO_SYNC is off");
						doUpdateNotifications(projectSource);
					}
				}
			}
		});
	}


	private static void doUpdateNotifications(final Project project) {
		logger.info("doUpdateNotifications():");
		if (myNotification != null && !myNotification.isExpired()) {
			return;
		}
		myNotification = new Notification(ANNO_REF_NOTIFICATION_GORUP, AnnoRefBundle.message("annoRef.notfiy.title"), "<a href='reindex'>" +
				AnnoRefBundle.message("annoRef.notfiy.reindex") + "</a> " +
				"<a href='autoSync'>" + AnnoRefBundle.message("annoRef.notfiy.enableAutoSync") + "</a>",
				NotificationType.INFORMATION, new NotificationListener.Adapter() {
			@Override
			protected void hyperlinkActivated(@NotNull Notification notification, @NotNull HyperlinkEvent event) {
				if (event.getDescription().equals("reindex")) {
					new AnnoRefBackgroundWorker().runInBackground(project);
				}
				if (event.getDescription().equals("autoSync")) {
					final AnnoRefSettings annoRefState = AnnoRefConfigSettings.getInstance(project).getAnnoRefState();
					annoRefState.ENABLE_AUTO_SYNC = true;
				}
				notification.expire();
				myNotification = null;
			}
		}
		);
		Notifications.Bus.notify(myNotification, project);
	}


	@TestOnly
	private static void moduleEnumeratorInfo(Project project, Set<Module> moduleList) {
		ModulesOrderEnumerator orderEnumerator = (ModulesOrderEnumerator) ProjectRootManager.getInstance(project).orderEntries(moduleList);
		orderEnumerator.processRootModules(new Processor<Module>() {
			@Override
			public boolean process(Module module) {
				logger.info("process-Module=" + module.getName());
				return false;  //To change body of implemented methods use File | Settings | File Templates.
			}
		});
		orderEnumerator.forEach(new Processor<OrderEntry>() {
			@Override
			public boolean process(OrderEntry orderEntry) {
				logger.info("ForEach-ownerModule=" + orderEntry.getOwnerModule());
				return false;  //To change body of implemented methods use File | Settings | File Templates.
			}
		});
		orderEnumerator.forEachModule(new Processor<Module>() {
			@Override
			public boolean process(Module orderEntry) {
				logger.info("forEachModule-ModuleEntry=" + orderEntry.getName());
				if (orderEntry.isLoaded()) {
					return false;  //To change body of implemented methods use File | Settings | File Templates.
				} else {
					return true;
				}
			}
		});
	}

	private static void createNewClassesRefCacheForProject(Project project) {
		String basePath = project.getBasePath();
		logger.info("createNewClassesRefCacheForProject(): BasePath=" + basePath);
		classesRefCacheProjectManager.put(basePath, new ClassReferenceCache());
	}

	public static ClassReferenceCache getCurrentProjectClassesReferenceCache(Project project) {
//		 project = PlatformDataKeys.PROJECT.getData(DataManager.getInstance().getDataContext(editor.getContentComponent()));
		String basePath = project.getBasePath();
		logger.info("getCurrentProjectClassesReferenceCache(): BasePath=" + basePath);
		return classesRefCacheProjectManager.get(basePath);
	}

	public static ReferenceCollectionManager getCurrentProjectReferenceCollectionManager(Project project) {
		String basePath = project.getBasePath();
		logger.info("getCurrentProjectReferenceCollectionManager(): BasePath=" + basePath);
		return sqlRefProjectReferencesManager.get(basePath);
	}

	public static void resetProjectClassesAndReferences(Project project) {
		sqlRefProjectReferencesManager.remove(project.getBasePath());
		classesRefCacheProjectManager.remove(project.getBasePath());
	}

	public void initializeManagersForProject(Project project) {
		createNewClassesRefCacheForProject(project);
		if (isNotScanning()) {
			registerForProjectRootChanges(project);
		}
		logger.info("initializeManagersForProject(): Finished initialize");
	}

	public static boolean isLastIndexTimeIfLapsed(final Project project, int phase) {
		if (phase == 0) {
			logger.info("setLastIndexTimeIfLapsed(): Project Bootstrap");
			lastIndexTime.set(System.currentTimeMillis());
			return true;
		}
		if (AnnoRefConfigSettings.getInstance(project).getAnnoRefState().REINDEX_INTERVAL < 0) {
			return true;
		}
		if (TimeUtil.isOlderThan(lastIndexTime.get(), AnnoRefConfigSettings.getInstance(project).getAnnoRefState().REINDEX_INTERVAL)) {
			logger.info("setLastIndexTimeIfLapsed(): LastIndexTime lapsed, ReIndexing");
			lastIndexTime.set(System.currentTimeMillis());
			return true;
		}
		logger.info("isLastIndexTimeIfLapsed(): LastIndexTime hasn't lapsed, LastIndexTime=" + lastIndexTime.get());
		return false;
	}
}
