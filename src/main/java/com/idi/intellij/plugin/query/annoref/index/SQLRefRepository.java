package com.idi.intellij.plugin.query.annoref.index;

import com.google.common.collect.Lists;
import com.idi.intellij.plugin.query.annoref.common.SPViewIndexHelper;
import com.idi.intellij.plugin.query.annoref.repo.model.SQLRefReference;
import com.idi.intellij.plugin.query.annoref.util.AnnoRefBundle;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.util.indexing.ID;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 7/11/13
 * Time: 12:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class SQLRefRepository implements ProjectComponent {
	private static final Logger logger = Logger.getInstance(SQLRefRepository.class.getName());

	//	private Map<ID, SQLRefReference> xmlRepository = new ConcurrentHashMap<ID, SQLRefReference>();
	private Map<String, SPViewIndexHelper> spViewIndexHelperMap = new ConcurrentHashMap<String, SPViewIndexHelper>();
	private Map<ID, SQLRefReference> classRepository = new ConcurrentHashMap<ID, SQLRefReference>();
	private Map<String, SQLRefReference> sqlRefReferenceMap = new ConcurrentHashMap<String, SQLRefReference>();


	public void resetAllProjectOnModulesChange() {
		logger.info("resetAllProjectOnModulesChange():");
//		xmlRepository.clear();
		classRepository.clear();
		sqlRefReferenceMap.clear();
	}

	public void addSPViewIndexToRepo(String spName, SPViewIndexHelper spViewIndexHelper) {
		if (spName != null && !spName.isEmpty()) {
			spViewIndexHelperMap.put(spName, spViewIndexHelper);
		}

	}

	public SPViewIndexHelper getSPViewIndexByName(String spName) {
		if (spViewIndexHelperMap.containsKey(spName)) {
			return spViewIndexHelperMap.get(spName);
		}
		return null;
	}


	public List<String> getAllReferencesIDs() {
		final List<String> refIDs = Lists.newArrayList();
		if (!sqlRefReferenceMap.isEmpty()) {
			for (final String refID : sqlRefReferenceMap.keySet()) {
				refIDs.add(refID);
			}
		}
		return refIDs;
	}

	public boolean isValidForReference(String refId) {
		return (sqlRefReferenceMap.containsKey(refId));
	}

/*	public boolean removeClassFromRepository(ID classID) {
		logger.info("removeClassFromRepository(): classID=" + classID.toString());
		if (classRepository.containsKey(classID)) {
			SQLRefReference sqlRefReference = classRepository.get(classID);
			if (sqlRefReferenceMap.containsValue(sqlRefReference)) {
				return sqlRefReferenceMap.values().remove(sqlRefReference);
			}
		}
		return false;
	}

	public boolean removeXmlFromRepository(ID xmlID) {
		logger.info("removeXmlFromRepository(): xmlID=" + xmlID);
		if (xmlRepository.containsKey(xmlID)) {
			SQLRefReference sqlRefReference = xmlRepository.get(xmlID);
			if (sqlRefReferenceMap.containsValue(sqlRefReference)) {
				return sqlRefReferenceMap.values().remove(sqlRefReference);
			}
		}
		return false;
	}*/

	public void addXmlFileInformationToRepository(String refID, VirtualFile xmlVF, PsiElement xmlAttribElement) {
//		logger.info("addXmlFileInformationToRepository(): refID=" + refID);
		if (sqlRefReferenceMap.containsKey(refID)) {
			SQLRefReference sqlRefReference = sqlRefReferenceMap.get(refID);
//			logger.info("addXmlFileInformationToRepository(): sqlRefReference exists=" + sqlRefReference);
			sqlRefReference.addXmlInformation(xmlVF, xmlAttribElement);
//			xmlRepository.put(xmlFileID, sqlRefReference);
		} else {

			SQLRefReference sqlRefReference = new SQLRefReference(refID);
			sqlRefReference.addXmlInformation(xmlVF, xmlAttribElement);
//			logger.info("addXmlFileInformationToRepository(): sqlRefReference new=" + sqlRefReference);
			sqlRefReferenceMap.put(refID, sqlRefReference);
//			xmlRepository.put(xmlFileID, sqlRefReference);
		}
	}

	public void addClassFileInformationToRepository(String refID, VirtualFile classVF, PsiElement annoElement) {
//		logger.info("addClassFileInformationToRepository(): refID=" + refID);
		if (sqlRefReferenceMap.containsKey(refID)) {
			SQLRefReference sqlRefReference = sqlRefReferenceMap.get(refID);
//			logger.info("addClassFileInformationToRepository(): sqlRefReference exists=" + sqlRefReference);
			sqlRefReference.addClassInformation(classVF, annoElement);
//			classRepository.put(classFileID, sqlRefReference);
		} else {
			SQLRefReference sqlRefReference = new SQLRefReference(refID);
			sqlRefReference.addClassInformation(classVF, annoElement);
//			logger.info("addClassFileInformationToRepository(): sqlRefReference new=" + sqlRefReference);
			sqlRefReferenceMap.put(refID, sqlRefReference);
//			classRepository.put(classFileID, sqlRefReference);
		}
	}

/*
	public void addMethodPropertyInformationToClassReference(String refID, PsiMethod psiMethod, String propertyName) {
		if (sqlRefReferenceMap.containsKey(refID)) {
			SQLRefReference sqlRefReference = sqlRefReferenceMap.get(refID);
			sqlRefReference.addMethodPropertyInformation(psiMethod, propertyName);
		} else {
			logger.warn("addMethodPropertyInformationToClassReference(): refID=" + refID + " Method=" + psiMethod.getName());
		}
	}
*/

	public void assignMethodPropertiesInformation(String refID, Map<String, Map<String, PsiMethod>> methodPropertiesMap) {
		if (sqlRefReferenceMap.containsKey(refID)) {
			SQLRefReference sqlRefReference = sqlRefReferenceMap.get(refID);
			sqlRefReference.assignMethodPropertyInformationMap(methodPropertiesMap);
		} else {
			logger.warn("assignMethodPropertiesInformation(): refID=" + refID);
		}
	}

	public void assignSqlSelectColumnsInformation(String refID, Map<String, String> methodPropertiesMap) {
		if (sqlRefReferenceMap.containsKey(refID) && !methodPropertiesMap.isEmpty()) {
			SQLRefReference sqlRefReference = sqlRefReferenceMap.get(refID);
			sqlRefReference.assignSqlSelectColumnsInformationMap(methodPropertiesMap);
		} else {
			logger.warn("assignSqlSelectColumnsInformation(): refID=" + refID);
		}
	}

	public void removeClassAndXmlFilesInformationFromRepository(String refID) {
		logger.info("removeClassFileInformationFromRepository():");
		if (sqlRefReferenceMap.containsKey(refID)) {
			sqlRefReferenceMap.remove(refID);
		}
	}

	public SQLRefReference getSQLRefReferenceForID(String refId) {
		if (sqlRefReferenceMap.containsKey(refId)) {
			return sqlRefReferenceMap.get(refId);
		}
//		logger.warn("getSQLRefReferenceForID(): refId=" + refId + " wasn't found");
		return null;
	}


	public void finishedAnnoRefScan(final Project project, final String annoRefType, final int filesCount) {
		ApplicationManager.getApplication().invokeLater(new Runnable() {
			@Override
			public void run() {
				Notification notification = new Notification("AnnoRef Index", "AnnoRef Indexing process",
						AnnoRefBundle.message("annoRef.progress.index.finish", filesCount, annoRefType),
						NotificationType.INFORMATION);
				Notifications.Bus.notify(notification, project);
			}
		});
	}

	@Override
	public void projectOpened() {
	}

	@Override
	public void projectClosed() {
	}

	@Override
	public void initComponent() {
	}

	@Override
	public void disposeComponent() {
	}

	@NotNull
	@Override
	public String getComponentName() {
		return getClass().getName();
	}


}
