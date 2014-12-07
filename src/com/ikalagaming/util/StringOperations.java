
package com.ikalagaming.util;

/**
 * Methods useful for Strings.
 * 
 * @author Ches Burks
 * 
 */
public class StringOperations {
	/**
	 * Counts the number of times the given string (toFind) occurs in str. If
	 * str is empty or does not contain the substring, 0 is returned.
	 * 
	 * @param toSearch the string to search
	 * @param toFind the string to count
	 * @return how many times the substring appears in the string
	 */
	public static int countOccurances(String toSearch, String toFind) {
		if (toSearch.length() == 0) {
			return 0;
		}
		if (!toSearch.contains(toFind)) {
			return 0;
		}
		return (toSearch.length() - toSearch.replace(toFind, "").length())
				/ toFind.length();
	}

	/**
	 * Counts the number of times the given character (toFind) occurs in str. If
	 * str is empty or does not contain the substring, 0 is returned.
	 * 
	 * @param toSearch the string to search
	 * @param toFind the character to count
	 * @return how many times the character appears in the string
	 */
	public static int countOccurances(String toSearch, char toFind) {
		int count = 0;
		if (toSearch.length() == 0) {
			return count;
		}
		int i;
		for (i = 0; i < toSearch.length(); ++i) {
			if (toSearch.charAt(i) == toFind) {
				++count;
			}
		}
		return count;
	}
}
