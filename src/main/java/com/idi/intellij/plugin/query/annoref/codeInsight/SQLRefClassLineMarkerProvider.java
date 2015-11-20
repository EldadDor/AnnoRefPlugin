package com.idi.intellij.plugin.query.annoref.codeInsight;

import com.google.common.collect.Lists;
import com.idi.intellij.plugin.query.annoref.common.SQLRefConstants;
import com.idi.intellij.plugin.query.annoref.index.SQLRefRepository;
import com.idi.intellij.plugin.query.annoref.model.ReferenceCollectionManager;
import com.idi.intellij.plugin.query.annoref.model.SQLRefReference;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefConfigSettings;
import com.idi.intellij.plugin.query.annoref.util.SQLRefNamingUtil;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
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
		if (element instanceof PsiModifierList) {
			PsiAnnotation[] annotations = ((PsiAnnotationOwner) element).getAnnotations();
			for (PsiAnnotation annotation : annotations) {
				PsiFile psiFile = element.getContainingFile();
				if (AnnoRefConfigSettings.getInstance(element.getProject()).getAnnoRefState().ANNOREF_ANNOTATION_FQN.equals(annotation.getQualifiedName())) {
					String cleanedAnnoRef = SQLRefNamingUtil.cleanAnnoRefForName(annotation.getContainingFile(), annotation);
					if (cleanedAnnoRef != null) {
						com.idi.intellij.plugin.query.annoref.repo.model.SQLRefReference sqlRefReferenceForID = ServiceManager.getService(psiFile.getProject(), SQLRefRepository.class).getSQLRefReferenceForID(cleanedAnnoRef);
						if (sqlRefReferenceForID != null) {
							if (!sqlRefReferenceForID.getXmlQueryElements().isEmpty() || !sqlRefReferenceForID.getUtilClassSmartPointersElements().isEmpty()) {
								final List<PsiElement> psiElements = Lists.newArrayList();
								for (final SmartPsiElementPointer<PsiElement> elementPointer : sqlRefReferenceForID.getXmlSmartPointersElements()) {
									psiElements.add(elementPointer.getElement());
								}
								for (final SmartPsiElementPointer<PsiElement> elementPointer : sqlRefReferenceForID.getUtilClassSmartPointersElements().values()) {
									psiElements.add(elementPointer.getElement());
								}
								PsiElement[] elements = new PsiElement[psiElements.size()];
								psiElements.toArray(elements);
								return SQLRefIdLineMarkerInfo.create(annotation, elements, SQLRefConstants.ANNO_REF_COMPONENT_ICON_XML, null);
							} else {
								return null;
							}
						}
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

	@Deprecated
	public void runSQLRefAnnoSearchInClasses(final PsiElement annoElement, final String cleanedAnno, final Project project) {
		Map<String, SQLRefReference> sqlRefReferenceMap = ReferenceCollectionManager.getInstance(project).getAllSQLReferencesInXmlFile(cleanedAnno);
	}
}
