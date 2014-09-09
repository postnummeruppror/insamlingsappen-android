package insamlingsappen.postnummeruppror.nu.insamlingsappen;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.impl.client.DefaultHttpClient;

import insamlingsappen.postnummeruppror.nu.insamlingsappen.commands.GetLocationStatistics;
import insamlingsappen.postnummeruppror.nu.insamlingsappen.commands.GetServerStatistics;


public class StatisticsActivity extends ActionBarActivity {

  public static final String intent_extra_latitude = "latitude";
  public static final String intent_extra_longitude = "longitude";

  private Button server_statistics_refreshButton;

  private TextView server_statistics_numberOfAccountsTextView;
  private TextView server_statistics_numberOfLocationSamplesTextView;

  private TextView server_statistics_locationSamplesWithinOneHundredMetersRadiusTextView;
  private TextView server_statistics_locationSamplesWithinFiveHundredMetersRadiusTextView;

  private Double latitude;
  private Double longitude;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_statistics);

    server_statistics_refreshButton = (Button) findViewById(R.id.server_statistics_refresh);
    server_statistics_numberOfAccountsTextView = (TextView) findViewById(R.id.server_statistics_number_of_accounts);
    server_statistics_numberOfLocationSamplesTextView = (TextView) findViewById(R.id.server_statistics_number_of_location_samples);

    server_statistics_locationSamplesWithinOneHundredMetersRadiusTextView = (TextView) findViewById(R.id.server_number_of_location_samples_100_meters);
    server_statistics_locationSamplesWithinFiveHundredMetersRadiusTextView = (TextView) findViewById(R.id.server_number_of_location_samples_500_meters);

    if (this.getIntent().getExtras() != null) {
      if (this.getIntent().getExtras().containsKey(intent_extra_latitude)) {
        latitude = this.getIntent().getExtras().getDouble(intent_extra_latitude);
      }
      if (this.getIntent().getExtras().containsKey(intent_extra_longitude)) {
        longitude = this.getIntent().getExtras().getDouble(intent_extra_longitude);
      }
    }

    server_statistics_refreshButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        updateServerStatistics();
      }
    });

    updateServerStatistics();

  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.statistics, menu);
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

  public void updateServerStatistics() {

    server_statistics_refreshButton.setEnabled(false);
    try {

      GetLocationStatistics getLocationStatistics = new GetLocationStatistics();
      getLocationStatistics.setHttpClient(new DefaultHttpClient());
      getLocationStatistics.setServerHostname(Application.serverHostname);
      getLocationStatistics.setLatitude(latitude);
      getLocationStatistics.setLongitude(longitude);
      getLocationStatistics.run();
      if (getLocationStatistics.getSuccess()) {

        server_statistics_locationSamplesWithinOneHundredMetersRadiusTextView.setText(String.valueOf(getLocationStatistics.getLocationSamplesWithinOneHundredMetersRadius()));
        server_statistics_locationSamplesWithinFiveHundredMetersRadiusTextView.setText(String.valueOf(getLocationStatistics.getLocationSamplesWithinFiveHundredMetersRadius()));

      } else {
        Toast.makeText(StatisticsActivity.this, getLocationStatistics.getFailureMessage(), Toast.LENGTH_SHORT).show();
        Log.e("GetLocationStatistics", getLocationStatistics.getFailureMessage(), getLocationStatistics.getFailureException());

      }

      GetServerStatistics getServerStatistics = new GetServerStatistics();
      getServerStatistics.setHttpClient(new DefaultHttpClient());
      getServerStatistics.setServerHostname(Application.serverHostname);
      getServerStatistics.run();
      if (getServerStatistics.getSuccess()) {

        server_statistics_numberOfAccountsTextView.setText(String.valueOf(getServerStatistics.getNumberOfAccounts()));
        server_statistics_numberOfLocationSamplesTextView.setText(String.valueOf(getServerStatistics.getNumberOfLocationSamples()));

      } else {
        Toast.makeText(StatisticsActivity.this, getServerStatistics.getFailureMessage(), Toast.LENGTH_SHORT).show();
        Log.e("GetServerStatistics", getServerStatistics.getFailureMessage(), getServerStatistics.getFailureException());

      }



    } finally {
      server_statistics_refreshButton.setEnabled(true);
    }


  }


}
