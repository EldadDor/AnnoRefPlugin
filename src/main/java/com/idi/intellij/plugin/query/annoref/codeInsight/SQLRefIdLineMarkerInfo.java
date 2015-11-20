package com.idi.intellij.plugin.query.annoref.codeInsight;

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.ide.util.PsiElementListCellRenderer;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBList;
import com.intellij.util.Function;
import com.intellij.util.PsiNavigateUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.MouseEvent;

/**
 * Created by IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 14/02/2011
 * Time: 21:40:59
 * To change this template use File | Settings | File Templates.
 */
public class SQLRefIdLineMarkerInfo {

	private SQLRefIdLineMarkerInfo() {

	}

	public static LineMarkerInfo<PsiElement> create(@NotNull PsiElement element, @NotNull final PsiElement[] targets, final Icon icon, @Nullable final String[] tooltips) {
		return new LineMarkerInfo<PsiElement>(element, element.getTextRange(), icon, Pass.UPDATE_ALL,
				tooltips == null || tooltips.length == 0 ? null : new Function<PsiElement, String>() {
					@Override
					public String fun(PsiElement psiElement) {
						// only one tooltip
						if (tooltips.length == 1) {
							return StringUtil.escapeXml(tooltips[0]);
						}
						// multiple tooltips
						StringBuilder sb = new StringBuilder();
						for (String tooltip : tooltips) {
							if (sb.length() > 0) {
								sb.append("<hr>");
							}
							sb.append("<div>").append(StringUtil.escapeXml(tooltip)).append("</div>");
						}
						return sb.toString();
					}
				},
				new GutterIconNavigationHandler<PsiElement>() {
					@Override
					public void navigate(MouseEvent e, PsiElement elt) {
						openTargets(e, targets, "Select Target", new AnnoRefPsiElementCellRenderer(icon));
					}
				},
				GutterIconRenderer.Alignment.LEFT
		);
	}

	public static void openTargets(MouseEvent e, PsiElement[] targets, String title, ListCellRenderer listRenderer) {
		if (targets.length == 0) {
			return;
		}
		if (targets.length == 1) {
			PsiNavigateUtil.navigate(targets[0]);
		} else {
			final JList list = new JBList(targets);
			list.setCellRenderer(listRenderer);
			final PopupChooserBuilder builder = new PopupChooserBuilder(list);
			if (listRenderer instanceof PsiElementListCellRenderer) {
				((PsiElementListCellRenderer) listRenderer).installSpeedSearch(builder);
			}
			builder.setTitle(title).setMovable(true).
					setItemChoosenCallback(new Runnable() {
						public void run() {
							int[] ids = list.getSelectedIndices();
							if (ids == null || ids.length == 0) {
								return;
							}
							Object[] selectedElements = list.getSelectedValues();
							for (Object element : selectedElements) {
								PsiElement selected = (PsiElement) element;
								PsiNavigateUtil.navigate(selected);
							}
						}
					}).createPopup().
					show(new RelativePoint(e));
		}
	}

}
