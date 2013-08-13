package com.idi.intellij.plugin.query.sqlref.index.progress;

import com.idi.intellij.plugin.query.sqlref.index.SQLRefClassFileIndex;
import com.idi.intellij.plugin.query.sqlref.index.SQLRefXmlFileIndex;
import com.idi.intellij.plugin.query.sqlref.index.listeners.ProgressChangedListener;
import com.intellij.openapi.module.Module;
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

	private final ProgressChangedListener progressListener;
	private Project project;
	private Set<Module> oldModules;
	private Set<Module> newModules;

	public SQLRefProgressRunnable(Project project, Pair<Set<Module>, Set<Module>> modulesToScan, ProgressChangedListener progressListener) {
		this.oldModules = modulesToScan.getFirst();
		this.newModules = modulesToScan.getSecond();
		this.project = project;
		this.progressListener = progressListener;
	}


	public void startScan(Set<Module> modules, boolean remove) {
		modules.size();
		progressListener.changeMade(true);
		scanXmlFiles(modules, remove);
		scanClassFiles(modules, remove);
	}

	private void scanClassFiles(Set<Module> modules, boolean remove) {
		SQLRefClassFileIndex sqlRefClassFileIndex = new SQLRefClassFileIndex(project);
		for (Module module : modules) {
			sqlRefClassFileIndex.scanModuleClasses(module, progressListener, remove);
		}
	}

	private void scanXmlFiles(Set<Module> modules, boolean remove) {
		SQLRefXmlFileIndex refXmlFileIndex = new SQLRefXmlFileIndex(project);
		for (Module module : modules) {
			refXmlFileIndex.scanModuleXmlFiles(module, progressListener, remove);
		}
	}


	@Override
	public void run() {
		startScan(oldModules, true);
		startScan(newModules, false);
	}
}
