package com.idi.intellij.plugin.query.annoref.model;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 16/11/2010
 * Time: 20:12:21
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class FileReferenceCollection {
	private final static Logger logger = Logger.getInstance(FileReferenceCollection.class.getName());


	private PsiFile referencedFile;
	private Boolean isListening = false;
	private Boolean isOpen = false;
	private Boolean isOpenForScanning = false;
	@NotNull
	private ConcurrentSkipListMap<String, SQLRefReference> queriesIdMap = new ConcurrentSkipListMap<String, SQLRefReference>();
	private ConcurrentSkipListMap<String, SQLRefReference> queriesRangeHighlightersMap = new ConcurrentSkipListMap<String, SQLRefReference>();

	public FileReferenceCollection(PsiReference reference) {
	}

	public FileReferenceCollection() {
		isOpen = false;
		isListening = false;
	}

	public FileReferenceCollection(PsiFile referencedFile) {
		this.referencedFile = referencedFile;
	}

	public PsiFile getReferencedFile() {
		return referencedFile;
	}

	public void setReferencedFile(PsiFile referencedFile) {
		if (this.referencedFile == null && referencedFile != null) {
			logger.info("setReferencedFile(): referencedFile= " + referencedFile.getName());
			this.referencedFile = referencedFile;

		}
	}


	public Boolean isOpen() {
		return isOpen;
	}

	public void setOpen(Boolean open) {
		isOpen = open;
	}

	public Boolean isListening() {
		return isListening;
	}

	public void setListening(Boolean listening) {
		isListening = listening;
	}

	public Boolean getOpenForScanning() {
		return isOpenForScanning;
	}

	public void setOpenForScanning(Boolean openForScanning) {
		isOpenForScanning = openForScanning;
	}

	public SQLRefReference putSQLRefIntoCollection(@NotNull SQLRefReference reference) {
		String key = StringUtil.stripQuotesAroundValue(reference.getXmlPsiElement().getText());
		logger.info("putSQLRefIntoCollection(): key=" + key + " ref=" + reference);
		return queriesIdMap.put(key, reference);
	}


	@NotNull
	public Map<String, SQLRefReference> getQueriesIdMap() {
		return queriesIdMap;
	}

	public ConcurrentSkipListMap<String, SQLRefReference> getQueriesRangeHighlightersMap() {
		return queriesRangeHighlightersMap;
	}

	public String showQueriesIdMapInfo() {
		StringBuilder sb = new StringBuilder();
		for (final String key : queriesIdMap.keySet()) {
			sb.append(queriesIdMap.get(key).toString());
		}
		return sb.toString();
	}

	@Nullable
	public SQLRefReference getQueryId(String ref) {
		if (queriesIdMap.containsKey(ref)) {
			return queriesIdMap.get(ref);
		}
		return null;
	}

	protected FileReferenceCollection cloneRangeHighlightersInFileCollection() {
		FileReferenceCollection newFileRefCollection = new FileReferenceCollection(getReferencedFile());
		for (String refKey : getQueriesIdMap().keySet()) {
			SQLRefReference refReference = getQueriesIdMap().get(refKey);
			if (refReference.getRangeHighlighter() != null) {
				newFileRefCollection.getQueriesRangeHighlightersMap().put(refKey, new SQLRefReference(refReference.getRangeHighlighter(), refKey));
			} else {
				newFileRefCollection.getQueriesRangeHighlightersMap().put(refKey, new SQLRefReference(refKey));
			}
		}
		return newFileRefCollection;
	}
}
