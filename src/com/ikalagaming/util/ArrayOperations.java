
package com.ikalagaming.util;

/**
 * Methods useful for Arrays.
 * 
 * @author Ches Burks
 * 
 */
public class ArrayOperations {

	private final static String start = "[";
	private final static String end = "]";
	private final static String separator = ",";

	/**
	 * Converts the given array into a String that can be output. The String
	 * will start with '[', end with ']' and have values separated by ','. Empty
	 * arrays will output "[]". The values are determined by the objects
	 * {@code toString()} method.
	 * 
	 * @param array the array to convert
	 * @return the formatted string
	 */
	public static String convertToString(Object[] array) {
		String output = "";

		if (array == null) {
			return start + end;
		}
		if (array.length <= 0) {
			return start + end;
		}
		output = output.concat(start);
		for (int i = 0; i <= array.length - 2; ++i) {
			output = output.concat(array[i].toString());
			output = output.concat(separator);
		}
		output = output.concat(array[array.length - 1].toString());
		output = output.concat(end);

		return output;
	}
}
