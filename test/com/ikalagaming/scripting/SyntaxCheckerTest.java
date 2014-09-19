package com.ikalagaming.scripting;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests the functionality of the syntax checker.
 *
 * @author Ches Burks
 *
 */
public class SyntaxCheckerTest {

	/**
	 * Tests a variety of possible commands to ensure the checker is
	 * functioning properly.
	 */
	@Test
	public void testCommandCheckerSingleCommand() {
		char errorChar = ' ';
		String toCheck = "";
		int j = 0;
		int i = 0;

		//Tests several special characters inserted in various positions
		for (i = 1; i <= 6; ++i){
			for (j = 1; j <= 6; ++j){
				switch (i){
				case 0: errorChar = ' ';
				break;
				case 1: errorChar = ScriptingSettings.ARGUMENT_BEGIN;
				break;
				case 2: errorChar = ScriptingSettings.ARGUMENT_END;
				break;
				case 3: errorChar = ScriptingSettings.METHOD_INDICATOR;
				break;
				case 4: errorChar = ScriptingSettings.STRING_CHAR;
				break;
				case 5: errorChar = ScriptingSettings.TRAILING_CHAR;
				break;
				case 6: errorChar = ScriptingSettings.ARGUMENT_SEPARATOR;
				break;
				}

				toCheck = "";
				if (j == 0){
					toCheck += errorChar;
				}
				toCheck += "testCommandCheckerSingleCommand";
				if (j == 1){
					toCheck += errorChar;
				}
				toCheck += ScriptingSettings.METHOD_INDICATOR;
				if (j == 2){
					toCheck += errorChar;
				}
				toCheck += "tester";
				if (j == 3){
					toCheck += errorChar;
				}
				toCheck += ScriptingSettings.ARGUMENT_BEGIN;
				if (j == 4){
					toCheck += errorChar;
				}
				toCheck += ScriptingSettings.ARGUMENT_END;
				if (j == 5){
					toCheck += errorChar;
				}
				toCheck += ScriptingSettings.TRAILING_CHAR;
				if (j == 6){
					toCheck += errorChar;
				}
				assertFalse(toCheck,
						SyntaxChecker.isValidCommand(toCheck));
			}
		}

		assertTrue("testCommandCheckerSingleCommand.tester();",
				SyntaxChecker.isValidCommand(
						"testCommandCheckerSingleCommand.tester();"));
	}


	/**
	 * Tests a variety of possible commands to ensure the checker is
	 * functioning properly.
	 */
	@Test
	public void testCommandCheckerNestedCommand() {
		String nestedText = "";
		String toCheck = "";
		String blankMethod = "testCommandCheckerNestedCommand"
				+ ScriptingSettings.METHOD_INDICATOR
				+ "testing"
				+ ScriptingSettings.ARGUMENT_BEGIN
				+ ""
				+ ScriptingSettings.ARGUMENT_END;
		String complexMethod = "complex"
				+ ScriptingSettings.METHOD_INDICATOR
				+ "method"
				+ ScriptingSettings.ARGUMENT_BEGIN
				+ "1"
				+ ScriptingSettings.ARGUMENT_SEPARATOR
				+ "true"
				+ ScriptingSettings.ARGUMENT_END;

		int arg1 = 0;
		int arg2 = 0;
		int maxCases = 2;

		for (arg2 = 0; arg2 <= maxCases; ++arg2){
			for (arg1 = 0; arg1 <= maxCases; ++arg1){
				nestedText = "";
				switch (arg2){
				case 0: nestedText += blankMethod;
				break;
				case 1: nestedText += complexMethod;
				break;
				case 2: nestedText += "1";
				break;
				}
				nestedText += ScriptingSettings.ARGUMENT_SEPARATOR;
				switch (arg1){
				case 0: nestedText += blankMethod;
				break;
				case 1: nestedText += complexMethod;
				break;
				case 2: nestedText += "1";
				break;
				}
				toCheck += ScriptingSettings.TRAILING_CHAR;

				toCheck = "";
				toCheck += "testCommandCheckerNestedCommand";
				toCheck += ScriptingSettings.METHOD_INDICATOR;
				toCheck += "tester";
				toCheck += ScriptingSettings.ARGUMENT_BEGIN;
				toCheck += nestedText;
				toCheck += ScriptingSettings.ARGUMENT_END;
				toCheck += ScriptingSettings.TRAILING_CHAR;
				assertTrue(toCheck,
						SyntaxChecker.isValidCommand(toCheck));
			}
		}
	}

	/**
	 * Tests a variety of possible argument patterns to ensure the checker is
	 * functioning properly.
	 */
	@Test
	public void testArguments() {
		String arg = "";
		String toCheck = "";
		int arg1 = 0;
		int arg2 = 0;
		int arg3 = 0;
		int maxArg = 7;
		int errorPoint = 5;//starting at this number all cases are errors
		for (arg1 = 1; arg1 <= maxArg; ++arg1){
			for (arg2 = 0; arg2 <= maxArg; ++arg2){
				for (arg3 = 0; arg3 <= maxArg; ++arg3){
					arg = "";
					switch (arg1){
					case 0: arg += "";
					break;
					case 1: arg += "1";
					break;
					case 2: arg += "a";
					break;
					case 3: arg += ScriptingSettings.STRING_CHAR
							+ "1"
							+ ScriptingSettings.STRING_CHAR;
					break;
					case 4: arg += ScriptingSettings.STRING_CHAR
							+ "a"
							+ ScriptingSettings.STRING_CHAR;
					break;
					case 5: arg += ScriptingSettings.ARGUMENT_SEPARATOR;
					break;
					case 6: arg += ScriptingSettings.STRING_CHAR
							+ "a";
					break;
					case 7: arg += ScriptingSettings.ARGUMENT_END;
					break;
					}
					switch (arg2){
					case 0: arg += "";
					break;
					case 1: arg += ScriptingSettings.ARGUMENT_SEPARATOR
							+ "1";
					break;
					case 2: arg += ScriptingSettings.ARGUMENT_SEPARATOR
							+ "a";
					break;
					case 3: arg += ScriptingSettings.ARGUMENT_SEPARATOR
							+ ""
							+ ScriptingSettings.STRING_CHAR
							+ "1"
							+ ScriptingSettings.STRING_CHAR;
					break;
					case 4: arg += ScriptingSettings.ARGUMENT_SEPARATOR
							+ ""
							+ ScriptingSettings.STRING_CHAR
							+ "a"
							+ ScriptingSettings.STRING_CHAR;
					break;
					case 5: arg += ScriptingSettings.ARGUMENT_SEPARATOR;
					break;
					case 6: arg += ScriptingSettings.ARGUMENT_SEPARATOR
							+ ""
							+ ScriptingSettings.STRING_CHAR
							+ "a";
					break;
					case 7: arg += ScriptingSettings.ARGUMENT_SEPARATOR
							+ ""
							+ ScriptingSettings.ARGUMENT_END;
					break;
					}

					switch (arg3){
					case 0: arg += "";
					break;
					case 1: arg += ScriptingSettings.ARGUMENT_SEPARATOR
							+ "1";
					break;
					case 2: arg += ScriptingSettings.ARGUMENT_SEPARATOR
							+ "a";
					break;
					case 3: arg += ScriptingSettings.ARGUMENT_SEPARATOR
							+ ""
							+ ScriptingSettings.STRING_CHAR
							+ "1"
							+ ScriptingSettings.STRING_CHAR;
					break;
					case 4: arg += ScriptingSettings.ARGUMENT_SEPARATOR
							+ ""
							+ ScriptingSettings.STRING_CHAR
							+ "a"
							+ ScriptingSettings.STRING_CHAR;
					break;
					case 5: arg += ScriptingSettings.ARGUMENT_SEPARATOR;
					break;
					case 6: arg += ScriptingSettings.ARGUMENT_SEPARATOR
							+ ""
							+ ScriptingSettings.STRING_CHAR
							+ "a";
					break;
					case 7: arg += ScriptingSettings.ARGUMENT_SEPARATOR
							+ ""
							+ ScriptingSettings.ARGUMENT_END;
					break;
					}

					toCheck = "";
					toCheck += "testArguments";
					toCheck += ScriptingSettings.METHOD_INDICATOR;
					toCheck += "tester";
					toCheck += ScriptingSettings.ARGUMENT_BEGIN;
					toCheck += "";
					toCheck += arg;
					toCheck += "";
					toCheck += ScriptingSettings.ARGUMENT_END;
					toCheck += "";
					toCheck += ScriptingSettings.TRAILING_CHAR;

					if (arg1 >= errorPoint
							|| arg2 >= errorPoint
							|| arg3 >= errorPoint){
						System.out.println(arg1+" "+arg2+" "+arg3+" "+toCheck);
						assertFalse(toCheck,
								SyntaxChecker.isValidCommand(toCheck));
					}
					else{
						assertTrue(toCheck,
								SyntaxChecker.isValidCommand(toCheck));
					}


				}
			}
		}

	}

}
