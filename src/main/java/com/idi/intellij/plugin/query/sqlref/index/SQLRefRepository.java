package com.idi.intellij.plugin.query.sqlref.index;

import com.idi.intellij.plugin.query.sqlref.repo.model.SQLRefReference;
import com.intellij.idea.LoggerFactory;
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
	private final static Logger logger = LoggerFactory.getInstance().getLoggerInstance(SQLRefRepository.class.getName());

//	private List<Map<ID<String, PsiElement>, VirtualFile>> xmlFilesRepository = Lists.newArrayList();

//	private List<Map<ID<String, PsiElement>, VirtualFile>> classFilesRepository = Lists.newArrayList();

	private Map<ID, SQLRefReference> xmlRepository = new ConcurrentHashMap<ID, SQLRefReference>();

	private Map<ID, SQLRefReference> classRepository = new ConcurrentHashMap<ID, SQLRefReference>();

//	private Map<String, Pair<ID, ID>> sqlRefIdRepository = new ConcurrentHashMap<String, Pair<ID, ID>>();

	private Map<String, SQLRefReference> sqlRefReferenceMap = new ConcurrentHashMap<String, SQLRefReference>();


	public boolean removeClassFromRepository(ID classID) {
		if (classRepository.containsKey(classID)) {
			SQLRefReference sqlRefReference = classRepository.get(classID);
			if (sqlRefReferenceMap.containsValue(sqlRefReference)) {
				return sqlRefReferenceMap.values().remove(sqlRefReference);
			}
		}
		return false;
	}

	public boolean removeXmlFromRepository(ID classID) {
		if (xmlRepository.containsKey(classID)) {
			SQLRefReference sqlRefReference = xmlRepository.get(classID);
			if (sqlRefReferenceMap.containsValue(sqlRefReference)) {
				return sqlRefReferenceMap.values().remove(sqlRefReference);
			}
		}
		return false;
	}


	public void addXmlFileInformationToRepository(String refID, ID xmlFileID, VirtualFile xmlVF, PsiElement xmlAttribElement) {
		logger.info("addXmlFileInformationToRepository(): refID=" + refID);
		if (sqlRefReferenceMap.containsKey(refID)) {
			SQLRefReference sqlRefReference = sqlRefReferenceMap.get(refID);
			logger.info("addXmlFileInformationToRepository(): sqlRefReference exists=" + sqlRefReference);
			sqlRefReference.addXmlInformation(xmlFileID, xmlVF, xmlAttribElement);
			xmlRepository.put(xmlFileID, sqlRefReference);
		} else {
			SQLRefReference sqlRefReference = new SQLRefReference(refID);
			sqlRefReference.addXmlInformation(xmlFileID, xmlVF, xmlAttribElement);
			logger.info("addXmlFileInformationToRepository(): sqlRefReference new=" + sqlRefReference);
			sqlRefReferenceMap.put(refID, sqlRefReference);
			xmlRepository.put(xmlFileID, sqlRefReference);
		}
	}

	public void addClassFileInformationToRepository(String refID, ID classFileID, VirtualFile classVF, PsiElement annoElement) {
		logger.info("addClassFileInformationToRepository(): refID=" + refID);
		if (sqlRefReferenceMap.containsKey(refID)) {
			SQLRefReference sqlRefReference = sqlRefReferenceMap.get(refID);
			logger.info("addClassFileInformationToRepository(): sqlRefReference exists=" + sqlRefReference);
			sqlRefReference.addClassInformation(classFileID.toString(), classVF, annoElement);
			classRepository.put(classFileID, sqlRefReference);
		} else {
			SQLRefReference sqlRefReference = new SQLRefReference(refID);
			sqlRefReference.addClassInformation(classFileID.toString(), classVF, annoElement);
			logger.info("addClassFileInformationToRepository(): sqlRefReference new=" + sqlRefReference);
			sqlRefReferenceMap.put(refID, sqlRefReference);
			classRepository.put(classFileID, sqlRefReference);
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