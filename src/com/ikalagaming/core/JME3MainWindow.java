package com.ikalagaming.core;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

public class JME3MainWindow extends SimpleApplication {

	/*
	 * first of all, use my event system and work on getting my code hooked into
	 * jme code. also I need to start integrating my packages with the graphics
	 * and game environment.
	 */

	@Override
	public void simpleInitApp() {
		Box b = new Box(1, 1, 1); // create cube shape
		// create cube geometry from the shape
		Geometry geom = new Geometry("Box", b);

		// create a simple material
		Material mat =
				new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue); // set color of material to blue
		geom.setMaterial(mat); // set the cube's material
		rootNode.attachChild(geom); // make the cube appear in the scene
	}
}
