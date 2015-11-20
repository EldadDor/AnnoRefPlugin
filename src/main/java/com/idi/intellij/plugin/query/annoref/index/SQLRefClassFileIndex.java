package com.idi.intellij.plugin.query.annoref.index;

import com.google.common.collect.Lists;
import com.idi.intellij.plugin.query.annoref.common.SQLRefConstants;
import com.idi.intellij.plugin.query.annoref.filter.StringLiteralElementFilter;
import com.idi.intellij.plugin.query.annoref.index.listeners.ClassVisitorListener;
import com.idi.intellij.plugin.query.annoref.index.listeners.IndexProgressChangedListener;
import com.idi.intellij.plugin.query.annoref.notification.AnnoRefNotifications;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefConfigSettings;
import com.idi.intellij.plugin.query.annoref.repo.model.SQLRefReference;
import com.idi.intellij.plugin.query.annoref.util.SQLRefApplication;
import com.idi.intellij.plugin.query.annoref.util.SQLRefNamingUtil;
import com.idi.intellij.plugin.query.annoref.util.StringUtils;
import com.intellij.codeInsight.daemon.impl.CollectHighlightsUtil;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.scope.processor.FilterElementProcessor;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 7/5/13
 * Time: 7:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class SQLRefClassFileIndex {
	private static final Logger logger = Logger.getInstance(SQLRefClassFileIndex.class.getName());
	private Project project;
	private IndexProgressChangedListener progressChangedListener;
	private int classRefCount;
	private int classUtilRefCount;

	public SQLRefClassFileIndex(Project project) {
		this.project = project;
	}

	public SQLRefClassFileIndex(Project project, IndexProgressChangedListener progressChangedListener) {
		this.project = project;
		this.progressChangedListener = progressChangedListener;
	}

	public void indexSQLRef() {
		logger.info("indexSQLRef():");
		classRefCount = 0;
		classUtilRefCount = 0;
		Collection<VirtualFile> classFiles = FileTypeIndex.getFiles(JavaFileType.INSTANCE, GlobalSearchScope.projectScope(project));
		try {
			for (final VirtualFile classFile : classFiles) {
				scanClassFile(classFile);
				if (progressChangedListener != null) {
					progressChangedListener.changeMade(true);
				}
			}
			if (!classFiles.isEmpty() && progressChangedListener != null) {
				progressChangedListener.finishedProcess();
			}
			ServiceManager.getService(project, AnnoRefNotifications.class).notifyAnnoRefIndex(project, SQLRefConstants.ANNO_REF_CLASS, classRefCount);
			ServiceManager.getService(project, AnnoRefNotifications.class).notifyAnnoRefIndex(project, SQLRefConstants.ANNO_REF_UTIL_CLASS, classUtilRefCount);
		} catch (Exception e) {
			if (progressChangedListener != null) {
				progressChangedListener.failedProcess(e.getMessage());
			}
		}
	}

	private void scanClassFile(VirtualFile classFile) {
		String classFileName = classFile.getName();
		final PsiElement[] annoElement = new PsiElement[1];
		final Map<String, Map<String, PsiMethod>>[] methodPropertiesMap = new Map[]{new HashMap<String, Map<String, PsiMethod>>()};
		final PsiFile psiFile = SQLRefApplication.getPsiFileFromVirtualFile(classFile, project);
		String sqlRefIdInClass = SQLRefNamingUtil.isPropitiousClassFile(psiFile, new ClassVisitorListener() {
			@Override
			public void foundValidAnnotation(PsiElement classRef) {
				annoElement[0] = classRef;
			}

			@Override
			public void foundValidMethodProperty(Map<String, Map<String, PsiMethod>> methodsPropertiesMap) {
				methodPropertiesMap[0] = methodsPropertiesMap;
			}
		}, AnnoRefConfigSettings.getInstance(project).getAnnoRefState().ANNOREF_ANNOTATION_FQN);
		if (sqlRefIdInClass != null) {
			final SQLRefRepository repository = ServiceManager.getService(project, SQLRefRepository.class);
			repository.addClassFileInformationToRepository(sqlRefIdInClass, classFile, annoElement[0]);
			if (!methodPropertiesMap[0].isEmpty()) {
				repository.assignMethodPropertiesInformation(sqlRefIdInClass, methodPropertiesMap[0]);
			}
			classRefCount++;
		}
		if (AnnoRefConfigSettings.getInstance(project).getAnnoRefState().ENABLE_UTIL_CLASS_SCAN) {
			scanForRequiredMethodCalls(psiFile);
		}
	}

	/**
	 * Scanning for all QueryUtils calls...insane, insane, got no brain
	 *
	 * @param psiFile
	 */
	private void scanForRequiredMethodCalls(PsiFile psiFile) {
		if (psiFile instanceof PsiJavaFile) {
			for (final PsiImportStatement psiImportStatement : ((PsiJavaFile) psiFile).getImportList().getImportStatements()) {
				if (psiImportStatement.getQualifiedName().equals(AnnoRefConfigSettings.getInstance(project).getAnnoRefState().ANNOREF_UTIL_CLASS_FQN)) {
					if (logger.isDebugEnabled()) {
						logger.debug("scanClassFile(): psiImportStatement.getQualifiedName()=" + psiImportStatement.getQualifiedName());
					}
					final FilterElementProcessor processor = new FilterElementProcessor(new StringLiteralElementFilter());
					final boolean processed = PsiTreeUtil.processElements(psiFile, processor);
					final List<PsiElement> results = processor.getResults();
					if (!results.isEmpty()) {
						for (final PsiElement psiElement : results) {
							final String refId = StringUtils.cleanQuote(((PsiCall) psiElement).getArgumentList().getExpressions()[0].getText());
							final SQLRefReference refReference = ServiceManager.getService(project, SQLRefRepository.class).getSQLRefReferenceForID(refId);
							if (refReference != null && refReference.getSqlRefId() != null) {
								final int startOffset = ((ASTNode) psiElement).getStartOffset();
								final String methodCallReferenceId = refReference.getSqlRefId() + "_" + startOffset;
								refReference.addUtilClassCallInformation(psiFile.getName(), psiElement);
								classUtilRefCount++;
								if (logger.isDebugEnabled()) {
									logger.debug("scanForRequiredMethodCalls(): MethodCallRef=" + methodCallReferenceId);
								}
							}
						}
					}
				}
			}
		}
	}

	@Deprecated
	private void lookForInnerUsageInClass(VirtualFile classFile, PsiFile psiFile) {
		final Editor editor = findFirstAvailableEditor(FileEditorManager.getInstance(project).getAllEditors(classFile));
		if (editor != null) {
			final Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
			int i = editor.getCaretModel().getOffset();
			int j = document.getLineNumber(i);
			int k = document.getLineStartOffset(j);
			int m = document.getLineEndOffset(j);
			List<PsiElement> elementsInRange = CollectHighlightsUtil.getElementsInRange(psiFile, k, m);
			for (final PsiElement psiElement : elementsInRange) {
				if (psiElement instanceof PsiMethodCallExpression) {
					final PsiType type = ((PsiMethodCallExpression) psiElement).getMethodExpression().getQualifierExpression().getType();
					if (type instanceof PsiClassReferenceType && ((PsiClassReferenceType) type).getReference() != null) {
						final Project project = psiElement.getProject();
						if (AnnoRefConfigSettings.getInstance(project).getAnnoRefState().ANNOREF_UTIL_CLASS_FQN.equals(((PsiClassReferenceType) type).getReference().getQualifiedName())) {
							if (((PsiCall) psiElement).getArgumentList().getExpressions().length == 1) {
								final PsiExpression psiExpression = ((PsiCall) psiElement).getArgumentList().getExpressions()[0];
								final String refId = String.valueOf(((PsiLiteral) psiExpression).getValue());
								final SQLRefReference refReference = ServiceManager.getService(project, SQLRefRepository.class).getSQLRefReferenceForID(refId);
								if (!refReference.getXmlSmartPointersElements().isEmpty()) {
									final List<PsiElement> psiElements = Lists.newArrayList();
									for (final SmartPsiElementPointer<PsiElement> elementPointer : refReference.getXmlSmartPointersElements()) {
										psiElements.add(elementPointer.getElement());
									}
									PsiElement[] elements = new PsiElement[psiElements.size()];
									psiElements.toArray(elements);
									refReference.addUtilClassCallInformation(refId, psiElement);
								}
							}
						}
					}
				}
			}
		}
	}


	@Nullable
	private Editor findFirstAvailableEditor(@NotNull FileEditor[] fileEditors) {
		for (FileEditor fileEditor : fileEditors) {
			if (fileEditor instanceof TextEditor) {
				return ((TextEditor) fileEditor).getEditor();
			}
		}
		return null;
	}


	private void showVFInfo(VirtualFile virtualFile) {
		logger.info("showVFInfo(): CLASS");
		logger.info("virtualFile: isValid= " + virtualFile.isValid());
		logger.info("virtualFile: isSymLink=" + virtualFile.isSymLink());
		logger.info("virtualFile: url= " + virtualFile.getUrl());
		logger.info("virtualFile: fileType= " + virtualFile.getFileType());
	}


}
