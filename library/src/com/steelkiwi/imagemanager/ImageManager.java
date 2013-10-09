package com.steelkiwi.imagemanager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.steelkiwi.imagemanager.thirdparty.MemoryCache;
import com.steelkiwi.imagemanager.util.DiskUtil;
import com.steelkiwi.imagemanager.util.EllipsideDrawable;
import com.steelkiwi.imagemanager.util.RoundedDrawable;

public class ImageManager {

	public static final String tag = ImageManager.class.getSimpleName();

	public final static int NO_RESOURCE = -1;

	private final static float DEFAULT_MEMORY_CACHE_QUOTA = 0.2f;
	private final static float MAX_MEMORY_CACHE_QUOTA = 0.7f;
	private final static float MIN_MEMORY_CACHE_QUOTA = 0.05f;
	
	private final static int DEFAULT_THREADS = 1;

	private File cacheDir;
	private File storageDir;

	private MemoryCache memcache;
	private ExecutorService executor;
	private MemoryWatchdog memoryWatchdog;

	private Map<ImageView, AbstractDownloader> tasks;

	private ImageManager(Context context, float memoryPercentAvailable, int threads) {
		memoryWatchdog = new MemoryWatchdog();
		memcache = new MemoryCache(memoryPercentAvailable);
		executor = Executors.newFixedThreadPool(threads);
		tasks = new HashMap<ImageView, AbstractDownloader>();
		cacheDir = DiskUtil.defineCacheDir(context);
		storageDir = DiskUtil.defineStorageDir(context);
	}

	/**
	 * Executes a task to download image from Internet or file system.
	 * @param task {@link DownloadTask} to execute.
	 */
	public void loadImage(DownloadTask task) {
		Bitmap bm = memcache.get(task.getCacheId());
		if (bm != null) {
			task.setResult(bm);
			onTaskCompletedSuccessfully(task);
		} else {
			searchInDiskCacheOrLoad(task);
		}
	}

	private void searchInDiskCacheOrLoad(DownloadTask task) {
		if (task.isdCache()) {
			task.setCacheRoot(cacheDir);
			File f = new File(task.getdCachePath());
			if (f.exists()) {
				task.changeParamsForDiscCacheDownload();
			}
		}
		prepareNewDownloadTask(task);
	}

	private void prepareNewDownloadTask(DownloadTask task) {
		AbstractDownloader imageTask = DownloaderFactory.create(task, handler, memoryWatchdog);
		ImageView view = task.getView();
		if (view != null) {
			stopExistingTaskForView(view);
			tasks.put(view, imageTask);
			applyStubDrawable(task);
		}
		executor.execute(imageTask);
	}

	private void applyStubDrawable(DownloadTask task) {
		ImageView view = task.getView();
		if (task.getPlaceholderResourse() != NO_RESOURCE) {
			view.clearAnimation();
			view.setImageResource(task.getPlaceholderResourse());
		}else if(task.getPlaceholderBitmap() != null){
			view.clearAnimation();
			view.setImageBitmap(task.getPlaceholderBitmap());
		}
	}

	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			if (msg.what == AbstractDownloader.DOWNLOAD_SUCCESS) {
				onTaskCompletedSuccessfully((DownloadTask) msg.obj);
			} else {
				handleDownloadFailed((DownloadTask)msg.obj, msg.what);
			}
			msg.obj = null;
			return true;
		}
	});

	private void handleDownloadFailed(DownloadTask task, int what) {
		if (what == AbstractDownloader.DOWNLOAD_ERROR) {
			setErrorIconAsResult(task);
		}
	}
	
	private void setErrorIconAsResult(DownloadTask task){
		ImageView view = task.getView();
		if(view != null){
			if(task.getErrorPlaceholderResource() != NO_RESOURCE){
				view.setImageResource(task.getErrorPlaceholderResource());
			}else if(task.getErrorBitmap() != null){
				view.setImageBitmap(task.getErrorBitmap());
			}
		}
	}

	private void onTaskCompletedSuccessfully(DownloadTask task) {
		cacheResultIfNeed(task);
		setResultToView(task);
		notifyLoadingCallback(task);

		task.cleanup();
		task = null;
	}

	private void setResultToView(DownloadTask task) {
		ImageView view = task.getView(); 
		// task may be cancelled if some new task was created for this view (ImageView)
		if (view != null && !task.isCancelled()) {
			
			stopExistingTaskForView(view);
			
			Bitmap result = task.getResult();
			
			if(task.isCircleView()){
				view.setImageDrawable(new EllipsideDrawable(result));
			}else if(task.isRoundedCorners()){
				view.setImageDrawable(new RoundedDrawable(result, task.getCornerRadius()));
			}else{
				view.setImageBitmap(result);
			}
			
			if (task.getAnimation() != null) {
				view.clearAnimation();
				view.startAnimation(task.getAnimation());
			}
		}
	}

	private void notifyLoadingCallback(DownloadTask task) {
		if (task.getCallback() != null) {
			task.getCallback().onBitmapLoaded(task.getUrl(), task.getResult());
		}
	}

	private void stopExistingTaskForView(ImageView view) {
		AbstractDownloader downloader = tasks.remove(view);
		if (downloader != null) {
			downloader.stop();
		}
	}

	private void cacheResultIfNeed(DownloadTask task) {
		if (task.ismCache()) {
			memcache.put(task.getCacheId(), task.getResult());
		}
	}

	/**
	 * Clears all in-memory cache.
	 */
	public void clearMemCache() {
		memcache.clear();
	}
	
	/**
	 * Clears all cached files on file system.
	 */
	public void clearDiscCache(){
		for(File f : cacheDir.listFiles()){
			f.delete();
		}
	}

	/**
	 * Clears all current download tasks and shutdowns an ThreadExecutor.
	 */
	public void clearAll() {
		executor.shutdownNow();
		tasks.clear();
		clearMemCache();
	}

	/**
	 * Executes a SaveBitmapTask.
	 * @param task task to execute.
	 */
	public void save(SaveBitmapTask task) {
		String pathToSave = task.getPath();
		if (pathToSave == null) {
			pathToSave = storageDir.getAbsolutePath() + File.separator + System.nanoTime();
			task.setPath(pathToSave);
		}
		new SaveFileTask(task).execute();
	}
	
	/**
	 * Delete file by path.
	 * @param path
	 */
	public void delete(String path){
		File f = new File(path);
		if(f.exists()){
			f.delete();
		}
	}

	// ==========================================================================
	// inner classes
	// ==========================================================================

	protected static class MemoryWatchdog {
		@SuppressWarnings("unused")
		private final static String tag = MemoryWatchdog.class.getSimpleName();
		private final static float AVAILABLE_MEMORY_LOAD_UPPER_LIMIT = 0.7f;

		private Runtime runtime = Runtime.getRuntime();
		private long currentReservedBytes;

		public synchronized boolean reserveMemory(long bytes, long threadId, DownloadTask task) {
			if ((getAvailableMemory() - currentReservedBytes) > bytes) {
				currentReservedBytes += bytes;
//				Log.i(ImageManager.tag, "Memory reserve request for " + bytes + " bytes, accepted (available=" + getAvailableMemory() + ", reserved=" + currentReservedBytes + ")");
//				Log.i(tag, "+" + bytes + "(" + threadId + ", " + task + "), total reserved - " + currentReservedBytes);
				return true;
			} else {
//				Log.i(ImageManager.tag, "Memory reserve request for " + bytes + " bytes, declined (available=" + getAvailableMemory() + ", reserved=" + currentReservedBytes + ")");
				return false;
			}
		}

		public synchronized void clearMemoryReservation(long bytes, long threadId, DownloadTask task, String reason) {
			currentReservedBytes -= bytes;
//			Log.i(ImageManager.tag, "thread cleaned up its reservation - " + bytes + " bytes, current reservation - " + currentReservedBytes);
//			Log.i(tag, "-" + bytes + "(" + threadId + ", " + task + ", " + reason + "), total reserved - " + currentReservedBytes);
		}

		private long getAvailableMemory() {
			return (long) ((runtime.maxMemory() - runtime.totalMemory()) * AVAILABLE_MEMORY_LOAD_UPPER_LIMIT);
		}
	}

	public static class Builder {
		private float memoryQuota = DEFAULT_MEMORY_CACHE_QUOTA;
		private int threads = DEFAULT_THREADS;
		private Context context;

		public Builder(Context context) {
			this.context = context;
		}

		/**
		 * Set fraction of total application memory which will be available to cache.<br/>
		 * It should be more than 0.05f and less 0.7f.
		 * @param memoryQuota
		 * @return builder instance.
		 */
		public Builder memoryPercentAvailable(float memoryQuota) {
			this.memoryQuota = checkMemoryQuotaOrThrow(memoryQuota);
			return this;
		}

		private float checkMemoryQuotaOrThrow(float memoryQuota) {
			if (memoryQuota >= 1) {
				memoryQuota = MAX_MEMORY_CACHE_QUOTA;
			} else if (memoryQuota < MIN_MEMORY_CACHE_QUOTA) {
				throw new IllegalArgumentException("Quota for memory cache cannot be less than 5%");
			}
			return memoryQuota;
		}

		/**
		 * Number of threads which will be used by loader simultaneously.
		 * @param threads
		 * @return builder instance.
		 */
		public Builder threads(int threads) {
			if (threads >= 1) {
				this.threads = threads;
				return this;
			} else {
				throw new IllegalArgumentException("Threads cannot be less 1!");
			}
		}

		/**
		 * @return new instance of ImageLoader.
		 */
		public ImageManager build() {
			ImageManager loader = new ImageManager(context, memoryQuota, threads);
			context = null;
			return loader;
		}
	}
}
