package com.steelkiwi.imagemanager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Handler;
import android.os.Message;

import com.steelkiwi.imagemanager.ImageManager.MemoryWatchdog;

public abstract class AbstractDownloader implements Runnable {

	public final static int DOWNLOAD_SUCCESS = 900; 								// success code for download task
	public final static int DOWNLOAD_ERROR = 901; 									// error code for download task
	public final static int DOWNLOAD_INTERRUPTED = 902; 							// interrupted flag

	public final static int BUFFER_SIZE = 8192;

	protected final static String REASON_ERROR = "Error"; 							// only purpose for using these reasons is logging in MemoryWatchdog.java
	protected final static String REASON_FINISHED = "Finished";
	protected final static String REASON_CANCELLED = "Cancelled";

	protected final static int MAX_RETRY = 5; 										// max retry count to reserve memory in MemoryWatchdog.java
	protected final static long RETRY_SLEEP = 50; 									// memory reservation retry interval

	private final static CompressFormat COMPRESS_FORMAT = CompressFormat.PNG; 		// compression settings for disk caching
	private final static int COMPRESS_QUALITY = 100;

	protected Handler handler; 														// handler of thread which will receive download result or error report
	protected MemoryWatchdog memoryWatchdog; 										// MemoryWatchdog used to reserve some memory to download picture to avoid OutOfMemory error
	protected DownloadTask task; 													// task description and details 
	protected long bitmapSizeBytes; 												// size of bitmap that will be download

	protected AbstractDownloader(Handler handler, MemoryWatchdog memoryWatchdog, DownloadTask task) {
		this.handler = handler;
		this.memoryWatchdog = memoryWatchdog;
		this.task = task;
	}

	public void stop() {
		task.setCancelled(true);
	}

	protected void onDownloadComplete(Bitmap bm) {
		Bitmap result = applyBitmapPostProcessing(bm);
		onDownloadSuccess(result);
		cacheToFS(result);
	}

	private void cacheToFS(Bitmap bm) {
		if (task.isdCache()) {
			File cacheFile = new File(task.getdCachePath());
			OutputStream os = null;
			try {
				os = new BufferedOutputStream(new FileOutputStream(cacheFile), BUFFER_SIZE);
				bm.compress(COMPRESS_FORMAT, COMPRESS_QUALITY, os);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				ensureOutputStreamClosed(os);
			}
		}
	}

	private void ensureOutputStreamClosed(OutputStream os) {
		if (os != null) {
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private Bitmap applyBitmapPostProcessing(Bitmap bm) {
		Bitmap result = bm;
		if (task.getImageProcessor() != null) {
			result = task.getImageProcessor().processImage(bm);
		}
		return result;
	}

	private void onDownloadSuccess(Bitmap bm) {
		task.setResult(bm);
		Message m = handler.obtainMessage(DOWNLOAD_SUCCESS, task);
		handler.sendMessage(m);
		cleanup(REASON_FINISHED);
	}

	protected void onDownloadError(Exception e) {
		/*
		 * TODO we also can send a Throwable but there is some difficulties to
		 * send a task description. this can be solved wrapping task and
		 * Exception to sole object but I see no sense at this time.
		 */
		Message m = handler.obtainMessage(DOWNLOAD_ERROR, task);
		handler.sendMessage(m);
		cleanup(REASON_ERROR);
	}

	protected void onDownloadError() {
		Message m = handler.obtainMessage(DOWNLOAD_ERROR, task);
		handler.sendMessage(m);
		cleanup(REASON_ERROR);
	}

	protected void onDownloadCancelled() {
		Message m = handler.obtainMessage(DOWNLOAD_INTERRUPTED, task);
		handler.sendMessage(m);
		cleanup(REASON_CANCELLED);
	}

	private void cleanup(String reason) {
		memoryWatchdog.clearMemoryReservation(bitmapSizeBytes, Thread.currentThread().getId(), task, reason);
	}

	protected int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	protected void correctInSampleSize(BitmapFactory.Options options, DownloadTask task) {
		if (task.isProcessScaleProportionally()) {
			options.inSampleSize = calculateInSampleSize(options, task.getProcessScaleMaxWidth(), task.getProcessScaleMaxHeight());
		}
	}

	protected long estimateBitmapSize(BitmapFactory.Options options) {
		return options.outWidth * options.outHeight * getBytesPerPixel(options.inPreferredConfig);
	}

	protected boolean reserveMemoryForBitmapDecode(BitmapFactory.Options options) {
		bitmapSizeBytes = estimateBitmapSize(options);
		int retryCounter = 0;
		while (!memoryWatchdog.reserveMemory(bitmapSizeBytes, Thread.currentThread().getId(), task) && !task.isCancelled()) {
			try {
				Thread.sleep(RETRY_SLEEP);
				retryCounter++;
				if (retryCounter == MAX_RETRY) {
					task.setCancelled(true);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				task.setCancelled(true);
			}
		}

		if (task.isCancelled()) {
			onDownloadCancelled();
		}

		return !task.isCancelled();
	}

	protected boolean isStopped() {
		return task.isCancelled();
	}

	private int getBytesPerPixel(Bitmap.Config config) {
		switch (config) {
		case ALPHA_8:
			return 1;
		case ARGB_4444:
			return 2;
		case ARGB_8888:
			return 4;
		case RGB_565:
			return 2;
		default:
			throw new IllegalArgumentException("Unknown Bitmap.Config - " + task.getBitmapConfig());
		}
	}

	protected void closeInputStream(InputStream imageStream) {
		if (imageStream != null) {
			try {
				imageStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
