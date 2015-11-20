package com.idi.intellij.plugin.query.annoref.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.idi.intellij.plugin.query.annoref.index.listeners.ClassVisitorListener;
import com.idi.intellij.plugin.query.annoref.notification.AnnoRefNotifications;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefConfigSettings;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefSettings;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.jam.model.util.JamCommonUtil;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.FilteringProcessor;
import com.intellij.util.Processor;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.idi.intellij.plugin.query.annoref.common.SQLRefConstants.*;

/**
 * Created by IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 25/03/2011
 * Time: 00:46:07
 * To change this template use File | Settings | File Templates.
 */
public class SQLRefNamingUtil {
	private static final Logger logger = Logger.getInstance(SQLRefNamingUtil.class.getName());

	public static Boolean isMatchFileName(String fileName, String regexPattern) {
		Pattern pattern = Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(fileName);
		return matcher.find();
	}

	public static boolean isPropitiousXmlFile(PsiFileSystemItem file) {
		String queriesRegex = AnnoRefConfigSettings.getInstance(file.getProject()).getAnnoRefState().QUERIES_REGEX;
		return file instanceof XmlFile && isMatchFileName(file.getVirtualFile().getPresentableName(), queriesRegex);
	}

	public static String cleanSQLRefAnnotationForValue(PsiElement element) {
		PsiAnnotation sqlRefAnnoValue = PsiTreeUtil.getParentOfType(element, PsiAnnotation.class);
		return cleanSQLRefQuotes(sqlRefAnnoValue);
	}

	//	private static String cleanSQLRefQuotes(PsiElement annoRefValue) {
	private static String cleanSQLRefQuotes(PsiAnnotation annoRefValue) {
		String firstStrip;
		if (annoRefValue != null && annoRefValue.getText().length() > 1 && annoRefValue.getText().contains("=")) {
			firstStrip = StringUtils.stripEnd(annoRefValue.getText().split("=")[1], ")");
		} else {
			return null;
		}
		return stripDoubleQuotes(firstStrip);
	}

	public static String stripDoubleQuotes(String firstStrip) {
		String sndStrip = null;
		if (firstStrip != null) {
			sndStrip = StringUtil.stripQuotesAroundValue(firstStrip);
		}
		if (sndStrip != null && sndStrip.length() > 2 && !StringUtils.containsOnly("'", sndStrip)) {
			return StringUtil.stripQuotesAroundValue(sndStrip).substring(2, sndStrip.length());
		}
		return null;
	}

	public static PsiAnnotation getPropitiousAnnotationForFile(PsiFile psiFile) {
		try {
			if (psiFile instanceof PsiJavaFile) {
				for (PsiElement classChild : psiFile.getChildren()) {
					if (classChild instanceof PsiClass && ((PsiModifierListOwner) classChild).hasModifierProperty(PsiModifier.PUBLIC)) {
						PsiAnnotation psiAnno = AnnotationUtil.findAnnotation(((PsiModifierListOwner) classChild), AnnoRefConfigSettings.getInstance(psiFile.getProject()).
								getAnnoRefState().ANNOREF_ANNOTATION_FQN);
						String qualifiedName = psiAnno.getQualifiedName();
						final String[] sQLRefArray = qualifiedName.split("\\.");
						if (isValidSQLRefId(psiAnno, sQLRefArray)) {
							return psiAnno;
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error("getPropitiousAnnotationForFile():", ex);
		}
		return null;
	}

	public static String isPropitiousClassFile(PsiFile psiFile, ClassVisitorListener visitorListener, final String classFQN) {
		try {

			final PsiClass childOfType = PsiTreeUtil.findChildOfType(psiFile, PsiClass.class);
			if (psiFile instanceof PsiJavaFile && psiFile.getChildren() != null) {
				for (PsiElement classChild : psiFile.getChildren()) {
					if (classChild instanceof PsiClass && ((PsiModifierListOwner) classChild).hasModifierProperty(PsiModifier.PUBLIC)) {
						final PsiModifierListOwner psiModifierListOwner = (PsiModifierListOwner) classChild;

						process((Class<PsiModifierListOwner>) psiModifierListOwner.getClass(), psiFile);
						final PsiAnnotation psiAnno = getPropitiousClassElementAnnotation((PsiClass) classChild, classFQN);
//						((PsiClass) classChild).getMethods()[0].getModifierList().findAnnotation("com.idi.framework.vo.common.vo.annotations.Property");


					/*	final PsiModifierList annoRefChild = ((PsiModifierListOwner) classChild).getModifierList();
						PsiAnnotation psiAnno = AnnotationUtil.findAnnotation(((PsiModifierListOwner) annoRefChild),
								AnnoRefConfigSettings.getInstance(project).getAnnoRefState().ANNOREF_ANNOTATION_FQN);*/
						if (psiAnno != null) {
							String qualifiedName = psiAnno.getQualifiedName();
							if (qualifiedName != null) {
								final String[] sQLRefArray = qualifiedName.split("\\.");
								if (isValidSQLRefId(psiAnno, sQLRefArray)) {
									final String cleanedAnno = cleanAnnoRefForName(psiFile, psiAnno);
									if (cleanedAnno != null) {
										if (visitorListener != null) {
											lookForValidPropertiesInAnnotatedClasses(visitorListener, (PsiClass) classChild, psiAnno, Maps.<String, Map<String, PsiMethod>>newHashMap());
										}
										return cleanedAnno;
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("PsiFile scanning for Propitiously failed : " + e.getMessage(), e);
		}
		return null;
	}

	private static void process(Class<PsiModifierListOwner> classToInspect, PsiFile psiFile) {
		JamCommonUtil.findAnnotatedElements(classToInspect, "", PsiManager.getInstance(psiFile.getProject()), GlobalSearchScope.allScope(psiFile.getProject()),
				new FilteringProcessor<PsiModifierListOwner>(new Condition<PsiModifierListOwner>() {
					@Override
					public boolean value(PsiModifierListOwner psiModifierListOwner) {
						return false;
					}
				}, new Processor<PsiModifierListOwner>() {
					@Override
					public boolean process(PsiModifierListOwner psiModifierListOwner) {
						return false;
					}
				}));
	}

	private static void lookForValidPropertiesInAnnotatedClasses(ClassVisitorListener visitorListener, PsiClass classChild, PsiAnnotation psiAnno,
	                                                             Map<String, Map<String, PsiMethod>> methodsPropertiesMap) {
		visitorListener.foundValidAnnotation(psiAnno);
		final AnnoRefSettings annoRefState = AnnoRefConfigSettings.getInstance(classChild.getProject()).getAnnoRefState();
		if (annoRefState.ENABLE_SQL_TO_MODEL_VALIDATION) {
			final PsiMethod[] allMethods = classChild.getMethods();
			final String voPropertyAnnotationFqn = annoRefState.VO_PROPERTY_ANNOTATION_FQN;
			for (final PsiMethod method : allMethods) {
				if (isMethodAnnotated(method, voPropertyAnnotationFqn)) {
					lookForValidPropertiesInAnnotatedChildClasses(visitorListener, psiAnno, method, methodsPropertiesMap);
					assignPropertyMethod(methodsPropertiesMap, method, voPropertyAnnotationFqn);
					visitorListener.foundValidMethodProperty(methodsPropertiesMap);
				}
			}
		}
	}

	private static void assignPropertyMethod(Map<String, Map<String, PsiMethod>> methodsPropertiesMap, PsiMethod method, String voPropertyAnnotationFqn) {
		final PsiAnnotationMemberValue annotationMemberValue = getMethodAnnotationValue(method, voPropertyAnnotationFqn, "name");
		if (annotationMemberValue != null) {
			assignPropertyMapWithMethod(methodsPropertiesMap, method, isGetterOrSetter(method), annotationMemberValue.getText());
		} else {
			if (PropertyUtil.isSimplePropertySetter(method)) {
				assignPropertyMapWithMethod(methodsPropertiesMap, method, SETTER_PROPERTY, null);
			}
			if (PropertyUtil.isSimplePropertyGetter(method)) {
				assignPropertyMapWithMethod(methodsPropertiesMap, method, GETTER_PROPERTY, null);
			}
		}
	}

	private static void lookForValidPropertiesInAnnotatedChildClasses(ClassVisitorListener visitorListener, PsiAnnotation psiAnno,
	                                                                  PsiMethod method, Map<String, Map<String, PsiMethod>> methodsPropertiesMap) {
		final PsiType returnType = method.getReturnType();
		logger.info("isPropitiousClassFile(): returnType=" + returnType);
		if (method.getReturnType() instanceof PsiClassType && ((PsiClassType) method.getReturnType()).resolve().getName().equals("VOList")) {
			if (((PsiClassType) method.getReturnType()).getParameterCount() == 1 && ((PsiClassType) method.getReturnType()).getParameters()[0] instanceof PsiClassType) {
				final PsiClass classGrandChild = ((PsiClassType) ((PsiClassType) method.getReturnType()).getParameters()[0]).resolveGenerics().getElement();
				lookForValidPropertiesInAnnotatedClasses(visitorListener, classGrandChild, psiAnno, methodsPropertiesMap);
			}
		}
	}

	private static String isGetterOrSetter(PsiMethod method) {
		if (PropertyUtil.isSimplePropertySetter(method)) {
			return SETTER_PROPERTY;
		}
		if (PropertyUtil.isSimplePropertyGetter(method)) {
			return GETTER_PROPERTY;
		}
		return null;
	}

	private static void assignPropertyMapWithMethod(Map<String, Map<String, PsiMethod>> methodsPropertiesMap, PsiMethod method, String propertyName, String propertyValue) {
		if (methodsPropertiesMap.containsKey(propertyName)) {
			if (SETTER_PROPERTY.equals(propertyName)) {
				methodsPropertiesMap.get(propertyName).put(propertyValue == null ? PropertyUtil.getPropertyNameBySetter(method) : propertyValue, method);
			}
			if (GETTER_PROPERTY.equals(propertyName)) {
				methodsPropertiesMap.get(propertyName).put(propertyValue == null ? PropertyUtil.getPropertyNameByGetter(method) : propertyValue, method);
			}
		} else {
			final Map<String, PsiMethod> map = Maps.newHashMap();
			if (GETTER_PROPERTY.equals(propertyName)) {
				map.put(propertyValue == null ? PropertyUtil.getPropertyNameByGetter(method) : propertyValue, method);
			}
			if (SETTER_PROPERTY.equals(propertyName)) {
				map.put(propertyValue == null ? PropertyUtil.getPropertyNameBySetter(method) : propertyValue, method);
			}
			methodsPropertiesMap.put(propertyName, map);
		}

	}


	public static PsiAnnotation getAnnotationForPropitiousClassFile(PsiFile psiFile, ClassVisitorListener visitorListener, String classFQN) {
		try {
			if (psiFile instanceof PsiJavaFile) {
				for (PsiElement classChild : psiFile.getChildren()) {
					if (classChild instanceof PsiClass && ((PsiModifierListOwner) classChild).hasModifierProperty(PsiModifier.PUBLIC)) {
						final PsiAnnotation psiAnno = getPropitiousClassElementAnnotation((PsiClass) classChild, classFQN);
						if (psiAnno != null) {
							String qualifiedName = psiAnno.getQualifiedName();
							if (qualifiedName != null) {
								final String[] sQLRefArray = qualifiedName.split("\\.");
								if (isValidSQLRefId(psiAnno, sQLRefArray)) {
									final String cleanedAnno = cleanAnnoRefForName(psiFile, psiAnno);
									if (cleanedAnno != null) {
										if (visitorListener != null) {
											visitorListener.foundValidAnnotation(psiAnno);
										}
										return psiAnno;
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("PsiFile scanning for Propitiously failed : " + e.getMessage(), e);
		}
		return null;
	}

	public static boolean isMethodAnnotated(PsiMethod method, String annotationFQN) {
		if (method.getModifierList() != null) {
			final PsiAnnotation annotation = method.getModifierList().findAnnotation(annotationFQN);
			return (annotation != null);
		}
		return false;
	}

	public static PsiAnnotationMemberValue getMethodAnnotationValue(PsiMethod method, String annotationFQN, String attributeName) {
		if (method.getModifierList() != null) {
			final PsiAnnotation annotation = method.getModifierList().findAnnotation(annotationFQN);
			if (annotation != null) {
				return annotation.findAttributeValue(attributeName);
			}
		}
		return null;
	}

	public static PsiAnnotation getAnnotationForConfiguredClassFile(PsiFile psiFile, String classFQN) {
		try {
			if (psiFile instanceof PsiJavaFile) {
				for (PsiElement classChild : psiFile.getChildren()) {
					if (classChild instanceof PsiClass && ((PsiModifierListOwner) classChild).hasModifierProperty(PsiModifier.PUBLIC)) {
						final PsiAnnotation psiAnno = getPropitiousClassElementAnnotation((PsiClass) classChild, classFQN);
						if (psiAnno != null) {
							return psiAnno;
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("PsiFile scanning for ConversionClass failed : " + e.getMessage(), e);
		}
		return null;
	}

	public static String cleanAnnoRefForName(PsiFile psiFile, PsiAnnotation psiAnno) {
		if (!psiAnno.getText().isEmpty()) {
			final PsiElement annoElement = psiFile.findElementAt(psiAnno.getTextOffset() + psiAnno.getText().split("=")[1].length());
			return cleanSQLRefAnnotationForValue(annoElement);
		}
		return null;
	}

	private static boolean isValidSQLRefId(PsiAnnotation psiAnno, String[] sQLRefArray) {
		return isSQLRef(sQLRefArray) &&
				psiAnno.findAttributeValue(AnnoRefConfigSettings.getInstance(psiAnno.getProject()).getAnnoRefState().ANNOREF_ANNOTATION_ATTRIBUTE_ID) != null;
	}

	public static String isPropitiousClassFile(PsiFile psiFile) {
		try {
			if (psiFile instanceof PsiJavaFile) {
				for (PsiElement classChild : psiFile.getChildren()) {
					if (classChild instanceof PsiClass && ((PsiModifierListOwner) classChild).hasModifierProperty(PsiModifier.PUBLIC)) {
						PsiModifierList list = ((PsiModifierListOwner) classChild).getModifierList();
						if (list != null && list.getApplicableAnnotations().length >= 1) {
							PsiModifierList annoList = ((PsiModifierListOwner) classChild).getModifierList();
							if (annoList != null) {
								for (PsiAnnotation psiAnno : annoList.getAnnotations()) {
									String qualifiedName = psiAnno.getQualifiedName();
									if (qualifiedName != null) {
										final String[] sQLRefArray = qualifiedName.split("\\.");
										if (isValidSQLRefId(psiAnno, sQLRefArray)) {
											final String cleanedAnno = cleanAnnoRefForName(psiFile, psiAnno);
											if (cleanedAnno != null) {
												return cleanedAnno;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("PsiFile scanning for Propitiously failed : " + e.getMessage(), e);
		}
		return null;
	}

	public static boolean isPropitiousClassElement(PsiClass psiClass) {
		if (psiClass.hasModifierProperty(PsiModifier.PUBLIC)) {
			for (PsiElement psiElement : psiClass.getChildren()) {
				if (psiElement instanceof PsiModifierList) {
					for (PsiAnnotation annotation : ((PsiAnnotationOwner) psiElement).getAnnotations()) {
						if (getSqlRefState(psiClass).
								ANNOREF_ANNOTATION_FQN.equals(annotation.getQualifiedName())) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private static AnnoRefSettings getSqlRefState(PsiClass psiClass) {
		final AnnoRefConfigSettings settings = AnnoRefConfigSettings.getInstance(psiClass.getProject());
		if (settings != null && settings.getState() != null) {
			return settings.getAnnoRefState();
		}
		ServiceManager.getService(psiClass.getProject(),
				AnnoRefNotifications.class).notifyAnnoRefError(psiClass.getProject(), AnnoRefBundle.message("annoRef.settings.error"));
		return null;
	}

	public static PsiAnnotation getPropitiousClassElementAnnotation(PsiClass psiClass, final String classFQN) {
		if (psiClass.hasModifierProperty(PsiModifier.PUBLIC)) {
			for (PsiElement psiElement : psiClass.getChildren()) {
				if (psiElement instanceof PsiModifierList) {
					for (PsiAnnotation annotation : ((PsiAnnotationOwner) psiElement).getAnnotations()) {
						if (annotation != null) {
							if (classFQN != null && classFQN.equals(annotation.getQualifiedName())) {
								return annotation;
							}
						}
					}
				}
			}
		}
		return null;
	}

	private static boolean isSQLRef(@NotNull final String[] psiAnnotation) {
		return psiAnnotation[psiAnnotation.length - 1].equalsIgnoreCase(SQL_REF_ANNOTATION);
	}


	public static PsiAnnotation[] getAllValidAnnoRefAnnotations(PsiModifierListOwner psiElement) {
		logger.info("getAllValidAnnoRefAnnotations():");
		final boolean annotated = AnnotationUtil.isAnnotated(psiElement, getAnnoRefAnnotationToScan(psiElement.getProject()));
		return null;
	}

	public static PsiAnnotation[] getAllValidAnnoRefAnnotations(PsiElement psiElement) {
		final boolean annotated = AnnotationUtil.isAnnotated((PsiModifierListOwner) psiElement, getAnnoRefAnnotationToScan(psiElement.getProject()));
		if (annotated) {
			final PsiAnnotation[] psiAnnotations = AnnotationUtil.findAnnotations((PsiModifierListOwner) psiElement, getAnnoRefAnnotationToScan(psiElement.getProject()));
			if (psiAnnotations.length > 0) {
				return psiAnnotations;
			}

		}
		return new PsiAnnotation[0];
	}

	public static PsiAnnotation isAnnoRefAnnotationValid(PsiElement psiElement) {
		if (psiElement instanceof PsiClass) {
			if (psiElement instanceof PsiModifierListOwner
					&& ((PsiModifierListOwner) psiElement).getModifierList() != null
					&& ((PsiModifierListOwner) psiElement).getModifierList().getAnnotations() != null
					&& ((PsiModifierListOwner) psiElement).getModifierList().getAnnotations().length > 0) {
				final PsiAnnotation[] annotations = ((PsiModifierListOwner) psiElement).getModifierList().getAnnotations();
				final List<String> annoRefAnnotationToScan = getAnnoRefAnnotationToScan(psiElement.getProject());
				for (final String annotationFQN : annoRefAnnotationToScan) {
					final PsiAnnotation annotation = AnnotationUtil.findAnnotation(((PsiModifierListOwner) psiElement), annotationFQN);
					if (annotation != null) {
						return annotation;
					}
				}
			}
		}
		return null;
	}

	private static List<String> getAnnoRefAnnotationToScan(Project project) {
		final List<String> annoRefToScanList = Lists.newArrayList();
		annoRefToScanList.add(AnnoRefConfigSettings.getInstance(project).getAnnoRefState().SP_VIEW_ANNOTATION_FQN);
//		annoRefToScanList.add(AnnoRefConfigSettings.getInstance(project).getAnnoRefState().ANNO_ANNOTATION_FQN);
		annoRefToScanList.add(AnnoRefConfigSettings.getInstance(project).getAnnoRefState().ANNOREF_ANNOTATION_FQN);
		return annoRefToScanList;
	}
}
