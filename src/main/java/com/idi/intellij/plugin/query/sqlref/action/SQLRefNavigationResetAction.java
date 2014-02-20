package com.idi.intellij.plugin.query.sqlref.action;


import com.idi.intellij.plugin.query.sqlref.util.SQLRefApplication;
import com.idi.intellij.plugin.query.sqlref.util.SQLRefDataAccessor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.PackageIndex;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * Created by IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 26/03/2011
 * Time: 15:08:31
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class SQLRefNavigationResetAction extends AnAction {
	private static final Logger LOGGER = Logger.getInstance(SQLRefNavigationResetAction.class.getName());

	private boolean isBackGroundTaskFinished;

	public void actionPerformed(AnActionEvent event) {
		final Project project = (Project) event.getDataContext().getData(PlatformDataKeys.PROJECT.getName());

		final BackgroundableProcessIndicator progress = new BackgroundableProcessIndicator(project, "RexIndexing SQLRef usages...", PerformInBackgroundOption.DEAF, "Cancel SQLRef Indexing", "", true);
		if (ApplicationManager.getApplication().isDispatchThread()) {
			ProgressManager.getInstance().runProcess(new Runnable() {
				@Override
				public void run() {
					try {
						if (!progress.isRunning()) {
							progress.start();
						}
						progress.setFraction(0.1);
						if (project != null) {


							SQLRefApplication.resetProjectClassesAndReferences(project);
							progress.setFraction(0.2);
							SQLRefApplication.getInstance().initializeManagersForProject(project);
							progress.setFraction(0.3);
							final VirtualFile[] xmlVfToScan = PackageIndex.getInstance(project).getDirectoriesByPackageName("queries", true);
							progress.setFraction(0.4);
							SQLRefApplication.getInstance(project, SQLRefDataAccessor.class).lookForConventionalReferencedFile(project, xmlVfToScan);
							progress.setFraction(0.5);
							final VirtualFile[] classVfToScan = PackageIndex.getInstance(project).getDirectoriesByPackageName("com.idi", true);
							progress.setFraction(0.6);
							SQLRefApplication.getInstance(project, SQLRefDataAccessor.class).lookForConventionalReferencedFile(project, classVfToScan);
							progress.setFraction(0.7);
							SQLRefApplication.getInstance(project, SQLRefDataAccessor.class).findNonCorrelatingSQLRefsInXmlFiles();
							progress.setFraction(0.8);
						}
					} catch (Exception e) {
						LOGGER.error(e);
					}
					progress.setFraction(1.0);
				}
			}, progress);

//	/*			Application.
//					Task.Backgroundable myTask = new Task.Backgroundable(project, "ReIndexing XML for SQLRef Navigation", true) {
//						public void run(@NotNull ProgressIndicator indicator) {
//							indicator.setText("RexIndexing xml change...");
//							indicator.setFraction(0.0);
//							try {
//								indicator.setFraction(0.1);
//								if (project != null) {
//									SQLRefApplication.resetProjectClassesAndReferences(project);
//									indicator.setFraction(0.2);
//									SQLRefApplication.initializeManagersForProject(project);
//									indicator.setFraction(0.3);
//									final VirtualFile[] xmlVfToScan = PackageIndex.getInstance(project).getDirectoriesByPackageName("queries", true);
//									indicator.setFraction(0.4);
//									SQLRefApplication.getInstance(project, SQLRefDataAccessor.class).lookForConventionalReferencedFile(project, xmlVfToScan);
//									indicator.setFraction(0.5);
//									final VirtualFile[] classVfToScan = PackageIndex.getInstance(project).getDirectoriesByPackageName("com.idi", true);
//									indicator.setFraction(0.6);
//									SQLRefApplication.getInstance(project, SQLRefDataAccessor.class).lookForConventionalReferencedFile(project, classVfToScan);
//									indicator.setFraction(0.8);
//								}
//							}
//							catch (Exception e) {
//								System.out.println("e = " + e.getMessage());
//							}
//							indicator.setFraction(1.0);
//						}
//
//						@Override
//						public void onSuccess() {
//							isBackGroundTaskFinished = true;
//						}
//					};
//					myTask.setCancelText("Stop Indexing For SQLRef").queue();
//					myTask.setCancelTooltipText("Terminates current indexing for xml content change");*/
			if (ApplicationManager.getApplication().isDispatchThread()) {
				ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {
					@Override
					public void run() {
						if (isBackGroundTaskFinished) {
							SQLRefApplication.getInstance(project, SQLRefDataAccessor.class).findNonCorrelatingSQLRefsInXmlFiles();
						}
						while (true) {
							if (isBackGroundTaskFinished) {
								SQLRefApplication.getInstance(project, SQLRefDataAccessor.class).findNonCorrelatingSQLRefsInXmlFiles();
								return;
							}
						}
					}
				}, "", true, project);
			}
		}
	}
}

