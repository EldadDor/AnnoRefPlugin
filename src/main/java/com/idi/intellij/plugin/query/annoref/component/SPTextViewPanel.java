/*
 * User: eldad.Dor
 * Date: 29/01/14 17:55
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.component;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;

import javax.swing.*;
import java.awt.*;

/**
 * @author eldad
 * @date 29/01/14
 */
@Deprecated
public class SPTextViewPanel {

	private JPanel spViewFormPanel;
	private JButton button1;
	private JButton button2;
	private JTextArea textArea1;
	private JTextField textAreaBody;
	private JButton submitButton;
	private Content myContent;

	public JPanel getSpViewFormPanel() {
		return spViewFormPanel;
	}

	public void setTextForViewing(String text) {
		textAreaBody.setText(text);
		textAreaBody.transferFocusUpCycle();
	}

	public void setContent(final Content content) {
		myContent = content;
	}

	private void createUIComponents() {
		JScrollPane scroll = new JBScrollPane(textArea1, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

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
		createUIComponents();
		spViewFormPanel = new JPanel();
		spViewFormPanel.setLayout(new GridBagLayout());
		spViewFormPanel.setPreferredSize(new Dimension(500, 200));
		final JPanel spacer1 = new JPanel();
		GridBagConstraints gbc;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.weightx = 10.0;
		gbc.weighty = 10.0;
		gbc.fill = GridBagConstraints.VERTICAL;
		spViewFormPanel.add(spacer1, gbc);
		final JSeparator separator1 = new JSeparator();
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.weightx = 200.0;
		gbc.fill = GridBagConstraints.BOTH;
		spViewFormPanel.add(separator1, gbc);
		textAreaBody = new JTextField();
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 3;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		spViewFormPanel.add(textAreaBody, gbc);
		submitButton = new JButton();
		submitButton.setText("Submit");
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		spViewFormPanel.add(submitButton, gbc);
		final JSeparator separator2 = new JSeparator();
		separator2.setPreferredSize(new Dimension(0, 20));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		spViewFormPanel.add(separator2, gbc);
		final JPanel panel1 = new JPanel();
		panel1.setLayout(new BorderLayout(0, 0));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.fill = GridBagConstraints.BOTH;
		spViewFormPanel.add(panel1, gbc);
		button2 = new JButton();
		button2.setHorizontalTextPosition(0);
		button2.setIcon(new ImageIcon(getClass().getResource("/nodes/collapseNode.png")));
		button2.setText("");
		button2.setVerticalAlignment(3);
		panel1.add(button2, BorderLayout.CENTER);
		button1 = new JButton();
		button1.setHideActionText(true);
		button1.setIcon(new ImageIcon(getClass().getResource("/nodes/expandNode.png")));
		button1.setText("");
		panel1.add(button1, BorderLayout.SOUTH);
		final JLabel label1 = new JLabel();
		label1.setText("Enter SP Name:");
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 2;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.WEST;
		spViewFormPanel.add(label1, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.BOTH;
		spViewFormPanel.add(textArea1, gbc);
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return spViewFormPanel;
	}
}