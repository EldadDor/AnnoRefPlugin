package com.idi.intellij.plugin.query.sqlref.codeInsight;

import com.idi.intellij.plugin.query.sqlref.common.SQLRefConstants;
import com.idi.intellij.plugin.query.sqlref.index.SQLRefRepository;
import com.idi.intellij.plugin.query.sqlref.model.ReferenceCollectionManager;
import com.idi.intellij.plugin.query.sqlref.model.SQLRefReference;
import com.idi.intellij.plugin.query.sqlref.persist.SQLRefConfigSettings;
import com.idi.intellij.plugin.query.sqlref.util.SQLRefNamingUtil;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.idea.LoggerFactory;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiModifierList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 5/30/13
 * Time: 11:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class SQLRefClassLineMarkerProvider implements LineMarkerProvider {
	private final static Logger log = LoggerFactory.getInstance().getLoggerInstance(SQLRefClassLineMarkerProvider.class.getName());

	@Nullable
	@Override
	public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement element) {
		PsiFile psiFile = element.getContainingFile();
		log.info("getLineMarkerInfo(): element=" + element);
		Project project = ProjectUtil.guessProjectForFile(psiFile.getVirtualFile());
		if (element instanceof PsiModifierList) {
			PsiAnnotation[] annotations = ((PsiModifierList) element).getAnnotations();
			for (PsiAnnotation annotation : annotations) {
				if (SQLRefConfigSettings.getInstance(project).getSqlRefState().SQLREF_ANNOTATION_FQN.equals(annotation.getQualifiedName())) {
					String cleanedAnnoRef = SQLRefNamingUtil.cleanAnnoRefForName(annotation.getContainingFile(), annotation);
					log.info("Found annoRef, cleaned=" + cleanedAnnoRef);
					com.idi.intellij.plugin.query.sqlref.repo.model.SQLRefReference sqlRefReferenceForID = ServiceManager.getService(psiFile.getProject(), SQLRefRepository.class).getSQLRefReferenceForID(cleanedAnnoRef);
					if (sqlRefReferenceForID.getXmlQueryElements().isEmpty()) {
						return null;
					}
					PsiElement[] elements = new PsiElement[sqlRefReferenceForID.getXmlQueryElements().size()];
					sqlRefReferenceForID.getXmlQueryElements().toArray(elements);
					return SQLRefIdLineMarkerInfo.create(element, elements, SQLRefConstants.ANNO_REF_COMPONENT_ICON_CLASS, null);
				}
			}
		}
		return null;
	}


	/*@Nullable
	@Override
	public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement element) {
		PsiFile psiFile = element.getContainingFile();
		Project project = ProjectUtil.guessProjectForFile(psiFile.getVirtualFile());
		if (psiFile instanceof PsiClass) {
			SQLRefNamingUtil.isPropitiousClassFile(psiFile, project);
			for (PsiElement classChild : psiFile.getChildren()) {
				if (classChild instanceof PsiModifierListOwner && ((PsiModifierListOwner) classChild).getModifierList().getApplicableAnnotations().length >= 1) {
					PsiAnnotation psiAnnotation = AnnotationUtil.findAnnotation((PsiModifierListOwner) classChild,
							SQLRefConfigSettings.getInstance(project).getSqlRefState().SQLREF_ANNOTATION_FQN);
					if (psiAnnotation != null) {
						log.info("psiAnnotation=" + psiAnnotation.findAttributeValue("refId"));
						final PsiElement annoElement = psiFile.findElementAt(psiAnnotation.getTextOffset() + psiAnnotation.getText().split("=")[1].length());
//						final String cleanedAnno = SQLRefApplication.getInstance(project, SQLRefNamingUtil.class).cleanSQLRefAnnotationForValue(annoElement);
						final String cleanedAnno = SQLRefNamingUtil.cleanSQLRefAnnotationForValue(annoElement);
						if (cleanedAnno == null) {
							continue;
						}
						if (annoElement != null) {
							ClassReferenceCache.getInstance(project).addClassReferenceToCache(annoElement.getContainingFile().getVirtualFile(), cleanedAnno, annoElement);
							for (final String xmlFileName : ReferenceCollectionManager.getInstance(project).getQueriesIdValue().keySet()) {
//								return SQLRefIdLineMarkerInfo.create(element, sqlRef.getPsiElementsArray(), SQLRefConstants.ANNO_REF_COMPONENT_ICON_CLASS, null);

							}
						}
					}
				}
			}
		}
		return null;
	}*/

	@Override
	public void collectSlowLineMarkers(@NotNull List<PsiElement> elements, @NotNull Collection<LineMarkerInfo> result) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public void runSQLRefAnnoSearchInClasses(final PsiElement annoElement, final String cleanedAnno, final Project project) {
		Map<String, SQLRefReference> sqlRefReferenceMap = ReferenceCollectionManager.getInstance(project).getAllSQLReferencesInXmlFile(cleanedAnno);
//		sqlRefReferenceMap.entrySet().iterator().next().getValue().getClassPsiElements()
	/*	if (fileReferenceCollection.getQueriesIdMap().containsKey(cleanedAnno)) {
			SQLRefReference refReference = fileReferenceCollection.getQueriesIdMap().get(cleanedAnno);
			refReference.addClassAnnoReference(annoElement);
		} else {
			if (!ClassAnnoRefSearchStateEnum.FOUND.equals(classAnnoState)) {
				classAnnoState = ClassAnnoRefSearchStateEnum.NOT_FOUND;
//				if (refReference.getRangeHighlighter() != null) { *//* on Project initialization the reference will be null *//*
//				addErrorTextRangeForElement(fileReferenceCollection.getQueriesIdMap().get(cleanedAnno).getXmlPsiElement());
			}
		}*/
	}
}
