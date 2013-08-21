package com.idi.intellij.plugin.query.sqlref.index;

import com.idi.intellij.plugin.query.sqlref.index.listeners.ClassVisitorListener;
import com.idi.intellij.plugin.query.sqlref.index.listeners.ProgressChangedListener;
import com.idi.intellij.plugin.query.sqlref.util.SQLRefApplication;
import com.idi.intellij.plugin.query.sqlref.util.SQLRefNamingUtil;
import com.intellij.ide.highlighter.JavaFileType;
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
import com.intellij.util.indexing.ID;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 7/5/13
 * Time: 7:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class SQLRefClassFileIndex {
	private static final Logger logger = LoggerFactory.getInstance().getLoggerInstance(SQLRefClassFileIndex.class.getName());
	private Project project;

	public SQLRefClassFileIndex(Project project) {
		this.project = project;
	}

	public void indexSQLRef() {
		logger.info("indexSQLRef():");
		Collection<VirtualFile> classFiles = FileTypeIndex.getFiles(JavaFileType.INSTANCE, GlobalSearchScope.projectScope(project));
		for (final VirtualFile classFile : classFiles) {
			scanClassFile(classFile);
		}
	}

	public void scanModuleClassFiles(Module module, ProgressChangedListener progressIndicator, boolean remove) {
		Collection<VirtualFile> classFiles = FileTypeIndex.getFiles(JavaFileType.INSTANCE, GlobalSearchScope.projectScope(project));
		for (VirtualFile classFile : classFiles) {
			scanClassFile(classFile);
			progressIndicator.changeMade(true);

		}
	}

	private void scanClassFile(VirtualFile classFile) {
		String classFileName = classFile.getName();
		final PsiElement[] annoElement = new PsiElement[1];
		String sqlRefIdInClass = SQLRefNamingUtil.isPropitiousClassFile(SQLRefApplication.getPsiFileFromVirtualFile(classFile, project), new ClassVisitorListener() {
			@Override
			public void foundValidAnnotation(PsiElement classRef) {
				annoElement[0] = classRef;
			}
		});
		if (sqlRefIdInClass != null) {
			final ID classFileNameKey = StubIndexKey.createIndexKey(classFileName);
			ServiceManager.getService(project, SQLRefRepository.class).addClassFileInformationToRepository(sqlRefIdInClass, classFileNameKey, classFile, annoElement[0]);
		}

		/*if (sqlRefIdInClass != null) {
			if (remove) {
				ServiceManager.getService(project, SQLRefRepository.class).removeClassFromRepository(classFileNameKey);
			} else {
			}
		}*/
	}


}
