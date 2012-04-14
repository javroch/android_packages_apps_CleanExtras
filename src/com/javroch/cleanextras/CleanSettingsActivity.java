package com.javroch.cleanextras;

import com.javroch.cleanextras.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.provider.Settings;

public class CleanSettingsActivity extends PreferenceActivity
	implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener,
		OnPreferenceChangeListener {

	/*
	 * Root Access
	 */
	private ListPreference mRootAccess;
    private static final String ROOT_ACCESS_KEY = "root_access";
    private static final String ROOT_ACCESS_PROPERTY = "persist.sys.clean.root";
    private static final String ROOT_ACCESS_DEFAULT = "0";
    private static final String ROOT_SETTINGS_PROPERTY = "ro.clean.root";
    private Object mSelectedRootValue;
    
    /*
     * Airplane mode option
     */
    private CheckBoxPreference mAirplaneModeOption;
    private static final String AIRPLANE_MODE_OPTION_KEY = Settings.System.AIRPLANE_MODE_OPTION;
    private static final int AIRPLANE_MODE_OPTION_DEFAULT = 1;
    private static final String AIRPLANE_MODE_SETTINGS_PROPERTY = "ro.clean.airplane_mode";
    
    /*
     * Silent mode option
     */
    private CheckBoxPreference mSilentModeOption;
    private static final String SILENT_MODE_OPTION_KEY = Settings.System.SILENT_MODE_OPTION;
    private static final int SILENT_MODE_OPTION_DEFAULT = 1;
    private static final String SILENT_MODE_SETTINGS_PROPERTY = "ro.clean.silent_mode";
    
    /*
     * Reboot option
     */
    private ListPreference mRebootOption;
    private static final String REBOOT_OPTION_KEY = Settings.System.REBOOT_OPTION;
    private static final int REBOOT_OPTION_DEFAULT = 1;
    private static final String REBOOT_SETTINGS_PROPERTY = "ro.clean.reboot";
    
    /*
     * Screenshot option
     */
    private CheckBoxPreference mScreenshotOption;
    private static final String SCREENSHOT_OPTION_KEY = Settings.System.SCREENSHOT_OPTION;
    private static final int SCREENSHOT_OPTION_DEFAULT = 1;
    private static final String SCREENSHOT_SETTINGS_PROPERTY = "ro.clean.screenshot";
    
    /*
     * Battery percentage option
     */
    private CheckBoxPreference mStatusBarBattery;
    private static final String STATUS_BAR_BATTERY_KEY = Settings.System.STATUS_BAR_BATTERY;
    private static final int STATUS_BAR_BATTERY_DEFAULT = 1;
    private static final String STATUS_BAR_BATTERY_SETTINGS_PROPERTY = "ro.clean.batt_percent";
    
    /*
     * Launcher screen count option
     */
    private ListPreference mLauncherScreenCount;
    private static final String LAUNCHER_SCREEN_COUNT_KEY = Settings.System.LAUNCHER_SCREEN_COUNT;
    private static final int LAUNCHER_SCREEN_COUNT_DEFAULT = 5;
    private static final String LAUNCHER_SCREEN_COUNT_PROPERTY = "ro.clean.launcher_screens";
	
	private Dialog mOkDialog;
	private boolean mOkClicked;	
	private String mCurrentDialog;
	
	private static final String SYSTEM_CATEGORY_KEY = "system_category";
	private static final String USER_INTERFACE_CATEGORY_KEY = "user_interface_category";
	private static final String POWER_MENU_CATEGORY_KEY = "power_menu_category";
	
	private PreferenceCategory mSystemCategory;
	private PreferenceCategory mUserInterfaceCategory;
	private PreferenceCategory mPowerMenuCategory;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.clean_extras);
		
		mSystemCategory = (PreferenceCategory) findPreference(SYSTEM_CATEGORY_KEY);
		mUserInterfaceCategory = (PreferenceCategory) findPreference(USER_INTERFACE_CATEGORY_KEY);
		mPowerMenuCategory = (PreferenceCategory) findPreference(POWER_MENU_CATEGORY_KEY);
		
		mRootAccess = (ListPreference) findPreference(ROOT_ACCESS_KEY);
		mRootAccess.setOnPreferenceChangeListener(this);

        mRebootOption = (ListPreference) findPreference(REBOOT_OPTION_KEY);
        mRebootOption.setOnPreferenceChangeListener(this);

        mScreenshotOption = (CheckBoxPreference) findPreference(SCREENSHOT_OPTION_KEY);
        
        mAirplaneModeOption = (CheckBoxPreference) findPreference(AIRPLANE_MODE_OPTION_KEY);
        
        mSilentModeOption = (CheckBoxPreference) findPreference(SILENT_MODE_OPTION_KEY);
        
        mStatusBarBattery = (CheckBoxPreference) findPreference(STATUS_BAR_BATTERY_KEY);
        
        mLauncherScreenCount = (ListPreference) findPreference(LAUNCHER_SCREEN_COUNT_KEY);
        mLauncherScreenCount.setOnPreferenceChangeListener(this);

		removeRootOptions();
		removeRebootOptions();
		removeScreenshotOptions();
		removeAirplaneModeOptions();
		removeSilentModeOptions();
		removeBatteryOptions();
		removeLauncherScreenOptions();
		
		removeEmptyCategories();
	}
	
	private void removeEmptyCategories() {
		PreferenceScreen screen = getPreferenceScreen();
		
		if (mSystemCategory.getPreferenceCount() == 0)
			screen.removePreference(mSystemCategory);
		
		if (mUserInterfaceCategory.getPreferenceCount() == 0)
			screen.removePreference(mUserInterfaceCategory);
		
		if (mPowerMenuCategory.getPreferenceCount() == 0)
			screen.removePreference(mPowerMenuCategory);
	}
	
	private void removeLauncherScreenOptions() {
		String launcherScreenSettings = SystemProperties.get(LAUNCHER_SCREEN_COUNT_PROPERTY, "");
		if (!"1".equals(launcherScreenSettings)) {
			if (mLauncherScreenCount != null) {
				mUserInterfaceCategory.removePreference(mLauncherScreenCount);
			}
		}
	}
	
	private void removeBatteryOptions() {
		String batterySettings = SystemProperties.get(STATUS_BAR_BATTERY_SETTINGS_PROPERTY, "");
		if (!"1".equals(batterySettings)) {
			if (mStatusBarBattery != null) {
				mUserInterfaceCategory.removePreference(mStatusBarBattery);
			}
		}
	}

	private void removeScreenshotOptions() {
        String screenshotSettings = SystemProperties.get(SCREENSHOT_SETTINGS_PROPERTY, "");
        if (!"1".equals(screenshotSettings)) {
            if (mScreenshotOption != null) {
                mPowerMenuCategory.removePreference(mScreenshotOption);
            }
        }
	}

	private void removeAirplaneModeOptions() {
        String airplaneModeSettings = SystemProperties.get(AIRPLANE_MODE_SETTINGS_PROPERTY, "");
        if (!"1".equals(airplaneModeSettings)) {
            if (mAirplaneModeOption != null) {
            	mPowerMenuCategory.removePreference(mAirplaneModeOption);
            }
        }
	}
	
	private void removeSilentModeOptions() {
		String silentModeSettings = SystemProperties.get(SILENT_MODE_SETTINGS_PROPERTY, "");
		if (!"1".equals(silentModeSettings)) {
			if (mSilentModeOption != null) {
				mPowerMenuCategory.removePreference(mSilentModeOption);
			}
		}
	}

	private void removeRebootOptions() {
        String rebootSettings = SystemProperties.get(REBOOT_SETTINGS_PROPERTY, "");
        if (!"1".equals(rebootSettings)) {
            if (mRebootOption != null) {
            	mPowerMenuCategory.removePreference(mRebootOption);
            }
        }
	}

	private void removeRootOptions() {
		String rootSettings = SystemProperties.get(ROOT_SETTINGS_PROPERTY, "");
		if (!Build.IS_DEBUGGABLE || "eng".equals(Build.TYPE) || !"1".equals(rootSettings)) {
			if (mRootAccess != null) {
				mSystemCategory.removePreference(mRootAccess);
			}
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		updateRootOptions();
		updateRebootOptions();
		updateScreenshotOptions();
		updateAirplaneModeOptions();
		updateSilentModeOptions();
		updateBatteryOptions();
		updateLauncherScreenOptions();
	}
	
	private void updateLauncherScreenOptions() {
		int value = Settings.System.getInt(getContentResolver(), LAUNCHER_SCREEN_COUNT_KEY, LAUNCHER_SCREEN_COUNT_DEFAULT);
		String strValue = String.valueOf(value);
		mLauncherScreenCount.setValue(strValue);
		mLauncherScreenCount.setSummary(getResources().getStringArray(R.array.launcher_screen_count_summaries)[mLauncherScreenCount.findIndexOfValue(strValue)]);
	}
	
	private void updateBatteryOptions() {
		Boolean value = Settings.System.getInt(getContentResolver(), STATUS_BAR_BATTERY_KEY, STATUS_BAR_BATTERY_DEFAULT) == 1;
		mStatusBarBattery.setChecked(value);
		mStatusBarBattery.setSummary(getResources().getStringArray(R.array.status_bar_battery_summaries)[value ? 1 : 0]);
	}

	private void updateScreenshotOptions() {
		Boolean value = Settings.System.getInt(getContentResolver(), SCREENSHOT_OPTION_KEY, SCREENSHOT_OPTION_DEFAULT) == 1;
        mScreenshotOption.setChecked(value);
        mScreenshotOption.setSummary(getResources().getStringArray(R.array.screenshot_option_summaries)[value ? 1 : 0]);
	}
	
	private void updateAirplaneModeOptions() {
		Boolean value = Settings.System.getInt(getContentResolver(), AIRPLANE_MODE_OPTION_KEY, AIRPLANE_MODE_OPTION_DEFAULT) == 1;
		mAirplaneModeOption.setChecked(value);
		mAirplaneModeOption.setSummary(getResources().getStringArray(R.array.airplane_mode_option_summaries)[value ? 1 : 0]);
	}
	
	private void updateSilentModeOptions() {
		Boolean value = Settings.System.getInt(getContentResolver(), SILENT_MODE_OPTION_KEY, SILENT_MODE_OPTION_DEFAULT) == 1;
		mSilentModeOption.setChecked(value);
		mSilentModeOption.setSummary(getResources().getStringArray(R.array.silent_mode_option_summaries)[value ? 1 : 0]);
	}

	private void updateRebootOptions() {
        int value = Settings.System.getInt(getContentResolver(), REBOOT_OPTION_KEY, REBOOT_OPTION_DEFAULT);
        mRebootOption.setValue(String.valueOf(value));
        mRebootOption.setSummary(getResources().getStringArray(R.array.reboot_option_summaries)[value]);
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
		} else if (preference == mRebootOption) {
			writeRebootOptions(newValue);
			return true;
		} else if (preference == mLauncherScreenCount) {
			writeLauncherScreenOptions(newValue);
			return true;
		}
		
		return false;
	}
	
	@Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		if (preference == mScreenshotOption) {
			writeScreenshotOptions();
		} else if (preference == mAirplaneModeOption) {
			writeAirplaneModeOptions();
		} else if (preference == mSilentModeOption) {
			writeSilentModeOptions();
		} else if (preference == mStatusBarBattery) {
			writeBatteryOptions();
		}
		
		return false;
	}
	
	private void writeLauncherScreenOptions(Object newValue) {
		Settings.System.putInt(getContentResolver(), LAUNCHER_SCREEN_COUNT_KEY, Integer.valueOf(newValue.toString()));
		updateLauncherScreenOptions();
	}
	
	private void writeBatteryOptions() {
		Settings.System.putInt(getContentResolver(), STATUS_BAR_BATTERY_KEY, mStatusBarBattery.isChecked() ? 1 : 0);
		updateBatteryOptions();
	}

	private void writeScreenshotOptions() {
		Settings.System.putInt(getContentResolver(), SCREENSHOT_OPTION_KEY, mScreenshotOption.isChecked() ? 1 : 0);
        updateScreenshotOptions();
	}
	
	private void writeAirplaneModeOptions() {
		Settings.System.putInt(getContentResolver(), AIRPLANE_MODE_OPTION_KEY, mAirplaneModeOption.isChecked() ? 1 : 0);
		updateAirplaneModeOptions();
	}
	
	private void writeSilentModeOptions() {
		Settings.System.putInt(getContentResolver(), SILENT_MODE_OPTION_KEY, mSilentModeOption.isChecked() ? 1 : 0);
		updateSilentModeOptions();
	}

	private void writeRebootOptions(Object newValue) {
		Settings.System.putInt(getContentResolver(), REBOOT_OPTION_KEY, Integer.valueOf(newValue.toString()));
        updateRebootOptions();
	}

	private void writeRootOptions(Object newValue) {
		String oldValue = SystemProperties.get(ROOT_ACCESS_PROPERTY, ROOT_ACCESS_DEFAULT);
		SystemProperties.set(ROOT_ACCESS_PROPERTY, newValue.toString());
        if (Integer.valueOf(newValue.toString()) < 2 && !oldValue.equals(newValue.toString())
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
