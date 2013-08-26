package com.steelkiwi.imagemanager.processing;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class PortraitProcessor extends ImageProcessingDecorator {

	@Override
	protected Bitmap applyProcessingEffect(Bitmap bm) {
		
		int bmWidth = bm.getWidth();
		int bmHeight = bm.getHeight();
		
		if(bmWidth > bmHeight){
			Matrix matrix = new Matrix();
			matrix.postRotate(90);
			return Bitmap.createBitmap(bm, 0, 0, bmWidth, bmHeight, matrix, true);
		} else {
			return bm;
		}
	}

}
