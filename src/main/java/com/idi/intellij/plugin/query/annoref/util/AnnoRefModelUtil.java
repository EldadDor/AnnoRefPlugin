/*
 * User: eldad.Dor
 * Date: 06/07/2014 11:16
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.util;

import com.google.common.collect.Lists;
import com.idi.intellij.plugin.query.annoref.index.SQLRefRepository;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefConfigSettings;
import com.idi.intellij.plugin.query.annoref.repo.model.SQLRefReference;
import com.intellij.find.findUsages.FindUsagesOptions;
import com.intellij.find.findUsages.PsiElement2UsageTargetComposite;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.lang.Language;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.filters.position.XmlTokenTypeFilter;
import com.intellij.psi.impl.cache.CacheManager;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.scope.processor.FilterElementProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTokenType;
import com.intellij.usages.UsageTarget;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author eldad
 * @date 06/07/2014
 */
public class AnnoRefModelUtil {
	private static final Logger log = Logger.getInstance(AnnoRefModelUtil.class.getName());

	public static UsageTarget[] getAnnoRefTargets(PsiElement psiElement) {
		final SQLRefReference sqlRefReference = isValidAnnoRef(psiElement);
		if (sqlRefReference != null) {
			return getTargetsForSQLReference(psiElement, sqlRefReference);
		}
		return UsageTarget.EMPTY_ARRAY;
	}

	public static SQLRefReference isValidAnnoRef(PsiElement psiElement) {
		final Language language = psiElement.getLanguage();
		final Project project = psiElement.getProject();
		if (language instanceof JavaLanguage) {
			if (psiElement instanceof PsiModifierList) {
				PsiAnnotation[] annotations = ((PsiAnnotationOwner) psiElement).getAnnotations();
				for (PsiAnnotation annotation : annotations) {
					return getValidAnnoReference(annotation, project);
				}
			}
			if (psiElement instanceof PsiAnnotation) {
				return getValidAnnoReference((PsiAnnotation) psiElement, project);
			}
			if (psiElement instanceof PsiJavaToken && ((PsiJavaToken) psiElement).getTokenType().toString().equals("STRING_LITERAL")) {
				return getValidAnnoReference(StringUtils.cleanQuote(psiElement.getText()), project);
			}
		}
		if (language instanceof XMLLanguage) {
			if (SQLRefNamingUtil.isPropitiousXmlFile(psiElement.getContainingFile())) {
				final boolean isXmlTokenValid = new FilterElementProcessor(new XmlTokenTypeFilter(XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN)).execute(psiElement);
				if (isXmlTokenValid) {
					final String sqlRefKey = getSqlRefKey(psiElement);
					if (sqlRefKey != null) {
						SQLRefReference sqlRefReferenceForID = ServiceManager.getService(psiElement.getProject(), SQLRefRepository.class).getSQLRefReferenceForID(sqlRefKey);
						if (sqlRefReferenceForID != null) {
							if (!sqlRefReferenceForID.getClassAnnoElements().isEmpty()) {
								return sqlRefReferenceForID;
							}
						}
					}
				}
			}
		}
		return null;
	}

	public static String isValidMethodExpression(PsiElement psiElement) {
		if (psiElement instanceof PsiMethodCallExpression) {
			final PsiReferenceExpression methodExpression = ((PsiMethodCallExpression) psiElement).getMethodExpression();
			if (methodExpression != null && methodExpression.getQualifierExpression() != null && methodExpression.getQualifierExpression().getType() != null) {
				final PsiType type = methodExpression.getQualifierExpression().getType();
				if (type instanceof PsiClassReferenceType) {
					final PsiClassReferenceType classReferenceType = (PsiClassReferenceType) type;
					if (classReferenceType.getReference() != null) {
						final Project project = psiElement.getProject();
						if (AnnoRefConfigSettings.getInstance(project).getAnnoRefState().ANNOREF_UTIL_CLASS_FQN.equals(classReferenceType.getReference().getQualifiedName())) {
							if (((PsiCall) psiElement).getArgumentList().getExpressions().length == 1) {
								final PsiExpression psiExpression = ((PsiCall) psiElement).getArgumentList().getExpressions()[0];
								return String.valueOf(((PsiLiteral) psiExpression).getValue());
							}
						}
					}
				}
			}
		}
		return null;
	}


	public static SQLRefReference getAnnoRefIdFromMethodCallExpressionElement(PsiElement psiElement) {
		if (psiElement instanceof PsiMethodCallExpression) {
			final PsiReferenceExpression methodExpression = ((PsiMethodCallExpression) psiElement).getMethodExpression();
			if (methodExpression != null && methodExpression.getQualifierExpression() != null &&
					methodExpression.getQualifierExpression().getType() != null) {
				final PsiType type = methodExpression.getQualifierExpression().getType();
				final PsiClassReferenceType classReferenceType = (PsiClassReferenceType) type;
				if (type instanceof PsiClassReferenceType && classReferenceType.getReference() != null) {
					final Project project = psiElement.getProject();
					if (AnnoRefConfigSettings.getInstance(project).getAnnoRefState().ANNOREF_UTIL_CLASS_FQN.equals(classReferenceType.getReference().getQualifiedName())) {
						if (((PsiCall) psiElement).getArgumentList().getExpressions().length == 1) {
							final PsiExpression psiExpression = ((PsiCall) psiElement).getArgumentList().getExpressions()[0];
							final String refId = String.valueOf(((PsiLiteral) psiExpression).getValue());
							final SQLRefReference refReference = ServiceManager.getService(project, SQLRefRepository.class).getSQLRefReferenceForID(refId);
							return refReference;
						} else {
							throw new IllegalArgumentException(String.format("Argument %s for parameter of %s.%s must not be null",
									new Object[]{"0", classReferenceType.getReference().getQualifiedName(), methodExpression.getQualifiedName()}));
						}
					} else {
						log.warn("getAnnoRefIdFromMethodCallExpressionElement(): PsiElement ClassReferenceType FQA=" + classReferenceType.getReference().getQualifiedName() + " different" +
								" from ANNOREF_UTIL_CLASS_FQN=" + AnnoRefConfigSettings.getInstance(project).getAnnoRefState().ANNOREF_UTIL_CLASS_FQN);
					}
				} else {
					log.warn("getAnnoRefIdFromMethodCallExpressionElement(): PsiElement ReferenceType=" + classReferenceType);
				}
			} else {
				log.warn("getAnnoRefIdFromMethodCallExpressionElement(): PsiElement is of type=" + psiElement.getClass());
			}
		}
		return null;
	}

	public static String cleanRefIdForPsiElement(PsiElement element) {
		final Language language = element.getLanguage();
		if (language instanceof JavaLanguage) {
			if (element instanceof PsiModifierList) {
				PsiAnnotation[] annotations = ((PsiAnnotationOwner) element).getAnnotations();
				for (PsiAnnotation annotation : annotations) {
					if (AnnoRefConfigSettings.getInstance(element.getProject()).getAnnoRefState().ANNOREF_ANNOTATION_FQN.equals(annotation.getQualifiedName())) {
						String refKey = SQLRefNamingUtil.cleanAnnoRefForName(annotation.getContainingFile(), annotation);
						if (refKey != null) {
							return refKey;
						}
					}
				}
			}
			if (element instanceof PsiAnnotation) {
				final PsiAnnotation psiAnnotation = (PsiAnnotation) element;
				if (AnnoRefConfigSettings.getInstance(element.getProject()).getAnnoRefState().ANNOREF_ANNOTATION_FQN.equals(psiAnnotation.getQualifiedName())) {
					String cleanedAnnoRef = SQLRefNamingUtil.cleanAnnoRefForName(psiAnnotation.getContainingFile(), psiAnnotation);
					if (cleanedAnnoRef != null) {
						return cleanedAnnoRef;
					}
				}
				if (element instanceof PsiJavaToken && ((PsiJavaToken) element).getTokenType().toString().equals("STRING_LITERAL")) {
					final String refKey = StringUtils.cleanQuote(element.getText());
					if (refKey != null) {
						return refKey;
					}
				}
			}
		}
		if (language instanceof XMLLanguage) {
			if (SQLRefNamingUtil.isPropitiousXmlFile(element.getContainingFile())) {
				final boolean isXmlTokenValid = new FilterElementProcessor(new XmlTokenTypeFilter(XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN)).execute(element);
				if (isXmlTokenValid) {
					final String refKey = getSqlRefKey(element);
					if (refKey != null) {
						return refKey;
					}
				}
			}
		}
		return null;
	}


	private static SQLRefReference getValidAnnoReference(final String refId, final Project project) {
		return ServiceManager.getService(project, SQLRefRepository.class).getSQLRefReferenceForID(refId);
	}

	private static SQLRefReference getValidAnnoReference(final PsiAnnotation annotation, final Project project) {
		if (AnnoRefConfigSettings.getInstance(project).getAnnoRefState().ANNOREF_ANNOTATION_FQN.equals(annotation.getQualifiedName())) {
			String cleanedAnnoRef = SQLRefNamingUtil.cleanAnnoRefForName(annotation.getContainingFile(), annotation);
			if (cleanedAnnoRef != null) {
				SQLRefReference sqlRefReferenceForID = ServiceManager.getService(project, SQLRefRepository.class).getSQLRefReferenceForID(cleanedAnnoRef);
				if (!sqlRefReferenceForID.getXmlQueryElements().isEmpty()) {
					return sqlRefReferenceForID;
				}
			}
		}
		return null;
	}

	public static boolean isAnnoRefAnywhereInXml(@NotNull Project project, @NotNull Module module, @NotNull String refId) {
		if (module == null) {
			throw new IllegalArgumentException(String.format("Argument %s for @NotNull parameter of %s.%s must not be null", new Object[]{"0",
					"com.idi.intellij.plugin.query.sqlref.util.AnnoRefModelUtil", "isAnnoRefAnywhereInXml"}));
		}
		GlobalSearchScope scope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module);
		GlobalSearchScope searchScope = GlobalSearchScope.getScopeRestrictedByFileTypes(scope, XmlFileType.INSTANCE);
		PsiFile[] files = CacheManager.SERVICE.getInstance(module.getProject()).getFilesWithWord(refId, Short.valueOf("255"), searchScope, true);

		for (PsiFile file : files) {
			if (((file instanceof XmlFile)) && (SQLRefApplication.getInstance(project, SQLRefDataAccessor.class).isPropitiousXmlFile(file))) {
				return true;
			}
		}
		return false;
	}

	private static UsageTarget[] getTargetsForSQLReference(PsiElement psiElement, SQLRefReference sqlRefReferenceForID) {
		final List<PsiElement> primaryElementList = Lists.newArrayList();
		final List<PsiElement> secondaryElementList = Lists.newArrayList();
		if (psiElement.getLanguage() instanceof JavaLanguage) {
			for (final SmartPsiElementPointer<PsiElement> elementPointer : sqlRefReferenceForID.getXmlSmartPointersElements()) {
				primaryElementList.add(elementPointer.getElement());
			}
		} else if (psiElement.getLanguage() instanceof XMLLanguage) {
			for (final SmartPsiElementPointer<PsiElement> elementPointer : sqlRefReferenceForID.getClassSmartPointersElements()) {
				primaryElementList.add(elementPointer.getElement());
			}
			for (final SmartPsiElementPointer<PsiElement> elementPointer : sqlRefReferenceForID.getUtilClassSmartPointersElements().values()) {
				secondaryElementList.add(elementPointer.getElement());
			}
		}
		PsiElement[] primeElements = new PsiElement[primaryElementList.size()];
		PsiElement[] secondaryElements = new PsiElement[secondaryElementList.size()];
		primaryElementList.toArray(primeElements);
		secondaryElementList.toArray(secondaryElements);
//		return new UsageTarget[]{new PsiElement2UsageTargetAdapter(primeElements[0])};
//		return new UsageTarget[new PsiElement2UsageTargetAdapter(primeElements[0],new FindUsagesOptions(psiElement.getProject())};
		return new UsageTarget[]{new PsiElement2UsageTargetComposite(primeElements, secondaryElements, new FindUsagesOptions(psiElement.getProject()))};
	}


	private static String getSqlRefKey(PsiElement element) {
		if (element.getText().contains("1.0") || element.getText().contains("UTF-8")) {
			return null;
		}
		return StringUtil.stripQuotesAroundValue(element.getText());
	}
}
