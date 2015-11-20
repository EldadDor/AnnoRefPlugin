package com.idi.intellij.plugin.query.annoref.index.visitors;

import com.idi.intellij.plugin.query.annoref.common.XmlParsingPhaseEnum;
import com.idi.intellij.plugin.query.annoref.index.SQLRefRepository;
import com.idi.intellij.plugin.query.annoref.repo.model.SQLRefReference;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.impl.source.parsing.xml.XmlBuilder;
import com.intellij.sql.dialects.sybase.SybaseDialect;
import com.intellij.sql.psi.*;
import com.intellij.sql.psi.impl.SqlPsiElementFactory;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by EAD-MASTER on 11/1/2014.
 */
public class AnnoRefXmlVisitor implements XmlBuilder {

	private static final Logger logger = Logger.getInstance(AnnoRefXmlVisitor.class.getName());
	private final Project project;
	private final VirtualFile virtualFile;
	private static final Pattern VO_SETTER_PARAM = Pattern.compile("(@\\w+[\b\\&]|@\\w+|%\\w+)", Pattern.CASE_INSENSITIVE);
	private static final Pattern VO_GETTER_PARAM = Pattern.compile("(?i)(^\\*)*(\\w+)|(\\w_\\w)+", Pattern.CASE_INSENSITIVE);
	private String annoRefId;
	private SQLRefReference sqlRefReference;


	public AnnoRefXmlVisitor(Project project, VirtualFile virtualFile) {
		this.project = project;
		this.virtualFile = virtualFile;
	}

	@Override
	public void doctype(@Nullable CharSequence publicId, @Nullable CharSequence systemId, int startOffset, int endOffset) {
		logger.info("doctype():");
	}

	@Override
	public ProcessingOrder startTag(CharSequence localName, String namespace, int startoffset, int endoffset, int headerEndOffset) {
		logger.info("startTag():");
		if (((String) localName).equalsIgnoreCase(XmlParsingPhaseEnum.QUERIES_TAG.getXmlElement())) {
			return ProcessingOrder.TAGS_AND_ATTRIBUTES_AND_TEXTS;
		}
		if (((String) localName).equalsIgnoreCase(XmlParsingPhaseEnum.QUERY_TAG.getXmlElement())) {
			return ProcessingOrder.TAGS_AND_ATTRIBUTES_AND_TEXTS;
		}
		return ProcessingOrder.TAGS;
	}

	@Override
	public void endTag(CharSequence localName, String namespace, int startoffset, int endoffset) {
		logger.info("endTag():");
	}

	@Override
	public void attribute(CharSequence name, CharSequence value, int startoffset, int endoffset) {
		logger.info("attribute(): name=" + name + " value=" + value);
		annoRefId = value.toString();
		sqlRefReference = ServiceManager.getService(project, SQLRefRepository.class).getSQLRefReferenceForID(annoRefId);
	}

	@Override
	public void textElement(CharSequence display, CharSequence physical, int startoffset, int endoffset) {
		if (display.toString().toUpperCase().contains("SELECT")) {
			final SqlStatement sqlStatement = SqlPsiElementFactory.createStatementFromText(physical.toString(), SybaseDialect.INSTANCE, project, null);
			final SqlExpression sqlExpression = SqlPsiElementFactory.createQueryExpressionFromText(physical.toString(), SybaseDialect.INSTANCE, sqlStatement.getContext());
			if (sqlExpression instanceof SqlQueryExpression) {
				final SqlQueryExpression queryExpression = (SqlQueryExpression) sqlExpression;
				if (queryExpression.getSelectClause() != null) {
					new AnnoRefSqlVisitor().visitSqlSelectClause(queryExpression.getSelectClause());
				}
				if (queryExpression.getTableExpression() != null && queryExpression.getTableExpression().getWhereClause() != null) {
					new AnnoRefSqlVisitor().visitSqlWhereClause(queryExpression.getTableExpression().getWhereClause());
				}
				if (queryExpression.getTableExpression() != null && queryExpression.getTableExpression().getFromClause() != null) {
					new AnnoRefSqlVisitor().visitSqlFromClause(queryExpression.getTableExpression().getFromClause());
				}
			} else {
				logger.info("textElement(): sqlExpression=" + sqlExpression.getClass());
			}
		}
	}

	@Override
	public void entityRef(CharSequence ref, int startOffset, int endOffset) {
		logger.info("entityRef():");
	}

	@Override
	public void error(String message, int startOffset, int endOffset) {
		logger.info("error():");
	}

	public class AnnoRefSqlVisitor extends SqlVisitor {

		@Override
		public void visitSqlElement(SqlElement sqlElement) {
			if (sqlElement instanceof SqlSelectClause) {
				final SqlSelectClause sqlSelectClause = (SqlSelectClause) sqlElement;
				logger.info("visitSqlElement(): size=" + sqlSelectClause.getExpressions().size());
				for (int i = 0; i < sqlSelectClause.getExpressions().size(); i++) {
					final SqlExpression sqlExpression = sqlSelectClause.getExpressions().get(i);
					if (sqlExpression instanceof SqlReferenceExpression) {
						if (((SqlReferenceExpression) sqlExpression).getReferenceElementType().equals(SqlCompositeElementTypes.SQL_COLUMN_REFERENCE)) {
							final String name = sqlExpression.getName();
							isValidSelectColumnName(name);
							logger.info("visitSqlElement(): SqlReferenceExpression name=" + name);
						}
					}
					if (sqlExpression instanceof SqlAsExpression) {
						final String name = ((SqlDefinition) sqlExpression).getNameElement().getName();
						logger.info("visitSqlElement(): SqlAsExpression name=" + name);
						isValidSelectColumnName(name);
					}
				}
			}
			if (sqlElement instanceof SqlWhereClause) {
				sqlElement.accept(new SqlVisitor() {
					@Override
					public void visitSqlElement(SqlElement o) {
						final Matcher matcher = VO_SETTER_PARAM.matcher(o.getText());
						while (matcher.find()) {
							final String group = matcher.group();
							sqlRefReference.addSqlSelectColumnToInformationMap(group.substring(1, group.length()));
						}
						super.visitSqlElement(o);
					}
				});
			}
			super.visitSqlElement(sqlElement);
		}

		private void isValidSelectColumnName(String name) {
			final Matcher matcher = VO_GETTER_PARAM.matcher(name);
			if (matcher.find()) {
				final String columnName = matcher.group();
				sqlRefReference.addSqlSelectColumnToInformationMap(columnName);
			} else {
				logger.warn("isValidSelectColumnName(): Selected Column=" + name + " is not regex valid");
			}
		}


		@Override
		public void visitSqlWhereClause(SqlWhereClause o) {
			super.visitSqlWhereClause(o);
		}

		@Override
		public void visitSqlFromClause(SqlFromClause o) {
			super.visitSqlFromClause(o);
		}

		@Override
		public void visitSqlSelectClause(SqlSelectClause o) {
			super.visitSqlSelectClause(o);
		}
	}
}
