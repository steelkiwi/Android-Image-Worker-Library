package com.steelkiwi.imageloaderexample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
		
		if(position % 2 == 0){
			
		}
		DownloadTask.Builder taskBuilder = new DownloadTask.Builder()
			.url(images[position])
			.loadTo(holder.image)
			.config(Bitmap.Config.RGB_565)
			.mCache()
			.placeholder(placeholder)
			.errorPlaceholder(BitmapFactory.decodeResource(convertView.getResources(), R.drawable.ic_launcher))
			.animation(animation)
			.cropToSquare();
		
		if(position % 2 == 0){
			taskBuilder.circleView();
		}else{
			taskBuilder.roundedView(30);
		}
		
		loader.loadImage(taskBuilder.build());

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
