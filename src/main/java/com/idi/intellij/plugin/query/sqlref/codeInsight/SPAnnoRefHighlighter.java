/*
 * User: eldad.Dor
 * Date: 16/02/14 13:49
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.sqlref.codeInsight;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.event.*;
import com.intellij.openapi.editor.ex.MarkupModelEx;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author eldad
 * @date 16/02/14
 */
public class SPAnnoRefHighlighter implements SelectionListener, CaretListener, DocumentListener {
	private Editor editor;
	private final List<RangeHighlighter> items = new ArrayList<RangeHighlighter>();
	private volatile int updating;
	private boolean autoHighlight;
	String currentWord;
	private static final int HIGHLIGHTLAYER = HighlighterLayer.SELECTION - 1;
	private static final int UPDATEDELAY = 400;

	public SPAnnoRefHighlighter(Editor editor, boolean autoHighlight) {
		this.editor = editor;
		this.autoHighlight = autoHighlight;

		editor.getSelectionModel().addSelectionListener(this);
		editor.getCaretModel().addCaretListener(this);
		editor.getDocument().addDocumentListener(this);
	}

	public void dispose() {
		editor.getSelectionModel().removeSelectionListener(this);
		editor.getCaretModel().removeCaretListener(this);
		editor.getDocument().removeDocumentListener(this);
		editor = null;
	}

	@Override
	public void selectionChanged(SelectionEvent selectionEvent) {
		if (updating > 0) {
			return;
		}

		// wenn ColumnMode -> gibts nicht mehr zu tun
		if (editor.isColumnMode()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					buildHighlighters(null); // noch löschen, da vielleicht etwas selektiert war und umgestellt wurde
				}
			});
			return;
		}
		// Selektion wurde aufgehoben -> nichts machen -> sobald cursor ausserhalb kommt wird ge'cleared... ( siehe caretPositionChanged...)
		if (selectionEvent.getNewRange().getLength() == 0) {
			return;
		}

		String text = editor.getDocument().getText();
		TextRange textRange = selectionEvent.getNewRange();

		// aufgrund selektiertem Text erstellen
		final String highlightText;
		   /* if ((textRange.getStartOffset() != 0 || textRange.getEndOffset() != text.length()) && // fix issue 5: komplettem text ausschliessen
		            BWACUtils.isStartEnd(text, textRange.getStartOffset(), textRange.getEndOffset(), false)) {
	            highlightText = textRange.substring(text);
	        } else {
	            highlightText = null; // ansonsten löschen
	        }*/
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
//	                buildHighlighters(highlightText);
			}
		});
	}

	private Timer caretChangedDelayTimer = new Timer(UPDATEDELAY, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			synchronized (items) {
				int currentOffset = editor.getCaretModel().getOffset();
				if (!items.isEmpty() && getItemIndex(currentOffset) >= 0) {
					return;
				}
				performHighlight();
			}
		}
	}) {
		{
			setRepeats(false);
		}
	};

	private void performHighlight() {
		String wordToHighlight = null;
		if (autoHighlight && !editor.getSelectionModel().hasSelection()) {
			int currentOffset = editor.getCaretModel().getOffset();
			wordToHighlight = extractWordFrom(editor.getDocument().getText(), currentOffset);
			if (wordToHighlight != null && !wordToHighlight.equals(currentWord)) {
				wordToHighlight = null;
			}
		}
		final String finalWordToHighlight = wordToHighlight;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
//				buildHighlighters(StringUtil.isEmpty(finalWordToHighlight) ? null : finalWordToHighlight);
				buildHighlighters(StringUtil.isEmpty(finalWordToHighlight) ? null : finalWordToHighlight);
			}
		});
	}

	public boolean isAutoHighlight() {
		return autoHighlight;
	}

	public void setAutoHighlight(boolean autoHighlight) {
		if (this.autoHighlight != autoHighlight) {
			this.autoHighlight = autoHighlight;
			if (!editor.getSelectionModel().hasSelection()) {
				performHighlight();
			}
		}
	}

	@Override
	public void caretPositionChanged(CaretEvent caretEvent) {
		if (updating > 0) {
			return;
		}
		caretChangedDelayTimer.restart();
	}

	@Override
	public void beforeDocumentChange(DocumentEvent documentEvent) {
	}

	@Override
	public void documentChanged(DocumentEvent documentEvent) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				buildHighlighters(null);
			}
		});
	}

	public void browse(final BrowseDirection browseDirection, final String textToHighlight) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				updating++;
				try {
					synchronized (items) {
						currentWord = null;
						if (items.isEmpty() || caretChangedDelayTimer.isRunning()) {
							if (textToHighlight != null) {
								currentWord = textToHighlight;
							} else {
								currentWord = extractWordFrom(editor.getDocument().getText(), editor.getCaretModel().getOffset());
							}
							if (currentWord == null) {
								return;
							}
							buildHighlighters(currentWord);
						}
						int index = getItemIndex(editor.getCaretModel().getOffset()) + (BrowseDirection.NEXT.equals(browseDirection) ? 1 : -1);

						if (index >= 0 && index < items.size()) {
							int offset = items.get(index).getStartOffset();
							editor.getCaretModel().moveToOffset(offset);
							editor.getSelectionModel().setSelection(offset, offset + currentWord.length());
							editor.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
						}
					}
				} finally {
					updating--;
				}
			}
		});
	}

	private int getItemIndex(int offset) {
		synchronized (items) {
			for (int i = 0; i < items.size(); i++) {
				RangeHighlighter item = items.get(i);
				if (offset >= item.getStartOffset() && offset <= item.getEndOffset()) {
					return i;
				}
			}
		}
		return -1;
	}

	// DISPATCH THREAD METHODS

	private void buildHighlighters(final String highlightText) {
		ApplicationManager.getApplication().assertIsDispatchThread();
		synchronized (items) {
			final MarkupModelEx markupModel = (MarkupModelEx) editor.getMarkupModel();
			for (RangeHighlighter rangeHighlighter : items) {
				if (markupModel.containsHighlighter(rangeHighlighter)) {
					markupModel.removeHighlighter(rangeHighlighter);
				}
			}
			items.clear();
			if (highlightText != null) {
				String text = editor.getDocument().getText();
				final TextAttributes textAttributes = editor.getColorsScheme().getAttributes(EditorColors.SEARCH_RESULT_ATTRIBUTES);
				int index = -1;
				do {
					index = text.indexOf(highlightText, index + 1);
					if (index >= 0 && isStartEnd(text, index, index + highlightText.length(), true)) {
						RangeHighlighter rangeHighlighter = markupModel.addRangeHighlighter(index, index + highlightText.length(), HIGHLIGHTLAYER, textAttributes, HighlighterTargetArea.EXACT_RANGE);
						/*final SyntaxHighlighter syntaxHighlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(SybaseDialect.INSTANCE, editor.getProject(),
								SQLRefApplication.getVirtualFileFromPsiFile(, project));*/
						rangeHighlighter.setErrorStripeTooltip(highlightText);
						items.add(rangeHighlighter);
//						highlighter.setEditor(new LightHighlighterClient(editor.getDocument(), editor.getProject()));
//						final EditorHighlighter highlighter = HighlighterFactory.createHighlighter(syntaxHighlighter, editor.getColorsScheme());
					}
				} while (index >= 0);
			}
		}
	}

	public static String extractWordFrom(@NotNull String text, int index) {
		int length = text.length();
		if (length <= 0 || index < 0 || index > length) {
			return null;
		}
		int begin = index;
		while (begin > 0 && isWordchar(text.charAt(begin - 1))) {
			begin--;
		}
		int end = index;
		while (end < length && isWordchar(text.charAt(end))) {
			end++;
		}
		if (end <= begin || (begin == 0 && end == length)) {
			return null;
		}
		return text.substring(begin, end);
	}

	/**
	 *
	 */
	public static boolean isStartEnd(@NotNull final String text, final int begin, final int end, boolean checkOnlyPreviousNext) {
		return isWordStart(text, begin, checkOnlyPreviousNext) && isWordEnd(text, end, checkOnlyPreviousNext);
	}

	static boolean isWordStart(@NotNull final String text, final int begin, boolean checkOnlyPrevious) {
		int length = text.length();
		return (length > 0 && begin >= 0 && begin < length) &&
				(begin == 0 || !isWordchar(text.charAt(begin - 1))) &&
				(checkOnlyPrevious || isWordchar(text.charAt(begin)));
	}

	/**
	 * @param text
	 * @param end
	 * @param checkOnlyNext
	 * @return
	 */
	static boolean isWordEnd(@NotNull final String text, final int end, boolean checkOnlyNext) {
		int length = text.length();
		return (length > 0 && end > 0 && end <= length) &&
				(end == length || !isWordchar(text.charAt(end))) &&
				(checkOnlyNext || isWordchar(text.charAt(end - 1)));
	}

	/**
	 * @param currentChar
	 * @return
	 */
	private static boolean isWordchar(char currentChar) {
		return ((currentChar >= 65) && (currentChar <= 90)) || // A..Z
				((currentChar >= 97) && (currentChar <= 122)) || // a..z
				((currentChar == 95)) || // _
				((currentChar >= 48) && (currentChar <= 57)) || // 0..9
				((currentChar >= 192) && (currentChar <= 255) && (currentChar != 215) && (currentChar != 247));  // À..ÿ (ohne ×, ÷)
	}

	enum BrowseDirection {
		NEXT, PREVIOUS
	}

}