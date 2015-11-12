package com.wt.android.criminalintent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.json.JSONException;

import android.content.Context;
import android.util.Log;

public class CrimeLab {

	private static CrimeLab sCrimeLab;
	private Context mAppContext;

	private ArrayList<Crime> mCrimes;
	private CriminalIntentJSONSerializer mSerializer;

	private static final String TAG = "CrimeLab";
	private static final String FILENAME = "crime.json";

	public CrimeLab(Context appContext) {
		// TODO Auto-generated constructor stub
		mAppContext = appContext;
		mSerializer = new CriminalIntentJSONSerializer(mAppContext, FILENAME);

		try {
			mCrimes = mSerializer.loadCrimes();
		} catch (Exception e) {
			mCrimes = new ArrayList<Crime>();
			Log.e(TAG, "Error loading crimes: ", e);
		}
	}

	public void addCrime(Crime c) {
		mCrimes.add(c);
	}

	public static CrimeLab get(Context c) {
		if (sCrimeLab == null) {
			sCrimeLab = new CrimeLab(c.getApplicationContext());
		}
		return sCrimeLab;
	}

	public ArrayList<Crime> getCrimes() {
		return mCrimes;
	}

	public Crime getCrime(UUID id) {
		for (Crime c : mCrimes) {
			if (c.getId().equals(id))
				return c;
		}
		return null;
	}

	public void deleteCrime(Crime c) {
		mCrimes.remove(c);
		// �Լ����еĸ��ģ�ɾ��ʱҲҪ�������ݵı��棻���ɾ����ֱ���˳����ٴδ�Ӧ�õ�ʱ���ϴζ����ݵ�ɾ����������Ч
		try {
			mSerializer.saveCrimes(mCrimes);
			Log.e(TAG, "deleted -- crime saved to file");
		} catch (Exception e) {
			// TODO: handle exception
			mCrimes = new ArrayList<Crime>();
		}
	}

	public boolean saveCrimes() {
		try {
			mSerializer.saveCrimes(mCrimes);
			Log.e(TAG, "crimes saved to file");
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, "Error saving crimes: ", e);
			return false;
		}
	}
}
