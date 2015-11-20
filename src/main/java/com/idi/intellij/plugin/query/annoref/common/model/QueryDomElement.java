package com.idi.intellij.plugin.query.annoref.common.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;

/**
 * Created by EAD-MASTER on 7/30/2014.
 */
public interface QueryDomElement extends AnnoRefDomElement {
	@Attribute("refId")
	@Required
	GenericAttributeValue<String> getRefId();

	@Attribute("update")
	@Required(value = false)
	GenericAttributeValue<String> getUpdate();


}
