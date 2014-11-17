
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
import com.ikalagaming.core.PackageManager;
import com.ikalagaming.core.ResourceLocation;
import com.ikalagaming.core.events.CommandFired;
import com.ikalagaming.core.events.PackageEvent;
import com.ikalagaming.core.packages.Package;
import com.ikalagaming.event.EventHandler;
import com.ikalagaming.event.EventManager;
import com.ikalagaming.event.Listener;
import com.ikalagaming.logging.LoggingLevel;
import com.ikalagaming.logging.PackageLogger;
import com.ikalagaming.util.SafeResourceLoader;

/**
 * A simple console.
 * 
 * @author Ches Burks
 * 
 */
public class Console extends WindowAdapter implements Package, Listener {
	private class ConsoleKeyListener extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent event) {
			// TODO paste
			int keyCode = event.getKeyCode();
			switch (keyCode) {
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_KP_LEFT:
				moveLeft();
				break;
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_KP_RIGHT:
				moveRight();
				break;
			case KeyEvent.VK_UP:
			case KeyEvent.VK_KP_UP:
				if (history.hasPrevious()) {
					setCurrentText(history.getPrevious());
				}
				break;
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_KP_DOWN:
				if (history.hasNext()) {
					setCurrentText(history.getNext());
				}
				break;
			case KeyEvent.VK_ENTER:
				runLine();
				break;
			case KeyEvent.VK_BACK_SPACE:
				delChar();
				break;
			case KeyEvent.VK_0:
			case KeyEvent.VK_1:
			case KeyEvent.VK_2:
			case KeyEvent.VK_3:
			case KeyEvent.VK_4:
			case KeyEvent.VK_5:
			case KeyEvent.VK_6:
			case KeyEvent.VK_7:
			case KeyEvent.VK_8:
			case KeyEvent.VK_9:
			case KeyEvent.VK_A:
			case KeyEvent.VK_B:
			case KeyEvent.VK_C:
			case KeyEvent.VK_D:
			case KeyEvent.VK_E:
			case KeyEvent.VK_F:
			case KeyEvent.VK_G:
			case KeyEvent.VK_H:
			case KeyEvent.VK_I:
			case KeyEvent.VK_J:
			case KeyEvent.VK_K:
			case KeyEvent.VK_L:
			case KeyEvent.VK_M:
			case KeyEvent.VK_N:
			case KeyEvent.VK_O:
			case KeyEvent.VK_P:
			case KeyEvent.VK_Q:
			case KeyEvent.VK_R:
			case KeyEvent.VK_S:
			case KeyEvent.VK_T:
			case KeyEvent.VK_U:
			case KeyEvent.VK_V:
			case KeyEvent.VK_W:
			case KeyEvent.VK_X:
			case KeyEvent.VK_Y:
			case KeyEvent.VK_Z:
			case KeyEvent.VK_NUMPAD0:
			case KeyEvent.VK_NUMPAD1:
			case KeyEvent.VK_NUMPAD2:
			case KeyEvent.VK_NUMPAD3:
			case KeyEvent.VK_NUMPAD4:
			case KeyEvent.VK_NUMPAD5:
			case KeyEvent.VK_NUMPAD6:
			case KeyEvent.VK_NUMPAD7:
			case KeyEvent.VK_NUMPAD8:
			case KeyEvent.VK_NUMPAD9:
			case KeyEvent.VK_AMPERSAND:
			case KeyEvent.VK_ASTERISK:
			case KeyEvent.VK_AT:
			case KeyEvent.VK_BACK_SLASH:
			case KeyEvent.VK_BRACELEFT:
			case KeyEvent.VK_BRACERIGHT:
			case KeyEvent.VK_CLOSE_BRACKET:
			case KeyEvent.VK_COLON:
			case KeyEvent.VK_COMMA:
			case KeyEvent.VK_DOLLAR:
			case KeyEvent.VK_EQUALS:
			case KeyEvent.VK_EXCLAMATION_MARK:
			case KeyEvent.VK_GREATER:
			case KeyEvent.VK_LEFT_PARENTHESIS:
			case KeyEvent.VK_LESS:
			case KeyEvent.VK_MINUS:
			case KeyEvent.VK_NUMBER_SIGN:
			case KeyEvent.VK_OPEN_BRACKET:
			case KeyEvent.VK_PERIOD:
			case KeyEvent.VK_PLUS:
			case KeyEvent.VK_QUOTE:
			case KeyEvent.VK_QUOTEDBL:
			case KeyEvent.VK_RIGHT_PARENTHESIS:
			case KeyEvent.VK_SEMICOLON:
			case KeyEvent.VK_SLASH:
			case KeyEvent.VK_SPACE:
				addChar(event.getKeyChar());
				break;
			default:
				break;
			}
		}

		@Override
		public void keyReleased(KeyEvent event) {}
	}

	private ResourceBundle resourceBundle;
	private String windowTitle;
	private int width = 680;
	private int height = 350;
	private int maxLineCount = 150;
	private Color background = new Color(2, 3, 2);

	private Color foreground = new Color(2, 200, 2);
	private JFrame frame;

	private JTextArea textArea;
	private int cursorX = 0;// delta from left of screen
	private int cursorY = 0;// delta from last line
	private int posInString = 0;// where the cursor is in the string
	private int charWidth = 80;// how many characters per line
	private int charHeight = 25;// lines per window
	private char inputIndicator = '>';
	private String currentLine = "";
	private int currentIndicatorLine = 0;
	private final int maxHistory = 30;

	private CommandHistory history;
	private PackageManager packageManager;
	private String packageName = "console";
	private boolean enabled = false;
	private PackageLogger logger;

	private final double version = 0.1;

	/**
	 * Adds a char to the end of the current string and console line
	 * 
	 * @param c the char to add
	 */
	private void addChar(char c) {
		if (cursorY > 0) {
			textArea.insert(System.lineSeparator(),
					getSafeLineStartOffset(currentIndicatorLine + cursorY)

					+ ((posInString + 1) % charWidth) + 1);
		}
		textArea.insert("" + c, getSafeLineStartOffset(currentIndicatorLine)
				+ (posInString + 1) % charWidth);
		// how many lines the current line takes up
		currentLine =
				currentLine.substring(0, posInString) + c
						+ currentLine.substring(posInString);
		moveRight();
	}

	/**
	 * Appends the input indicator char to the console
	 */
	private void appendIndicatorChar() {
		textArea.append("" + inputIndicator);
		++cursorX;
		moveRight();
	}

	/**
	 * Adds a String to the bottom of the console. Removes the top lines
	 * if/while they exceed the maximum line count.
	 * 
	 * @param message The message to append
	 */
	public synchronized void appendMessage(String message) {
		// should this not be synchronized?
		// it seems like it could be a choke point for speed. -CB
		String curLine = currentLine;
		int x = cursorX;
		int y = cursorY;
		int p = posInString;
		clearCurrentText();
		removeIndicatorChar();
		textArea.append(message + System.lineSeparator());
		updateInputLine();
		textArea.append(curLine);
		currentLine = curLine;
		cursorX = x;
		cursorY = y;
		posInString = p;
		validatePositions();
		updateCaretPosition();
		while (textArea.getLineCount() > maxLineCount) {
			removeTopLine();
		}
	}

	/**
	 * Clears out the text on the current line(s). Everything after the
	 * indicator char until the end of the current string (end of the console)
	 * will be removed.
	 */
	private void clearCurrentText() {
		int start;
		// fetch the index of the last line of text
		start = getSafeLineStartOffset(currentIndicatorLine);
		// add one to account for the input indicator char
		++start;
		textArea.replaceRange("", start, start + currentLine.length());
		posInString = 0;
		cursorY = 0;
		cursorX = 0;
		currentLine = "";
		validatePositions();
		updateCaretPosition();
	}

	/**
	 * Removes a char from the end of the current string and console line
	 */
	private void delChar() {
		if (cursorY == 0) {
			if (cursorX <= 1) {
				return;
			}
		}
		int pos =
				getSafeLineStartOffset(currentIndicatorLine) + cursorY
						+ ((posInString + 1) % charWidth);
		textArea.replaceRange("", pos - 1, pos);

		currentLine =
				currentLine.substring(0, posInString - 1)
						+ currentLine.substring(posInString);
		moveLeft();
	}

	@Override
	public boolean disable() {
		onDisable();
		enabled = false;
		return true;
	}

	@Override
	public boolean enable() {
		Runnable myrunnable = new Runnable() {
			public void run() {
				onEnable();
			}
		};
		new Thread(myrunnable).start();// Call it when you need to run the
										// function

		enabled = true;
		return true;
	}

	/**
	 * Returns the window height. This is the height of the frame the console is
	 * in.
	 * 
	 * @return the height of the frame
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Returns the maximum number of lines that are stored in the window.
	 * 
	 * @return the max number of lines
	 */
	public int getMaxLineCount() {
		return maxLineCount;
	}

	@Override
	public PackageManager getPackageManager() {
		return this.packageManager;
	}

	/**
	 * Returns the lineStartOffset of the given line and handles errors.
	 * 
	 * @param line the line to find
	 * @return the offset of the start of the line
	 */
	private int getSafeLineStartOffset(int line) {
		try {
			return textArea.getLineStartOffset(line);
		}
		catch (BadLocationException e) {
			logger.logError(SafeResourceLoader.getString("error_bad_location",
					resourceBundle, "Bad location"), LoggingLevel.WARNING,
					"Console.getSafeLineOffset(String)");
		}
		return -1;
	}

	@Override
	public String getName() {
		return packageName;
	}

	@Override
	public double getVersion() {
		return version;
	}

	/**
	 * Returns window width. This is the width of the frame the console is in.
	 * 
	 * @return the width of the frame
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Returns the window title.
	 * 
	 * @return the String that is used as the title
	 */
	public String getWindowTitle() {
		return windowTitle;
	}

	private void init() {
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
		MyCaret caret = (MyCaret) textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		caret.setBlinkRate(500);
		caret.setVisible(true);
		textArea.setCaretColor(foreground);

		// unbind caret bindings
		ActionMap am = textArea.getActionMap();
		am.get("caret-down").setEnabled(false);
		am.get("caret-up").setEnabled(false);
		am.get("selection-up").setEnabled(false);// shift pressed UP
		am.get("caret-next-word").setEnabled(false);// ctrl pressed RIGHT
		am.get("selection-previous-word").setEnabled(false);// shift ctrl
		// pressed LEFT
		am.get("selection-up").setEnabled(false);// shift pressed KP_UP
		am.get("caret-down").setEnabled(false);// pressed DOWN
		am.get("caret-previous-word").setEnabled(false);// ctrl pressed LEFT
		am.get("caret-end-line").setEnabled(false);// pressed END
		am.get("selection-page-up").setEnabled(false);// shift pressed PAGE_UP
		am.get("caret-up").setEnabled(false);// pressed KP_UP
		am.get("delete-next").setEnabled(false);// pressed DELETE
		am.get("caret-begin").setEnabled(false);// ctrl pressed HOME
		am.get("selection-backward").setEnabled(false);// shift pressed LEFT
		am.get("caret-end").setEnabled(false);// ctrl pressed END
		am.get("delete-previous").setEnabled(false);// pressed BACK_SPACE
		am.get("selection-next-word").setEnabled(false);// shift ctrl pressed
		// RIGHT
		am.get("caret-backward").setEnabled(false);// pressed LEFT
		am.get("caret-backward").setEnabled(false);// pressed KP_LEFT
		am.get("selection-forward").setEnabled(false);// shift pressed KP_RIGHT
		am.get("delete-previous").setEnabled(false);// ctrl pressed H
		am.get("unselect").setEnabled(false);// ctrl pressed BACK_SLASH
		am.get("insert-break").setEnabled(false);// pressed ENTER
		am.get("selection-begin-line").setEnabled(false);// shift pressed HOME
		am.get("caret-forward").setEnabled(false);// pressed RIGHT
		am.get("selection-page-left").setEnabled(false);// shift ctrl pressed
		// PAGE_UP
		am.get("selection-down").setEnabled(false);// shift pressed DOWN
		am.get("page-down").setEnabled(false);// pressed PAGE_DOWN
		am.get("delete-previous-word").setEnabled(false);// ctrl pressed
		// BACK_SPACE
		am.get("delete-next-word").setEnabled(false);// ctrl pressed DELETE
		am.get("selection-backward").setEnabled(false);// shift pressed KP_LEFT
		am.get("selection-page-right").setEnabled(false);// shift ctrl pressed
		// PAGE_DOWN
		am.get("caret-next-word").setEnabled(false);// ctrl pressed KP_RIGHT
		am.get("selection-end-line").setEnabled(false);// shift pressed END
		am.get("caret-previous-word").setEnabled(false);// ctrl pressed KP_LEFT
		am.get("caret-begin-line").setEnabled(false);// pressed HOME
		am.get("caret-down").setEnabled(false);// pressed KP_DOWN
		am.get("selection-forward").setEnabled(false);// shift pressed RIGHT
		am.get("selection-end").setEnabled(false);// shift ctrl pressed END
		am.get("selection-previous-word").setEnabled(false);// shift ctrl
		// pressed KP_LEFT
		am.get("selection-down").setEnabled(false);// shift pressed KP_DOWN
		am.get("insert-tab").setEnabled(false);// pressed TAB
		am.get("caret-up").setEnabled(false);// pressed UP
		am.get("selection-begin").setEnabled(false);// shift ctrl pressed HOME
		am.get("selection-page-down").setEnabled(false);// shift pressed
		// PAGE_DOWN
		am.get("delete-previous").setEnabled(false);// shift pressed BACK_SPACE
		am.get("caret-forward").setEnabled(false);// pressed KP_RIGHT
		am.get("selection-next-word").setEnabled(false);// shift ctrl pressed
		// KP_RIGHT
		am.get("page-up").setEnabled(false);// pressed PAGE_UP

		history = new CommandHistory();
		history.setMaxLines(maxHistory);

		textArea.addKeyListener(new ConsoleKeyListener());

		frame.getContentPane().add(new JScrollPane(textArea));

		frame.setVisible(true);
		System.gc();
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	private void moveLeft() {
		if (cursorY == 0) {
			if (cursorX <= 1) {
				updateCaretPosition();
				return;
			}
			else {
				--cursorX;
				--posInString;
			}
		}
		else if (cursorY > 0) {
			// on an additional line
			if (cursorX > 0) {
				--cursorX;
				--posInString;
			}
			else if (cursorX <= 0) {
				cursorX = charWidth - 1;
				--posInString;
				--cursorY;
			}
		}
		else {
			// on the same line as the indicator
			if (cursorX > 1) {
				--cursorX;
				--posInString;
			}
		}
		validatePositions();
		updateCaretPosition();

	}

	private void moveRight() {
		if (currentLine.length() <= 0) {
			validatePositions();
			updateCaretPosition();
			return;// do not do anything
		}
		if (posInString >= currentLine.length()) {
			validatePositions();
			updateCaretPosition();
			return;// do not do anything
		}
		if (cursorX < 0) {
			posInString = 0;
			cursorX = 0;
			validatePositions();
			updateCaretPosition();
			return;// do not do anything
		}
		else if (cursorX >= 0 && cursorX < charWidth) {
			++cursorX;
			++posInString;
			validatePositions();
			updateCaretPosition();
		}
		else if (cursorX >= charWidth) {
			cursorX = 0;
			++cursorY;
			++posInString;
			validatePositions();
			updateCaretPosition();
		}
	}

	/**
	 * Moves the cursor to the next line, then shows the line indicator char.
	 */
	private void newLine() {
		textArea.append(System.lineSeparator());
		posInString = 0;
		currentLine = "";
		if (textArea.getLineCount() < charHeight) {
			++cursorY;
		}
		cursorX = 0;
		cursorY = 0;
		updateInputLine();
		while (textArea.getLineCount() > maxLineCount) {
			removeTopLine();
		}
	}

	/**
	 * Called when a command event is sent.
	 * 
	 * @param event the command sent
	 */
	@EventHandler
	public void onCommand(CommandFired event) {
		appendMessage("got cmd " + event.getMessage());
		if (!event.getTo().equalsIgnoreCase(packageName)) {
			return;
		}

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
		logger = new PackageLogger(this);
		try {
			resourceBundle =
					ResourceBundle.getBundle(ResourceLocation.Console,
							Localization.getLocale());
		}
		catch (MissingResourceException missingResource) {
			// don't localize this since it would fail anyways
			logger.logError("Locale not found", LoggingLevel.WARNING,
					"Console.onLoad()");
		}
		windowTitle =
				SafeResourceLoader
						.getString("title", resourceBundle, "Console");

	}

	/**
	 * Called when a package event is sent out by the event system.
	 * 
	 * @param event the event that was fired
	 */
	@EventHandler
	public void onPackageEvent(PackageEvent event) {
		if (event.getTo() != packageName) {
			return;
		}
		String callMethod = "call";
		String onLoad = "onLoad";
		String onUnload = "onUnload";
		String enable = "enable";
		String disable = "disable";

		ResourceBundle packageBundle;
		packageBundle = packageManager.getResourceBundle();

		SafeResourceLoader.getString("CMD_CALL", packageBundle, "call");
		SafeResourceLoader.getString("ARG_ON_LOAD", packageBundle, "onLoad");
		SafeResourceLoader
				.getString("ARG_ON_UNLOAD", packageBundle, "onUnload");
		SafeResourceLoader.getString("ARG_ENABLE", packageBundle, "enable");
		SafeResourceLoader.getString("ARG_DISABLE", packageBundle, "disable");

		if (event.getMessage().startsWith(callMethod)) {
			String trimmed = event.getMessage().replaceFirst(callMethod, "");
			trimmed = trimmed.replaceFirst(" ", "");
			if (trimmed.startsWith(onLoad)) {
				onLoad();
			}
			else if (trimmed.startsWith(onUnload)) {
				onUnload();
			}
			else if (trimmed.startsWith(enable)) {
				enable();
			}
			else if (trimmed.startsWith(disable)) {
				disable();
			}
		}
	}

	@Override
	public void onUnload() {
		if (frame != null) {
			frame.setVisible(false);
			frame.dispose();
			frame = null;
		}
		resourceBundle = null;
		history = null;
		packageManager = null;
		logger = null;
	}

	@Override
	public boolean reload() {
		if (!disable()) {
			// failed to disable
			return false;
		}
		if (!enable()) {
			// failed to enable
			return false;
		}
		return true;
	}

	/**
	 * Appends the input indicator char to the console
	 */
	private void removeIndicatorChar() {
		int offset = getSafeLineStartOffset(currentIndicatorLine);
		textArea.replaceRange("", offset, offset + 1);
	}

	/**
	 * Removes the top line of the input.
	 */
	private void removeTopLine() {
		int end;
		try {
			end = textArea.getLineEndOffset(0);
			textArea.replaceRange("", 0, end);
		}
		catch (BadLocationException e) {
			logger.logError(SafeResourceLoader.getString("error_bad_location",
					resourceBundle, "Bad location"), LoggingLevel.WARNING,
					"Console.removeTopLine(String)");
		}
	}

	/**
	 * Attempts to execute the current line of input. If none exists, it does
	 * nothing.
	 */
	private void runLine() {
		String line = currentLine;
		newLine();

		if (line.isEmpty()) {
			// don't do anything with an empty line
			return;
		}

		history.addItem(line);

		String firstWord = line.trim().split("\\s+")[0];

		if (!packageManager.getCommandRegistry().contains(firstWord)) {
			appendMessage(resourceBundle.getString("unknown_command") + " "
					+ firstWord);
		}
		if (packageManager.isLoaded("event-manager")) {
			EventManager mgr =
					(EventManager) packageManager.getPackage("event-manager");

			Package pack =
					packageManager.getCommandRegistry().getParent(firstWord);
			if (pack != null) {
				mgr.fireEvent(new CommandFired(pack.getName(), line));
			}
		}
	}

	/**
	 * Sets the current text. This assumes that the indicator char is already in
	 * place. This will clear out the current text if it is not already cleared.
	 */
	private void setCurrentText(String s) {
		if (!currentLine.isEmpty()) {
			clearCurrentText();
		}
		textArea.append(s);
		posInString = s.length();
		cursorY = s.length() / charWidth;
		cursorX = s.length() % charWidth + 1;
		currentLine = s;
		validatePositions();
		updateCaretPosition();
	}

	/**
	 * Sets the frame height. This is the height of the frame the console is in.
	 * 
	 * @param height The new height
	 */
	public void setHeight(int height) {
		this.height = height;
		frame.setSize(frame.getWidth(), height);
	}

	/**
	 * Sets the maximum number of lines stored in the window.
	 * 
	 * @param maxLineCount the maximum number of lines to store
	 */
	public void setMaxLineCount(int maxLineCount) {
		this.maxLineCount = maxLineCount;
	}

	@Override
	public void setPackageManager(PackageManager parent) {
		this.packageManager = parent;
	}

	/**
	 * Sets the frame width. This is the width of the frame the console is in.
	 * 
	 * @param width The new width
	 */
	public void setWidth(int width) {
		this.width = width;
		frame.setSize(width, frame.getHeight());
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
	 * Moves the caret to the correct position.
	 */
	private void updateCaretPosition() {
		int position =
				getSafeLineStartOffset(currentIndicatorLine + cursorY)
						+ cursorX;
		if (position >= textArea.getText().length()) {
			position = textArea.getText().length();
		}
		textArea.setCaretPosition(position);
	}

	private void updateInputLine() {
		currentIndicatorLine = textArea.getLineCount() - 1;
		appendIndicatorChar();
		validatePositions();
		updateCaretPosition();
	}

	/**
	 * Checks that the cursor and string positions are valid, and fixes them if
	 * they are not.
	 */
	private void validatePositions() {
		if (cursorX < 0) {
			cursorX = 0;
		}
		if (cursorY < 0) {
			cursorY = 0;
		}
		if (cursorX > charWidth) {
			cursorX = charWidth;
		}
		if (posInString < 0) {
			posInString = 0;
		}
		if (posInString > currentLine.length()) {
			posInString = currentLine.length();
		}
	}

}
