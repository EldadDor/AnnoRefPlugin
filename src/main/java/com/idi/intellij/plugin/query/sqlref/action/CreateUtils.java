package com.idi.intellij.plugin.query.sqlref.action;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.impl.file.PsiDirectoryImpl;
import com.intellij.util.IncorrectOperationException;

import javax.swing.*;
import java.util.*;

public class CreateUtils {
	static Icon ICON_CLASS = IconLoader.getIcon("/nodes/class.png");
	static Icon ICON_INTERFACE = IconLoader.getIcon("/nodes/interface.png");
	static Icon ICON_ENUM = IconLoader.getIcon("/nodes/enum.png");
	static Icon ICON_ANNOTATION = IconLoader.getIcon("/nodes/annotationtype.png");
	static Icon ICON_CUSTOM = IconLoader.getIcon("/fileTypes/java.png");

	static Set<String> BUILT_IN_TEMPLATE_NAMES = new HashSet();
	static List<TemplateDescriptor> BUILT_IN_TEMPLATES = new ArrayList();

	protected static Logger fLog = Logger.getInstance("IntroduceSuperTypeIntention.class");

	private static void registerBuiltInTemplate(TemplateDescriptor aDescriptor) {
		BUILT_IN_TEMPLATES.add(aDescriptor);
		BUILT_IN_TEMPLATE_NAMES.add(aDescriptor.getTemplateName());
	}

	static List<TemplateDescriptor> getBuiltInDescriptors() {
		List result = new ArrayList(BUILT_IN_TEMPLATES);
		return result;
	}

	public static List<TemplateDescriptor> getTemplateDescriptors() {
		List result = getBuiltInDescriptors();

		return result;
	}

	public static void getCustomJavaTemplates(List<TemplateDescriptor> aResult) {
		FileTemplate[] templates = FileTemplateManager.getInstance().getAllTemplates();
		for (FileTemplate template : templates) {
			String extension = template.getExtension();
			if (extension.equals(StdFileTypes.JAVA.getDefaultExtension())) {
				String templateName = template.getName();
				if (!BUILT_IN_TEMPLATES.contains(templateName)) {
					TemplateDescriptor descriptor = new TemplateDescriptor(templateName, ICON_CUSTOM);
					aResult.add(descriptor);
				}
			}
		}
	}

	public static PsiClass create(PsiDirectoryImpl aDirectory, String aName, String aTemplateName, Project project) throws IncorrectOperationException {
//	  aDirectory.checkCreateClassOrInterface(aName);
		PsiManager manager = aDirectory.getManager();
		CodeStyleManager codestylemanager = CodeStyleManager.getInstance(manager.getProject());
		FileTemplate filetemplate = FileTemplateManager.getInstance().getInternalTemplate(aTemplateName);
		boolean flag = filetemplate.isReformatCode();
		Properties properties = FileTemplateManager.getInstance().getDefaultProperties();
		properties = new Properties(properties);
//    FileTemplateUtil.setPackageNameAttribute(properties, aDirectory);
		properties.setProperty("NAME", aName);
		String templateText;
		try {
			templateText = filetemplate.getText(properties);
		} catch (Exception exception) {
			throw new RuntimeException("Unable to load template for " + FileTemplateManager.getInstance().internalTemplateToSubject(aTemplateName), exception);
		}

//    PsiElementFactory psielementfactory = JavaPsiFacade.getInstance(project).getElementFactory();
		String javaExtension = StdFileTypes.JAVA.getDefaultExtension();
		PsiJavaFile psijavafile = (PsiJavaFile) PsiFileFactory.getInstance(project).createFileFromText(aName + "." + javaExtension, templateText);

		PsiClass[] apsiclass = psijavafile.getClasses();
		if ((apsiclass.length != 1) || (!apsiclass[0].getName().equals(aName))) {
			throw new IncorrectOperationException("File was not created by templat: " + aTemplateName);
		}
		if (flag) {
			codestylemanager.reformat(psijavafile);
		}
		PsiJavaFile psiJavaFile = (PsiJavaFile) aDirectory.add(psijavafile);
		return psiJavaFile.getClasses()[0];
	}

	static {
		registerBuiltInTemplate(new TemplateDescriptor("Class", ICON_CLASS));
		registerBuiltInTemplate(new TemplateDescriptor("Interface", ICON_INTERFACE));
		registerBuiltInTemplate(new TemplateDescriptor("Enum", ICON_ENUM));
		registerBuiltInTemplate(new TemplateDescriptor("Annotation", ICON_ANNOTATION));
	}
}
