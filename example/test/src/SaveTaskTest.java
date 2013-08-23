package src;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.test.AndroidTestCase;

import com.steelkiwi.imagemanager.SaveBitmapTask;

public class SaveTaskTest extends AndroidTestCase {

	public void testNullBitmap() {
		try {
			new SaveBitmapTask.Builder(null).build();
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}

	public void testCompressFormatValid() {
		try {
			Bitmap bm = createSimpleBitmap();
			new SaveBitmapTask.Builder(bm).compressTo(null).build();
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}

	public void testQualityValid() {
		Bitmap bm = createSimpleBitmap();
		SaveBitmapTask task = new SaveBitmapTask.Builder(bm).compressTo(CompressFormat.PNG).compressionQuality(0).build();
		assertEquals(task.getQuality(), 100);
	}

	private Bitmap createSimpleBitmap() {
		return Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
	}
}
