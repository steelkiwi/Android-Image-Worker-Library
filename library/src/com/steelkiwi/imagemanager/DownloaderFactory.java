package com.steelkiwi.imagemanager;

import com.steelkiwi.imagemanager.ImageManager.MemoryWatchdog;

import android.os.Handler;

public class DownloaderFactory {

	public static AbstractDownloader create(DownloadTask task, Handler handler, MemoryWatchdog watchdog) {
		return task.isNetworkDownload() ? new NetworkImageLoader(handler, watchdog, task) : new FileImageLoader(handler, watchdog, task);
	}

}
