package com.idi.intellij.plugin.query.sqlref.index;

import com.idi.intellij.plugin.query.sqlref.index.listeners.ClassVisitorListener;
import com.idi.intellij.plugin.query.sqlref.index.listeners.ProgressChangedListener;
import com.idi.intellij.plugin.query.sqlref.persist.SQLRefConfigSettings;
import com.idi.intellij.plugin.query.sqlref.util.SQLRefApplication;
import com.idi.intellij.plugin.query.sqlref.util.SQLRefNamingUtil;
import com.intellij.ide.highlighter.JavaFileType;
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
 * Time: 7:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class SQLRefClassFileIndex {
	private static final Logger logger = Logger.getInstance(SQLRefClassFileIndex.class.getName());
	private Project project;
	private ProgressChangedListener progressChangedListener;

	public SQLRefClassFileIndex(Project project) {
		this.project = project;
	}

	public SQLRefClassFileIndex(Project project, ProgressChangedListener progressChangedListener) {
		this.project = project;
		this.progressChangedListener = progressChangedListener;
	}

	public void indexSQLRef() {
		logger.info("indexSQLRef():");
		Collection<VirtualFile> classFiles = FileTypeIndex.getFiles(JavaFileType.INSTANCE, GlobalSearchScope.projectScope(project));
		for (final VirtualFile classFile : classFiles) {
			scanClassFile(classFile);
			if (progressChangedListener != null) {
				progressChangedListener.changeMade(true);
			}
		}
	}

	private void scanClassFile(VirtualFile classFile) {
		String classFileName = classFile.getName();
//		logger.info("scanClassFile(): classFileName="+classFileName);
		final PsiElement[] annoElement = new PsiElement[1];
		String sqlRefIdInClass = SQLRefNamingUtil.isPropitiousClassFile(SQLRefApplication.getPsiFileFromVirtualFile(classFile, project), new ClassVisitorListener() {
			@Override
			public void foundValidAnnotation(PsiElement classRef) {
				annoElement[0] = classRef;
			}
		}, SQLRefConfigSettings.getInstance(project).getSqlRefState().ANNOREF_ANNOTATION_FQN);
		if (sqlRefIdInClass != null) {
//				showVFInfo(classFile);
//			logger.info("scanClassFile(): sqlRefIdInClass=" + sqlRefIdInClass);
//				final ID classFileNameKey = StubIndexKey.createIndexKey(classFileName);
			ServiceManager.getService(project, SQLRefRepository.class).addClassFileInformationToRepository(sqlRefIdInClass, classFile, annoElement[0]);
		}
		/*if (sqlRefIdInClass != null) {
			if (remove) {
				ServiceManager.getService(project, SQLRefRepository.class).removeClassFromRepository(classFileNameKey);
			} else {
			}
		}*/
	}

	private void showVFInfo(VirtualFile virtualFile) {
		logger.info("showVFInfo(): CLASS");
		logger.info("virtualFile: isValid= " + virtualFile.isValid());
		logger.info("virtualFile: isSymLink=" + virtualFile.isSymLink());
		logger.info("virtualFile: url= " + virtualFile.getUrl());
		logger.info("virtualFile: fileType= " + virtualFile.getFileType());
	}


}
