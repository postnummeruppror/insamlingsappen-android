package insamlingsappen.postnummeruppror.nu.insamlingsappen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import insamlingsappen.postnummeruppror.nu.insamlingsappen.commands.CreateLocationSample;
import insamlingsappen.postnummeruppror.nu.insamlingsappen.commands.GetLocationStatistics;
import insamlingsappen.postnummeruppror.nu.insamlingsappen.commands.SetAccount;


public class InsamlingActivity extends ActionBarActivity implements LocationListener {

  private HttpClient httpClient = new DefaultHttpClient();
  ;

  private Account account;
  private Location currentLocation;

  private View location_fix_view;
  private View content_view;
  private View account_setup_view;

  private TextView location_timestampTextView;
  private TextView location_latitudeTextView;
  private TextView location_longitudeTextView;
  private TextView location_accuracyTextView;
  private TextView location_altitudeTextView;
  private TextView location_providerTextView;

  private EditText location_postalCodeEditText;
  private Button location_submitPostalCodeButton;

  private Button server_statistics_refreshButton;

  private TextView server_statistics_numberOfAccountsTextView;
  private TextView server_statistics_numberOfLocationSamplesTextView;

  private EditText account_setup_emailAddressEditText;
  private CheckBox account_setup_ccZeroCheckBox;
  private Button account_setup_submitAccount;

  private LocationManager locationManager;
  private String provider;

  private SharedPreferences accountPreferences;

  private long maximumMillisecondsAgeOfLocationFix = 10 * 1000; // 10 seconds
  private AssertGoodLocationFixRunnable assertGoodLocationFixRunnable = new AssertGoodLocationFixRunnable();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Required to execute HTTP post from within the application thread on newer Android devices.
    if (android.os.Build.VERSION.SDK_INT > 8) {
      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
      StrictMode.setThreadPolicy(policy);
    }


    accountPreferences = getSharedPreferences("account", Context.MODE_PRIVATE);

//    // todo debug remove! makes sure the UI really works by showing account settings first!
//    SharedPreferences.Editor editor = accountPreferences.edit();
//    editor.putString("account.identity", null);
//    editor.commit();
//

    setContentView(R.layout.activity_insamling);

    /*
     *
     *
     *
     *
     *
     *
     */


    // Define views.

    location_fix_view = findViewById(R.id.location_fix);
    content_view = findViewById(R.id.content);
    account_setup_view = findViewById(R.id.account_setup);

    // Hide all views.
    // What we display depends on logic further in to the application.

    location_fix_view.setVisibility(View.GONE);
    account_setup_view.setVisibility(View.GONE);
    content_view.setVisibility(View.GONE);


    // Define all widgets the application touch.

    account_setup_ccZeroCheckBox = (CheckBox) findViewById(R.id.account_setup_cc_zero);
    account_setup_emailAddressEditText = (EditText) findViewById(R.id.account_setup_email_address);
    account_setup_submitAccount = (Button) findViewById(R.id.account_setup_submit_account);


    // make cc0-link clickable
    account_setup_emailAddressEditText.setMovementMethod(LinkMovementMethod.getInstance());

    account_setup_submitAccount.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        if (!account_setup_ccZeroCheckBox.isChecked()) {
          Toast.makeText(InsamlingActivity.this, "Du måste godkänna CC0 för att fortsätta!", Toast.LENGTH_LONG).show();
          return;
        }

        if (createAccount()) {
          account_setup_view.setVisibility(View.GONE);
          if (assertGoodLocationFixRunnable.stop) {
            new Thread(assertGoodLocationFixRunnable).start();
          }
        }
      }
    });


    location_timestampTextView = (TextView) findViewById(R.id.location_timestamp);
    location_latitudeTextView = (TextView) findViewById(R.id.location_latitude);
    location_longitudeTextView = (TextView) findViewById(R.id.location_longitude);
    location_accuracyTextView = (TextView) findViewById(R.id.location_accuracy);
    location_altitudeTextView = (TextView) findViewById(R.id.location_altitude);
    location_providerTextView = (TextView) findViewById(R.id.provider);

    location_postalCodeEditText = (EditText) findViewById(R.id.location_postal_code);
    location_submitPostalCodeButton = (Button) findViewById(R.id.submit_location_sample);

    location_submitPostalCodeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        sendLocationSample();
      }
    });

    server_statistics_refreshButton = (Button) findViewById(R.id.server_statistics_refresh);
    server_statistics_numberOfAccountsTextView = (TextView) findViewById(R.id.server_statistics_number_of_accounts);
    server_statistics_numberOfLocationSamplesTextView = (TextView) findViewById(R.id.server_statistics_number_of_location_samples);

    server_statistics_refreshButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        updateServerStatistics();
      }
    });


    /*
     *
     *
     *
     *
     *
     *
     */


    // Get the location manager
    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
      showSettingsAlert();
    }

    // Define the criteria how to select the locatioin provider -> use
    // default
    Criteria criteria = new Criteria();
    provider = locationManager.getBestProvider(criteria, true);
    Location location = locationManager.getLastKnownLocation(provider);

    // Initialize the location fields
    if (location != null) {
      onLocationChanged(location);
    }


    // Account
    account = loadAccount();
    if (account == null) {
      account_setup_view.setVisibility(View.VISIBLE);
    }

  }

  /**
   * onResume is executed after onCreate AND when returning to the application.
   */
  @Override
  protected void onResume() {
    super.onResume();

    new Thread(assertGoodLocationFixRunnable).start();

    currentLocation = null;
    locationManager.requestLocationUpdates(provider, 0, 0, this);
  }

  @Override
  protected void onPause() {
    super.onPause();
    assertGoodLocationFixRunnable.stop = true;
    locationManager.removeUpdates(this);
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.insamling, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }


  @Override
  public void onLocationChanged(Location location) {

    boolean updateStatistics = currentLocation == null;

    currentLocation = location;

    location_timestampTextView.setText(String.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(location.getTime()))));
    location_latitudeTextView.setText(String.valueOf(location.getLatitude()));
    location_longitudeTextView.setText(String.valueOf(location.getLongitude()));
    location_accuracyTextView.setText(String.valueOf(location.getAccuracy()));
    location_altitudeTextView.setText(String.valueOf(location.getAltitude()));
    location_providerTextView.setText(String.valueOf(location.getProvider()));

    if (updateStatistics) {
      updateServerStatistics();
    }

  }

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {

  }

  @Override
  public void onProviderEnabled(String provider) {
    Toast.makeText(this, "Enabled new provider " + provider,
        Toast.LENGTH_SHORT).show();

  }

  @Override
  public void onProviderDisabled(String provider) {
    Toast.makeText(this, "Disabled provider " + provider,
        Toast.LENGTH_SHORT).show();
  }

  /**
   * Function to show settings alert dialog
   */
  public void showSettingsAlert() {
    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

    // Setting Dialog Title
    alertDialog.setTitle("GPS is settings");

    // Setting Dialog Message
    alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

    // Setting Icon to Dialog
    //alertDialog.setIcon(R.drawable.delete);

    // On pressing Settings button
    alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
      }
    });

    // on pressing cancel button
    alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
      }
    });

    // Showing Alert Message
    alertDialog.show();
  }


  public void updateServerStatistics() {

    if (currentLocation == null) {
      Toast.makeText(this, "Ingen position ännu! Statistikanropet avbrutet.", Toast.LENGTH_LONG);
      return;
    }

    server_statistics_refreshButton.setEnabled(false);
    try {

      GetLocationStatistics getLocationStatistics = new GetLocationStatistics();
      getLocationStatistics.setHttpClient(httpClient);
      getLocationStatistics.setServerHostname(Application.serverHostname);
      getLocationStatistics.setLatitude(currentLocation.getLatitude());
      getLocationStatistics.setLongitude(currentLocation.getLongitude());
      getLocationStatistics.run();
      if (getLocationStatistics.getSuccess()) {

        server_statistics_numberOfAccountsTextView.setText(String.valueOf(getLocationStatistics.getNumberOfAccounts()));
        server_statistics_numberOfLocationSamplesTextView.setText(String.valueOf(getLocationStatistics.getNumberOfLocationSamples()));

      } else {
        Toast.makeText(InsamlingActivity.this, getLocationStatistics.getFailureMessage(), Toast.LENGTH_SHORT).show();
        Log.e("GetLocationStatistics", getLocationStatistics.getFailureMessage(), getLocationStatistics.getFailureException());

      }


    } finally {
      server_statistics_refreshButton.setEnabled(true);
    }
  }

  private void sendLocationSample() {

    if (currentLocation.getTime() < System.currentTimeMillis() - maximumMillisecondsAgeOfLocationFix) {
      // This is a secondary check. might not be needed as we have a thread that check for location fix!
      // Better safe than sorry though!
      Toast.makeText(InsamlingActivity.this, "För gammal position! Rapporten avbröts!", Toast.LENGTH_SHORT).show();

    } else if (currentLocation == null) {
      Toast.makeText(InsamlingActivity.this, "Ingen position! Rapporten avbröts!", Toast.LENGTH_SHORT).show();

    } else {
      // todo are you sure? please check postal code before submitting


      CreateLocationSample createLocationSample = new CreateLocationSample();

      createLocationSample.setHttpClient(httpClient);
      createLocationSample.setServerHostname(Application.serverHostname);

      createLocationSample.setApplication(Application.application);
      createLocationSample.setApplicationVersion(Application.version);

      createLocationSample.setAccountIdentity(account.getIdentity());

      createLocationSample.setPostalCode(location_postalCodeEditText.getText().toString());
//          sendLocationSample.setPostalTown();
//          sendLocationSample.setStreetName();
//          sendLocationSample.setHouseNumber();

      createLocationSample.setLatitude(currentLocation.getLatitude());
      createLocationSample.setLongitude(currentLocation.getLongitude());
      createLocationSample.setAccuracy((double) currentLocation.getAccuracy());
      createLocationSample.setProvider(currentLocation.getProvider());
      createLocationSample.setAltitude(currentLocation.getAltitude());

      createLocationSample.run();
      if (createLocationSample.getSuccess()) {
        Toast.makeText(InsamlingActivity.this, "Rapporten mottagen av server!", Toast.LENGTH_SHORT).show();
        location_postalCodeEditText.setText(null);

      } else {
        Toast.makeText(InsamlingActivity.this, createLocationSample.getFailureMessage(), Toast.LENGTH_SHORT).show();
        Log.e("SendLocationSample", createLocationSample.getFailureMessage(), createLocationSample.getFailureException());

      }
    }
  }

  private class AssertGoodLocationFixRunnable implements Runnable {
    private boolean stop = true;

    @Override
    public void run() {
      stop = false;
      try {

        while (!stop) {
          // Can't touch views from anything but the UI-thread!
          runOnUiThread(new Runnable() {
            @Override
            public void run() {

              // Don't wait for location fix if we're setting up the account!
              if (account_setup_view.getVisibility() != View.VISIBLE) {
                if (currentLocation != null
                    && currentLocation.getTime() > System.currentTimeMillis() - maximumMillisecondsAgeOfLocationFix) {
                  location_fix_view.setVisibility(View.GONE);
                  content_view.setVisibility(View.VISIBLE);
                } else {
                  location_fix_view.setVisibility(View.VISIBLE);
                  content_view.setVisibility(View.GONE);
                }
              }

            }
          });

          try {
            Thread.sleep(1000);
          } catch (InterruptedException ie) {
            Log.e("AssertGoodLocationFixRunnable", "Caught interruption", ie);
          }
        }

      } finally {
        Log.i("AssertGoodLocationFixRunnable", "stopping");
        stop = true;
      }
    }

  }

  private Account loadAccount() {
    Account account = null;
    String identity = accountPreferences.getString("account.identity", null);
    if (identity != null) {
      account = new Account();
      account.setIdentity(identity);
      account.setEmailAddress(accountPreferences.getString("account.emailAddress", null));
    }
    return account;
  }

  private boolean createAccount() {

    // todo assert identity and email is set!

    Account account = new Account();
    account.setIdentity(UUID.randomUUID().toString());
    account.setEmailAddress(account_setup_emailAddressEditText.getText().toString());
    account.setAcceptingCcZero(account_setup_ccZeroCheckBox.isChecked());

    SetAccount setAccount = new SetAccount();

    setAccount.setServerHostname(Application.serverHostname);
    setAccount.setHttpClient(httpClient);

    setAccount.setIdentity(account.getIdentity());
    setAccount.setEmailAddress(account.getEmailAddress());
    setAccount.setAcceptingCcZero(account.isAcceptingCcZero());

    setAccount.run();

    if (setAccount.getSuccess()) {

      SharedPreferences.Editor editor = accountPreferences.edit();
      editor.putString("account.identity", account.getIdentity());
      editor.putString("account.emailAddress", account.getEmailAddress());
      editor.putBoolean("account.acceptingCcZero", account.isAcceptingCcZero());
      editor.apply();

      this.account = account;

      Toast.makeText(this, "Kontodetaljer registrerade.", Toast.LENGTH_LONG);
      return true;

    } else {

      Toast.makeText(InsamlingActivity.this, setAccount.getFailureMessage(), Toast.LENGTH_SHORT).show();
      Log.e("GetLocationStatistics", setAccount.getFailureMessage(), setAccount.getFailureException());
      return false;

    }


  }

}
