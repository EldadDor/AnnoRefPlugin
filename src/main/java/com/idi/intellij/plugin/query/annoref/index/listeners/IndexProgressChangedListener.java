package com.idi.intellij.plugin.query.annoref.index.listeners;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 8/10/13
 * Time: 10:47 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IndexProgressChangedListener {
	void changeMade(boolean isChanged);

	void failedProcess(String errorMessage);

	void finishedProcess();
}
