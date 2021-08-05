package com.ikalagaming.entity.effects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.ikalagaming.event.Listener;
import com.ikalagaming.plugins.Plugin;

/**
 * A database of effects that are known to the game.
 * 
 * @author Ches Burks
 *
 */
//TODO move this to a new project
public class EffectDatabase extends Plugin implements Listener {

	/**
	 * The human readable name of the plugin.
	 */
	private static final String pluginName = "Effect Database";
	/**
	 * The current iteration of this power database.
	 */
	private static final double version = 0.1;
	private Set<Listener> listeners;

	private HashMap<String, Effect> effects;

	@Override
	public Set<Listener> getListeners() {
		if (this.listeners == null) {
			this.listeners = new HashSet<>();
			this.listeners.add(this);
		}
		return this.listeners;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return EffectDatabase.pluginName;
	}

	public double getVersion() {
		return EffectDatabase.version;
	}

	@Override
	public boolean onDisable() {
		this.effects.clear();
		return true;
	}

	@Override
	public boolean onEnable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onLoad() {
		this.effects = new HashMap<>();
		return true;
	}

	@Override
	public boolean onUnload() {
		this.effects.clear();
		this.effects = null;
		return false;
	}

	/**
	 * Returns true if an effect exists with the specified name. If this plugin
	 * is not loaded/enabled, it will always return false.
	 * 
	 * @param name the name of the effect
	 * @return true if an effect has that name, false if that is not the name of
	 *         an effect
	 */
	public boolean effectExistsByName(String name) {
		if (name == null) {
			return false;
		}
		if (this.effects == null) {
			return false;// This plugin might not be loaded
		}
		return this.effects.containsKey(name);
	}

	/**
	 * Returns the effect with the given name if one exists. If name is null,
	 * this plugin is not loaded/enabled, or no known effect has that name, it
	 * returns null.
	 * 
	 * @param name the name of the effect
	 * @return the effect with the given name, or null if no such effect is
	 *         found
	 * @see #effectExistsByName(String)
	 */
	public Effect getEffect(String name) {
		if (name == null) {
			return null;
		}
		if (this.effects == null) {
			return null;// This plugin might not be loaded
		}
		if (!this.effects.containsKey(name)) {
			return null;
		}
		return this.effects.get(name);
	}

}
