/*
 * User: eldad.Dor
 * Date: 31/03/2015 14:34
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.codeInsight;

import com.idi.intellij.plugin.query.annoref.common.XmlParsingPhaseEnum;
import com.idi.intellij.plugin.query.annoref.component.AnnoRefDataKey;
import com.idi.intellij.plugin.query.annoref.connection.DataSourceAccessorComponent;
import com.idi.intellij.plugin.query.annoref.index.SQLRefRepository;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefConfigSettings;
import com.idi.intellij.plugin.query.annoref.repo.model.SQLRefReference;
import com.idi.intellij.plugin.query.annoref.util.SQLRefNamingUtil;
import com.idi.intellij.plugin.query.annoref.util.StringUtils;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageDialect;
import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.pom.PomNamedTarget;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlText;
import com.intellij.psi.xml.XmlToken;
import com.intellij.sql.dialects.sybase.SybaseDialect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Random;

/**
 * @author eldad
 * @date 31/03/2015
 */
public class MyJavaDocumentationProvider extends AbstractDocumentationProvider {
	private static final Logger logger = Logger.getInstance(MyJavaDocumentationProvider.class);

	@Override
	public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
//		logger.info("getDocumentationElementForLookupItem():");
		return super.getDocumentationElementForLookupItem(psiManager, object, element);
	}

	@Override
	public PsiElement getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
//		logger.info("getDocumentationElementForLink():");
		return super.getDocumentationElementForLink(psiManager, link, context);
	}

	@Nullable
	@Override
	public PsiElement getCustomDocumentationElement(@NotNull Editor editor, @NotNull PsiFile file, PsiElement contextElement) {
//		logger.info("getCustomDocumentationElement():");
//		final String textBuilder = generateDocInformation(contextElement);

//		return PsiFileFactory.getInstance(file.getProject()).createFileFromText(textBuilder, SybaseDialect.INSTANCE, textBuilder, true, true);
//		editor = SQLRefApplication.getInstance(file.getProject(), SybaseLanguageManager.class).initializeSqlSyntaxForEditor(file.getProject(), textBuilder);

		return super.getCustomDocumentationElement(editor, file, contextElement);
	}

	@Nullable
	@Override
	public Image getLocalImageForElement(@NotNull PsiElement element, @NotNull String imageSpec) {
//		logger.info("getLocalImageForElement():");
		return super.getLocalImageForElement(element, imageSpec);
	}

	@Override
	public String getQuickNavigateInfo(final PsiElement psiElement, PsiElement psiElement1) {
//		logger.info("getQuickNavigateInfo():");
		final Project project = psiElement.getProject();
		if (psiElement instanceof PsiAnnotation) {
			final PsiElement javaAnnoRefElement = PsiTreeUtil.findFirstParent(psiElement, new Condition<PsiElement>() {
				@Override public boolean value(PsiElement psiElement) {
					if (psiElement instanceof PsiClass) {
						return true;
					}
					return false;
				}
			});
			return "[" + ((PsiQualifiedNamedElement) javaAnnoRefElement).getQualifiedName() + "]";
		}
		if (psiElement instanceof PsiJavaToken && ((PsiJavaToken) psiElement).getTokenType() == JavaTokenType.STRING_LITERAL) {
//			logger.info("getQuickNavigateInfo(): THIS IS IT!");
			if (psiElement.getUserData(AnnoRefDataKey.GOTO_DECLARATION_ANN_REF) != null) {
				final AnnoRefGotoTargetModel targetModel = psiElement.getUserData(AnnoRefDataKey.GOTO_DECLARATION_ANN_REF);
				switch (targetModel.getOption()) {
					case SP_ANNO:
						SPToolWindowDisplayHelper.displayStorageProcedureText(psiElement.getContainingFile(), project);
						return "SP is displayed from DB=" + AnnoRefConfigSettings.getInstance(project).getAnnoRefState().SP_DATA_SOURCE_NAME;
					case SQLREF_ANNO:
						final SQLRefReference referenceForID = ServiceManager.getService(project, SQLRefRepository.class).getSQLRefReferenceForID(StringUtils.cleanQuote(psiElement.getText()));
						return "[ xml queries file = " + referenceForID.getXmlQueryElements().get(0).getContainingFile().getName().trim() + " ]";
				}
//			logger.info("getQuickNavigateInfo():");
			}
		}
		if (psiElement instanceof XmlAttributeValue) {
			return "[   id =  \"" + ((XmlAttributeValue) psiElement).getValue() + "\"" + "<br>XML file = " + psiElement.getContainingFile().getName() +
					"   ]";


		}
		if (psiElement instanceof PsiMethodCallExpression) {
			if (psiElement.getParent() instanceof PsiLocalVariable) {
				return "[ SQL will be assign to local method field = " + ((NavigationItem) psiElement.getParent()).getPresentation().getPresentableText() + " ]";
			}
			if (psiElement.getParent() instanceof PsiAssignmentExpression) {
				return "[ SQL will be assign to local class field = " + ((PsiAssignmentExpression) psiElement.getParent()).getLExpression().getText() + " ]";
			}
		}
		if (psiElement instanceof LeafPsiElement && psiElement.getLanguage().equals(SybaseDialect.INSTANCE)) {
			if (ServiceManager.getService(project, DataSourceAccessorComponent.class).getSpNames().containsKey(psiElement.getText())) {
				return "[   SP =  \"" + psiElement.getText() + "\"" + " ]";
			} else {
				return null;
			}
		}
		return super.getQuickNavigateInfo(psiElement, psiElement1);
	}

	@Override
	public String generateDoc(PsiElement psiElement, PsiElement psiElement1) {
//		return super.generateDoc(psiElement, psiElement1);
		final Project project = psiElement.getProject();
		final PsiFile containingFile = psiElement1.getContainingFile();
		if (psiElement1 instanceof XmlToken && SQLRefNamingUtil.isPropitiousXmlFile(containingFile)) {
			final SQLRefReference sqlRefReference = ServiceManager.getService(project, SQLRefRepository.class).getSQLRefReferenceForID(psiElement1.getText());
			PsiElement javaAnnoRefElement = null;
			if (!sqlRefReference.getClassSmartPointersElements().isEmpty()) {
				final SmartPsiElementPointer<PsiElement> psiElementPointer = sqlRefReference.getClassSmartPointersElements().get(0);
				javaAnnoRefElement = PsiTreeUtil.findFirstParent(psiElementPointer.getElement(), new Condition<PsiElement>() {
					@Override public boolean value(PsiElement psiElement) {
						if (psiElement instanceof PsiClass) {
							return true;
						}
						return false;
					}
				});
			} else if (!sqlRefReference.getUtilClassSmartPointersElements().isEmpty()) {
				javaAnnoRefElement = ((SmartPsiElementPointer) sqlRefReference.getUtilClassSmartPointersElements().values().toArray()[0]).getElement();
			}
			if (javaAnnoRefElement != null) {
				final int rand = new Random().nextInt(11) + 1;
//				logger.info("generateDoc(): rand=" + rand);
//				final URL resource = getClass().getClassLoader().getResource("icons/starwars_"+rand+".png");
				final URL resource = getClass().getClassLoader().getResource("icons/nature/nature_" + rand + ".jpg");
				String urlAsString = null;
				try {
					urlAsString = resource.toURI().toString();
				} catch (URISyntaxException e) {
					logger.error(e);
				}
				return buildJavaDocInformationForElement(javaAnnoRefElement, urlAsString);
				/*return "<html><div>" +
						"<p style=\"color: #5935ed;font-size: 20px\">The connected Class is =" + ((PomNamedTarget) javaAnnoRefElement).getName() + "</p><p>FQN=" + ((PsiClass)
						javaAnnoRefElement).getQualifiedName() + "</p><br>" + "<img src=" + urlAsString + " style=\"width: 880px; height: 500px; margin: 10px; padding: 10px; " +
						"border\"/>" +
						"</div></html>";*/
			}
		}
		final String displayClassDoc = displayClassDoc(psiElement);
		if (displayClassDoc != null) {
			return displayClassDoc;
		}
		return super.generateDoc(psiElement, psiElement1);
	}

	private String buildJavaDocInformationForElement(PsiElement psiElement, String urlAsString) {
		if (psiElement instanceof PomNamedTarget) {
			return "<html><div>" +
					"<p style=\"color: #5935ed;font-size: 20px\">The connected Class is = " + ((PomNamedTarget) psiElement).getName() + "</p><p>FQN=" + ((PsiClass)
					psiElement).getQualifiedName() + "</p><br>" + "<img src=" + urlAsString + " style=\"width: 880px; height: 500px; margin: 10px; padding: 10px; " +
					"border\"/>" +
					"</div></html>";
		}

		final PsiElement firstParent = PsiTreeUtil.findFirstParent(psiElement, new Condition<PsiElement>() {
			@Override public boolean value(PsiElement psiElement) {
				return psiElement instanceof PsiClass;
			}
		});
		return "<html><div>" +
				"<p style=\"color: #5935ed;font-size: 20px\">The assigned field is = " + ((PomNamedTarget) psiElement.getParent()).getName() + "</p><p>Class=" + ((PsiClass)
				firstParent).getQualifiedName() + "</p><br>" + "<img src=" + urlAsString + " style=\"width: 880px; height: 500px; margin: 10px; padding: 10px; " +
				"border\"/>" +
				"</div></html>";
	}


	@Nullable
	private String generateDocInformation(PsiElement psiElement) {
		final String stripCDATA = displayClassDoc(psiElement);
		if (stripCDATA != null) {
			return stripCDATA;
		}
		return null;
	}

	@Nullable private String displayClassDoc(PsiElement psiElement) {
		if (psiElement.getContainingFile() != null && psiElement.getContainingFile() instanceof PsiJavaFile) {
			final Project project = psiElement.getProject();
			final PsiElement classModifierListOwnerElement = PsiTreeUtil.findFirstParent(psiElement, new Condition<PsiElement>() {
				@Override
				public boolean value(PsiElement psiElement) {
					if (psiElement instanceof PsiModifierListOwner) {
						return true;
					}
					return false;
				}
			});
			if (classModifierListOwnerElement instanceof PsiModifierListOwner) {
				for (PsiAnnotation annotation : ((PsiModifierListOwner) classModifierListOwnerElement).getModifierList().getAnnotations()) {
					if (annotation != null && AnnoRefConfigSettings.getInstance(project).getAnnoRefState().ANNOREF_ANNOTATION_FQN.equals(annotation.getQualifiedName())) {
						final String cleanedAnnoRef = SQLRefNamingUtil.cleanAnnoRefForName(psiElement.getContainingFile(), annotation);
						SQLRefReference sqlRefReferenceForID = ServiceManager.getService(project, SQLRefRepository.class).getSQLRefReferenceForID(cleanedAnnoRef);
						if (sqlRefReferenceForID != null) {
							for (final PsiElement xmlQueryElement : sqlRefReferenceForID.getXmlQueryElements()) {
								final PsiElement queryParentElement = PsiTreeUtil.findFirstParent(xmlQueryElement, new Condition<PsiElement>() {
									@Override
									public boolean value(PsiElement psiElement) {
										if (psiElement instanceof XmlTag && ((PsiNamedElement) psiElement).getName().equals(XmlParsingPhaseEnum.QUERY_TAG.getXmlElement())) {
											return true;
										}
										return false;
									}
								});
								final XmlText xmlTextChildElement = PsiTreeUtil.findChildOfType(queryParentElement, XmlText.class);
								final String xmlTextChildElementText = xmlTextChildElement.getText();
								final String stripCDATA = StringUtils.stripCDATA(xmlTextChildElementText);
//								return "<html><div>" + stripCDATA + "<br>" + "</div></html>";
								return "<html><div>" + stripCDATA.replaceAll("(\r\n|\r|\n|\n\r)", "<br>") + "</div></html>";
							}
						}
					}
				}
			}
		}
		return null;
	}

/*
	private void format(Document document, PsiElement psiElement, int startOffset, int endOffset) {
	    Language language = getLanguage(psiElement);
		CodeStyleSettings styleCaseSettings = CodeStyleSettingsManager.getSettings(psiElement.getProject());
	      PsiElement child = psiElement.getFirstChild();
	      while (child != null) {
	        if ((child instanceof LeafPsiElement)) {
	          TextRange textRange = child.getTextRange();
	          boolean isInRange = (startOffset == endOffset) || ((textRange.getStartOffset() >= startOffset) && (textRange.getEndOffset() <= endOffset));

	          if (isInRange) {
	            CodeStyleCaseOption caseOption = null;
	            if ((child instanceof IdentifierPsiElement)) {
	              IdentifierPsiElement identifierPsiElement = (IdentifierPsiElement)child;
	              if ((identifierPsiElement.isObject()) && (!identifierPsiElement.isQuoted())) {
	                caseOption = styleCaseSettings.getObjectCaseOption();
	              }
	            }
	            else if ((child instanceof TokenPsiElement)) {
	              TokenPsiElement tokenPsiElement = (TokenPsiElement)child;
	              TokenType tokenType = tokenPsiElement.getElementType().getTokenType();
	              caseOption = tokenType.isDataType() ? styleCaseSettings.getDatatypeCaseOption() : tokenType.isParameter() ?
			              styleCaseSettings.getParameterCaseOption() : tokenType.isFunction() ? styleCaseSettings.getFunctionCaseOption() :
			              tokenType.isKeyword() ? styleCaseSettings.getKeywordCaseOption() : null;
	            }

	            if (caseOption != null) {
	              String text = child.getText();
	              String newText = caseOption.changeCase(text);

	              if ((newText != null) && (!newText.equals(text)))
	                document.replaceString(textRange.getStartOffset(), textRange.getEndOffset(), newText);
	            }
	          }
	        }
	        else {
	          format(document, child, startOffset, endOffset);
	        }
	        child = child.getNextSibling();
	      }
	    }*/

	public static Language getLanguage(PsiElement element) {
		Language language = element.getLanguage();
		if ((language instanceof LanguageDialect)) {
			LanguageDialect languageDialect = (LanguageDialect) language;
			language = languageDialect.getBaseLanguage();
		}
		return language;
	}

	public static void main(String[] args) {
		final String s = "\n" +
				"            select MP.BASKET_NR,\n" +
				"            MP.SRV_ACTIVITY,\n" +
				"            MP.PRIORITY,\n" +
				"            MP.NO_EMAIL_SEND_FLAG,\n" +
				"            MP.FAX_OUT_NR,\n" +
				"            MP.INSERT_USER,\n" +
				"            MP.PRINT_NR,\n" +
				"            NQ.NDPS_NAME DEST_QUEUE,\n" +
				"            convert(char(11),convert(int,substring(MP.TIMESTAMP,5,4))) TIMESTAMP,\n" +
				"            MP.CASEFET_PATH,\n" +
				"            MP.MAIL_NR\n" +
				"            from M_SRV_PRN MP, NDPS_QUEUES NQ\n" +
				"            where\n" +
				"            (MP.BASKET_NR is not null\n" +
				"            and MP.SRV_ACTIVITY in ('1','2','3','5','6','8','9','10', '11')\n" +
				"            and not exists(select * from M_SRV_PRN mmsp where\n" +
				"            mmsp.SRV_ACTIVITY in ('12') and (mmsp.PRINT_STS<>'13' and mmsp.PRINT_STS<>'18')\n" +
				"            and mmsp.BASKET_NR = MP.BASKET_NR)\n" +
				"            and MP.PRINT_STS = '0'\n" +
				"            and MP.INSERT_USER not in (7777,1679,1680)\n" +
				"            and not exists (select MS.PRINT_NR from M_SRV_PRN_STS MS where MS.PRINT_NR =MP.PRINT_NR)\n" +
				"            and NQ.NOVELL_QUEUE=*MP.DEST_QUEUE)\n" +
				"            order by MP.PRIORITY desc\n" +
				"             ";
		final String s1 = s.replaceAll("(\r\n|\r|\n|\n\r)", "<br>");
		System.out.println("s1 = " + s1);
	}
}