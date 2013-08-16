package com.idi.intellij.plugin.query.sqlref.util;

import com.google.common.collect.Sets;
import com.idi.intellij.plugin.query.sqlref.index.listeners.ProgressChangedListener;
import com.idi.intellij.plugin.query.sqlref.index.progress.SQLRefProgressIndicator;
import com.idi.intellij.plugin.query.sqlref.index.progress.SQLRefProgressRunnable;
import com.idi.intellij.plugin.query.sqlref.model.ClassReferenceCache;
import com.idi.intellij.plugin.query.sqlref.model.ReferenceCollectionManager;
import com.idi.intellij.plugin.query.sqlref.repo.model.SQLRefProjectModulesCollection;
import com.intellij.ProjectTopics;
import com.intellij.idea.LoggerFactory;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootAdapter;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.impl.ModulesOrderEnumerator;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.Processor;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 27/01/2011
 * Time: 23:32:31
 * To change this template use File | Settings | File Templates.
 */
public class SQLRefApplication {
	private final static Logger logger = LoggerFactory.getInstance().getLoggerInstance(SQLRefApplication.class.getName());


	private static final Map<String, ReferenceCollectionManager> sqlRefProjectReferencesManager = new ConcurrentHashMap<String, ReferenceCollectionManager>();
	private static final Map<String, ClassReferenceCache> classesRefCacheProjectManager = new ConcurrentHashMap<String, ClassReferenceCache>();


	@Deprecated
	public static <T> T getInstance(Class<T> type) {
		return ServiceManager.getService(type);
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

	public static void initializeManagersForProject(Project project) {
		createNewClassesRefCacheForProject(project);
		registerForProjectRootChanges(project);
		createNewProjectReferenceManager(project);
	}

	private static void registerForProjectRootChanges(Project project) {
		project.getMessageBus().connect().subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootAdapter() {
			public void rootsChanged(ModuleRootEvent event) {
				logger.info("registerForProjectRootChanges()_rootsChanged(): event=" + event.isCausedByFileTypesChange());
				Project project = (Project) event.getSource();
				Set<Module> moduleList = getModules(project);
				SQLRefProjectModulesCollection modulesRepo = ServiceManager.getService(project, SQLRefProjectModulesCollection.class);
				Pair<Set<Module>, Set<Module>> differentiate = modulesRepo.differentiate(moduleList);

				final SQLRefProgressIndicator sqlRefProgressIndicator = new SQLRefProgressIndicator(project, "RexIndexing SQLRef usages...",
						PerformInBackgroundOption.DEAF, "Cancel SQLRef Indexing", "", true);


				if (ApplicationManager.getApplication().isDispatchThread()) {
					SQLRefProgressRunnable process = new SQLRefProgressRunnable(project, differentiate, new ProgressChangedListener() {
						@Override
						public void changeMade(boolean isChanged) {
							if (isChanged) {
								sqlRefProgressIndicator.setFraction(sqlRefProgressIndicator.getFraction() + 0.05d);
								logger.info("changeMade(): currentFraction()= " + sqlRefProgressIndicator.getFraction());
							}
						}
					});
					ProgressManager.getInstance().runProcess(process, sqlRefProgressIndicator);
					sqlRefProgressIndicator.setFraction(1);
				}
//				moduleEnumeratorInfo(project, moduleList);
			}

			private Set<Module> getModules(Project project) {
				Module[] modules = ModuleManager.getInstance(project).getModules();
				Set<Module> moduleList = Sets.newHashSet();
				Collections.addAll(moduleList, modules);
				return moduleList;
			}
		});
	}

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

	private static void createNewProjectReferenceManager(Project project) {
		String basePath = project.getBasePath();
		logger.info("createNewProjectReferenceManager(): BasePath=" + basePath);
		sqlRefProjectReferencesManager.put(basePath, new ReferenceCollectionManager());
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

	/*public static Project getFilesCorrespondingProject(String filePath) {
		Map<String, Project> projectCacheHolder = ApplicationManager.getApplication().getComponent(SQLRefProjectManager.class).getProjectCacheHolder();
		for (final String projectKey : projectCacheHolder.keySet()) {
			if (FileUtil.startsWith(filePath, projectKey)) {
				return projectCacheHolder.get(projectKey);
			}
		}
		return null;
	}*/
}
