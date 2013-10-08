Android Image Worker Library
================================

ImageManager is Android library that helps you to simplify operations of downloading and displaying images.

ImageManager supports memory & disc cache, allows you to download and save bitmaps asynchronously. It uses thread pool
to run tasks simultaneously. You can create an instance using a Builder object:

```java
ImageManager manager = new ImageManager.Builder(Context context)
	.memoryPercentAvailable(0.2f)  // fraction of all application memory that will be used by cache.
	.threads(3)                    // maximum parallelly downloaded threads. 
	.build();
```
	
Downloading.
------------------------------

Every single download is described by DownloadTask e.g.:

```java
DownloadTask task = new DownloadTask.Builder()
	.url(String some_picture_url)                       // image url.
	.mCache()                                           // result of task execution will be cached in memory.
	.dCache()                                           // result of task execution will be cached on local filesystem.
	.loadTo(ImageView some_view)                        // bitmap will be placed to specified ImageView.
	.downloadCallback(ImageLoaderCallback callback)     // after successfull download ImageLoaderCallback will be notified with Bitmap.
	.config(Bitmap.Config config)                       // specified config will be used to decode image.
	.animation(Animation a)                             // ImageView will be animated after Bitmap set to it.
	.scaleToProportionally(int maxWidth, int maxHeight) // Bitmap will be scaled to match specified dimensions with saving proportions.
	.cropToSquare()                                     // Crop effect will be applied to download result.
	.placeholder(int placeholderDrawable)               // Stub drawable will be shown while image loading procedure.
	.build();
```

Also you can load an image from filesystem using .file(String path) instead of .url(String url).
	
After task created you can run it using ImageManager:

```java
manager.loadImage(DownloadTask task);
```

Saving.
--------------------

To save some Bitmap you've created (or got in another way than ImageManager) you can use SaveBitmapTask:

```java
SaveBitmapTask task = new SaveBitmapTask.Builder(Bitmap bm)
	.compressionListener(BitmapCompressionListener listener) // listener that will be notified after bitmap compression is completed (success or fail).
	.compressTo(CompressFormat format)                       // CompressFormat to save bitmap. CompressFormat.PNG will be used by default.
	.saveToFile(String path)                                 // Path to save bitmap, unnecessary param.
	.setTag(String tag)                                      // Tag that describes this SaveBitmapTask.
	.compressionQuality(int quality)                         // Compression quality.
```
	
After creating it you can execute SaveBitmapTask using ImageManager:

```java
manager.save(SaveBitmapTask task);
```
	
