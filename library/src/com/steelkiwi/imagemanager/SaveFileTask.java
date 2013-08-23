package com.steelkiwi.imagemanager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.os.AsyncTask;

/**
 * This class represents a task to store file asynchronously. <br/>
 * You shouldn't instantiate it manually - you need to call ImageManager.save(SaveBitmapTask task) instead.
 * @author syndarin
 *
 */
public class SaveFileTask extends AsyncTask<Void, Void, Boolean> {
	
	private SaveBitmapTask task;
	
	public SaveFileTask(SaveBitmapTask task) {
		this.task = task;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		File destinationFile = new File(task.getPath());
		OutputStream os = null;
		try {
			os = new BufferedOutputStream(new FileOutputStream(destinationFile), AbstractDownloader.BUFFER_SIZE);
			return task.getBitmap().compress(task.getFormat(), task.getQuality(), os);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		BitmapCompressionListener listener = task.getCompressionListener();
		if(listener != null){
			if(result){
				listener.onCompressedSuccessfully(task.getPath(), task.getTag());
			} else {
				listener.onCompressionFailed(task.getTag());
			}
		}
		task.recycle();
	}

	// ===========================================================================================
	// inner classes
	// ===========================================================================================

	public interface BitmapCompressionListener{
		void onCompressedSuccessfully(String filename, String tag);
		void onCompressionFailed(String tag);
	}
}
