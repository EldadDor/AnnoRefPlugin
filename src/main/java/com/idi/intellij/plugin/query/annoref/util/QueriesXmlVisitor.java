package com.idi.intellij.plugin.query.annoref.util;

import com.idi.intellij.plugin.query.annoref.common.XmlParsingPhaseEnum;
import com.idi.intellij.plugin.query.annoref.model.FileReferenceCollection;
import com.idi.intellij.plugin.query.annoref.model.ReferenceCollectionManager;
import com.idi.intellij.plugin.query.annoref.model.SQLRefReference;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.XmlRecursiveElementVisitor;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 05/11/2010
 * Time: 00:49:00
 * To change this template use File | Settings | File Templates.
 */
public class QueriesXmlVisitor extends XmlRecursiveElementVisitor {

	private static QueriesXmlVisitor instance;
	@NotNull
	private XmlParsingPhaseEnum phase = XmlParsingPhaseEnum.WAITING;
	private PsiFile visitedFile;
	private Project project;
	private Boolean assigner = true;
	private SQLRefReference currentIndexedSQLRef;
	private FileReferenceCollection currentFileRefCollection;
	//	private ProgressIndicator progressIndicator;
//    private static JBReentrantReadWriteLock lock = LockFactory.createReadWriteLock();
	private boolean soDisplayProgress = false;
	private boolean isFirstEncounter;

	private QueriesXmlVisitor() {
	}

	public static QueriesXmlVisitor getInstance() {
		try {
			if (instance == null) {
				instance = new QueriesXmlVisitor();
			} else {
			}
			return instance;
		} catch (Exception ex) {
			System.out.println("Error : " + ex.getMessage());
			return null;
		}
	}

	public void putQueryReferenceToFileRefManager(String fileRef, SQLRefReference sqlRefReference) {
		final FileReferenceCollection fileRefCollection = ReferenceCollectionManager.getInstance(project).getQueriesCollection(fileRef, true);
		fileRefCollection.putSQLRefIntoCollection(sqlRefReference);
	}

	public <E> E getClassQueryReference(String refName) {
		return (E) ReferenceCollectionManager.getInstance(project).getAllSQLReferencesInXmlFile(String.valueOf(refName));
	}

	public void setProgressIndicator(ProgressIndicator progressIndicator) {
//		this.progressIndicator = progressIndicator;
		soDisplayProgress = true;
	}

	public SQLRefReference getCurrentIndexedSQLRef() {
		return currentIndexedSQLRef;
	}

	public Boolean getAssigner() {
		return assigner;
	}

	public void setAssigner(Boolean assigner) {
		this.assigner = assigner;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	@Override
	public void visitElement(PsiElement element) {
//		if (soDisplayProgress) {
//			progressIndicator.setFraction(0.0 + (1 / 30));
        /*try {
  //				Thread.sleep((long) progressIndicator.getFraction() / 10);
              } catch (InterruptedException e) {
                  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
  //			}
          }*/
		if (element != null) {
			super.visitElement(element);
		}
	}

	@Override
	public void visitFile(PsiFile file) {
		try {
//			lock.writeLock().lock();
			visitedFile = file;
			currentFileRefCollection = new FileReferenceCollection(file);
			/*	if (soDisplayProgress)
	         {
                   progressIndicator.setFraction(0.0 + (1 / 20));
               }*/
			super.visitFile(file);
		} finally {
			soDisplayProgress = false;
//			lock.writeLock().unlock();
		}
	}

	@Override
	public void visitXmlTag(XmlTag tag) {
        /*	if (soDisplayProgress) {
              progressIndicator.setFraction(0.0 + (1 / 30));
          }*/
//		System.out.println("xmlTag.getName() = " + tag.getName());
//		System.out.println("phase = " + phase);
        /*	if (tag.getName().equals(XmlParsingPhaseEnum.QUERIES_TAG.getXmlElement())) {
              phase = XmlParsingPhaseEnum.QUERIES_TAG;
          }*/
		if (tag.getName().equals(XmlParsingPhaseEnum.QUERY_TAG.getXmlElement())) {
			phase = XmlParsingPhaseEnum.QUERY_TAG;
		}
		super.visitXmlTag(tag);
	}

	@Override
	public void visitXmlElement(XmlElement element) {
        /*if (soDisplayProgress) {
              progressIndicator.setFraction(0.0 + (1 / 45));
          }*/
//		System.out.println("value");
//		System.out.println("phase = " + phase);
		if (XmlParsingPhaseEnum.QUERY_TAG.equals(phase) && XmlParsingPhaseEnum.ID_ATTRIBUTE.getXmlElement().equals(element.getText())) {
			phase = XmlParsingPhaseEnum.ID_ATTRIBUTE;
		}
		super.visitXmlElement(element);
	}

	@Override
	public void visitXmlAttributeValue(XmlAttributeValue value) {
        /*if (soDisplayProgress) {
              progressIndicator.setFraction(0.0 + (1 / 40));
          }*/
//		System.out.println("phase = " + phase);
//		System.out.println("xmlAttributeValue.getText() = " + value.getText());
		if (XmlParsingPhaseEnum.ID_ATTRIBUTE.equals(phase) && value.getValue().length() != 0) {
			phase = XmlParsingPhaseEnum.ID_VALUE;
			SQLRefReference refReference = new SQLRefReference(value, visitedFile, currentFileRefCollection);
			currentIndexedSQLRef = refReference;
            /*if (!isFirstEncounter) {
//                String containingFilePath = StringUtils.cleanPath(value.getContainingFile().getVirtualFile().getPath());
//                Project guessedProject = ProjectUtil.guessProjectForFile(value.getContainingFile().getVirtualFile());
//                String parentPath = PathUtil.getParentPath(containingFilePath);
            }*/
			putQueryReferenceToFileRefManager(visitedFile.getName(), refReference);
		}
		super.visitXmlAttributeValue(value);
	}

	@Override
	public void visitXmlDocument(XmlDocument document) {
        /*if (soDisplayProgress) {
              progressIndicator.setFraction(0.0 + (1 / 20));
          }*/
		super.visitXmlDocument(document);
	}

  /*  public int line(@NotNull PsiElement psiElement, @NotNull VirtualFile file) {
        Editor editor;
        if (FileEditorManager.getInstance(project).isFileOpen(file) && SQLRefApplication.getInstance(SQLRefDataAccessor.class).getEditor() != null) {
            editor = SQLRefApplication.getInstance(SQLRefDataAccessor.class).getEditor();
        } else {
            editor = FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, file, 0), false);
        }
        FileEditor[] fileEditors = FileEditorManager.getInstance(project).getEditors(file);
        for (FileEditor fileEditor : fileEditors) {
            if (fileEditor instanceof TextEditor) {
                editor = ((TextEditor) fileEditor).getEditor();
            }
        }
        if (editor != null) {
            return editor.offsetToVisualPosition(psiElement.getTextOffset()).line + 1;
        }
        return 0;
    }

    public int column(@NotNull PsiElement psiElement, @NotNull VirtualFile file) {
        Editor editor;
        if (FileEditorManager.getInstance(project).isFileOpen(file) && SQLRefApplication.getInstance(SQLRefDataAccessor.class).getEditor() != null) {
            editor = SQLRefApplication.getInstance(SQLRefDataAccessor.class).getEditor();
        } else {
            editor = FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, file, 0), false);
        }
        FileEditor[] fileEditors = FileEditorManager.getInstance(project).getEditors(file);
        for (FileEditor fileEditor : fileEditors) {
            editor = ((TextEditor) fileEditor).getEditor();
        }
        if (!FileEditorManager.getInstance(project).isFileOpen(file)) {
            FileEditorManager.getInstance(project).closeFile(file);
        }
        if (editor != null) {
            return editor.offsetToVisualPosition(psiElement.getTextOffset()).column + 1;
        }
        return 0;
    }*/

}
