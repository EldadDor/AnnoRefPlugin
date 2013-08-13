package com.idi.intellij.plugin.query.sqlref.model;

import com.idi.intellij.plugin.query.sqlref.util.SQLRefApplication;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 01/02/2011
 * Time: 22:25:00
 * To change this template use File | Settings | File Templates.
 */
public class ClassReferenceCache {
	private ConcurrentSkipListMap<String, List<Pair<VirtualFile, PsiElement>>> classesRefCache = new ConcurrentSkipListMap<String, List<Pair<VirtualFile, PsiElement>>>();

	public static ClassReferenceCache getInstance(Project project) {
		return SQLRefApplication.getCurrentProjectClassesReferenceCache(project);
	}

	public boolean addClassReferenceToCache(VirtualFile virtualFile, String annoName, PsiElement psiElement) {
		if (classesRefCache.containsKey(annoName)) {
			classesRefCache.get(annoName).add(new Pair<VirtualFile, PsiElement>(virtualFile, psiElement));
			return true;
		} else {
			ArrayList<Pair<VirtualFile, PsiElement>> pairArrayList = new ArrayList<Pair<VirtualFile, PsiElement>>();
			pairArrayList.add(new Pair<VirtualFile, PsiElement>(virtualFile, psiElement));
			classesRefCache.put(annoName, pairArrayList);
			return true;
		}
	}

	public List<Pair<VirtualFile, PsiElement>> hitCacheForClassReference(String virtualFile) {
		return classesRefCache.get(virtualFile);
	}

	public String displayCachedClassInfo(String virtualFile) {
		StringBuilder sb = new StringBuilder();
		List<Pair<VirtualFile, PsiElement>> pairs = classesRefCache.get(virtualFile);
		for (final Pair<VirtualFile, PsiElement> pair : pairs) {
			sb.append("Cached VF: ").append(virtualFile).append("Class VF:").append(pair.getFirst()).append("PsiElement: ").append(pair.getSecond());
		}
		return sb.toString();
	}
}
