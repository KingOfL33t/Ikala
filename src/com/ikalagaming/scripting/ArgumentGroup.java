package com.ikalagaming.scripting;

import java.util.LinkedList;

/**
 * Contains arguments and operators for use in functions.
 * This contains the operators for opening and closing the group
 * such as "(" and ")".
 *
 * @author Ches Burks
 *
 */
public class ArgumentGroup extends Group{
	/**
	 * The list of elements this group contains
	 */
	private LinkedList<ScriptToken> contents =
			new LinkedList<ScriptToken>();

	/**
	 * Returns true if this is a valid group and follows the syntax rules
	 * that apply to it. Method groups must have a string, followed by
	 * a method indicator, followed by another string.
	 *
	 * @return true if this is valid
	 */
	@Override
	public boolean isValid(){
		if (contents.size() == 2){
			if (contents.get(0).toString() == ScriptingSettings.ARGUMENT_BEGIN
					&& contents.get(1).toString() ==
					ScriptingSettings.ARGUMENT_END){
				return true;//its empty and therefore valid
			}
		}

		int i = 0;
		boolean lastWasAComma = false;
		int parenCount = 0;
		String tmp = "";
		for (i = 0; i < contents.size(); i++){
			if (!contents.get(i).isValid()){
				return false;//one of the elements is invalid
			}
			if (contents.get(i).getType() == TokenType.OPERATOR){
				tmp = contents.get(i).toString();
				if (tmp == ScriptingSettings.ARGUMENT_BEGIN){
					++parenCount;
				}
				else if (tmp == ScriptingSettings.ARGUMENT_END){
					--parenCount;
				}
				else if (tmp == ScriptingSettings.ARGUMENT_SEPARATOR){
					if (lastWasAComma){
						return false;//two commas in a row
					}
					lastWasAComma = true;
				}
				else{
					return false;//misc operator
				}
			}
			else if (contents.get(i).getType() == TokenType.ARGUMENT){
				if (lastWasAComma){
					lastWasAComma = false;
				}
				if (!contents.get(i).isValid()){
					return false;//invalid arg
				}
			}
		}
		if (parenCount != 0){
			//unmatched parentheses
			return false;
		}

		return true;
	}

	/**
	 * Returns the {@link TokenType type} of this element
	 * @return the type of element this is
	 */
	@Override
	public TokenType getType(){
		return TokenType.ARGUMENT_GROUP;
	}
}
