package com.ikalagaming.scripting;

import java.util.LinkedList;

/**
 * A complete function. Contains at least one method reference, argument
 * group (which may not contain arguments), and ending operator.
 *
 * @author Ches Burks
 *
 */
public class FunctionGroup extends Group{
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
		if (contents.size() < 2 || contents.size() > 3){
			return false;//not three elements
		}
		if (!(contents.get(0).getType() == CmdComponentType.METHOD_GROUP
				&& contents.get(1).getType()
				== CmdComponentType.ARGUMENT_GROUP)){
			//does not have a valid layout
			return false;
		}
		for (CommandElement element : contents){
			if (!element.isValid()){
				return false;//one of the groups is not valid
			}
		}
		return true;
	}

	/**
	 * Returns the {@link CmdComponentType type} of this element
	 * @return the type of element this is
	 */
	@Override
	public CmdComponentType getType(){
		return CmdComponentType.FUNCTION;
	}
}
