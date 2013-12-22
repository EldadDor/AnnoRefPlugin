package com.idi.intellij.plugin.sqlref;

import com.idi.intellij.plugin.query.sqlref.persist.SQLRefSettings;
import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;
import org.junit.Test;


/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 6/21/13
 * Time: 6:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class SQLRefStoragePersistTest {
//	    private final static Logger logger= Logger.getInstance(SQLRefStoragePersistTest.class.getName());


	@Test
	public void sqlRefConfigSettingsTest() {
		SQLRefSettings configSettings = new SQLRefSettings();
		Element serialize = XmlSerializer.serialize(configSettings);
		System.out.println("serialize = " + serialize);
	}
}
