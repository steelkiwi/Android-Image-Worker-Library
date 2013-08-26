package com.steelkiwi.imageloaderexample;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.steelkiwi.imagemanager.DownloadTask;
import com.steelkiwi.imagemanager.ImageManager;

public class ImagesAdapter extends BaseAdapter {

	private String[] images;
	private ImageManager loader;

	public ImagesAdapter(ImageManager loader, String[] images) {
		this.images = images;
		this.loader = loader;
	}

	@Override
	public int getCount() {
		return images.length;
	}

	@Override
	public Object getItem(int position) {
		return images[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_layout, null);
			createHolder(convertView);
		}
		
		Holder holder = (Holder) convertView.getTag();
		
		Animation animation = new AlphaAnimation(0, 1);
		animation.setDuration(100);

		int placeholder = position%2 == 0 ? R.drawable.stub_green : R.drawable.stub_pink;
		
		DownloadTask task = new DownloadTask.Builder()
			.url(images[position])
			.loadTo(holder.image)
			.config(Bitmap.Config.ALPHA_8)
			.mCache()
			.placeholder(placeholder)
			.errorIcon(R.drawable.stub_error)
			.animation(animation)
			.scaleToProportionaly(200, 200)
			.forcePortrait()
			.build();
		
		loader.loadImage(task);

		return convertView;
	}
	
	private Holder createHolder(View view){
		Holder holder = new Holder();
		holder.image = (ImageView)view.findViewById(R.id.image);
		view.setTag(holder);
		return holder;
	}
	
	public static class Holder {
		ImageView image;
	}

}
