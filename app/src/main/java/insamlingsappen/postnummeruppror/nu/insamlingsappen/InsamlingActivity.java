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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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


public class InsamlingActivity extends ActionBarActivity implements LocationListener {

  private HttpClient httpClient;

  private String accountIdentity;
  private Location currentLocation;

  private TextView timestampTextView;
  private TextView latitudeTextView;
  private TextView longitudeTextView;
  private TextView accuracyTextView;
  private TextView altitudeTextView;
  private TextView providerTextView;

  private EditText postalCodeEditText;
  private Button sendPostalCodeButton;

  private Button updateServerStatisticsButton;

  private TextView numberOfAccountsTextView;
  private TextView numberOfLocationSamplesTextView;


  private LocationManager locationManager;
  private String provider;

  private SharedPreferences sharedPref;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Required to execute HTTP post from within the application thread on newer Android devices.
    if (android.os.Build.VERSION.SDK_INT > 8) {
      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
      StrictMode.setThreadPolicy(policy);
    }

    httpClient = new DefaultHttpClient();


    sharedPref = getSharedPreferences("account", Context.MODE_PRIVATE);

    // assert that we have an account identity

    accountIdentity = sharedPref.getString("account.identity", null);
    if (accountIdentity == null) {
      accountIdentity = UUID.randomUUID().toString();
      SharedPreferences.Editor editor = sharedPref.edit();
      editor.putString("account.identity", accountIdentity);
      editor.commit();

    }

    setContentView(R.layout.activity_insamling);

    timestampTextView = (TextView) findViewById(R.id.location_timestamp);
    latitudeTextView = (TextView) findViewById(R.id.location_latitude);
    longitudeTextView = (TextView) findViewById(R.id.location_longitude);
    accuracyTextView = (TextView) findViewById(R.id.location_accuracy);
    altitudeTextView = (TextView) findViewById(R.id.location_altitude);
    providerTextView = (TextView) findViewById(R.id.provider);


    postalCodeEditText = (EditText) findViewById(R.id.postal_code);
    sendPostalCodeButton = (Button) findViewById(R.id.submit_postal_code);

    sendPostalCodeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (currentLocation.getTime() < System.currentTimeMillis() - (1000 * 30)) {
          Toast.makeText(InsamlingActivity.this, "För gammal position! Rapporten avbröts!", Toast.LENGTH_SHORT).show();

        } else  if (currentLocation == null) {
          Toast.makeText(InsamlingActivity.this, "Ingen position! Rapporten avbröts!", Toast.LENGTH_SHORT).show();

        } else {
          // todo are you sure? please check postal code before submitting


          CreateLocationSample createLocationSample = new CreateLocationSample();

          createLocationSample.setHttpClient(httpClient);
          createLocationSample.setServerHostname(Application.serverHostname);

          createLocationSample.setApplication(Application.application);
          createLocationSample.setApplicationVersion(Application.version);

          createLocationSample.setAccountIdentity(accountIdentity);

          createLocationSample.setPostalCode(postalCodeEditText.getText().toString());
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
            postalCodeEditText.setText(null);

          } else {
            Toast.makeText(InsamlingActivity.this, createLocationSample.getFailureMessage(), Toast.LENGTH_SHORT).show();
            Log.e("SendLocationSample", createLocationSample.getFailureMessage(), createLocationSample.getFailureException());

          }
        }
      }
    });

    updateServerStatisticsButton = (Button) findViewById(R.id.update_server_statistics);
    numberOfAccountsTextView = (TextView) findViewById(R.id.server_number_of_accounts);
    numberOfLocationSamplesTextView = (TextView) findViewById(R.id.server_number_of_location_samples);

    updateServerStatistics();

    updateServerStatisticsButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        updateServerStatistics();
      }
    });

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
    locationManager.requestLocationUpdates(provider, 0, 0, this);



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

  /* Request updates at startup */
  @Override
  protected void onResume() {
    super.onResume();
    locationManager.requestLocationUpdates(provider, 0, 0, this);
  }

  /* Remove the locationlistener updates when Activity is paused */
  @Override
  protected void onPause() {
    super.onPause();
    locationManager.removeUpdates(this);
  }

  @Override
  public void onLocationChanged(Location location) {

    currentLocation = location;

    timestampTextView.setText(String.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(location.getTime()))));
    latitudeTextView.setText(String.valueOf(location.getLatitude()));
    longitudeTextView.setText(String.valueOf(location.getLongitude()));
    accuracyTextView.setText(String.valueOf(location.getAccuracy()));
    altitudeTextView.setText(String.valueOf(location.getAltitude()));
    providerTextView.setText(String.valueOf(location.getProvider()));

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
    updateServerStatisticsButton.setEnabled(false);
    try {

      GetLocationStatistics getLocationStatistics = new GetLocationStatistics();
      getLocationStatistics.setHttpClient(httpClient);
      getLocationStatistics.setServerHostname(Application.serverHostname);
      getLocationStatistics.setLatitude(currentLocation.getLatitude());
      getLocationStatistics.setLongitude(currentLocation.getLongitude());
      getLocationStatistics.run();
      if (getLocationStatistics.getSuccess()) {

        numberOfAccountsTextView.setText(String.valueOf(getLocationStatistics.getNumberOfAccounts()));
        numberOfLocationSamplesTextView.setText(String.valueOf(getLocationStatistics.getNumberOfLocationSamples()));

      } else {
        Toast.makeText(InsamlingActivity.this, getLocationStatistics.getFailureMessage(), Toast.LENGTH_SHORT).show();
        Log.e("GetLocationStatistics", getLocationStatistics.getFailureMessage(), getLocationStatistics.getFailureException());

      }


    } finally {
      updateServerStatisticsButton.setEnabled(true);
    }
  }

}
