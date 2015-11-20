/*
 * User: eldad.Dor
 * Date: 17/07/2014 11:59
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.index.visitors;

import com.idi.intellij.plugin.query.annoref.index.listeners.ClassVisitorListener;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefConfigSettings;
import com.idi.intellij.plugin.query.annoref.util.SQLRefNamingUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiElementFilter;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * @author eldad
 * @date 17/07/2014
 */
public class AnnoRefJavaFileVisitor extends JavaElementVisitor {

	private static final Logger log = Logger.getInstance(AnnoRefJavaFileVisitor.class.getName());
	private final Project project;
	private final ClassVisitorListener classVisitorListener;

	public AnnoRefJavaFileVisitor(Project project, ClassVisitorListener classVisitorListener) {
		this.project = project;
		this.classVisitorListener = classVisitorListener;
	}

	@Override
	public void visitFile(PsiFile file) {
		super.visitFile(file);
	}

	@Override
	public void visitJavaFile(PsiJavaFile file) {
		super.visitJavaFile(file);
		final boolean b = PsiTreeUtil.processElements(file.getOriginalElement(), new AnnoRefPsiElementProcessor());
		final PsiElement[] psiElements = PsiTreeUtil.collectElements(file, new PsiElementFilter() {
			@Override
			public boolean isAccepted(PsiElement element) {
				final PsiAnnotation annoRefAnnotationValid = SQLRefNamingUtil.isAnnoRefAnnotationValid(element);
				if (annoRefAnnotationValid != null) {
					log.info("isAccepted(): element=" + annoRefAnnotationValid.getText());
				}
				final String fqn = AnnoRefConfigSettings.getInstance(project).getAnnoRefState().ANNOREF_ANNOTATION_FQN;
							/*if (element instanceof PsiModifierListOwner) {
					if (((PsiModifierListOwner) element).getModifierList().getAnnotations().length > 0) {
						PsiAnnotation[] refAnnotations = new PsiAnnotation[0];
						try {
//							final PsiAnnotationOwner owner = ((PsiAnnotationOwner) element).getAnnotations()[0].getOwner();
							log.info("isAccepted():");
							refAnnotations = SQLRefNamingUtil.getAllValidAnnoRefAnnotations(((PsiModifierListOwner) element));
						} catch (Exception e) {
							log.info("isAccepted(): error=" + e.getMessage(), e);
						}
						log.info("isAccepted(): element=" + Arrays.toString(refAnnotations));
					}
				}*/
				return true;
			}
		});
	}


	@Override
	public void visitClass(PsiClass aClass) {
		super.visitClass(aClass);
		if (hasDeprecatedAnnotation(aClass)) {
			return;
		}
	}

	@Override
	public void visitAnnotation(PsiAnnotation annotation) {
		super.visitAnnotation(annotation);
	}


	private static boolean hasDeprecatedAnnotation(PsiModifierListOwner element) {
		final PsiModifierList modifierList = element.getModifierList();
		if (modifierList == null) {
			return false;
		}
		final PsiAnnotation annotation = modifierList.findAnnotation("java.lang.Deprecated");
		return annotation != null;
	}


}