package com.idi.intellij.plugin.query.sqlref.index;

import com.idi.intellij.plugin.query.sqlref.common.SPViewIndexHelper;
import com.idi.intellij.plugin.query.sqlref.repo.model.SQLRefReference;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.util.indexing.ID;
import org.jetbrains.annotations.NotNull;

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

	public void removeClassAndXmlFilesInformationFromRepository(String refID) {
		logger.info("removeClassFileInformationFromRepository():");
		if (sqlRefReferenceMap.containsKey(refID)) {
			sqlRefReferenceMap.remove(refID);
		}
	}

	public SQLRefReference getSQLRefReferenceForID(String refId) {
		if (sqlRefReferenceMap.containsKey(refId)) {
			SQLRefReference sqlRefReference = sqlRefReferenceMap.get(refId);
			return sqlRefReference;
		}
		return new SQLRefReference(refId);
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
