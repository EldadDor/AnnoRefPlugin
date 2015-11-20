package com.idi.intellij.plugin.query.annoref.inspection;

import com.idi.intellij.plugin.query.annoref.repo.model.SQLRefReference;
import com.idi.intellij.plugin.query.annoref.util.AnnoRefModelUtil;
import com.intellij.find.findUsages.PsiElement2UsageTargetAdapter;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.usages.UsageTarget;
import com.intellij.usages.UsageTargetProvider;
import org.jetbrains.annotations.Nullable;

/**
 * Created by EAD-MASTER on 2/22/14.
 */
public class AnnoRefUsagesTargetProvider implements UsageTargetProvider {
	private static final Logger log = Logger.getInstance(AnnoRefUsagesTargetProvider.class.getName());

	@Nullable
	@Override
	public UsageTarget[] getTargets(Editor editor, PsiFile file) {
		final CaretModel caretModel = editor.getCaretModel();
		final int position = caretModel.getOffset();
		PsiElement element = file.findElementAt(position);
		final SQLRefReference annoRef = AnnoRefModelUtil.isValidAnnoRef(element);
		if (annoRef != null) {
			assert element != null;
			return new UsageTarget[]{new PsiElement2UsageTargetAdapter(element)};
		} else {
			return UsageTarget.EMPTY_ARRAY;
		}
	}

	@Nullable
	@Override
	public UsageTarget[] getTargets(PsiElement psiElement) {
		return UsageTarget.EMPTY_ARRAY;
	}
}
