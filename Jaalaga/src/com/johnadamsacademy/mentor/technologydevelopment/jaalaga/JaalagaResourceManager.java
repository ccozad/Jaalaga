package com.johnadamsacademy.mentor.technologydevelopment.jaalaga;

import java.io.IOException;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.util.color.Color;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

// This software is licensed under The MIT License (MIT)
//
// Copyright (c) 2013 Charles Cozad
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

/**
 * \brief Access point for all of the Jaalaga resources such as scenes, graphics and sounds
 * 
 * \copyright Copyright (c) 2013 Charles Cozad
 *
 * \author Charles Cozad
 */
public class JaalagaResourceManager extends Object {
	private Engine engine;
	private Context context;
	private float cameraWidth;
	private float cameraHeight;
	private int maxPlayerRocketCount;
	
	// Shared Resources
	private ITiledTextureRegion buttonTiledTextureRegion;
	private ITiledTextureRegion arcadeButtonTextureRegion;
	private ITextureRegion logoTextureRegion;
	private ITextureRegion shipTextureRegion;
	private ITextureRegion enemy1TextureRegion;
	private ITextureRegion enemy2TextureRegion;
	private ITextureRegion enemy3TextureRegion;
	private ITextureRegion onScreenControlBaseTextureRegion;
	private ITextureRegion onScreenControlKnobTextureRegion;
	private ITextureRegion playerRocketTextureRegion;
	private Sound clickSound;
	private Font fontDefault32Bold;
	private Font fontDefault72Bold;
	private RocketPool playerRocketPool;
	
	// This variable will be used to revert the TextureFactory's default path when we change it.
	private String mPreviousAssetBasePath = "";

	// ====== Constructors ======
	public JaalagaResourceManager(final Engine pEngine, 
			final Context pContext, 
			final float pCameraWidth, 
			final float pCameraHeight,
			int playerRocketCount){
		engine = pEngine;
		context = pContext;
		cameraWidth = pCameraWidth;
		cameraHeight = pCameraHeight;
		this.maxPlayerRocketCount = playerRocketCount;
	}
	
	// ====== Getter & Setter Methods ======
	
	public ITiledTextureRegion getArcadeButton() {
		return this.arcadeButtonTextureRegion;
	}
	
	public ITiledTextureRegion getButton() {
		return this.buttonTiledTextureRegion;
	}
	
	public Sound getClickSound() {
		return this.clickSound;
	}
	
	public ITextureRegion getLogo() {
		return this.logoTextureRegion;
	}
	
	public Context getContext() {
		return this.context;
	}
	
	public float getCameraWidth() {
		return this.cameraWidth;
	}
	
	public float getCameraHeight() {
		return this.cameraHeight;
	}
	
	public Engine getEngine() {
		return this.engine;
	}
	
	public Font getLargeFont() {
		return this.fontDefault72Bold;
	}
	
	public Font getMediumFont() {
		return this.fontDefault32Bold;
	}
	
	public ITextureRegion getShip() {
		return this.shipTextureRegion;
	}
	
	public ITextureRegion getEnemy1() {
		return this.enemy1TextureRegion;
	}
	
	public ITextureRegion getEnemy2() {
		return this.enemy2TextureRegion;
	}
	
	public ITextureRegion getEnemy3() {
		return this.enemy3TextureRegion;
	}
	
	public ITextureRegion getOnScreenControlBase() {
		return this.onScreenControlBaseTextureRegion;
	}
	
	public ITextureRegion getOnScreenControlKnob() {
		return this.onScreenControlKnobTextureRegion;
	}
	
	public Sprite getPlayerRocket() {
		return this.playerRocketPool.obtainPoolItem();
	}
	
	public int getMaxPlayerRockets() {
		return this.maxPlayerRocketCount;
	}
	
	public int getAvailablePlayerRockets() {
		return this.playerRocketPool.getAvailableItemCount();
	}
	
	public void recyclePlayerRocket(Sprite playerRocket) {
		this.playerRocketPool.recyclePoolItem(playerRocket);
	}
	
	// ====== Private Behavior Methods ======
	
	// Loads all game resources.
	public void loadGameResources() {
		this.loadGameTextures();
		this.loadSharedResources();
	}
	
	// Loads all menu resources
	public void loadMenuResources() {
		this.loadMenuTextures();
		this.loadSharedResources();
	}
	
	// Unloads all game resources.
	public void unloadGameResources() {
		this.unloadGameTextures();
		this.unloadRocketPools();
	}

	// Unloads all menu resources
	public void unloadMenuResources() {
		this.unloadMenuTextures();
	}
	
	// Unloads all shared resources
	public  void unloadSharedResources() {
		this.unloadSharedTextures();
		this.unloadSounds();
		this.unloadFonts();
	}
	
	// Loads resources used by both the game scenes and menu scenes
	private void loadSharedResources(){
		this.loadSharedTextures();
		this.loadSounds();
		this.loadFonts();
	}
	
	//cwc_note Clean up this method, inefficient use of Texture atlases and sloppy code
	private void loadGameTextures(){
		// Store the current asset base path to apply it after we've loaded our textures
		mPreviousAssetBasePath = BitmapTextureAtlasTextureRegionFactory.getAssetBasePath();
		// Set our game assets folder to "assets/gfx/game/"
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");

		// Investigate proper texture packing/loading later
		// TODO Change the game sprites to something more interesting
		if(this.shipTextureRegion == null) {
			BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), 80, 80);
			this.shipTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(texture, context, "ship.png");
			try {
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 4));
				texture.load();
				Log.v("Jaalaga","Ship texture loaded");
			} catch (TextureAtlasBuilderException e) {
				Log.e("Jaalaga","Ship texture load exception:" + e.getMessage());
			}
		}
		
		if(this.enemy1TextureRegion == null) {
			BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), 40, 40);
			this.enemy1TextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(texture, context, "enemy1.png");
			try {
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 4));
				texture.load();
				Log.v("Jaalaga","Enemy 1 texture loaded");
			} catch (TextureAtlasBuilderException e) {
				Log.e("Jaalaga","Enemy 1 texture load exception:" + e.getMessage());
			}
		}
		
		if(this.enemy2TextureRegion == null) {
			BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), 40, 40);
			this.enemy2TextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(texture, context, "enemy2.png");
			try {
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 4));
				texture.load();
				Log.v("Jaalaga","Enemy 2 texture loaded");
			} catch (TextureAtlasBuilderException e) {
				Log.e("Jaalaga","Enemy 2 texture load exception:" + e.getMessage());
			}
		}
		
		if(this.enemy3TextureRegion == null) {
			BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), 40, 40);
			this.enemy3TextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(texture, context, "enemy3.png");
			try {
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 4));
				texture.load();
				Log.v("Jaalaga", "Enemy 3 texture loaded");
			} catch (TextureAtlasBuilderException e) {
				Log.e("Jaalaga","Enemy 3 texture load exception:" + e.getMessage());
			}
		}
		
		BuildableBitmapTextureAtlas controlTextureAtlas = new BuildableBitmapTextureAtlas(engine.getTextureManager(), 280, 150);
		this.onScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(controlTextureAtlas, context, "onscreen_control_base.png");
		this.onScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(controlTextureAtlas, context, "onscreen_control_knob.png");
		try {
			controlTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 4));
			controlTextureAtlas.load();
			Log.v("Jaalaga","Analog Control texture loaded");
		} catch (TextureAtlasBuilderException e) {
			Log.e("Jaalaga"," Analog Control texture load exception:" + e.getMessage());
		}
		controlTextureAtlas.load();
		
		if(this.arcadeButtonTextureRegion == null) {
			BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), 264, 136);
			this.arcadeButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(texture, context, "arcadeButtonRed.png", 2, 1);
			try {
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
				texture.load();
				Log.v("Jaalaga","Arcade Button texture loaded");
			} catch (TextureAtlasBuilderException e) {
				Log.e("Jaalaga","Arcade Button texture load exception:" + e.getMessage());
			}
		}

		// Revert the Asset Path.
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath(mPreviousAssetBasePath);
	}

	private void unloadGameTextures(){
		// background texture - only unload it if it is loaded:
		
		if(this.shipTextureRegion != null) {
			if(this.shipTextureRegion.getTexture().isLoadedToHardware()) {
				this.shipTextureRegion.getTexture().unload();
				this.shipTextureRegion = null;
				Log.v("Jaalaga","Ship texture unloaded");
			}
		}
		
		if(this.enemy1TextureRegion != null) {
			if(this.enemy1TextureRegion.getTexture().isLoadedToHardware()) {
				this.enemy1TextureRegion.getTexture().unload();
				this.enemy1TextureRegion = null;
				Log.v("Jaalaga","Enemy 1 texture unloaded");
			}
		}
		
		if(this.enemy2TextureRegion != null) {
			if(this.enemy2TextureRegion.getTexture().isLoadedToHardware()) {
				this.enemy2TextureRegion.getTexture().unload();
				this.enemy2TextureRegion = null;
				Log.v("Jaalaga","Enemy 2 texture unloaded");
			}
		}
		
		if(this.enemy3TextureRegion != null) {
			if(this.enemy3TextureRegion.getTexture().isLoadedToHardware()) {
				this.enemy3TextureRegion.getTexture().unload();
				this.enemy3TextureRegion = null;
				Log.v("Jaalaga","Enemy 3 texture unloaded");
			}
		}
		
		if(this.arcadeButtonTextureRegion !=null) {
			if(this.arcadeButtonTextureRegion.getTexture().isLoadedToHardware()) {
				this.arcadeButtonTextureRegion.getTexture().unload();
				this.arcadeButtonTextureRegion = null;
				Log.v("Jaalaga","Arcade Button texture unloaded");
			}
		}
	}

	private void loadMenuTextures(){
		// Store the current asset base path to apply it after we've loaded our textures
		mPreviousAssetBasePath = BitmapTextureAtlasTextureRegionFactory.getAssetBasePath();
		// Set our menu assets folder to "assets/gfx/menu/"
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
		
		// logo texture:
		//TODO Change the logo sprite to something more interesting
		if(this.logoTextureRegion==null) {
			BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), 256, 256);
			this.logoTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(texture, context, "logo.png");
			try {
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 4));
				texture.load();
				Log.v("Jaalaga","Logo texture loaded");
			} catch (TextureAtlasBuilderException e) {
				Log.e("Jaalaga","Logo texture load exception:" + e.getMessage());
			}
		}
		
		// Revert the Asset Path.
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath(mPreviousAssetBasePath);
	}

	private void unloadMenuTextures(){
		// logo texture:
		if(this.logoTextureRegion!=null) {
			if(this.logoTextureRegion.getTexture().isLoadedToHardware()) {
				this.logoTextureRegion.getTexture().unload();
				this.logoTextureRegion = null;
				Log.v("Jaalaga","Logo texture unloaded");
			}
		}
	}
	
	private void loadSharedTextures(){
		// Store the current asset base path to apply it after we've loaded our textures
		mPreviousAssetBasePath = BitmapTextureAtlasTextureRegionFactory.getAssetBasePath();
		// Set our shared assets folder to "assets/gfx/"
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		// button texture:
		if(buttonTiledTextureRegion==null) {
			BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), 522, 74);
			buttonTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(texture, context, "button01.png", 2, 1);
			try {
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 4));
				texture.load();
				Log.v("Jaalaga","Button texture loaded");
			} catch (TextureAtlasBuilderException e) {
				Log.e("Jaalaga","Button texture loaded exception:" + e.getMessage());
			}
		}

		// Revert the Asset Path.
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath(mPreviousAssetBasePath);
	}

	private void unloadSharedTextures(){
		// button texture:
		if(buttonTiledTextureRegion!=null) {
			if(buttonTiledTextureRegion.getTexture().isLoadedToHardware()) {
				buttonTiledTextureRegion.getTexture().unload();
				buttonTiledTextureRegion = null;
				Log.v("Jaalaga","Button texture unloaded");
			}
		}
	}
	
	private void loadSounds(){
		SoundFactory.setAssetBasePath("sounds/");
		if(clickSound==null) {
			try {
				// Create the clickSound object via the SoundFactory class
				clickSound	= SoundFactory.createSoundFromAsset(engine.getSoundManager(), context, "click.mp3");
				Log.v("Jaalaga", "Click sound loaded");
			} catch (final IOException e) {
				Log.e("Jaalaga","Click sound load Exception:" + e.getMessage());
			}
		}
	}

	private void unloadSounds(){
		if(clickSound!=null)
			if(clickSound.isLoaded()) {
				// Unload the clickSound object. Make sure to stop it first.
				clickSound.stop();
				engine.getSoundManager().remove(clickSound);
				clickSound = null;
				Log.v("Jaalaga", "Click sound unloaded");
			}
	}

	private void loadFonts(){
		// Create the Font objects via FontFactory class
		if(fontDefault32Bold==null) {
			fontDefault32Bold = FontFactory.create(engine.getFontManager(), engine.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD),  32f, true, Color.WHITE_ARGB_PACKED_INT);
			fontDefault32Bold.load();
			Log.v("Jaalaga", "Default 32 Bold font loaded");
		}
		if(fontDefault72Bold==null) {
			fontDefault72Bold = FontFactory.create(engine.getFontManager(), engine.getTextureManager(), 512, 512, Typeface.create(Typeface.DEFAULT, Typeface.BOLD),  72f, true, Color.WHITE_ARGB_PACKED_INT);
			fontDefault72Bold.load();
			Log.v("Jaalaga", "Default 72 Bold font loaded");
		}
	}
	
	private void unloadFonts(){
		// Unload the fonts
		if(fontDefault32Bold!=null) {
			fontDefault32Bold.unload();
			fontDefault32Bold = null;
			Log.v("Jaalaga", "Default 32 Bold font loaded");
		}
		if(fontDefault72Bold!=null) {
			fontDefault72Bold.unload();
			fontDefault72Bold = null;
			Log.v("Jaalaga", "Default 72 Bold font loaded");
		}
	}
	
	public void loadRocketPools() {
		// Store the current asset base path to apply it after we've loaded our textures
		mPreviousAssetBasePath = BitmapTextureAtlasTextureRegionFactory.getAssetBasePath();
		// Set our game assets folder to "assets/gfx/game/"
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
		
		
		if(this.playerRocketTextureRegion == null) {
			BuildableBitmapTextureAtlas texture = new BuildableBitmapTextureAtlas(engine.getTextureManager(), 25, 25);
			this.playerRocketTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(texture, context, "playerRocket.png");
			try {
				texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 4));
				texture.load();
				Log.v("Jaalaga","Player Rocket texture loaded");
				if(this.playerRocketPool == null) {
					this.playerRocketPool = new RocketPool(
							this.maxPlayerRocketCount, 
							this.playerRocketTextureRegion, 
							this.engine.getVertexBufferObjectManager());
					Log.v("Jaalaga","Player Rocket Pool loaded");
				}
			} catch (TextureAtlasBuilderException e) {
				Log.e("Jaalaga","Player Rocket texture load exception:" + e.getMessage());
			}
		}
		
		// Revert the Asset Path.
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath(mPreviousAssetBasePath);
	}
	
	private void unloadRocketPools() {
		this.playerRocketTextureRegion.getTexture().unload();
		this.playerRocketTextureRegion = null;
		Log.v("Jaalaga","Player Rocket texture unloaded");
		this.playerRocketPool = null;
		Log.v("Jaalaga","Player Rocket Pool unloaded");
	}
}
