package nu.postnummeruppror.insamlingsappen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import insamlingsappen.postnummeruppror.nu.insamlingsappen.R;
import nu.postnummeruppror.insamlingsappen.commands.CreateLocationSample;


public class DataEntryActivity extends ActionBarActivity implements LocationListener {

  private static final int CREATE_ACCOUNT_REQUEST = 0;

  private CompoundLocationService locationService;
  private Location mostRecentLocation;
  private Location selectedLocation;

  private ProgressBar accuracyProgressBar;
  private TextView accuracyValue;
  private CheckBox accuracyIStandStill;

  private EditText postalCode;
  private EditText postalTown;
  private EditText streetName;
  private EditText houseNumber;
  private EditText houseName;

  private Button submit;

  private boolean ruleBasedPostalTown = false;

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

    accuracyProgressBar = (ProgressBar) findViewById(R.id.location_accuracy_progress_bar);
    accuracyValue = (TextView) findViewById(R.id.location_accuracy_value);
    accuracyIStandStill = (CheckBox) findViewById(R.id.location_accuracy_standing_still);

    accuracyIStandStill.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        if (!checked) {
          onLocationChanged(mostRecentLocation);
        }
      }
    });

    postalCode = (EditText) findViewById(R.id.location_postal_code);
    postalTown = (EditText) findViewById(R.id.location_postal_town);
    streetName = (EditText) findViewById(R.id.location_street_name);
    houseNumber = (EditText) findViewById(R.id.location_house_number);
    houseName = (EditText) findViewById(R.id.location_house_name);

    submit = (Button) findViewById(R.id.submit_location_sample);

    submit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (sendLocationSample()) {
          postalCode.setText(null);
          postalTown.setText(null);
          streetName.setText(null);
          houseNumber.setText(null);
          houseName.setText(null);

          accuracyIStandStill.setChecked(false);
          ruleBasedPostalTown = false;

        }
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
    locationService.start();


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

    locationService.requestLocationUpdates(this);

    Location mostRecentLocation = locationService.getMostRecentLocation();
    if (mostRecentLocation != null) {
      onLocationChanged(mostRecentLocation);
    }

//    accuracyProgressBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
//    accuracyProgressBar.setProgress(0);

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

    if (accuracyIStandStill.isChecked()) {
      if (selectedLocation == null
          || selectedLocation.getAccuracy() > location.getAccuracy()) {
        selectedLocation = location;
      }
    } else {
      selectedLocation = location;

    }


    if (selectedLocation.getAccuracy() > 15) {
      // todo Är du inomhus? Gå till ett fönster så nära ingången som möjligt!
    }

    if (selectedLocation.getAccuracy() < 10) {
      accuracyProgressBar.getProgressDrawable().setColorFilter(Color.parseColor("#35BF00"), PorterDuff.Mode.SRC_IN);
    } else if (selectedLocation.getAccuracy() < 20) {
      accuracyProgressBar.getProgressDrawable().setColorFilter(Color.parseColor("#76C600"), PorterDuff.Mode.SRC_IN);
    } else if (selectedLocation.getAccuracy() < 25) {
      accuracyProgressBar.getProgressDrawable().setColorFilter(Color.parseColor("#99CA00"), PorterDuff.Mode.SRC_IN);
    } else if (selectedLocation.getAccuracy() < 30) {
      accuracyProgressBar.getProgressDrawable().setColorFilter(Color.parseColor("#BCCE00"), PorterDuff.Mode.SRC_IN);
    } else if (selectedLocation.getAccuracy() < 40) {
      accuracyProgressBar.getProgressDrawable().setColorFilter(Color.parseColor("#D5A400"), PorterDuff.Mode.SRC_IN);
    } else if (selectedLocation.getAccuracy() < 50) {
      accuracyProgressBar.getProgressDrawable().setColorFilter(Color.parseColor("#D98400"), PorterDuff.Mode.SRC_IN);
    } else {
      accuracyProgressBar.getProgressDrawable().setColorFilter(Color.parseColor("#E51E00"), PorterDuff.Mode.SRC_IN);
    }

    float progress = (100f - selectedLocation.getAccuracy());
    if (progress < 0) {
      progress = 5;
    }

    accuracyProgressBar.setProgress((int) progress);
    accuracyValue.setText(String.valueOf((int) selectedLocation.getAccuracy()) + " meter.");


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


  private boolean sendLocationSample() {

    if (!accuracyIStandStill.isChecked()) {
      Toast.makeText(DataEntryActivity.this, "Du måste stanna kvar på postadressen! Rapporten avbröts!", Toast.LENGTH_SHORT).show();
      return false;
    }

    Location location = mostRecentLocation;

    Account account = Account.load(this);

    if (location.getTime() < System.currentTimeMillis() - maximumMillisecondsAgeOfLocationFix) {
      // This is a secondary check. might not be needed as we have a thread that check for location fix!
      // Better safe than sorry though!
      Toast.makeText(DataEntryActivity.this, "För gammal position! Rapporten avbröts!", Toast.LENGTH_SHORT).show();
      return false;

    } else if (location == null) {
      Toast.makeText(DataEntryActivity.this, "Ingen position! Rapporten avbröts!", Toast.LENGTH_SHORT).show();
      return false;

    } else {
      // todo are you sure? please check postal code before submitting


      CreateLocationSample createLocationSample = new CreateLocationSample();

      createLocationSample.setApplication(Application.application);
      createLocationSample.setApplicationVersion(Application.version);

      createLocationSample.setAccountIdentity(account.getIdentity());

      createLocationSample.setPostalCode(postalCode.getText().toString());
      createLocationSample.setPostalTown(postalTown.getText().toString());
      createLocationSample.setStreetName(streetName.getText().toString());
      createLocationSample.setHouseNumber(houseNumber.getText().toString());
      createLocationSample.setHouseName(houseName.getText().toString());

      createLocationSample.setLatitude(location.getLatitude());
      createLocationSample.setLongitude(location.getLongitude());
      createLocationSample.setAccuracy((double) location.getAccuracy());
      createLocationSample.setProvider(location.getProvider());
      createLocationSample.setAltitude(location.getAltitude());

      createLocationSample.run();
      if (createLocationSample.getSuccess()) {
        Toast.makeText(DataEntryActivity.this, "Rapporten mottagen av server!", Toast.LENGTH_SHORT).show();
        return true;

      } else {
        Toast.makeText(DataEntryActivity.this, createLocationSample.getFailureMessage(), Toast.LENGTH_SHORT).show();
        Log.e("SendLocationSample", createLocationSample.getFailureMessage(), createLocationSample.getFailureException());
        return false;
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

              if (!accuracyIStandStill.isChecked()
                  && mostRecentLocation != null
                  && mostRecentLocation.getTime() < System.currentTimeMillis() - maximumMillisecondsAgeOfLocationFix) {

                accuracyProgressBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                accuracyProgressBar.setProgress(0);

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


}
