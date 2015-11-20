/*
 * User: eldad.Dor
 * Date: 03/01/2015 19:56
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.query.annoref.task;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.util.Condition;

import java.util.ArrayList;
import java.util.List;

/**
 * @author eldad
 * @date 03/01/2015
 */
public class IDIProgressIndicator implements IndicateProgressListener {
	private static final Logger log = Logger.getInstance(IDIProgressIndicator.class.getName());
	private ProgressIndicator myIndicator;
	private final List<Condition<IDIProgressIndicator>> myCancelConditions = new ArrayList();
	private int totalFiles;
	private double fraction;

	public IDIProgressIndicator() {
		this(new MyEmptyProgressIndicator());
	}

	public IDIProgressIndicator(ProgressIndicator myIndicator, int totalFiles) {
		this.myIndicator = myIndicator;
		this.totalFiles = totalFiles;
		fraction = 1;
	}

	public IDIProgressIndicator(ProgressIndicator indicator) {
		myIndicator = indicator;

	}


	public synchronized void setIndicator(ProgressIndicator i) {
		i.setText(myIndicator.getText());
		i.setText2(myIndicator.getText2());
		i.setFraction(myIndicator.getFraction());
		if (i.isCanceled()) {
			i.cancel();
		}
		myIndicator = i;
	}

	public synchronized ProgressIndicator getIndicator() {
		return myIndicator;
	}

	public synchronized void setText(String text) {
		myIndicator.setText(text);
	}

	public synchronized void setText2(String text) {
		myIndicator.setText2(text);
	}

	public synchronized void setFraction(double fraction) {
		myIndicator.setFraction(fraction);
	}

	public synchronized void setIndeterminate(boolean indeterminate) {
		myIndicator.setIndeterminate(indeterminate);
	}

	public synchronized void pushState() {
		myIndicator.pushState();
	}

	public synchronized void popState() {
		myIndicator.popState();
	}

	@Override
	public synchronized void cancel() {
		myIndicator.cancel();
	}

	public synchronized void addCancelCondition(Condition<IDIProgressIndicator> condition) {
		myCancelConditions.add(condition);
	}

	public synchronized void removeCancelCondition(Condition<IDIProgressIndicator> condition) {
		myCancelConditions.remove(condition);
	}

	public synchronized boolean isCanceled() {
		if (myIndicator.isCanceled()) {
			return true;
		}
		for (Condition each : myCancelConditions) {
			if (each.value(this)) {
				return true;
			}
		}
		return false;
	}

	public void checkCanceled() throws IDIProcessCancelledException {
		if (isCanceled()) {
			throw new IDIProcessCancelledException();
		}
	}

	public void checkCanceledNative() {
		if (isCanceled()) {
			throw new ProcessCanceledException();
		}
	}

	@Override
	public synchronized void indicateChange() {
		final double fraction1 = fraction++ / totalFiles;
		setFraction(fraction1);
	}

	private static class MyEmptyProgressIndicator extends EmptyProgressIndicator {
		private String myText;
		private String myText2;
		private double myFraction;

		@Override
		public void setText(String text) {
			myText = text;
		}

		@Override
		public String getText() {
			return myText;
		}

		@Override
		public void setText2(String text) {
			myText2 = text;
		}

		@Override
		public String getText2() {
			return myText2;
		}

		@Override
		public void setFraction(double fraction) {
			myFraction = fraction;
		}

		@Override
		public double getFraction() {
			return myFraction;
		}
	}
}

