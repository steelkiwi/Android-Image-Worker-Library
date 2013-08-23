package com.steelkiwi.imagemanager.util;

import java.io.File;

import android.content.Context;
import android.os.Environment;

public class DiskUtil {

	private final static String CACHE_DIR_NAME = "image_cache";
	private final static String STORE_DIR_NAME = "storage";
	
	public static File defineCacheDir(Context context) {
		File storageRoot = getStorageRoot(context);
		File cacheDir = new File(storageRoot, CACHE_DIR_NAME);
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
		return cacheDir;
	}
	
	public static File defineStorageDir(Context context) {
		File storageRoot = getStorageRoot(context);
		File storeDir = new File(storageRoot, STORE_DIR_NAME);
		if (!storeDir.exists()) {
			storeDir.mkdirs();
		}
		return storeDir;
	}

	private static File getStorageRoot(Context context) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable()) {
			return context.getExternalCacheDir();
		} else {
			return context.getCacheDir();
		}
	}
	
}
