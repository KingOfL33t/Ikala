package com.ikalagaming.core;

import com.ikalagaming.entity.Entity;
import com.ikalagaming.packages.rng.RngPackageMain;
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
		spawnSomeCubes();
	}

	private void spawnSomeCubes() {
		RngPackageMain pack =
				(RngPackageMain) Game.getPackageManager().getPackage("rng");
		boolean created = false;
		if (pack == null || !pack.isEnabled()) {
			pack = new RngPackageMain();
			pack.enable();
			created = true;
		}
		float x, y, z;
		final int range = 10;
		for (int i = 0; i < 50; ++i) {
			Entity testEntity = new Entity("test");
			testEntity.init(this);
			this.rootNode.attachChild(testEntity.getRoot());

			x = pack.getFloat() * range;
			y = pack.getFloat() * range;
			z = pack.getFloat() * range;
			if (pack.getBoolean()) {
				x = -x;
			}
			if (pack.getBoolean()) {
				y = -y;
			}
			if (pack.getBoolean()) {
				z = -z;
			}

			testEntity.getRoot().move(x, y, z);

		}
		if (created) {
			pack.disable();
			pack.onUnload();
			pack = null;
		}
	}
}
