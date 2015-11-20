/*
 * User: eldad.Dor
 * Date: 03/02/14 19:00
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.common;

import com.intellij.openapi.editor.VisualPosition;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author eldad
 * @date 03/02/14
 */
public class SPViewIndexHelper<T, S> {

	private List<T> indices;
	private Map<String, S> spLinkable;
	private HelperIterator iterator;

	public SPViewIndexHelper(Collection<T> indices) {
		this.indices = new LinkedList<T>();
		spLinkable = new ConcurrentHashMap<String, S>();
	}

	public static SPViewIndexHelper build() {
		final List<VisualPosition> synchronizedList = Collections.synchronizedList(new LinkedList<VisualPosition>());
//		final List<Integer> synchronizedList = Collections.synchronizedList(new LinkedList<Integer>());
		return new SPViewIndexHelper(synchronizedList);
	}

	public static List<Integer> buildIndices() {
		final List<VisualPosition> synchronizedList = Collections.synchronizedList(new LinkedList<VisualPosition>());
//		final List<Integer> synchronizedList = Collections.synchronizedList(new LinkedList<Integer>());
		return new SPViewIndexHelper(synchronizedList).indices;
	}

	public static void main(String[] args) {
		final SPViewIndexHelper build = SPViewIndexHelper.build();
		build.add(1);
		build.add(2);
		build.add(3);
		build.add(4);
		build.add(5);
		while (build.getIterator().hasNext()) {
			build.getIterator().next();
		}
	}


	public HelperIterator getIterator() {
		return iterator;
	}

	public void initializeIterator(SPViewIndexHelper indexHelper) {
		iterator = new HelperIterator(indexHelper.getIndices().listIterator());
	}

	public List<T> getIndices() {
		return indices;
	}

	public Map<String, S> getSpLinkable() {
		return spLinkable;
	}

	public void add(T element) {
		indices.add(element);
	}

	public class HelperIterator {

		private final ListIterator<T> listIterator;

		private boolean nextWasCalled = false;
		private boolean previousWasCalled = false;

		public HelperIterator(ListIterator<T> listIterator) {
			this.listIterator = listIterator;
		}


		public synchronized T next() {
			nextWasCalled = true;
			if (previousWasCalled) {
				previousWasCalled = false;
				listIterator.next();
			}
			if (listIterator.hasNext()) {
				return listIterator.next();
			} else {
				return getLast();
			}
		}


		public synchronized T previous() {
			if (nextWasCalled) {
				listIterator.previous();
				nextWasCalled = false;
			}
			previousWasCalled = true;
			if (listIterator.hasPrevious()) {
				return listIterator.previous();
			} else {
				return getFirst();
			}
		}

		public boolean hasNext() {
			return (listIterator.hasNext());
		}

		public boolean hasPrevious() {
			return (listIterator.hasPrevious());
		}

		public T getLast() {
			if (!indices.isEmpty()) {
				return (T) ((Deque) indices).getLast();
			}
			return null;
		}

		public T getFirst() {
			if (!indices.isEmpty()) {
				return (T) ((Deque) indices).getFirst();
			}
			return null;
		}
	}


}