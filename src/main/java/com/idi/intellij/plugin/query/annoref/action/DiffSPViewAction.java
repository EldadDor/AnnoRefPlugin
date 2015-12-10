/*
 * User: eldad.Dor
 * Date: 22/02/2015 11:57
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.action;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.idi.intellij.plugin.query.annoref.common.SQLRefConstants;
import com.idi.intellij.plugin.query.annoref.component.AnnoRefDataKey;
import com.idi.intellij.plugin.query.annoref.component.SPViewContentStateManager;
import com.idi.intellij.plugin.query.annoref.connection.DataSourceAccessorComponent;
import com.idi.intellij.plugin.query.annoref.util.AnnRefApplication;
import com.idi.intellij.plugin.query.annoref.util.PsiDiffContentManager;
import com.idi.intellij.plugin.query.annoref.util.StringUtils;
import com.idi.intellij.plugin.query.annoref.util.SybaseLanguageManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.diff.DiffContent;
import com.intellij.openapi.diff.DiffManager;
import com.intellij.openapi.diff.DiffTool;
import com.intellij.openapi.diff.SimpleDiffRequest;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.actions.IconWithTextAction;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.ui.popup.ListSeparator;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.ui.CollectionListModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author eldad
 * @date 22/02/2015
 */
@SuppressWarnings("ComponentNotRegistered")
public class DiffSPViewAction extends IconWithTextAction {
	private static Logger log = Logger.getInstance(DiffSPViewAction.class);

/*
	@Override
	public JComponent createCustomComponent(Presentation presentation) {
		JComponent component = super.createCustomComponent(presentation);
		Insets i = new JCheckBox().getInsets();
		component.setBorder(new EmptyBorder(i));
		component.setMaximumSize(component.getPreferredSize());
		return component;
	}
*/


	@Override
	public void actionPerformed(@NotNull AnActionEvent event) {
		log.info("actionPerformed():");
		final Project project = (Project) event.getDataContext().getData(PlatformDataKeys.PROJECT.getName());
		final Editor editor = (Editor) event.getDataContext().getData(PlatformDataKeys.EDITOR.getName());
		final PsiFile psiFile1 = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
//		((UserDataHolder) event.getDataContext()).getUserData(AnnoRefDataKey.DATA_SOURCE_NAME_DATA_KEY);
		final SPViewingInformation SPViewingInformation = editor.getUserData(AnnoRefDataKey.DATA_SOURCE_NAME_DATA_KEY);

//		final JBList jbList = new JBList();
		final CollectionListModel<DataSourcePojo> listModel = new CollectionListModel<DataSourcePojo>();
//		jbList.setModel(listModel);
		final Collection<String> availableConnections = AnnRefApplication.getInstance(project, DataSourceAccessorComponent.class).getAvailableConnections(project);
		for (final String availableConnection : availableConnections) {
			String substring;
			if (availableConnection.indexOf("[") > 0) {
				substring = availableConnection.substring(0, availableConnection.indexOf("[") - 1);
			} else {
				substring = availableConnection;
			}
			final ResourceBundle resourceBundle = ResourceBundle.getBundle("dataSourcesInfo", Locale.ENGLISH);
			if (resourceBundle.containsKey(substring.toUpperCase())) {
				final String dataSourcesInfo = resourceBundle.getString(substring.toUpperCase());
				listModel.add(new DataSourcePojo(availableConnection, dataSourcesInfo));
			} else {
				listModel.add(new DataSourcePojo(availableConnection, null));
			}
		}
		final Collection<DataSourcePojo> filteredConnections = Collections2.filter(listModel.toList(), new Predicate<DataSourcePojo>() {
			@Override
			public boolean apply(@Nullable DataSourcePojo predicate) {
				return !predicate.getDbName().equalsIgnoreCase(SPViewingInformation.getDbName());
			}
		});
		final BaseListPopupStep<DataSourcePojo> popupStep = createBaseListPopupStep(project, psiFile1, SPViewingInformation, filteredConnections);

		ListPopup popup = JBPopupFactory.getInstance().createListPopup(popupStep);
		/*ComponentPopupBuilder popupBuilder = JBPopupFactory.getInstance().createComponentPopupBuilder(editor.getComponent(),
				WindowManager.getInstance().getIdeFrame(project).getComponent());*/

//		final JBPopup[] finalPopup = new JBPopup[0];

		/*popupBuilder.setShowShadow(true).
				setResizable(true).
				setMovable(true).
				setTitle("Compare SP with\nSelect DataSource:").
						setSettingButtons(popOutSettingsPanel).
//						setTitleIcon(new ActiveIcon(IconLoader.findIcon("icons/syBaseLogo_3_sm.png"))).
//		setMayBeParent(true).
				setCancelOnClickOutside(false).
				setRequestFocus(true);*/
		 /*JBPopup popup = popupBuilder.createPopup();
		//		finalPopup[0] = popup;
				popup.setSize(new Dimension(850, 900));
				((AbstractPopup) popup).setAdText("Test ADText");
				popup.showCenteredInCurrentWindow(project);
				popup.moveToFitScreen();
				((AbstractPopup) popup).focusPreferredComponent();*/


//		final JBPopup popup = JBPopupFactory.getInstance().createListPopupBuilder(jbList).createPopup();
		popup.showInBestPositionFor(editor);
//		popup.getContent();
//		popup.showUnderneathOf(editor.getComponent());
//		popup.showInBestPositionFor(event.getDataContext());
//		popup.show(editor.getContentComponent());
//		final QuickActionManager quickActionManager = QuickActionManager.getInstance(project);
//		quickActionManager.showQuickActions();
	}

	private BaseListPopupStep<DataSourcePojo> createBaseListPopupStep(final Project project, final PsiFile psiFile, final SPViewingInformation SPViewingInformation, final Collection<DataSourcePojo> filteredConnections) {
		return new BaseListPopupStep<DataSourcePojo>("<html><body>Select a DataSource For Compare</body></html>",
				new ArrayList<DataSourcePojo>(filteredConnections)) {
			@Override
			public PopupStep onChosen(DataSourcePojo selectedValue, boolean finalChoice) {
				log.info("onChosen():");
				try {
					final String spTextForDiff = ServiceManager.getService(project, SPViewContentStateManager.class).
							fetchSpTextForDBEnvironment(project, SPViewingInformation.getSpName(), selectedValue.getDbName());
					final PsiFile diffPsiFile = AnnRefApplication.getInstance(project, SybaseLanguageManager.class).initializeSqlSyntaxForPsiFile(spTextForDiff);
					final SimpleDiffRequest diffRequest = getSimpleDiffRequest(selectedValue, diffPsiFile, psiFile, SPViewingInformation);
					final DiffTool diffTool = DiffManager.getInstance().getDiffTool();
//					Window active = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
//					final DiffPanel diffPanel = DiffManager.getInstance().createDiffPanel(active, project, diffTool);
//					diffPanel.setTitle1(selectedValue.getDbName());
//					diffPanel.setTitle2(SPViewingInformation.getDbName());
//					final SPDiffDialogWrapper diffDialogWrapper = new SPDiffDialogWrapper(diffPanel.getComponent(), "SP Comparison");
//					diffDialogWrapper.show();
					diffTool.show(diffRequest);
				} catch (Exception e1) {
					log.error("OnChosen error=" + e1.getMessage(), e1);
				}
				return super.onChosen(selectedValue, finalChoice);
			}

			@Nullable
			@Override
			public ListSeparator getSeparatorAbove(DataSourcePojo value) {
				return value == null ? new ListSeparator() : null;
			}

			@Override
			public Icon getIconFor(DataSourcePojo value) {
				if (StringUtils.startsWithIgnoreCase(value.getDbName(), "TEST")) {
					return SQLRefConstants.DB_TEST_ICON;
				}
				if (StringUtils.startsWithIgnoreCase(value.getDbName(), "USERTEST")) {
					return SQLRefConstants.DB_UT_ICON;
				}
				if (StringUtils.startsWithIgnoreCase(value.getDbName(), "YEST")) {
					return SQLRefConstants.DB_YEST_ICON;
				}
				if (StringUtils.startsWithIgnoreCase(value.getDbName(), "TRAIN")) {
					return SQLRefConstants.DB_TRAIN_ICON;
				}
				if (StringUtils.startsWithIgnoreCase(value.getDbName(), "REP") || StringUtils.startsWithIgnoreCase(value.getDbName(), "REM")) {
					return SQLRefConstants.DB_REP_ICON;
				} else {
					return super.getIconFor(value);
				}
			}

			@NotNull
			@Override
			public String getTextFor(DataSourcePojo value) {
				if (value != null && !Strings.isNullOrEmpty(value.getDbName())) {
					if (value.getDescription() == null) {
						return value.getDbName();
					}
					return value.getDbName() + " (" + value.getDescription() + ")";
				} else {
					return "";
				}
			}
		};
	}

	private SimpleDiffRequest getSimpleDiffRequest(@NotNull final DataSourcePojo selectedValue,
			@NotNull final PsiFile diffPsiFile, @NotNull final PsiFile psiFile, @NotNull final SPViewingInformation SPViewingInformation) {
		final SimpleDiffRequest diffRequest = PsiDiffContentManager.comparePsiElements(psiFile, diffPsiFile);
		diffRequest.setWindowTitle("SP Comparison for " + SPViewingInformation.getSpName());
		diffRequest.setContentTitles(SPViewingInformation.getDbName(), selectedValue.getDbName());
		for (final DiffContent diffContent : diffRequest.getContents()) {
			diffContent.getDocument().setReadOnly(true);
		}
		return diffRequest;
	}


}