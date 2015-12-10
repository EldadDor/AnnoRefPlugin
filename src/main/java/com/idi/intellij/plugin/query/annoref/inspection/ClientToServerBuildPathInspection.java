/*
 * User: eldad.Dor
 * Date: 21/11/2015 14:31
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.inspection;

import com.google.common.collect.Sets;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefConfigSettings;
import com.idi.intellij.plugin.query.annoref.util.AnnoRefBundle;
import com.idi.intellij.plugin.query.annoref.util.SQLRefNamingUtil;
import com.intellij.analysis.AnalysisScope;
import com.intellij.codeInspection.*;
import com.intellij.codeInspection.reference.RefElement;
import com.intellij.codeInspection.reference.RefEntity;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.pom.PomNamedTarget;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiLiteralExpressionImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ReflectionUtil;
import com.siyeh.ig.BaseGlobalInspection;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.model.MavenArtifactNode;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author eldad
 * @date 21/11/2015
 */
public class ClientToServerBuildPathInspection extends BaseGlobalInspection {
	private static final Logger logger = Logger.getInstance(ClientToServerBuildPathInspection.class.getName());
	private boolean isInspected;

	@Nullable
	@Override
	public String getAlternativeID() {
		return super.getAlternativeID();
	}

	@Nls @NotNull @Override public String getDisplayName() {
		return AnnoRefBundle.message("annoRef.inspection.global.client.to.server.displayname");
	}

	@Nullable @Override
	public CommonProblemDescriptor[] checkElement(@NotNull RefEntity refEntity, @NotNull AnalysisScope scope, @NotNull InspectionManager manager,
			@NotNull GlobalInspectionContext globalContext) {
		ReflectionUtil.getClassDeclaredFields(scope.getClass());

		final PsiElement myElement = ReflectionUtil.getField(scope.getClass(), scope, PsiElement.class, "myElement");
		final PsiClass classChild = PsiTreeUtil.findChildOfType(myElement, PsiClass.class);
		PsiElement element = null;
		boolean shouldIncludeLibraries = false;
		if (refEntity instanceof RefElement) {
			element = ((RefElement) refEntity).getElement();
		} else if (classChild != null) {
			element = classChild;
			shouldIncludeLibraries = true;
		}
		if (!isInspected && element != null) {
			isInspected = true;
			final CommonProblemDescriptor[] problemDescriptorList = createCommonProblemDescriptorsForValidElement(manager, element, shouldIncludeLibraries);
			if (problemDescriptorList != null) {
				return problemDescriptorList;
			}
		}
		return super.checkElement(refEntity, scope, manager, globalContext);
	}

	@Nullable
	private CommonProblemDescriptor[] createCommonProblemDescriptorsForValidElement(@NotNull InspectionManager manager, PsiElement element, boolean shouldIncludeLibraries) {
		if (element instanceof PsiClass && ((PsiModifierListOwner) element).hasModifierProperty(PsiModifier.PUBLIC)) {
			final List<ProblemDescriptor> problemDescriptorList = new ArrayList<ProblemDescriptor>();
			final Project project = manager.getProject();

			PsiAnnotation psiAnnotation = SQLRefNamingUtil.getPropitiousClassElementAnnotation((PsiClass) element, AnnoRefConfigSettings.getInstance(project).getAnnoRefState().
					IMPLEMENTED_BY_FRAMEWORK_FQN);
			if (psiAnnotation != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("checkFile(): conversionClassFile=" + ((PomNamedTarget) element).getName());
				}
				final PsiAnnotationMemberValue value = psiAnnotation.findAttributeValue("value");
				MavenProjectsManager.getInstance(project).getModules(MavenProjectsManager.getInstance(project).getRootProjects().get(0));
				final List<MavenArtifactNode> dependencyTree = MavenProjectsManager.getInstance(project).getRootProjects().get(0).getDependencyTree();
				Set<Module> rootModules = Sets.newHashSet();
				Set<Module> dependentModules = Sets.newHashSet();
				GlobalSearchScope searchScope = GlobalSearchScope.EMPTY_SCOPE;
				for (final MavenArtifactNode mavenArtifactNode : dependencyTree) {
					final Module moduleByName = ModuleManager.getInstance(project).findModuleByName(mavenArtifactNode.getArtifact().getArtifactId());
					if (moduleByName != null) {
						rootModules.add(moduleByName);
					}
				}
				for (final Module module : rootModules) {
//					final GlobalSearchScope moduleWithDependenciesScope = module.getModuleWithDependenciesScope();
					final GlobalSearchScope moduleWithDependenciesScope = module.getModuleWithDependenciesAndLibrariesScope(false);
					searchScope = searchScope.union(moduleWithDependenciesScope);
				}
				final PsiClass implementationClass = JavaPsiFacade.getInstance(project).findClass(((PsiLiteralExpressionImpl) value).getInnerText(), searchScope);
				logger.info("checkFile(): implementationClass=" + implementationClass);
				if (implementationClass == null) {
					problemDescriptorList.add(addProblemDescriptorForUnResolvedDependencies(value, manager, true));
				}
			}
			if (!problemDescriptorList.isEmpty() && problemDescriptorList.get(0) != null) {
				return problemDescriptorList.toArray(new ProblemDescriptor[problemDescriptorList.size()]);
			}
		}
		return null;
	}

	private ProblemDescriptor addProblemDescriptorForUnResolvedDependencies(PsiElement psiElement, InspectionManager manager, Boolean isRegister) {
		logger.info("addProblemDescriptorForUnResolvedDependencies():");
		ProblemDescriptor problemDescriptor = null;
		final LocalQuickFix fix = new LocalQuickFix() {
			@Nls @NotNull @Override public String getName() {
				return null;
			}

			@NotNull @Override public String getFamilyName() {
				return null;
			}

			@Override public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {

			}
		};
		if (isRegister) {
			problemDescriptor = manager.createProblemDescriptor(psiElement.getContext(),
					AnnoRefBundle.message("annoRef.class.inspection.client.to.server.dep"), fix, ProblemHighlightType.ERROR, true);
		} else {
			problemDescriptor = manager.createProblemDescriptor(psiElement.getContext(), null, fix, null, false);
		}
		return problemDescriptor;
	}


}