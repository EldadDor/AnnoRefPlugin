package com.idi.intellij.plugin.query.annoref.inspection;

import com.idi.intellij.plugin.query.annoref.filter.XmlAnnoRefTokenTypeFilter;
import com.idi.intellij.plugin.query.annoref.util.AnnoRefModelUtil;
import com.intellij.find.impl.HelpID;
import com.intellij.lang.LangBundle;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.xml.XmlFindUsagesProvider;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.scope.processor.FilterElementProcessor;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlToken;
import com.intellij.psi.xml.XmlTokenType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by EAD-MASTER on 2/22/14.
 */
//public class AnnoRefInXmlRefUsagesProvider implements FindUsagesProvider {
public class AnnoRefInXmlRefUsagesProvider extends XmlFindUsagesProvider {
	private static final Logger log = Logger.getInstance(AnnoRefInXmlRefUsagesProvider.class.getName());

	@Nullable
	@Override
	public WordsScanner getWordsScanner() {
		return null;
	}

	@Override
	public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
//		log.info("canFindUsagesFor():");
		final boolean b = AnnoRefModelUtil.isValidAnnoRef(psiElement) != null;
		return b;
	}

	@Nullable
	@Override
	public String getHelpId(@NotNull PsiElement psiElement) {
		return HelpID.FIND_CLASS_USAGES;
	}

	@NotNull
	@Override
	public String getType(@NotNull PsiElement element) {
		if (element instanceof XmlAttributeValue) {
			return ((XmlAttributeValue) element).getValue();
		}
		if (element instanceof XmlToken) {
			return element.getText();
		}
		return element.getText();
	}

	@NotNull
	@Override
	public String getDescriptiveName(@NotNull PsiElement element) {
		if (element instanceof XmlAttributeValue) {
			return ((XmlAttributeValue) element).getValue();
		} else {
			if (element instanceof XmlToken) {
				return element.getText();
			}
		}
		return element.getText();
	}

	@NotNull
	@Override
	public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
		if (element instanceof XmlAttribute) {
			return LangBundle.message("xml.terms.attribute");
		}
		if (element instanceof XmlToken) {
			final FilterElementProcessor elementProcessor = new FilterElementProcessor(new XmlAnnoRefTokenTypeFilter(XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN));
			final boolean isXmlTokenValid = elementProcessor.execute(element);
			if (isXmlTokenValid) {
				return "XML ->" + element.getText();
			}
			return element.getText();
		} else {
			throw new IllegalArgumentException("Cannot get type for " + element);
		}
	}
}
