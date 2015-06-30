package com.thenomads.android.nomadlive.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

import com.thenomads.android.nomadlive.R;

import java.util.Arrays;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {

    private static final String TAG = "SettingsActivity";
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            // For all other preferences, set the summary to the value's
            // simple string representation.
            preference.setSummary(stringValue);

            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        populateSettings();
    }

    /**
     * Also called after modifying a setting programatically to update the UI.
     */
    private void populateSettings() {

        addPreferencesFromResource(R.xml.pref_general);

        // Display secret key on same screen.
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_cine_secret)));

        // Allow all settings to be reset.
        bindResetToButton();

        // Pulls the version from the package info.
        Preference p = findPreference(getString(R.string.pref_key_version));
        p.setSummary(getVersionName());

    }

    /**
     * Resets all settings to their default value, then refreshes the page.
     */
    private void bindResetToButton() {

        Preference button = findPreference(getString(R.string.pref_key_reset));

        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                resetSettings();

                return true;
            }
        });
    }

    private String getVersionName() {
        String versionName = "NOT_FOUND";

        PackageInfo packageInfo;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
            versionName += " (build " + packageInfo.versionCode + ")";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionName;
    }

    private void resetSettings() {

        Log.d(TAG, "Resetting settings to defaults...");

        // Resets settings loading from the XML file.
        Context context = getBaseContext();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        // Values before doing anything.
        Log.d(TAG, Arrays.toString(sp.getAll().entrySet().toArray()));

        // Delete all settings and reload from defaults.
        sp.edit().clear().commit();
        PreferenceManager.setDefaultValues(context, R.xml.pref_general, true);

        // Makes sure the new settings have been applied.
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        Log.d(TAG, Arrays.toString(sp.getAll().entrySet().toArray()));

        // Refreshes the screen.
        // http://stackoverflow.com/questions/8003098/how-do-you-refresh-preferenceactivity-to-show-changes-in-the-settings
        setPreferenceScreen(null);
        populateSettings();

        Log.d(TAG, "Settings reset done.");
    }

}
