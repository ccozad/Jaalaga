package com.johnadamsacademy.mentor.technologydevelopment.jaalaga;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.texture.region.ITextureRegion;

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

public class MainMenu extends ManagedMenuScene {
	
	private float screenWidthCenter;
	private float buttonWidthCenter;
	private float buttonHeightCenter;
	private ITextureRegion normalButtonTexture;
	private ITextureRegion pressedButtonTexture;
	// We have a logo and four buttons (Play Game, High Scores, Options, Credits)
	private Sprite logoSprite;
	private ButtonSprite playButton;
	private ButtonSprite highScoresButton;
	private ButtonSprite optionsButton;
	private ButtonSprite creditsButton;
	private Text playButtonText;
	private Text highScoresButtonText;
	private Text optionsButtonText;
	private Text creditsButtonText;
	
	public MainMenu(JaalagaResourceManager resourceManager, JaalagaSceneManager sceneManager) {
		super(resourceManager, sceneManager);
		this.setOnSceneTouchListenerBindingOnActionDownEnabled(true);
		this.setTouchAreaBindingOnActionDownEnabled(true);
		this.setTouchAreaBindingOnActionMoveEnabled(true);
	}
	
	@Override
	public void onLoadScene() {
		this.getResourceManager().loadMenuResources();
		//We are going to need these values several times so we calculate it once and store the result
		screenWidthCenter = this.getResourceManager().getCameraWidth()/2f;
		buttonWidthCenter = this.getResourceManager().getButton().getTextureRegion(0).getWidth()/2f;
		buttonHeightCenter = this.getResourceManager().getButton().getTextureRegion(0).getHeight()/2f;
		normalButtonTexture = this.getResourceManager().getButton().getTextureRegion(0);
		pressedButtonTexture = this.getResourceManager().getButton().getTextureRegion(1);
		
		this.setBackground(new Background(0.0f, 0.0f, 0.0f));
		// Create the logo
		this.logoSprite = new Sprite(
				0,
				0,
				this.getResourceManager().getLogo(),
				this.getResourceManager().getEngine().getVertexBufferObjectManager());
		this.logoSprite.setPosition(screenWidthCenter - this.logoSprite.getWidth()/2.0f, 25);
		this.attachChild(this.logoSprite);
		
		//Setup the play button
		this.playButton = new ButtonSprite(
				0,
				0, 
				this.normalButtonTexture, 
				this.pressedButtonTexture, 
				this.getResourceManager().getEngine().getVertexBufferObjectManager());
		this.playButtonText = new Text(
				0, 
				0, 
				this.getResourceManager().getMediumFont(), 
				"Play Game", 
				this.getResourceManager().getEngine().getVertexBufferObjectManager());
		this.playButtonText.setPosition(
				this.buttonWidthCenter - this.playButtonText.getWidth()/2f, 
				this.buttonHeightCenter - this.playButtonText.getHeight()/2f);
		this.playButton.attachChild(this.playButtonText);
		this.playButton.setPosition(screenWidthCenter - this.playButton.getWidth()/2f, 300);
		this.attachChild(this.playButton);
		this.playButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				// Create a new GameLevel and show it using the SceneManager. And play a click.
				getSceneManager().showScene(new GameLevel(getResourceManager(), getSceneManager()));
				getResourceManager().getClickSound().play();
			}});
		this.registerTouchArea(this.playButton);
		
		//Setup the high scores button
		this.highScoresButton = new ButtonSprite(
				0,
				0, 
				this.normalButtonTexture, 
				this.pressedButtonTexture, 
				this.getResourceManager().getEngine().getVertexBufferObjectManager());
		this.highScoresButtonText = new Text(
				0, 
				0, 
				this.getResourceManager().getMediumFont(), 
				"High Scores", 
				this.getResourceManager().getEngine().getVertexBufferObjectManager());
		this.highScoresButtonText.setPosition(
				this.buttonWidthCenter - this.highScoresButtonText.getWidth()/2f, 
				this.buttonHeightCenter - this.highScoresButtonText.getHeight()/2f);
		this.highScoresButton.attachChild(this.highScoresButtonText);
		this.highScoresButton.setPosition(screenWidthCenter - this.playButton.getWidth()/2f, 375);
		this.attachChild(this.highScoresButton);
		this.highScoresButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				getResourceManager().getClickSound().play();
			}});
		this.registerTouchArea(this.highScoresButton);
		
		//Setup the options button
		this.optionsButton = new ButtonSprite(
				0,
				0, 
				this.normalButtonTexture, 
				this.pressedButtonTexture, 
				this.getResourceManager().getEngine().getVertexBufferObjectManager());
		this.optionsButtonText = new Text(
				0, 
				0, 
				this.getResourceManager().getMediumFont(), 
				"Options", 
				this.getResourceManager().getEngine().getVertexBufferObjectManager());
		this.optionsButtonText.setPosition(
				this.buttonWidthCenter - this.optionsButtonText.getWidth()/2f, 
				this.buttonHeightCenter - this.optionsButtonText.getHeight()/2f);
		this.optionsButton.attachChild(this.optionsButtonText);
		this.optionsButton.setPosition(screenWidthCenter - this.optionsButton.getWidth()/2f, 450);
		this.attachChild(this.optionsButton);
		this.optionsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {;
				getResourceManager().getClickSound().play();
			}});
		this.registerTouchArea(this.optionsButton);
		
		//Setup the credits button
		this.creditsButton = new ButtonSprite(
				0,
				0, 
				this.normalButtonTexture, 
				this.pressedButtonTexture, 
				this.getResourceManager().getEngine().getVertexBufferObjectManager());
		this.creditsButtonText = new Text(
				0, 
				0, 
				this.getResourceManager().getMediumFont(), 
				"Credits", 
				this.getResourceManager().getEngine().getVertexBufferObjectManager());
		this.creditsButtonText.setPosition(
				this.buttonWidthCenter - this.creditsButtonText.getWidth()/2f, 
				this.buttonHeightCenter - this.creditsButtonText.getHeight()/2f);
		this.creditsButton.attachChild(this.creditsButtonText);
		this.creditsButton.setPosition(screenWidthCenter - this.creditsButton.getWidth()/2f, 525);
		this.attachChild(this.creditsButton);
		this.creditsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {;
				getResourceManager().getClickSound().play();
			}});
		this.registerTouchArea(this.creditsButton);
	}
	
	// No loading screen means no reason to use the following methods.
	@Override
	public Scene onLoadingScreenLoadAndShown() {
		return null;
	}
	@Override
	public void onLoadingScreenUnloadAndHidden() { }
	
	@Override
	public void onShowScene() { }
	@Override
	public void onHideScene() { }
	@Override
	public void onUnloadScene() { }
}