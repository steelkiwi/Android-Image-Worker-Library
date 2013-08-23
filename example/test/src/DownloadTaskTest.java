package src;

import android.graphics.Bitmap;
import android.test.AndroidTestCase;
import android.util.Log;
import android.view.animation.AlphaAnimation;

import com.steelkiwi.imagemanager.DownloadTask;
import com.steelkiwi.imagemanager.ImageLoaderCallback;
import com.steelkiwi.imagemanager.processing.ProportionalScaleProcessor;

/**
 * Set of tests for DownloadTask.java and DownloadTask.Builder
 * @author syndarin
 *
 */
public class DownloadTaskTest extends AndroidTestCase {
	
	private final static String VALID_IMAGE_URL = "https://lh6.googleusercontent.com/-55osAWw3x0Q/URquUtcFr5I/AAAAAAAAAbs/rWlj1RUKrYI/s1024/A%252520Photographer.jpg";
	private final static String STUB_FILE_PATH = "file.jpg";
	
	public void testNullUrl(){
		try{
			new DownloadTask.Builder().url(null).build();
			fail("Exception expected!");
		} catch (Exception e){
			assertTrue(e instanceof IllegalArgumentException);
		}
	}

	public void testEmptyUrl(){
		try{
			new DownloadTask.Builder().url("").build();
			fail("Exception expected!");
		} catch (Exception e){
			assertTrue(e instanceof IllegalArgumentException);
		}
	}
	
	public void testNullFilePath(){
		try{
			new DownloadTask.Builder().file(null).build();
			fail("Exception expected!");
		} catch (Exception e){
			assertTrue(e instanceof IllegalArgumentException);
		}
	}
	
	public void testEmptyFilePath(){
		try{
			new DownloadTask.Builder().file("").build();
			fail("Exception expected!");
		} catch (Exception e){
			assertTrue(e instanceof IllegalArgumentException);
		}
	}
	
	public void testNoViewNoCallback(){
		try{
			new DownloadTask.Builder().url(VALID_IMAGE_URL).build();
		} catch (Exception e){
			assertTrue(e instanceof IllegalArgumentException);
		}
	}
	
	public void testIncorrectScalingParams(){
		int exceptionCounter = 0;
		
		try{
			new DownloadTask.Builder().url(VALID_IMAGE_URL).scaleToProportionaly(0, 1).build();
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
			exceptionCounter++;
		}
		
		try{
			new DownloadTask.Builder().url(VALID_IMAGE_URL).scaleToProportionaly(1, 0).build();
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
			exceptionCounter++;
		}

		assertEquals(2, exceptionCounter);
	}
	
	public void testDownloadSourceCorrect(){
		
		DownloadTask nTask = new DownloadTask.Builder().url(VALID_IMAGE_URL).downloadCallback(callback).build();
		assertTrue(nTask.isNetworkDownload());
	
		DownloadTask fTask = new DownloadTask.Builder().file(STUB_FILE_PATH).downloadCallback(callback).build();
		assertFalse(fTask.isNetworkDownload());
	}
	
	public void testPublicMethods(){
		DownloadTask task = createNetworkTaskWithAllOptions();
		
		assertNotNull(task.getUrl());
		assertTrue(task.getAnimation() instanceof AlphaAnimation);
		assertTrue(task.getBitmapConfig() == Bitmap.Config.RGB_565);
		assertTrue(task.getImageProcessor() instanceof ProportionalScaleProcessor);
		assertNotNull(task.getCallback());
		
		task = new DownloadTask.Builder()
			.url(VALID_IMAGE_URL)
			.downloadCallback(callback)
			.build();
		
		assertNotNull(task.getBitmapConfig());
		assertNull(task.getImageProcessor());
		assertNull(task.getAnimation());
	}
	
	ImageLoaderCallback callback = new ImageLoaderCallback() {
		@Override
		public void onBitmapLoaded(String url, Bitmap bm) {
			Log.i(url, "bitmap loaded!");
		}
	};
	
	private DownloadTask createNetworkTaskWithAllOptions(){
		return new DownloadTask.Builder()
		.url(VALID_IMAGE_URL)
		.animation(new AlphaAnimation(0, 1))
		.config(Bitmap.Config.RGB_565)
		.cropToSquare()
		.scaleToProportionaly(200, 200)
		.downloadCallback(callback)
		.dCache()
		.mCache()
		.build();
	}
}
