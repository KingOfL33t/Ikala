package com.ikalagaming.core;

/**
 * A distinct chunk of the program with a specific purpose and methods for
 * managing its state and interacting with the main program.
 *
 * @author Ches Burks
 *
 */
public interface Node {
	/**
	 * Which version of the node this is. This changes periodically for each
	 * node subclass as they are changed and updated.
	 */
	final double version = 0.0;

	/**
	 * Returns the type of node this is. This is a string that describes
	 * the node, such as "Graphics" or "AI".
	 *
	 * @return a string descriptor of the node
	 */
	public String getType();

	/**
	 * Returns this classes version number. This changes periodically for each
	 * node subclass as they are changed and updated.
	 *
	 * @return the version
	 */
	public double getVersion();

	/**
	 * Activates the node and enables it to perform its normal functions.
	 *
	 * @return true if the node was successfully enabled
	 */
	public boolean enable();

	/**
	 * Deactivates the node and halts all of its operations. The node is still
	 * loaded in memory but not active.
	 *
	 * @return true if the node has been successfully disabled
	 */
	public boolean disable();

	/**
	 * Disables and then enables the node.
	 *
	 * @return true if the node restarted successfully
	 */
	public boolean reload();

	/**
	 * Returns true if the node is enabled, and false otherwise.
	 *
	 * @return true if the node is enabled
	 */
	public boolean isEnabled();

	/**
	 * This method is called when the node is enabled. Initialization tends
	 * to be performed here.
	 */
	public void onEnable();

	/**
	 * This method is called when the node is disabled.
	 */
	public void onDisable();

	/**
	 * Called when the node is loaded into memory. The node may or may not
	 * be enabled at this time.
	 */
	public void onLoad();

	/**
	 * Called when the node is unloaded from memory.
	 */
	public void onUnload();
}
