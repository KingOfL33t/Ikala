
package com.ikalagaming.gui;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runners.Suite.SuiteClasses;

import com.ikalagaming.core.packages.PackageState;
import com.ikalagaming.gui.Console;

/**
 * Used for testing the Console class.
 * 
 * @author Ches Burks
 * 
 */
@SuiteClasses({Console.class})
public class ConsoleTest {

	/**
	 * Ensures the window can be properly resized
	 */
	@Test
	public void testResize() {
		Console tested = new Console();
		tested.onLoad();
		while (tested.getPackageState() == PackageState.LOADING){
			;
		}
		tested.enable();
		while (tested.getPackageState() == PackageState.ENABLING){
			;
		}
		tested.setHeight(100);
		tested.setWidth(100);
		tested.setHeight(400);
		tested.setWidth(50);
		tested.setHeight(50);
		tested.setWidth(400);
	}

	/**
	 * Ensures that getter methods do not return null values immediately after
	 * init
	 */
	@Test
	public void testGetters() {
		Console tested = new Console();
		tested.onLoad();
		while (tested.getPackageState() == PackageState.LOADING){
			;
		}
		tested.enable();
		while (tested.getPackageState() == PackageState.ENABLING){
			;
		}
		assertNotNull(tested.getHeight());
		assertNotNull(tested.getWidth());
		assertNotNull(tested.getMaxLineCount());
		assertNotNull(tested.getWindowTitle());
	}

	/**
	 * Appends a message to the console
	 */
	@Test
	public void testAppendingMessage() {
		Console tested = new Console();
		tested.onLoad();
		while (tested.getPackageState() == PackageState.LOADING){
			;
		}
		tested.enable();
		while (tested.getPackageState() == PackageState.ENABLING){
			;
		}
		tested.appendMessage("This is a test");
	}

	/**
	 * Tests the appending of more messages than the console is supposed to hold
	 * to ensure they are handled properly
	 */
	@Test
	public void testOverflowingMessages() {
		Console tested = new Console();
		tested.onLoad();
		while (tested.getPackageState() == PackageState.LOADING){
			;
		}
		tested.enable();
		while (tested.getPackageState() == PackageState.ENABLING){
			;
		}
		int i;
		int overflow = 10;// how many lines to add after the max
		for (i = 0; i < tested.getMaxLineCount() + overflow; i++) {
			tested.appendMessage("Test line " + i);
		}
	}
	
	/**
	 * Appends a message that is longer than one line
	 */
	@Test
	public void testLongMessage() {
		Console tested = new Console();
		tested.onLoad();
		while (tested.getPackageState() == PackageState.LOADING){
			;
		}
		tested.enable();
		while (tested.getPackageState() == PackageState.ENABLING){
			;
		}
		int length = 81;
		int i;
		String testInput = "";
		for (i = 0; i < length; ++i){
			testInput += "a";
		}
		tested.appendMessage(testInput);
	}

}
