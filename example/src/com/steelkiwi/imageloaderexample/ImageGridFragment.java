package com.steelkiwi.imageloaderexample;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.steelkiwi.imageloaderexample.content.Images;
import com.steelkiwi.imagemanager.ImageManager;

public class ImageGridFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.image_grid_fragment, container, false);
		
		ImageManager imageLoader = ((MainActivity)getActivity()).getImageLoader();
		
		ImagesAdapter adapter = new ImagesAdapter(imageLoader, Images.photos);
		
		GridView grid = (GridView) view.findViewById(R.id.grid);
		grid.setAdapter(adapter);

		return view;
	}

	
	
}
