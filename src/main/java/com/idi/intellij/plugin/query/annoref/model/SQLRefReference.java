package com.idi.intellij.plugin.query.annoref.model;

import com.idi.intellij.plugin.query.annoref.util.AnnRefApplication;
import com.idi.intellij.plugin.query.annoref.util.SQLRefDataAccessor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 05/11/2010
 * Time: 15:19:37
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class SQLRefReference implements Comparator<String> {
	private static final Logger LOGGER = Logger.getInstance(SQLRefReference.class.getName());
	private final String sqlRefId;
	@NotNull
	private PsiElement xmlPsiElement;
	private FileReferenceCollection fileRefCollectionParent;
	/**
	 * The java module the xml files resides in, used for reIndexing on project changed via maven pom.xmls
	 */
	private Module currentContainedModule;
	@NotNull
	private Map<String, PsiElement> classPsiElements = new ConcurrentHashMap<String, PsiElement>();
	private RangeHighlighter rangeHighlighter;
/*
	@Deprecated
	public SQLRefReference(@NotNull PsiReference reference) {
		xmlPsiElement = reference.getElement();
		sqlRefId = SQLRefApplication.getInstance(SQLRefNamingUtil.class).stripDoubleQuotes(xmlPsiElement.getText());
	}*/

	/**
	 * Used for cloning when reIndexing project
	 *
	 * @param sqlRefId
	 */
	public SQLRefReference(String sqlRefId) {
		this.sqlRefId = sqlRefId;
	}

	/**
	 * Used for cloning when reIndexing project
	 *
	 * @param rangeHighlighter
	 * @param sqlRefId
	 */
	public SQLRefReference(RangeHighlighter rangeHighlighter, String sqlRefId) {
		this.rangeHighlighter = rangeHighlighter;
		this.sqlRefId = sqlRefId;
	}

	public SQLRefReference(@NotNull XmlAttributeValue value, PsiFile visitedFile, FileReferenceCollection fileReferenceCollection) {
		this.xmlPsiElement = value;
		if (this.fileRefCollectionParent == null) {
			this.fileRefCollectionParent = fileReferenceCollection;
		}

		fileRefCollectionParent.setReferencedFile(visitedFile);
		sqlRefId = StringUtil.stripQuotesAroundValue(xmlPsiElement.getText());
	}

	public RangeHighlighter getRangeHighlighter() {
		return rangeHighlighter;
	}

	public void setRangeHighlighter(RangeHighlighter rangeHighlighter) {
		this.rangeHighlighter = rangeHighlighter;
	}

	public void addClassAnnoReference(PsiElement element) {
		String fileName = element.getContainingFile().getName();
		classPsiElements.put(fileName, element);
	}

	public boolean removeClassAnnoReference(String fileName) {
		if (classPsiElements.containsKey(fileName)) {
			PsiElement element = classPsiElements.remove(fileName);
			LOGGER.info(String.format("Class Reference %s was removed from SQLRefReference file : %s", element.getText(), containingVirtualFile().getName()));
			return true;
		}
		return false;
	}


	public PsiElement getClassElement(String refId) {
		return classPsiElements.get(refId);
	}

	@NotNull
	public Map<String, PsiElement> getClassPsiElements() {
		return classPsiElements;
	}

	public PsiElement[] getPsiElementsArray() {
		return classPsiElements.values().toArray(new PsiElement[classPsiElements.size()]);
	}

	public void setClassElements(Map<String, PsiElement> beforeClassPsiElements) {
		classPsiElements = beforeClassPsiElements;
	}

	public boolean hasClassesCorrelated() {
		return !classPsiElements.isEmpty();
	}

	@NotNull
	public Navigatable xmlLocation(Project project) {
		return new OpenFileDescriptor(AnnRefApplication.getInstance(project, SQLRefDataAccessor.class).getProject(), containingVirtualFile(), xmlPsiElement.getTextOffset());
	}

	public Navigatable classLocation(Project project, VirtualFile classVF) {
		return new OpenFileDescriptor(AnnRefApplication.getInstance(project, SQLRefDataAccessor.class).getProject(), classVF, ((PsiElement) classPsiElements.values().toArray()[0]).getTextOffset());
	}

	private void paintQueryId(@NotNull Editor editor) {
		MarkupModel markupModel = editor.getMarkupModel();
		Color color = editor.getColorsScheme().getDefaultForeground();
		TextAttributes textAttributes = new TextAttributes(color, null, Color.red, EffectType.WAVE_UNDERSCORE, Font.PLAIN);
		RangeHighlighter rangeHighlighter = markupModel.addRangeHighlighter(xmlPsiElement.getTextOffset(), xmlPsiElement.getTextOffset() + xmlPsiElement.getTextLength(),
				0, textAttributes, HighlighterTargetArea.EXACT_RANGE);

	}

	@Nullable
	public VirtualFile containingVirtualFile() {
		return containingFile().getVirtualFile();
	}

	public VirtualFile containingClassVirtualFile(String fileName) {
		PsiFile psiFile = containingClassFile(fileName);
		if (psiFile != null) {
			return psiFile.getVirtualFile();
		}
		return null;
	}

	@Nullable
	public PsiMethod containingMethod() {
		PsiElement current = xmlPsiElement;
		while (true) {
			PsiElement parent = current.getParent();
			if (parent instanceof PsiFile) {
				return null;
			}
			if (parent instanceof PsiMethod) {
				return (PsiMethod) parent;
			}
			current = parent;
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (final String classKey : classPsiElements.keySet()) {
			sb.append("Class file name :").append(classPsiElements.get(classKey).getContainingFile().getName());
			sb.append(System.getProperty("line.separator"));
		}
		return "SQLRefReference{" +
				"xmlPsiElement=" + xmlPsiElement +
				", classPsiElements=" + sb.toString() +
				", rangeHighlighter=" + rangeHighlighter +
				", sqlRefId='" + sqlRefId + '\'' +
				'}';
	}

	public PsiElement getXmlPsiElement() {
		return xmlPsiElement;
	}

	public PsiElement cloneXmlPsiElement() {
		return xmlPsiElement.copy();
	}

	public Map<String, PsiElement> cloneCorrelatedClasses() {
		Map<String, PsiElement> newCorrelatedClasses = new ConcurrentHashMap<String, PsiElement>();
		for (String key : classPsiElements.keySet()) {
			PsiElement classAnnoElement = classPsiElements.get(key);
			newCorrelatedClasses.put(key, classAnnoElement.copy());
		}
		return newCorrelatedClasses;
	}


	@Nullable
	public PsiClass containingClass() {
		PsiElement current = xmlPsiElement;
		while (true) {
			PsiElement parent = current.getParent();
			if (parent instanceof PsiFile) {
				return null;
			}
			if (parent instanceof PsiClass) {
				return (PsiClass) parent;
			}
			current = parent;
		}
	}


	public PsiFile containingClassFile(String fileName) {
		if (!classPsiElements.isEmpty()) {
			if (classPsiElements.size() > 1) {
				LOGGER.info("There are multiple class ref to the xml " + getXmlPsiElement().getText());
				for (PsiElement psiElement : classPsiElements.values()) {
					LOGGER.info("Class reference java file : " + psiElement.getContainingFile().getName());
				}
			}
			if (((PsiElement) classPsiElements.values().toArray()[classPsiElements.size() - 1]).isValid()) {
				return ((PsiElement) classPsiElements.values().toArray()[classPsiElements.size() - 1]).getContainingFile();
			}
		}
		return null;
	}

	public PsiFile containingFile() {
		return xmlPsiElement.getContainingFile();
	}

	/*

		public int line() {
			Editor editor = SQLRefApplication.getInstance(SQLRefDataAccessor.class).getEditor();
			FileEditor[] fileEditors = FileEditorManager.getInstance(SQLRefApplication.getInstance(SQLRefDataAccessor.class).getProject()).getEditors(containingVirtualFile());
			for (FileEditor fileEditor : fileEditors) {
				if (fileEditor instanceof TextEditor) {
					editor = ((TextEditor) fileEditor).getEditor();
				}
			}
			return editor.offsetToVisualPosition(xmlPsiElement.getTextOffset()).line + 1;
		}

		public int column() {
			Editor editor = SQLRefApplication.getInstance(SQLRefDataAccessor.class).getEditor();
			FileEditor[] fileEditors = FileEditorManager.getInstance(SQLRefApplication.getInstance(SQLRefDataAccessor.class).getProject()).getEditors(containingVirtualFile());
			for (FileEditor fileEditor : fileEditors) {
				editor = ((TextEditor) fileEditor).getEditor();
				paintQueryId(editor);
			}
			return editor.offsetToVisualPosition(xmlPsiElement.getTextOffset()).column + 1;
		}
	*/
	@Override
	public int compare(String refId1, String refId2) {
		return refId1.substring(0, 1).getBytes().length < refId2.substring(0, 1).getBytes().length ? -1 : refId1.substring(0, 1).getBytes().length == refId2.substring(0, 1).getBytes().length ? 0 : 1;
	}
}
