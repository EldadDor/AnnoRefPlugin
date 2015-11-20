/*
 * User: eldad.Dor
 * Date: 10/07/2014 17:36
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.common;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiManagerEx;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.impl.source.tree.TreeElementVisitor;
import com.intellij.psi.impl.source.tree.java.PsiJavaTokenImpl;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author eldad
 * @date 10/07/2014
 */
public class AnnoRefElementType extends PsiJavaTokenImpl {

	private PsiElement psiElement;

	public AnnoRefElementType(IElementType paramIElementType, CharSequence paramCharSequence) {
		super(paramIElementType, paramCharSequence);
	}

	@Override
	public IElementType getTokenType() {
		return super.getTokenType();
	}

	@Override
	public void accept(@NotNull PsiElementVisitor paramPsiElementVisitor) {
		super.accept(paramPsiElementVisitor);
	}

	@Override
	public String toString() {
		return super.toString();
	}

	@NotNull
	@Override
	public PsiElement[] getChildren() {
		return super.getChildren();
	}

	@Override
	public PsiElement getFirstChild() {
		return super.getFirstChild();
	}

	@Override
	public PsiElement getLastChild() {
		return super.getLastChild();
	}

	@Override
	public void acceptChildren(@NotNull PsiElementVisitor paramPsiElementVisitor) {
		super.acceptChildren(paramPsiElementVisitor);
	}

	@Override
	public PsiElement getParent() {
		return super.getParent();
	}

	@Override
	public PsiElement getNextSibling() {
		return super.getNextSibling();
	}

	@Override
	public PsiElement getPrevSibling() {
		return super.getPrevSibling();
	}

	@Override
	public PsiFile getContainingFile() {
		return super.getContainingFile();
	}

	@Override
	public PsiElement findElementAt(int paramInt) {
		return super.findElementAt(paramInt);
	}

	@Override
	public PsiReference findReferenceAt(int paramInt) {
		return super.findReferenceAt(paramInt);
	}

	@Override
	public PsiElement copy() {
		return super.copy();
	}

	@Override
	public boolean isValid() {
		return super.isValid();
	}

	@Override
	public boolean isWritable() {
		return super.isWritable();
	}

	@Override
	public PsiReference getReference() {
		return super.getReference();
	}

	@NotNull
	@Override
	public PsiReference[] getReferences() {
		return super.getReferences();
	}

	@Override
	public PsiElement add(@NotNull PsiElement paramPsiElement) throws IncorrectOperationException {
		return super.add(paramPsiElement);
	}

	@Override
	public PsiElement addBefore(@NotNull PsiElement paramPsiElement1, PsiElement paramPsiElement2) throws IncorrectOperationException {
		return super.addBefore(paramPsiElement1, paramPsiElement2);
	}

	@Override
	public PsiElement addAfter(@NotNull PsiElement paramPsiElement1, PsiElement paramPsiElement2) throws IncorrectOperationException {
		return super.addAfter(paramPsiElement1, paramPsiElement2);
	}

	@Override
	public void checkAdd(@NotNull PsiElement paramPsiElement) throws IncorrectOperationException {
		super.checkAdd(paramPsiElement);
	}

	@Override
	public PsiElement addRange(PsiElement paramPsiElement1, PsiElement paramPsiElement2) throws IncorrectOperationException {
		return super.addRange(paramPsiElement1, paramPsiElement2);
	}

	@Override
	public PsiElement addRangeBefore(@NotNull PsiElement paramPsiElement1, @NotNull PsiElement paramPsiElement2, PsiElement paramPsiElement3) throws IncorrectOperationException {
		return super.addRangeBefore(paramPsiElement1, paramPsiElement2, paramPsiElement3);
	}

	@Override
	public PsiElement addRangeAfter(PsiElement paramPsiElement1, PsiElement paramPsiElement2, PsiElement paramPsiElement3) throws IncorrectOperationException {
		return super.addRangeAfter(paramPsiElement1, paramPsiElement2, paramPsiElement3);
	}

	@Override
	public void delete() throws IncorrectOperationException {
		super.delete();
	}

	@Override
	public void checkDelete() throws IncorrectOperationException {
		super.checkDelete();
	}

	@Override
	public void deleteChildRange(PsiElement paramPsiElement1, PsiElement paramPsiElement2) throws IncorrectOperationException {
		super.deleteChildRange(paramPsiElement1, paramPsiElement2);
	}

	@Override
	public PsiElement replace(@NotNull PsiElement paramPsiElement) throws IncorrectOperationException {
		return super.replace(paramPsiElement);
	}

	@Override
	public boolean processDeclarations(@NotNull PsiScopeProcessor paramPsiScopeProcessor, @NotNull ResolveState paramResolveState, PsiElement paramPsiElement1, @NotNull PsiElement paramPsiElement2) {
		return super.processDeclarations(paramPsiScopeProcessor, paramResolveState, paramPsiElement1, paramPsiElement2);
	}

	@Override
	public PsiElement getContext() {
		return super.getContext();
	}

	@Override
	public PsiElement getNavigationElement() {
		return super.getNavigationElement();
	}

	@Override
	public PsiElement getOriginalElement() {
		return super.getOriginalElement();
	}

	@Override
	public boolean isPhysical() {
		return super.isPhysical();
	}

	@NotNull
	@Override
	public GlobalSearchScope getResolveScope() {
		return super.getResolveScope();
	}

	@NotNull
	@Override
	public SearchScope getUseScope() {
		return super.getUseScope();
	}

	@NotNull
	@Override
	public Project getProject() {
		return super.getProject();
	}

	@NotNull
	@Override
	public Language getLanguage() {
		return super.getLanguage();
	}

	@Override
	public ASTNode getNode() {
		return super.getNode();
	}

	@Override
	public PsiElement getPsi() {
		return super.getPsi();
	}

	@Override
	public ItemPresentation getPresentation() {
		return super.getPresentation();
	}

	@Override
	public String getName() {
		return super.getName();
	}

	@Override
	public void navigate(boolean paramBoolean) {
		super.navigate(paramBoolean);
	}

	@Override
	public boolean canNavigate() {
		return super.canNavigate();
	}

	@Override
	public boolean canNavigateToSource() {
		return super.canNavigateToSource();
	}

	@Override
	public boolean isEquivalentTo(PsiElement paramPsiElement) {
		return super.isEquivalentTo(paramPsiElement);
	}

	@Override
	public LeafElement clone() {
		return super.clone();
	}

	@Override
	public int getTextLength() {
		return super.getTextLength();
	}

	@Override
	public CharSequence getChars() {
		return super.getChars();
	}

	@Override
	public String getText() {
		return super.getText();
	}

	@Override
	public char charAt(int paramInt) {
		return super.charAt(paramInt);
	}

	@Override
	public int copyTo(@Nullable char[] paramArrayOfChar, int paramInt) {
		return super.copyTo(paramArrayOfChar, paramInt);
	}

	@NotNull
	@Override
	public char[] textToCharArray() {
		return super.textToCharArray();
	}

	@Override
	public boolean textContains(char paramChar) {
		return super.textContains(paramChar);
	}

	@Override
	protected int textMatches(@NotNull CharSequence paramCharSequence, int paramInt) {
		return super.textMatches(paramCharSequence, paramInt);
	}

	@Override
	public LeafElement rawReplaceWithText(String paramString) {
		return super.rawReplaceWithText(paramString);
	}

	@Override
	public LeafElement replaceWithText(String paramString) {
		return super.replaceWithText(paramString);
	}

	@Override
	public LeafElement findLeafElementAt(int paramInt) {
		return super.findLeafElementAt(paramInt);
	}

	@Override
	public boolean textMatches(@NotNull CharSequence paramCharSequence, int paramInt1, int paramInt2) {
		return super.textMatches(paramCharSequence, paramInt1, paramInt2);
	}

	@Override
	public void acceptTree(TreeElementVisitor paramTreeElementVisitor) {
		super.acceptTree(paramTreeElementVisitor);
	}

	@Override
	public ASTNode findChildByType(IElementType paramIElementType) {
		return super.findChildByType(paramIElementType);
	}

	@Override
	public ASTNode findChildByType(IElementType paramIElementType, @Nullable ASTNode paramASTNode) {
		return super.findChildByType(paramIElementType, paramASTNode);
	}

	@Nullable
	@Override
	public ASTNode findChildByType(@NotNull TokenSet paramTokenSet) {
		return super.findChildByType(paramTokenSet);
	}

	@Nullable
	@Override
	public ASTNode findChildByType(@NotNull TokenSet paramTokenSet, @Nullable ASTNode paramASTNode) {
		return super.findChildByType(paramTokenSet, paramASTNode);
	}

	@Override
	public int hc() {
		return super.hc();
	}

	@Override
	public TreeElement getFirstChildNode() {
		return super.getFirstChildNode();
	}

	@Override
	public TreeElement getLastChildNode() {
		return super.getLastChildNode();
	}

	@Override
	public int getNotCachedLength() {
		return super.getNotCachedLength();
	}

	@Override
	public int getCachedLength() {
		return super.getCachedLength();
	}

	@Override
	public ASTNode[] getChildren(TokenSet paramTokenSet) {
		return super.getChildren(paramTokenSet);
	}

	@Override
	public void addChild(@NotNull ASTNode paramASTNode1, ASTNode paramASTNode2) {
		super.addChild(paramASTNode1, paramASTNode2);
	}

	@Override
	public void addLeaf(@NotNull IElementType paramIElementType, CharSequence paramCharSequence, ASTNode paramASTNode) {
		super.addLeaf(paramIElementType, paramCharSequence, paramASTNode);
	}

	@Override
	public void addChild(@NotNull ASTNode paramASTNode) {
		super.addChild(paramASTNode);
	}

	@Override
	public void removeChild(@NotNull ASTNode paramASTNode) {
		super.removeChild(paramASTNode);
	}

	@Override
	public void replaceChild(@NotNull ASTNode paramASTNode1, @NotNull ASTNode paramASTNode2) {
		super.replaceChild(paramASTNode1, paramASTNode2);
	}

	@Override
	public void replaceAllChildrenToChildrenOf(ASTNode paramASTNode) {
		super.replaceAllChildrenToChildrenOf(paramASTNode);
	}

	@Override
	public void removeRange(@NotNull ASTNode paramASTNode1, ASTNode paramASTNode2) {
		super.removeRange(paramASTNode1, paramASTNode2);
	}

	@Override
	public void addChildren(ASTNode paramASTNode1, ASTNode paramASTNode2, ASTNode paramASTNode3) {
		super.addChildren(paramASTNode1, paramASTNode2, paramASTNode3);
	}

	@Nullable
	@Override
	public <T extends PsiElement> T getPsi(Class<T> paramClass) {
		return super.getPsi(paramClass);
	}

	@Override
	public ASTNode copyElement() {
		return super.copyElement();
	}

	@Override
	public PsiManagerEx getManager() {
		return super.getManager();
	}

	@Override
	public TextRange getTextRange() {
		return super.getTextRange();
	}

	@Override
	public int getStartOffset() {
		return super.getStartOffset();
	}

	@Override
	public int getTextOffset() {
		return super.getTextOffset();
	}

	@Override
	public boolean textMatches(@NotNull CharSequence paramCharSequence) {
		return super.textMatches(paramCharSequence);
	}

	@Override
	public boolean textMatches(@NotNull PsiElement paramPsiElement) {
		return super.textMatches(paramPsiElement);
	}

	@Override
	public void clearCaches() {
		super.clearCaches();
	}

	@Override
	protected void onInvalidated() {
		super.onInvalidated();
	}

	@Override
	public void rawInsertBeforeMe(@NotNull TreeElement paramTreeElement) {
		super.rawInsertBeforeMe(paramTreeElement);
	}

	@Override
	public void rawInsertAfterMe(@NotNull TreeElement paramTreeElement) {
		super.rawInsertAfterMe(paramTreeElement);
	}

	@Override
	public void rawRemove() {
		super.rawRemove();
	}

	@Override
	public void rawReplaceWithList(TreeElement paramTreeElement) {
		super.rawReplaceWithList(paramTreeElement);
	}

	@Override
	protected void invalidate() {
		super.invalidate();
	}

	@Override
	public void rawRemoveUpToLast() {
		super.rawRemoveUpToLast();
	}

	@Override
	public void rawRemoveUpTo(@Nullable TreeElement paramTreeElement) {
		super.rawRemoveUpTo(paramTreeElement);
	}

	@Override
	public IElementType getElementType() {
		return super.getElementType();
	}

	@Nullable
	@Override
	public Icon getIcon(int flags) {
		return super.getIcon(flags);
	}

	@Override
	protected Icon computeBaseIcon(@IconFlags int flags) {
		return super.computeBaseIcon(flags);
	}

	@Override
	protected Icon getBaseIcon() {
		return super.getBaseIcon();
	}

	@Override
	protected Icon getAdjustedBaseIcon(Icon icon, @IconFlags int flags) {
		return super.getAdjustedBaseIcon(icon, flags);
	}

	@Override
	protected boolean isVisibilitySupported() {
		return super.isVisibilitySupported();
	}

	@Nullable
	@Override
	protected Icon getElementIcon(@IconFlags int flags) {
		return super.getElementIcon(flags);
	}

	@Override
	public String getUserDataString() {
		return super.getUserDataString();
	}

	@Override
	public void copyUserDataTo(UserDataHolderBase other) {
		super.copyUserDataTo(other);
	}

	@Override
	public <T> T getUserData(@NotNull Key<T> key) {
		return super.getUserData(key);
	}

	@Override
	public <T> void putUserData(@NotNull Key<T> key, @Nullable T value) {
		super.putUserData(key, value);
	}

	@Override
	public <T> T getCopyableUserData(Key<T> key) {
		return super.getCopyableUserData(key);
	}

	@Override
	public <T> void putCopyableUserData(Key<T> key, T value) {
		super.putCopyableUserData(key, value);
	}

	@Override
	public <T> boolean replace(@NotNull Key<T> key, @Nullable T oldValue, @Nullable T newValue) {
		return super.replace(key, oldValue, newValue);
	}

	@NotNull
	@Override
	public <T> T putUserDataIfAbsent(@NotNull Key<T> key, @NotNull T value) {
		return super.putUserDataIfAbsent(key, value);
	}

	@Override
	public void copyCopyableDataTo(@NotNull UserDataHolderBase clone) {
		super.copyCopyableDataTo(clone);
	}

	@Override
	protected void clearUserData() {
		super.clearUserData();
	}

	@Override
	public boolean isUserDataEmpty() {
		return super.isUserDataEmpty();
	}
}