package com.ikalagaming.core;

import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.ikalagaming.core.events.NodeEvent;
import com.ikalagaming.event.EventManager;

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
		//if the node exists and is older than toLoad, unload
		if (isLoaded(toLoad)){
			if (loadedNodes.get(toLoad.getType()).getVersion()
					< toLoad.getVersion()){
				unloadNode(loadedNodes.get(toLoad.getType()));
				//unload the old node and continue loading the new one
			}
			else{
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
				//TODO error
				messageValid = false;
			}
			catch (ClassCastException classCast){
				//TODO error
				messageValid = false;
			}

			if (messageValid){
				/*
				 * Tries to send the event. If the return value is false,
				 * it failed and therefore we must load manually
				 */
				if (!fireEvent(toLoad.getType(), toSend)){
					toLoad.onLoad();
				}
			}
			else {
				//errors creating message so the event would not work
				toLoad.onLoad();
			}
		}
		else{
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
					//TODO error
					messageValid = false;
				}
				catch (ClassCastException classCast){
					//TODO error
					messageValid = false;
				}

				if (messageValid){
					/*
					 * Tries to send the event. If the return value is false,
					 * it failed and therefore we must load manually
					 */
					if (!fireEvent(toLoad.getType(), toSend)){
						toLoad.enable();
					}
				}
				else {
					//errors creating message so the event would not work
					toLoad.enable();
				}
			}
			else{
				//not using events for enable, or not using events at all
				toLoad.enable();
			}
		}
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
		String eventNodeType;
		try {
			eventNodeType = ResourceBundle.getBundle(
					ResourceLocation.EventManager,
					Localization.getLocale()).getString("nodeType");
		}
		catch (MissingResourceException missingRes){
			//TODO error
			return false;
		}

		if (!isLoaded(eventNodeType)){
			//TODO error
			return false;
		}

		if (!getNode(eventNodeType).isEnabled()){
			//TODO error
			return false;
		}

		NodeEvent tmpEvent;
		try {
			tmpEvent = new NodeEvent(
					resourceBundle.getString("NODE_MANAGER_NAME"),
					to,
					content);
		}
		catch (MissingResourceException missingRes){
			//TODO error
			return false;
		}
		try{
			if (tmpEvent!= null){//just in case the assignment failed
				((EventManager)getNode(eventNodeType)).fireEvent(tmpEvent);

			}
		}
		catch (IllegalStateException illegalState){
			//the queue was full
			//TODO error
			return false;
		}
		return true;
	}

	/**
	 * Returns true if a node exists with the given type
	 *  (for example: "Graphics")
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
		if (!isLoaded(toUnload)){
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
						//TODO error
						messageValid = false;
					}
					catch (ClassCastException classCast){
						//TODO error
						messageValid = false;
					}

					if (messageValid){
						/*
						 * Tries to send the event. If the return value is false,
						 * it failed and therefore we must load manually
						 */
						if (!fireEvent(toUnload, toSend)){
							loadedNodes.get(toUnload).disable();
						}
					}
					else {
						//errors creating message so the event would not work
						loadedNodes.get(toUnload).disable();
					}
				}
				else{
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
						+ resourceBundle.getString("ARG_DISABLE");
			}
			catch (MissingResourceException missingResource){
				//TODO error
				messageValid = false;
			}
			catch (ClassCastException classCast){
				//TODO error
				messageValid = false;
			}

			if (messageValid){
				/*
				 * Tries to send the event. If the return value is false,
				 * it failed and therefore we must load manually
				 */
				if (!fireEvent(toUnload, toSend)){
					loadedNodes.get(toUnload).onUnload();
				}
			}
			else {
				//errors creating message so the event would not work
				loadedNodes.get(toUnload).onUnload();
			}
		}
		else{
			loadedNodes.get(toUnload).onUnload();
		}

		loadedNodes.remove(toUnload);
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
			return;
		}
		unloadNode(type);
	}
}
