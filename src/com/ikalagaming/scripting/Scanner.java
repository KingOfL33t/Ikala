package com.ikalagaming.scripting;

import java.util.LinkedList;

public class Scanner {

	/**
	 * Returns true if the given string is an operator (that is, a character
	 * used by the scripting language such as a semicolon or comma).
	 *
	 * @param c the string to check
	 * @return true if it is an operator, false otherwise
	 */
	private static boolean isOperator(String c){
		if (c == ScriptingSettings.ARGUMENT_BEGIN){
			return true;
		}
		else if (c == ScriptingSettings.ARGUMENT_END){
			return true;
		}
		else if (c == ScriptingSettings.ARGUMENT_SEPARATOR){
			return true;
		}
		else if (c == ScriptingSettings.COMMENT_CHAR){
			return true;
		}
		else if (c == ScriptingSettings.COMPILER_CHAR){
			return true;
		}
		else if (c == ScriptingSettings.METHOD_INDICATOR){
			return true;
		}
		else if (c == ScriptingSettings.STRING_INDICATOR){
			return true;
		}
		else if (c == ScriptingSettings.TRAILING_CHAR){
			return true;
		}
		return false;
	}

	private static ScanState getTypeOfChar(char c){
		if (ScriptingSettings.ARGUMENT_BEGIN.contains(c+"")){
			return ScanState.OPERATOR;
		}
		else if (ScriptingSettings.ARGUMENT_END.contains(c+"")){
			return ScanState.OPERATOR;
		}
		else if (ScriptingSettings.ARGUMENT_SEPARATOR.contains(c+"")){
			return ScanState.OPERATOR;
		}
		else if (ScriptingSettings.COMMENT_CHAR.contains(c+"")){
			return ScanState.OPERATOR;
		}
		else if (ScriptingSettings.COMPILER_CHAR.contains(c+"")){
			return ScanState.OPERATOR;
		}
		else if (ScriptingSettings.METHOD_INDICATOR.contains(c+"")){
			return ScanState.OPERATOR;
		}
		else if (ScriptingSettings.STRING_INDICATOR.contains(c+"")){
			return ScanState.OPERATOR;
		}
		else if (ScriptingSettings.TRAILING_CHAR.contains(c+"")){
			return ScanState.OPERATOR;
		}
		if (Character.isDigit(c)){
			return ScanState.NUMBER;
		}
		if (Character.isLetter(c)){
			return ScanState.STRING;
		}
		if (Character.isWhitespace(c)){
			return ScanState.WHITESPACE;
		}
		return ScanState.STRING;
	}

	public LinkedList<ScriptToken> parseString(String data){
		int i = 0;
		String buffer = "";
		LinkedList<ScriptToken> sections
		= new LinkedList<ScriptToken>();

		for (i = 0; i < data.length(); ++i){
			buffer += data.charAt(i);
			//TODO fix this section of code to scan in tokens correctly
			if (isOperator(data)){
				sections.add(new Operator(data));
				buffer = "";
			}
			else{
				sections.add(new CommandString(data));
				buffer = "";
			}
		}
		return sections;
	}
}
