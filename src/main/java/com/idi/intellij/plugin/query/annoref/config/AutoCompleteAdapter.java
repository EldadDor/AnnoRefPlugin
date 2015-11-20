/*
 * User: eldad.Dor
 * Date: 11/01/2015 23:28
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.config;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.List;

/**
 * @author eldad
 * @date 11/01/2015
 */
public class AutoCompleteAdapter
		implements DocumentListener {

	private static enum Mode {
		INSERT,
		COMPLETION
	}

	private JTextField textField;
	private final List<String> keywords;
	private Mode mode = Mode.INSERT;

	public AutoCompleteAdapter(JTextField textField, List<String> keywords) {
		this.textField = textField;
		this.keywords = keywords;
		Collections.sort(keywords);
		final String COMMIT_ACTION = "commit";
		textField.getActionMap().put(COMMIT_ACTION, new AutoCompleteAdapter.CommitAction());
	}

	@Override
	public void changedUpdate(DocumentEvent ev) {
	}

	@Override
	public void removeUpdate(DocumentEvent ev) {
	}

	@Override
	public void insertUpdate(DocumentEvent ev) {
		if (ev.getLength() != 1) {
			return;
		}

		int pos = ev.getOffset();
		String content = null;
		try {
			content = textField.getText(0, pos + 1);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		// Find where the word starts
		int w;
		for (w = pos; w >= 0; w--) {
			if (!Character.isLetter(content.charAt(w))) {
				break;
			}
		}

		// Too few chars
		if (pos - w < 2) {
			return;
		}

		String prefix = content.substring(w + 1).toLowerCase();
		int n = Collections.binarySearch(keywords, prefix);
		if (n < 0 && -n <= keywords.size()) {
			String match = keywords.get(-n - 1);
			if (match.startsWith(prefix)) {
				// A completion is found
				String completion = match.substring(pos - w);
				// We cannot modify Document from within notification,
				// so we submit a task that does the change later
				SwingUtilities.invokeLater(new CompletionTask(completion, pos + 1));
			}
		} else {
			// Nothing found
			mode = Mode.INSERT;
		}
	}

	class CommitAction extends AbstractAction {
		private static final long serialVersionUID = 5794543109646743416L;

		@Override
		public void actionPerformed(@NotNull ActionEvent ev) {
			if (mode == Mode.COMPLETION) {
				int pos = textField.getSelectionEnd();
				StringBuilder sb = new StringBuilder(textField.getText());
				sb.insert(pos, " ");
				textField.setText(sb.toString());
				textField.setCaretPosition(pos + 1);
				mode = Mode.INSERT;
			} else {
				textField.replaceSelection("\t");
			}
		}
	}

	private class CompletionTask implements Runnable {
		private String completion;
		private int position;

		CompletionTask(String completion, int position) {
			this.completion = completion;
			this.position = position;
		}

		@Override
		public void run() {
			StringBuilder sb = new StringBuilder(textField.getText());
			sb.insert(position, completion);
			textField.setText(sb.toString());
			textField.setCaretPosition(position + completion.length());
			textField.moveCaretPosition(position);
			mode = Mode.COMPLETION;
		}
	}

}
