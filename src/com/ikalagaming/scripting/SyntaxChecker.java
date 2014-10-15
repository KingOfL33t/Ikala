package com.ikalagaming.scripting;

import java.util.LinkedList;

/**
 * Provides methods for checking syntax of scripting commands to ensure they
 * are valid.
 *s
 * @author Ches Burks
 *@deprecated dude use Lua
 */
public class SyntaxChecker {

	/**
	 * Takes the given command and creates a command object.
	 *
	 * @param command the command to use
	 * @return the function group created
	 */
	public static FunctionGroup splitIntoFunction(String command){
		command = command.trim();//remove leading and trailing whitespace
		int i = 0;
		int lastIndex = 0;//when the string opened

		LinkedList<ScriptToken> sections
		= new LinkedList<ScriptToken>();

		boolean insideString = false;
		String text = "";

		//TODO read with scanner
		System.out.println(sections);


		//strings get collapsed here
		for (i = 0; i < sections.size(); ++i){
			if (sections.get(i).getType() == TokenType.OPERATOR){
				if (sections.get(i).toString().contains(
						ScriptingSettings.STRING_INDICATOR)){

					if (insideString){
						text += sections.get(i).toString();
						insideString = false;
						sections.remove(i);
						--i;
						sections.add(lastIndex, new CommandString(text));

						text = "";
					}
					else{
						lastIndex = i;
						insideString = true;
					}
				}
			}
			if (insideString){
				text += sections.get(i).toString();
				sections.remove(i);
				--i;
			}
		}

		return new FunctionGroup();
	}
}
