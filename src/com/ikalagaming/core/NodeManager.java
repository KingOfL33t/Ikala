package com.ikalagaming.core;

import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.ikalagaming.core.events.NodeEvent;
import com.ikalagaming.event.EventManager;
import com.ikalagaming.logging.ErrorCode;
import com.ikalagaming.logging.LoggingLevel;
import com.ikalagaming.logging.LoggingNode;

/**
 * Handles loading, unloading and storage of nodes.
 * @author Ches Burks
 *
 */
public class NodeManager {

	private ResourceBundle resourceBundle =
			ResourceBundle.getBundle(ResourceLocation.NodeManager,
					Localization.getLocale());
	/**maps strings to nodes loaded in memory*/
	private HashMap<String, Node> loadedNodes;
	private String nodeName = "node-manager";

	/**
	 * Constructs a new {@link NodeManager} and initializes variables.
	 */
	public NodeManager() {
		loadedNodes = new HashMap<String, Node>();
	}

	/**
	 * <p>Loads the given node into memory, stores it by type, and
	 * enables it if nodes are
	 * {@link com.ikalagaming.core.NodeSettings#ENABLE_ON_LOAD
	 * enabled on load} by default.</p>
	 * <p> If the type of node already exists in the manager,
	 * and the new node has a higher version number, then the old node
	 * is unloaded and the new one is loaded in its place. If the versions
	 * are equal, or the new node is older, then it does not load the new
	 * version and returns false.
	 * </p>
	 * @param toLoad the node to load
	 * @return true if the node was loaded properly, false otherwise
	 */
	public boolean loadNode(Node toLoad){
		getLogger().log(LoggingLevel.FINE, "Loading node " + toLoad.getType()
				+ " (V"+toLoad.getVersion()+ ")"+"...");
		//if the node exists and is older than toLoad, unload
		if (isLoaded(toLoad)){
			getLogger().log(LoggingLevel.FINE, "Node " + toLoad.getType()
					+ " is already loaded. (V" + toLoad.getVersion() + ")");
			if (loadedNodes.get(toLoad.getType()).getVersion()
					< toLoad.getVersion()){
				unloadNode(loadedNodes.get(toLoad.getType()));
				//unload the old node and continue loading the new one
			}
			else{
				getLogger().log(LoggingLevel.FINE, "Node " + toLoad.getType()
						+ " (V"+toLoad.getVersion()+ ")"+" was outdated. "
						+ "Aborting.");
				return false;
			}
		}

		//store the new node
		loadedNodes.put(toLoad.getType(), toLoad);

		//load it
		if (NodeSettings.USE_EVENTS_FOR_ACCESS
				&& NodeSettings.USE_EVENTS_FOR_ON_LOAD){
			String toSend = "";
			boolean messageValid = true;
			try {
				toSend = resourceBundle.getString("CMD_CALL")
						+ " "
						+ resourceBundle.getString("ARG_ON_LOAD");
			}
			catch (MissingResourceException missingResource){
				getLogger().logError(ErrorCode.locale_resource_not_found,
						LoggingLevel.WARNING,
						"NodeManager.loadNode(Node) load");
				messageValid = false;
			}
			catch (ClassCastException classCast){
				getLogger().logError(ErrorCode.locale_resource_wrong_type,
						LoggingLevel.WARNING,
						"NodeManager.loadNode(Node) load");
				messageValid = false;
			}

			if (messageValid){
				getLogger().log(LoggingLevel.FINER, "Calling onLoad of "
						+ toLoad.getType()
						+ " using event system.");
				/*
				 * Tries to send the event. If the return value is false,
				 * it failed and therefore we must load manually
				 */
				if (!fireEvent(toLoad.getType(), toSend)){
					getLogger().log(LoggingLevel.FINER, "Event failed"
							+ "calling method directly.");
					toLoad.onLoad();
				}
			}
			else {
				getLogger().log(LoggingLevel.FINER, "Calling onLoad of "
						+ toLoad.getType());
				//errors creating message so the event would not work
				toLoad.onLoad();
			}
		}
		else{
			getLogger().log(LoggingLevel.FINER, "Calling onLoad of "
					+ toLoad.getType());
			//not using events for onload, or not using events at all
			toLoad.onLoad();
		}

		//enable the node
		if (NodeSettings.ENABLE_ON_LOAD){
			if (NodeSettings.USE_EVENTS_FOR_ACCESS
					&& NodeSettings.USE_EVENTS_FOR_ENABLE){
				String toSend = "";
				boolean messageValid = true;
				try {
					toSend = resourceBundle.getString("CMD_CALL")
							+ " "
							+ resourceBundle.getString("ARG_ENABLE");
				}
				catch (MissingResourceException missingResource){
					getLogger().logError(ErrorCode.locale_resource_not_found,
							LoggingLevel.WARNING,
							"NodeManager.loadNode(Node) enable");
					messageValid = false;
				}
				catch (ClassCastException classCast){
					getLogger().logError(ErrorCode.locale_resource_wrong_type,
							LoggingLevel.WARNING,
							"NodeManager.loadNode(Node) enable");
					messageValid = false;
				}

				if (messageValid){
					getLogger().log(LoggingLevel.FINER, "Calling onEnable of "
							+ toLoad.getType()
							+ " using event system.");
					/*
					 * Tries to send the event. If the return value is false,
					 * it failed and therefore we must load manually
					 */
					if (!fireEvent(toLoad.getType(), toSend)){
						getLogger().log(LoggingLevel.FINER, "Event failed"
								+ "calling method directly.");
						toLoad.enable();
					}
				}
				else {
					getLogger().log(LoggingLevel.FINER, "Calling onLoad of "
							+ toLoad.getType());
					//errors creating message so the event would not work
					toLoad.enable();
				}
			}
			else{
				getLogger().log(LoggingLevel.FINER, "Calling onLoad of "
						+ toLoad.getType());
				//not using events for enable, or not using events at all
				toLoad.enable();
			}
		}
		getLogger().log(LoggingLevel.FINE, "Node " + toLoad.getType()
				+ " (V"+toLoad.getVersion()+ ")"+" loaded!");
		return true;
	}

	/**
	 * Fires an event with a message to a node type from the node manager.
	 * If an error occurs, this will return false. The event should not have
	 * been sent if false was returned.
	 *
	 * @param to the node to send the message to
	 * @param content the message to transfer
	 * @return true if the event was fired correctly
	 */
	private boolean fireEvent(String to, String content){

		if (!isLoaded(nodeName)){
			getLogger().logError(ErrorCode.node_not_loaded,
					LoggingLevel.WARNING, to);
			return false;
		}

		if (!getNode(nodeName).isEnabled()){
			getLogger().logError(ErrorCode.node_not_enabled,
					LoggingLevel.WARNING, to);
			return false;
		}

		NodeEvent tmpEvent;

		tmpEvent = new NodeEvent(
				nodeName,
				to,
				content);
		try{
			if (tmpEvent!= null){//just in case the assignment failed
				((EventManager)getNode(nodeName)).fireEvent(tmpEvent);

			}
		}
		catch (IllegalStateException illegalState){
			//the queue was full
			getLogger().logError(ErrorCode.event_queue_full,
					LoggingLevel.WARNING,
					"NodeManager.fireEvent(String, String)");
			return false;
		}
		return true;
	}

	/**
	 * Returns true if a node exists with the given type
	 *  (for example: "Graphics")'
	 * @param type the node type
	 * @return true if the node is loaded in memory, false if it does not exist
	 */
	public boolean isLoaded(String type){
		return loadedNodes.containsKey(type);
	}


	/**
	 * Returns true if a node exists that has the same type as the provided
	 * node (for example: "Graphics"). This is the same as calling
	 * <code>{@link #isLoaded(String) isLoaded}(Node.getType())</code>
	 * @param type the node type
	 * @return true if the node is loaded in memory, false if it does not exist
	 */
	public boolean isLoaded(Node type){
		return loadedNodes.containsKey(type.getType());
	}

	/**
	 * If a node of type exists ({@link #isLoaded(String)}), then the node
	 * that is of that type is returned. If no node exists of that type,
	 * null is returned.
	 *
	 * @param type The node type
	 * @return the Node with the given type or null if none exists
	 */
	public Node getNode(String type){
		if (isLoaded(type)){
			return loadedNodes.get(type);
		}
		else{
			return null;
		}
	}

	/**
	 * Attempts to unload the node from memory. If no node exists with the
	 * given name ({@link #isLoaded(String)}), returns false and does nothing.
	 * @param toUnload The type of node to unload
	 * @return true if the node was unloaded properly
	 */
	public boolean unloadNode(String toUnload){
		getLogger().log(LoggingLevel.FINE, "Unloading node " + toUnload
				+"...");
		if (!isLoaded(toUnload)){
			getLogger().log(LoggingLevel.FINE, "Node " + toUnload
					+" is not loaded. Aborting.");
			return false;
		}

		if (NodeSettings.DISABLE_ON_UNLOAD){
			if (loadedNodes.get(toUnload).isEnabled()){
				if (NodeSettings.USE_EVENTS_FOR_ACCESS &&
						NodeSettings.USE_EVENTS_FOR_DISABLE){
					String toSend = "";
					boolean messageValid = true;
					try {
						toSend = resourceBundle.getString("CMD_CALL")
								+ " "
								+ resourceBundle.getString("ARG_ON_DISABLE");
					}
					catch (MissingResourceException missingResource){
						getLogger().logError(ErrorCode.locale_resource_not_found,
								LoggingLevel.WARNING,
								"NodeManager.unloadNode(String) disable");
						messageValid = false;
					}
					catch (ClassCastException classCast){
						getLogger().logError(ErrorCode.locale_resource_wrong_type,
								LoggingLevel.WARNING,
								"NodeManager.unloadNode(String) disable");
						messageValid = false;
					}

					if (messageValid){
						getLogger().log(LoggingLevel.FINER, "Calling disable "
								+ "method of " + toUnload + " using events.");
						/*
						 * Tries to send the event. If the return value is false,
						 * it failed and therefore we must load manually
						 */
						if (!fireEvent(toUnload, toSend)){
							getLogger().log(LoggingLevel.FINER,
									"Events failed, "
											+ "calling method instead.");
							loadedNodes.get(toUnload).disable();
						}
					}
					else {
						getLogger().log(LoggingLevel.FINER, "Calling disable "
								+ "method of " + toUnload);
						//errors creating message so the event would not work
						loadedNodes.get(toUnload).disable();
					}
				}
				else{
					getLogger().log(LoggingLevel.FINER, "Calling disable "
							+ "method of " + toUnload);
					loadedNodes.get(toUnload).disable();
				}
			}
		}

		if (NodeSettings.USE_EVENTS_FOR_ACCESS &&
				NodeSettings.USE_EVENTS_FOR_ON_UNLOAD){
			String toSend = "";
			boolean messageValid = true;
			try {
				toSend = resourceBundle.getString("CMD_CALL")
						+ " "
						+ resourceBundle.getString("ARG_UNLOAD");
			}
			catch (MissingResourceException missingResource){
				getLogger().logError(ErrorCode.locale_resource_not_found,
						LoggingLevel.WARNING,
						"NodeManager.unloadNode(String) unload");
				messageValid = false;
			}
			catch (ClassCastException classCast){
				getLogger().logError(ErrorCode.locale_resource_wrong_type,
						LoggingLevel.WARNING,
						"NodeManager.unloadNode(String) unload");
				messageValid = false;
			}

			if (messageValid){
				getLogger().log(LoggingLevel.FINER, "Calling onUnload "
						+ "method of " + toUnload + " using events.");
				/*
				 * Tries to send the event. If the return value is false,
				 * it failed and therefore we must load manually
				 */
				if (!fireEvent(toUnload, toSend)){
					getLogger().log(LoggingLevel.FINER,
							"Events failed, "
									+ "calling method instead.");
					loadedNodes.get(toUnload).onUnload();
				}
			}
			else {
				getLogger().log(LoggingLevel.FINER, "Calling onUnload "
						+ "method of " + toUnload);
				//errors creating message so the event would not work
				loadedNodes.get(toUnload).onUnload();
			}
		}
		else{
			getLogger().log(LoggingLevel.FINER, "Calling onUnload "
					+ "method of " + toUnload);
			loadedNodes.get(toUnload).onUnload();
		}

		loadedNodes.remove(toUnload);

		getLogger().log(LoggingLevel.FINE, "Node " + toUnload
				+" unloaded!");
		return true;
	}

	/**
	 * Attempts to unload the node from memory. Does nothing if the node is
	 * not loaded. Nodes are disabled before unloading. This calls
	 * {@link #unloadNode(String)} using the node type.
	 *
	 * @param toUnload The type of node to unload
	 */
	public void unloadNode(Node toUnload){
		/* using a string the nodes type to ensure
		 * the node that is stored in this class is modified and not just
		 * the node passed to the method.
		 */
		String type = toUnload.getType();
		if (!isLoaded(type)){
			getLogger().log(LoggingLevel.FINE, "Node " + toUnload.getType()
					+" is not loaded. Aborting.");
			return;
		}
		unloadNode(type);
	}

	/**
	 * Returns a logger for the system. If one does not exist, it will
	 * be created.
	 *
	 * @return a logger for the engine
	 */
	public LoggingNode getLogger(){
		String loggingNodeName = "logging";

		if (!loadedNodes.containsKey(loggingNodeName)){
			LoggingNode node = new LoggingNode();
			//store the new node
			loadedNodes.put(loggingNodeName, node);
			//we don't know if there is an event system.
			//this has to work properly
			node.onLoad();
			//enable the node
			if (NodeSettings.ENABLE_ON_LOAD){
				node.enable();
			}
		}
		//safe cast since we know its a LoggingNode
		return (LoggingNode)loadedNodes.get(loggingNodeName);

	}
}
