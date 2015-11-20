package com.idi.intellij.plugin.query.annoref.util;

import com.idi.intellij.plugin.query.annoref.model.SQLRefReference;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.NavigatableFileEditor;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 06/11/2010
 * Time: 23:06:02
 * To change this template use File | Settings | File Templates.
 */
public class ReferenceNavigator {

	private static ReferenceNavigator instance;

	public static ReferenceNavigator getInstance() {
		if (instance == null) {
			instance = new ReferenceNavigator();
		}
		return instance;
	}

	public boolean navigateToClassReference(Project project, @NotNull SQLRefReference reference) {
		VirtualFile virtualFile = reference.containingClassVirtualFile(null);
		if (virtualFile != null) {
			FileEditor[] fileEditors = FileEditorManager.getInstance(SQLRefApplication.getInstance(project, SQLRefDataAccessor.class).getProject()).openFile(virtualFile, true);
			for (FileEditor fileEditor : fileEditors) {
				if (fileEditor instanceof TextEditor) {
					((NavigatableFileEditor) fileEditor).navigateTo(reference.classLocation(project, virtualFile));
					((TextEditor) fileEditor).getEditor().getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
					return true;
				}
			}
		}
		return false;
	}

	public boolean navigateToXmlReference(Project project, @NotNull com.idi.intellij.plugin.query.annoref.repo.model.SQLRefReference reference) {
//		FileEditor[] fileEditors = FileEditorManager.getInstance(SQLRefApplication.getInstance(project, SQLRefDataAccessor.class).getProject()).openFile(reference.containingVirtualFile(), true);
		/*for (FileEditor fileEditor : fileEditors) {
			if (fileEditor instanceof TextEditor) {
//				((NavigatableFileEditor) fileEditor).navigateTo(reference.getXmlQueryElements().get(0));
				((TextEditor) fileEditor).getEditor().getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
				return true;
			}
		}*/
		return false;
	}


}
