package com.steelkiwi.imageloaderexample;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class PhotoCollectionAdapter extends BaseAdapter {

	private List<Bitmap> pictures;
	
	public PhotoCollectionAdapter() {
		pictures = new ArrayList<Bitmap>();
	}
	
	public void add(Bitmap bm){
		pictures.add(bm);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return pictures != null ? pictures.size() : 0;
	}

	@Override
	public Bitmap getItem(int position) {
		return pictures.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if(convertView == null){
			ImageView iv = new ImageView(parent.getContext()); 
			iv.setAdjustViewBounds(true);
			iv.setScaleType(ScaleType.FIT_XY);
			convertView = iv;
		}
		
		ImageView view = (ImageView) convertView;
		view.setImageBitmap(getItem(position));
		
		return view;
	}
	
	public void clear(){
		pictures.clear();
	}

}
