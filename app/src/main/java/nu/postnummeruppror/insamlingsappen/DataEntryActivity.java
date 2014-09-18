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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import insamlingsappen.postnummeruppror.nu.insamlingsappen.R;
import nu.postnummeruppror.insamlingsappen.domain.Account;
import nu.postnummeruppror.insamlingsappen.domain.Coordinate;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import nu.postnummeruppror.insamlingsappen.domain.PostalAddress;


public class DataEntryActivity extends ActionBarActivity implements LocationListener {

  private static final int CREATE_ACCOUNT_REQUEST = 0;

  private CompoundLocationService locationService;
  private Location mostRecentLocation;

  private Location selectedLocationIStandStill;
  private Location selectedLocationIMove;

  private ProgressBar accuracyProgressBar;
  private TextView accuracyValue;

  private RadioGroup positionRadioGroup;
  private RadioButton positionIStandStill;
  private RadioButton positionIMove;
  private RadioButton positionImNotThere;

  private EditText postalCode;
  private EditText postalTown;
  private EditText streetName;
  private EditText houseNumber;
  private EditText houseName;

  private Button submit;


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

    positionRadioGroup = (RadioGroup) findViewById(R.id.location_radio);
    positionIStandStill = (RadioButton) findViewById(R.id.location_radio_i_stand_still);
    positionIMove = (RadioButton) findViewById(R.id.location_radio_i_move);
    positionImNotThere = (RadioButton) findViewById(R.id.location_radio_im_not_there);

    positionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      public void onCheckedChanged(RadioGroup group, int checkedId) {
        selectedLocationIStandStill = null;
        selectedLocationIMove = null;

        if (mostRecentLocation != null) {
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


        String postalCode = DataEntryActivity.this.postalCode.getText().toString().trim();
        String postalTown = DataEntryActivity.this.postalTown.getText().toString().trim();
        String streetName = DataEntryActivity.this.streetName.getText().toString().trim();
        String houseNumber = DataEntryActivity.this.houseNumber.getText().toString().trim();
        String houseName = DataEntryActivity.this.houseName.getText().toString().trim();

        if (postalCode.isEmpty()
            && postalTown.isEmpty()
            && streetName.isEmpty()
            && houseNumber.isEmpty()
            && houseName.isEmpty()) {
          Toast.makeText(DataEntryActivity.this, "Du måste fylla i minst ett av postadressfälten! Rapporten avbröts!", Toast.LENGTH_SHORT).show();
          return;
        }

        Location selectedLocation = null;

        if (positionIStandStill.isChecked()) {
          if (selectedLocationIStandStill == null) {
            Toast.makeText(DataEntryActivity.this, "Ingen position! Rapporten avbröts!", Toast.LENGTH_LONG).show();
          } else {
            selectedLocation = selectedLocationIStandStill;
          }
        } else if (positionIMove.isChecked()) {
          if (selectedLocationIMove == null) {
            Toast.makeText(DataEntryActivity.this, "Ingen position! Rapporten avbröts!", Toast.LENGTH_LONG).show();
          } else {
            selectedLocation = selectedLocationIMove;
          }
        } else if (positionImNotThere.isChecked()) {
          selectedLocation = null;

        } else {
          Toast.makeText(DataEntryActivity.this, "Du måste berätta hur vi skall tolka din position! Rapporten avbröts!", Toast.LENGTH_LONG).show();
          return;
        }


        // todo are you sure? please check postal code before submitting

        LocationSample locationSample = new LocationSample();

        locationSample.setTimestampCreated(System.currentTimeMillis());

        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setPostalCode(postalCode);
        postalAddress.setPostalTown(postalTown);
        postalAddress.setStreetName(streetName);
        postalAddress.setHouseNumber(houseNumber);
        postalAddress.setHouseName(houseName);
        locationSample.setPostalAddress(postalAddress);

        if (selectedLocation != null) {

          Coordinate coordinate = new Coordinate();

          coordinate.setLatitude(selectedLocation.getLatitude());
          coordinate.setLongitude(selectedLocation.getLongitude());
          coordinate.setAccuracy((double) selectedLocation.getAccuracy());
          coordinate.setProvider(selectedLocation.getProvider());
          coordinate.setAltitude(selectedLocation.getAltitude());

          locationSample.setCoordinate(coordinate);
        }

        DataStore store = new DataStore();
        store.open(DataEntryActivity.this);
        try {
          store.create(locationSample);
        } finally {
          store.close();
        }

        Toast.makeText(DataEntryActivity.this, "Rapporten har sparats.\nTack för ditt bidrag!", Toast.LENGTH_LONG).show();


        DataEntryActivity.this.postalCode.setText(null);
        DataEntryActivity.this.postalTown.setText(null);
        DataEntryActivity.this.streetName.setText(null);
        DataEntryActivity.this.houseNumber.setText(null);
        DataEntryActivity.this.houseName.setText(null);

        positionRadioGroup.clearCheck();

        selectedLocationIStandStill = null;
        selectedLocationIMove = null;
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
    locationService.setMaximumMillisecondsAgeOfGpsLocation(10000);
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

    startService(new Intent(this, PublisherService.class));

    positionRadioGroup.clearCheck();

    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
      showSettingsAlert();
    }

    locationService.requestLocationUpdates(this);

    accuracyProgressBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
    accuracyProgressBar.setProgress(0);

    Location mostRecentLocation = locationService.getMostRecentLocation();
    if (mostRecentLocation != null) {
      onLocationChanged(mostRecentLocation);
    }


  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    locationService.stop();
  }

  @Override
  protected void onPause() {
    super.onPause();

    positionRadioGroup.clearCheck();

    locationService.removeUpdates(this);
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
      case R.id.action_location_samples:
        startActivity(new Intent(this, LocationSamplesActivity.class));
        break;
      case R.id.action_help:
        startActivity(new Intent(this, HelpActivity.class));
        break;
    }
    return true;
  }


  @Override
  public void onLocationChanged(Location location) {

    mostRecentLocation = location;


    if (selectedLocationIStandStill == null
        || selectedLocationIStandStill.getAccuracy() > location.getAccuracy()) {
      selectedLocationIStandStill = location;
    }

    if (selectedLocationIMove == null) {
      selectedLocationIMove = location;
    }

    Location displayLocation = null;
    if (positionIStandStill.isChecked()) {
      displayLocation = selectedLocationIStandStill;

    } else if (positionIMove.isChecked()) {
      displayLocation = selectedLocationIMove;

    } else if (positionImNotThere.isChecked()) {
      displayLocation = location;

    } else {
      displayLocation = location;
    }

    if (displayLocation.getAccuracy() > 15) {
      // todo Är du inomhus? Gå till ett fönster så nära ingången som möjligt!
    }

    if (displayLocation.getAccuracy() < 10) {
      accuracyProgressBar.getProgressDrawable().setColorFilter(Color.parseColor("#35BF00"), PorterDuff.Mode.SRC_IN);
    } else if (displayLocation.getAccuracy() < 20) {
      accuracyProgressBar.getProgressDrawable().setColorFilter(Color.parseColor("#76C600"), PorterDuff.Mode.SRC_IN);
    } else if (displayLocation.getAccuracy() < 25) {
      accuracyProgressBar.getProgressDrawable().setColorFilter(Color.parseColor("#99CA00"), PorterDuff.Mode.SRC_IN);
    } else if (displayLocation.getAccuracy() < 30) {
      accuracyProgressBar.getProgressDrawable().setColorFilter(Color.parseColor("#BCCE00"), PorterDuff.Mode.SRC_IN);
    } else if (displayLocation.getAccuracy() < 40) {
      accuracyProgressBar.getProgressDrawable().setColorFilter(Color.parseColor("#D5A400"), PorterDuff.Mode.SRC_IN);
    } else if (displayLocation.getAccuracy() < 50) {
      accuracyProgressBar.getProgressDrawable().setColorFilter(Color.parseColor("#D98400"), PorterDuff.Mode.SRC_IN);
    } else {
      accuracyProgressBar.getProgressDrawable().setColorFilter(Color.parseColor("#E51E00"), PorterDuff.Mode.SRC_IN);
    }

    float progress = (100f - displayLocation.getAccuracy());
    if (progress < 0) {
      progress = 5;
    }

    accuracyProgressBar.setProgress((int) progress);
    accuracyValue.setText(String.valueOf((int) displayLocation.getAccuracy()) + " meter.");


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




}
