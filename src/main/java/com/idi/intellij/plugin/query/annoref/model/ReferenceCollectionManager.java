package com.idi.intellij.plugin.query.annoref.model;

import com.idi.intellij.plugin.query.annoref.util.AnnRefApplication;
import com.idi.intellij.plugin.query.annoref.util.SQLRefDataAccessor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 05/11/2010
 * Time: 15:26:12
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class ReferenceCollectionManager extends AbstractCollection<FileReferenceCollection> {
	private static final Logger LOGGER = Logger.getInstance(ReferenceCollectionManager.class.getName());
	@NotNull
	private Set<FileReferenceCollection> references = new HashSet<FileReferenceCollection>();
	@NotNull
//	private Map<String, FileReferenceCollection> queriesIdValue = new ConcurrentHashMap<String, FileReferenceCollection>();
	private Map<String, FileReferenceCollection> queriesIdValue = new HashMap<String, FileReferenceCollection>();
	private FileReferenceCollection beforeChangesFileReference;
//	private ProgressIndicator progressIndicator;

	public ReferenceCollectionManager() {
	}


	public ReferenceCollectionManager(@NotNull Collection<PsiReference> psiReferences) {
		for (PsiReference reference : psiReferences) {
			references.add(new FileReferenceCollection(reference));
		}
	}

	public static ReferenceCollectionManager getInstance(Project project) {
		return AnnRefApplication.getCurrentProjectReferenceCollectionManager(project);
	}

	public FileReferenceCollection resetXMLSQLReferencesInCollection(String fileName) throws Exception {
		LOGGER.info(MessageFormat.format("Resetting XML file: {0} via ReferenceCollectionManager", fileName));
		FileReferenceCollection referenceCollection = getQueriesCollection(fileName, false);
		LOGGER.info("ReferenceCollection retrieved for reset: " + referenceCollection);
		try {
			if (referenceCollection != null && !referenceCollection.getQueriesIdMap().isEmpty()) {
				LOGGER.info(MessageFormat.format("ReferenceCollection holding data: \n{0}", referenceCollection.showQueriesIdMapInfo()));
				beforeChangesFileReference = referenceCollection.cloneRangeHighlightersInFileCollection();
				referenceCollection.getQueriesIdMap().clear();
				return beforeChangesFileReference;
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
		return null;
	}

	public void revisitXMLReferenceCollectionAfterContentsChangedViaCache(final Project project, FileReferenceCollection newReferenceCollection) throws Exception {
		try {
//			progressIndicator.setFraction(0.0 + (1 / 30));
			NavigableSet navigableSetAfter = ((NavigableMap) newReferenceCollection.getQueriesIdMap()).descendingKeySet();
			for (int i = 0; i < navigableSetAfter.descendingSet().toArray().length; i++) {
//				progressIndicator.setFraction(0.0 + ((1 + i) / 150));
				String after = (String) navigableSetAfter.descendingSet().toArray()[i];
				ClassReferenceCache currentProjectClassesReferenceCache = AnnRefApplication.getCurrentProjectClassesReferenceCache(project);
				List<Pair<VirtualFile, PsiElement>> classesRefs = currentProjectClassesReferenceCache.hitCacheForClassReference(after);
				if (classesRefs != null && !classesRefs.isEmpty()) {
					LOGGER.info("Retrieving cached class : " + currentProjectClassesReferenceCache.displayCachedClassInfo(after));
					newReferenceCollection.getQueryId(after).getClassPsiElements().clear();
					for (Pair<VirtualFile, PsiElement> classRef : classesRefs) {
						newReferenceCollection.getQueryId(after).getClassPsiElements().put(classRef.getFirst().getName(), classRef.getSecond());
						if (newReferenceCollection.getQueryId(after).getRangeHighlighter() != null) {
							if (AnnRefApplication.getInstance(project, SQLRefDataAccessor.class).removeErrorTextRangeForElement(newReferenceCollection.getQueryId(after).getXmlPsiElement(),
									newReferenceCollection.getQueryId(after).getRangeHighlighter())) {
								LOGGER.info("RangeHighLighter was removed successfully");
							} else {
								LOGGER.warn("RangeHighLighter failed to be removed!");
							}
						}
					}
				} else {
					SQLRefReference newSQLRef = newReferenceCollection.getQueryId(after);
					newSQLRef.setRangeHighlighter(AnnRefApplication.getInstance(project, SQLRefDataAccessor.class).addErrorTextRangeForElement(newSQLRef.getXmlPsiElement()));
				}
			}
//			progressIndicator.setFraction(0.0 + (1 / 30));
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
	}

	public void revisitXMLReferenceCollectionAfterContentsChanged(FileReferenceCollection newReferenceCollection) {
		NavigableSet navigableSetAfter = ((NavigableMap) newReferenceCollection.getQueriesIdMap()).descendingKeySet();
		NavigableSet navigableSetBefore = ((NavigableMap) beforeChangesFileReference.getQueriesIdMap()).descendingKeySet();
		for (int i = 0; i < navigableSetAfter.descendingSet().toArray().length; i++) {
			String after = (String) navigableSetAfter.descendingSet().toArray()[i];
			String before = (String) navigableSetBefore.descendingSet().toArray()[i];
			if (!after.equals(before)) {
				newReferenceCollection.getQueriesIdMap().get(after).setClassElements(beforeChangesFileReference.getQueryId(before).getClassPsiElements());
			}
		}
	}

	public void cloneBeforeRangeHighlighterToAfter(FileReferenceCollection newReferenceCollection) {
		NavigableSet navigableSetAfter = createNewNavigationSetFromGiven(((NavigableMap) newReferenceCollection.getQueriesIdMap()).descendingKeySet());
		NavigableSet navigableSetBefore = ((NavigableMap) beforeChangesFileReference.getQueriesRangeHighlightersMap()).descendingKeySet();
		navigableSetAfter.removeAll(navigableSetBefore);
		navigableSetBefore.removeAll(((NavigableMap) newReferenceCollection.getQueriesIdMap()).descendingKeySet());
		if (!navigableSetAfter.isEmpty() && !navigableSetBefore.isEmpty()) {
			String soleSurvivorAfter = (String) navigableSetAfter.toArray()[0];
			String soleSurvivorBefore = (String) navigableSetBefore.toArray()[0];
			if (beforeChangesFileReference.getQueriesRangeHighlightersMap().get(soleSurvivorBefore) != null) {
				newReferenceCollection.getQueriesIdMap().get(soleSurvivorAfter).setRangeHighlighter(beforeChangesFileReference.getQueriesRangeHighlightersMap().get(soleSurvivorBefore).getRangeHighlighter());
			}
		}
	}

	private NavigableSet<String> createNewNavigationSetFromGiven(NavigableSet<String> givenSet) {
		return new TreeSet<String>(givenSet);
		/*for (String key : givenSet) {

          }*/
	}

	public boolean resetClassSQLReferencesInCollection(String xmlFileName, String classFileName) {
		FileReferenceCollection referenceCollection = getQueriesCollection(xmlFileName, false);
		return referenceCollection != null && !referenceCollection.getQueriesIdMap().isEmpty() && referenceCollection.getQueriesIdMap().get(xmlFileName).removeClassAnnoReference(classFileName);
	}
	//todo if start using this - synchronize it!

	public FileReferenceCollection getBeforeChangesFileReference() {
		return beforeChangesFileReference;
	}

	@NotNull
	public Map<String, FileReferenceCollection> getQueriesIdValue() {
		return queriesIdValue;
	}

	public FileReferenceCollection getQueriesCollection(@NotNull String fileReference, Boolean toAssign) {
		if (!queriesIdValue.containsKey(fileReference) && toAssign) {
			queriesIdValue.put(fileReference, new FileReferenceCollection());
		}
		return queriesIdValue.get(fileReference);
	}

	public Map<String, SQLRefReference> getAllSQLReferencesInXmlFile(String sqlRefFileName) {
		for (String fileRef : queriesIdValue.keySet()) {
			Map<String, SQLRefReference> sqlRefReferenceMap = queriesIdValue.get(fileRef).getQueriesIdMap();
			for (String sqlRef : sqlRefReferenceMap.keySet()) {
				if (sqlRef.equals(sqlRefFileName)) {
					return queriesIdValue.get(fileRef).getQueriesIdMap();
				}
			}
		}
		return null;
	}


	@Nullable
	public SQLRefReference findReferenceInCollections(String sqlRef) {
		for (String fileRef : queriesIdValue.keySet()) {
			Map<String, SQLRefReference> refReferenceMap = queriesIdValue.get(fileRef).getQueriesIdMap();
			for (String refKey : refReferenceMap.keySet()) {
				if (refKey.equals(sqlRef)) {
					return refReferenceMap.get(refKey);
				}
			}
		}
		return null;
	}

	@Override
	public Iterator<FileReferenceCollection> iterator() {
		return references.iterator();
	}

	@Override
	public int size() {
		return references.size();
	}
}
