package com.ikalagaming.logging;

import java.util.NoSuchElementException;

import com.ikalagaming.core.IQueue;

/**
 * Holds an EventQueue and dispatches the events in order when possible.
 *
 * @author Ches Burks
 *
 */
public class LogDispatcher extends Thread{

	private IQueue<String> queue;
	private String currentStr;
	private boolean running;
	private boolean hasLogs;
	private LoggingPackage manager;

	/**
	 * Creates and starts the thread. It will begin attempting to dispatch
	 * events immediately if there are any available.
	 * @param manager the logging package that this dispatcher belongs to
	 */
	public LogDispatcher(LoggingPackage manager){
		queue = new IQueue<String>();
		this.hasLogs = false;
		this.running = true;
		this.manager = manager;
	}

	/**
	 * Adds the String to the queue pending logging.
	 *
	 * @param log The log message to record
	 * @throws IllegalStateException if the element cannot be added at this
	 * time due to capacity restrictions
	 */
	public void log(String log)
			throws IllegalStateException{
		try{
			queue.add(log);
			hasLogs = true;
		}
		catch(IllegalStateException illegalState){
			throw illegalState;
		}
		catch(NullPointerException nullPointer){
			;//do nothing since its a null event
		}
		catch(Exception e){
			System.err.println(e.toString());
		}
	}

	/**
	 * Checks for Strings in the queue, and logs them if possible.
	 * Does not do anything if {@link #terminate()} has been called.
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
			if (hasEvents()){
				try{
					if (queue.isEmpty()){
						hasLogs = false;
					}
					else if (queue.peek() != null){
						currentStr = queue.remove();
						//log it to the system output stream
						System.out.println(currentStr);
						//TODO log to console/file
					}
					else{
						continue;
					}
				}
				catch(NoSuchElementException noElement){
					//the queue is empty
					//hasEvents = false;
					continue;
				}
				catch(Exception e){
					System.err.println(e.toString());
				}
			}
		}
	}

	private boolean hasEvents(){
		return hasLogs;
	}

	/**
	 * Stops the thread from executing its run method in preparation for
	 * shutting down the thread.
	 */
	public void terminate(){
		hasLogs = false;
		running = false;
	}
}
