package com.stripe1.xmouse;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import java.io.File;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    private static final int REQUEST_WRITE_STORAGE = 112;

    static int selectedHostIndex=0;
    CharSequence[] hostsNames;
    CharSequence[] hostsValues;
    ListPreference listPrefHost;
    ListPreference listPrefKeys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    final Context ctx = this;


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupSimplePreferencesScreen();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                // TODO: If Settings has multiple levels, Up should navigate up
                // that hierarchy.
                NavUtils.navigateUpFromSameTask(this);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    private void setupSimplePreferencesScreen() {


        // In the simplified UI, fragments are not used at all and we instead
        // use the older PreferenceActivity APIs.

        // Add 'general' preferences.


        //addPreferencesFromResource(R.xml.pref_connection);
        PreferenceCategory fakeHeader = new PreferenceCategory(this);

        // Add 'notifications' preferences, and a corresponding header.
        //fakeHeader = new PreferenceCategory(this);
        //fakeHeader.setTitle("Connections");
        //getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_connection);

        /*fakeHeader.setTitle(R.string.pref_header_data_sync);
        getPreferenceScreen().addPreference(fakeHeader);

        ArrayList<ArrayList<String>> hosts = MainActivity.db.listAll(DatabaseHandler.HOST_TABLE_NAME,new String[]{"Alias","Host","Username","Port","Password","id"});
        hostsNames = new CharSequence[hosts.size()];
        hostsValues = new CharSequence[hosts.size()];

        for(int i=0;i<hosts.size();i++){

            String desc = hosts.get(i).get(0)+" ["+hosts.get(i).get(2)+"@"+hosts.get(i).get(1)+":"+hosts.get(i).get(3)+"] id="+hosts.get(i).get(5);
            String val = hosts.get(i).get(5);

            hostsNames[i]=desc;
            hostsValues[i]=val;
            //Log.d("setting", "-"+hosts.get(i).get(0)+"-");
        }
        //Log.d("setting", "-"+hosts.size()+"-");
        if(hosts.size()!=0){
            listPrefHost = new ListPreference(this);
            listPrefHost.setKey("hostPreferenceList"); //Refer to get the pref value
            listPrefHost.setDialogTitle("Select Host");
            listPrefHost.setTitle("Host Computer");
            //listPref.setSummary("Select Host");
            listPrefHost.setEntries(hostsNames);
            listPrefHost.setEntryValues(hostsValues);
            listPrefHost.setDefaultValue(hostsValues[0]);

            getPreferenceScreen().addPreference(listPrefHost);

            bindPreferenceSummaryToValue(findPreference("hostPreferenceList"));

            Preference pref = new Preference(this);
            pref.setTitle("- Remove selected Host");
            pref.setSummary("");
            pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

                    builder.setMessage("Really delete Host?\n\n"+hostsNames[selectedHostIndex]);
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {   }
                    });
                    builder.setPositiveButton("Yes, Delete", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {

                            if(hostsValues[selectedHostIndex].toString().equals("")){

                                Toast.makeText(getApplicationContext(), "Nothing happened", Toast.LENGTH_SHORT).show();
                            }else{
                                int r = Integer.valueOf(hostsValues[selectedHostIndex].toString());

                                int rowsAffected = MainActivity.db.deleteRow(DatabaseHandler.HOST_TABLE_NAME,r);

                                Toast.makeText(getApplicationContext(), rowsAffected+" Record(s) affected", Toast.LENGTH_SHORT).show();

                                hostsNames[selectedHostIndex]="Recently Deleted";
                                hostsValues[selectedHostIndex]="";

                                listPrefHost.setEntries(hostsNames);
                                listPrefHost.setEntryValues(hostsValues);
                                listPrefHost.setSummary("Select Host");
                            }
                        }

                    });

                    builder.show();
                    return false;
                }

            });

            getPreferenceScreen().addPreference(pref);

        }
        Preference pref = new Preference(this);
        pref.setTitle("+ Add new Host");
        pref.setSummary("");
        Intent newHostActivity = new Intent(SettingsActivity.this,NewHostSettingsActivity.class);
        pref.setIntent(newHostActivity);
        getPreferenceScreen().addPreference(pref);

        */
        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle("Authentication");
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_auth);
        //addPreferencesFromResource(R.xml.ssh_conn);
        final Preference myPref = (Preference) findPreference("pref_addkeybutton");


        myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {

                //check if we have permissions via android 6


                //ask for the permission in android M
                int permission = ContextCompat.checkSelfPermission(SettingsActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    Log.i("SettingsActivity", "Permission to record denied");

                    if (ActivityCompat.shouldShowRequestPermissionRationale(SettingsActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                        builder.setMessage("Access the SD-CARD is required.")
                                .setTitle("Permission required");

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                Log.i("SettingsActivity", "Clicked ok for permission");

                                ActivityCompat.requestPermissions(SettingsActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        REQUEST_WRITE_STORAGE);
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();

                    } else {

                        ActivityCompat.requestPermissions(SettingsActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_WRITE_STORAGE);
                    }
                }

                //open browser or intent here

                //File mPath = new File(Environment.getExternalStorageDirectory(), null);
                FileDialog fileDialog = new FileDialog(SettingsActivity.this, null);
                //fileDialog.setFileEndsWith(".txt");
                //fileDialog.setSelectDirectoryOption(true);
                fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
                    public void fileSelected(File file) {
                        Log.d(getClass().getName(), "selected file " + file.toString());
                        //SharedPreferences settings = getSharedPreferences(String n, MODE_PRIVATE);
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("pref_addkeybutton", file.toString());
                        editor.commit();
                        myPref.setSummary(file.toString());
                    }
                });
                //fileDialog.addDirectoryListener(new FileDialog.DirectorySelectedListener() {
                //  public void directorySelected(File directory) {
                //      Log.d(getClass().getName(), "selected dir " + directory.toString());
                //  }
                //});
                //fileDialog.setSelectDirectoryOption(false);
                fileDialog.showDialog();

                return true;
            }
        });

        //listPrefKeys = (ListPreference)this.findPreference("pref_usekeyauth_list");
        //listPrefKeys = new ListPreference(this);
        //listPrefKeys.setKey("pref_usekeyauth_list"); //Refer to get the pref value
        //listPrefKeys.setDependency("pref_usekeyauth");
        //listPrefKeys.setDialogTitle("Select Key");
        //listPrefKeys.setTitle("Selected Key");
        //listPrefKeys.setEnabled(false);


        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle("Gyro");
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_gyro);


        bindPreferenceSummaryToValue(findPreference("gyro_z_sensitivity_list"));
        bindPreferenceSummaryToValue(findPreference("gyro_y_sensitivity_list"));
        bindPreferenceSummaryToValue(findPreference("gyro_z_threshold_list"));
        bindPreferenceSummaryToValue(findPreference("gyro_y_threshold_list"));


        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle("Joystick");
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_js);


        bindPreferenceSummaryToValue(findPreference("js_sensitivity_list"));
        bindPreferenceSummaryToValue(findPreference("js_size_list"));
        bindPreferenceSummaryToValue(findPreference("js_dead_zone_list"));



        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle("Keyboard");
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_keyboard);
        // Bind the summaries of EditText/List/Dialog/Ringtone preferences to
        // their values. When their values change, their summaries are updated
        // to reflect the new value, per the Android Design guidelines.



        //bindPreferenceSummaryToValue(findPreference("setting_pass"));
        //bindPreferenceSummaryToValue(findPreference("autologin_checkbox"));
        bindPreferenceSummaryToValue(findPreference("pref_addkeybutton"));
        bindPreferenceSummaryToValue(findPreference("sensitivity_list"));
        bindPreferenceSummaryToValue(findPreference("delay_list"));
        bindPreferenceSummaryToValue(findPreference("mdelay_list"));
        bindPreferenceSummaryToValue(findPreference("setting_xdotool_initial"));
        bindPreferenceSummaryToValue(findPreference("setting_shell"));

        //bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        //bindPreferenceSummaryToValue(findPreference("sync_frequency"));

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {

                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {

                    Log.i("SettingsActivity", "Permission has been denied by user");

                } else {

                    Log.i("SettingsActivity", "Permission has been granted by user");

                }
                return;
            }
        }
    }
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary("Silent");

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
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




}
