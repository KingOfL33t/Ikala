package com.ikalagaming.core;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;

/**
 * A simple console.
 * 
 * @author Ches Burks
 *
 */
public class Console extends WindowAdapter{

	private ResourceBundle resourceBundle = 
			ResourceBundle.getBundle("com.ikalagaming.core.resources.Console", 
					Localization.getLocale());
	private String windowTitle = resourceBundle.getString("title");
	private int width = 300;
	private int height = 200;
	private int maxLineCount = 150;
	private Color background = new Color(2,3,2);
	private Color foreground = new Color(2,200,2);

	private JFrame frame;
	private JTextArea textArea;
	private JTextField inputArea;

	/**
	 * Sets up the console.
	 * Constructs a new console and sets up components.
	 */
	public Console(){

		frame = new JFrame(windowTitle);
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setBackground(background);
		frame.setForeground(foreground);

		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setBackground(background);
		textArea.setForeground(foreground);
		DefaultCaret caret = (DefaultCaret)textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		inputArea = new JTextField();
		inputArea.setEditable(true);
		inputArea.setBackground(background);
		inputArea.setForeground(foreground);

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(new JScrollPane(textArea),
				BorderLayout.CENTER);
		frame.getContentPane().add(inputArea, BorderLayout.SOUTH);

		frame.setVisible(true);
	}

	/**
	 * Returns window width.
	 * This is the width of the frame the console is in.
	 * 
	 * @return the width of the frame
	 */
	public synchronized int getWidth() {
		return width;
	}

	/**
	 * Sets the frame width.
	 * This is the width of the frame the console is in.
	 * 
	 * @param width The new width
	 */
	public synchronized void setWidth(int width) {
		this.width = width;
		frame.setSize(width, frame.getHeight());
	}

	/**
	 * Returns the window height.
	 * This is the height of the frame the console is in.
	 * 
	 * @return the height of the frame
	 */
	public synchronized int getHeight() {
		return height;
	}

	/**
	 * Sets the frame height.
	 * This is the height of the frame the console is in.
	 * 
	 * @param height The new height
	 */
	public synchronized void setHeight(int height) {
		this.height = height;
		frame.setSize(frame.getWidth(), height);
	}

	/**
	 * Returns the maximum number of lines that are 
	 * stored in the window.
	 * 
	 * @return the max number of lines
	 */
	public synchronized int getMaxLineCount() {
		return maxLineCount;
	}

	/**
	 * Sets the maximum number of lines stored in the window.
	 * 
	 * @param maxLineCount the maximum number of lines to store
	 */
	public synchronized void setMaxLineCount(int maxLineCount) {
		this.maxLineCount = maxLineCount;
	}

	/**
	 * Returns the window title.
	 * 
	 * @return the String that is used as the title
	 */
	public synchronized String getWindowTitle() {
		return windowTitle;
	}

	/**
	 * Sets the title of the window.
	 * 
	 * @param windowTitle the String to use as the title
	 */
	public synchronized void setWindowTitle(String windowTitle) {
		this.windowTitle = windowTitle;
		frame.setTitle(windowTitle);
	}

	/**
	 * Adds a String to the bottom of the console.
	 * Removes the top lines if/while they exceed the maximum line count.
	 * 
	 * @param message The message to append
	 */
	public synchronized void appendMessage(String message){
		this.textArea.append(message+"\n");

		while (this.textArea.getLineCount() >= maxLineCount){
			int end;
			try {
				end = this.textArea.getLineEndOffset(0);
				this.textArea.replaceRange("", 0, end);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}

}
