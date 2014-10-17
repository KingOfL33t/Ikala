package com.ikalagaming.core;

import java.util.HashMap;
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
		if (loadedNodes.containsKey(toLoad.getType())){
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

		String eventNodeType = "";//name of the event node


		//load it
		if (NodeSettings.USE_EVENTS_FOR_ACCESS
				&& NodeSettings.USE_EVENTS_FOR_ON_LOAD){
			if (eventNodeType.isEmpty()){
				eventNodeType = ResourceBundle.getBundle(
						ResourceLocation.EventManager,
						Localization.getLocale()).getString("nodeType");
			}
			if (isLoaded(eventNodeType)){
				//build a new event from the manager to the event manager
				//telling it to call its onLoad method
				NodeEvent tmpEvent = new NodeEvent(
						resourceBundle.getString("NODE_MANAGER_NAME"),
						eventNodeType,
						resourceBundle.getString("CMD_CALL")
						+ " "
						+ resourceBundle.getString("ARG_ON_LOAD"));

				((EventManager)getNode(eventNodeType)).fireEvent(tmpEvent);
			}
			else {
				//direct call since the event system is down
				toLoad.onLoad();
			}
		}
		else{
			toLoad.onLoad();
		}

		//enable the node
		if (NodeSettings.ENABLE_ON_LOAD){
			if (NodeSettings.USE_EVENTS_FOR_ACCESS
					&& NodeSettings.USE_EVENTS_FOR_ENABLE){
				if (eventNodeType.isEmpty()){
					eventNodeType = ResourceBundle.getBundle(
							ResourceLocation.EventManager,
							Localization.getLocale()).getString("nodeType");
				}
				if (isLoaded(eventNodeType)){
					NodeEvent tmpEvent = new NodeEvent(
							resourceBundle.getString("NODE_MANAGER_NAME"),
							eventNodeType,
							resourceBundle.getString("CMD_CALL")
							+ " "
							+ resourceBundle.getString("ARG_ENABLE"));
					((EventManager)getNode(eventNodeType)).fireEvent(tmpEvent);
				}
				else{
					//no event system is loaded. load via direct call.
					toLoad.enable();
				}
			}
			else{
				toLoad.enable();
			}
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
					String eventNodeType = ResourceBundle.getBundle(
							ResourceLocation.EventManager,
							Localization.getLocale()).getString("nodeType");

					if (isLoaded(eventNodeType)){
						NodeEvent tmpEvent = new NodeEvent(
								resourceBundle.getString("NODE_MANAGER_NAME"),
								eventNodeType,
								resourceBundle.getString("CMD_CALL")
								+ " "
								+ resourceBundle.getString("ARG_ON_UNLOAD"));
						((EventManager)
								getNode(eventNodeType)).fireEvent(tmpEvent);
					}
					else{
						//no event system is loaded. unload via direct call.
						loadedNodes.get(toUnload).disable();
					}
				}
				else{
					loadedNodes.get(toUnload).disable();
				}
			}
		}
		unloadNode(loadedNodes.get(toUnload));
		return true;
	}

	/**
	 * Attempts to unload the node from memory. Does nothing if the node is
	 * not loaded. Nodes are disabled before unloading.
	 * @param toUnload The type of node to unload
	 */
	public void unloadNode(Node toUnload){
		//TODO just call the unloadNOde(String) method. no need to repeat code
		/* using a string the nodes type to ensure
		 * the node that is stored in this class is modified and not just
		 * the node passed to the method.
		 */
		String type = toUnload.getType();
		if (!isLoaded(type)){
			return;
		}
		if (NodeSettings.DISABLE_ON_UNLOAD){
			if (loadedNodes.get(type).isEnabled()){
				if (NodeSettings.USE_EVENTS_FOR_ACCESS &&
						NodeSettings.USE_EVENTS_FOR_DISABLE){
					String eventNodeType = ResourceBundle.getBundle(
							ResourceLocation.EventManager,
							Localization.getLocale()).getString("nodeType");
					if (isLoaded(eventNodeType)){
						NodeEvent tmpEvent = new NodeEvent(
								resourceBundle.getString("NODE_MANAGER_NAME"),
								eventNodeType,
								resourceBundle.getString("CMD_CALL")
								+ " "
								+ resourceBundle.getString("ARG_ON_UNLOAD"));
						((EventManager)
								getNode(eventNodeType)).fireEvent(tmpEvent);
					}
					else{
						//no event system is loaded. unload via direct call.
						loadedNodes.get(type).disable();
					}
				}
				else{
					loadedNodes.get(type).disable();
				}
			}
		}
		loadedNodes.get(type).onUnload();
		loadedNodes.remove(type);
	}
}
