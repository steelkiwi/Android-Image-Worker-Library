package com.steelkiwi.imageloaderexample;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.steelkiwi.imagemanager.DownloadTask;
import com.steelkiwi.imagemanager.ImageLoaderCallback;
import com.steelkiwi.imagemanager.ImageManager;
import com.steelkiwi.imagemanager.SaveBitmapTask;
import com.steelkiwi.imagemanager.SaveFileTask.BitmapCompressionListener;

public class PhotoCollectionFragment extends Fragment implements ImageLoaderCallback, BitmapCompressionListener {

	private final static int CAM_REQUEST = 900;

	private ImageManager imageLoader;
	private PhotoCollectionAdapter adapter;
	private List<String> files;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.photo_collection_fragment, container, false);
		view.findViewById(R.id.btnMakePhoto).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				makePhoto();
			}
		});

		adapter = new PhotoCollectionAdapter();
		GridView grid = (GridView) view.findViewById(R.id.gvPhotoGrid);
		grid.setAdapter(adapter);
		files = new ArrayList<String>();
		imageLoader = ((MainActivity) getActivity()).getImageLoader();
		loadImages();

		return view;
	}

	private void loadImages() {
		files.addAll(extractFiles());
		for (Iterator<String> iterator = files.iterator(); iterator.hasNext();) {
			String path = iterator.next();
			DownloadTask task = createTaskForImageLoading(path);
			imageLoader.loadImage(task);
		}
	}

	private DownloadTask createTaskForImageLoading(String path) {
		return new DownloadTask.Builder().config(Bitmap.Config.RGB_565).placeholder(R.drawable.stub_pink).file(path).downloadCallback(this).cropToSquare().scaleToProportionaly(200, 200).build();
	}

	private void makePhoto() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, CAM_REQUEST);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAM_REQUEST && resultCode == Activity.RESULT_OK) {
			Bundle extras = data.getExtras();
			if (extras != null) {
				Bitmap bm = (Bitmap) extras.get("data");
				if (bm != null) {
					SaveBitmapTask task = new SaveBitmapTask.Builder(bm).compressionQuality(100).compressTo(CompressFormat.JPEG).setTag("sometag").compressionListener(this).build();

					imageLoader.save(task);
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onBitmapLoaded(String url, Bitmap bm) {
		adapter.add(bm);
	}

	@Override
	public void onDestroyView() {
		adapter.clear();
		super.onDestroyView();
	}

	@Override
	public void onCompressedSuccessfully(String filename, String tag) {
		Toast.makeText(getActivity(), "Image " + tag + " was stored successfully!", Toast.LENGTH_SHORT).show();
		storeNewFile(filename);
		DownloadTask task = createTaskForImageLoading(filename);
		imageLoader.loadImage(task);
	}

	@Override
	public void onCompressionFailed(String tag) {
		Toast.makeText(getActivity(), "Image " + tag + " compression was failed!", Toast.LENGTH_SHORT).show();
	}

	private void storeNewFile(String path){
		files.add(path);
		File f = new File(Environment.getExternalStorageDirectory(), "pictures.dat");
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(f));
			oos.writeObject(files);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(oos != null){
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private List<String> extractFiles() {
		File f = new File(Environment.getExternalStorageDirectory(), "pictures.dat");
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(f));
			return (ArrayList<String>) ois.readObject();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return Collections.emptyList();
	}
}
