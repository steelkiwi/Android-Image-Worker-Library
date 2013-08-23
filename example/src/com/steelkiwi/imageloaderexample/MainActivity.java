package com.steelkiwi.imageloaderexample;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;

import com.steelkiwi.imagemanager.ImageManager;

public class MainActivity extends Activity {

	private ImageManager imageLoader;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.root);
		
		imageLoader = new ImageManager.Builder(this).memoryPercentAvailable(0.2f).threads(3).build();
		
		ActionBar ab = getActionBar();
		
		ActionBar.Tab imageListTab = ab.newTab().setText("List").setTabListener(new TabListener<ImageListFragment>(ImageListFragment.class, this, ImageListFragment.class.getSimpleName(), R.id.flContainer));
		ab.addTab(imageListTab);
		ActionBar.Tab imageGridTab = ab.newTab().setText("Grid").setTabListener(new TabListener<ImageGridFragment>(ImageGridFragment.class, this, ImageGridFragment.class.getSimpleName(), R.id.flContainer));
		ab.addTab(imageGridTab);
		ActionBar.Tab photoCollTab = ab.newTab().setText("Photo Collection").setTabListener(new TabListener<PhotoCollectionFragment>(PhotoCollectionFragment.class, this, PhotoCollectionFragment.class.getSimpleName(), R.id.flContainer));
		ab.addTab(photoCollTab);
		
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	}
	
	@Override
	protected void onDestroy() {
		imageLoader.clearAll();
		super.onDestroy();
	}

	public ImageManager getImageLoader(){
		return imageLoader;
	}
	
	private class TabListener<T extends Fragment> implements ActionBar.TabListener{
		
		private Fragment fragment;
		private Class<T> fClass;
		private Context context;
		private String tag;
		private int containerId;
		
		private TabListener(Class<T> fClass, Context context, String tag, int containerId) {
			this.fClass = fClass;
			this.context = context;
			this.tag = tag;
			this.containerId = containerId;
		}

		@Override
		public void onTabReselected(Tab arg0, FragmentTransaction ft) {
			
		}

		@Override
		public void onTabSelected(Tab arg0, FragmentTransaction ft) {
			if(fragment == null){
				fragment = Fragment.instantiate(context, fClass.getName());
				ft.add(containerId, fragment, tag);
			} else {
				ft.attach(fragment);
			}
		}

		@Override
		public void onTabUnselected(Tab arg0, FragmentTransaction ft) {
			if(fragment != null){
				ft.detach(fragment);
			}
		}
		
	}
	
}
