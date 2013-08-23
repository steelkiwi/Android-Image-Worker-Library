package com.steelkiwi.imagemanager;

import android.graphics.Bitmap;

public interface ImageLoaderCallback {
	void onBitmapLoaded(String url, Bitmap bm);
}
