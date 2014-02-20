package com.idi.intellij.plugin.query.sqlref.index.listeners;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 8/10/13
 * Time: 10:47 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ProgressChangedListener {
	void changeMade(boolean isChanged);

	void failedProcess();
}
