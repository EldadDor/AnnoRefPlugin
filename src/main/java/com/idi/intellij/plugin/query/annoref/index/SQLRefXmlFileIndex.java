package com.idi.intellij.plugin.query.annoref.index;

import com.idi.intellij.plugin.query.annoref.common.SQLRefConstants;
import com.idi.intellij.plugin.query.annoref.index.listeners.IndexProgressChangedListener;
import com.idi.intellij.plugin.query.annoref.index.listeners.XmlVisitorListener;
import com.idi.intellij.plugin.query.annoref.index.visitors.AnnoRefXmlVisitor;
import com.idi.intellij.plugin.query.annoref.notification.AnnoRefNotifications;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefConfigSettings;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefSettings;
import com.idi.intellij.plugin.query.annoref.task.IDIProgressIndicator;
import com.idi.intellij.plugin.query.annoref.util.AnnRefApplication;
import com.idi.intellij.plugin.query.annoref.util.AnnoRefBundle;
import com.idi.intellij.plugin.query.annoref.util.SQLRefNamingUtil;
import com.idi.intellij.plugin.query.annoref.util.SQLRefXmlVisitor;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.parsing.xml.XmlBuilderDriver;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 7/5/13
 * Time: 7:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class SQLRefXmlFileIndex {

	private static final Logger logger = Logger.getInstance(SQLRefXmlFileIndex.class.getName());
	private Project project;
	private IndexProgressChangedListener progressChangedListener;
	private IDIProgressIndicator progressIndicator;
	private int xmlRefCount;
	private int filesToBeScannedCount;
	private final String QUERIES_FILE_REGEX_PATTERN;

	public SQLRefXmlFileIndex(Project project) {
		this.project = project;
		QUERIES_FILE_REGEX_PATTERN = AnnoRefConfigSettings.getInstance(project).getAnnoRefState().QUERIES_REGEX;
	}

	public SQLRefXmlFileIndex(Project project, IndexProgressChangedListener progressChangedListener) {
		this.project = project;
		this.progressChangedListener = progressChangedListener;
		QUERIES_FILE_REGEX_PATTERN = AnnoRefConfigSettings.getInstance(project).getAnnoRefState().QUERIES_REGEX;
	}

	public synchronized void indexSQLRef() {
		logger.info("indexSQLRef():");
		xmlRefCount = 0;
		Collection<VirtualFile> xmlFiles = FileTypeIndex.getFiles(XmlFileType.INSTANCE, GlobalSearchScope.projectScope(project));
		try {
			for (final VirtualFile xmlFile : xmlFiles) {
				scanXmlFile(xmlFile);
				progressIndicator.setFraction(1 / filesToBeScannedCount);
			}
			if (!xmlFiles.isEmpty() && progressChangedListener != null) {
				progressChangedListener.finishedProcess();
			}
			ServiceManager.getService(project, AnnoRefNotifications.class).notifyAnnoRefIndex(project, SQLRefConstants.ANNO_REF_XML, xmlRefCount);
		} catch (Exception e) {
			if (progressChangedListener != null) {
				progressChangedListener.failedProcess(e.getMessage());
			}
		}
	}

	private void scanXmlFile(final VirtualFile xmlFile) {
		String xmlFileName = xmlFile.getName();
		if (SQLRefNamingUtil.isMatchFileName(xmlFileName, QUERIES_FILE_REGEX_PATTERN)) {
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

	private void showVFInfo(VirtualFile virtualFile) {
		logger.info("showVFInfo(): XML");
		logger.info("virtualFile: isValid= " + virtualFile.isValid());
		logger.info("virtualFile: isSymLink=" + virtualFile.isSymLink());
		logger.info("virtualFile: url= " + virtualFile.getUrl());
		logger.info("virtualFile: fileType= " + virtualFile.getFileType());
	}


}
