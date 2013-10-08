package com.steelkiwi.imagemanager;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import com.steelkiwi.imagemanager.SaveFileTask.BitmapCompressionListener;

public class SaveBitmapTask {
	
	private Bitmap bitmap;
	private BitmapCompressionListener compressionListener;
	private CompressFormat format;
	private String path;
	private String tag;
	private int quality;
	
	private SaveBitmapTask(Builder builder) {
		this.bitmap = builder.bitmap;
		this.compressionListener = builder.compressionListener;
		this.format = builder.format;
		this.path = builder.path;
		this.tag = builder.tag;
		this.quality = builder.quality;
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}

	public BitmapCompressionListener getCompressionListener() {
		return compressionListener;
	}

	public CompressFormat getFormat() {
		return format;
	}

	public String getPath() {
		return path;
	}
	
	void setPath(String path) {
		this.path = path;
	}

	public String getTag() {
		return tag;
	}

	public int getQuality() {
		return quality;
	}
	
	public void recycle(){
		bitmap = null;
		compressionListener = null;
	}
	
	// ================================================================================
	// inner classes
	// ================================================================================

	public static class Builder{
		private BitmapCompressionListener compressionListener;
		private CompressFormat format = CompressFormat.PNG;
		private String path;
		private String tag;
		private Bitmap bitmap;
		private int quality = 100;
		
		/**
		 * Constructs a new Builder 
		 * @param bitmap bitmap to store on FS
		 */
		public Builder(Bitmap bitmap) {
			this.bitmap = bitmap;
		}
		
		/**
		 * Set BitmapCompressionListener which will be notified after<br/>
		 * bitmap will be saved of if an error occurs.
		 * @param compressionListener.
		 * @return builder instance.
		 */
		public Builder compressionListener(BitmapCompressionListener compressionListener){
			this.compressionListener = compressionListener;
			return this;
		}

		/**
		 * Set compression format for file, @see {@link CompressFormat} for details.
		 * CompressFormat.PNG will be used by default.
		 * @param format format to compress bitmap.
		 * @return builder instance.
		 */
		public Builder compressTo(CompressFormat format){
			if(format != null){
				this.format = format;
				return this;
			} else {
				throw new IllegalArgumentException("CompressFormat cannot be null!");
			}
		}
		
		/**
		 * Full path to file you want to save bitmap to. Unnecessary parameter, <br/>
		 * if you not set it bitmap will be saved to "ImageLoader.storageDir + / + System.nanoTime()".
		 * @param path to store bitmap.
		 * @return builder instance.
		 */
		public Builder saveToFile(String path){
			this.path = path;
			return this;
		}
		
		/**
		 * Optional parameter which will be returned with a BitmapCompressionListener callback.
		 * @param tag operation tag.
		 * @return builder instance.
		 */
		public Builder setTag(String tag){
			this.tag = tag;
			return this;
		}
		
		/**
		 * Hint to the compressor, 0-100. 0 meaning compress for small size,<br/> 
		 * 100 meaning compress for max quality. Some formats, like PNG which<br/> 
		 * is lossless, will ignore the quality setting. Default value - 100.
		 * @param quality
		 * @return builder instance.
		 */
		public Builder compressionQuality(int quality){
			if(quality > 0){
				this.quality = quality;
			}
			return this;
		}

		/**
		 * Creates SaveBitmapTask based on specified parameters.
		 * @return new {@link SaveBitmapTask}
		 */
		public SaveBitmapTask build(){
			if(this.bitmap == null){
				throw new IllegalArgumentException("Bitmap to compress cannot be null!");
			}
			return new SaveBitmapTask(this);
		}
	}

}
