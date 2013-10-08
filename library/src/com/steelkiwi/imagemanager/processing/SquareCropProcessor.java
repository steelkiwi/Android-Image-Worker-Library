package com.steelkiwi.imagemanager.processing;

import android.graphics.Bitmap;

public class SquareCropProcessor extends ImageProcessingDecorator {
	
	public final static String tag = SquareCropProcessor.class.getSimpleName();

	@Override
	protected Bitmap applyProcessingEffect(Bitmap bm) {
		
		int bmWidth = bm.getWidth();
		int bmHeight = bm.getHeight();
		
		int sideSize = bmWidth >= bmHeight ? bmHeight : bmWidth;
		
		int subsetX = (bmWidth - sideSize) / 2;
		int subsetY = (bmHeight - sideSize) / 2;
		
		return Bitmap.createBitmap(bm, subsetX, subsetY, sideSize, sideSize);
	}

}
