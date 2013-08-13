package com.idi.intellij.plugin.query.sqlref.util;

import com.intellij.idea.LoggerFactory;
import com.intellij.openapi.diagnostic.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 6/2/13
 * Time: 11:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class SQLRefPluginManager {

	private final static Logger logger = LoggerFactory.getInstance().getLoggerInstance(SQLRefPluginManager.class.getName());

	public static Map<String, SQLRefApplication> projectBasePathMap = new ConcurrentHashMap<String, SQLRefApplication>(1);

	public static SQLRefApplication getRequestedIdeaInstanceForProjectBasePath(String guessedProject) {
		if (projectBasePathMap.containsKey(guessedProject)) {
			return projectBasePathMap.get(guessedProject);
		}
		return null;
	}

}