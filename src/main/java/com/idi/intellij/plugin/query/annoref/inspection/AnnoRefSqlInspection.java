package com.idi.intellij.plugin.query.annoref.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.sql.SqlDialects;
import com.intellij.sql.dialects.SqlLanguageDialect;
import com.intellij.sql.inspections.SqlInspectionBase;
import com.intellij.sql.psi.*;
import com.intellij.sql.psi.impl.SqlImplUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EAD-MASTER on 11/2/2014.
 */

public class AnnoRefSqlInspection extends SqlInspectionBase {

	public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
		if (!(file instanceof SqlFile)) {
			return null;
		}
		if (SqlImplUtil.getSqlDialectSafe(file) == SqlDialects.GENERIC) {
			return null;
		}

		List<ProblemDescriptor> result = new ArrayList<ProblemDescriptor>();
		final SqlAnnotationVisitor visitor = createAnnotationVisitor((SqlFile) file, manager, result, isOnTheFly);
		file.accept(new PsiRecursiveElementWalkingVisitor(true) {
			@Override
			public void visitElement(PsiElement element) {
				super.visitElement(element);
			}

			@Override
			protected void elementFinished(PsiElement element) {
				super.elementFinished(element);
			}
		});
		return (ProblemDescriptor[]) result.toArray(new ProblemDescriptor[result.size()]);
	}


	@Override
	protected SqlAnnotationVisitor createAnnotationVisitor(@NotNull SqlFile sqlFile, @NotNull InspectionManager manager, @NotNull List<ProblemDescriptor> result, boolean paramBoolean) {
		final SqlLanguageDialect language = sqlFile.getSqlLanguage();
		return new SqlInspectionBase.SqlAnnotationVisitor(manager, language, result) {
			@Override
			public void visitSqlSelectStatement(SqlSelectStatement o) {
				super.visitSqlSelectStatement(o);
			}

			@Override
			public void visitSqlSelectClause(SqlSelectClause o) {
				super.visitSqlSelectClause(o);
			}

			@Override
			public void visitSqlWhereClause(SqlWhereClause o) {
				o.accept(new SqlVisitor() {
					@Override
					public void visitSqlExpression(SqlExpression o) {
						super.visitSqlExpression(o);
					}

					@Override
					public void visitSqlClause(SqlClause o) {
						super.visitSqlClause(o);
					}

					@Override
					public void visitSqlParameter(SqlParameter o) {
						super.visitSqlParameter(o);
					}

					@Override
					public void visitSqlElement(SqlElement o) {
						super.visitSqlElement(o);
					}
				});
			}

			@Override
			public void visitSqlInsertStatement(SqlInsertStatement o) {
				super.visitSqlInsertStatement(o);
			}
		};
	}
}
