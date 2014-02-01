package com.idi.intellij.plugin.sqlref;

import com.idi.intellij.plugin.query.sqlref.config.SPViewPanelForm;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

import javax.swing.*;
import java.awt.*;

/**
 * Created by EAD-MASTER on 2/1/14.
 */
public class SPViewerTestCase extends LightCodeInsightFixtureTestCase {
	public void testPanelViewConstruction() throws Exception {
		final SPViewPanelForm panelForm = new SPViewPanelForm();
		final JFrame frame = new JFrame("SPViewMainPanel");
		frame.setContentPane(panelForm.getMainPanel());
		frame.setSize(new Dimension(700, 500));
		frame.pack();
		frame.setVisible(true);
		Thread.sleep(Integer.MAX_VALUE);
	}

	public static void main(String[] args) {
		final SPViewPanelForm panelForm = new SPViewPanelForm();
		final JFrame frame = new JFrame("SPViewMainPanel");
		frame.setContentPane(panelForm.getMainPanel());
		frame.setSize(new Dimension(700, 500));
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

	}
}
