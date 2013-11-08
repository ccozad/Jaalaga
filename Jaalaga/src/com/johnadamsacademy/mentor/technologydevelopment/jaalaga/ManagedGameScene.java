package com.johnadamsacademy.mentor.technologydevelopment.jaalaga;

import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;

import android.opengl.GLES20;

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

public abstract class ManagedGameScene extends ManagedScene {
	private Sprite shipSprite;
	private PhysicsHandler physicsHandler;
	AnalogOnScreenControl analogOnScreenControl;
	private int speed;
	
	public ManagedGameScene(JaalagaResourceManager resourceManager, JaalagaSceneManager sceneManager) {
		// Let the Scene Manager know that we want to show a Loading Scene for at least 2 seconds.
		this(resourceManager, sceneManager, 2f);
	};
	
	public ManagedGameScene(
			JaalagaResourceManager resourceManager, 
			JaalagaSceneManager sceneManager,
			float pLoadingScreenMinimumSecondsShown) {
		super(resourceManager, sceneManager, pLoadingScreenMinimumSecondsShown);
		// Setup the touch attributes for the Game Scenes.
		this.setOnSceneTouchListenerBindingOnActionDownEnabled(true);
		this.setTouchAreaBindingOnActionDownEnabled(true);
		this.setTouchAreaBindingOnActionMoveEnabled(true);
		//TODO What speed gives good game play?
		speed = 200;
	}
	
	// These objects will make up our loading scene.
	private Text loadingText;
	private Scene loadingScene;
	@Override
	public Scene onLoadingScreenLoadAndShown() {
		// Setup and return the loading screen.
		loadingScene = new Scene();
		loadingScene.setBackgroundEnabled(true);
		loadingText = new Text(
				0,
				0,
				this.getResourceManager().getMediumFont(),
				"Loading...",
				this.getResourceManager().getEngine().getVertexBufferObjectManager());
		loadingText.setPosition(
				this.getResourceManager().getCameraWidth()/2 - loadingText.getWidth()/2f, 
				this.getResourceManager().getCameraHeight()/2 - loadingText.getHeight()/2f);
		loadingScene.attachChild(loadingText);
		return loadingScene;
	}

	@Override
	public void onLoadingScreenUnloadAndHidden() {
		// detach the loading screen resources.
		loadingText.detachSelf();
		loadingText = null;
		loadingScene = null;
	}
	
	@Override
	public void onLoadScene() {
		// Load the resources to be used in the Game Scenes.
		this.getResourceManager().loadGameResources();
		this.setBackground(new Background(0.0f, 0.0f, 0.0f));
		
		this.shipSprite = new Sprite(
				0,
				0,
				this.getResourceManager().getShip(),
				this.getResourceManager().getEngine().getVertexBufferObjectManager());
		this.shipSprite.setPosition(
				this.getResourceManager().getCameraWidth()/2 - this.shipSprite.getWidth()/2.0f, 
				this.getResourceManager().getCameraHeight() - 184);
		this.physicsHandler = new PhysicsHandler(this.shipSprite);
		this.shipSprite.registerUpdateHandler(physicsHandler);
		this.attachChild(this.shipSprite);
		
		this.analogOnScreenControl = new AnalogOnScreenControl(
				0, 
				this.getResourceManager().getCameraHeight() - this.getResourceManager().getOnScreenControlBase().getHeight(), 
				this.getResourceManager().getEngine().getCamera(), 
				this.getResourceManager().getOnScreenControlBase(), 
				this.getResourceManager().getOnScreenControlKnob(), 
				0.1f, 
				200, 
				this.getResourceManager().getEngine().getVertexBufferObjectManager(), 
				new IAnalogOnScreenControlListener() {
					@Override
					public void onControlChange(
							final BaseOnScreenControl pBaseOnScreenControl, 
							final float pValueX, 
							final float pValueY) {
						//TODO How can we limit to left and right movement?
						physicsHandler.setVelocity(pValueX * speed, pValueY * speed);
					}
		
					@Override
					public void onControlClick(final AnalogOnScreenControl pAnalogOnScreenControl) {
						//face.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(0.25f, 1, 1.5f), new ScaleModifier(0.25f, 1.5f, 1)));
					}
				}
		);
		
		analogOnScreenControl.getControlBase().setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		analogOnScreenControl.refreshControlKnobPosition();

		this.attachChild(analogOnScreenControl);
		int touchAreaCount = analogOnScreenControl.getTouchAreas().size();
		for(int i = 0; i < touchAreaCount; i++) {
			this.registerTouchArea(analogOnScreenControl.getTouchAreas().get(i));
		}
	}
	
	@Override
	public void onShowScene() {
		// We want to wait to set the HUD until the scene is shown because otherwise it will appear on top of the loading screen.
		//this.getResourceManager().getEngine().getCamera().setHUD(GameHud);
	}
	
	@Override
	public void onHideScene() {
		this.getResourceManager().getEngine().getCamera().setHUD(null);
	}
	
	@Override
	public void onUnloadScene() {
		// detach and unload the scene.
		this.getResourceManager().getEngine().runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				detachChildren();
				clearEntityModifiers();
				clearTouchAreas();
				clearUpdateHandlers();
			}});
	}
}