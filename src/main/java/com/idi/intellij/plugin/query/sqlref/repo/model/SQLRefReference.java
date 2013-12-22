package com.idi.intellij.plugin.query.sqlref.repo.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 05/11/2010
 * Time: 15:19:37
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("ClassWithoutNoArgConstructor")
public class SQLRefReference implements Comparable<String> {
	private static final Logger LOGGER = Logger.getInstance(SQLRefReference.class.getName());
	private final String sqlRefId;
	//	private Map<ID, List<VirtualFile>> xmlFiles = Maps.newConcurrentMap();
	private Map<String, List<VirtualFile>> xmlFiles = Maps.newConcurrentMap();
	private Map<String, List<VirtualFile>> classFiles = Maps.newConcurrentMap();
	//	private Map<ID, List<VirtualFile>> classFiles = Maps.newConcurrentMap();
	private List<PsiElement> xmlQueryElements = new LinkedList<PsiElement>();
	private List<PsiElement> classAnnoElements = new LinkedList<PsiElement>();


	public SQLRefReference(String sqlRefId) {
		this.sqlRefId = sqlRefId;
	}

	public SQLRefReference addClassInformation(VirtualFile classVF, PsiElement annoElement) {
		addClassFile(classVF.getName(), classVF);
		classAnnoElements.add(annoElement);
		return this;
	}

/*	public SQLRefReference addXmlInformation(ID xmlFileName, VirtualFile xmlVF, PsiElement annoElement) {
		addXmlFile(xmlFileName, xmlVF);
		xmlQueryElements.add(annoElement);
		return this;
	}	*/

	public SQLRefReference addXmlInformation(VirtualFile xmlVF, PsiElement annoElement) {
		addXmlFile(xmlVF.getName(), xmlVF);
		xmlQueryElements.add(annoElement);
		return this;
	}

/*
	public Map<ID, List<VirtualFile>> getXmlFiles() {
		return xmlFiles;
	}
*/

	public List<PsiElement> getXmlQueryElements() {
		return xmlQueryElements;
	}

	public List<PsiElement> getClassAnnoElements() {
		return classAnnoElements;
	}

	public void addXmlFile(String xmlFileName, VirtualFile xmlFile) {
		if (!xmlFiles.containsKey(xmlFileName)) {
			List<VirtualFile> xmlFilesList = Lists.newArrayList();
			xmlFilesList.add(xmlFile);
			xmlFiles.put(xmlFileName, xmlFilesList);
		}
	}

	public void addClassFile(String classFileName, VirtualFile classFile) {
		if (!classFiles.containsKey(classFileName)) {
			List<VirtualFile> classFilesList = Lists.newArrayList();
			classFilesList.add(classFile);
			classFiles.put(classFileName, classFilesList);
		}
	}


	public String getSqlRefId() {
		return sqlRefId;
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

	@Override
	public int compareTo(String refID) {
		if (refID.substring(0, refID.length()).getBytes().length < sqlRefId.substring(0, sqlRefId.length()).getBytes().length) {
			return -1;
		} else if (refID.substring(0, refID.length()).getBytes().length == sqlRefId.substring(0, sqlRefId.length()).getBytes().length) {
			return 0;
		} else {
			return 1;
		}
	}
}
