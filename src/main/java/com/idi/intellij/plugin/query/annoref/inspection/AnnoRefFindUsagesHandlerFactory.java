package com.idi.intellij.plugin.query.annoref.inspection;

import com.google.common.collect.Lists;
import com.idi.intellij.plugin.query.annoref.common.AnnoRefPsiElement;
import com.idi.intellij.plugin.query.annoref.common.AnnoRefXmlPsiElement;
import com.idi.intellij.plugin.query.annoref.repo.model.SQLRefReference;
import com.idi.intellij.plugin.query.annoref.util.AnnoRefModelUtil;
import com.idi.intellij.plugin.query.annoref.util.StringUtils;
import com.intellij.find.findUsages.*;
import com.intellij.ide.DataManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaToken;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlToken;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by EAD-MASTER on 2/22/14.
 */
public class AnnoRefFindUsagesHandlerFactory extends FindUsagesHandlerFactory {
	private static final Logger log = Logger.getInstance(AnnoRefFindUsagesHandlerFactory.class.getName());

	@Override
	public boolean canFindUsages(@NotNull PsiElement element) {
//		log.info("canFindUsages():");
		if (element instanceof XmlToken) {
			final boolean b = AnnoRefModelUtil.isValidAnnoRef(element) != null;
			return b;
		}
		if (element instanceof PsiJavaToken && ((PsiJavaToken) element).getTokenType() == JavaTokenType.STRING_LITERAL) {
			final boolean b = AnnoRefModelUtil.isValidAnnoRef(element) != null;
			return b;
		}
		return false;
		/*final Project project = element.getProject();
		if (SQLRefNamingUtil.isPropitiousXmlFile(element.getContainingFile()) && element instanceof XmlToken
				&& ((XmlToken) element).getTokenType().equals(XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN)) {
			if (!StringUtil.stripQuotesAroundValue(element.getText()).isEmpty() && ServiceManager.getService(project, SQLRefRepository.class).isValidForReference(element.getText())) {
				return true;
			}
		}
		if (element instanceof PsiJavaToken) {
			if (AnnoRefConfigSettings.getInstance(project).getAnnoRefState().
					ANNOREF_ANNOTATION_FQN.equals(SQLRefNamingUtil.stripDoubleQuotes(element.getText()))) {
				return ServiceManager.getService(project, SQLRefRepository.class).isValidForReference(element.getText());
			}
		}
		return false;*/
	}

	@Nullable
	@Override
	public FindUsagesHandler createFindUsagesHandler(@NotNull final PsiElement element, boolean forHighlightUsages) {
//		log.info("createFindUsagesHandler():");
		final SQLRefReference sqlRefReferenceForID = AnnoRefModelUtil.isValidAnnoRef(element);
		if (sqlRefReferenceForID.hasSomeElements()) {
			final List<PsiElement> primaryElementList = Lists.newArrayList();
			for (final SmartPsiElementPointer<PsiElement> xmlSmartPointersElement : sqlRefReferenceForID.getXmlSmartPointersElements()) {
				primaryElementList.add(xmlSmartPointersElement.getElement());
			}
			for (final SmartPsiElementPointer<PsiElement> classSmartPointerElement : sqlRefReferenceForID.getClassSmartPointersElements()) {
				primaryElementList.add(classSmartPointerElement.getElement());
			}
			for (final SmartPsiElementPointer<PsiElement> utilClassSmartPointerElement : sqlRefReferenceForID.getUtilClassSmartPointersElements().values()) {
				primaryElementList.add(utilClassSmartPointerElement.getElement());
			}
			final PsiElement[] psiElements = new PsiElement[sqlRefReferenceForID.collectiveSize()];
			primaryElementList.toArray(psiElements);
			final AnnoRefPsiElement annoRefPsiElement = new AnnoRefXmlPsiElement(element, psiElements);
			return new AnnoRefFindUsagesHandler(annoRefPsiElement.getWrappedElement(), annoRefPsiElement, annoRefPsiElement.getPsiElements());

//		SQLRefReference sqlRefReferenceForID = ServiceManager.getService(element.getProject(), SQLRefRepository.class).getSQLRefReferenceForID(element.getText());
//		if (((XmlToken) element).getTokenType().getLanguage().equals(XMLLanguage.INSTANCE)) {
/*			final List<PsiElement> primaryElementList = Lists.newArrayList();
			if (element instanceof PsiAnnotation) {
				final List<SmartPsiElementPointer<PsiElement>> xmlSmartPointersElements = sqlRefReferenceForID.getXmlSmartPointersElements();
				if (!xmlSmartPointersElements.isEmpty()) {
					final PsiElement[] psiElements = new PsiElement[sqlRefReferenceForID.getClassSmartPointersElements().size()];
					for (final SmartPsiElementPointer<PsiElement> xmlSmartPointersElement : xmlSmartPointersElements) {
						primaryElementList.add(xmlSmartPointersElement.getElement());
					}
					primaryElementList.toArray(psiElements);
					final AnnoRefPsiElement annoRefPsiElement = new AnnoRefXmlPsiElement(element, psiElements);
//				return new FindUsagesHandler(element) { } ;
				*//*((FindManagerImpl) FindManager.getInstance(element.getProject())).getFindUsagesManager();
				final PsiElement navigationElement = annoRefPsiElement.getNavigationElement();
				NavigationItem[] navigationItems = new NavigationItem[0];
				navigationItems = new NavigationItem[]{(NavigationItem) navigationElement};*//*
//				((FindManagerImpl) FindManager.getInstance(element.getProject())).getFindUsagesManager().showSettingsAndFindUsages(navigationItems);
					return new AnnoRefFindUsagesHandler(element, annoRefPsiElement, annoRefPsiElement.getPsiElements());
				} else {
					return new AnnoRefFindUsagesHandler(element);
				}
			} else {
				if (!sqlRefReferenceForID.getUtilClassSmartPointersElements().isEmpty() || !sqlRefReferenceForID.getClassSmartPointersElements().isEmpty()) {
					final PsiElement[] psiElements = new PsiElement[sqlRefReferenceForID.getClassSmartPointersElements().size()];
					for (final SmartPsiElementPointer<PsiElement> xmlSmartPointersElement : sqlRefReferenceForID.getClassSmartPointersElements()) {
						primaryElementList.add(xmlSmartPointersElement.getElement());
					}
					for (final SmartPsiElementPointer<PsiElement> xmlSmartPointersElement : sqlRefReferenceForID.getUtilClassSmartPointersElements().values()) {
						primaryElementList.add(xmlSmartPointersElement.getElement());
					}
					primaryElementList.toArray(psiElements);
					final AnnoRefPsiElement annoRefPsiElement = new AnnoRefXmlPsiElement(element, psiElements);
					return new AnnoRefFindUsagesHandler(element, annoRefPsiElement, annoRefPsiElement.getPsiElements()) {
					};
				} else {
					return new AnnoRefFindUsagesHandler(element);
				}
			}*/
//		}
		}
		return null;
	}

	private static class AnnoRefFindUsagesHandler extends FindUsagesHandler {
		private final PsiElement[] annoRefPsiElements;
		private AnnoRefPsiElement annoRefPsiElement;

		protected AnnoRefFindUsagesHandler(@NotNull PsiElement psiElement, PsiElement... secondaryElements) {
			super(psiElement);
			annoRefPsiElements = secondaryElements;
		}

		private AnnoRefFindUsagesHandler(@NotNull PsiElement psiElement, AnnoRefPsiElement annoRefPsiElement, PsiElement[] annoRefPsiElements) {
			super(psiElement);
			this.annoRefPsiElement = annoRefPsiElement;
			this.annoRefPsiElements = annoRefPsiElements;
		}

		@NotNull
		@Override
		public PsiElement[] getSecondaryElements() {
			return annoRefPsiElements;
		}


		@NotNull
		@Override
		public PsiElement[] getPrimaryElements() {
			return super.getPrimaryElements();
		}

		public AnnoRefPsiElement getAnnoRefPsiElement() {
			return annoRefPsiElement;
		}

		@NotNull
		@Override
		public AbstractFindUsagesDialog getFindUsagesDialog(boolean isSingleFile, boolean toShowInNewTab, boolean mustOpenInNewTab) {
			final CommonFindUsagesDialog usagesDialog = new CommonFindUsagesDialog(getPsiElement(), getProject(), getFindUsagesOptions(DataManager.getInstance().getDataContext()),
					toShowInNewTab, mustOpenInNewTab, isSingleFile, this);
			return usagesDialog;
		}


		@Override
		public boolean processElementUsages(@NotNull PsiElement element, @NotNull Processor<UsageInfo> processor, @NotNull FindUsagesOptions options) {
			if (element == null) {
				throw new IllegalArgumentException(String.format("Argument %s for @NotNull parameter of %s.%s must not be null",
						new Object[]{"0", "com.idi.intellij.plugin.query.sqlref.inspection.AnnoRefFindUsagesHandler", "processElementUsages"}));
			}
			if (processor == null) {
				throw new IllegalArgumentException(String.format("Argument %s for @NotNull parameter of %s.%s must not be null",
						new Object[]{"1", "com.idi.intellij.plugin.query.sqlref.inspection.AnnoRefFindUsagesHandler", "processElementUsages"}));
			}
			if (options == null) {
				throw new IllegalArgumentException(String.format("Argument %s for @NotNull parameter of %s.%s must not be null",
						new Object[]{"2", "com.idi.intellij.plugin.query.sqlref.inspection.AnnoRefFindUsagesHandler", "processElementUsages"}));
			}
			return processAnnoRefUsage(element, processor, options, getAnnoRefPsiElement());
//			return super.processElementUsages(element, processor, options) && processAnnoRefUsage(element, processor, options, getAnnoRefPsiElement());
		}

		public boolean processAnnoRefUsage(PsiElement element, @NotNull final Processor<UsageInfo> processor, FindUsagesOptions options, AnnoRefPsiElement annoRefPointer) {
			final Module module = ModuleUtilCore.findModuleForPsiElement(getPsiElement());
			if (module == null) {
				return true;
			}
			GlobalSearchScope scope = module.getModuleWithDependenciesAndLibrariesScope(false);
			return ApplicationManager.getApplication().runReadAction(new Computable<Boolean>() {
				@Override
				public Boolean compute() {
					AnnoRefModelUtil.isAnnoRefAnywhereInXml(getProject(), module, StringUtils.cleanQuote(getAnnoRefPsiElement().getPsiElements()[0].getText()));
					//			processor.process(new UsageInfo(annoRefPointer.getNavigationElement()));
					for (final PsiElement psiElement : annoRefPsiElement.getPsiElements()) {
						final String refKey = AnnoRefModelUtil.cleanRefIdForPsiElement(psiElement);
						processor.process(new UsageInfo(psiElement, false));
					}
					return true;
				}
			});
		}

		@Override
		protected boolean isSearchForTextOccurencesAvailable(@NotNull PsiElement psiElement, boolean isSingleFile) {
			return true;
		}
	}


}

