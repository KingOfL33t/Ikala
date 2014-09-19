package com.ikalagaming.scripting;

import java.util.LinkedList;

/**
 * Provides methods for checking syntax of scripting commands to ensure they
 * are valid.
 *
 * @author Ches Burks
 *
 */
public class SyntaxChecker {

	/**
	 * Checks that the command has a valid format.
	 * parentheses
	 * @param command the command to check
	 * @return true if the command is valid, false if it has syntax errors
	 */
	public static boolean isValidCommand(String command){

		//easy checks
		if (command == null){
			return false;//cant be null and also valid
		}
		if (command.isEmpty()){
			return false;//no command to check
		}

		LinkedList<String> cmdStack = new LinkedList<String>();
		String trimmedCmd = command.trim();
		ScanState currentState = ScanState.NOT_STARTED;
		int index = 0;
		String charBuffer = "";
		char c = ' ';
		boolean repeatFlag = false;

		//basic syntax checks

		//commands does not have a trailing char
		if (!trimmedCmd.endsWith(""+ScriptingSettings.TRAILING_CHAR)){
			return false;
		}
		//does not have the method indicator
		if (!trimmedCmd.contains(""+ScriptingSettings.METHOD_INDICATOR)){
			return false;
		}

		//check for mismatched parentheses
		for (int i = 0; i < trimmedCmd.length(); ++i){
			if (trimmedCmd.charAt(i) == ScriptingSettings.ARGUMENT_BEGIN){
				cmdStack.push(""+ScriptingSettings.ARGUMENT_BEGIN);
			}
			else if (trimmedCmd.charAt(i) == ScriptingSettings.ARGUMENT_END){
				if (cmdStack.peek() == null){
					//there was no open parentheses before the closed
					return false;
				}
				else{
					cmdStack.pop();//remove the matching open parentheses
				}
			}
		}
		if (cmdStack.peek() != null){
			//there were extra open parentheses
			return false;
		}

		//check for unpaired quotes
		for (int i = 0; i < trimmedCmd.length(); ++i){
			if (trimmedCmd.charAt(i) == ScriptingSettings.STRING_CHAR){
				if (cmdStack.peek() == null){
					//if there is not a quote in the stack
					//add one
					cmdStack.push(""+ScriptingSettings.STRING_CHAR);
				}
				else{
					//if there is a quote in the stack, pop it off
					cmdStack.pop();
				}
			}
		}
		if (cmdStack.peek() != null){
			//there was an unclosed quote
			return false;
		}

		/*
		 * used to test if there are multiple semicolons
		 * true if during the specific test its inside a string
		 */
		boolean semicolonStringFlag = false;
		//check for extra semicolons
		for (int i = 0; i < trimmedCmd.length(); ++i){
			if (trimmedCmd.charAt(i) == ScriptingSettings.STRING_CHAR){
				//toggle the flag
				semicolonStringFlag = !semicolonStringFlag;
			}
			else if (trimmedCmd.charAt(i) == ScriptingSettings.TRAILING_CHAR){
				if (!semicolonStringFlag){
					//its inside a string
					if (i != trimmedCmd.length()-1){
						/*
						 * this should remove any semicolons in the command
						 * that are not inside a string
						 */
						return false;
					}
				}
			}
		}

		while (currentState != ScanState.FINISHED){
			c = trimmedCmd.charAt(index);
			++index;

			if (currentState == ScanState.NOT_STARTED){
				//the first char
				if (c == ScriptingSettings.ARGUMENT_BEGIN){
					return false;
				}
				else if (c == ScriptingSettings.ARGUMENT_END){
					return false;
				}
				else if (c == ScriptingSettings.ARGUMENT_SEPARATOR){
					return false;
				}
				else if (c == ScriptingSettings.COMMENT_CHAR){
					return false;
				}
				else if (c == ScriptingSettings.COMPILER_CHAR){
					return false;
				}
				else if (c == ScriptingSettings.METHOD_INDICATOR){
					return false;
				}
				else if (c == ScriptingSettings.STRING_CHAR){
					return false;
				}
				else if (c == ScriptingSettings.TRAILING_CHAR){
					return false;
				}
				//at this point it does not begin with a special character
				charBuffer += c;
				currentState = ScanState.NODE_NAME;
				continue;//we don't want to do anything else in the loop
			}
			else if (currentState == ScanState.NODE_NAME){
				if (c == ScriptingSettings.ARGUMENT_BEGIN){
					return false;
				}
				else if (c == ScriptingSettings.COMMENT_CHAR){
					return false;
				}
				else if (c == ScriptingSettings.COMPILER_CHAR){
					return false;
				}
				else if (c == ScriptingSettings.STRING_CHAR){
					return false;
				}
				else if (c == ScriptingSettings.ARGUMENT_SEPARATOR){
					if (index >= 2 &&
							trimmedCmd.replace(" ", "").charAt(index-2) ==
							ScriptingSettings.ARGUMENT_END){
						if (index == trimmedCmd.length()-1){
							//just before the end
							return false;
						}
						/*
						 * still inside arguments but the character before
						 * the comma is a closed parentheses
						 * (ignoring whitespace)
						 */
						continue;
					}
					else if (index == trimmedCmd.length()-1){
						//just before the end
						return false;
					}
					else{
						return false;
					}
				}
				else if (c == ScriptingSettings.TRAILING_CHAR){
					if (index == trimmedCmd.length()){
						//its the final character of the string
						currentState = ScanState.FINISHED;
						//its done
						break;
					}
					else{
						//its somewhere other than at the end
						return false;
					}
				}
				else if (c == ScriptingSettings.ARGUMENT_END){
					continue;
				}
				else if (c == ' '){
					return false;//no spaces
				}
				//should be a valid char at this point
				else if (c == ScriptingSettings.METHOD_INDICATOR){
					cmdStack.push(charBuffer);
					charBuffer = "";//reset the buffer
					repeatFlag = true;
					currentState = ScanState.METHOD_NAME;
					//now looking for a method name
					continue;//don't do anything else in this iteration
				}
				//it is a normal char
				charBuffer += c;
			}
			else if (currentState == ScanState.METHOD_NAME){
				if (c == ScriptingSettings.ARGUMENT_END){
					return false;
				}
				else if (c == ScriptingSettings.ARGUMENT_SEPARATOR){
					return false;
				}
				else if (c == ScriptingSettings.COMMENT_CHAR){
					return false;
				}
				else if (c == ScriptingSettings.COMPILER_CHAR){
					return false;
				}
				else if (c == ScriptingSettings.STRING_CHAR){
					return false;
				}
				else if (c == ScriptingSettings.TRAILING_CHAR){
					return false;
				}
				else if (c == ScriptingSettings.METHOD_INDICATOR){
					if (repeatFlag){
						return false;
						//there are more than one periods
					}
					else if (trimmedCmd.charAt(index) ==
							ScriptingSettings.ARGUMENT_BEGIN){
						//edge case where the method ends in a period
						return false;
					}
					else{
						repeatFlag = true;
					}
					continue;//should be here, skip to the next char
				}
				else if (c == ' '){
					return false;//no spaces
				}
				else if (c == ScriptingSettings.ARGUMENT_BEGIN){
					cmdStack.push(charBuffer);
					charBuffer = "";
					currentState = ScanState.METHOD_ARGS;
					//now reading arguments
					repeatFlag = false;//reset the warning for multiple chars
					continue;
				}
				repeatFlag = false;
				//its a normal char
				charBuffer += c;
			}
			else if (currentState == ScanState.METHOD_ARGS){
				if (c == ScriptingSettings.COMMENT_CHAR){
					if (!charBuffer.startsWith(""
							+ ScriptingSettings.STRING_CHAR)){
						//its not inside a string
						return false;
					}
				}
				else if (c == ScriptingSettings.COMPILER_CHAR){
					if (!charBuffer.startsWith(""
							+ ScriptingSettings.STRING_CHAR)){
						//its not inside a string
						return false;
					}
				}
				else if (c == ScriptingSettings.METHOD_INDICATOR){
					if (!charBuffer.startsWith(""
							+ ScriptingSettings.STRING_CHAR)){
						//its not inside a string
						return false;
					}
				}
				else if (c == ScriptingSettings.TRAILING_CHAR){
					if (!charBuffer.startsWith(""
							+ ScriptingSettings.STRING_CHAR)){
						//its not inside a string
						return false;
					}
				}
				else if (c == ' '){
					if (!charBuffer.startsWith(""
							+ ScriptingSettings.STRING_CHAR)){
						//its not inside a string
						continue;//remove the spaces in the arguments
					}
				}
				else if (c == ScriptingSettings.ARGUMENT_BEGIN){
					if (charBuffer.startsWith(""
							+ ScriptingSettings.STRING_CHAR)){
						//its inside a string
						charBuffer += c;
					}
					/*
					 * There could be more than one parentheses and still
					 * be a valid command so dont worry about the repeatFlag
					 * here.
					 */
					continue;//should be here, skip to the next char
				}
				else if (c == ScriptingSettings.ARGUMENT_SEPARATOR){
					if (charBuffer.startsWith(""
							+ ScriptingSettings.STRING_CHAR)){
						//its inside a string
						charBuffer += c;
					}
					else{
						//not in a string
						if (index >= 2 && trimmedCmd.charAt(index-2) ==
								ScriptingSettings.ARGUMENT_BEGIN){
							//the arguments start with a comma
							return false;
						}
						else if (trimmedCmd.charAt(index) ==
								ScriptingSettings.ARGUMENT_END){
							//ends with a comma
							System.out.println(trimmedCmd);
							return false;
						}
						if (repeatFlag){
							return false;//more than one comma
						}
						cmdStack.push(charBuffer);
						charBuffer = "";
						repeatFlag = true;//reset the warning for multiple chars
					}
					continue;
				}
				else if (c == ScriptingSettings.ARGUMENT_END){
					if (charBuffer.startsWith(""
							+ ScriptingSettings.STRING_CHAR)){
						//its inside a string
						charBuffer += c;
					}
					else{
						//not in a string, move back to node level
						cmdStack.push(charBuffer);
						charBuffer = "";
						//back to reading the node name
						currentState = ScanState.NODE_NAME;
						repeatFlag = false;//resets the flag from a comma
					}
					continue;
				}
				else if (c == ScriptingSettings.STRING_CHAR){
					charBuffer += c;
					if (charBuffer.startsWith(""
							+ ScriptingSettings.STRING_CHAR)){
						//its a completed string
						cmdStack.push(charBuffer);
						charBuffer = "";
					}

					//its still reading a string so continue
					repeatFlag = false;//resets the flag from a comma
					continue;
				}
				charBuffer += c;
				repeatFlag = false;//resets the flag from a comma

			}
			//TODO closing chars cases to handle nested methods
		}

		return true;
	}

	/**
	 * Returns true if the given char is an operator (that is, a character
	 * used by the scripting language such as a semicolon or comma).
	 *
	 * @param c the char to check
	 * @return true if it is an operator, false otherwise
	 */
	private boolean isOperator(char c){
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
		else if (c == ScriptingSettings.STRING_CHAR){
			return true;
		}
		else if (c == ScriptingSettings.TRAILING_CHAR){
			return true;
		}

		return false;
	}

	/**
	 * Takes the given command and creates a command object.
	 * @deprecated this will be moved to isValidCommand soon
	 *
	 * @param command
	 * @return
	 */
	public boolean isValidCmd(String command){
		//easy checks
		if (command == null){
			return false;//can't be null and also valid
		}
		if (command.isEmpty()){
			return false;//no command to check
		}


		String trimmed = command.trim();

		return true;
	}
}
