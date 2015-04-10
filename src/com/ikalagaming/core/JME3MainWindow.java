package com.ikalagaming.core;

import com.ikalagaming.entity.Entity;
import com.jme3.app.SimpleApplication;

/**
 * A window that represents the main application.
 * 
 * @author Ches Burks
 *
 */
public class JME3MainWindow extends SimpleApplication {

	@Override
	public void simpleInitApp() {
		Entity testEntity = new Entity("test");
		testEntity.init(this);
		rootNode.attachChild(testEntity.getRoot());
	}
}
