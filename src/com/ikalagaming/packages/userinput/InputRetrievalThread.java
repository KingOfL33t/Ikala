
package com.ikalagaming.packages.userinput;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Fetches input from system.in and informs the input package of the data.
 * 
 * @author Ches Burks
 * 
 */
public class InputRetrievalThread extends Thread {

	private boolean running;
	private InputPackage manager;

	/**
	 * Creates and starts the thread.
	 * 
	 * @param manager the logging package that this dispatcher belongs to
	 */
	public InputRetrievalThread(InputPackage manager) {
		this.running = true;
		this.manager = manager;
	}

	/**
	 * Checks for Strings in the queue, and logs them if possible. Does not do
	 * anything if {@link #terminate()} has been called.
	 */
	public void run() {
		BufferedReader br =
				new BufferedReader(new InputStreamReader(System.in));
		String buffer = "";
		if (!running) {
			return;
		}
		while (running) {
			try {
				sleep(10);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				if (br.ready()) {
					try {
						buffer = buffer + (char) br.read();
					}
					catch (IOException e) {
						e.printStackTrace();
					}
					if (buffer.endsWith(System.lineSeparator())
							|| buffer.endsWith("\n")) {
						manager.addToInputBuffer(buffer);
						buffer = "";
					}
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Stops the thread from executing its run method in preparation for
	 * shutting down the thread.
	 */
	public void terminate() {
		running = false;
	}
}
