package com.idi.intellij.plugin.query.sqlref.index;

import com.idi.intellij.plugin.query.sqlref.index.listeners.ProgressChangedListener;
import com.idi.intellij.plugin.query.sqlref.index.listeners.XmlVisitorListener;
import com.idi.intellij.plugin.query.sqlref.util.SQLRefApplication;
import com.idi.intellij.plugin.query.sqlref.util.SQLRefNamingUtil;
import com.idi.intellij.plugin.query.sqlref.util.SQLRefXmlVisitor;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.idea.LoggerFactory;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndexKey;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 7/5/13
 * Time: 7:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class SQLRefXmlFileIndex {

	private static final Logger logger = LoggerFactory.getInstance().getLoggerInstance(SQLRefXmlFileIndex.class.getName());


	private Project project;

	public SQLRefXmlFileIndex(Project project) {
		this.project = project;
	}

	public synchronized void indexSQLRef() {
		Collection<VirtualFile> xmlFiles = FileTypeIndex.getFiles(XmlFileType.INSTANCE, GlobalSearchScope.projectScope(project));
		for (final VirtualFile xmlFile : xmlFiles) {
			scanXmlFile(xmlFile, false);
		}
	}

	public void scanModuleXmlFiles(Module module, ProgressChangedListener progressIndicator, boolean remove) {
		/*if (remove) {
			ServiceManager.getService(project, SQLRefRepository.class).
		}*/
		Collection<VirtualFile> xmlFiles = FileTypeIndex.getFiles(XmlFileType.INSTANCE, GlobalSearchScope.moduleScope(module));
		for (VirtualFile xmlFile : xmlFiles) {
			scanXmlFile(xmlFile, remove);
			progressIndicator.changeMade(true);
		}
	}

	private void scanXmlFile(final VirtualFile xmlFile, boolean remove) {
		String xmlFileName = xmlFile.getName();
		if (SQLRefNamingUtil.isMatchFileName(xmlFileName)) {
			final StubIndexKey<String, PsiElement> indexKey = StubIndexKey.createIndexKey(xmlFileName);
			XmlVisitorListener xmlVisitorListener = new XmlVisitorListener() {
				@Override
				public void foundValidRefId(String refID, PsiElement xmlAttributeElement) {
					ServiceManager.getService(project, SQLRefRepository.class).addXmlFileInformationToRepository(refID, indexKey, xmlFile, xmlAttributeElement);
				}
			};
			SQLRefXmlVisitor.getInstance(project).setXmlVisitorListener(xmlVisitorListener);
			SQLRefXmlVisitor.getInstance(project).visitFile(SQLRefApplication.getPsiFileFromVirtualFile(xmlFile, project));
			/*if (remove) {
				ServiceManager.getService(project, SQLRefRepository.class).removeXmlFromRepository(indexKey);
			} else {
			}*/
		}
	}


}
