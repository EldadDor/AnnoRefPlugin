package com.idi.intellij.plugin.query.annoref.common;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

/**
 * Created by EAD-MASTER on 7/19/2014.
 */
public class AnnoRefCompletionProvider extends CompletionProvider<CompletionParameters> {
	private static final Logger logger = Logger.getInstance(AnnoRefCompletionProvider.class.getName());

	@Override
	protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
		Editor editor = parameters.getEditor();
		if (editor.getCaretModel().getCaretCount() != 1) {
			logger.info("addCompletions():");
		}
		logger.info("addCompletions():");
	}
}
