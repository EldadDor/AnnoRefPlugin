package com.idi.intellij.plugin.query.sqlref.util;

import com.idi.intellij.plugin.query.sqlref.index.listeners.ClassVisitorListener;
import com.idi.intellij.plugin.query.sqlref.persist.SQLRefConfigSettings;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.idea.LoggerFactory;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 25/03/2011
 * Time: 00:46:07
 * To change this template use File | Settings | File Templates.
 */
public class SQLRefNamingUtil {
	private final static Logger logger = LoggerFactory.getInstance().getLoggerInstance(SQLRefNamingUtil.class.getName());

	public static Boolean isMatchFileName(String fileName) {
		Pattern pattern = Pattern.compile("^([A-Za-z1-9]*(-)?[A-Za-z1-9]*(-queries.xml)$)", Pattern.CASE_INSENSITIVE);
//		Pattern pattern3 = Pattern.compile("\\w+(-queries.xml)");
		// In case you would like to ignore case sensitivity you could use this statement
		// Pattern pattern = Pattern.compile("\\s+", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(fileName);
		// Check all occurance
		return matcher.find();
	}


	public static boolean isPropitiousXmlFile(PsiFile file) {
		return file instanceof XmlFile && isMatchFileName(file.getVirtualFile().getPresentableName());
	}

	public static String cleanSQLRefAnnotationForValue(PsiElement element) {
		PsiAnnotation sqlRefAnnoValue = PsiTreeUtil.getParentOfType(element, PsiAnnotation.class);
		return cleanSQLRefQuotes(sqlRefAnnoValue);
	}

	private static String cleanSQLRefQuotes(PsiElement sqlRefAnnoValue) {
		String firstStrip = null;
		if (sqlRefAnnoValue != null && sqlRefAnnoValue.getText().length() > 1 && sqlRefAnnoValue.getText().contains("=")) {
			firstStrip = org.apache.commons.lang.StringUtils.stripEnd(sqlRefAnnoValue.getText().split("=")[1], ")");
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
		if (sndStrip != null && sndStrip.length() > 2 && !org.apache.commons.lang.StringUtils.containsOnly("'", sndStrip)) {
			return StringUtil.stripQuotesAroundValue(sndStrip).substring(2, sndStrip.length());
		}
		return null;
	}


	public static PsiAnnotation getPropitiousAnnotationForFile(PsiFile psiFile) {
		try {
			if (psiFile instanceof PsiJavaFile) {
				for (PsiElement classChild : psiFile.getChildren()) {
					if (classChild instanceof PsiClass && ((PsiModifierListOwner) classChild).hasModifierProperty(PsiModifier.PUBLIC)) {
						PsiAnnotation psiAnno = AnnotationUtil.findAnnotation(((PsiModifierListOwner) classChild), SQLRefConfigSettings.getInstance(psiFile.getProject()).
								getSqlRefState().SQLREF_ANNOTATION_FQN);
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

	public static String isPropitiousClassFile(PsiFile psiFile, Project project, ClassVisitorListener visitorListener) {
		try {
			if (psiFile instanceof PsiJavaFile) {
				for (PsiElement classChild : psiFile.getChildren()) {

					if (classChild instanceof PsiClass && ((PsiModifierListOwner) classChild).hasModifierProperty(PsiModifier.PUBLIC)) {
						PsiAnnotation psiAnno = AnnotationUtil.findAnnotation(((PsiModifierListOwner) classChild),
								SQLRefConfigSettings.getInstance(project).getSqlRefState().SQLREF_ANNOTATION_FQN);
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

	public static String cleanAnnoRefForName(PsiFile psiFile, PsiAnnotation psiAnno) {
		final PsiElement annoElement = psiFile.findElementAt(psiAnno.getTextOffset() + psiAnno.getText().split("=")[1].length());
		//										PsiElement annoElement = psiFile.findElementAt(annoList.getTextOffset() + 20);
		return cleanSQLRefAnnotationForValue(annoElement);
	}

	private static boolean isValidSQLRefId(PsiAnnotation psiAnno, String[] sQLRefArray) {
		return isSQLRef(sQLRefArray) && psiAnno.findAttributeValue("refId") != null;
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
					for (PsiAnnotation annotation : ((PsiModifierList) psiElement).getAnnotations()) {
						if (SQLRefConfigSettings.getInstance(psiClass.getProject()).getSqlRefState().
								SQLREF_ANNOTATION_FQN.equals(annotation.getQualifiedName())) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public static PsiAnnotation getPropitiousClassElementAnnotation(PsiClass psiClass) {
		if (psiClass.hasModifierProperty(PsiModifier.PUBLIC)) {
			for (PsiElement psiElement : psiClass.getChildren()) {
				if (psiElement instanceof PsiModifierList) {
					for (PsiAnnotation annotation : ((PsiModifierList) psiElement).getAnnotations()) {
						if (SQLRefConfigSettings.getInstance(psiClass.getProject()).getSqlRefState().
								SQLREF_ANNOTATION_FQN.equals(annotation.getQualifiedName())) {
							return annotation;
						}
					}
				}
			}
		}
		return null;
	}


	private static boolean isSQLRef(@NotNull final String[] psiAnnotation) {
		return psiAnnotation[psiAnnotation.length - 1].equalsIgnoreCase("SQLRef");
	}

/*
	@Override
	public void projectOpened() {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void projectClosed() {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void initComponent() {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void disposeComponent() {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@NotNull
	@Override
	public String getComponentName() {
		return getClass().getName();
	}*/
}
