package com.steelkiwi.imagemanager;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import com.steelkiwi.imagemanager.ImageManager.MemoryWatchdog;

public class NetworkImageLoader extends AbstractDownloader {
	
	private final int BUFFER_SIZE = 4096;

	public NetworkImageLoader(Handler handler, MemoryWatchdog memoryWatchdog, DownloadTask task) {
		super(handler, memoryWatchdog, task);
	}

	@Override
	public void run() {

		InputStream is = null;
		try {
			URL url = new URL(task.getUrl());
			BitmapFactory.Options options = task.createPreDecodeOptions();
			
			if(isStopped()){
				onDownloadCancelled();
				return;
			}

			is = new BufferedInputStream(url.openStream(), BUFFER_SIZE);
			BitmapFactory.decodeStream(is, null, options);
			is.close();
			
			if(isStopped()){
				onDownloadCancelled();
				return;
			}
			
			correctInSampleSize(options, task);
			is = new BufferedInputStream(url.openStream(), BUFFER_SIZE);
			BitmapFactory.decodeStream(is, null, options);
			is.close();
			
			if(isStopped()){
				onDownloadCancelled();
				return;
			}
			
			if(!reserveMemoryForBitmapDecode(options)){
				return;
			}
			
			options.inJustDecodeBounds = false;
			is = new BufferedInputStream(url.openStream(), BUFFER_SIZE);
			Bitmap bm = BitmapFactory.decodeStream(is, null, options);
			is.close();
			
			if(isStopped()){
				onDownloadCancelled();
				return;
			}
			
			if(bm != null){
				onDownloadComplete(bm);
			} else {
				onDownloadError();
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
			onDownloadError(e);
		} catch (IOException e) {
			e.printStackTrace();
			onDownloadError(e);
		} finally {
			closeInputStream(is);
		}
		
	}
}
