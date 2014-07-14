package com.example.fragmenttest1;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fragment2 extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment2, container, false);
	}
	/*
	 * @Override public View onCreateView( LayoutInflater inflater, ViewGroup
	 * container, Bundle savedInstanceState) {
	 * super.onCreate(savedInstanceState);
	 * 
	 * surfaceView = new AnimationSurfaceView(getActivity()); return
	 * surfaceView;
	 * 
	 * }
	 */
}
