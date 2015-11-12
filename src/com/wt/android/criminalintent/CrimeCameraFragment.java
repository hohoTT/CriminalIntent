package com.wt.android.criminalintent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class CrimeCameraFragment extends Fragment {

	private Camera mCamera;
	private SurfaceView mSurfaceView;
	private View mProgressContainer;

	private static final String TAG = "CrimeCameraFragment";
	public static final String EXTRA_PHOTO_FILENAME = "com.wt.android.criminalintent.photo_filename";

	@Override
	@SuppressWarnings("deprecation")
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater
				.inflate(R.layout.fragment_crime_camera, parent, false);

		mProgressContainer = v
				.findViewById(R.id.crime_camera_progressContainer);
		mProgressContainer.setVisibility(View.INVISIBLE); // 来设置控件的可见性

		Button takePictureButton = (Button) v
				.findViewById(R.id.crime_camera_takePictureButton);

		takePictureButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mCamera != null) {
					mCamera.takePicture(mShutterCallback, null, mJpegCallback);
				}
			}
		});

		mSurfaceView = (SurfaceView) v
				.findViewById(R.id.crime_camera_surfaceView);
		SurfaceHolder holder = mSurfaceView.getHolder();
		// setTtype() and SURFACE_TYPE_PUSH_BUFFERS are both deprecated
		// but are required for Camera preview to work on pre-3.0 devices
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.addCallback(new SurfaceHolder.Callback() {

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// Tell the camera to use this surface as its preview area
				try {
					if (mCamera != null) {
						mCamera.setPreviewDisplay(holder);
					}
				} catch (IOException exception) {
					// TODO: handle exception
					Log.e(TAG, "Eorror setting up preview display", exception);
				}

			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// We can no longer display on this surface, so stop the
				// preview.
				if (mCamera != null) {
					mCamera.stopPreview();
				}
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int w,
					int h) {
				if (mCamera == null)
					return;
				// The surface has change size; update the camera preview size
				Camera.Parameters parameters = mCamera.getParameters();
				Size s = getBestSupportedSize(
						parameters.getSupportedPreviewSizes(), w, h);
				parameters.setPreviewSize(s.width, s.height);
				s = getBestSupportedSize(parameters.getSupportedPictureSizes(),
						w, h);
				parameters.setPictureSize(s.width, s.height);
				mCamera.setParameters(parameters);
				try {
					mCamera.startPreview();
				} catch (Exception e) {
					// TODO: handle exception
					Log.e(TAG, "Could not start preview", e);
					mCamera.release();
					mCamera = null;
				}
			}
		});

		return v;
	}

	private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {

		@Override
		public void onShutter() {
			// Display the progess indicator
			mProgressContainer.setVisibility(View.VISIBLE);
		}
	};

	private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// Create a filename
			String filename = UUID.randomUUID().toString();
			// Save th jpeg data to disk
			FileOutputStream os = null;
			boolean success = true;

			try {
				os = getActivity().openFileOutput(filename,
						Context.MODE_PRIVATE);
				os.write(data);
			} catch (Exception e) {
				// TODO: handle exception
				Log.e(TAG, "Error writing to file " + e);
				success = false;
			} finally {
				try {
					if (os != null) {
						os.close();
					}
				} catch (Exception e) {
					// TODO: handle exception
					Log.e(TAG, "Error closing file " + filename, e);
					success = false;
				}
			}

			// Set the photo filename on the result intent
			if (success) {
				// Log.e(TAG, "JPEG save at " + filename);
				Intent i = new Intent();
				i.putExtra(EXTRA_PHOTO_FILENAME, filename);
				getActivity().setResult(Activity.RESULT_OK, i);
			} else {
				getActivity().setResult(Activity.RESULT_CANCELED);
			}
			getActivity().finish();
		}
	};

	private Size getBestSupportedSize(List<Size> sizes, int width, int height) {
		Size bestsize = sizes.get(0);
		int largestArea = bestsize.width * bestsize.height;
		for (Size s : sizes) {
			int area = s.width * s.height;
			if (area > largestArea) {
				bestsize = s;
				largestArea = area;
			}
		}

		return bestsize;
	}

	@TargetApi(9)
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			mCamera = Camera.open(0);
		} else {
			mCamera = Camera.open();
		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}
}
