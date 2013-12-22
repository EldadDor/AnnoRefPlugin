package com.idi.intellij.plugin.query.sqlref.util;

import com.idi.intellij.plugin.query.sqlref.common.ClassAnnoRefSearchStateEnum;
import com.idi.intellij.plugin.query.sqlref.index.SQLRefRepository;
import com.idi.intellij.plugin.query.sqlref.model.ClassReferenceCache;
import com.idi.intellij.plugin.query.sqlref.model.FileReferenceCollection;
import com.idi.intellij.plugin.query.sqlref.model.ReferenceCollectionManager;
import com.idi.intellij.plugin.query.sqlref.model.SQLRefReference;
import com.idi.intellij.plugin.query.sqlref.persist.SQLRefConfigSettings;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.fileTypes.FileNameMatcher;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlFile;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 29/10/2010
 * Time: 12:02:13
 * To change this template use File | Settings | File Templates.
 */
public class SQLRefDataAccessor implements ProjectComponent {
	private static final Logger log = Logger.getInstance(SQLRefDataAccessor.class.getName());

	private static final String PLUGIN_NAME = "SQLRef";
	public Project project;
	public Editor editor;
	public PsiFile psiFile;
	private Set<XmlFile> xmlFiles = new HashSet<XmlFile>(10);
	private Set<String> xmlFQNFiles = new HashSet<String>(10);
	private QueriesXmlVisitor xmlVisitor = QueriesXmlVisitor.getInstance();
	private ClassAnnoRefSearchStateEnum classAnnoState = ClassAnnoRefSearchStateEnum.NOT_FOUND;

	FileNameMatcher nameMatcher = FileTypeManager.parseFromString("^([A-Za-z1-9]*(-queries.xml)$");

	public void setProject(@Nullable Project project) {
		this.project = project;
	}


	public String getPluginName() {
		return PLUGIN_NAME;
	}

	private Boolean isMatchFileName(String fileName) {
		Pattern pattern = Pattern.compile("^([A-Za-z1-9]*(-)?[A-Za-z1-9]*(-queries.xml)$)", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(fileName);
		return matcher.find();
	}

	public void initializeSQLRefData(@NotNull DataContext dataContext) {
		if (project == null) {
			project = (Project) dataContext.getData(PlatformDataKeys.PROJECT.getName());
		}
		editor = (Editor) dataContext.getData(PlatformDataKeys.EDITOR.getName());
		psiFile = (PsiFile) dataContext.getData(LangDataKeys.PSI_FILE.getName());
//		MODULE = (Module) dataContext.getData(LangDataKeys.MODULE.getName());
//		PSI_ELEMENT = (PsiElement) dataContext.getData(LangDataKeys.PSI_ELEMENT.getName());
//		NAVIGATABLE = (Navigatable) dataContext.getData(LangDataKeys.NAVIGATABLE.getName());
//		VIRTUAL_FILE = (VirtualFile) dataContext.getData(PlatformDataKeys.VIRTUAL_FILE.getName());
//		Module[] modules = ModuleManager.getInstance(project).getModules();
		FileTypeEnum fileTypeEnum = whatIsTheFileType(psiFile);
		String sqlRefId = null;
		switch (fileTypeEnum.getValue()) {
			case 1:
				if (editor != null) {
					Pair<String, Boolean> xmlToClassPair = fetchSQLReferenceInClass(editor, psiFile);
					if (xmlToClassPair != null && xmlToClassPair.getSecond()) {
						sqlRefId = xmlToClassPair.getFirst();
						com.idi.intellij.plugin.query.sqlref.repo.model.SQLRefReference sqlRefReferenceForID = ServiceManager.getService(psiFile.getProject(), SQLRefRepository.class).getSQLRefReferenceForID(sqlRefId);
						if (sqlRefReferenceForID != null && !sqlRefReferenceForID.getXmlQueryElements().isEmpty()) {
							if (ReferenceNavigator.getInstance().navigateToXmlReference(project, sqlRefReferenceForID)) {
								break;
							} else {
								showNotFindIdErrorMessage(sqlRefId);
								break;
							}
						}
						showNotFindIdErrorMessage(sqlRefId);
					} else {
						showNotFindIdErrorMessage(sqlRefId);
					}
				} else {
					showNotFindIdErrorMessage(sqlRefId);
				}
				return;

			case 0:
				Pair<String, Boolean> classToXmlPair = fetchSQLReferenceInXML(editor, psiFile);
				if (classToXmlPair != null && classToXmlPair.getFirst() != null) {
					sqlRefId = classToXmlPair.getFirst();
					Map<String, SQLRefReference> classQueryReference = xmlVisitor.getClassQueryReference(sqlRefId);
					if (classQueryReference != null) {
						SQLRefReference sqlRefReference = classQueryReference.get(sqlRefId);
						if (sqlRefReference != null) {
							log.info("navigating to class reference - " + sqlRefId + " found xml file holder : " + sqlRefReference.getXmlPsiElement().getContainingFile().getName());
							boolean wasNavigated = ReferenceNavigator.getInstance().navigateToClassReference(project, sqlRefReference);
							if (!wasNavigated) {
								showNotFindIdErrorMessage(sqlRefId);
							}
						} else {
							showNotFindIdErrorMessage(sqlRefId);
						}
					} else if (classToXmlPair.getSecond()) {
						showNotFindIdErrorMessage(sqlRefId);
					}
				}
		}
	}

//		GlobalSearchScope scope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(MODULE);
//		final VirtualFile[] vfToScan = PackageIndex.getInstance(project).getDirectoriesByPackageName("queries", true);
//		lookForConventionalReferencedFile(project, vfToScan);
//		Language language = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument()).getLanguage();
//		SyntaxHighlighter syntaxHighlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(language, project, psiFile.getVirtualFile());
//			if (child != null && scope.contains(child)) {
//}

	public boolean isPropitiousXmlFile(PsiFile file) {
		return (file instanceof XmlFile) && isMatchFileName(file.getVirtualFile().getPresentableName());
	}


	private void showNotFindIdErrorMessage(String sqlRef) {
		WindowManager.getInstance().getStatusBar(project).setInfo("Can't find a reference sql with id : " + sqlRef);
		WindowManager.getInstance().getStatusBar(project).fireNotificationPopup(constructAMessagePopUp(), Color.orange);
		final JFrame frame = WindowManager.getInstance().getFrame(project);
//		NotificationsManagerImpl.createBalloon();
	}

	@NotNull
	private JComponent constructAMessagePopUp() {
		JPanel panel = new JPanel(new BorderLayout(500, 900));
		panel.add(new JLabel("CAN'T FIND A REFERENCE ID"));
		panel.setBackground(JBColor.ORANGE);
		return panel;
	}


	@SuppressWarnings("UnsafeVfsRecursion")
	public void lookForConventionalReferencedFile(@NotNull final Project project, @NotNull final VirtualFile[] files) {
		for (VirtualFile file : files) {
			if (!file.isDirectory() && file.isValid() && !file.isSymLink() && file.getChildren().length == 0) {
				try {
					final Document fileDocument;
					try {
						fileDocument = FileDocumentManager.getInstance().getDocument(file);
					} catch (IllegalStateException e) {
						log.error(e);
						continue;
					}
					if (fileDocument != null) {
						final PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(fileDocument);
						if (psiFile instanceof XmlFile && isMatchFileName(file.getPresentableName())) {
							visitXmlFileToPassInspection(project, file, psiFile);
						} else if (psiFile instanceof PsiJavaFile) {
							lookForConventionalAnnotatedClassReference(psiFile);
						}
					}
				} catch (Throwable e) {
					log.error(e);
				}
			} else {
				lookForConventionalReferencedFile(project, file.getChildren());
			}
		}
	}

	private void visitXmlFileToPassInspection(Project project, VirtualFile file, PsiFile psiFile) {
		xmlFQNFiles.add(file.getName());
		xmlFiles.add((XmlFile) psiFile);
		try {
			QueriesXmlVisitor.getInstance().setProject(project);
			xmlVisitor.visitFile(psiFile);
			System.out.println("scanned xml file = " + file.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String isPropitiousClassFile(PsiFile psiFile) {
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
										if (isSQLRef(sQLRefArray) && psiAnno.findAttributeValue("refId") != null) {
											final PsiElement annoElement = psiFile.findElementAt(psiAnno.getTextOffset() + psiAnno.getText().split("=")[1].length());
											//										PsiElement annoElement = psiFile.findElementAt(annoList.getTextOffset() + 20);
											final String cleanedAnno = SQLRefNamingUtil.cleanSQLRefAnnotationForValue(annoElement);
//											final String cleanedAnno = SQLRefApplication.getInstance(project, SQLRefNamingUtil.class).cleanSQLRefAnnotationForValue(annoElement);
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
			log.error("PsiFile scanning for Propitiously failed : " + e.getMessage(), e);
		}
		return null;
	}


	public void lookForConventionalAnnotatedClassReference(PsiFile psiFile) {
		for (PsiElement classChild : psiFile.getChildren()) {
			if (classChild instanceof PsiModifierListOwner && ((PsiModifierListOwner) classChild).getModifierList().getApplicableAnnotations().length >= 1) {
				final PsiModifierList annoList = ((PsiModifierListOwner) classChild).getModifierList();
				PsiAnnotation psiAnnotation = AnnotationUtil.findAnnotation((PsiModifierListOwner) classChild,
						SQLRefConfigSettings.getInstance(project).getSqlRefState().ANNOREF_ANNOTATION_FQN);
				if (psiAnnotation != null) {
					log.info("psiAnnotation=" + psiAnnotation.findAttributeValue("refId"));
					final PsiElement annoElement = psiFile.findElementAt(psiAnnotation.getTextOffset() + psiAnnotation.getText().split("=")[1].length());
//					final String cleanedAnno = SQLRefApplication.getInstance(project, SQLRefNamingUtil.class).cleanSQLRefAnnotationForValue(annoElement);
					final String cleanedAnno = SQLRefNamingUtil.cleanSQLRefAnnotationForValue(annoElement);
					if (cleanedAnno == null) {
						continue;
					}
					if (annoElement != null) {
						ClassReferenceCache.getInstance(project).addClassReferenceToCache(annoElement.getContainingFile().getVirtualFile(), cleanedAnno, annoElement);
						for (final String xmlFileName : ReferenceCollectionManager.getInstance(project).getQueriesIdValue().keySet()) {
							runSQLRefAnnoSearchInClasses(annoElement, cleanedAnno, xmlFileName);
						}
					}
				}
			}
		}
	}

	public void runSQLRefAnnoSearchInClasses(final PsiElement annoElement, final String cleanedAnno, final String xmlFileName) {
		FileReferenceCollection fileReferenceCollection = ReferenceCollectionManager.getInstance(project).getQueriesIdValue().get(xmlFileName);
		if (fileReferenceCollection.getQueriesIdMap().containsKey(cleanedAnno)) {
			SQLRefReference refReference = fileReferenceCollection.getQueriesIdMap().get(cleanedAnno);
			refReference.addClassAnnoReference(annoElement);
			classAnnoState = ClassAnnoRefSearchStateEnum.FOUND;
			if (refReference.getRangeHighlighter() != null) { /* on Project initialization the reference will be null */
				removeErrorTextRangeForElement(refReference.getXmlPsiElement(), refReference.getRangeHighlighter());
			}
		} else {
			if (!ClassAnnoRefSearchStateEnum.FOUND.equals(classAnnoState)) {
				classAnnoState = ClassAnnoRefSearchStateEnum.NOT_FOUND;
//				if (refReference.getRangeHighlighter() != null) { /* on Project initialization the reference will be null */
//				addErrorTextRangeForElement(fileReferenceCollection.getQueriesIdMap().get(cleanedAnno).getXmlPsiElement());
			}
		}
	}

	public boolean removeErrorTextRangeForElement(PsiElement annoElement, RangeHighlighter rangeHighlighter) {
		FileEditor[] editors = openEditorIfNeeded(annoElement);
		for (FileEditor fileEditor : editors) {
			if (fileEditor instanceof TextEditor) {
				Editor editor = ((TextEditor) fileEditor).getEditor();
				try {
					editor.getMarkupModel().removeHighlighter(rangeHighlighter);
					editor.getMarkupModel().removeAllHighlighters();
				} catch (Throwable e) {
					log.error("Tried to remove a RangeHighlighter from an Editor that are not related, RangeHL: " + rangeHighlighter + " XmlAttribElement: " + annoElement.getText(), e);
				}
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings({"ConstantConditions"})
	public RangeHighlighter addErrorTextRangeForElement(PsiElement annoElement) {
		FileEditor[] editors = openEditorIfNeeded(annoElement);
		if (editors != null && editors.length > 0) {
			for (FileEditor fileEditor : editors) {
				if (fileEditor instanceof TextEditor) {
					Editor editor = ((TextEditor) fileEditor).getEditor();
//					TextAttributes attributes = new TextAttributes(editor.getColorsScheme().getDefaultForeground(), null, Color.red, EffectType.WAVE_UNDERSCORE, Font.PLAIN);
					TextAttributes attributes = new TextAttributes(new JBColor(new Color(205, 230, 230), new Color(0, 50, 50)), null, JBColor.MAGENTA, EffectType.WAVE_UNDERSCORE, Font.PLAIN);
					TextRange annoRefTextRange = new TextRange(annoElement.getTextRange().getStartOffset(), annoElement.getTextRange().getEndOffset());
					RangeHighlighter rangeHighlighter = editor.getMarkupModel().addRangeHighlighter(annoElement.getTextRange().getStartOffset(),
							annoElement.getTextRange().getEndOffset(), HighlighterLayer.FIRST, attributes, HighlighterTargetArea.EXACT_RANGE);
					rangeHighlighter.setGreedyToLeft(true);
					rangeHighlighter.setGreedyToRight(true);
//					rangeHighlighter.setGutterIconRenderer(););
					//		editor.getGutter()
					if (ReferenceCollectionManager.getInstance(project).getQueriesCollection(annoElement.getContainingFile().getVirtualFile().getName(), false).getOpenForScanning()) {
						ReferenceCollectionManager.getInstance(project).getQueriesCollection(annoElement.getContainingFile().getVirtualFile().getName(), false).setOpen(false);
						ReferenceCollectionManager.getInstance(project).getQueriesCollection(annoElement.getContainingFile().getVirtualFile().getName(), false).setOpenForScanning(false);
						FileEditorManager.getInstance(project).closeFile(annoElement.getContainingFile().getVirtualFile());
					}
/*
					ProblemDescriptor problemDescriptor = InspectionManager.getInstance(project).createProblemDescriptor(annoElement, annoRefTextRange, "SQLRef invalid id", ProblemHighlightType.ERROR, true);
					problemDescriptor.showTooltip();
					problemDescriptor.setTextAttributes(TextAttributesKey.createTextAttributesKey("Error_" + annoElement.getText()));
*/
					//					InspectionManager.getInstance(project).createProblemDescriptor(annoElement, "SQLRef invalid id", true, ProblemHighlightType.ERROR, true);
					return rangeHighlighter;
				}
			}
		}
		return null;
	}

	private FileEditor[] openEditorIfNeeded(PsiElement annoElement) {
		try {
			FileEditorManager.getInstance(project).getAllEditors(annoElement.getContainingFile().getVirtualFile());
			FileEditor[] editors = FileEditorManager.getInstance(project).getEditors(annoElement.getContainingFile().getVirtualFile());
			if (editors.length == 0) { /* opening the class textEditor for reading */
				ReferenceCollectionManager.getInstance(project).getQueriesCollection(annoElement.getContainingFile().getVirtualFile().getName(), false).setOpenForScanning(true);
				ReferenceCollectionManager.getInstance(project).getQueriesCollection(annoElement.getContainingFile().getVirtualFile().getName(), false).setOpen(true);
				FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, annoElement.getContainingFile().getVirtualFile()), false);
				editors = FileEditorManager.getInstance(project).getEditors(annoElement.getContainingFile().getVirtualFile());
			}
			return editors;
		} catch (Throwable e) {
			log.error("opening Editor for element " + annoElement + "error " + e.getMessage());
			return null;
		}
	}
//			System.out.println("refInXmlKey = " + refInXmlKey);
//			if (refInXmlKey.equals(cleanedAnno)) {
//				System.out.println("Added ClassRef: " + cleanedAnno + " to XmlRef: " + refInXmlKey + " resides in: " + fileReferenceCollection.getQueriesIdMap());
//			}
//		}
//}
    /*	}, progressBarTitle, true, project);*/
    /*	if (searchWasCancelled) {
             System.gc();
         }
         }*/

    /*public void runSQLRefAnnoSearchInClasses(PsiElement psiElement) {
         if (!(psiElement instanceof PsiNamedElement)) {
             return;
         }
         String progressBarTitle = "Searching for usages of '" + ((PsiNamedElement) psiElement).getName() + "'  (in scope '" + psiElement.getUseScope() + "')";
         boolean searchWasNotCancelled = ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {
             public void run() {
                 references.addAll(PsiElementUsageFinderFactory.getUsageFinder(psiElement).findUsages());
             }
         }, progressBarTitle, true, DataHolder.getInstance().project);

         if (searchWasNotCancelled)
             presentUsages(psiElement, references);
         else
             System.gc();

         references.clear();
     }*/

	public void findNonCorrelatingSQLRefsInXmlFiles() {
		try {
			int xmlFilesCount = 0;
			for (String xmlReferenceKey : ReferenceCollectionManager.getInstance(project).getQueriesIdValue().keySet()) {
				log.info("correlating xml file " + xmlReferenceKey);
				xmlFilesCount++;
				findNonCorrelatingSQLRefInXmlFile(xmlReferenceKey, true);
			}
			log.info("Number of files correlated successfully : " + xmlFilesCount);
		} catch (Exception e) {
			log.error(e);
		}
	}


	public void findNonCorrelatingSQLRefInXmlFile(String fileName, Boolean isOpening) {
		FileReferenceCollection referenceCollection = ReferenceCollectionManager.getInstance(project).getQueriesCollection(fileName, false);
		if (referenceCollection == null || referenceCollection.isOpen() == isOpening) {
			return;
		}
		for (String sqlReferenceKey : referenceCollection.getQueriesIdMap().keySet()) {
			SQLRefReference refReference = referenceCollection.getQueriesIdMap().get(sqlReferenceKey);
			if (!refReference.hasClassesCorrelated()) {
				refReference.setRangeHighlighter(addErrorTextRangeForElement(refReference.getXmlPsiElement()));
			}
			referenceCollection.setOpen(isOpening);
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


	private FileTypeEnum whatIsTheFileType(PsiFile file) {
		if (file instanceof PsiJavaFile) {
			return FileTypeEnum.JAVA_FILE;
		} else if (file instanceof XmlFile) {
			return FileTypeEnum.XML_FILE;
		}
		return null;
	}

	@Nullable
	private Pair<String, Boolean> fetchSQLReferenceInClass(@NotNull Editor editor, PsiFile file) {
		try {
			String annoValue = isPropitiousClassFile(file);
			if (annoValue != null) {
				PsiJavaFile javaFile = (PsiJavaFile) file;
				PsiElement actualElement = javaFile.findElementAt(editor.getCaretModel().getOffset());
				PsiTreeUtil.getParentOfType(actualElement, PsiClass.class).getChildren();
//			AnnotatedMembersSearch.search(PsiClass);
//			Query<PsiMember> memberQuery = AnnotatedMembersSearch.search(PsiTreeUtil.getParentOfType(actualElement, PsiAnnotation.class).getParent());
//			Collection<PsiMember> collection = memberQuery.findAll();
//			for (PsiMember psiMember : collection) {
				//rangeHighlighter.getCustomRenderer()
//			}

                /*String str = PsiTreeUtil.getParentOfType(actualElement, PsiAnnotation.class).getQualifiedName();
                                if (str != null && str.substring(str.length() - 6, str.length()).equalsIgnoreCase("SQLRef")) {*/
				return new Pair<String, Boolean>(annoValue, true);
			}
		} catch (Throwable e) {
			return null;
		}
		return null;
	}


	private Pair<String, Boolean> fetchSQLReferenceInXML(Editor editor, PsiFile file) {
		if (isPropitiousXmlFile(file)) {
			PsiElement actualElement = file.findElementAt(editor.getCaretModel().getOffset());
			PsiElement[] xmlAnnoChildren = actualElement.getParent().getParent().getChildren();
			for (int i = 0, xmlAnnoChildrenLength = xmlAnnoChildren.length; i < xmlAnnoChildrenLength; i++) {
				switch (i) {
					case 0:
						if (!xmlAnnoChildren[i].getText().equals("id")) {
							return null;
						}
						break;
					case 1:
						if (!xmlAnnoChildren[i].getText().equals("=")) {
							return null;
						}
						break;
					case 2:
						return new Pair<String, Boolean>(StringUtil.stripQuotesAroundValue(xmlAnnoChildren[i].getText()), true);
				}
			}
			return new Pair<String, Boolean>(actualElement.getText(), false);
		}
		return null;
	}


	/*public String cleanSQLRefAnnotationForValue(PsiElement element) {
		PsiAnnotation sqlRefAnnoValue = PsiTreeUtil.getParentOfType(element, PsiAnnotation.class);
		return cleanSQLRefQuotes(sqlRefAnnoValue);
	}

	private String cleanSQLRefQuotes(PsiElement sqlRefAnnoValue) {
		String firstStrip = null;
		if (sqlRefAnnoValue != null && sqlRefAnnoValue.getText().length() > 1 && sqlRefAnnoValue.getText().contains("=")) {
			firstStrip = StringUtils.stripEnd(sqlRefAnnoValue.getText().split("=")[1], ")");
		} else {
			return null;
		}
		return stripDoubleQuotes(firstStrip);
	}

	public String stripDoubleQuotes(String firstStrip) {
		String sndStrip = null;
		if (firstStrip != null) {
			sndStrip = StringUtil.stripQuotesAroundValue(firstStrip);
		}
		if (sndStrip != null && sndStrip.length() > 2 && !StringUtils.containsOnly("'", sndStrip)) {
			return StringUtil.stripQuotesAroundValue(sndStrip).substring(2, sndStrip.length());
		}
		return null;
	}*/

	private boolean isSQLRef(@NotNull final String[] psiAnnotation) {
		return psiAnnotation[psiAnnotation.length - 1].equalsIgnoreCase("SQLRef");
	}


	@Nullable
	public Project getProject() {
		if (project == null) {
			log.info("Project is null, getting Project via Manager...");
			project = ProjectManager.getInstance().getOpenProjects()[ProjectManager.getInstance().getOpenProjects().length - 1];
		}
		log.info("Project Returned from DataAccessor: " + project.getName());
		return project;
	}

	@Nullable
	public Editor getEditor() {
		return editor;
	}

	public File getDefaultConfigPath() {
		return new File(PathManager.getConfigPath() + File.separatorChar + "sqlRef.log");
	}

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
	}
}
