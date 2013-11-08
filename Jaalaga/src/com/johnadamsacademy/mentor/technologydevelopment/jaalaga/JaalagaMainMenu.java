package com.johnadamsacademy.mentor.technologydevelopment.jaalaga;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ColorMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.color.Color;

import android.opengl.GLES20;
import android.view.KeyEvent;

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

public class JaalagaMainMenu extends SimpleBaseGameActivity 
	implements IOnMenuItemClickListener {
	
	// ====== Constants ======
	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 480;

	protected static final int MENU_RESET = 0;
	protected static final int MENU_QUIT = MENU_RESET + 1;

	// ====== Private Fields ======
	private Camera mCamera;
	private BitmapTextureAtlas mBitmapTextureAtlas;
	private ITextureRegion mFaceTextureRegion;
	private Font mFont;
	
	// ====== Protected Fields ======
	protected Scene mMainScene;
	protected MenuScene mMenuScene;

	// ====== Constructors ======

	// ====== Getter & Setter Methods ======
	
	// ====== Behavior Methods ======
	protected MenuScene createMenuScene() {
		final MenuScene menuScene = new MenuScene(this.mCamera);

		final IMenuItem resetMenuItem = new ColorMenuItemDecorator(
				new TextMenuItem(
						MENU_RESET, // ID
						this.mFont, // Font
						"RESET",  // Text
						this.getVertexBufferObjectManager()), 
				new Color(1,0,0), 
				new Color(0,0,0));
		resetMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.addMenuItem(resetMenuItem);

		final IMenuItem quitMenuItem = new ColorMenuItemDecorator(
				new TextMenuItem(
						MENU_QUIT, // ID
						this.mFont, // Font
						"QUIT", // Text
						this.getVertexBufferObjectManager()), 
				new Color(1,0,0), 
				new Color(0,0,0));
		quitMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.addMenuItem(quitMenuItem);

		menuScene.buildAnimations();

		menuScene.setBackgroundEnabled(false);

		menuScene.setOnMenuItemClickListener(this);
		return menuScene;
	}
	
	// ====== Super Class Method Overrides ======
	@Override
	public EngineOptions onCreateEngineOptions() {
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(
				true, // Full Screen
				ScreenOrientation.LANDSCAPE_FIXED,
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), 
				this.mCamera);
	}

	@Override
	public void onCreateResources() {
		FontFactory.setAssetBasePath("font/");

		final ITexture fontTexture = new BitmapTextureAtlas(
				this.getTextureManager(), 
				256, // Width
				256, // Height
				TextureOptions.BILINEAR);
		this.mFont = FontFactory.createFromAsset(
				this.getFontManager(), 
				fontTexture, 
				this.getAssets(), 
				"Plok.ttf", // Asset path
				48, // Font size
				true, // Anti-Alias
				android.graphics.Color.WHITE);
		this.mFont.load();

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(
				this.getTextureManager(), 
				64, // Width
				64, // Height
				TextureOptions.BILINEAR);
		this.mFaceTextureRegion = 
				BitmapTextureAtlasTextureRegionFactory.createFromAsset(
						this.mBitmapTextureAtlas, 
						this, 
						"face_box_menu.png", // Asset Path
						0, // X
						0); // Y
		this.mBitmapTextureAtlas.load();
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mMenuScene = this.createMenuScene();

		/* Just a simple scene with an animated face flying around. */
		this.mMainScene = new Scene();
		this.mMainScene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));

		final Sprite face = new Sprite(
				0, // X
				0, // Y
				this.mFaceTextureRegion, 
				this.getVertexBufferObjectManager());
		face.registerEntityModifier(
				new MoveModifier(
						30, // Duration
						0, // From X
						CAMERA_WIDTH - face.getWidth(), // To X
						0, // From Y
						CAMERA_HEIGHT - face.getHeight())); // To Y
		this.mMainScene.attachChild(face);

		return this.mMainScene;
	}

	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if(pKeyCode == KeyEvent.KEYCODE_MENU 
				&& pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			if(this.mMainScene.hasChildScene()) {
				/* Remove the menu and reset it. */
				this.mMenuScene.back();
			} else {
				/* Attach the menu. */
				this.mMainScene.setChildScene(this.mMenuScene, false, true, true);
			}
			return true;
		} else {
			return super.onKeyDown(pKeyCode, pEvent);
		}
	}

	@Override
	public boolean onMenuItemClicked(
			final MenuScene pMenuScene, 
			final IMenuItem pMenuItem, 
			final float pMenuItemLocalX, 
			final float pMenuItemLocalY) {
		switch(pMenuItem.getID()) {
			case MENU_RESET:
				/* Restart the animation. */
				this.mMainScene.reset();

				/* Remove the menu and reset it. */
				this.mMainScene.clearChildScene();
				this.mMenuScene.reset();
				return true;
			case MENU_QUIT:
				/* End Activity. */
				this.finish();
				return true;
			default:
				return false;
		}
	}

	// ====== Inner and Anonymous Classes ======
}