package com.idi.intellij.plugin.query.sqlref.util;

import com.idi.intellij.plugin.query.sqlref.common.XmlParsingPhaseEnum;
import com.idi.intellij.plugin.query.sqlref.index.listeners.XmlVisitorListener;
import com.intellij.openapi.diagnostic.Logger;
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
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 7/26/13
 * Time: 12:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class SQLRefXmlVisitor extends XmlRecursiveElementVisitor {
	private static final Logger logger = Logger.getInstance(SQLRefXmlVisitor.class.getName());
	private static SQLRefXmlVisitor instance;
	@NotNull
	private XmlParsingPhaseEnum phase = XmlParsingPhaseEnum.WAITING;
	private Project project;
	private Boolean inspector = false;
	//	private ProgressIndicator progressIndicator;
//    private static JBReentrantReadWriteLock lock = LockFactory.createReadWriteLock();
//	private volatile boolean isMultipleAttributes;
	private boolean isFirstEncounter;
	private XmlVisitorListener xmlVisitorListener;

	public static SQLRefXmlVisitor getInstance(Project project) {
		try {
			if (instance == null) {
				instance = new SQLRefXmlVisitor();
			} else {
			}
			instance.project = project;
			return instance;
		} catch (Exception ex) {
			logger.error("SQLRefXmlVisitor(): Error=" + ex.getMessage(), ex);
			return null;
		}
	}

	public SQLRefXmlVisitor setXmlVisitorListener(XmlVisitorListener xmlVisitorListener) {
		this.xmlVisitorListener = xmlVisitorListener;
		return instance;
	}

	public Boolean istInspector() {
		return inspector;
	}

	public SQLRefXmlVisitor setInspector(Boolean inspector) {
		this.inspector = inspector;
		return instance;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	@Override
	public void visitElement(PsiElement element) {
		if (element != null) {
			super.visitElement(element);
		}
	}

	@Override
	public void visitFile(PsiFile file) {
		super.visitFile(file);

	}

	@Override
	public void visitXmlTag(XmlTag tag) {
		if (tag.getName().equals(XmlParsingPhaseEnum.QUERY_TAG.getXmlElement())) {
			phase = XmlParsingPhaseEnum.QUERY_TAG;
		/*	if (tag.getAttributes().length == 2) {
				isMultipleAttributes = true;
			} else {
				isMultipleAttributes = false;
			}*/
		}
		super.visitXmlTag(tag);
	}

	@Override
	public void visitXmlElement(XmlElement element) {
		if (XmlParsingPhaseEnum.QUERY_TAG.equals(phase) && XmlParsingPhaseEnum.ID_ATTRIBUTE.getXmlElement().equals(element.getText())) {
			phase = XmlParsingPhaseEnum.ID_ATTRIBUTE;
		}
		super.visitXmlElement(element);
	}

	@Override
	public void visitXmlAttributeValue(XmlAttributeValue value) {
		if (XmlParsingPhaseEnum.ID_ATTRIBUTE.equals(phase) && !value.getValue().isEmpty()) {
			phase = XmlParsingPhaseEnum.ID_VALUE;
			/*if (!isFirstEncounter) {
				String containingFilePath = StringUtils.cleanPath(value.getContainingFile().getVirtualFile().getPath());
				Project guessedProject = ProjectUtil.guessProjectForFile(value.getContainingFile().getVirtualFile());
				String parentPath = PathUtil.getParentPath(containingFilePath);
				System.out.println("containingFilePath= " + containingFilePath + "\nparentPath = " + parentPath + "\nguessedProject: " + guessedProject);
			}*/
//			if (istInspector()) {
//				if (isMultipleAttributes) {
//					logger.info("visitXmlAttributeValue(): multipleAttributes Found For RefId=" + value.getValue());
//					xmlVisitorListener.foundDoubleAttributeValidRefId(value.getValue(), value);
//				} else {
			xmlVisitorListener.foundValidRefId(value.getValue(), value);
//				}
//			}
		}
		super.visitXmlAttributeValue(value);
	}

	@Override
	public void visitXmlDocument(XmlDocument document) {
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
