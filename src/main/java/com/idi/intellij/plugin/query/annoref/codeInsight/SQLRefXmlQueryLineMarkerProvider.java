package com.idi.intellij.plugin.query.annoref.codeInsight;

import com.google.common.collect.Lists;
import com.idi.intellij.plugin.query.annoref.common.SQLRefConstants;
import com.idi.intellij.plugin.query.annoref.filter.XmlAnnoRefTokenTypeFilter;
import com.idi.intellij.plugin.query.annoref.index.SQLRefRepository;
import com.idi.intellij.plugin.query.annoref.repo.model.SQLRefReference;
import com.idi.intellij.plugin.query.annoref.util.SQLRefNamingUtil;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.psi.impl.smartPointers.PsiClassReferenceTypePointerFactory;
import com.intellij.psi.scope.processor.FilterElementProcessor;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTokenType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 14/02/2011
 * Time: 21:50:58
 * To change this template use File | Settings | File Templates.
 */
public class SQLRefXmlQueryLineMarkerProvider implements LineMarkerProvider {
	private static final Logger logger = Logger.getInstance(SQLRefXmlQueryLineMarkerProvider.class.getName());

	@Override
	public LineMarkerInfo<PsiElement> getLineMarkerInfo(@NotNull PsiElement element) {
		try {
			if (SQLRefNamingUtil.isPropitiousXmlFile(element.getContainingFile())) {
				if (element instanceof XmlAttributeValue) {
					final FilterElementProcessor elementProcessor = new FilterElementProcessor(new XmlAnnoRefTokenTypeFilter(XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN));
					final boolean isXmlTokenValid = elementProcessor.execute(element);
					if (isXmlTokenValid) {
						elementProcessor.getResults();
						final String sqlRefKey = getSqlRefKey(element);
						if (sqlRefKey != null) {
							SQLRefReference sqlRefReferenceForID = ServiceManager.getService(element.getProject(), SQLRefRepository.class).getSQLRefReferenceForID(sqlRefKey);
							if (sqlRefReferenceForID == null) {
								return null;
							}
							if (sqlRefReferenceForID.getClassAnnoElements().isEmpty() && sqlRefReferenceForID.getUtilClassSmartPointersElements().isEmpty()) {
								return null;
							}
							final List<PsiElement> psiElements = Lists.newArrayList();
							for (final SmartPsiElementPointer<PsiElement> elementPointer : sqlRefReferenceForID.getClassSmartPointersElements()) {
								psiElements.add(elementPointer.getElement());
							}
							for (final SmartPsiElementPointer<PsiElement> elementPointer : sqlRefReferenceForID.getUtilClassSmartPointersElements().values()) {
								psiElements.add(elementPointer.getElement());
							}
							if (psiElements.isEmpty()) {
								return null;
							}
							PsiElement[] elements = new PsiElement[psiElements.size()];
							psiElements.toArray(elements);
							final PsiClassReferenceTypePointerFactory referenceTypePointerFactory = new PsiClassReferenceTypePointerFactory();
							return SQLRefIdLineMarkerInfo.create(element, elements, SQLRefConstants.ANNO_REF_COMPONENT_ICON_CLASS, null);
						}
					} else {
						return null;       //todo   place some inspection here?
					}
				}
			}
		} catch (Exception e) {
			logger.error("getLineMarkerInfo(): ERROR=" + e.getMessage(), e);
		}
		return null;
	}

	private String getSqlRefKey(PsiElement element) {
		if (element.getText().contains("1.0") || element.getText().contains("UTF-8")) {
			return null;
		}
		return StringUtil.stripQuotesAroundValue(element.getText());
	}

	@Override
	public void collectSlowLineMarkers(@NotNull List<PsiElement> elements, @NotNull Collection<LineMarkerInfo> result) {
//		logger.info("collectSlowLineMarkers():");
		//To change body of implemented methods use File | Settings | File Templates.
	}
}
