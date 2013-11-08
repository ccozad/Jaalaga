package com.johnadamsacademy.mentor.technologydevelopment.jaalaga;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
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
public class JaalagaSceneManager extends Object {
	// ====== Public Fields ======
	private ManagedScene mCurrentScene;
	private boolean isLayerShown;
	private ManagedLayer currentLayer;
	
	// ====== Private Fields ======
	private ManagedScene mNextScene;
	private Engine engine;
	private int mNumFramesPassed;
	private boolean mLoadingScreenHandlerRegistered;
	// An update handler that shows the loading screen of mNextScene before calling it to load.
	private IUpdateHandler loadingScreenHandler;
	private boolean mCameraHadHud;
	private Scene mPlaceholderModalScene;
	private JaalagaResourceManager resourceManager;
	private MainMenu mainMenu;
	
	// ====== Constructors ======
	public JaalagaSceneManager(JaalagaResourceManager resourceManager) {
		this.resourceManager = resourceManager;
		this.engine = this.resourceManager.getEngine();
		this.isLayerShown = false;
		this.mNumFramesPassed = -1;
		this.mLoadingScreenHandlerRegistered = false;
		this.mCameraHadHud = false;
		this.mainMenu = new MainMenu(this.resourceManager, this);
		
		this.loadingScreenHandler = new IUpdateHandler() {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				mNumFramesPassed++;
				mNextScene.increaseElapsedTimeBy(pSecondsElapsed);
				// On the first frame AFTER the loading screen has been shown.
				if(mNumFramesPassed==1) {
					// Hide and unload the previous scene if it exists.
					if(mCurrentScene!=null) {
						mCurrentScene.onHideManagedScene();
						mCurrentScene.onUnloadManagedScene();
					}
					// Load the new scene.
					mNextScene.onLoadManagedScene();
				}
				// On the first frame AFTER the scene has been completely loaded and the loading screen has been shown for its minimum limit.
				if(mNumFramesPassed>1 && mNextScene.getElapsedLoadingScreenTime()>=mNextScene.getMinLoadingScreenTime()) {
					// Remove the loading screen that was set as a child in the showScene() method.
					mNextScene.clearChildScene();
					// Tell the new scene to unload its loading screen.
					mNextScene.onLoadingScreenUnloadAndHidden();
					// Tell the new scene that it is shown.
					mNextScene.onShowManagedScene();
					// Set the new scene to the current scene.
					mCurrentScene = mNextScene;
					// Reset the handler & loading screen variables to be ready for another use.
					mNextScene.resetElapsedTime();
					mNumFramesPassed = -1;
					engine.unregisterUpdateHandler(this);
					mLoadingScreenHandlerRegistered = false;
				}
			}
			
			@Override public void reset() {}
		};
	}
	
	// ====== Getter & Setter Methods ======
	public ManagedScene getCurrentScene() {
		return this.mCurrentScene;
	}
	
	public ManagedLayer getCurrentLayer() {
		return this.currentLayer;
	}
	
	public MainMenu getMainMenu() {
		return this.mainMenu;
	}
	
	public boolean isLayerShown() {
		return this.isLayerShown;
	}
	
	// ====== Behavior Methods ======
	// Initiates the process of switching the current managed scene for a new managed scene.
	public void showScene(final ManagedScene pManagedScene) {
		// Reset the camera. This is automatically overridden by any calls to alter the camera from within a managed scene's onShowScene() method.
		this.engine.getCamera().set(
				0f, 
				0f, 
				this.resourceManager.getCameraWidth(),
				this.resourceManager.getCameraHeight());
		// If the new managed scene has a loading screen.
		if(pManagedScene.hasLoadingScreen()) {
			// Set the loading screen as a modal child to the new managed scene.
			pManagedScene.setChildScene(pManagedScene.onLoadingScreenLoadAndShown(),true,true,true);
			// This if/else block assures that the LoadingScreen Update Handler is only registered if necessary.
			if(mLoadingScreenHandlerRegistered){
				mNumFramesPassed = -1;
				mNextScene.clearChildScene();
				mNextScene.onLoadingScreenUnloadAndHidden();
			} else {
				this.engine.registerUpdateHandler(loadingScreenHandler);
				mLoadingScreenHandlerRegistered = true;
			}
			// Set pManagedScene to mNextScene which is used by the loading screen update handler.
			mNextScene = pManagedScene;
			// Set the new scene as the engine's scene.
			this.engine.setScene(pManagedScene);
			// Exit the method and let the LoadingScreen Update Handler finish the switching.
			return;
		}
		// If the new managed scene does not have a loading screen.
		// Set pManagedScene to mNextScene and apply the new scene to the engine.
		mNextScene = pManagedScene;
		this.engine.setScene(mNextScene);
		// If a previous managed scene exists, hide and unload it.
		if(mCurrentScene!=null)
		{
			mCurrentScene.onHideManagedScene();
			mCurrentScene.onUnloadManagedScene();
		}
		// Load and show the new managed scene, and set it as the current scene.
		mNextScene.onLoadManagedScene();
		mNextScene.onShowManagedScene();
		mCurrentScene = mNextScene;
	}
	
	// Convenience method to quickly show the Main Menu.
	public void showMainMenu() {
		showScene(this.mainMenu);
	}

	// Shows a layer by placing it as a child to the Camera's HUD.
	public void showLayer(final ManagedLayer pLayer, final boolean pSuspendSceneDrawing, final boolean pSuspendSceneUpdates, final boolean pSuspendSceneTouchEvents) {
		// If the camera already has a HUD, we will use it.
		if(this.engine.getCamera().hasHUD()){
			mCameraHadHud = true;
		} else {
			// Otherwise, we will create one to use.
			mCameraHadHud = false;
			HUD placeholderHud = new HUD();
			this.engine.getCamera().setHUD(placeholderHud);
		}
		// If the managed layer needs modal properties, set them.
		if(pSuspendSceneDrawing || pSuspendSceneUpdates || pSuspendSceneTouchEvents) {
			// Apply the managed layer directly to the Camera's HUD
			this.engine.getCamera().getHUD().setChildScene(pLayer, pSuspendSceneDrawing, pSuspendSceneUpdates, pSuspendSceneTouchEvents);
			// Create the place-holder scene if it needs to be created.
			if(mPlaceholderModalScene==null) {
				mPlaceholderModalScene = new Scene();
				mPlaceholderModalScene.setBackgroundEnabled(false);
			}
			// Apply the place-holder to the current scene.
			mCurrentScene.setChildScene(mPlaceholderModalScene, pSuspendSceneDrawing, pSuspendSceneUpdates, pSuspendSceneTouchEvents);
		} else {
			// If the managed layer does not need to be modal, simply set it to the HUD.
			this.engine.getCamera().getHUD().setChildScene(pLayer);
		}
		// Set the camera for the managed layer so that it binds to the camera if the camera is moved/scaled/rotated.
		pLayer.setCamera(this.engine.getCamera());
		// Scale the layer according to screen size.
		pLayer.setScale(this.resourceManager.getCameraScaleFactorX(), this.resourceManager.getCameraScaleFactorY());
		// Let the layer know that it is being shown.
		pLayer.onShowManagedLayer();
		// Reflect that a layer is shown.
		isLayerShown = true;
		// Set the current layer to pLayer.
		currentLayer = pLayer;
	}

	// Hides the open layer if one is open.
	public void hideLayer() {
		if(isLayerShown) {
			// Clear the HUD's child scene to remove modal properties.
			this.engine.getCamera().getHUD().clearChildScene();
			// If we had to use a place-holder scene, clear it.
			if(mCurrentScene.hasChildScene())
				if(mCurrentScene.getChildScene()==mPlaceholderModalScene)
					mCurrentScene.clearChildScene();
			// If the camera did not have a HUD before we showed the layer, remove the place-holder HUD.
			if(!mCameraHadHud)
				this.engine.getCamera().setHUD(null);
			// Reflect that a layer is no longer shown.
			isLayerShown = false;
			// Remove the reference to the layer.
			currentLayer = null;
		}
	}
}
