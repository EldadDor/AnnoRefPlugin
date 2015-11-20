package com.idi.intellij.plugin.query.annoref.common;

import com.intellij.extapi.psi.PsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by EAD-MASTER on 2/22/14.
 */
public abstract class AnnoRefPsiElement extends PsiElementBase implements NavigationItem {

	protected PsiElement psiElement;
	protected PsiElement wrappedElement;
	protected PsiElement[] psiElements;

	protected AnnoRefPsiElement(PsiElement psiElement) {
		this.psiElement = psiElement;
		wrappedElement = psiElement;
		final LeafElement clone = ((LeafElement) psiElement).clone();
	}

	protected AnnoRefPsiElement(PsiElement psiElement, PsiElement[] psiElements) {
		this.psiElement = psiElement;
//		wrappedElement = new AnnoRefElementType(((PsiJavaToken) psiElement.copy()).getTokenType(), psiElement.getText());
		wrappedElement = psiElement;
		final LeafElement clone = ((LeafElement) psiElement).clone();
		this.psiElements = psiElements;
	}

	public PsiElement[] getPsiElements() {
		return psiElements;
	}

	public PsiElement getPsiElement() {
		return psiElement;
	}

	public PsiElement getWrappedElement() {
		return psiElement;
	}

	@NotNull
	@Override
	public PsiElement getNavigationElement() {
		return psiElement.getChildren()[2];
	}

	@NotNull
	@Override
	public Language getLanguage() {
		return psiElement.getLanguage();
	}

	@NotNull
	@Override
	public PsiElement[] getChildren() {
		return new PsiElement[0];
	}

	@Override
	public PsiElement getParent() {
		return psiElement.getParent().getParent();
	}

	@Override
	public PsiElement getFirstChild() {
		return psiElement.getFirstChild();
	}

	@Override
	public PsiElement getLastChild() {
		return psiElement.getLastChild();
	}

	@Override
	public PsiElement getNextSibling() {
		return psiElement.getNextSibling();
	}

	@Override
	public PsiElement getPrevSibling() {
		return psiElement.getPrevSibling();
	}


	public String getPresentableText() {
		return psiElement.getText();
	}

	@Nullable
	@Override
	public TextRange getTextRange() {
		return psiElement.getTextRange();
	}

	@Override
	public int getStartOffsetInParent() {
		return psiElement.getStartOffsetInParent();
	}

	@Override
	public int getTextLength() {
		return psiElement.getTextLength();
	}

	@Nullable
	@Override
	public PsiElement findElementAt(int offset) {
		return psiElement.findElementAt(offset);
	}

	@Override
	public int getTextOffset() {
		return psiElement.getTextOffset();
	}

	@Override
	public String getText() {
		return psiElement.getText();
	}

	@NotNull
	@Override
	public char[] textToCharArray() {
		return psiElement.getText().toCharArray();
	}

	@Override
	public boolean textContains(char c) {
		final boolean contains = psiElement.getText().contains(new CharSequence() {
			@Override
			public int length() {
				return psiElement.getText().length();
			}

			@Override
			public char charAt(int index) {
				return psiElement.getText().charAt(index);
			}

			@Override
			public CharSequence subSequence(int start, int end) {
				return psiElement.getText().subSequence(start, end);
			}
		});
		return contains;
	}

	@Override
	public ASTNode getNode() {
		return psiElement.getNode();
	}

	class AnnoRefElementType extends LeafElement {

		AnnoRefElementType(IElementType paramIElementType, CharSequence paramCharSequence) {
			super(paramIElementType, paramCharSequence);
		}

		@Override
		public String getText() {
			return wrappedElement.getText();
		}

		//		@Override
//		public LeafElement clone() {
//			return super.clone();
//		}
	}
}
