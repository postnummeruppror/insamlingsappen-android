package insamlingsappen.postnummeruppror.nu.insamlingsappen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import org.apache.http.impl.client.DefaultHttpClient;

import java.text.SimpleDateFormat;
import java.util.Date;

import insamlingsappen.postnummeruppror.nu.insamlingsappen.commands.CreateLocationSample;


public class DataEntryActivity extends ActionBarActivity implements LocationListener {

  private static final int CREATE_ACCOUNT_REQUEST = 0;

  private CompoundLocationService locationService;
  private Location mostRecentLocation;

  private View waiting_for_location_fix_view;
  private View fixed_location_view;
  private View data_entry_view;


  private TextView location_timestampTextView;
  private TextView location_latitudeTextView;
  private TextView location_longitudeTextView;
  private TextView location_accuracyTextView;
  private TextView location_altitudeTextView;
  private TextView location_providerTextView;

  private EditText location_postalCodeEditText;
  private Button location_submitPostalCodeButton;


  private long maximumMillisecondsAgeOfLocationFix = 30 * 1000; // 30 seconds
  private AssertGoodLocationFixRunnable assertGoodLocationFixRunnable = new AssertGoodLocationFixRunnable();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Required to execute HTTP post from within the application thread on newer Android devices.
    if (android.os.Build.VERSION.SDK_INT > 8) {
      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
      StrictMode.setThreadPolicy(policy);
    }


    setContentView(R.layout.activity_data_entry);

    /*
     *
     *
     *
     *
     *
     *
     */


    // Define views and widgets the application touch.

    waiting_for_location_fix_view = findViewById(R.id.waiting_for_location_fix_view);
    fixed_location_view = findViewById(R.id.fixed_location_view);
    data_entry_view = findViewById(R.id.data_entry_view);

    // There is a thread that handle displaying them.
    // Not having both of them set GONE at start will cause unwanted GUI effects.
    fixed_location_view.setVisibility(View.GONE);
    waiting_for_location_fix_view.setVisibility(View.GONE);


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



    /*
     *
     *
     *
     *
     *
     *
     */


    locationService = new CompoundLocationService(this);
    locationService.setMaximumMillisecondsAgeOfGpsLocation(maximumMillisecondsAgeOfLocationFix);
    locationService.requestLocationUpdates(this);

    // make sure an account is created and registered.
    assertRegisteredAccount();



  }

  /**
   * onResume is executed after onCreate AND when returning to the application.
   */
  @Override
  protected void onResume() {
    super.onResume();

    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
      showSettingsAlert();
    }

    locationService.start();

    mostRecentLocation = null;
    displayWaitingForLocationFix();

    new Thread(assertGoodLocationFixRunnable).start();

  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    locationService.stop();
  }

  @Override
  protected void onPause() {
    super.onPause();
    locationService.removeUpdates(this);
    assertGoodLocationFixRunnable.stop = true;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.data_entry, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_about:
        startActivity(new Intent(this, AboutActivity.class));
        break;
      case R.id.action_settings:
        break;
      case R.id.action_statistics:
        Intent intent = new Intent(this, StatisticsActivity.class);
        if (mostRecentLocation != null) {
          intent.putExtra(StatisticsActivity.intent_extra_latitude, mostRecentLocation.getLatitude());
          intent.putExtra(StatisticsActivity.intent_extra_longitude, mostRecentLocation.getLongitude());
        }
        startActivity(intent);
        break;
      case R.id.action_account:
        startActivityForResult(new Intent(this, AccountActivity.class), CREATE_ACCOUNT_REQUEST);
        break;
    }
    return true;
  }


  @Override
  public void onLocationChanged(Location location) {

    mostRecentLocation = location;

    location_timestampTextView.setText(String.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(location.getTime()))));
    location_latitudeTextView.setText(String.valueOf(location.getLatitude()));
    location_longitudeTextView.setText(String.valueOf(location.getLongitude()));
    location_accuracyTextView.setText(String.valueOf(location.getAccuracy()));
    location_altitudeTextView.setText(String.valueOf(location.getAltitude()));
    location_providerTextView.setText(String.valueOf(location.getProvider()));

  }

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {
  }

  @Override
  public void onProviderEnabled(String provider) {

  }

  @Override
  public void onProviderDisabled(String provider) {
  }

  /**
   * Function to show settings alert dialog
   */
  public void showSettingsAlert() {
    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

    // Setting Dialog Title
    alertDialog.setTitle("GPS");

    // Setting Dialog Message
    alertDialog.setMessage("GPS är inte påslagen. Vill du öppna inställningarna så du kan ändra på det?");

    // Setting Icon to Dialog
    //alertDialog.setIcon(R.drawable.delete);

    // On pressing Settings button
    alertDialog.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
      }
    });

    // on pressing cancel button
    alertDialog.setNegativeButton("Nej", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
      }
    });

    // Showing Alert Message
    alertDialog.show();
  }

  private void assertRegisteredAccount() {
    if (Account.load(this) == null) {
      Toast.makeText(this, "Du måste registrera ett konto!", Toast.LENGTH_LONG).show();
      startActivityForResult(new Intent(this, AccountActivity.class), CREATE_ACCOUNT_REQUEST);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == CREATE_ACCOUNT_REQUEST) {
      assertRegisteredAccount();
    }
  }


  private void sendLocationSample() {

    Account account = Account.load(this);

    if (mostRecentLocation.getTime() < System.currentTimeMillis() - maximumMillisecondsAgeOfLocationFix) {
      // This is a secondary check. might not be needed as we have a thread that check for location fix!
      // Better safe than sorry though!
      Toast.makeText(DataEntryActivity.this, "För gammal position! Rapporten avbröts!", Toast.LENGTH_SHORT).show();

    } else if (mostRecentLocation == null) {
      Toast.makeText(DataEntryActivity.this, "Ingen position! Rapporten avbröts!", Toast.LENGTH_SHORT).show();

    } else {
      // todo are you sure? please check postal code before submitting


      CreateLocationSample createLocationSample = new CreateLocationSample();

      createLocationSample.setHttpClient(new DefaultHttpClient());
      createLocationSample.setServerHostname(Application.serverHostname);

      createLocationSample.setApplication(Application.application);
      createLocationSample.setApplicationVersion(Application.version);

      createLocationSample.setAccountIdentity(account.getIdentity());

      createLocationSample.setPostalCode(location_postalCodeEditText.getText().toString());
//          sendLocationSample.setPostalTown();
//          sendLocationSample.setStreetName();
//          sendLocationSample.setHouseNumber();

      createLocationSample.setLatitude(mostRecentLocation.getLatitude());
      createLocationSample.setLongitude(mostRecentLocation.getLongitude());
      createLocationSample.setAccuracy((double) mostRecentLocation.getAccuracy());
      createLocationSample.setProvider(mostRecentLocation.getProvider());
      createLocationSample.setAltitude(mostRecentLocation.getAltitude());

      createLocationSample.run();
      if (createLocationSample.getSuccess()) {
        Toast.makeText(DataEntryActivity.this, "Rapporten mottagen av server!", Toast.LENGTH_SHORT).show();
        location_postalCodeEditText.setText(null);

      } else {
        Toast.makeText(DataEntryActivity.this, createLocationSample.getFailureMessage(), Toast.LENGTH_SHORT).show();
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

              if (mostRecentLocation != null
                  && mostRecentLocation.getTime() > System.currentTimeMillis() - maximumMillisecondsAgeOfLocationFix) {
                displayFixedLocationView();
              } else {
                displayWaitingForLocationFix();
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

  private void displayWaitingForLocationFix() {
    if (waiting_for_location_fix_view.getVisibility() != View.VISIBLE) {
      location_submitPostalCodeButton.setEnabled(false);
      fixed_location_view.setVisibility(View.GONE);
      waiting_for_location_fix_view.setVisibility(View.VISIBLE);
    }
  }

  private void displayFixedLocationView() {

    if (fixed_location_view.getVisibility() != View.VISIBLE) {
      location_submitPostalCodeButton.setEnabled(true);
      waiting_for_location_fix_view.setVisibility(View.GONE);
      fixed_location_view.setVisibility(View.VISIBLE);
    }

  }




}
