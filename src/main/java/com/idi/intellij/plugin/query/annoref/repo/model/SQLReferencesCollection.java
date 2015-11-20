package com.idi.intellij.plugin.query.annoref.repo.model;

import com.google.common.collect.Lists;
import com.intellij.openapi.components.ProjectComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 8/2/13
 * Time: 2:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class SQLReferencesCollection implements ProjectComponent {
	private List<SQLRefReference> refReferences = Lists.newArrayList();

	public void addSQLReference(SQLRefReference sqlRefReference) {
		refReferences.add(sqlRefReference);
	}

	@Override
	public void projectOpened() {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void projectClosed() {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void initComponent() {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void disposeComponent() {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@NotNull
	@Override
	public String getComponentName() {
		return getClass().getName();
	}
}
