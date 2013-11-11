package com.johnadamsacademy.mentor.technologydevelopment.jaalaga;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.pool.GenericPool;

public class RocketPool extends GenericPool<Sprite> {

	private ITextureRegion textureRegion;
	private VertexBufferObjectManager vertexBufferObjectManager;
	
	public RocketPool(int maxCount, ITextureRegion newTextureRegion, VertexBufferObjectManager newVertexBufferObjectManager) {
		super(0, 1, maxCount);
		if(newTextureRegion != null) {
			this.textureRegion = newTextureRegion;
		} else {
			throw new IllegalArgumentException("The texture region must not be NULL");
		}
		
		if(newVertexBufferObjectManager != null) {
			this.vertexBufferObjectManager = newVertexBufferObjectManager;
		} else {
			throw new IllegalArgumentException("The texture region must not be NULL");
		}
		
	}
	
	@Override
	protected Sprite onAllocatePoolItem() {
		return new Sprite(0, 0, this.textureRegion, this.vertexBufferObjectManager);
	}
	
	@Override
	protected void onHandleRecycleItem(final Sprite sprite) {
		sprite.setIgnoreUpdate(true);
		sprite.setVisible(false);
	}
	
	@Override
	 protected void onHandleObtainItem(final Sprite sprite) {
		sprite.reset();
	 }

}
