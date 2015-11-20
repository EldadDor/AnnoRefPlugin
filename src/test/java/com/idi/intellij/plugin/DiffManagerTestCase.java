/*
 * User: eldad.Dor
 * Date: 19/02/2015 15:48
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin;

import com.idi.intellij.plugin.query.annoref.util.SPDiffManager;
import com.intellij.testFramework.LightIdeaTestCase;

/**
 * @author eldad
 * @date 19/02/2015
 */
public class DiffManagerTestCase extends LightIdeaTestCase {
	public void testDiffManager() throws Exception {
		final SPDiffManager diffManager = new SPDiffManager(getProject());
		String sql1 = "SELECT *\n" +
				"FROM M_SRV_PRN M\n" +
				"WHERE M.PRINT_NR=1;";
		String sql2 = "SELECT *\n" +
				"FROM M_SRV_PRN M\n" +
				"WHERE M.PRINT_NR=2;";
		diffManager.diff(sql1, sql2);

	}
}