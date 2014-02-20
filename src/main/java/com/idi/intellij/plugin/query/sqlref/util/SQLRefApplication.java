package com.idi.intellij.plugin.query.sqlref.util;

import com.idi.intellij.plugin.query.sqlref.index.listeners.ProgressChangedListener;
import com.idi.intellij.plugin.query.sqlref.index.progress.SQLRefProgressIndicator;
import com.idi.intellij.plugin.query.sqlref.index.progress.SQLRefProgressRunnable;
import com.idi.intellij.plugin.query.sqlref.model.ClassReferenceCache;
import com.idi.intellij.plugin.query.sqlref.model.ReferenceCollectionManager;
import com.idi.intellij.plugin.query.sqlref.notification.AnnoRefNotifications;
import com.idi.intellij.plugin.query.sqlref.persist.SQLRefConfigSettings;
import com.intellij.ProjectTopics;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.TaskInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootAdapter;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.impl.ModulesOrderEnumerator;
import com.intellij.openapi.vcs.changes.RunnableBackgroundableWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.Processor;
import com.intellij.util.messages.impl.MessageListenerList;
import org.jetbrains.annotations.TestOnly;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 27/01/2011
 * Time: 23:32:31
 * To change this template use File | Settings | File Templates.
 */
public class SQLRefApplication {
	private static final Logger logger = Logger.getInstance(SQLRefApplication.class.getName());
	private static final Map<String, ReferenceCollectionManager> sqlRefProjectReferencesManager = new ConcurrentHashMap<String, ReferenceCollectionManager>();
	private static final Map<String, ClassReferenceCache> classesRefCacheProjectManager = new ConcurrentHashMap<String, ClassReferenceCache>();
	private static final AtomicInteger scannerCounter = new AtomicInteger(0);
	private static final AtomicBoolean isResetRunning = new AtomicBoolean(false);
	private MessageListenerList<Notifications> notificationsMessageListenerList;
	private static SQLRefApplication instance;

	@Deprecated
	public static <T> T getInstance(Class<T> type) {
		return ServiceManager.getService(type);
	}

	public static SQLRefApplication getInstance() {
		if (instance == null) {
			instance = new SQLRefApplication();
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

	public void initializeManagersForProject(Project project) {
		createNewClassesRefCacheForProject(project);
		if (isNotScanning()) {
			registerForProjectRootChanges(project);
		}
		registerNotificationsListener(project);
		logger.info("initializeManagersForProject(): Finished initialize");
	}

	private static void registerForProjectRootChanges(final Project project) {
		project.getMessageBus().connect().subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootAdapter() {
			@Override
			public void rootsChanged(ModuleRootEvent event) {
				if (SQLRefConfigSettings.getInstance(project).getSqlRefState().ENABLE_AUTO_SYNC) {
					logger.info("registerForProjectRootChanges()_rootsChanged(): event=" + event.isCausedByFileTypesChange());
					final Project projectSource = (Project) event.getSource();
					final SQLRefProgressIndicator sqlRefProgressIndicator = new SQLRefProgressIndicator(projectSource, AnnoRefBundle.message("annoRef.progress.reindex"),
							PerformInBackgroundOption.ALWAYS_BACKGROUND, AnnoRefBundle.message("annoRef.progress.reindex.cancel"), "", true);
					TaskInfo taskInfoWrapper = null;
					final ProgressChangedListener progressListener = new ProgressChangedListener() {
						@Override
						public synchronized void changeMade(boolean isChanged) {
							if (isChanged) {
								sqlRefProgressIndicator.setFraction(sqlRefProgressIndicator.getFraction() + 0.01d);
								if (logger.isDebugEnabled()) {
									logger.debug("changeMade(): currentFraction()= " + sqlRefProgressIndicator.getFraction());
								}

							}
						}

						@Override
						public void failedProcess() {
							logger.info("failedProcess(): indexing SQLRef");
						}
					};
					Runnable process = new SQLRefProgressRunnable(projectSource, taskInfoWrapper, progressListener);
					if (ApplicationManager.getApplication().isDispatchThread()) {
						taskInfoWrapper = new RunnableBackgroundableWrapper(projectSource, "SQLRef project root reset", process);
						if (sqlRefProgressIndicator.isFinished(taskInfoWrapper)) {
							sqlRefProgressIndicator.setFraction(1);
						} else {
							ProgressManager.getInstance().runProcess(process, sqlRefProgressIndicator);
						}
					}
				} else {
					logger.info("registerForProjectRootChanges(): Auto_Sync not enabled");
				}
			}
		});

	}

	private void registerNotificationsListener(Project project) {
		project.getMessageBus().connect(project).subscribe(Notifications.TOPIC, new AnnoRefNotifications());
//		notificationsMessageListenerList = new MessageListenerList<Notifications>(project.getMessageBus(), Notifications.TOPIC);
//		notificationsMessageListenerList.add(new AnnoRefNotifications());
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
}
