package com.ikalagaming.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.ActionMap;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;

import com.ikalagaming.core.Localization;
import com.ikalagaming.core.Package;
import com.ikalagaming.core.PackageManager;
import com.ikalagaming.core.ResourceLocation;
import com.ikalagaming.core.events.CommandFired;
import com.ikalagaming.core.events.PackageEvent;
import com.ikalagaming.event.EventHandler;
import com.ikalagaming.event.EventManager;
import com.ikalagaming.event.Listener;
import com.ikalagaming.logging.ErrorCode;
import com.ikalagaming.logging.LoggingLevel;

/**
 * A simple console.
 *
 * @author Ches Burks
 *
 */
public class Console extends WindowAdapter implements Package, Listener{
	private ResourceBundle resourceBundle;
	private String windowTitle;
	private int width = 680;
	private int height = 350;
	private int maxLineCount = 150;
	private Color background = new Color(2,3,2);
	private Color foreground = new Color(2,200,2);

	private JFrame frame;
	private JTextArea textArea;

	private int cursorX = 0;//delta from left of screen
	private int cursorY = 0;//delta from last line
	private int posInString = 0;//where the cursor is in the string
	private int charWidth = 80;//how many characters per line
	private int charHeight = 25;//lines per window
	private char inputIndicator = '>';
	private String currentLine = "";
	private int currentIndicatorLine = 0;

	private PackageManager packageManager;
	private String packageName = "console";
	private boolean enabled = false;
	private final double version = 0.1;

	private void init(){
		frame = new JFrame(windowTitle);
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setBackground(background);
		frame.setForeground(foreground);


		textArea = new JTextArea();
		textArea.setFont(new Font("monospaced", Font.PLAIN, 12));
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setBackground(background);
		textArea.setForeground(foreground);
		textArea.setCaret(new MyCaret());
		MyCaret caret = (MyCaret)textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		caret.setBlinkRate(500);
		caret.setVisible(true);
		textArea.setCaretColor(foreground);

		//unbind caret bindings
		ActionMap am = textArea.getActionMap();
		am.get("caret-down").setEnabled(false);
		am.get("caret-up").setEnabled(false);
		am.get("selection-up").setEnabled(false);//shift pressed UP
		am.get("caret-next-word").setEnabled(false);//ctrl pressed RIGHT
		am.get("selection-previous-word").setEnabled(false);//shift ctrl pressed LEFT
		am.get("selection-up").setEnabled(false);//shift pressed KP_UP
		am.get("caret-down").setEnabled(false);//pressed DOWN
		am.get("caret-previous-word").setEnabled(false);//ctrl pressed LEFT
		am.get("caret-end-line").setEnabled(false);//pressed END
		am.get("selection-page-up").setEnabled(false);//shift pressed PAGE_UP
		am.get("caret-up").setEnabled(false);//pressed KP_UP
		am.get("delete-next").setEnabled(false);//pressed DELETE
		am.get("caret-begin").setEnabled(false);//ctrl pressed HOME
		am.get("selection-backward").setEnabled(false);//shift pressed LEFT
		am.get("caret-end").setEnabled(false);//ctrl pressed END
		am.get("delete-previous").setEnabled(false);//pressed BACK_SPACE
		am.get("selection-next-word").setEnabled(false);//shift ctrl pressed RIGHT
		am.get("caret-backward").setEnabled(false);//pressed LEFT
		am.get("caret-backward").setEnabled(false);//pressed KP_LEFT
		am.get("selection-forward").setEnabled(false);//shift pressed KP_RIGHT
		am.get("delete-previous").setEnabled(false);//ctrl pressed H
		am.get("unselect").setEnabled(false);//ctrl pressed BACK_SLASH
		am.get("insert-break").setEnabled(false);//pressed ENTER
		am.get("selection-begin-line").setEnabled(false);//shift pressed HOME
		am.get("caret-forward").setEnabled(false);//pressed RIGHT
		am.get("selection-page-left").setEnabled(false);//shift ctrl pressed PAGE_UP
		am.get("selection-down").setEnabled(false);//shift pressed DOWN
		am.get("page-down").setEnabled(false);//pressed PAGE_DOWN
		am.get("delete-previous-word").setEnabled(false);//ctrl pressed BACK_SPACE
		am.get("delete-next-word").setEnabled(false);//ctrl pressed DELETE
		am.get("selection-backward").setEnabled(false);//shift pressed KP_LEFT
		am.get("selection-page-right").setEnabled(false);//shift ctrl pressed PAGE_DOWN
		am.get("caret-next-word").setEnabled(false);//ctrl pressed KP_RIGHT
		am.get("selection-end-line").setEnabled(false);//shift pressed END
		am.get("caret-previous-word").setEnabled(false);//ctrl pressed KP_LEFT
		am.get("caret-begin-line").setEnabled(false);//pressed HOME
		am.get("caret-down").setEnabled(false);//pressed KP_DOWN
		am.get("selection-forward").setEnabled(false);//shift pressed RIGHT
		am.get("selection-end").setEnabled(false);//shift ctrl pressed END
		am.get("selection-previous-word").setEnabled(false);//shift ctrl pressed KP_LEFT
		am.get("selection-down").setEnabled(false);//shift pressed KP_DOWN
		am.get("insert-tab").setEnabled(false);//pressed TAB
		am.get("caret-up").setEnabled(false);//pressed UP
		am.get("selection-begin").setEnabled(false);//shift ctrl pressed HOME
		am.get("selection-page-down").setEnabled(false);//shift pressed PAGE_DOWN
		am.get("delete-previous").setEnabled(false);//shift pressed BACK_SPACE
		am.get("caret-forward").setEnabled(false);//pressed KP_RIGHT
		am.get("selection-next-word").setEnabled(false);//shift ctrl pressed KP_RIGHT
		am.get("page-up").setEnabled(false);//pressed PAGE_UP

		textArea.addKeyListener(new ConsoleKeyListener());

		frame.getContentPane().add(new JScrollPane(textArea));

		frame.setVisible(true);
	}

	/**
	 * Attempts to execute the current line of input. If none exists, it does
	 * nothing.
	 */
	private void runLine(){
		String line = currentLine;
		clearCurrentText();
		newLine();

		String firstWord = line.trim().split("\\s+")[0];

		if (!packageManager.getCommandRegistry().contains(firstWord)){
			appendMessage(resourceBundle.getString("unknown_command")
					+" "+ firstWord);
		}
		if (packageManager.isLoaded("event-manager")){
			EventManager mgr =
					(EventManager) packageManager.getPackage("event-manager");

			Package pack =
					packageManager.getCommandRegistry().getParent(firstWord);
			if (pack != null){
				mgr.fireEvent(new CommandFired(pack.getType(), line));
			}
		}
	}

	/**
	 * Appends the input indicator char to the console
	 */
	private void appendIndicatorChar(){
		textArea.append(""+inputIndicator);
		++cursorX;
		moveRight();
	}
	/**
	 * Appends the input indicator char to the console
	 */
	private void removeIndicatorChar(){
		int offset = getSafeLineStartOffset(currentIndicatorLine);
		textArea.replaceRange("", offset, offset+1);
	}

	/**
	 * Clears out the text on the current line(s). Everything after
	 * the indicator char until the end of the current string (end of
	 * the console) will be removed.
	 */
	private void clearCurrentText(){
		int start;
		//fetch the index of the last line of text
		start = getSafeLineStartOffset(currentIndicatorLine);
		//add one to account for the input indicator char
		++start;
		textArea.replaceRange("", start, start+currentLine.length());
		posInString = 0;
		cursorY = 0;
		cursorX = 0;
		currentLine = "";
		validatePositions();
		updateCaretPosition();
	}

	/**
	 * Moves the cursor to the next line, then shows the line indicator char.
	 */
	private void newLine(){
		textArea.append(System.lineSeparator());
		posInString = 0;
		currentLine = "";
		if (textArea.getLineCount() < charHeight){
			++cursorY;
		}
		cursorX = 0;
		cursorY = 0;
		updateInputLine();
		while (textArea.getLineCount() > maxLineCount){
			removeTopLine();
		}
	}

	private void updateInputLine(){
		currentIndicatorLine = textArea.getLineCount() - 1;
		appendIndicatorChar();
		validatePositions();
		updateCaretPosition();
	}

	/**
	 * Adds a char to the end of the current string and console line
	 * @param c the char to add
	 */
	private void addChar(char c){
		if (cursorY > 0){
			textArea.insert(System.lineSeparator(),
					getSafeLineStartOffset(currentIndicatorLine + cursorY)

					+ ((posInString+1)%charWidth)
					+ 1);
		}
		textArea.insert(""+c, getSafeLineStartOffset(currentIndicatorLine)
				+ (posInString+1)%charWidth);
		//how many lines the current line takes up
		currentLine = currentLine.substring(0, posInString) +
				c +
				currentLine.substring(posInString);
		moveRight();
	}

	/**
	 * Removes a char from the end of the current string and console line
	 */
	private void delChar(){
		if (cursorY == 0){
			if (cursorX <= 1){
				return;
			}
		}
		int pos = getSafeLineStartOffset(currentIndicatorLine)
				+ cursorY
				+ ((posInString+1)%charWidth);
		textArea.replaceRange("", pos-1, pos);

		currentLine = currentLine.substring(0, posInString-1) +
				currentLine.substring(posInString);
		moveLeft();
	}

	/**
	 * Checks that the cursor and string positions are
	 * valid, and fixes them if they are not.
	 */
	private void validatePositions(){
		if (cursorX < 0){
			cursorX = 0;
		}
		if (cursorY < 0){
			cursorY = 0;
		}
		if (cursorX > charWidth){
			cursorX = charWidth;
		}
		if (posInString < 0){
			posInString = 0;
		}
		if (posInString > currentLine.length()){
			posInString = currentLine.length();
		}
	}

	/**
	 * Moves the caret to the correct position.
	 */
	private void updateCaretPosition(){
		int position = getSafeLineStartOffset(
				currentIndicatorLine + cursorY)
				+ cursorX;
		if (position >= textArea.getText().length()){
			position = textArea.getText().length();
		}
		textArea.setCaretPosition(position);
	}

	private void moveLeft(){
		if (cursorY == 0){
			if (cursorX <= 1){
				updateCaretPosition();
				return;
			}
			else {
				--cursorX;
				--posInString;
			}
		}
		else if (cursorY > 0){
			//on an additional line
			if (cursorX > 0){
				--cursorX;
				--posInString;
			}
			else if (cursorX <= 0){
				cursorX = charWidth - 1;
				--posInString;
				--cursorY;
			}
		}
		else {
			//on the same line as the indicator
			if (cursorX > 1){
				--cursorX;
				--posInString;
			}
		}
		validatePositions();
		updateCaretPosition();

	}

	private void moveRight(){
		if (currentLine.length() <= 0){
			validatePositions();
			updateCaretPosition();
			return;//do not do anything
		}
		if (posInString >= currentLine.length()){
			validatePositions();
			updateCaretPosition();
			return;//do not do anything
		}
		if (cursorX < 0){
			posInString = 0;
			cursorX = 0;
			validatePositions();
			updateCaretPosition();
			return;//do not do anything
		}
		else if (cursorX >= 0 && cursorX < charWidth){
			++cursorX;
			++posInString;
			validatePositions();
			updateCaretPosition();
		}
		else if (cursorX >= charWidth){
			cursorX = 0;
			++cursorY;
			++posInString;
			validatePositions();
			updateCaretPosition();
		}
	}


	/**
	 * Returns the lineStartOffset of the given line and handles errors.
	 * @param line the line to find
	 * @return the offset of the start of the line
	 */
	private int getSafeLineStartOffset(int line){
		try {
			return textArea.getLineStartOffset(line);
		} catch (BadLocationException e) {
			packageManager.getLogger().logError(
					ErrorCode.EXCEPTION,
					LoggingLevel.WARNING, "Console.getSafeLineOffset(String)");
		}
		return -1;
	}

	private class ConsoleKeyListener extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent event) {

			int keyCode = event.getKeyCode();
			switch (keyCode){
			case KeyEvent.VK_LEFT:
				moveLeft();
				break;
			case KeyEvent.VK_RIGHT:
				moveRight();
				break;
			case KeyEvent.VK_UP:

				break;
			case KeyEvent.VK_DOWN:

				break;
			case KeyEvent.VK_ENTER:
				runLine();
				break;
			case KeyEvent.VK_BACK_SPACE:
				delChar();
				break;
			default:
				addChar(event.getKeyChar());
				break;
			}
		}

		@Override
		public void keyReleased(KeyEvent event) {
		}
	}

	/**
	 * Returns window width.
	 * This is the width of the frame the console is in.
	 *
	 * @return the width of the frame
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets the frame width.
	 * This is the width of the frame the console is in.
	 *
	 * @param width The new width
	 */
	public void setWidth(int width) {
		this.width = width;
		frame.setSize(width, frame.getHeight());
	}

	/**
	 * Returns the window height.
	 * This is the height of the frame the console is in.
	 *
	 * @return the height of the frame
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Sets the frame height.
	 * This is the height of the frame the console is in.
	 *
	 * @param height The new height
	 */
	public void setHeight(int height) {
		this.height = height;
		frame.setSize(frame.getWidth(), height);
	}

	/**
	 * Returns the maximum number of lines that are
	 * stored in the window.
	 *
	 * @return the max number of lines
	 */
	public int getMaxLineCount() {
		return maxLineCount;
	}

	/**
	 * Sets the maximum number of lines stored in the window.
	 *
	 * @param maxLineCount the maximum number of lines to store
	 */
	public void setMaxLineCount(int maxLineCount) {
		this.maxLineCount = maxLineCount;
	}

	/**
	 * Returns the window title.
	 *
	 * @return the String that is used as the title
	 */
	public String getWindowTitle() {
		return windowTitle;
	}

	/**
	 * Sets the title of the window.
	 *
	 * @param windowTitle the String to use as the title
	 */
	public void setWindowTitle(String windowTitle) {
		this.windowTitle = windowTitle;
		frame.setTitle(windowTitle);
	}

	/**
	 * Adds a String to the bottom of the console.
	 * Removes the top lines if/while they exceed the maximum line count.
	 *
	 * @param message The message to append
	 */
	public void appendMessage(String message){
		String curLine = currentLine;
		int x = cursorX;
		int y = cursorY;
		int p = posInString;
		clearCurrentText();
		removeIndicatorChar();
		textArea.append(message+System.lineSeparator());
		updateInputLine();
		textArea.append(curLine);
		currentLine = curLine;
		cursorX = x;
		cursorY = y;
		posInString = p;
		validatePositions();
		updateCaretPosition();
		while (textArea.getLineCount() > maxLineCount){
			removeTopLine();
		}
	}

	/**
	 * Removes the top line of the input.
	 */
	private void removeTopLine(){
		int end;
		try {
			end = textArea.getLineEndOffset(0);
			textArea.replaceRange("", 0, end);
		} catch (BadLocationException e) {
			packageManager.getLogger().logError(
					ErrorCode.EXCEPTION,
					LoggingLevel.WARNING, "Console.removeTopLine(String)");
		}
	}

	@Override
	public boolean disable() {
		onDisable();
		enabled = false;
		return true;
	}

	@Override
	public boolean enable() {
		onEnable();
		enabled = true;
		return true;
	}

	@Override
	public String getType() {
		return packageName;
	}

	@Override
	public double getVersion() {
		return version;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void onDisable() {
		frame.setVisible(false);
		frame.dispose();

	}

	@Override
	public void onEnable() {
		init();
		appendIndicatorChar();
	}

	@Override
	public void onLoad() {
		try {
			resourceBundle = ResourceBundle.getBundle(ResourceLocation.Console,
					Localization.getLocale());
		}
		catch (MissingResourceException missingResource){
			packageManager.getLogger().logError(ErrorCode.LOCALE_NOT_FOUND,
					LoggingLevel.WARNING, "Console.onLoad()");
		}
		try{
			windowTitle = resourceBundle.getString("title");
		}
		catch (MissingResourceException missingResource){
			packageManager.getLogger().logError(ErrorCode.LOCALE_NOT_FOUND,
					LoggingLevel.WARNING, "Console.onLoad()");
		}
		catch (ClassCastException classCast){
			packageManager.getLogger().logError(
					ErrorCode.LOCALE_RESOURCE_WRONG_TYPE,
					LoggingLevel.WARNING, "Console.onLoad()");
		}
	}

	@Override
	public void onUnload() {
		resourceBundle = null;
	}

	@Override
	public boolean reload() {
		if(!disable()){
			//failed to disable
			return false;
		}
		if (!enable()){
			//failed to enable
			return false;
		}
		return true;
	}

	@Override
	public void setPackageManager(PackageManager parent) {
		this.packageManager = parent;
	}

	@Override
	public PackageManager getPackageManager() {
		return this.packageManager;
	}


	/**
	 * Called when a command event is sent.
	 * @param event the command sent
	 */
	@EventHandler
	public void onCommand(CommandFired event){
		appendMessage("got cmd " + event.getMessage());
		if (!event.getTo().equalsIgnoreCase(packageName)){
			return;
		}

	}
	/**
	 * Called when a package event is sent out by the event system.
	 * @param event the event that was fired
	 */
	@EventHandler
	public void onPackageEvent(PackageEvent event){
		if (event.getTo() != packageName){
			return;
		}
		String callMethod = "call";
		String onLoad = "onLoad";
		String onUnload = "onUnload";
		String enable = "enable";
		String disable = "disable";


		try{
			ResourceBundle packageBundle;
			packageBundle = packageManager.getResourceBundle();
			callMethod = packageBundle.getString("CMD_CALL");
			onLoad = packageBundle.getString("ARG_ON_LOAD");
			onUnload = packageBundle.getString("ARG_ON_UNLOAD");
			enable = packageBundle.getString("ARG_ENABLE");
			disable = packageBundle.getString("ARG_DISABLE");
		}
		catch (MissingResourceException missingResource){
			packageManager.getLogger().logError(
					ErrorCode.LOCALE_RESOURCE_NOT_FOUND,
					LoggingLevel.WARNING,
					"PackageManager.loadPackage(Package) load");
		}
		catch (ClassCastException classCast){
			packageManager.getLogger().logError(
					ErrorCode.LOCALE_RESOURCE_WRONG_TYPE,
					LoggingLevel.WARNING,
					"PackageManager.loadPackage(Package) load");
		}

		if (event.getMessage().startsWith(callMethod)){
			String trimmed = event.getMessage().replaceFirst(callMethod, "");
			trimmed = trimmed.replaceFirst(" ", "");
			if (trimmed.startsWith(onLoad)){
				onLoad();
			}
			else if (trimmed.startsWith(onUnload)){
				onUnload();
			}
			else if (trimmed.startsWith(enable)){
				enable();
			}
			else if (trimmed.startsWith(disable)){
				disable();
			}
		}
	}

}
