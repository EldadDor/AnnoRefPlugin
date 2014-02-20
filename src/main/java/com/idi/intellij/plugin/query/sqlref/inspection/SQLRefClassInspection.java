package com.idi.intellij.plugin.query.sqlref.inspection;

import com.idi.intellij.plugin.query.sqlref.index.SQLRefRepository;
import com.idi.intellij.plugin.query.sqlref.inspection.fix.CreateNewAnnoRefInFileFix;
import com.idi.intellij.plugin.query.sqlref.persist.SQLRefConfigSettings;
import com.idi.intellij.plugin.query.sqlref.repo.model.SQLRefReference;
import com.idi.intellij.plugin.query.sqlref.util.AnnoRefBundle;
import com.idi.intellij.plugin.query.sqlref.util.SQLRefNamingUtil;
import com.idi.intellij.plugin.query.sqlref.util.StringUtils;
import com.intellij.codeInsight.documentation.DocumentationComponent;
import com.intellij.codeInsight.documentation.DocumentationManager;
import com.intellij.codeInspection.*;
import com.intellij.javaee.dataSource.DataSource;
import com.intellij.javaee.dataSource.DataSourceManager;
import com.intellij.javaee.dataSource.ServerInstance;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.ui.components.JBLabel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 8/2/13
 * Time: 1:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class SQLRefClassInspection extends LocalInspectionTool {
	private static final Logger logger = Logger.getInstance(SQLRefClassInspection.class.getName());

	@NotNull
	@Override
	public String getID() {
		final String name = SQLRefClassInspection.class.getName();
		logger.info("getID(): name=" + name);
		return name;
	}

	@Nullable
	@Override
	public String getAlternativeID() {
		return super.getAlternativeID();
	}

	@Nullable
	@Override
	public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
		final List<ProblemDescriptor> problemDescriptorList = new ArrayList<ProblemDescriptor>();
		if (file instanceof PsiJavaFile) {
			logger.info("checkFile(): file=" + file);
			PsiAnnotation psiAnnotation = SQLRefNamingUtil.getAnnotationForConfiguredClassFile(file, SQLRefConfigSettings.getInstance(file.getProject()).getSqlRefState().
					ANNO_ANNOTATION_FQN);
			if (psiAnnotation != null) {
				logger.info("checkFile(): conversionClassFile=" + file.getName());
				final Module module = ModuleUtil.findModuleForPsiElement(psiAnnotation.getContainingFile());
				final PsiNameValuePair psiNameValuePair = psiAnnotation.getParameterList().getAttributes()[0];
				final SQLRefReference sqlRefReferenceForID = ServiceManager.getService(file.getProject(), SQLRefRepository.class).getSQLRefReferenceForID(psiNameValuePair.getValue().getText());
				problemDescriptorList.add(addProblemDescriptorForConversionCandidate((PsiClassOwner) file, manager, psiAnnotation, module));
			/*	try {
					datasourceTest(file.getProject());
				} catch (Exception e) {
					logger.error("checkFile(): Exception=" + e.getMessage(), e);
				}*/
			} else {
				logger.info("checkFile(): propitiousClassFile=" + file.getName());
				psiAnnotation = SQLRefNamingUtil.getAnnotationForPropitiousClassFile(file, null, SQLRefConfigSettings.getInstance(file.getProject()).getSqlRefState().
						ANNOREF_ANNOTATION_FQN);
				if (psiAnnotation != null) {
					final Module module = ModuleUtil.findModuleForPsiElement(psiAnnotation.getContainingFile());
					final PsiNameValuePair psiNameValuePair = psiAnnotation.getParameterList().getAttributes()[0];
					final String cleanAnnoRefId = StringUtils.cleanQuote(psiNameValuePair.getValue().getText());
					final SQLRefReference sqlRefReferenceForID = ServiceManager.getService(file.getProject(), SQLRefRepository.class).getSQLRefReferenceForID(cleanAnnoRefId);
					problemDescriptorList.add(addProblemDescriptorForUnUsedClass(psiAnnotation, module, ((PsiClassOwner) file).getPackageName(), psiNameValuePair, sqlRefReferenceForID, manager));
				}
			}
			if (!problemDescriptorList.isEmpty() && problemDescriptorList.get(0) != null) {
				return problemDescriptorList.toArray(new ProblemDescriptor[problemDescriptorList.size()]);
			}
		}
		return super.checkFile(file, manager, isOnTheFly);
	}

	private ProblemDescriptor addProblemDescriptorForConversionCandidate(PsiClassOwner file, InspectionManager manager, PsiAnnotation psiAnnotation, Module module) {
		logger.info("addProblemDescriptorForConversionCandidate():");
		return manager.createProblemDescriptor(psiAnnotation.getContext(),
				AnnoRefBundle.message("annoRef.conversion"),
				new CreateNewAnnoRefInFileFix("Create A @SQLRef and move sql to xml file", psiAnnotation, module, file.getPackageName()),
				ProblemHighlightType.INFORMATION, true);
	}


	private ProblemDescriptor addProblemDescriptorForUnUsedClass(PsiAnnotation classAnnotation, Module module, String packageName, PsiElement classAnnotationElement,
	                                                             SQLRefReference sqlRefReferenceForID, InspectionManager manager) {
		logger.info("addProblemDescriptorForUnUsedClass():");
		if (sqlRefReferenceForID.getXmlQueryElements().isEmpty()) {
			return manager.createProblemDescriptor(classAnnotationElement.getContext(),
					AnnoRefBundle.message("annoRef.class.inspection.unused.class"),
					new CreateNewAnnoRefInFileFix("Create corresponding Query for unused Annotation ref", classAnnotation, module, packageName),
					ProblemHighlightType.LIKE_UNUSED_SYMBOL, true);
		}
		return null;
	}

	public void createDocument(Project project) {
		final DocumentationManager documentationManager = DocumentationManager.getInstance(project);
		final DocumentationComponent component = new DocumentationComponent(documentationManager);

	}


	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
		if (logger.isDebugEnabled()) {
			logger.debug("buildVisitor():");
		}
		PsiFile file = session.getFile();
		logger.info("buildVisitor(): file=" + file.getName());
		return super.buildVisitor(holder, isOnTheFly, session);    //To change body of overridden methods use File | Settings | File Templates.
	}

	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
		if (logger.isDebugEnabled()) {
			logger.debug("buildVisitor():");
		}
		return super.buildVisitor(holder, isOnTheFly);    //To change body of overridden methods use File | Settings | File Templates.
	}

	@Nullable
	@Override
	public PsiNamedElement getProblemElement(PsiElement psiElement) {
		return super.getProblemElement(psiElement);    //To change body of overridden methods use File | Settings | File Templates.
	}

	@Override
	public void inspectionStarted(@NotNull LocalInspectionToolSession session, boolean isOnTheFly) {
		super.inspectionStarted(session, isOnTheFly);    //To change body of overridden methods use File | Settings | File Templates.
	}

	@Nullable
	@Override
	public JComponent createOptionsPanel() {
		final JPanel panel = new JPanel(new BorderLayout(2, 2));
		panel.add(new JBLabel("Still In Testing Phases"), BorderLayout.CENTER);
		return panel;
	}

	public void datasourceTest(Project project) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		DataSourceManager dataSourceManager = DataSourceManager.getInstance(project);
		DataSource dataSource = dataSourceManager.getDataSourceByName("TEST");
		String query = "exec sp_helptext ?";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = dataSource.getConnection(project).prepareStatement(query);
			stmt.setString(1, "ADD_PAY");
			rs = stmt.executeQuery();
			StringBuilder b = new StringBuilder();
			while (rs.next()) {
				b.append(rs.getString("Text"));
				System.out.println(b.toString());
			}
		} catch (Exception e) {
			logger.error("datasourceTest(): Exception=" + e.getMessage(), e);
		} finally {
			try {
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				logger.error("datasourceTest(): Exception=" + e.getMessage(), e);
			}
		}
		Method getConnectionMethod = dataSource.getClass().getDeclaredMethod("getConnection", Project.class, ServerInstance.class);
		getConnectionMethod.setAccessible(true);
		Connection conn = (Connection) getConnectionMethod.invoke(dataSource, project, null);
	}
}
