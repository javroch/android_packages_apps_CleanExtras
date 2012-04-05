package com.javroch.cleanoptions;

import com.javroch.cleanoptions.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.provider.Settings;

public class CleanSettingsActivity extends PreferenceActivity
	implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener,
		OnPreferenceChangeListener {

	private ListPreference mRootAccess;
	
    private static final String ROOT_ACCESS_KEY = "root_access";
    private static final String ROOT_ACCESS_PROPERTY = "persist.sys.clean.root";
    private static final String ROOT_ACCESS_DEFAULT = "0";
    private static final String ROOT_SETTINGS_PROPERTY = "ro.clean.root";
	
	private Dialog mOkDialog;
	private boolean mOkClicked;	
	private String mCurrentDialog;
    private Object mSelectedRootValue;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.clean_options);
		
		mRootAccess = (ListPreference) findPreference(ROOT_ACCESS_KEY);
		mRootAccess.setOnPreferenceChangeListener(this);
		
		removeRootOptions();
	}

	private void removeRootOptions() {
		String rootSettings = SystemProperties.get(ROOT_SETTINGS_PROPERTY, "");
		if (!Build.IS_DEBUGGABLE || "eng".equals(Build.TYPE) || !"1".equals(rootSettings)) {
			if (mRootAccess != null)
				getPreferenceScreen().removePreference(mRootAccess);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		updateRootOptions();
	}

	private void updateRootOptions() {
        String value = SystemProperties.get(ROOT_ACCESS_PROPERTY, ROOT_ACCESS_DEFAULT);
        mRootAccess.setValue(value);
        mRootAccess.setSummary(getResources().getStringArray(R.array.root_access_summaries)[Integer.valueOf(value)]);
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference == mRootAccess) {
            if ("0".equals(SystemProperties.get(ROOT_ACCESS_PROPERTY, ROOT_ACCESS_DEFAULT))
                && !"0".equals(newValue)) {

                mSelectedRootValue = newValue;
                mOkClicked = false;
                dismissDialog();
                mOkDialog = new AlertDialog.Builder(this).setMessage(
                    getResources().getString(R.string.root_access_warning_message))
                    .setTitle(R.string.root_access_warning_title)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, this)
                    .setNegativeButton(android.R.string.no, this)
                    .show();
                mCurrentDialog = ROOT_ACCESS_KEY;
                mOkDialog.setOnDismissListener(this);
            } else {
                writeRootOptions(newValue);
            }
            return true;
		}
		
		return false;
	}

	private void writeRootOptions(Object newValue) {
        String oldValue = SystemProperties.get(ROOT_ACCESS_PROPERTY, ROOT_ACCESS_DEFAULT);
        SystemProperties.set(ROOT_ACCESS_PROPERTY, newValue.toString());
        if (Integer.valueOf(newValue.toString()) < 2 && !oldValue.equals(newValue)
            && "1".equals(SystemProperties.get("service.adb.root", "0"))) {

            SystemProperties.set("service.adb.root", "0");
            Settings.Secure.putInt(getContentResolver(),
                Settings.Secure.ADB_ENABLED, 0);
            Settings.Secure.putInt(getContentResolver(),
                Settings.Secure.ADB_ENABLED, 1);
        }
        updateRootOptions();
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		if (!mOkClicked) {
			if (mCurrentDialog.equals(ROOT_ACCESS_KEY)) {
				writeRootOptions("0");
			}
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
			mOkClicked = true;

			if (mCurrentDialog.equals(ROOT_ACCESS_KEY)) {
				writeRootOptions(mSelectedRootValue);
			}
		} else {
			mOkClicked = false;

			if (mCurrentDialog.equals(ROOT_ACCESS_KEY)) {
				writeRootOptions("0");
			}
		}
	}
	
	@Override
	public void onDestroy() {
		dismissDialog();
		super.onDestroy();
	}

	private void dismissDialog() {
        if (mOkDialog == null) return;
        mOkDialog.dismiss();
        mOkDialog = null;	
	}
}