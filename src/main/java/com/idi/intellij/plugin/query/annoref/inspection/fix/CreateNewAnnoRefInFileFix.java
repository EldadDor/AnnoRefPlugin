package com.idi.intellij.plugin.query.annoref.inspection.fix;

import com.idi.intellij.plugin.query.annoref.util.AnnRefApplication;
import com.idi.intellij.plugin.query.annoref.util.SQLRefDataAccessor;
import com.intellij.codeInspection.LocalQuickFixBase;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.impl.DirectoryIndex;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.FilteredQuery;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 10/4/13
 * Time: 1:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class CreateNewAnnoRefInFileFix extends LocalQuickFixBase {
	private static final Logger log = Logger.getInstance(CreateNewAnnoRefInFileFix.class.getName());
	private String annoRefId;
	private String classPackageName;
	private Module classModule;
	private PsiAnnotation annoRefAnnotation;

	protected CreateNewAnnoRefInFileFix(@NotNull String name, @NotNull String familyName) {
		super(name, familyName);
	}

	public CreateNewAnnoRefInFileFix(@NotNull String name, PsiAnnotation annoRefAnnotation, Module classTargetModule, String classPackageName) {
		super(name);
		this.annoRefAnnotation = annoRefAnnotation;
		classModule = classTargetModule;
		this.classPackageName = classPackageName;
	}

	@Override
	public void applyFix(@NotNull final Project project, @NotNull ProblemDescriptor problemDescriptor) {
		PsiElement psiElement = problemDescriptor.getPsiElement();
		final TextRange textRange = psiElement.getTextRange();

//		if (psiElement instanceof PsiModifierList) {
			/*final PsiAnnotation[] annotations = ((PsiModifierList) psiElement).getAnnotations();
			for (PsiAnnotation annotation : annotations) {*/
		final String moduleFilePath = classModule.getModuleFilePath();

		final String fileInProvidedPath = FileUtil.findFileInProvidedPath(moduleFilePath, "");
		final List<File> filesByMask = FileUtil.findFilesByMask(Pattern.compile(""), new File(""));
//		final List<VirtualFile> queries = findResourceFileInScope("queries", project, GlobalSearchScope.projectScope(project));
		DirectoryIndex.getInstance(project).getDirectoriesByPackageName("queries", false).forEach(new Processor<VirtualFile>() {
			@Override
			public boolean process(VirtualFile virtualFile) {
				for (final VirtualFile childFile : virtualFile.getChildren()) {
					if (isValidResourceFile(childFile, project)) {
						log.info("process(): virtualFile=" + virtualFile.getName() + " children=" + virtualFile.getChildren().length);
					}
				}
				return true;
			}
		});
//ResourceFileUtil.findResourceFileInDependents(classModule)
	/*	annoRefId = String.valueOf(annoRefAnnotation.getParameterList().getAttributes()[0].getValue());
		final CreateNewAnnoRefIdInXmlDialog annoRefIdInXmlDialog = new CreateNewAnnoRefIdInXmlDialog(project,
				classModule, classPackageName, annoRefId);*/
//		}
	}

	private boolean isValidResourceFile(VirtualFile virtualFile, Project project) {
		final String name = virtualFile.getName();
		final Boolean matchFileName = AnnRefApplication.getInstance(project, SQLRefDataAccessor.class).isMatchFileName(name);
		log.info("isValidResourceFile(): name=" + name + " isValid=" + matchFileName);
		return matchFileName;
	}

	@Nullable
	public static List<VirtualFile> findResourceFileInScope(final String resourceName,
	                                                        final Project project,
	                                                        final GlobalSearchScope scope) {
		int index = resourceName.lastIndexOf('/');
		String packageName = index >= 0 ? resourceName.substring(0, index).replace('/', '.') : "";
		final String fileName = index >= 0 ? resourceName.substring(index + 1) : resourceName;

		final Collection<VirtualFile> dir = new FilteredQuery<VirtualFile>(
				DirectoryIndex.getInstance(project).getDirectoriesByPackageName(packageName, false), new Condition<VirtualFile>() {
			@Override
			public boolean value(final VirtualFile file) {
				final VirtualFile child = file.findChild(fileName);
				return child != null && scope.contains(child);
			}
		}
		).findAll();
		return (List<VirtualFile>) dir;
	}

}

