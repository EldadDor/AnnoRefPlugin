package com.idi.intellij.plugin.query.annoref.repo.model;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.idi.intellij.plugin.query.annoref.common.SQLRefConstants.GETTER_PROPERTY;
import static com.idi.intellij.plugin.query.annoref.common.SQLRefConstants.SETTER_PROPERTY;

/**
 * Created by IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 05/11/2010
 * Time: 15:19:37
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("ClassWithoutNoArgConstructor")
public class SQLRefReference implements Comparable<String> {
	private static final Logger log = Logger.getInstance(SQLRefReference.class);
	private final String sqlRefId;
	private Map<String, List<VirtualFile>> xmlFiles = Maps.newConcurrentMap();
	private Map<String, List<VirtualFile>> classFiles = Maps.newConcurrentMap();
	private List<PsiElement> xmlQueryElements = new LinkedList<PsiElement>();
	private List<PsiElement> classAnnoElements = new LinkedList<PsiElement>();
	private List<SmartPsiElementPointer<PsiElement>> xmlSmartPointersElements = new LinkedList<SmartPsiElementPointer<PsiElement>>();
	private List<SmartPsiElementPointer<PsiElement>> classSmartPointersElements = new LinkedList<SmartPsiElementPointer<PsiElement>>();
	private HashBasedTable<String, String, SmartPsiElementPointer<PsiElement>> utilClassSmartPointersElements = HashBasedTable.create();

	private Map<String, Map<String, PsiMethod>> classPropertiedMethods = Maps.newConcurrentMap();
	private Map<String, String> sqlSelectedColumns = Maps.newConcurrentMap();
	private Map<String, String> sqlWhereParams = Maps.newConcurrentMap();

	public SQLRefReference(String sqlRefId) {
		this.sqlRefId = sqlRefId;
	}

	public SQLRefReference addClassInformation(VirtualFile classVF, PsiElement psiElement) {
		if (addClassFile(classVF.getName(), classVF)) {
			classAnnoElements.add(psiElement);
			classSmartPointersElements.add(createAnnoRefSmartPointer(psiElement));
		}
		return this;
	}

	public SQLRefReference addUtilClassCallInformation(VirtualFile classVF, PsiElement psiElement) {
		if (addClassFile(classVF.getName(), classVF)) {
			final SmartPsiElementPointer<PsiElement> annoRefSmartPointer = createAnnoRefSmartPointer(psiElement);
			final HashBasedTable<Object, Object, Object> table = HashBasedTable.create();
			utilClassSmartPointersElements.add(annoRefSmartPointer);
		}
		return this;
	}

	public SQLRefReference addXmlInformation(VirtualFile xmlVF, PsiElement psiElement) {
		if (addXmlFile(xmlVF.getName(), xmlVF)) {
			xmlQueryElements.add(psiElement);
			xmlSmartPointersElements.add(createAnnoRefSmartPointer(psiElement));
		}
		return this;
	}

	/*public SQLRefReference addMethodPropertyInformation(PsiMethod method, String propertyName) {
		classPropertiedMethods.put(propertyName, method);
		return this;
	}*/

	public SQLRefReference assignMethodPropertyInformationMap(Map<String, Map<String, PsiMethod>> methodPropertiesMap) {
		classPropertiedMethods.putAll(methodPropertiesMap);
		return this;
	}

	public SQLRefReference assignSqlSelectColumnsInformationMap(Map<String, String> methodPropertiesMap) {
		sqlSelectedColumns.putAll(methodPropertiesMap);
		return this;
	}

	public SQLRefReference addSqlSelectColumnToInformationMap(String columnName) {
		sqlSelectedColumns.put(columnName, columnName);
		return this;
	}

	public SQLRefReference addSqlWhereParamsToInformationMap(String paramName) {
		sqlWhereParams.put(paramName, paramName);
		return this;
	}

	public boolean isVoToXmlValidModel() {
		boolean isValidModel = true;
		if (!classPropertiedMethods.isEmpty()) {
			final Map<String, PsiMethod> settersMap = classPropertiedMethods.get(SETTER_PROPERTY);
			for (final String setterName : settersMap.keySet()) {
				if (!sqlWhereParams.containsKey(setterName)) {
					isValidModel = false;
					break;
				}
			}
			final Map<String, PsiMethod> gettersMap = classPropertiedMethods.get(GETTER_PROPERTY);
			for (final String getterName : gettersMap.keySet()) {
				if (!sqlSelectedColumns.containsKey(getterName)) {
					isValidModel = false;
					break;
				}

			}
		}
		return isValidModel;
	}

	private SmartPsiElementPointer<PsiElement> createAnnoRefSmartPointer(PsiElement annoElement) {
		return SmartPointerManager.getInstance(annoElement.getProject()).createSmartPsiElementPointer(annoElement);
	}

	public SQLRefReference addUtilClassCallInformation(String refId, PsiElement psiMethodElement) {
		if (log.isDebugEnabled()) {
			log.info("addUtilClassCallInformation(): refId=" + refId);
		}
		final SmartPsiElementPointer<PsiElement> annoRefSmartPointer = createAnnoRefSmartPointer(psiMethodElement);
//		utilClassSmartPointersElements.put(refId, annoRefSmartPointer);
		return this;
	}

	public List<PsiElement> getXmlQueryElements() {
		return xmlQueryElements;
	}

	public List<PsiElement> getClassAnnoElements() {
		return classAnnoElements;
	}

	public List<SmartPsiElementPointer<PsiElement>> getClassSmartPointersElements() {
		return classSmartPointersElements;
	}

	public List<SmartPsiElementPointer<PsiElement>> getXmlSmartPointersElements() {
		return xmlSmartPointersElements;
	}


	public boolean hasSomeElements() {
		return !xmlSmartPointersElements.isEmpty() || !classSmartPointersElements.isEmpty() || !utilClassSmartPointersElements.isEmpty();
	}

	public int collectiveSize() {
		return xmlSmartPointersElements.size() + classSmartPointersElements.size() + utilClassSmartPointersElements.size();
	}

	public HashBasedTable<String, String, SmartPsiElementPointer<PsiElement>> getUtilClassSmartPointersElements() {
		return utilClassSmartPointersElements;
	}

	public boolean addXmlFile(String xmlFileName, VirtualFile xmlFile) {
		if (!xmlFiles.containsKey(xmlFileName)) {
			List<VirtualFile> xmlFilesList = Lists.newArrayList();
			xmlFilesList.add(xmlFile);
			xmlFiles.put(xmlFileName, xmlFilesList);
			return true;
		}
		return false;
	}

	public boolean addClassFile(String classFileName, VirtualFile classFile) {
		if (!classFiles.containsKey(classFileName)) {
			List<VirtualFile> classFilesList = Lists.newArrayList();
			classFilesList.add(classFile);
			classFiles.put(classFileName, classFilesList);
			return true;
		}
		return false;
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
