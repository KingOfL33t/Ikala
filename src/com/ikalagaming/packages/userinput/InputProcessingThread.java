package com.ikalagaming.packages.userinput;


/**
 * Keeps the input package processing input.
 *
 * @author Ches Burks
 *
 */
public class InputProcessingThread extends Thread{

	private boolean running;
	private InputPackage manager;

	/**
	 * Creates and starts the thread.
	 * @param manager the logging package that this dispatcher belongs to
	 */
	public InputProcessingThread(InputPackage manager){
		this.running = true;
		this.manager = manager;
	}

	/**
	 * Tells the manager to process input constantly
	 */
	public void run() {
		if (!running){
			return;
		}
		while (running){
			try {
				sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			manager.processInput();
		}
	}

	/**
	 * Stops the thread from executing its run method in preparation for
	 * shutting down the thread.
	 */
	public void terminate(){
		running = false;
	}
}
