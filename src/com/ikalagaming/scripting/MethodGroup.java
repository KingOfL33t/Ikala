package com.ikalagaming.scripting;

import java.util.LinkedList;

/**
 * Contains a string representing the name of a node and a string representing
 * a function contained in that node, separated by an operator which indicates
 * the statement after the node is a function name.
 *
 * @author Ches Burks
 *
 */
public class MethodGroup extends Group{
	/**
	 * The list of elements this group contains
	 */
	private LinkedList<CommandElement> contents =
			new LinkedList<CommandElement>();

	/**
	 * Returns true if this is a valid group and follows the syntax rules
	 * that apply to it. Method groups must have a string, followed by
	 * a method indicator, followed by another string.
	 *
	 * @return true if this is valid
	 */
	@Override
	public boolean isValid(){
		if (contents.size() != 3){
			return false;//not three elements
		}
		if (!(contents.get(0).getType() == CmdComponentType.STRING
				&& contents.get(1).getType() == CmdComponentType.OPERATOR
				&& contents.get(2).getType() == CmdComponentType.STRING)){
			//does not have a valid layout
			return false;
		}
		else if (contents.get(1).toString() != "" +
				ScriptingSettings.METHOD_INDICATOR){
			return false;//wrong operator
		}
		return true;
	}

	/**
	 * Returns the {@link CmdComponentType type} of this element
	 * @return the type of element this is
	 */
	@Override
	public CmdComponentType getType(){
		return CmdComponentType.METHOD_GROUP;
	}
}
