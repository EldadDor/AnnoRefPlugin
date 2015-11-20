package com.idi.intellij.plugin.query.annoref.util;

import com.intellij.openapi.util.Pair;
import com.intellij.util.containers.ConcurrentHashSet;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 8/3/13
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class DoubleValuesSameKeyMap<K, V, V2> {
	private Map<K, Set<V>> baseMapValues = new ConcurrentHashMap<K, Set<V>>();
	private Map<K, Set<V2>> baseMapValues2 = new ConcurrentHashMap<K, Set<V2>>();

	public void put(K key, V value, V2 value2) {
		if (!baseMapValues.containsKey(key) && !baseMapValues2.containsKey(key)) {
			baseMapValues.put(key, new ConcurrentHashSet<V>());
			ConcurrentHashSet<V> baseValuesSet = new ConcurrentHashSet<V>();
			ConcurrentHashSet<V2> baseValues2Set = new ConcurrentHashSet<V2>();
			baseValuesSet.add(value);
			baseValues2Set.add(value2);
			baseMapValues.put(key, baseValuesSet);
			baseMapValues2.put(key, baseValues2Set);
		}
	}

	public Pair<Set<V>, Set<V2>> get(K key) {
		if (baseMapValues.containsKey(key) && baseMapValues2.containsKey(key)) {
			return new Pair<Set<V>, Set<V2>>(baseMapValues.get(key), baseMapValues2.get(key));
		}
		return null;
	}

	public boolean contains(K key) {
		return (baseMapValues.containsKey(key) && baseMapValues2.containsKey(key));
	}


}
