package com.wt.android.criminalintent;

import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

public class Crime {

	private UUID mId;
	private String mtitle;
	private Date mDate;
	private boolean mSolved;
	private Photo mPhoto;
	private String mSuspect;

	private static final String JSON_ID = "id";
	private static final String JSON_TITLE = "title";
	private static final String JSON_SOLVED = "solved";
	private static final String JSON_DATE = "date";
	private static final String JSON_PHOTO = "photo";
	private static final String JSON_SUSPECT = "suspect";

	public Crime() {
		// 生成唯一的标识符
		mId = UUID.randomUUID();
		mDate = new Date();
	}

	public Crime(JSONObject json) throws JSONException {
		mId = UUID.fromString(json.getString(JSON_ID));
		if (json.has(JSON_TITLE)) {
			mtitle = json.getString(JSON_TITLE);
		}
		mSolved = json.getBoolean(JSON_SOLVED);
		mDate = new Date(json.getLong(JSON_DATE));
		if (json.has(JSON_PHOTO)) {
			mPhoto = new Photo(json.getJSONObject(JSON_PHOTO));
		}
		if (json.has(JSON_SUSPECT)) {
			mSuspect = json.getString(JSON_SUSPECT);
		}
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSON_ID, mId.toString());
		json.put(JSON_TITLE, mtitle);
		json.put(JSON_SOLVED, mSolved);
		json.put(JSON_DATE, mDate.getTime());
		if (mPhoto != null) {
			json.put(JSON_PHOTO, mPhoto.toJSON());
		}
		json.put(JSON_SUSPECT, mSuspect);
		return json;
	}

	public UUID getId() {
		return mId;
	}

	public String getTitle() {
		return mtitle;
	}

	public void setTitle(String title) {
		mtitle = title;
	}

	public Date getDate() {
		return mDate;
	}

	public void setDate(Date Date) {
		mDate = Date;
	}

	public boolean isSolved() {
		return mSolved;
	}

	public void setSolved(boolean Solved) {
		mSolved = Solved;
	}

	public Photo getPhoto() {
		return mPhoto;
	}

	public void setPhoto(Photo p) {
		mPhoto = p;
	}

	public String getSuspect() {
		return mSuspect;
	}

	public void setSuspect(String suspect) {
		mSuspect = suspect;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return mtitle;
	}

}
