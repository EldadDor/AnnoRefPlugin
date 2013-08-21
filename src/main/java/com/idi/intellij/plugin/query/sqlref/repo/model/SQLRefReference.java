package com.idi.intellij.plugin.query.sqlref.repo.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.util.indexing.ID;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 05/11/2010
 * Time: 15:19:37
 * To change this template use File | Settings | File Templates.
 */
public class SQLRefReference implements Comparator<String> {
	private static final Logger LOGGER = Logger.getInstance(SQLRefReference.class.getName());

	private final String sqlRefId;

	private Module containerModule;

	private Map<ID, List<VirtualFile>> xmlFiles = Maps.newConcurrentMap();
	private Map<String, List<VirtualFile>> classFiles = Maps.newConcurrentMap();

	private List<PsiElement> xmlQueryElements = new LinkedList<PsiElement>();
	private List<PsiElement> classAnnoElements = new LinkedList<PsiElement>();


	public Module getContainerModule() {
		return containerModule;
	}

	public SQLRefReference(String sqlRefId) {
		this.sqlRefId = sqlRefId;
	}

	public SQLRefReference addClassInformation(String classFileName, VirtualFile classVF, PsiElement annoElement) {
		addClassFile(classFileName, classVF);
		classAnnoElements.add(annoElement);
		return this;
	}

	public SQLRefReference addXmlInformation(ID xmlFileName, VirtualFile xmlVF, PsiElement annoElement) {
		addXmlFile(xmlFileName, xmlVF);
		xmlQueryElements.add(annoElement);
		return this;
	}


	public Map<ID, List<VirtualFile>> getXmlFiles() {
		return xmlFiles;
	}

	public List<PsiElement> getXmlQueryElements() {
		return xmlQueryElements;
	}

	public List<PsiElement> getClassAnnoElements() {
		return classAnnoElements;
	}

	Pair<Set<VirtualFile>, Set<PsiElement>> addClassAndAnnoElement(VirtualFile classFile, PsiElement annoElement) {
		final Set<VirtualFile> classVFList = Sets.newHashSet();
		final Set<PsiElement> annoElementList = Sets.newHashSet();
		classVFList.add(classFile);
		annoElementList.add(annoElement);
		return new Pair<Set<VirtualFile>, Set<PsiElement>>(classVFList, annoElementList);
	}

	public void addXmlFile(ID xmlFileName, VirtualFile xmlFile) {
		if (xmlFiles.containsKey(xmlFileName)) {
			xmlFiles.get(xmlFileName).add(xmlFile);
		} else {
			List<VirtualFile> xmlFilesList = Lists.newArrayList();
			xmlFilesList.add(xmlFile);
			xmlFiles.put(xmlFileName, xmlFilesList);
		}
	}

	public void addClassFile(String classFileName, VirtualFile classFile) {
		if (classFiles.containsKey(classFileName)) {
			classFiles.get(classFileName).add(classFile);
		} else {
			List<VirtualFile> classFilesList = Lists.newArrayList();
			classFilesList.add(classFile);
			classFiles.put(classFileName, classFilesList);
		}
	}


	public List<VirtualFile> getXmlFiles(String xmlFileName) {
		return xmlFiles.get(xmlFileName);
	}


	public List<VirtualFile> getClassFiles(String classFileName) {
		return classFiles.get(classFileName);
	}

	public String getSqlRefId() {
		return sqlRefId;
	}

	@Override
	public int compare(@NotNull String refId1, @NotNull String refId2) {
		if (refId1.substring(0, refId1.length()).getBytes().length < refId2.substring(0, refId2.length()).getBytes().length)
			return -1;
		else if (refId1.substring(0, refId1.length()).getBytes().length == refId2.substring(0, refId2.length()).getBytes().length)
			return 0;
		else return 1;
	}

	@Override
	public String toString() {
		return "SQLRefReference{" +
				"sqlRefId='" + sqlRefId + '\'' +
				", xmlFiles=" + xmlFiles +
				", classFiles=" + classFiles +
				", xmlQueryElements=" + xmlQueryElements +
				", classAnnoElements=" + classAnnoElements +
				'}';
	}
}
