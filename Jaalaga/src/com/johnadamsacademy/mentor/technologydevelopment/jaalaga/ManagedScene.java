package com.johnadamsacademy.mentor.technologydevelopment.jaalaga;

import org.andengine.entity.scene.Scene;

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

/**
 *
 * @author Charles Cozad
 */
public abstract class ManagedScene extends Scene {
	
	// ====== Private Fields ======
	private boolean hasLoadingScreen;
	private float minLoadingScreenTime;
	private float elapsedLoadingScreenTime;
	private boolean isLoaded;
	private JaalagaResourceManager resourceManager;
	private JaalagaSceneManager sceneManager;
	
	// ====== Constructors ======
	// Convenience constructor that disables the loading screen.
	public ManagedScene(JaalagaResourceManager resourceManager, JaalagaSceneManager sceneManager) {
		this(resourceManager, sceneManager, 0f);
	}
	
	// Constructor that sets the minimum length of the loading screen and sets hasLoadingScreen accordingly.
	public ManagedScene(
			JaalagaResourceManager resourceManager, 
			JaalagaSceneManager sceneManager,
			final float pLoadingScreenMinimumSecondsShown) {
		this.minLoadingScreenTime = pLoadingScreenMinimumSecondsShown;
		this.hasLoadingScreen = (minLoadingScreenTime > 0f);
		this.elapsedLoadingScreenTime = 0f;
		this.isLoaded = false;
		this.resourceManager = resourceManager;
		this.sceneManager = sceneManager;
	}
	
	// ====== Getter & Setter Methods ======
	public float getElapsedLoadingScreenTime() {
		return this.elapsedLoadingScreenTime;
	}
	
	public float getMinLoadingScreenTime() {
		return this.minLoadingScreenTime;
	}
	
	public boolean hasLoadingScreen() {
		return this.hasLoadingScreen;
	}
	
	public void increaseElapsedTimeBy(float elapsedTime) {
		this.elapsedLoadingScreenTime += elapsedTime;
	}
	
	public boolean isLoaded() {
		return this.isLoaded;
	}
	
	public void resetElapsedTime() {
		this.elapsedLoadingScreenTime = 0.0f;
	}
	
	public void setMinLoadingScreenTime(float newTime) {
		this.minLoadingScreenTime = newTime;
		this.hasLoadingScreen = (this.minLoadingScreenTime > 0f);
	}
	
	// ====== Behavior Methods ======
	// Called by the Scene Manager. It calls onLoadScene if loading is needed, sets the isLoaded status, and pauses the scene while it's not shown.
	public void onLoadManagedScene() {
		if(!isLoaded) {
			onLoadScene();
			isLoaded = true;
			this.setIgnoreUpdate(true);
		}
	}
	// Called by the Scene Manager. It calls onUnloadScene if the scene has been previously loaded and sets the isLoaded status.
	public void onUnloadManagedScene() {
		if(isLoaded) {
			isLoaded = false;
			onUnloadScene();
		}
	}
	// Called by the Scene Manager. It unpauses the scene before showing it.
	public void onShowManagedScene() {
		this.setIgnoreUpdate(false);
		onShowScene();
	}
	// Called by the Scene Manager. It pauses the scene before hiding it.
	public void onHideManagedScene() {
		this.setIgnoreUpdate(true);
		onHideScene();
	}
	
	public JaalagaResourceManager getResourceManager() {
		return this.resourceManager;
	}
	
	public JaalagaSceneManager getSceneManager() {
		return this.sceneManager;
	}
	
	// ====== Abstract Methods ======
	public abstract Scene onLoadingScreenLoadAndShown();
	public abstract void onLoadingScreenUnloadAndHidden();
	public abstract void onLoadScene();
	public abstract void onShowScene();
	public abstract void onHideScene();
	public abstract void onUnloadScene();
}