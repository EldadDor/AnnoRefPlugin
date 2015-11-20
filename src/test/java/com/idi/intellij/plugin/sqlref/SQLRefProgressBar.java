package com.idi.intellij.plugin.sqlref;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 29/03/2011
 * Time: 19:18:42
 * To change this template use File | Settings | File Templates.
 */
public class SQLRefProgressBar {
	final static int interval = 100;
	int i;
	JLabel label;
	JProgressBar pb;
	Timer timer;
	JButton button;

	public SQLRefProgressBar() {
		JFrame frame = new JFrame("Swing Progress Bar");
		button = new JButton("Start");
		button.addActionListener(new ButtonListener());

		pb = new JProgressBar(0, 10);
		pb.setValue(0);
		pb.setStringPainted(true);

		label = new JLabel("Roseindia.net");

		JPanel panel = new JPanel();
		panel.add(button);
		panel.add(pb);

		JPanel panel1 = new JPanel();
		panel1.setLayout(new BorderLayout());
		panel1.add(panel, BorderLayout.NORTH);
		panel1.add(label, BorderLayout.CENTER);
		panel1.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		frame.setContentPane(panel1);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Create a timer.
		timer = new Timer(interval, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (i == 10) {
					Toolkit.getDefaultToolkit().beep();
					timer.stop();
					button.setEnabled(true);
					pb.setValue(0);
					String str = "<html>" + "<font color=\"#FF0000\">" + "<b>" + "Downloading completed." + "</b>" + "</font>" + "</html>";
					label.setText(str);
				}
				i = i + 1;
				pb.setValue(i);
			}
		});
	}

	public static void main(String[] args) {
		SQLRefProgressBar spb = new SQLRefProgressBar();
	}

	class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			button.setEnabled(false);
			i = 0;
			String str = "<html>" + "<font color=\"#008000\">" + "<b>" + "Downloading is in process......." + "</b>" + "</font>" + "</html>";
			label.setText(str);
			timer.start();
		}
	}
}
