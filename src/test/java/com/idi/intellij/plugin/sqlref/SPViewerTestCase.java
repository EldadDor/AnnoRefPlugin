package com.idi.intellij.plugin.sqlref;

import javax.swing.*;
import java.awt.*;

/**
 * Created by EAD-MASTER on 2/1/14.
 */
//public class SPViewerTestCase extends LightCodeInsightFixtureTestCase {
public class SPViewerTestCase  {
	public void testPanelViewConstruction() throws Exception {
//		final SPViewPanelForm panelForm = new SPViewPanelForm();
		final JFrame frame = new JFrame("SPViewMainPanel");
//		frame.setContentPane(panelForm.getMainPanel());
		frame.setSize(new Dimension(700, 500));
		frame.pack();
		frame.setVisible(true);
		Thread.sleep(Integer.MAX_VALUE);
	}

	public static void main(String[] args) {
//		final SPViewPanelForm panelForm = new SPViewPanelForm();
		JFrame f = new JFrame(SPViewerTestCase.class.getName());
		final Container c = f.getContentPane();
		c.setLayout(new BorderLayout());

//		DefaultSyntaxKit.initKit();

		final JEditorPane codeEditor = new JEditorPane();
		JScrollPane scrPane = new JScrollPane(codeEditor);
		c.add(scrPane, BorderLayout.CENTER);
		c.doLayout();
		codeEditor.setContentType("text/java");
		codeEditor.setText("public static void main(String[] args) {\n}");

		f.setSize(800, 600);
		f.setVisible(true);
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

	}
}
