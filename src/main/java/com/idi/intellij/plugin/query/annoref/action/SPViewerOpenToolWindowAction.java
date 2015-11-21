package com.idi.intellij.plugin.query.annoref.action;

import com.idi.intellij.plugin.query.annoref.component.SPViewContentStateManager;
import com.idi.intellij.plugin.query.annoref.component.SQLRefSPView;
import com.idi.intellij.plugin.query.annoref.config.SPViewPanelForm;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.tabs.impl.JBTabsImpl;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 29/10/2010
 * Time: 11:31:52
 * To change this template use File | Settings | File Templates.
 */
public class SPViewerOpenToolWindowAction extends AnAction {

	private static final String SP_VIEW_TOOL_WINDOW_ID = "SPViewer";
	private static Logger logger = Logger.getInstance(SPViewerOpenToolWindowAction.class.getName());
	private ContentManager contentManager;
	private Project project;

	@Override
	public void update(@NotNull AnActionEvent e) {
//		final Color color = UIManager.("Tree.selectionForeground");
	/*	UIDefaults uiDefaults = UIManager.getDefaults();
		uiDefaults.put("Tree.selectionForeground", Color.magenta);
		uiDefaults.put("Tree.textForeground", Color.blue);
		uiDefaults.put("Tree.textBackground", Color.YELLOW);
		uiDefaults.put("Tree.background", Color.RED);
		uiDefaults.put("Tree.selectionBorderColor", Color.RED);
		uiDefaults.put("Tree.foreground", Color.CYAN);
		uiDefaults.put("Table.focusCellHighlightBorder", Color.PINK);
		uiDefaults.put("List.focusCellHighlightBorder", Color.orange);
*/
//		final Color color = UIManager.getColor("Tree.selectionForeground");
		e.getPresentation().setEnabled(e.getDataContext().getData(PlatformDataKeys.EDITOR.getName()) != null);
	}

	@Override
	public void actionPerformed(@NotNull AnActionEvent event) {
		project = (Project) event.getDataContext().getData(PlatformDataKeys.PROJECT.getName());
//		Editor editor = (Editor) event.getDataContext().getData(PlatformDataKeys.EDITOR.getName());
//		PsiFile psiFile = (PsiFile) event.getDataContext().getData(LangDataKeys.PSI_FILE.getName());
//		PsiElement psiElement = (PsiElement) event.getDataContext().getData(LangDataKeys.PSI_ELEMENT.getName());
		displayStorageProcedureText(project);
//		displayStorageProcedureText(psiFile, project, psiElement);
//		SQLRefApplication.getInstance(project, SQLRefDataAccessor.class).initializeSQLRefData(event.getDataContext());
/*		ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
//		ContentManager
		final Container layoutTarget = LayoutFactory.getLayoutTarget(editor.getContentComponent());

		Content content = contentFactory.createContent(editor.getComponent(), "", false);
		final ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
		toolWindowManager.getActiveToolWindowId();
		ToolWindow spViewer = toolWindowManager.getToolWindow("SPViewer");
		final SQLRefSPView spViewPanel = (SQLRefSPView) spViewer.getContentManager().getContents()[0].getComponent();
//		spViewer = toolWindowManager.registerToolWindow("SPViewer", true, ToolWindowAnchor.BOTTOM);
		spViewPanel.initializeSPView();
		spViewPanel.setTextForViewing("Just Passing by...");*/

		/*ConsoleView view = null;
		if (view == null) {
			final TextConsoleBuilderFactory builderFactory = TextConsoleBuilderFactory.getInstance();
			final TextConsoleBuilder builder = builderFactory.createBuilder(project);
			view = builder.getConsole();
		}*/
	/*	OSProcessHandler handler = new OSProcessHandler(proc, command);
	                view.attachToProcess(handler);
	                handler.startNotify();
		view.print("Hello Mr Mr", ConsoleViewContentType.NORMAL_OUTPUT);
		view.getComponent().setVisible(true);*/
	}

	//	private void displayStorageProcedureText(PsiFile psiFile, final Project project, PsiElement psiElement) {
	private void displayStorageProcedureText(final Project project) {
//		final AnnoRefSettings sqlRefState = AnnoRefConfigSettings.getInstance(project).getAnnoRefState();
//		logger.info("displayStorageProcedureText(): PsiFile=" + psiFile.getName());
//		PsiAnnotation psiAnnotation = SQLRefNamingUtil.getAnnotationForConfiguredClassFile(psiFile, sqlRefState.
//				SP_VIEW_ANNOTATION_FQN);
//		final JPanel spViewFormPanel = new SPTextViewPanel().getSpViewFormPanel();
//		if (psiAnnotation != null) {
//			logger.info("displayStorageProcedureText(): psiAnnotation=" + psiAnnotation.getQualifiedName());
//			final PsiNameValuePair psiNameValuePair = psiAnnotation.getParameterList().getAttributes()[0];
//			final String spName = psiNameValuePair.getValue().getText();
////			final ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
////			ToolWindow toolWindow = getRegisteredToolWindow(toolWindowManager);
//			final String cleanSpName = StringUtils.cleanQuote(spName);
//			final Pair<Boolean, Disposable> spViewPair = initializeTabContentWindow(project, toolWindow, cleanSpName);
//			final Pair<Boolean, Disposable> spViewPair = initializeTabContentWindow(project, cleanSpName);
//			final Disposable spViewPanel = spViewPair.getSecond();
//			JBTabsImpl jbTabs = null;
//			if (spViewPair.getFirst()) {
//				spViewPanel.initializeSPView();
		final SPViewContentStateManager contentStateManager = ServiceManager.getService(project, SPViewContentStateManager.class);
		UIUtil.invokeAndWaitIfNeeded(new Runnable() {
			@Override
			public void run() {
				/*	final DataSourceAccessorComponent dbAccessor = SQLRefApplication.getInstance(project, DataSourceAccessorComponent.class);
					dbAccessor.initDataSource(project, sqlRefState.SP_DATA_SOURCE_NAME);
					try {
						String spText = dbAccessor.fetchSpForViewing(cleanSpName, project);
						final SPViewContentStateManager contentStateManager = ServiceManager.getService(project, SPViewContentStateManager.class);

						}*/
				final Content alreadyOpenContent = contentStateManager.getAlreadyOpenContent("");
				if (alreadyOpenContent != null) {
					contentStateManager.reactivateContent(alreadyOpenContent);
				} else {
					contentStateManager.addContent(getSPViewContent(""), "", "");
				}
			}
		});
		//					jbTabs = new JBTabsImpl(project, IdeFocusManager.findInstance(), spViewPanel);
		//					final TabbedPaneWrapper.TabWrapper tabWrapper = new TabbedPaneWrapper.TabWrapper(jbTabs.getComponent());
		//					IdeFocusManager.findInstance().getFocusOwner();
		//					((SPViewPanelForm) spViewPanel).setTextForViewing(spText);

	}

//			}
//			final JBTabsImpl finalJbTabs = jbTabs;
		/*	toolWindow.activate(new Runnable() {
				@Override
				public void run() {
					displayInTab(finalJbTabs);
				}
			});*/


	private ToolWindow getRegisteredToolWindow(ToolWindowManager toolWindowManager) {
		ToolWindow toolWindow = toolWindowManager.getToolWindow("SPViewer");
		if (toolWindow == null) {
			toolWindow = toolWindowManager.registerToolWindow("SPViewer", true, ToolWindowAnchor.BOTTOM);
			toolWindow.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/syBaseLogo_3_sm.png")));
		}
		return toolWindow;
	}

	private Content getSPViewContent(String spText) {
		SPViewPanelForm.preInitializedProject = project;
		final SPViewPanelForm spPanel = new SPViewPanelForm(null, project);
		final Content newContent = ContentFactory.SERVICE.getInstance().createContent(spPanel.getMainPanel(), "", false);
		newContent.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/syBaseLogo_3_sm.png")));
		newContent.setDisplayName("SPViewer");
		newContent.setTabName("SPViewer");
//		newContent.setIcon(IconLoader.findIcon("icons/syBaseLogo_36.png"));
		spPanel.setContent(newContent);
//		spPanel.setTextForViewing(spText);
		newContent.setDisposer(spPanel);
		return newContent;
	}

	@Deprecated
	private Pair<Boolean, Disposable> initializeTabContentWindow(Project project, String tabName) {
		final Content newContent = ContentFactory.SERVICE.getInstance().createContent(SQLRefSPView.getInstance(project), "", false);
		final SPViewPanelForm spPanel = new SPViewPanelForm(null, null);
		newContent.setDisposer(spPanel);
		return new Pair<Boolean, Disposable>(true, spPanel);
	}


/*	private Pair<Boolean, SQLRefSPView> initializeTabContentWindow(Project project, ToolWindow toolWindow, String tabName) {
		contentManager = toolWindow.getContentManager();
		final SQLRefSPView displayed = isSPAlreadyDisplayed(contentManager.getContents(), tabName);
		if (displayed != null) {
			return new Pair<Boolean, SQLRefSPView>(false, displayed);
		}
		final Content newContent = ContentFactory.SERVICE.getInstance().createContent(SQLRefSPView.getInstance(project), "", false);
		contentManager.addContent(newContent);
		newContent.setTabName(tabName);
		newContent.setDisplayName(tabName);

		return new Pair<Boolean, SQLRefSPView>(true, (SQLRefSPView) newContent.getComponent());
	}*/

	@Deprecated
	private SQLRefSPView isSPAlreadyDisplayed(final Content[] contents, final String tabName) {
		for (final Content content : contents) {
			if (content.getDisplayName().equals(tabName)) {
				return (SQLRefSPView) content.getComponent();
			}
		}
		return null;
	}

	@Deprecated
	private void displayInTab(JBTabsImpl jbTabs) {
		jbTabs.grabFocus();
		jbTabs.setFocused(true);
		jbTabs.setVisible(true);
		jbTabs.updateUI();
	}
}
