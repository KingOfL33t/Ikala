
package com.ikalagaming.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.io.IOException;
import java.util.HashSet;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.ActionMap;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;

import com.ikalagaming.core.Game;
import com.ikalagaming.core.Localization;
import com.ikalagaming.core.ResourceLocation;
import com.ikalagaming.core.events.CommandFired;
import com.ikalagaming.core.packages.Package;
import com.ikalagaming.core.packages.PackageState;
import com.ikalagaming.event.Listener;
import com.ikalagaming.logging.LoggingLevel;
import com.ikalagaming.logging.events.Log;
import com.ikalagaming.logging.events.LogError;
import com.ikalagaming.util.SafeResourceLoader;

/**
 * A simple console.
 * 
 * @author Ches Burks
 * 
 */
public class Console extends WindowAdapter implements Package, ClipboardOwner {
	private class ConsoleKeyListener extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent event) {
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
			case KeyEvent.VK_V:
				if (event.isControlDown()) {
					setCurrentText(getClipboardContents());
				}
				else {
					addChar(event.getKeyChar());
				}
				break;
			case KeyEvent.VK_C:
				if (event.isControlDown()) {
					if (textArea.getSelectedText() != null) {
						setClipboardContents(textArea.getSelectedText());
					}
				}
				else {
					addChar(event.getKeyChar());
				}
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
	private ConsoleListener listener = new ConsoleListener(this);
	private String windowTitle;
	private int width = 680;
	private int height = 350;
	private int maxLineCount = 150;
	private Color background = new Color(2, 3, 2);

	private Color foreground = new Color(2, 200, 2);
	private JFrame frame;

	private JTextArea textArea;
	private int posInString = 0;// where the cursor is in the string
	private char inputIndicator = '>';
	private String currentLine = "";
	private int currentIndicatorLine = 0;
	private final int maxHistory = 30;

	private CommandHistory history;
	private String packageName = "console";
	private PackageState state = PackageState.DISABLED;
	private final double version = 0.1;

	/**
	 * Adds a char to the end of the current string and console line
	 * 
	 * @param c the char to add
	 */
	private void addChar(char c) {
		textArea.insert("" + c, getSafeLineStartOffset(currentIndicatorLine)
				+ (posInString) + 1);
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
		++posInString;
		moveRight();
	}

	/**
	 * Adds a String to the bottom of the console. Removes the top lines
	 * if/while they exceed the maximum line count.
	 * 
	 * @param message The message to append
	 */
	public synchronized void appendMessage(String message) {
		if (!isEnabled()) {
			Game.getEventManager().fireEvent(
					new Log(SafeResourceLoader.getString("not_enabled",
							resourceBundle, "Console is not enabled"),
							LoggingLevel.SEVERE, this));
			return;
		}
		// should this not be synchronized?
		// it seems like it could be a choke point for speed. -CB

		int p = posInString;
		clearCurrentText();
		removeIndicatorChar();
		// the double spacing and removal of space is there to have an
		// extra gap just before the input line and any previous lines for
		// visibility
		if (textArea.getText().endsWith(System.lineSeparator())) {
			textArea.replaceRange("", textArea.getText().length() - 1, textArea
					.getText().length());// removes the last newline if it
											// exists
		}
		textArea.append(message);
		textArea.append(System.lineSeparator());// extra space to be removed
		textArea.append(System.lineSeparator());
		updateInputLine();
		textArea.append(currentLine);
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
		currentLine = "";
		validatePositions();
		updateCaretPosition();
	}

	/**
	 * Removes a char from the end of the current string and console line
	 */
	private void delChar() {
		if (posInString <= 0) {
			return;
		}
		int pos = getSafeLineStartOffset(currentIndicatorLine) + posInString;
		textArea.replaceRange("", pos, pos + 1);

		currentLine =
				currentLine.substring(0, posInString - 1)
						+ currentLine.substring(posInString);
		moveLeft();
	}

	@Override
	public boolean disable() {
		synchronized (state) {
			state = PackageState.DISABLING;
		}
		onDisable();
		return true;
	}

	@Override
	public boolean enable() {
		synchronized (state) {
			state = PackageState.ENABLING;
		}
		Runnable myrunnable = new Runnable() {
			public void run() {
				onEnable();
			}
		};
		new Thread(myrunnable).start();// Call it when you need to run the
		// function

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

	/**
	 * Returns the lineStartOffset of the given line and handles errors.
	 * 
	 * @param line the line to find
	 * @return the offset of the start of the line
	 */
	private int getSafeLineStartOffset(int line) {
		try {
			if (line >= textArea.getLineCount()) {
				if (textArea.getLineCount() >= 1) {
					line = textArea.getLineCount() - 1;
				}
				else {
					line = 0;
				}
			}
			if (line <= 0) {
				return 0;
			}
			return textArea.getLineStartOffset(line);
		}
		catch (BadLocationException e) {
			Game.getEventManager().fireEvent(
					new Log(SafeResourceLoader.getString("error_bad_location",
							resourceBundle, "Bad location"),
							LoggingLevel.WARNING, this));
		}
		return 0;
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
		synchronized (state) {
			if (state == PackageState.ENABLED) {
				return true;
			}
			return false;
		}
	}

	private void moveLeft() {
		if (posInString <= 0) {
			return;
		}
		--posInString;
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
		if (posInString < 0) {
			posInString = 0;
			validatePositions();
			updateCaretPosition();
		}
		else if (posInString >= 0) {
			++posInString;
			validatePositions();
			updateCaretPosition();
		}
		/*
		 * else if (cursorX >= charWidth) { cursorX = 0; ++cursorY;
		 * ++posInString; validatePositions(); updateCaretPosition(); }
		 */
	}

	/**
	 * Moves the cursor to the next line, then shows the line indicator char.
	 */
	private void newLine() {
		posInString = 0;
		currentLine = "";
		textArea.append(System.lineSeparator());// this extra space is removed
		textArea.append(System.lineSeparator());
		updateInputLine();
		while (textArea.getLineCount() > maxLineCount) {
			removeTopLine();
		}
		updateCaretPosition();
	}

	@Override
	public void onDisable() {
		frame.setVisible(false);
		frame.dispose();
		synchronized (state) {
			state = PackageState.DISABLED;
		}
	}

	@Override
	public void onEnable() {
		init();
		appendIndicatorChar();
		synchronized (state) {
			state = PackageState.ENABLED;
		}
	}

	@Override
	public void onLoad() {
		synchronized (state) {
			state = PackageState.LOADING;
		}
		try {
			resourceBundle =
					ResourceBundle.getBundle(ResourceLocation.Console,
							Localization.getLocale());
		}
		catch (MissingResourceException missingResource) {
			// don't localize this since it would fail anyways
			// TODO handle this better
			Game.getEventManager().fireEvent(
					new LogError("Locale not found for Console",
							LoggingLevel.WARNING, this));
		}
		windowTitle =
				SafeResourceLoader
						.getString("title", resourceBundle, "Console");
		synchronized (state) {
			state = PackageState.DISABLED;
		}
	}

	@Override
	public void onUnload() {
		synchronized (state) {
			state = PackageState.UNLOADING;
		}
		if (getPackageState() == PackageState.ENABLED) {
			disable();
			synchronized (state) {
				state = PackageState.UNLOADING;
			}
		}

		if (frame != null) {
			frame.setVisible(false);
			frame.dispose();
			frame = null;
		}
		resourceBundle = null;
		history = null;
		synchronized (state) {
			state = PackageState.PENDING_REMOVAL;
		}
	}

	@Override
	public boolean reload() {
		synchronized (state) {
			state = PackageState.UNLOADING;
		}
		if (frame != null) {
			frame.setVisible(false);
			frame.dispose();
			frame = null;
		}
		resourceBundle = null;
		history = null;

		onLoad();
		return true;
	}

	/**
	 * Replaces the input indicator char to the console
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
			Game.getEventManager().fireEvent(
					new Log(SafeResourceLoader.getString("error_bad_location",
							resourceBundle, "Bad location"),
							LoggingLevel.WARNING, this));

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

		if (!Game.getPackageManager().getCommandRegistry().contains(firstWord)) {
			appendMessage(SafeResourceLoader.getString("unknown_command",
					resourceBundle, "Unknown command")
					+ " '"
					+ firstWord
					+ "'. "
					+ SafeResourceLoader.getString("try_cmd", resourceBundle,
							"For a list of available commands, type")
					+ " '"
					+ SafeResourceLoader.getString("COMMAND_HELP", Game
							.getPackageManager().getResourceBundle(), "help")
					+ "'");
		}

		Package pack =
				Game.getPackageManager().getCommandRegistry()
						.getParent(firstWord);
		if (pack != null) {
			Game.getPackageManager().fireEvent(
					new CommandFired(pack.getName(), line));
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
				getSafeLineStartOffset(currentIndicatorLine) + posInString + 1;
		if (position >= textArea.getText().length()) {
			position = textArea.getText().length();
		}
		textArea.setCaretPosition(position);
		if (!textArea.getCaret().isVisible()) {
			textArea.getCaret().setVisible(true);
		}
	}

	/**
	 * Adds a new indicator character to the beginning of the last line.
	 */
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
		if (posInString < 0) {
			posInString = 0;
		}
		if (posInString > currentLine.length()) {
			posInString = currentLine.length();
		}
	}

	@Override
	public Set<Listener> getListeners() {
		HashSet<Listener> listeners = new HashSet<Listener>();
		listeners.add(listener);
		return listeners;
	}

	@Override
	public synchronized PackageState getPackageState() {
		return state;
	}

	/**
	 * Empty implementation of the ClipboardOwner interface.
	 */
	@Override
	public void lostOwnership(Clipboard aClipboard, Transferable aContents) {
		// do nothing
	}

	/**
	 * Copy a String to the clipboard, and make this class the owner of the
	 * Clipboard's contents.
	 * 
	 * @param contents the new contents of the clipboard
	 */
	private void setClipboardContents(String contents) {
		StringSelection stringSelection = new StringSelection(contents);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, this);
	}

	/**
	 * Get the String from the clipboard.
	 *
	 * @return any text found on the Clipboard. If one is not found, returns an
	 *         empty String.
	 */
	private String getClipboardContents() {
		String result = "";
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable contents = clipboard.getContents(this);
		boolean hasTransferableText =
				(contents != null)
						&& contents
								.isDataFlavorSupported(DataFlavor.stringFlavor);
		if (hasTransferableText) {
			try {
				result =
						(String) contents
								.getTransferData(DataFlavor.stringFlavor);
			}
			catch (UnsupportedFlavorException | IOException ex) {
				Game.getEventManager().fireEvent(
						new LogError(SafeResourceLoader.getString(
								"invalid_clipboard", resourceBundle,
								"Invalid clipboard contents").concat(
								ex.getLocalizedMessage()),
								LoggingLevel.WARNING, this));
			}
		}
		return result;
	}

}
