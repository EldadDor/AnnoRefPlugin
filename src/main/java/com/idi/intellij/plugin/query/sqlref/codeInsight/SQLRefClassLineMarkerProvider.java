package com.idi.intellij.plugin.query.sqlref.codeInsight;

import com.idi.intellij.plugin.query.sqlref.common.SQLRefConstants;
import com.idi.intellij.plugin.query.sqlref.index.SQLRefRepository;
import com.idi.intellij.plugin.query.sqlref.model.ReferenceCollectionManager;
import com.idi.intellij.plugin.query.sqlref.model.SQLRefReference;
import com.idi.intellij.plugin.query.sqlref.persist.SQLRefConfigSettings;
import com.idi.intellij.plugin.query.sqlref.util.SQLRefNamingUtil;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.psi.*;
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
	private static final Logger log = Logger.getInstance(SQLRefClassLineMarkerProvider.class.getName());

	@Nullable
	@Override
	public LineMarkerInfo<PsiElement> getLineMarkerInfo(@NotNull PsiElement element) {
		/*if (log.isDebugEnabled()) {
			log.debug("getLineMarkerInfo(): element=" + element);
		}*/
		if (element instanceof PsiModifierList) {
			PsiAnnotation[] annotations = ((PsiAnnotationOwner) element).getAnnotations();
			for (PsiAnnotation annotation : annotations) {
				PsiFile psiFile = element.getContainingFile();
				Project project = ProjectUtil.guessProjectForFile(psiFile.getVirtualFile());
				if (SQLRefConfigSettings.getInstance(project).getSqlRefState().ANNOREF_ANNOTATION_FQN.equals(annotation.getQualifiedName())) {
					String cleanedAnnoRef = SQLRefNamingUtil.cleanAnnoRefForName(annotation.getContainingFile(), annotation);
//					log.info("Found annoRef, cleaned=" + cleanedAnnoRef);
					if (cleanedAnnoRef != null) {
						com.idi.intellij.plugin.query.sqlref.repo.model.SQLRefReference sqlRefReferenceForID = ServiceManager.getService(psiFile.getProject(), SQLRefRepository.class).getSQLRefReferenceForID(cleanedAnnoRef);
						if (sqlRefReferenceForID.getXmlQueryElements().isEmpty()) {
							return null;
						}
						PsiElement[] elements = new PsiElement[sqlRefReferenceForID.getXmlQueryElements().size()];
						sqlRefReferenceForID.getXmlQueryElements().toArray(elements);
						return SQLRefIdLineMarkerInfo.create(annotation, elements, SQLRefConstants.ANNO_REF_COMPONENT_ICON_XML, null);
					}
				}
			}
		}
		return null;
	}


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
