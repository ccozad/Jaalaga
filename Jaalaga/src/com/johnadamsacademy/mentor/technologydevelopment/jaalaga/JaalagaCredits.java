package com.johnadamsacademy.mentor.technologydevelopment.jaalaga;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;

public class JaalagaCredits extends ManagedLayer {
	
	private String[] codeCredits;
	private String[] audioCredits;
	private String[] graphicsCredits;

	public JaalagaCredits(JaalagaResourceManager resourceManager,
			JaalagaSceneManager sceneManager) {
		super(resourceManager, sceneManager);
		this.initializeCreditsData();
	}
	
	public JaalagaCredits(JaalagaResourceManager resourceManager, 
			JaalagaSceneManager sceneManager, 
			boolean pUnloadOnHidden) {
		super(resourceManager, sceneManager, pUnloadOnHidden);
		this.initializeCreditsData();
	}
	
	// Animates the layer to slide in from the top.
	IUpdateHandler SlideIn = new IUpdateHandler() {
		@Override
		public void onUpdate(float pSecondsElapsed) {
			if(getY() > 0) {
				setPosition(getX(), Math.max(
						getY()-(3600*(pSecondsElapsed)),
						0));
			} else {
				unregisterUpdateHandler(this);
			}
		}
		@Override public void reset() {}
	};
	
	// Animates the layer to slide out through the top and tell the SceneManager to hide it when it is off-screen;
	IUpdateHandler SlideOut = new IUpdateHandler() {
		@Override
		public void onUpdate(float pSecondsElapsed) {
			if(getY() < getResourceManager().getCameraHeight() + 480f) {
				setPosition(getX(), Math.min(getY()+(3600*(pSecondsElapsed)),
						getResourceManager().getCameraHeight() + 480f));
			} else {
				unregisterUpdateHandler(this);
				getSceneManager().hideLayer();
			}
		}
		@Override public void reset() {}
	};
	
	@Override
	public void onLoadLayer() {
		// Create and attach a background that hides the Layer when touched.
		Rectangle smth = new Rectangle(0, 
				0,
				this.getResourceManager().getCameraWidth(),
				this.getResourceManager().getCameraHeight(),
				this.getResourceManager().getEngine().getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, 
					final float pTouchAreaLocalX, 
					final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionUp() && 
						pTouchAreaLocalX < this.getWidth() && 
						pTouchAreaLocalX > 0 && 
						pTouchAreaLocalY < this.getHeight() && 
						pTouchAreaLocalY > 0) {
					getResourceManager().getClickSound().play();
					onHideLayer();
				}
				return true;
			}
		};
		smth.setColor(0f, 0f, 0f, 0.98f);
		this.attachChild(smth);
		this.registerTouchArea(smth);
		
		// Create the OptionsLayerTitle text for the Layer.
		Text creditsTitle = new Text(0,
				0,
				this.getResourceManager().getMediumFont(),
				"Credits",
				this.getResourceManager().getEngine().getVertexBufferObjectManager());
		creditsTitle.setPosition(this.getResourceManager().getCameraWidth()/2 - creditsTitle.getWidth()/2,
				creditsTitle.getHeight()/2);
		this.attachChild(creditsTitle);
		
		Text codeSection = new Text(10,
				creditsTitle.getY() + creditsTitle.getHeight(),
				this.getResourceManager().getMediumFont(),
				"Code",
				this.getResourceManager().getEngine().getVertexBufferObjectManager());
		this.attachChild(codeSection);
		
		float codeSectionEnd = codeSection.getY() + codeSection.getHeight();
		float entryHeight = 0;
		for(int i = 0; i < this.codeCredits.length; i++) {
			Text codeText = new Text(0,
					0,
					this.getResourceManager().getSmallFont(),
					this.codeCredits[i],
					this.getResourceManager().getEngine().getVertexBufferObjectManager());
			entryHeight = codeText.getHeight();
			codeText.setPosition(10, codeSectionEnd +  entryHeight* i);
			this.attachChild(codeText);
		}
		
		Text audioSection = new Text(10,
				10 + codeSectionEnd +  entryHeight * this.codeCredits.length,
				this.getResourceManager().getMediumFont(),
				"Audio",
				this.getResourceManager().getEngine().getVertexBufferObjectManager());
		this.attachChild( audioSection);
		
		float audioSectionEnd = audioSection.getY() + audioSection.getHeight();
		for(int i = 0; i < this.audioCredits.length; i++) {
			Text codeText = new Text(0,
					0,
					this.getResourceManager().getSmallFont(),
					this.audioCredits[i],
					this.getResourceManager().getEngine().getVertexBufferObjectManager());
			entryHeight = codeText.getHeight();
			codeText.setPosition(10, audioSectionEnd +  entryHeight* i);
			this.attachChild(codeText);
		}
		
		// Let the player know how to get out of the blank Options Layer
		//Text OptionsLayerSubTitle = new Text(0,0,ResourceManager.fontDefault32Bold,"Tap to return",ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		//OptionsLayerSubTitle.setScale(0.75f);
		//OptionsLayerSubTitle.setPosition(0f,-BackgroundHeight/2f+OptionsLayerSubTitle.getHeight());
		//this.attachChild(OptionsLayerSubTitle);
		
		this.setPosition(0, getResourceManager().getCameraHeight() + 480f);
	}
	
	private void initializeCreditsData() {
		this.codeCredits = new String[] {
				"- Charles Cozad", 
				"- AndEngine for Android Game Development Cookbook", 
				"- AndEngine Examples, Nicolas Gramlich"};
		this.audioCredits = new String[] {
				"- Click Sound, AndEngine Examples", 
				"- \"Laser Shot Silenced\" by FreeSound.org user bubaproducer", 
				"- \"08205 game pitched explosion\" by FreeSound.org user Robinhood76"};
	}

	@Override
	public void onShowLayer() {
		this.registerUpdateHandler(SlideIn);
	}

	@Override
	public void onHideLayer() {
		this.registerUpdateHandler(SlideOut);
	}
	@Override
	public void onUnloadLayer() {
	}

}
