package com.steelkiwi.imageloaderexample;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.steelkiwi.imageloaderexample.content.Images;
import com.steelkiwi.imagemanager.ImageManager;

public class ImageListFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.image_list_fragment, container, false);
		
		ImageManager imageLoader = ((MainActivity)getActivity()).getImageLoader();
		
		ImagesAdapter adapter = new ImagesAdapter(imageLoader, Images.girls);
		
		ListView list = (ListView) view.findViewById(R.id.image_list);
		
		list.setAdapter(adapter);		
		
		return view;
	}

	
	
}
