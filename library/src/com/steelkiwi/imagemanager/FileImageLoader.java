package com.steelkiwi.imagemanager;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import com.steelkiwi.imagemanager.ImageManager.MemoryWatchdog;

public class FileImageLoader extends AbstractDownloader {

	public FileImageLoader(Handler handler, MemoryWatchdog memoryWatchdog, DownloadTask task) {
		super(handler, memoryWatchdog, task);
	}

	@Override
	public void run() {
		FileInputStream imageStream = null;
		try {
			
			imageStream = new FileInputStream(new File(task.getUrl()));
			FileDescriptor fd = imageStream.getFD();
			
			BitmapFactory.Options decodeOptions = task.createPreDecodeOptions();
			BitmapFactory.decodeFileDescriptor(fd, null, decodeOptions);

			correctInSampleSize(decodeOptions, task);
			BitmapFactory.decodeFileDescriptor(fd, null, decodeOptions);
			
			if(!reserveMemoryForBitmapDecode(decodeOptions)){
				return;
			}
			
			decodeOptions.inJustDecodeBounds = false;
			Bitmap bm = BitmapFactory.decodeFileDescriptor(fd, null, decodeOptions);
			
			if(bm != null){
				onDownloadComplete(bm);
			} else{
				onDownloadError();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			onDownloadError(e);
		} catch (IOException e) {
			e.printStackTrace();
			onDownloadError(e);
		} finally {
			closeInputStream(imageStream);
		}
	}
}
