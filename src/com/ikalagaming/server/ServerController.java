package com.ikalagaming.server;

import java.util.HashSet;
import java.util.Set;

import com.ikalagaming.event.Listener;
import com.ikalagaming.plugins.Plugin;
import com.ikalagaming.plugins.PluginManager;


/**
 * Handles startup, shutdown, and management of the server.
 *
 * @author Ches Burks
 *
 */
public class ServerController implements Plugin {
	private static final String pluginName = "Server Controller";
	private static final double version = 0.1;
	private HashSet<Listener> listeners;
	private ServerListener listener = new ServerListener(this);

	@Override
	public Set<Listener> getListeners() {
		if (this.listeners == null) {
			this.listeners = new HashSet<>();
			this.listeners.add(this.listener);
		}
		return this.listeners;
	}

	@Override
	public String getName() {
		return ServerController.pluginName;
	}

	@Override
	public double getVersion() {
		return ServerController.version;
	}

	@Override
	public boolean onDisable() {
		return true;
	}

	@Override
	public boolean onEnable() {
		return true;
	}

	@Override
	public boolean onLoad() {
		PluginManager.getInstance().registerCommand("shutdown", this);
		return true;
	}

	@Override
	public boolean onUnload() {
		this.listener = null;
		this.listeners.clear();
		this.listeners = null;
		PluginManager.getInstance().unregisterCommand("shutdown");
		return true;
	}

}
