package com.johnadamsacademy.mentor.technologydevelopment.jaalaga;

import org.andengine.engine.camera.hud.HUD;

//This software is licensed under The MIT License (MIT)
//
//Copyright (c) 2013 Charles Cozad
//
//Permission is hereby granted, free of charge, to any person obtaining a copy
//of this software and associated documentation files (the "Software"), to deal
//in the Software without restriction, including without limitation the rights
//to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//copies of the Software, and to permit persons to whom the Software is
//furnished to do so, subject to the following conditions:
//
//The above copyright notice and this permission notice shall be included in
//all copies or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//THE SOFTWARE.

public abstract class ManagedLayer extends HUD {
	
	// ====== Public Fields ======
	public boolean hasLoaded = false;
	public boolean unloadOnHidden;
	
	private JaalagaResourceManager resourceManager;
	private JaalagaSceneManager sceneManager;
	
	// ====== Constructors ======
	// Convenience constructor. Creates a layer that does not unload when hidden.
	public ManagedLayer(JaalagaResourceManager resourceManager, JaalagaSceneManager sceneManager) {
		this(resourceManager, sceneManager, false);
	}
	// Constructor. Sets whether the layer will unload when hidden and ensures that there is no background on the layer.
	public ManagedLayer(JaalagaResourceManager resourceManager, JaalagaSceneManager sceneManager, boolean pUnloadOnHidden) {
		unloadOnHidden = pUnloadOnHidden;
		this.setBackgroundEnabled(false);
		this.resourceManager = resourceManager;
		this.sceneManager = sceneManager;
	}
	
	// ====== Behavior Methods ======
	// If the layer is not loaded, load it. Ensure that the layer is not paused.
	public void onShowManagedLayer() {
		if(!hasLoaded) {
			hasLoaded = true;
			onLoadLayer();
		}
		this.setIgnoreUpdate(false);
		onShowLayer();
	}
	// Pause the layer, hide it, and unload it if it needs to be unloaded.
	public void onHideManagedLayer() {
		this.setIgnoreUpdate(true);
		onHideLayer();
		if(unloadOnHidden) {
			onUnloadLayer();
		}
	}
	
	public JaalagaResourceManager getResourceManager() {
		return this.resourceManager;
	}
	
	public JaalagaSceneManager getSceneManager() {
		return this.sceneManager;
	}
	
	// ====== Abstract Methods ======
	public abstract void onLoadLayer();
	public abstract void onShowLayer();
	public abstract void onHideLayer();
	public abstract void onUnloadLayer();
}