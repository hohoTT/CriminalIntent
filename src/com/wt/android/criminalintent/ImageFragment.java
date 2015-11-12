package com.wt.android.criminalintent;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageFragment extends DialogFragment {

	private ImageView mImageView;

	public static final String EXTRA_IMAGE_PATH = "com.wt.android.criminalintent.image_path";

	public static ImageFragment newInstance(String imagepath) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_IMAGE_PATH, imagepath);

		ImageFragment fragment = new ImageFragment();
		fragment.setArguments(args);
		fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);

		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mImageView = new ImageView(getActivity());
		String path = (String) getArguments().getSerializable(EXTRA_IMAGE_PATH);

		BitmapDrawable image = PictureUtils.getScaledDrawable(getActivity(),
				path);

		mImageView.setImageDrawable(image);

		return mImageView;
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		PictureUtils.cleanImageView(mImageView);
	}
}
