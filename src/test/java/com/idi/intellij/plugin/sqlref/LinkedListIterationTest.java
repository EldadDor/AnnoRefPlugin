/*
 * User: eldad.Dor
 * Date: 03/02/14 18:40
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.sqlref;

import org.junit.Test;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * @author eldad
 * @date 03/02/14
 */
public class LinkedListIterationTest {

	@Test
	public void testLinkedListIteration() throws Exception {
		final LinkedList<String> list = new LinkedList<String>();

		list.add("1");
		list.add("2");
		list.add("3");
		list.add("4");
		list.add("5");

		final MyIterator<String> it = new MyIterator(list.listIterator());
		while (it.hasNext()) {
			System.out.println(it.next());
		}
		while (it.hasPrevious()) {
			System.out.println(it.previous());
		}
/*		System.out.println(it.next());
		System.out.println(it.next());
		System.out.println(it.next());
		System.out.println(it.next());*/
	}

	public static class MyIterator<T> {

		private final ListIterator<T> listIterator;

		private boolean nextWasCalled = false;
		private boolean previousWasCalled = false;

		public MyIterator(ListIterator<T> listIterator) {
			this.listIterator = listIterator;
		}

		public T next() {
			nextWasCalled = true;
			if (previousWasCalled) {
				previousWasCalled = false;
				listIterator.next();
			}
			return listIterator.next();
		}

		public T previous() {
			if (nextWasCalled) {
				listIterator.previous();
				nextWasCalled = false;
			}
			previousWasCalled = true;
			return listIterator.previous();
		}

		public boolean hasNext() {
			return (listIterator.hasNext());
		}

		public boolean hasPrevious() {
			return (listIterator.hasPrevious());
		}
	}


}