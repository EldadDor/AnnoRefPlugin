package com.idi.intellij.plugin.query.sqlref.codeInsight;

import com.idi.intellij.plugin.query.sqlref.common.SQLRefConstants;
import com.idi.intellij.plugin.query.sqlref.index.SQLRefRepository;
import com.idi.intellij.plugin.query.sqlref.repo.model.SQLRefReference;
import com.idi.intellij.plugin.query.sqlref.util.SQLRefNamingUtil;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.idea.LoggerFactory;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttributeValue;
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
	private static final Logger logger = LoggerFactory.getInstance().getLoggerInstance(SQLRefXmlQueryLineMarkerProvider.class.getName());

	@Override
	public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement element) {
		try {
			if (SQLRefNamingUtil.isPropitiousXmlFile(element.getContainingFile())) {
				if (element instanceof XmlAttributeValue) {
					final String sqlRefKey = getSqlRefKey(element);
					if (sqlRefKey != null) {
						SQLRefReference sqlRefReferenceForID = ServiceManager.getService(element.getProject(), SQLRefRepository.class).getSQLRefReferenceForID(sqlRefKey);
						if (sqlRefReferenceForID.getClassAnnoElements().isEmpty()) {
							return null;
						}
						PsiElement[] elements = new PsiElement[sqlRefReferenceForID.getClassAnnoElements().size()];
						sqlRefReferenceForID.getClassAnnoElements().toArray(elements);
						return SQLRefIdLineMarkerInfo.create(element, elements, SQLRefConstants.ANNO_REF_COMPONENT_ICON_CLASS, null);
					} else {
						return null;       //todo   place some inspection here?
					}
				}
			}
		} catch (Exception e) {
			logger.error("getLineMarkerInfo(): ERROR=" + e.getMessage());
		}
		return null;
	}

	private String getSqlRefKey(PsiElement element) {
		if (element.getText().contains("1.0") || element.getText().contains("UTF-8")) {
			return null;
		}
//		final PsiFile psiFile = initContainingFile(element.getParent().getContainingFile());
//		currentFileRefCollection = getFileRefCollectionByPsiFile(psiFile);
		return StringUtil.stripQuotesAroundValue(element.getText());
	}

	/*private FileReferenceCollection getFileRefCollectionByPsiFile(PsiFile psiFile) {
		if (containingFile == null) {
			FileReferenceCollection fileReferenceCollectionByPsiFile = ReferenceCollectionManager.getFileReferenceCollectionByPsiFile(psiFile);
			if (logger.isDebugEnabled()) {
				logger.info("getFileRefCollectionByPsiFile(): fileReferenceCollectionByPsiFile=" + fileReferenceCollectionByPsiFile);
			}
			return fileReferenceCollectionByPsiFile;
		} else {
			FileReferenceCollection fileReferenceCollectionByPsiFile = ReferenceCollectionManager.getFileReferenceCollectionByPsiFile(containingFile);
			if (logger.isDebugEnabled()) {
				logger.debug("getFileRefCollectionByPsiFile(): fileReferenceCollectionByPsiFile=" + fileReferenceCollectionByPsiFile);
			}
			return fileReferenceCollectionByPsiFile;
		}
	}*/


/*	private SQLRefReference putQueryReferenceToFileRefManager(String fileRef, SQLRefReference sqlRefReference, Project project) {
		final FileReferenceCollection fileRefCollection = ReferenceCollectionManager.getInstance(project).getQueriesCollection(fileRef, true);
		return fileRefCollection.putSQLRefIntoCollection(sqlRefReference);
	}


	private PsiFile initContainingFile(PsiElement psiElement) {
		if (containingFile == null) {
			containingFile = (PsiFile) psiElement;
		}
		return containingFile;
	}*/

	@Override
	public void collectSlowLineMarkers(List<PsiElement> elements, Collection<LineMarkerInfo> result) {
		//To change body of implemented methods use File | Settings | File Templates.
	}
}
