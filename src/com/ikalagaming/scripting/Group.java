package com.ikalagaming.scripting;

import java.util.LinkedList;


/**
 * Contains one or more types of data. May contain strings, operators,
 * or arguments.
 *
 * @author Ches Burks
 *
 */
public class Group extends CommandElement{
	/**
	 * The list of elements this group contains
	 */
	private LinkedList<CommandElement> contents =
			new LinkedList<CommandElement>();

	/**
	 * Returns the current contents of this group.
	 * This may or may not be empty.
	 *
	 * @return this group's contents
	 */
	public LinkedList<CommandElement> getContents(){
		return contents;
	}

	/**
	 * Returns true if this group has no member elements.
	 *
	 * @return true if the group is empty, false if it contains elements
	 */
	public boolean isEmpty(){
		return contents.isEmpty();
	}

	/**
	 * Adds the given item to the end of the list.
	 *
	 * @param toAdd the element to add
	 */
	public void addItem(CommandElement toAdd){
		contents.add(toAdd);
	}

	/**
	 * Returns true if this is a valid group and follows the syntax rules
	 * that apply to it.
	 *
	 * @return true if this is valid
	 */
	@Override
	public boolean isValid(){
		boolean valid = true;
		for (CommandElement element : contents){
			//if one is not valid then this is not valid
			valid = valid && element.isValid();
		}
		return valid;
	}

	/**
	 * Returns this element in a string format. This prints all of the elements
	 * out in a string with no separation.
	 * @return this as a string
	 */
	@Override
	public String toString(){
		String output = "";
		for (CommandElement element : contents){
			output += element.toString();
		}
		return output;
	}
}
