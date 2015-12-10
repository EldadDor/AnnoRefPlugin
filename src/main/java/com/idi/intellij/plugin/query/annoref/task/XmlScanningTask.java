/*
 * User: eldad.Dor
 * Date: 03/01/2015 20:43
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.task;

import com.idi.intellij.plugin.query.annoref.common.SQLRefConstants;
import com.idi.intellij.plugin.query.annoref.index.SQLRefRepository;
import com.idi.intellij.plugin.query.annoref.index.listeners.XmlVisitorListener;
import com.idi.intellij.plugin.query.annoref.index.visitors.AnnoRefXmlVisitor;
import com.idi.intellij.plugin.query.annoref.notification.AnnoRefNotifications;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefConfigSettings;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefSettings;
import com.idi.intellij.plugin.query.annoref.util.AnnRefApplication;
import com.idi.intellij.plugin.query.annoref.util.AnnoRefBundle;
import com.idi.intellij.plugin.query.annoref.util.SQLRefNamingUtil;
import com.idi.intellij.plugin.query.annoref.util.SQLRefXmlVisitor;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.parsing.xml.XmlBuilderDriver;

/**
 * @author eldad
 * @date 03/01/2015
 */
public class XmlScanningTask extends IDIAbstractTask {
	private static final Logger logger = Logger.getInstance(XmlScanningTask.class.getName());
	private int xmlRefCount;

	public XmlScanningTask(Project project) {
		super(project, XmlFileType.INSTANCE);
	}

	@Override
	public void run(IDIProgressIndicator progressIndicator) {
		if (progressListener == null) {
			progressListener = progressIndicator;
		}
		runTask();
	}

	@Override
	public void runTask() {
		logger.info("run(): Xml files to scan=" + filesCollection.size());
		xmlRefCount = 0;
		try {
			for (final VirtualFile xmlFile : filesCollection) {
				final FileType fileTypeByFile = FileTypeManager.getInstance().getFileTypeByFile(xmlFile);
				if (fileTypeByFile instanceof XmlFileType) {
					progressListener.checkCanceled();
					scanXmlFile(xmlFile);
					progressListener.indicateChange();
				}
			}
			ServiceManager.getService(project, AnnoRefNotifications.class).notifyAnnoRefIndex(project, SQLRefConstants.ANNO_REF_XML, xmlRefCount);
		} catch (IDIProcessCancelledException e) {
			logger.error("runTask() processCancelled");
		} catch (Exception e) {
			logger.error(e);
			throw new IDIProcessCancelledException(e.getMessage(), e);
		}
	}

	@Override
	public boolean runComputableTask(IDIProgressIndicator progressIndicator) throws IDIProcessCancelledException {
		logger.info("run(): Xml files to scan=" + filesCollection.size());
		xmlRefCount = 0;
		try {
			for (final VirtualFile xmlFile : filesCollection) {
				scanXmlFile(xmlFile);
				progressIndicator.indicateChange();
			}
			ServiceManager.getService(project, AnnoRefNotifications.class).notifyAnnoRefIndex(project, SQLRefConstants.ANNO_REF_XML, xmlRefCount);
			return true;
		} catch (Exception e) {
			progressIndicator.cancel();
			return false;
		}
	}

	@Override
	public int numOfFiles() {
		return filesCollection.size();
	}

	@Override
	public String getTaskName() {
		return getClass().getSimpleName();
	}


	private void scanXmlFile(final VirtualFile xmlFile) {
		String xmlFileName = xmlFile.getPresentableName();
		if (SQLRefNamingUtil.isMatchFileName(xmlFileName, AnnoRefConfigSettings.getInstance(project).getAnnoRefState().QUERIES_REGEX)) {
			XmlVisitorListener xmlVisitorListener = new XmlVisitorListener() {
				@Override
				public void foundValidRefId(String refID, PsiElement xmlAttributeElement) {
					ServiceManager.getService(project, SQLRefRepository.class).addXmlFileInformationToRepository(refID, xmlFile, xmlAttributeElement);
					xmlRefCount++;
				}
			};
			SQLRefXmlVisitor.getInstance(project).setXmlVisitorListener(xmlVisitorListener).setInspector(false);
			SQLRefXmlVisitor.getInstance(project).visitFile(AnnRefApplication.getPsiFileFromVirtualFile(xmlFile, project));
			parseXmlForSql(xmlFile);

			/*if (remove) {
				ServiceManager.getService(project, SQLRefRepository.class).removeXmlFromRepository(indexKey);
			} else {
			}*/
		}
	}

	private void parseXmlForSql(VirtualFile xmlFile) {
		final AnnoRefSettings annoRefState = AnnoRefConfigSettings.getInstance(project).getAnnoRefState();
		if (annoRefState.ENABLE_SQL_TO_MODEL_VALIDATION) {
			logger.info(AnnoRefBundle.message("annoRef.xml.parse.sql.validation.enabled"));
			final XmlBuilderDriver xmlBuilderDriver = new XmlBuilderDriver(AnnRefApplication.getPsiFileFromVirtualFile(xmlFile, project).getText());
			xmlBuilderDriver.build(new AnnoRefXmlVisitor(project, xmlFile));
		}
		logger.info(AnnoRefBundle.message("annoRef.xml.parse..sql.validation.disabled"));
	}

}