package com.idi.intellij.plugin.query.annoref.index.progress;

import com.idi.intellij.plugin.query.annoref.index.SQLRefClassFileIndex;
import com.idi.intellij.plugin.query.annoref.index.SQLRefRepository;
import com.idi.intellij.plugin.query.annoref.index.SQLRefXmlFileIndex;
import com.idi.intellij.plugin.query.annoref.index.listeners.IndexProgressChangedListener;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.TaskInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 8/10/13
 * Time: 10:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class SQLRefProgressRunnable implements Runnable {

	private final IndexProgressChangedListener progressListener;
	private Project project;
	private TaskInfo taskInfo;
	private Set<Module> oldModules;
	private Set<Module> newModules;

	@Deprecated
	public SQLRefProgressRunnable(Project project, Pair<Set<Module>, Set<Module>> modulesToScan, IndexProgressChangedListener progressListener) {
		this.oldModules = modulesToScan.getFirst();
		this.newModules = modulesToScan.getSecond();
		this.project = project;
		this.progressListener = progressListener;
	}

	@Deprecated
	public SQLRefProgressRunnable(Project project, Set<Module> newModules, IndexProgressChangedListener progressListener) {
		this.progressListener = progressListener;
		this.project = project;
		this.newModules = newModules;
	}

	@Deprecated
	public SQLRefProgressRunnable(Project project, IndexProgressChangedListener progressListener) {
		this.project = project;
		this.progressListener = progressListener;
	}

	public SQLRefProgressRunnable(Project project, TaskInfo taskInfo, IndexProgressChangedListener progressListener) {
		this.project = project;
		this.taskInfo = taskInfo;
		this.progressListener = progressListener;
	}

	/*public void startScan(Collection<Module> modules, boolean remove) {
		modules.size();
		progressListener.changeMade(true);
		scanXmlFiles(modules, remove);
		scanClassFiles(modules, remove);
	}*/

/*	private void scanClassFiles(Iterable<Module> modules, boolean remove) {
		SQLRefClassFileIndex sqlRefClassFileIndex = new SQLRefClassFileIndex(project);
		for (Module module : modules) {
			sqlRefClassFileIndex.scanModuleClassFiles(module, progressListener, remove);
		}
	}*/

/*
	private void scanXmlFiles(Iterable<Module> modules, boolean remove) {
		SQLRefXmlFileIndex refXmlFileIndex = new SQLRefXmlFileIndex(project);
		for (Module module : modules) {
			refXmlFileIndex.scanModuleXmlFiles(module, progressListener, remove);
		}
	}
*/

	@Override
	public void run() {
		ServiceManager.getService(project, SQLRefRepository.class).resetAllProjectOnModulesChange();
		SQLRefXmlFileIndex refXmlFileIndex = new SQLRefXmlFileIndex(project, progressListener);
		SQLRefClassFileIndex sqlRefClassFileIndex = new SQLRefClassFileIndex(project, progressListener);
		refXmlFileIndex.indexSQLRef();
		sqlRefClassFileIndex.indexSQLRef();
//		startScan(newModules, false);
	}
}
