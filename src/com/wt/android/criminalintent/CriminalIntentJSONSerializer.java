package com.wt.android.criminalintent;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.Buffer;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;

public class CriminalIntentJSONSerializer {

	private Context mContext;
	private String mFilename;

	public CriminalIntentJSONSerializer(Context c, String f) {
		// TODO Auto-generated constructor stub
		mContext = c;
		mFilename = f;
	}

	public ArrayList<Crime> loadCrimes() throws IOException, JSONException {
		ArrayList<Crime> crimes = new ArrayList<Crime>();
		BufferedReader reader = null;
		try {
			// Open and read the file into a StringBuilder
			InputStream in = mContext.openFileInput(mFilename);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuffer jsonString = new StringBuffer();
			String line = null;
			while ((line = reader.readLine()) != null) {
				// Line breaks are omitted and irrelevent
				jsonString.append(line);
			}
			// Parse the JSON using JSONTokener
			JSONArray array = (JSONArray) new JSONTokener(jsonString.toString())
					.nextValue();
			// Build the array of crimes from JSONObejects
			for (int i = 0; i < array.length(); i++) {
				crimes.add(new Crime(array.getJSONObject(i)));
			}
		} catch (FileNotFoundException e) {
			// Ignore this one, it happens when starting fresh
		} finally {
			if (reader != null)
				reader.close();
		}

		return crimes;
	}

	public void saveCrimes(ArrayList<Crime> crimes) throws JSONException,
			IOException {
		// Build an array in JSON
		JSONArray array = new JSONArray();
		for (Crime c : crimes) {
			array.put(c.toJSON());
		}

		// Write the file to disk
		Writer writer = null;
		try {
			OutputStream out = mContext.openFileOutput(mFilename,
					Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(out);
			writer.write(array.toString());
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}
