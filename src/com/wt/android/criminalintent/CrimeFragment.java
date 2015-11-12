package com.wt.android.criminalintent;

import java.util.Date;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;

public class CrimeFragment extends Fragment {

	private Crime mCrime;
	private EditText mTitleField;
	private Button mDateButton;
	private CheckBox mSolvedCheckBox;
	private ImageButton mPhotoButton;
	private ImageView mPhotoView;
	private Button mSuspectButton;

	private static final int REQUEST_DATE = 0;
	private static final int REQUEST_PHOTO = 1;
	private static final int REQUEST_CONTACR = 2;

	public static final String EXTRA_CRIME_ID = "com.wt.android.criminalintent.crime_id";
	private static final String DIALOG_DATE = "date";
	private static final String TAG = "CrimeFragment";
	private static final String DIALOG_IMAGE = "image";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		UUID crimeid = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);

		mCrime = CrimeLab.get(getActivity()).getCrime(crimeid);
	}

	public void updateDate() {
		mDateButton.setText(mCrime.getDate().toString());
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode != Activity.RESULT_OK)
			return;
		if (requestCode == REQUEST_DATE) {
			Date date = (Date) data
					.getSerializableExtra(DatePickerFrament.EXTRA_DATE);
			mCrime.setDate(date);
			updateDate();
		} else if (requestCode == REQUEST_PHOTO) {
			// Create a new photo object and attach it to the crime
			String filename = data
					.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
			if (filename != null) {
				// Log.i(TAG, "filename: " + filename);
				Photo p = new Photo(filename);
				mCrime.setPhoto(p);
				showPhoto();
				// Log.i(TAG, "Crime: " + mCrime.getTitle() + " has a photo");
			}
		} else if (requestCode == REQUEST_CONTACR) {
			Uri contactUri = data.getData();

			// Specify which fields you want your query to return values for
			String[] queryFields = new String[] { ContactsContract.Contacts.DISPLAY_NAME };

			// Perform your query - the contactUri is like a "where" clause here
			Cursor c = getActivity().getContentResolver().query(contactUri,
					queryFields, null, null, null);

			// Double-check that you actually got results
			if (c.getCount() == 0) {
				c.close();
				return;
			}

			// Pull out the first column of the first row of data - that is your
			// suspect's name
			c.moveToFirst();
			String suspect = c.getString(0);
			mCrime.setSuspect(suspect);
			mSuspectButton.setText(suspect);
			c.close();
		}
	}

	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_crime, parent, false);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (NavUtils.getParentActivityName(getActivity()) != null) {
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
		}

		mTitleField = (EditText) v.findViewById(R.id.crime_title);
		mTitleField.setText(mCrime.getTitle());
		mTitleField.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence c, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				mCrime.setTitle(c.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence c, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable c) {
				// TODO Auto-generated method stub

			}
		});

		mDateButton = (Button) v.findViewById(R.id.crime_date);
		updateDate();
		mDateButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FragmentManager fm = getActivity().getSupportFragmentManager();
				DatePickerFrament dialog = DatePickerFrament.newInstance(mCrime
						.getDate());
				dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
				dialog.show(fm, DIALOG_DATE);
			}
		});

		mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
		mSolvedCheckBox.setChecked(mCrime.isSolved());
		mSolvedCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// Set the crime's solved property
						mCrime.setSolved(isChecked);
					}
				});

		mPhotoButton = (ImageButton) v.findViewById(R.id.crime_imageButton);
		mPhotoButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
				startActivityForResult(i, REQUEST_PHOTO);
			}
		});

		mPhotoView = (ImageView) v.findViewById(R.id.crime_imageView);
		mPhotoView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Photo p = mCrime.getPhoto();
				if (p == null)
					return;

				FragmentManager fm = getActivity().getSupportFragmentManager();

				String path = getActivity().getFileStreamPath(p.getFilename())
						.getAbsolutePath();

				ImageFragment.newInstance(path).show(fm, DIALOG_IMAGE);
			}
		});

		// If camera is not available, disable camera functionality
		PackageManager pm = getActivity().getPackageManager();
		boolean hasACamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
				|| pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)
				|| Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD
				|| Camera.getNumberOfCameras() > 0;
		if (!hasACamera) {
			mPhotoButton.setEnabled(false);
		}

		Button reportButton = (Button) v.findViewById(R.id.crime_reportButton);
		reportButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("text/plain");
				i.putExtra(Intent.EXTRA_TEXT, getCrimeResort());
				i.putExtra(Intent.EXTRA_SUBJECT,
						getString(R.string.crime_report_subject));
				i = Intent.createChooser(i, getString(R.string.send_report));
				startActivity(i);
			}
		});

		mSuspectButton = (Button) v.findViewById(R.id.crime_suspectButton);
		mSuspectButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(Intent.ACTION_PICK,
						ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(i, REQUEST_CONTACR);
			}
		});

		if (mCrime.getSuspect() != null) {
			mSuspectButton.setText(mCrime.getSuspect());
		}

		return v;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			// To be implemented next
			if (NavUtils.getParentActivityName(getActivity()) != null) {
				NavUtils.navigateUpFromSameTask(getActivity());
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public static CrimeFragment newInstance(UUID crimeId) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_CRIME_ID, crimeId);

		CrimeFragment fragment = new CrimeFragment();
		fragment.setArguments(args);

		return fragment;
	}

	private void showPhoto() {
		// (Re)set the image button's image based on our photo
		Photo p = mCrime.getPhoto();
		BitmapDrawable b = null;
		if (p != null) {
			String path = getActivity().getFileStreamPath(p.getFilename())
					.getAbsolutePath();
			b = PictureUtils.getScaledDrawable(getActivity(), path);
		}
		mPhotoView.setImageDrawable(b);
	}

	private String getCrimeResort() {
		String solvedString = null;
		if (mCrime.isSolved()) {
			solvedString = getString(R.string.crime_report_solved);
		} else {
			solvedString = getString(R.string.crime_report_unsolved);
		}

		String dateFormat = "EEE, MMM dd";
		String dateString = DateFormat.format(dateFormat, mCrime.getDate())
				.toString();

		String suspect = mCrime.getSuspect();
		if (suspect == null) {
			suspect = getString(R.string.crime_report_no_suspect);
		} else {
			suspect = getString(R.string.crime_report_suspect, suspect);
		}

		String report = getString(R.string.crime_report, mCrime.getTitle(),
				dateString, solvedString, suspect);

		return report;
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		showPhoto();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		PictureUtils.cleanImageView(mPhotoView);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		CrimeLab.get(getActivity()).saveCrimes();
	}
}
