package com.idi.intellij.plugin.query.annoref.config;

import com.google.common.collect.Lists;
import com.idi.intellij.plugin.query.annoref.action.DiffSPViewAction;
import com.idi.intellij.plugin.query.annoref.action.SPViewingInformation;
import com.idi.intellij.plugin.query.annoref.common.SPViewIndexHelper;
import com.idi.intellij.plugin.query.annoref.common.SQLRefConstants;
import com.idi.intellij.plugin.query.annoref.component.AnnoRefDataKey;
import com.idi.intellij.plugin.query.annoref.component.SPViewContentStateManager;
import com.idi.intellij.plugin.query.annoref.connection.ConnectionUtil;
import com.idi.intellij.plugin.query.annoref.connection.DataSourceAccessorComponent;
import com.idi.intellij.plugin.query.annoref.index.SQLRefRepository;
import com.idi.intellij.plugin.query.annoref.persist.AnnoRefConfigSettings;
import com.idi.intellij.plugin.query.annoref.util.SQLRefApplication;
import com.idi.intellij.plugin.query.annoref.util.SybaseLanguageManager;
import com.intellij.ide.DataManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.actionSystem.ShortcutSet;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.ui.JBColor;
import com.intellij.ui.TextFieldWithAutoCompletion;
import com.intellij.ui.content.Content;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.List;

/**
 * Created by EAD-MASTER on 2/1/14.
 */
public class SPViewPanelForm implements Disposable, SPViewSubmitListener {
	private static Logger logger = Logger.getInstance(SPViewPanelForm.class);
	private Project project;
	public static Project preInitializedProject;
	private final String spName;
	private final SPViewIndexHelper indexHelper = SPViewIndexHelper.build();
	private JTextField spNameTextField;
	private JButton closeButton;
	private JButton goDownButton;
	private JButton goUpButton;
	private JButton spSearchSubmitBtn;
	private JPanel mainPanel;
	private JPanel topPanel;
	private JLabel spNameLabel;
	private JPanel mainTextPanel;
	private JScrollPane textAreaScrollPane;
	private JTextPane textPaneBody;
	private JButton popOutSpButton;
	private JPanel sideMenuPanel;
	private JButton showSettingsButton;
	private JLabel dbNameLabel;
	private JLabel dbSelectedLabel;
	private TextFieldWithAutoCompletion textFieldWithAutoCompletion;
	private Content myContent;
	private Editor editor;
	private List<Integer> startIndices = Lists.newLinkedList();
	private List<Integer> endIndices = Lists.newLinkedList();
	private VisualPosition currentPosition;


	public SPViewPanelForm(String spName, Project project) {
		this.project = project;
		this.spName = spName;
		ConnectionUtil.initializeDefaultDataSource(project);
		$$$setupUI$$$();
		SQLRefApplication.getInstance(project, SQLRefRepository.class).addSPViewIndexToRepo(spName, indexHelper);
		addGoUpListener();
		addGoDownListener();
		addPopOutWindowListener();
		addSearchSpSubmitListener();
		addCloseListener();
		addShowSettingsListener();
	}

	private void addShowSettingsListener() {
		showSettingsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ShowSettingsUtil.getInstance().showSettingsDialog(project, SQLRefConstants.IDI_PLUGIN_SETTINGS);
			}
		});
	}

	private void addCloseListener() {
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ServiceManager.getService(project, SPViewContentStateManager.class).closeContent(myContent);
			}
		});
	}

	private void addSearchSpSubmitListener() {
		spSearchSubmitBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				spSubmitAction();
			}
		});
	}

	private void addTextFieldAutoCompletion() {
		if (project == null) {
			project = preInitializedProject;
		}
		final DataSourceAccessorComponent dbAccessor = SQLRefApplication.getInstance(project, DataSourceAccessorComponent.class);
		//		final AutoCompleteAdapter completeAdapter = new AutoCompleteAdapter(spNameTextField, dbAccessor.getStoreProceduresNames());
//		final TextComponentAdaptor completeAdapter = new TextComponentAdaptor(spNameTextField, dbAccessor.getStoreProceduresNames());
		final Collection<String> names = dbAccessor.getStoreProceduresNames();
		textFieldWithAutoCompletion = SPTTextFieldWithAutoCompletion.createWithAutoCompletion(project, names, this);
//		textFieldWithAutoCompletion = TextFieldWithAutoCompletion.create(project, names, true, "");
//		spNameTextField.getInputMap().put(KeyStroke.getKeyStroke("TAB"), COMMIT_ACTION);
		//		spNameTextField.getActionMap().put(COMMIT_ACTION, new AutoCompleteAdapter.CommitAction());
//		final AutoCompleteDocument autoCompleteDocument = new AutoCompleteDocument(completeAdapter, false);
//		topPanel.remove(spNameTextField);
//		textFieldWithAutoCompletion.setMinimumSize(new Dimension(150, 20));
//		textFieldWithAutoCompletion.setPreferredSize(new Dimension(150, 20));
		//		topPanel.add(spNameTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
	/*	topPanel.add(textFieldWithAutoCompletion, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
				GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));*/
	}

	private void addPopOutWindowListener() {
		popOutSpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new SPViewPanelPopOut(project).initializeAndPopWindow(spName, editor.getDocument().getText());
			}
		});
	}

	public JScrollPane getTextAreaPanel() {
		return textAreaScrollPane;
	}

	private void addGoDownListener() {
		goDownButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (currentPosition == null) {
						final Object first = indexHelper.getIterator().getFirst();
						if (first == null) {
							return;
						}
						currentPosition = (VisualPosition) first;
						editor.getScrollingModel().scrollTo(editor.visualToLogicalPosition(currentPosition), ScrollType.MAKE_VISIBLE);
					} else {
						if (indexHelper.getIterator().hasNext()) {
							currentPosition = (VisualPosition) indexHelper.getIterator().next();
							editor.getScrollingModel().scrollTo(editor.visualToLogicalPosition(currentPosition), ScrollType.MAKE_VISIBLE);
						}
					}
				} catch (Exception e1) {
					logger.error("BadLocationException=" + e1.getMessage(), e1);
				}
			}
		});
	}

	private void addGoUpListener() {
		goUpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (currentPosition == null) {
						final Object last = indexHelper.getIterator().getLast();
						if (last == null) {
							return;
						}
						currentPosition = (VisualPosition) last;
						editor.getScrollingModel().scrollTo(editor.visualToLogicalPosition(currentPosition), ScrollType.MAKE_VISIBLE);
					} else {
						if (indexHelper.getIterator().hasPrevious()) {
							currentPosition = (VisualPosition) indexHelper.getIterator().previous();
							editor.getScrollingModel().scrollTo(editor.visualToLogicalPosition(currentPosition), ScrollType.MAKE_VISIBLE);
						}
					}
				} catch (Exception e1) {
					logger.error("BadLocationException=" + e1.getMessage(), e1);
				}
			}
		});
	}

	private void locateSpNameOnCaret() {
		try {
			final String estimatedSpName = textPaneBody.getText(textPaneBody.getCaret().getDot(), 50);
			final String firstCleanUp = estimatedSpName.substring(6, estimatedSpName.length());
			final String secondCleanUp = firstCleanUp.substring(0, firstCleanUp.indexOf("\n", 0));
		} catch (BadLocationException e) {
			logger.error("locateSpNameOnCaret():", e);
		}
	}

	public void setContent(final Content content) {
		myContent = content;
	}

	public JPanel getMainPanel() {
		return mainPanel;
	}

	public void setTextForViewing(String text) {
		initializeSqlSyntax(text);
		highLightSPExecText();
		textPaneBody.transferFocusUpCycle();
	}

	private void initializeSqlSyntax(String text) {
		editor = SQLRefApplication.getInstance(project, SybaseLanguageManager.class).initializeSqlSyntaxForEditor(project, text);
		mainTextPanel.remove(textPaneBody);
		initializeSPDiffPanel();
		mainTextPanel.add(editor.getComponent(), new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
				GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
	}

	private void initializeSPDiffPanel() {
		final DataContext dataContext = DataManager.getInstance().getDataContext(editor.getComponent());
		final String dataSourceName = AnnoRefConfigSettings.getInstance(project).getAnnoRefState().SP_DATA_SOURCE_NAME;
		editor.putUserData(AnnoRefDataKey.DATA_SOURCE_NAME_DATA_KEY, new SPViewingInformation(dataSourceName, spName));
//		((UserDataHolder) dataContext).putUserData(AnnoRefDataKey.DATA_SOURCE_NAME_DATA_KEY, new SPViewingInformation(dataSourceName, spName));
		final DiffSPViewAction diffSPViewAction = new DiffSPViewAction();
		ShortcutSet ENTER_AND_CTRL_ENTER_SHORTCUT_SET = new CustomShortcutSet(KeyboardShortcut.fromString("control ENTER"));
		diffSPViewAction.registerCustomShortcutSet(ENTER_AND_CTRL_ENTER_SHORTCUT_SET, editor.getComponent());
	}

	private void highLightSPExecText() {
		searchForStartAndEndIndices(0);
		for (int i = 0; i < startIndices.size(); i++) {
			Integer startExec = startIndices.get(i);
			Integer endExec = endIndices.get(i);
			final JBColor foregroundColor = new JBColor(new Color(230, 44, 14), new Color(230, 44, 14));
			final JBColor backgroundColor = new JBColor(new Color(99, 185, 230), new Color(99, 185, 230));
			TextAttributes attributes = new TextAttributes(foregroundColor, backgroundColor, null, EffectType.ROUNDED_BOX, Font.PLAIN);
			RangeHighlighter rangeHighlighter = editor.getMarkupModel().addRangeHighlighter(startExec,
					endExec, HighlighterLayer.FIRST, attributes, HighlighterTargetArea.EXACT_RANGE);
			rangeHighlighter.getTargetArea();
		}

		indexHelper.initializeIterator(indexHelper);
	}

	private boolean searchForStartAndEndIndices(int start) {
		final String text = editor.getDocument().getText();
		final int startExec = text.indexOf("exec", start);
		if (startExec == -1) {
			return false;
		}
		startIndices.add(startExec);
		final VisualPosition visualPosition = editor.offsetToVisualPosition(startExec);
		indexHelper.add(visualPosition);
		final int endExec = text.indexOf("\n", startExec);
		final String spLinkText = text.substring(startExec + 6, endExec);
		indexHelper.getSpLinkable().put(spLinkText, spLinkText);
		endIndices.add(endExec);
		return searchForStartAndEndIndices(endExec);
	}

	@Override
	public void dispose() {
		if (editor != null && !editor.isDisposed()) {
			logger.info("dispose(): releasing SPViewPanelForm Editor");
			EditorFactory.getInstance().releaseEditor(editor);
		}
	}

	private void createUIComponents() {
		addTextFieldAutoCompletion();
	}

	@Override
	public void spSubmitAction() {
		if (textFieldWithAutoCompletion.getText() != null && !textFieldWithAutoCompletion.getText().isEmpty()) {
			final String spSubmittedName = textFieldWithAutoCompletion.getText().trim();
			final String contentName = spSubmittedName + "_" + AnnoRefConfigSettings.getInstance(project).getAnnoRefState().SP_DATA_SOURCE_NAME;
			final SPViewContentStateManager contentStateManager = ServiceManager.getService(project, SPViewContentStateManager.class);
			final Pair<Boolean, Content> contentPair = contentStateManager.fetchSpForContentDisplay(project, spSubmittedName, contentName, ApplicationManager.getApplication().isDispatchThread());
			if (contentPair.getSecond() == null) {
				return;
			}
			UIUtil.invokeAndWaitIfNeeded(new Runnable() {
				@Override
				public void run() {
					if (contentPair.getFirst()) {
						SPViewPanelForm.logger.info("run(): reactivating content, spName=" + spSubmittedName);
						contentStateManager.reactivateContent(contentPair.getSecond());
					} else {
						SPViewPanelForm.logger.info("run(): adding content, spName=" + spSubmittedName);
						contentStateManager.addContent(contentPair.second, spSubmittedName, contentName);
						topPanel.updateUI();
					}
				}
			});
		}
	}

	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$() {
		createUIComponents();
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayoutManager(2, 6, new Insets(0, 0, 0, 0), -1, -1));
		topPanel = new JPanel();
		topPanel.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
		mainPanel.add(topPanel, new GridConstraints(0, 3, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		spNameLabel = new JLabel();
		spNameLabel.setHorizontalAlignment(2);
		spNameLabel.setHorizontalTextPosition(2);
		spNameLabel.setText("SP Name:");
		topPanel.add(spNameLabel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		spSearchSubmitBtn = new JButton();
		spSearchSubmitBtn.setText("submit");
		topPanel.add(spSearchSubmitBtn, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		dbNameLabel = new JLabel();
		dbNameLabel.setText("DB:");
		topPanel.add(dbNameLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		dbSelectedLabel = new JLabel();
		dbSelectedLabel.setText("");
		topPanel.add(dbSelectedLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		topPanel.add(textFieldWithAutoCompletion, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(150, -1), null, 0, false));
		textAreaScrollPane = new JScrollPane();
		mainPanel.add(textAreaScrollPane, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		mainTextPanel = new JPanel();
		mainTextPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		textAreaScrollPane.setViewportView(mainTextPanel);
		textPaneBody = new JTextPane();
		mainTextPanel.add(textPaneBody, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
		sideMenuPanel = new JPanel();
		sideMenuPanel.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
		mainPanel.add(sideMenuPanel, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		sideMenuPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-3355444)), null));
		goUpButton = new JButton();
		goUpButton.setHorizontalTextPosition(0);
		goUpButton.setIcon(new ImageIcon(getClass().getResource("/icons/ScrollUpArrowActive.gif")));
		goUpButton.setText("");
		goUpButton.setToolTipText("Find previous SP exec in current SP");
		sideMenuPanel.add(goUpButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(24, 24), new Dimension(24, 24), 0, false));
		closeButton = new JButton();
		closeButton.setIcon(new ImageIcon(getClass().getResource("/actions/delete.png")));
		closeButton.setText("");
		sideMenuPanel.add(closeButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(24, 24), new Dimension(24, 24), 0, false));
		goDownButton = new JButton();
		goDownButton.setBorderPainted(true);
		goDownButton.setHorizontalTextPosition(0);
		goDownButton.setIcon(new ImageIcon(getClass().getResource("/icons/ScrollDownArrowActive.gif")));
		goDownButton.setOpaque(true);
		goDownButton.setText("");
		goDownButton.setToolTipText("Find next SP exec in current SP");
		sideMenuPanel.add(goDownButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(24, 24), new Dimension(24, 24), 0, false));
		popOutSpButton = new JButton();
		popOutSpButton.setHorizontalTextPosition(0);
		popOutSpButton.setIcon(new ImageIcon(getClass().getResource("/general/information.png")));
		popOutSpButton.setText("");
		popOutSpButton.setToolTipText("Open current displayed SP in a PopUp Window");
		sideMenuPanel.add(popOutSpButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(24, 24), new Dimension(24, 24), 0, false));
		showSettingsButton = new JButton();
		showSettingsButton.setIcon(new ImageIcon(getClass().getResource("/general/settings.png")));
		showSettingsButton.setText("");
		showSettingsButton.setToolTipText("Open AnnoRef settings editor");
		sideMenuPanel.add(showSettingsButton, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(24, 24), new Dimension(24, 24), 0, false));
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return mainPanel;
	}

	/*//	class SpTextLinkListener extends AbstractAction implements MouseListener {
	class SpTextLinkListener implements EditorMouseListener {
		private String textLink;

		SpTextLinkListener(String textLink) {
			this.textLink = textLink;
		}

		SpTextLinkListener() {
		}

		protected void execute() {
			logger.info("execute");
			*//*if ("accept".equals(url)) {
				//execute code
			} else if ("decline".equals(url)) {
				//execute code
			}*//*
		}


		@Override
		public void mousePressed(EditorMouseEvent editorMouseEvent) {
		}

		@Override
		public void mouseClicked(EditorMouseEvent editorMouseEvent) {
			if (editorMouseEvent.getMouseEvent().isControlDown()) {
				try {
					editor.xyToLogicalPosition(editorMouseEvent.getMouseEvent().getPoint());
//					if (editor.getDocument().getText(editor.xyToLogicalPosition(editorMouseEvent.getMouseEvent().getPoint()) - 40, 60).contains("exec")) {
					if (false) {
						final String text = textPaneBody.getText(textPaneBody.viewToModel(editorMouseEvent.getMouseEvent().getPoint()) - 40, 100);
						final String[] spNameExtract = text.split("exec");
						if (spNameExtract.length > 0) {
							final String[] spNameSndExtract = spNameExtract[1].split("\n");
							final String spClickedName = spNameSndExtract[0].trim();
							if (indexHelper.getSpLinkable().containsKey(spClickedName)) {
								fetchSpAndOpenNewTab(spClickedName);
							}
						}
					}
				} catch (Exception e1) {
					logger.error(e1);
				}
			}
		}

		@Override
		public void mouseReleased(EditorMouseEvent editorMouseEvent) {

		}

		@Override
		public void mouseEntered(EditorMouseEvent editorMouseEvent) {
		}

		@Override
		public void mouseExited(EditorMouseEvent editorMouseEvent) {

		}
	}

	private void fetchSpAndOpenNewTab(final String spName) {
		logger.info("spClickName=" + spName);
		UIUtil.invokeAndWaitIfNeeded(new Runnable() {
			@Override
			public void run() {
				final DataSourceAccessorComponent dbAccessor = SQLRefApplication.getInstance(project, DataSourceAccessorComponent.class);
				final AnnoRefSettings sqlRefState = AnnoRefConfigSettings.getInstance(project).getAnnoRefState();
				dbAccessor.initDataSource(project, sqlRefState.SP_DATA_SOURCE_NAME);
				String spText = null;
				try {
					spText = dbAccessor.fetchSpForViewing(spName, project);
				} catch (SQLException e1) {
					logger.error(e1);
				}
				ServiceManager.getService(project, SPViewContentStateManager.class).addContent(getSPViewContent(spName, spText), spName);
			}
		});
	}


	private Content getSPViewContent(String spName, String spText) {

		final SPViewPanelForm spPanel = new SPViewPanelForm(spName, project);
		final Content newContent = ContentFactory.SERVICE.getInstance().createContent(spPanel.getMainPanel(), "", false);
		newContent.setIcon(IconLoader.findIcon("icons/syBaseLogo_36.png"));
		spPanel.setContent(newContent);
		spPanel.setTextForViewing(spText);
		newContent.setDisposer(spPanel);
		return newContent;
	}
*/
}

