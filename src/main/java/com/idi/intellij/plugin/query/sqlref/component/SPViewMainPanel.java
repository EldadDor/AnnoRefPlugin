package com.idi.intellij.plugin.query.sqlref.component;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.DumbAwareAction;

import javax.swing.*;
import java.awt.*;

/**
 * Created by EAD-MASTER on 1/31/14.
 */
public class SPViewMainPanel {
	private final JPanel myPanel;
	private final JPanel myTopPanel;
	private final JLabel myTitleLabel;

	public SPViewMainPanel(String spName) {
		myPanel = new JPanel(new BorderLayout());
		myTopPanel = new JPanel(new BorderLayout());
		myTitleLabel = new JLabel(spName);
	}

	public void createUI() {
		final JPanel wrapper = new JPanel();
		wrapper.setLayout(new BorderLayout());
		myTitleLabel.setBorder(BorderFactory.createEmptyBorder(1, 2, 0, 0));
		wrapper.add(myTitleLabel, BorderLayout.WEST);
		DefaultActionGroup dag = new DefaultActionGroup();
		dag.add(new PopupAction());
		ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, dag, true);
		wrapper.add(toolbar.getComponent(), BorderLayout.EAST);
		myTopPanel.add(wrapper, BorderLayout.CENTER);

		myPanel.setMinimumSize(new Dimension(500, 300));
		myPanel.add(myTopPanel, BorderLayout.NORTH);
	}

	public JPanel getMyPanel() {
		return myPanel;
	}

	private class PopupAction extends DumbAwareAction {

		@Override
		public void actionPerformed(AnActionEvent e) {

		}
	}

	public static void main(String[] args) {
		final SPViewMainPanel mainPanel = new SPViewMainPanel("Test_SP");
		mainPanel.createUI();
		final JFrame frame = new JFrame("SPViewMainPanel");
		frame.setVisible(true);
	}
}
