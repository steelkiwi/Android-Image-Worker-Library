package com.steelkiwi.imagemanager.processing;

import android.graphics.Bitmap;
import android.util.Log;

public class ProportionalScaleProcessor extends ImageProcessingDecorator {

	private final static String tag = ProportionalScaleProcessor.class.getSimpleName();
	
	private int maxWidth;
	private int maxHeight;
	
	public ProportionalScaleProcessor(int maxWidth, int maxHeight) {
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
	}

	@Override
	protected Bitmap applyProcessingEffect(Bitmap bm) {
		
		Log.i(tag, "applyProcessingEffect");
		
		int bmWidth = bm.getWidth();
		int bmHeight = bm.getHeight();

		if (bmWidth <= maxWidth && bmHeight <= maxHeight) {
			return bm;
		}

		float proportion = (float)bmWidth / bmHeight;

		while (bmWidth > maxWidth || bmHeight > maxHeight) {
			float deltaWidth = bmWidth - maxWidth;
			float deltaHeight = bmHeight - maxHeight;

			if (deltaWidth >= deltaHeight) {
				bmWidth -= (int)deltaWidth;
				bmHeight = (int)(bmWidth / proportion);
			} else {
				bmHeight -= (int)deltaHeight;
				bmWidth = (int)(bmHeight * proportion);
			}
		}

		return Bitmap.createScaledBitmap(bm, (int) bmWidth, (int) bmHeight, false);
	}

}
