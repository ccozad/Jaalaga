package com.johnadamsacademy.mentor.technologydevelopment.jaalaga;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.controller.MultiTouch;
import org.andengine.ui.activity.BaseGameActivity;

import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

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

public class Jaalaga extends BaseGameActivity {

	static float DESIGN_SCREEN_WIDTH_PIXELS = 480f;
	static float DESIGN_SCREEN_HEIGHT_PIXELS = 800f;

	private float cameraWidth;
	private float cameraHeight;
	
	private JaalagaResourceManager resourceManager;
	private JaalagaSceneManager sceneManager;
	
	// If a Layer is open when the Back button is pressed, hide the layer.
	// If a Game scene or non-MainMenu is active, go back to the MainMenu.
	// Otherwise, exit the game.
	// cwc_note: This class knows too much about the internals of the scene manager
	// Have the scene manager expose the desired behaviors instead
	// - Hide the current layer
	// - Determine what the current scene is
	// - In general the scene manager should have a "back()" method
	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if(this.resourceManager.getEngine() != null){
				if(this.sceneManager.isLayerShown())
					this.sceneManager.getCurrentLayer().onHideLayer();
				else if(this.sceneManager.getCurrentScene().getClass().getGenericSuperclass().equals(ManagedGameScene.class) || 
						(this.sceneManager.getCurrentScene().getClass().getGenericSuperclass().equals(ManagedMenuScene.class) &!
								this.sceneManager.getCurrentScene().getClass().equals(MainMenu.class)))
					this.sceneManager.showMainMenu();
				else
					System.exit(0);
			}
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
	
	// ====================================================
	// CREATE ENGINE OPTIONS
	// ====================================================
	@Override
	public EngineOptions onCreateEngineOptions() {
		cameraWidth = DESIGN_SCREEN_WIDTH_PIXELS;
		cameraHeight = DESIGN_SCREEN_HEIGHT_PIXELS;

		EngineOptions engineOptions = new EngineOptions(
				true, 
				ScreenOrientation.PORTRAIT_FIXED, 
				new RatioResolutionPolicy(cameraWidth, cameraHeight), 
				new Camera(0, 0, cameraWidth, cameraHeight));
		
		// We need multi touch so we can fire and move at the same time
		engineOptions.getTouchOptions().setNeedsMultiTouch(true);
		
		if(MultiTouch.isSupported(this)) {
			if(MultiTouch.isSupportedDistinct(this)) {
				Log.v("Jaalaga", "Full multiTouch support detected");
			} else {
				Log.v("Jaalaga", "Limited multiTouch support detected");
			}
		} else {
			Log.v("Jaalaga", "No multiTouch support detected");
		}
		
		// Enable sounds.
		engineOptions.getAudioOptions().setNeedsSound(true);
		// Enable music.
		engineOptions.getAudioOptions().setNeedsMusic(true);
		// Turn on Dithering to smooth texture gradients.
		engineOptions.getRenderOptions().setDithering(true);
		// Turn on MultiSampling to smooth the alias of hard-edge elements.
		engineOptions.getRenderOptions().setMultiSampling(true);
		// Set the Wake Lock options to prevent the engine from dumping textures when focus changes.
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		return engineOptions;
	}
	
	// ====================================================
	// CREATE RESOURCES
	// ====================================================
	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) {
		// Setup the ResourceManager.
		this.resourceManager = new JaalagaResourceManager(this.getEngine(), 
				this.getApplicationContext(), 
				cameraWidth, 
				cameraHeight,
				2);
		this.sceneManager = new JaalagaSceneManager(this.resourceManager);
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	// ====================================================
	// CREATE SCENE
	// ====================================================
	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) {
		// Register an FPSLogger to output the game's FPS during development.
		mEngine.registerUpdateHandler(new FPSLogger());
		// Tell the SceneManager to show the MainMenu.
		this.sceneManager.showMainMenu();
		// Set the MainMenu to the Engine's scene.
		pOnCreateSceneCallback.onCreateSceneFinished(this.sceneManager.getMainMenu());
	}

	// ====================================================
	// POPULATE SCENE
	// ====================================================
	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) {
		// Our SceneManager will handle the population of the scenes, so we do nothing here.
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
}