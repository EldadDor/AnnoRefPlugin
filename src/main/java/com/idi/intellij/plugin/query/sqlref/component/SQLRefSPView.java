/*
 * User: eldad.Dor
 * Date: 26/01/14 17:46
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.sqlref.component;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.JBColor;
import com.intellij.ui.RightAlignedLabelUI;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.UIUtil;
import org.jdesktop.swingx.JXTextArea;
import sun.awt.VerticalBagLayout;

import javax.swing.*;
import java.awt.*;

/**
 * @author eldad
 * @date 26/01/14
 */
public class SQLRefSPView extends JBPanel implements Disposable {
	private JTextArea textAreaBody;
	private JPanel g;
	private JPanel leftSidePanel;
	private JLabel headerLabel;

	public static SQLRefSPView getInstance(Project project) {
		return new SQLRefSPView(project, ToolWindowManager.getInstance(project));
	}


	//	private final ToolWindowManager myToolWindowManager;
	private final Project project;

	public SQLRefSPView(Project project, ToolWindowManager myToolWindowManager) {
		super(new BorderLayout());
		this.project = project;
//		this.myToolWindowManager = myToolWindowManager;
	}

	@Override
	public void dispose() {

	}

	public void initializeSPView() {

		g = new JPanel(new BorderLayout());
		g.setBackground(UIUtil.getControlColor());
		g.setOpaque(true);
		textAreaBody = new JXTextArea();
//		textAreaBody.setPreferredSize(new Dimension(500, 700));

		JScrollPane scroll = new JBScrollPane(textAreaBody, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		initializeSidePanel();
		g.add(scroll);
		headerLabel = new MyTitleLabel(g);
		headerLabel.setText("SP presentation");
		headerLabel.setFont(UIUtil.getLabelFont().deriveFont(1));
		headerLabel.setForeground(JBColor.foreground());
		headerLabel.setUI(new RightAlignedLabelUI());

		add(g, "Center");
		g.add(leftSidePanel, "West");
		add(headerLabel, "North");
	}

	private void initializeSidePanel() {
		leftSidePanel = new JPanel(new VerticalBagLayout());
		leftSidePanel.add(new JButton(AllIcons.Nodes.ExpandNode));
		leftSidePanel.add(new JButton(AllIcons.Nodes.CollapseNode));
	}

	public void setTextForViewing(String text) {
		textAreaBody.setText(text);
		grabFocus();
		textAreaBody.transferFocusUpCycle();
	}

	private static final class MyTitleLabel extends JLabel {
		private final JPanel a;

		private MyTitleLabel(JPanel paramJPanel) {
			a = paramJPanel;
		}

		@Override
		public void setText(String paramString) {
			if ((paramString == null) || (paramString.isEmpty())) {
				paramString = " ";
			}
			super.setText(paramString);
			if (a != null) {
				a.setToolTipText(paramString.trim().isEmpty() ? null : paramString);
			}
		}
	}

}