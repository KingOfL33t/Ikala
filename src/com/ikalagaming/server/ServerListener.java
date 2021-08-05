package com.ikalagaming.server;

import com.ikalagaming.entity.powers.PowerDatabase;
import com.ikalagaming.event.EventHandler;
import com.ikalagaming.event.EventManager;
import com.ikalagaming.event.Listener;
import com.ikalagaming.plugins.PluginManager;
import com.ikalagaming.plugins.events.PluginCommandSent;
import com.ikalagaming.server.events.ServerShutdown;
import com.ikalagaming.server.events.ServerStarted;

public class ServerListener implements Listener {

	private PowerDatabase powerDatabase;

	private ServerController server;

	/**
	 * Constructs a listener that handles events for a given controller plugin.
	 *
	 * @param owner the server controller to listen to events for
	 */
	public ServerListener(ServerController owner) {
		this.server = owner;
	}

	@EventHandler
	public void onPluginCommand(PluginCommandSent event) {
		if (event.getCommand().equalsIgnoreCase("shutdown")) {
			// TODO check permissions
			new ServerShutdown().fire();;
		}

	}

	@EventHandler
	public void onServerShutdown(ServerShutdown event) {
		PluginManager.getInstance().unloadPlugin(this.powerDatabase.getName());
		this.powerDatabase = null;// dereference
		PluginManager.getInstance().unloadPlugin(this.server.PLUGIN_NAME);
		this.server = null;
		// TODO unload plugins
		EventManager.getInstance().unregisterEventListeners(this);
	}

	@EventHandler
	public void onServerStarted(ServerStarted event) {
		// TODO load plugins
		this.powerDatabase = new PowerDatabase();
		//PluginManager.getInstance().loadPlugin(this.powerDatabase.getName());
		PluginManager.getInstance().enable(this.powerDatabase.getName());
	}
}
