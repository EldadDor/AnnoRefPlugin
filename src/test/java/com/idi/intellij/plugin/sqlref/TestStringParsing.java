package com.idi.intellij.plugin.sqlref;

import com.intellij.openapi.fileTypes.FileNameMatcher;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.util.PatternUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: EAD-MASTER
 * Date: 04/11/2010
 * Time: 10:53:27
 * To change this template use File | Settings | File Templates.
 */
public class TestStringParsing {

	public static void main(String[] args) {
		String pattern = "^(\\w)+(-queries.xml)$";
		String pattern2 = "/w+";
		Matcher matcher = PatternUtil.fromMask(pattern).matcher("");
		Matcher matcher2 = PatternUtil.fromMask(pattern2).matcher("");
		matcher.reset("mock-queries.xml");
		matcher2.reset("mock");

		boolean isMatch = matcher.matches();
		boolean isMatch2 = matcher2.matches();
		System.out.println("isMatch = " + isMatch);
		System.out.println("isMatch2 = " + isMatch2);
		FileNameMatcher nameMatcher = FileTypeManager.parseFromString(pattern);
		boolean result = nameMatcher.accept("mock-queries.xml");
		System.out.println("result = " + result);

		Pattern pattern3 = Pattern.compile("^([A-Za-z1-9]*(-)?[A-Za-z1-9]*(-queries.xml)$)");
		// In case you would like to ignore case sensitivity you could use this
		// statement
		// Pattern pattern = Pattern.compile("\\s+", Pattern.CASE_INSENSITIVE);
//		Matcher matcher3 = pattern3.matcher("mock-queries.xml");
		Matcher matcher3 = pattern3.matcher("queries/mock-test-queries.xml");
		// Check all occurance
		while (matcher3.find()) {
			System.out.print("Start index: " + matcher3.start());
			System.out.print(" End index: " + matcher3.end() + " ");
			System.out.println(matcher3.group());
		}

	}
}
