/*
 * User: eldad
 * Date: 23/01/11 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.component;

import com.idi.intellij.plugin.query.annoref.model.ReferenceCollectionManager;
import com.idi.intellij.plugin.query.annoref.util.AnnRefApplication;
import com.idi.intellij.plugin.query.annoref.util.SQLRefDataAccessor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerAdapter;
import com.intellij.openapi.vfs.VirtualFile;

/**
 *
 */
public class SQLRefFileEditorListener extends FileEditorManagerAdapter {
	private static final Logger LOGGER = Logger.getInstance(SQLRefFileEditorListener.class.getName());

	@Override
	public void fileOpened(FileEditorManager source, VirtualFile file) {
		AnnRefApplication.getInstance(source.getProject(), SQLRefDataAccessor.class).findNonCorrelatingSQLRefInXmlFile(file.getName(), true);
		super.fileOpened(source, file);
	}

	@Override
	public void fileClosed(FileEditorManager source, VirtualFile file) {
		if (ReferenceCollectionManager.getInstance(source.getProject()).getQueriesCollection(file.getName(), false) != null) {
			ReferenceCollectionManager.getInstance(source.getProject()).getQueriesCollection(file.getName(), false).setOpen(false);
		}
		/*if (	SQLRefApplication.getInstance(ReferenceCollectionManager.class).getQueriesCollection(file.getName(), false) != null) {
			SQLRefApplication.getInstance(ReferenceCollectionManager.class).getQueriesCollection(file.getName(), false).setOpen(false);
		}*/
		super.fileClosed(source, file);    //To change body of overridden methods use File | Settings | File Templates.
	}


}