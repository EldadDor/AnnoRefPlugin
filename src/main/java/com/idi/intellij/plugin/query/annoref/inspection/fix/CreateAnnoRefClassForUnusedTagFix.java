package com.idi.intellij.plugin.query.annoref.inspection.fix;

import com.idi.intellij.plugin.query.annoref.action.AnnoRefCreateClassDialog;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefConfigSettings;
import com.idi.intellij.plugin.query.annoref.util.StringUtils;
import com.intellij.codeInsight.CodeInsightUtil;
import com.intellij.codeInsight.daemon.impl.quickfix.CreateClassKind;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateBuilderFactory;
import com.intellij.codeInsight.template.TemplateBuilderImpl;
import com.intellij.codeInspection.LocalQuickFixBase;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.impl.source.codeStyle.ImportHelper;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.ClassUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 9/7/13
 * Time: 8:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class CreateAnnoRefClassForUnusedTagFix extends LocalQuickFixBase {
	private static final Logger logger = Logger.getInstance(CreateAnnoRefClassForUnusedTagFix.class.getName());

	private String targetPackage;
	private String targetClassName;
	private Module classTargetModule;
	private PsiDirectory sourceDir;

	private SmartPsiElementPointer<PsiJavaCodeReferenceElement> myRefElement;

	public CreateAnnoRefClassForUnusedTagFix(@NotNull String name, @NotNull String familyName, String targetPackage,
	                                         String targetClassName, Module classTargetModule, PsiDirectory sourceDir) {
		super(name, familyName);
		this.targetPackage = targetPackage;
		this.targetClassName = targetClassName;
		this.classTargetModule = classTargetModule;
		this.sourceDir = sourceDir;
	}

	public static void setAnnotationParameter(PsiAnnotation annotation, String paramName, String value) throws IncorrectOperationException {
		setAnnotationParameter(annotation, paramName, value, false);
	}

	public static void setAnnotationParameter(PsiAnnotation annotation, String paramName, String value, boolean strict) throws IncorrectOperationException {
		final PsiAnnotationMemberValue psiAnnotationMemberValue = annotation.setDeclaredAttributeValue(paramName, JavaPsiFacade.getInstance(annotation.getProject()).getElementFactory().createAnnotationFromText("@A(" + value + ")", null).findDeclaredAttributeValue(null));
	}

	public static void setAnnotationParameter(PsiMember member, String annoName, String paramName, String value) throws IncorrectOperationException {
		PsiModifierList modifierList = member.getModifierList();
		PsiAnnotation anno = modifierList.findAnnotation(annoName);

		if (anno == null) {
			PsiAnnotation newAnnotation = JavaPsiFacade.getInstance(member.getProject()).getElementFactory().createAnnotationFromText("@" + annoName, null);

			anno = (PsiAnnotation) modifierList.add(newAnnotation);
		}
		setAnnotationParameter(anno, paramName, value);
	}

	@Override
	public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor) {
		final AnnoRefCreateClassDialog annoRefCreateClassDialog = createAnnoRefClass(project);
	}

	@Nullable
	public AnnoRefCreateClassDialog createAnnoRefClass(Project project) {
		final AnnoRefCreateClassDialog dialog = initCreateClassDialog(project);
		if (dialog == null) return null;
		final PsiDirectory targetDirectory = dialog.getTargetDirectory();
		final String[] parentInterfaceName = AnnoRefConfigSettings.getInstance(project).getAnnoRefState().ANNO_REF_SUPER_INTERFACE.split("\\.");
		final String refIdName = dialog.getAnnoRefIDName();
		final String interfaceName = StringUtils.cleanSpecialCharacters(dialog.getClassName()) + parentInterfaceName[parentInterfaceName.length - 1];
		final PsiClass targetClass = JavaDirectoryService.getInstance().createInterface(targetDirectory, interfaceName);
		dialog.getPreferredFocusedComponent();

//		final PsiExpressionTrimRenderer psiExpressionTrimRenderer = new PsiExpressionTrimRenderer(new StringBuilder());
//		psiExpressionTrimRenderer.visitFile(targetClass.getContainingFile());

		setAnnotationParameter(targetClass, AnnoRefConfigSettings.getInstance(project).getAnnoRefState().ANNOREF_ANNOTATION_FQN,
				AnnoRefConfigSettings.getInstance(project).getAnnoRefState().ANNOREF_ANNOTATION_ATTRIBUTE_ID,
				String.valueOf(StringUtils.doubleQuote(refIdName)));
		final PsiElementFactory psiElementFactory = JavaPsiFacade.getInstance(project).getElementFactory();
//		final PsiAnnotation importClass1 = JavaPsiFacade.getInstance(project).getElementFactory().createAnnotationFromText("@" + AnnoRefConfigSettings.getInstance(project).getAnnoRefState().ANNOREF_ANNOTATION_FQN, null);
		final PsiClass importClass2 = psiElementFactory.createClass(ClassUtil.extractClassName(AnnoRefConfigSettings.getInstance(project).getAnnoRefState().ANNO_REF_SUPER_INTERFACE));
		final PsiJavaCodeReferenceElement referenceElementByFQClassName = psiElementFactory.
				createReferenceElementByFQClassName(AnnoRefConfigSettings.getInstance(project).
						getAnnoRefState().ANNO_REF_SUPER_INTERFACE, GlobalSearchScope.projectScope(project));
		targetClass.getExtendsList().add(referenceElementByFQClassName);
		final Editor editor = CodeInsightUtil.positionCursor(project, targetClass.getContainingFile(), targetClass.getLBrace());
		final TemplateBuilderImpl templateBuilder = editor != null
				? (TemplateBuilderImpl) TemplateBuilderFactory.getInstance().createTemplateBuilder(targetClass) : null;
		final Template template = templateBuilder.buildTemplate();
		template.setToShortenLongNames(true);
		template.setToReformat(true);
//		new ImportHelper(new CodeStyleSettings()).addImport((PsiJavaFile) targetClass.getContainingFile(), importClass1);
		new ImportHelper(new CodeStyleSettings()).addImport((PsiJavaFile) targetClass.getContainingFile(), importClass2);
		PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());
		if (targetDirectory == null) {
			return null;
		}
		return dialog;
	}

	private AnnoRefCreateClassDialog initCreateClassDialog(final Project project) {
		final AnnoRefCreateClassDialog dialog = new AnnoRefCreateClassDialog(project, getName(), targetClassName,
				targetPackage, CreateClassKind.INTERFACE, true, classTargetModule) {
			@Override
			protected PsiDirectory getBaseDir(String packageName) {
				return sourceDir;
			}

			@Override
			protected boolean reportBaseInTestSelectionInSource() {
				return true;
			}

			@Override
			protected void doOKAction() {
				super.doOKAction();

			}
		};

		dialog.show();
		if (!dialog.isOK()) {
			return null;
		}
		return dialog;
	}

}
