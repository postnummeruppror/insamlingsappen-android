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

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.StringWriter;


public class Insamling extends ActionBarActivity implements LocationListener {

    private HttpClient httpClient;
    private String serverHostname = "insamling.postnummeruppror.nu.kodapan.se";

    private String accountIdentity;
    private Location currentLocation;

    private TextView latitudeTextView;
    private TextView longitudeTextView;
    private TextView accuracyTextView;
    private TextView altitudeTextView;
    private TextView providerTextView;

    private EditText postalCodeEditText;
    private Button sendPostalCodeButton;


    private LocationManager locationManager;
    private String provider;

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = getSharedPreferences("account", Context.MODE_PRIVATE);

        httpClient = new DefaultHttpClient();

        accountIdentity = sharedPref.getString("accountIdentity", null);
        if (accountIdentity == null) {
            createAccount();
        }


        setContentView(R.layout.activity_insamling);

        latitudeTextView = (TextView) findViewById(R.id.latitude);
        longitudeTextView = (TextView) findViewById(R.id.longitude);
        accuracyTextView = (TextView) findViewById(R.id.accuracy);
        altitudeTextView = (TextView) findViewById(R.id.altitude);
        providerTextView = (TextView) findViewById(R.id.provider);


        postalCodeEditText = (EditText) findViewById(R.id.postal_code);
        sendPostalCodeButton = (Button) findViewById(R.id.submit_postal_code);

        sendPostalCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLocation == null) {
                    // todo

                    Toast.makeText(Insamling.this, "Ingen position! Rapporten avbröts!", Toast.LENGTH_SHORT).show();

                } else {
                    // todo are you sure? please check postal code before submitting

                    JSONObject json = new JSONObject();

                    try {
                        json.put("accountIdentity", accountIdentity);

                        json.put("latitude", currentLocation.getLatitude());
                        json.put("longitude", currentLocation.getLongitude());
                        json.put("altitude", currentLocation.getAltitude());
                        json.put("provider", currentLocation.getProvider());
                        json.put("accuracy", currentLocation.getAccuracy());

                        json.put("postalCode", postalCodeEditText.getText());

                    } catch (Exception e) {
                        Log.e("todo tag", "Exception while assembling JSON object for location sample", e);
                        return;
                    }


                    HttpResponse response;
                    try {
                        HttpPost post = new HttpPost("http://" + serverHostname + "/api/location_sample/create");
                        post.setEntity(new StringEntity(json.toString(), "UTF-8"));
                        response = httpClient.execute(post);
                    } catch (Exception e) {
                        // todo
                        Log.e("todo tag", "Exception while sending create location sample request to server", e);
                        return;
                    }

                    try {

                        if (response.getStatusLine().getStatusCode() != 200) {
                            // todo report unexpected response
                            Log.e("todo tag", "Unexpected server response!");

                        } else {
                            StringWriter jsonWriter = new StringWriter(1024);
                            IOUtils.copy(response.getEntity().getContent(), jsonWriter);
                            // now consumed... no need for below
//                            response.getEntity().getContent().close();
                            JSONObject responseJson = new JSONObject(new JSONTokener(jsonWriter.toString()));
                            if (responseJson.getBoolean("success")) {
                                // todo report success

                                Toast.makeText(Insamling.this, "Rapporten mottagen hos server!", Toast.LENGTH_LONG).show();

                                postalCodeEditText.setText("");

                            } else {
                                // todo report error
                            }
                        }

                    } catch (Exception e) {
                        // todo
                        Log.e("todo tag", "Exception while processing server response", e);
                    }


                }
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
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            latitudeTextView.setText("N/A");
            longitudeTextView.setText("N/A");
            accuracyTextView.setText("N/A");
            altitudeTextView.setText("N/A");
            providerTextView.setText("N/A");
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

    public void createAccount() {

        Toast.makeText(this, "Registrerar nytt konto...", Toast.LENGTH_SHORT).show();

        JSONObject json = new JSONObject();

        try {
            // todo ask for email address!
            json.put("emailAddress", null);

        } catch (Exception e) {
            Log.e("todo tag", "Exception while assembling JSON object for account creation", e);
            return;
        }


        HttpResponse response;
        try {
            HttpPost post = new HttpPost("http://" + serverHostname + "/api/account/create");
            post.setEntity(new StringEntity(json.toString(), "UTF-8"));
            response = httpClient.execute(post);
        } catch (Exception e) {
            // todo
            Log.e("todo tag", "Exception while sending create account request to server", e);
            return;
        }

        try {

            if (response.getStatusLine().getStatusCode() != 200) {
                // todo report unexpected response
                Log.e("todo tag", "Unexpected server response!");

            } else {
                StringWriter jsonWriter = new StringWriter(1024);
                IOUtils.copy(response.getEntity().getContent(), jsonWriter);
                // now consumed... no need for below
//                            response.getEntity().getContent().close();
                JSONObject responseJson = new JSONObject(new JSONTokener(jsonWriter.toString()));
                if (responseJson.getBoolean("success")) {
                    // todo report success

                    accountIdentity = responseJson.getString("identity");

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("accountIdentity", accountIdentity);
                    editor.commit();

                    Toast.makeText(this, "Konto registrerat!", Toast.LENGTH_LONG).show();

                } else {
                    // todo report error
                }
            }

        } catch (Exception e) {
            // todo
            Log.e("todo tag", "Exception while processing server response", e);
        }

    }

}
