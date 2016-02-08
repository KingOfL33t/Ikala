package com.ikalagaming.server;

import com.ikalagaming.entity.powers.PowerDatabase;
import com.ikalagaming.event.EventHandler;
import com.ikalagaming.event.EventManager;
import com.ikalagaming.event.Listener;
import com.ikalagaming.packages.PackageManager;
import com.ikalagaming.packages.events.PackageCommandSent;
import com.ikalagaming.server.events.ServerShutdown;
import com.ikalagaming.server.events.ServerStarted;

public class ServerListener implements Listener {

	private PowerDatabase powerDatabase;

	private ServerController server;

	/**
	 * Constructs a listener that handles events for a given controller package.
	 *
	 * @param owner the server controller to listen to events for
	 */
	public ServerListener(ServerController owner) {
		this.server = owner;
	}

	@EventHandler
	public void onPackageCommand(PackageCommandSent event) {
		if (!event.getTo().equals(this.server.getName())) {
			return;
		}
		if (event.getCommand().equalsIgnoreCase("shutdown")) {
			// TODO check permissions
			EventManager.getInstance().fireEvent(new ServerShutdown());
		}

	}

	@EventHandler
	public void onServerShutdown(ServerShutdown event) {
		PackageManager.getInstance().unloadPackage(this.powerDatabase);
		this.powerDatabase = null;// dereference
		PackageManager.getInstance().unloadPackage(this.server);
		this.server = null;
		// TODO unload packages
		EventManager.getInstance().unregisterEventListeners(this);
	}

	@EventHandler
	public void onServerStarted(ServerStarted event) {
		// TODO load packages
		this.powerDatabase = new PowerDatabase();
		PackageManager.getInstance().loadPackage(this.powerDatabase);
		PackageManager.getInstance().enable(this.powerDatabase);
	}
}
