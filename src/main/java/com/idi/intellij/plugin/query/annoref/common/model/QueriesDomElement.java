package com.idi.intellij.plugin.query.annoref.common.model;

import com.intellij.util.xml.DefinesXml;

import java.util.List;

/**
 * Created by EAD-MASTER on 7/30/2014.
 */
@DefinesXml
public interface QueriesDomElement extends AnnoRefDomElement {
	List<QueryDomElement> getQuery();
}
