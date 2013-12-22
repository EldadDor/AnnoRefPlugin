package com.idi.intellij.plugin.query.sqlref.index;

import com.idi.intellij.plugin.query.sqlref.index.listeners.ProgressChangedListener;
import com.idi.intellij.plugin.query.sqlref.index.listeners.XmlVisitorListener;
import com.idi.intellij.plugin.query.sqlref.util.SQLRefApplication;
import com.idi.intellij.plugin.query.sqlref.util.SQLRefNamingUtil;
import com.idi.intellij.plugin.query.sqlref.util.SQLRefXmlVisitor;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
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
	private ProgressChangedListener progressChangedListener;

	public SQLRefXmlFileIndex(Project project) {
		this.project = project;
	}

	public SQLRefXmlFileIndex(Project project, ProgressChangedListener progressChangedListener) {
		this.project = project;
		this.progressChangedListener = progressChangedListener;
	}

	public synchronized void indexSQLRef() {
		Collection<VirtualFile> xmlFiles = FileTypeIndex.getFiles(XmlFileType.INSTANCE, GlobalSearchScope.projectScope(project));
		for (final VirtualFile xmlFile : xmlFiles) {
			scanXmlFile(xmlFile);
			if (progressChangedListener != null) {
				progressChangedListener.changeMade(true);
			}
		}
	}

	private void scanXmlFile(final VirtualFile xmlFile) {
		String xmlFileName = xmlFile.getName();
		if (SQLRefNamingUtil.isMatchFileName(xmlFileName)) {
//			showVFInfo(xmlFile);
//			final StubIndexKey<String, PsiElement> indexKey = StubIndexKey.createIndexKey(xmlFileName);
			XmlVisitorListener xmlVisitorListener = new XmlVisitorListener() {
				@Override
				public void foundValidRefId(String refID, PsiElement xmlAttributeElement) {
					ServiceManager.getService(project, SQLRefRepository.class).addXmlFileInformationToRepository(refID, xmlFile, xmlAttributeElement);
				}
			};
			SQLRefXmlVisitor.getInstance(project).setXmlVisitorListener(xmlVisitorListener).setInspector(false);
			SQLRefXmlVisitor.getInstance(project).visitFile(SQLRefApplication.getPsiFileFromVirtualFile(xmlFile, project));

			/*if (remove) {
				ServiceManager.getService(project, SQLRefRepository.class).removeXmlFromRepository(indexKey);
			} else {
			}*/
		}
	}

	private void showVFInfo(VirtualFile virtualFile) {
		logger.info("showVFInfo(): XML");
		logger.info("virtualFile: isValid= " + virtualFile.isValid());
		logger.info("virtualFile: isSymLink=" + virtualFile.isSymLink());
		logger.info("virtualFile: url= " + virtualFile.getUrl());
		logger.info("virtualFile: fileType= " + virtualFile.getFileType());
	}


}
