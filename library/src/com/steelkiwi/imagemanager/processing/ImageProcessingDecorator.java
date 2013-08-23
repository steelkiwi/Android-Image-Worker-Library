package com.steelkiwi.imagemanager.processing;

import android.graphics.Bitmap;

public abstract class ImageProcessingDecorator {
	
	private ImageProcessingDecorator decoratedProcessor;

	public Bitmap processImage(Bitmap bm) {
		Bitmap processed = bm;
		if(decoratedProcessor != null){
			processed = decoratedProcessor.processImage(bm);
		}
		return applyProcessingEffect(processed);
	}

	public void decorate(ImageProcessingDecorator decoratedProcessor) {
		this.decoratedProcessor = decoratedProcessor;
	}
	
	protected abstract Bitmap applyProcessingEffect(Bitmap bm);

}
