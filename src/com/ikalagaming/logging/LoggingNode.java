package com.ikalagaming.logging;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.ikalagaming.core.Localization;
import com.ikalagaming.core.Node;
import com.ikalagaming.core.NodeManager;
import com.ikalagaming.core.ResourceLocation;

/**
 * Handles reporting and logging errors.
 *
 * @author Ches Burks
 *
 */
public class LoggingNode implements Node {

	private ResourceBundle resourceBundle;
	private boolean enabled = false;
	private final double version = 0.1;
	private NodeManager nodeManager;
	/**
	 * Only logs events that are of this level or higher
	 */
	private LoggingLevel threshold;

	/**
	 * Logs the provided error. Attempts to use localized names for the
	 * error code and logging level. This only logs errors that are above
	 * or equal to the threshold.
	 *
	 * @param eCode The error code
	 * @param level what level is the requested log
	 * @param details additional information about the error
	 */
	public void logError(ErrorCode eCode, LoggingLevel level, String details) {
		if (!enabled){
			System.err.println(eCode.getName()
					+ " "
					+ level.getName()
					+ " "
					+ details);
			return;
		}
		if (level.intValue() < threshold.intValue()) {
			return;
		}
		String errorMessage;
		try {
			errorMessage = ResourceBundle.getBundle(
					ResourceLocation.ErrorCodes, Localization.getLocale())
					.getString(eCode.getName());
		} catch (Exception e) {
			errorMessage = eCode.getName();
		}
		//TODO print to console or files
		try {
			System.err.println(resourceBundle.getString("level_prefix")
					+ level.getLocalizedName()
					+ resourceBundle.getString("level_postfix")
					+ errorMessage);
		}
		catch (Exception e){
			System.err.println(level.getName());
			e.printStackTrace(System.err);//we need to know what broke the log
		}
		System.err.println(details);
	}

	/**
	 * Logs the provided error. Attempts to use localized names for the
	 * logging level. This only logs information that is above
	 * or equal to the logging threshold.
	 *
	 * @param level what level is the requested log
	 * @param details what to log
	 */
	public void log(LoggingLevel level, String details) {
		if (!enabled){
			System.out.println(level.getName() + " " + details);
			return;
		}
		if (level.intValue() < threshold.intValue()) {
			return;
		}
		//TODO print to console or files
		try {
			System.out.println(resourceBundle.getString("level_prefix")
					+ level.getLocalizedName()
					+ resourceBundle.getString("level_postfix")
					+ " "
					+details);
		}
		catch (Exception e){
			System.err.println(level.getName());
			e.printStackTrace(System.err);//we need to know what broke the log
		}
	}

	@Override
	public String getType() {
		String type = "";
		try {
			type = resourceBundle.getString("nodeType");
		} catch (MissingResourceException missingResource) {
			logError(ErrorCode.locale_resource_not_found,
					LoggingLevel.WARNING,
					"LoggingNode.getType()");
		} catch (ClassCastException classCast) {
			logError(ErrorCode.locale_resource_wrong_type,
					LoggingLevel.WARNING,
					"LoggingNode.getType()");
		}
		return type;
	}

	@Override
	public double getVersion() {
		return version;
	}

	@Override
	public boolean enable() {
		this.enabled = true;
		try {
			this.onEnable();
		} catch (Exception e) {
			logError(ErrorCode.node_enable_fail,
					LoggingLevel.SEVERE,
					"LoggingNode.enable()");
			// better safe than sorry (probably did not initialize correctly)
			this.enabled = false;
			return false;
		}
		return true;
	}

	@Override
	public boolean disable() {
		this.enabled = false;
		try {
			this.onDisable();
		} catch (Exception e) {
			logError(ErrorCode.node_disable_fail,
					LoggingLevel.SEVERE,
					"LoggingNode.enable()");
			return false;
		}
		return true;
	}

	@Override
	public boolean reload() {
		if (this.enabled) {
			this.disable();
		}
		this.enable();
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void onEnable() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onLoad() {
		try {
			resourceBundle = ResourceBundle.getBundle(
					ResourceLocation.LoggingNode, Localization.getLocale());
		} catch (MissingResourceException missingResource) {
			logError(ErrorCode.locale_resource_not_found,
					LoggingLevel.SEVERE,
					"LoggingNode.onLoad()");
		}
	}

	@Override
	public void onUnload() {
		this.resourceBundle = null;
		this.nodeManager = null;
	}

	@Override
	public void setNodeManager(NodeManager parent) {
		this.nodeManager = parent;
	}

}
