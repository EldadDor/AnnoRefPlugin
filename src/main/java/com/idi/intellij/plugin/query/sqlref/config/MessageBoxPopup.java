/*
 * User: eldad.Dor
 * Date: 25/08/13
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.sqlref.config;

import javax.swing.*;
import java.awt.*;

/**
 * @author eldad
 * @date 25/08/13
 */
public class MessageBoxPopup {

	private JPanel panel1;

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
		panel1 = new JPanel();
		panel1.setLayout(new BorderLayout(0, 0));
		panel1.setPreferredSize(new Dimension(200, 100));
		final JLabel label1 = new JLabel();
		label1.setPreferredSize(new Dimension(29, 50));
		label1.setText("Label");
		panel1.add(label1, BorderLayout.NORTH);
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return panel1;
	}
}