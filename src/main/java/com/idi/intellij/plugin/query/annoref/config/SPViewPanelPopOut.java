package com.idi.intellij.plugin.query.annoref.config;

import com.google.common.collect.Lists;
import com.idi.intellij.plugin.query.annoref.util.SQLRefApplication;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.fileTypes.SyntaxHighlighterLanguageFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.*;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.sql.dialects.sybase.SybaseDialect;
import com.intellij.sql.psi.SqlFileType;
import com.intellij.ui.content.Content;
import com.intellij.ui.popup.AbstractPopup;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Deque;
import java.util.List;

/**
 * Created by EAD-MASTER on 2/1/14.
 */
public class SPViewPanelPopOut implements Disposable {
	private static Logger logger = Logger.getInstance(SPViewPanelPopOut.class);
	private final Project project;
	private JTextField spNameTextField;
	private JButton closeButton;
	private JButton goDownButton;
	private JButton goUpButton;
	private JTextArea textAreaBody;
	private JButton spSearchSubmitBtn;
	private JPanel mainPanel;
	private JPanel topPanel;
	private JLabel spNameLabel;
	private JPanel textEnclosedPanel;
	private JScrollPane textAreaScrollPane;
	private JEditorPane editorTextPane;
	private JPanel popOutSettingsPanel;
	private Content myContent;
	private List<Integer> startIndices = Lists.newLinkedList();
	private List<Integer> endIndices = Lists.newLinkedList();
	private Rectangle currentSelection;
	private JBPopup popup;


	public SPViewPanelPopOut(Project project) {
		this.project = project;
		addClosePopupListener();
//		addGoUpListener();
//		addGoDownListener();
//		DefaultSyntaxKit.initKit();
//		final JEditorPane codeEditor = new JEditorPane();
	}

	public static void main(String[] args) {
//		new SPViewPanelPopOut(project).initializeAndPopWindow(new JFrame(SPViewPanelPopOut.class.getName()), "select * from T_TABLE");
	/*	JFrame f = new JFrame(SPViewPanelPopOut.class.getName());
		final Container c = f.getContentPane();
		c.setLayout(new BorderLayout());
		DefaultSyntaxKit.initKit();
		final SPViewPanelPopOut viewPanelPopOut = new SPViewPanelPopOut();
		final JEditorPane codeEditor = viewPanelPopOut.getEditorTextPane();
		c.add(viewPanelPopOut.getMainPanel(), BorderLayout.CENTER);
		c.doLayout();
		codeEditor.setContentType("text/java");
		codeEditor.setText("public static void main(String[] args) {\n}");
		f.setSize(800, 600);
		f.setVisible(true);
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);*/
	}

	private void addClosePopupListener() {
		closeButton.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				StackingPopupDispatcher.getInstance().close();
			}
		});
	}

	private void addGoDownListener() {
		goDownButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (currentSelection == null) {
						currentSelection = textAreaBody.modelToView((Integer) ((Deque) startIndices).getLast());
					} else {
						if (startIndices.listIterator().hasNext()) {
							currentSelection = textAreaBody.modelToView(startIndices.listIterator().next());
						}
					}
				} catch (BadLocationException e1) {
					logger.error("BadLocationException=" + e1.getMessage(), e1);
				}
				textAreaBody.setCaretPosition(textAreaBody.viewToModel(new Point(currentSelection.x, currentSelection.y)));
				textAreaScrollPane.scrollRectToVisible(currentSelection);
			}
		});
	}

	private void addGoUpListener() {
		goUpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (currentSelection == null) {
						currentSelection = textAreaBody.modelToView((Integer) ((Deque) startIndices).getLast());
					} else {
						if (startIndices.listIterator().hasNext()) {
							currentSelection = textAreaBody.modelToView(startIndices.listIterator().next());
						}
					}
				} catch (BadLocationException e1) {
					logger.error("BadLocationException=" + e1.getMessage(), e1);
				}
				textAreaBody.setCaretPosition(textAreaBody.viewToModel(new Point(currentSelection.x, currentSelection.y)));
				textAreaScrollPane.scrollRectToVisible(currentSelection);
			}
		});
	}

	public void setContent(final Content content) {
		myContent = content;
	}
/*
	private void highLightSPExecText() {
		final DefaultHighlighter.DefaultHighlightPainter orangeHighlighter = new DefaultHighlighter.DefaultHighlightPainter(JBColor.ORANGE);
		final String text = textAreaBody.getText();
		searchForStartAndEndIndices(0);
//		final int startExec = text.indexOf("exec");
//		final int endExec = text.indexOf("\n", startExec);
		final Highlighter highlighter = textAreaBody.getHighlighter();
		try {
			for (int i = 0; i < startIndices.size(); i++) {
				Integer startExec = startIndices.get(i);
				Integer endExec = endIndices.get(i);
				highlighter.addHighlight(startExec, endExec, orangeHighlighter);
			}
		} catch (BadLocationException e) {
			logger.error("highLightSPExecText(): error=" + e.getMessage(), e);
		}
	}*/

	/*private boolean searchForStartAndEndIndices(int start) {
		final String text = textAreaBody.getText();
		final int startExec = text.indexOf("\texec", start);
		if (startExec == -1) {
			return false;
		}
		startIndices.add(startExec);
		final int endExec = text.indexOf("\n", startExec);
		endIndices.add(endExec);
		return searchForStartAndEndIndices(endExec);
	}*/

	public JPanel getMainPanel() {
		return mainPanel;
	}

	public void setTextForViewing(String spName, String text) {
		PsiFile file = PsiFileFactory.getInstance(project).createFileFromText(text, SybaseDialect.INSTANCE, text, true, true);
		final SyntaxHighlighterLanguageFactory languageFactory = SyntaxHighlighterFactory.LANGUAGE_FACTORY;
		final SyntaxHighlighter syntaxHighlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(SybaseDialect.INSTANCE, project, SQLRefApplication.getVirtualFileFromPsiFile(file, project));
		Document document = PsiDocumentManager.getInstance(project).getDocument(file);
		Editor editor = EditorFactory.getInstance().createEditor(document, project, SqlFileType.INSTANCE, true);

		ComponentPopupBuilder popupBuilder = JBPopupFactory.getInstance().createComponentPopupBuilder(editor.getComponent(),
				WindowManager.getInstance().getIdeFrame(project).getComponent());
		final JBPopup[] finalPopup = new JBPopup[0];
		popupBuilder.setShowShadow(true).
				setResizable(true).
				setMovable(true).
				setTitle("SPViewer - " + spName).
				setSettingButtons(popOutSettingsPanel).
				setTitleIcon(new ActiveIcon(IconLoader.findIcon("icons/syBaseLogo_3_sm.png"))).
				setMayBeParent(true).
				setCancelOnClickOutside(false).
				setRequestFocus(true);
		popup = popupBuilder.createPopup();
//		finalPopup[0] = popup;
		popup.setSize(new Dimension(850, 900));
		((AbstractPopup) popup).setAdText("Lines in Sp=" + document.getLineCount() + " Total text length=" + document.getTextLength());
		popup.showCenteredInCurrentWindow(project);
		popup.moveToFitScreen();
		((AbstractPopup) popup).focusPreferredComponent();
//		final ActionCallback actionCallback = new SwitchManager(project, new QuickAccessSettings(), ActionManager.getInstance()).applySwitch();
	}

	@Override
	public void dispose() {

	}

	public void initializeAndPopWindow(String spName, String text) {
		setTextForViewing(spName, text);
		/*final Container c = frame.getContentPane();
		c.setLayout(new BorderLayout());
		DefaultSyntaxKit.initKit();
//		final SPViewPanelPopOut viewPanelPopOut = new SPViewPanelPopOut();
		final JEditorPane codeEditor = getEditorTextPane();
		c.add(textAreaScrollPane, BorderLayout.CENTER, -1);
//		c.doLayout();
		codeEditor.setContentType("text/sql");
		codeEditor.setText(text);
		try {
			EventQueue.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					c.setVisible(true);
				}
			});
		} catch (Exception e) {
			logger.error(e);
//		frame.setSize(800, 600);
//		frame.setVisible(true);
//		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		}*/
	}

	{
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
		$$$setupUI$$$();
	}

	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayoutManager(2, 6, new Insets(0, 0, 0, 0), -1, -1));
		topPanel = new JPanel();
		topPanel.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
		mainPanel.add(topPanel, new GridConstraints(0, 3, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		spNameLabel = new JLabel();
		spNameLabel.setHorizontalAlignment(2);
		spNameLabel.setHorizontalTextPosition(2);
		spNameLabel.setText("SP Name:");
		topPanel.add(spNameLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		spNameTextField = new JTextField();
		spNameTextField.setMinimumSize(new Dimension(150, 20));
		spNameTextField.setPreferredSize(new Dimension(150, 20));
		topPanel.add(spNameTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		spSearchSubmitBtn = new JButton();
		spSearchSubmitBtn.setText("submit");
		topPanel.add(spSearchSubmitBtn, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		textAreaScrollPane = new JScrollPane();
		mainPanel.add(textAreaScrollPane, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		textEnclosedPanel = new JPanel();
		textEnclosedPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		textAreaScrollPane.setViewportView(textEnclosedPanel);
		editorTextPane = new JEditorPane();
		textEnclosedPanel.add(editorTextPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
		popOutSettingsPanel = new JPanel();
		popOutSettingsPanel.setLayout(new BorderLayout(0, 0));
		popOutSettingsPanel.setBackground(UIManager.getColor("scrollbar"));
		mainPanel.add(popOutSettingsPanel, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_NORTHEAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		popOutSettingsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-3355444)), null));
		goUpButton = new JButton();
		goUpButton.setHorizontalAlignment(0);
		goUpButton.setHorizontalTextPosition(0);
		goUpButton.setIcon(new ImageIcon(getClass().getResource("/icons/ScrollUpArrowActive.gif")));
		goUpButton.setMaximumSize(new Dimension(25, 25));
		goUpButton.setMinimumSize(new Dimension(25, 25));
		goUpButton.setOpaque(false);
		goUpButton.setPreferredSize(new Dimension(25, 25));
		goUpButton.setText("");
		popOutSettingsPanel.add(goUpButton, BorderLayout.WEST);
		closeButton = new JButton();
		closeButton.setIcon(new ImageIcon(getClass().getResource("/actions/delete.png")));
		closeButton.setMaximumSize(new Dimension(25, 25));
		closeButton.setMinimumSize(new Dimension(25, 25));
		closeButton.setPreferredSize(new Dimension(25, 25));
		closeButton.setText("");
		popOutSettingsPanel.add(closeButton, BorderLayout.EAST);
		goDownButton = new JButton();
		goDownButton.setBorderPainted(true);
		goDownButton.setHorizontalTextPosition(0);
		goDownButton.setIcon(new ImageIcon(getClass().getResource("/icons/ScrollDownArrowActive.gif")));
		goDownButton.setMaximumSize(new Dimension(25, 25));
		goDownButton.setMinimumSize(new Dimension(25, 25));
		goDownButton.setOpaque(true);
		goDownButton.setPreferredSize(new Dimension(25, 25));
		goDownButton.setText("");
		popOutSettingsPanel.add(goDownButton, BorderLayout.CENTER);
		spNameLabel.setLabelFor(spNameTextField);
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return mainPanel;
	}
}
