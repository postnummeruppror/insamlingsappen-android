package nu.postnummeruppror.insamlingsappen;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import insamlingsappen.postnummeruppror.nu.insamlingsappen.R;
import nu.postnummeruppror.insamlingsappen.commands.GetNumberOfLocationStatisticsInRadius;
import nu.postnummeruppror.insamlingsappen.commands.GetServerStatistics;


public class StatisticsActivity extends ActionBarActivity {

  public static final String intent_extra_latitude = "latitude";
  public static final String intent_extra_longitude = "longitude";

  private TextView server_statistics_numberOfAccountsTextView;
  private TextView server_statistics_numberOfLocationSamplesTextView;
  private TextView server_statistics_numberOfPostalCodesTextView;
  private TextView server_statistics_numberOfPostalTownsTextView;

  private TextView server_statistics_locationSamplesWithinOneHundredMetersRadiusTextView;
  private TextView server_statistics_locationSamplesWithinFiveHundredMetersRadiusTextView;

  private Double latitude;
  private Double longitude;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_statistics);

    server_statistics_numberOfAccountsTextView = (TextView) findViewById(R.id.server_statistics_number_of_accounts);
    server_statistics_numberOfLocationSamplesTextView = (TextView) findViewById(R.id.server_statistics_number_of_location_samples);
    server_statistics_numberOfPostalTownsTextView = (TextView) findViewById(R.id.server_statistics_number_of_postal_towns);
    server_statistics_numberOfPostalCodesTextView = (TextView) findViewById(R.id.server_statistics_number_of_postal_codes);

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


  }

  @Override
  protected void onPostResume() {
    super.onPostResume();

    updateServerStatistics();
    updateLocationStatistics();
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

  public void updateLocationStatistics() {

    new Thread(new Runnable() {
      @Override
      public void run() {

        final GetNumberOfLocationStatisticsInRadius getNumberOfLocationStatisticsInRadius = new GetNumberOfLocationStatisticsInRadius();
        getNumberOfLocationStatisticsInRadius.setCentroidLatitude(latitude);
        getNumberOfLocationStatisticsInRadius.setCentroidLongitude(longitude);
        getNumberOfLocationStatisticsInRadius.setRadiusKilometers(0.100d);
        getNumberOfLocationStatisticsInRadius.run();

        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            if (getNumberOfLocationStatisticsInRadius.getSuccess()) {

              server_statistics_locationSamplesWithinOneHundredMetersRadiusTextView.setText(String.valueOf(getNumberOfLocationStatisticsInRadius.getNumberOfLocationSamples()));

            } else {
              Toast.makeText(StatisticsActivity.this, getNumberOfLocationStatisticsInRadius.getFailureMessage(), Toast.LENGTH_SHORT).show();
              Log.e("GetNumberOfLocationStatisticsInRadius", getNumberOfLocationStatisticsInRadius.getFailureMessage(), getNumberOfLocationStatisticsInRadius.getFailureException());

            }
          }
        });
      }
    }).start();

    new Thread(new Runnable() {
      @Override
      public void run() {

        final GetNumberOfLocationStatisticsInRadius getNumberOfLocationStatisticsInRadius = new GetNumberOfLocationStatisticsInRadius();
        getNumberOfLocationStatisticsInRadius.setCentroidLatitude(latitude);
        getNumberOfLocationStatisticsInRadius.setCentroidLongitude(longitude);
        getNumberOfLocationStatisticsInRadius.setRadiusKilometers(0.500d);
        getNumberOfLocationStatisticsInRadius.run();

        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            if (getNumberOfLocationStatisticsInRadius.getSuccess()) {

              server_statistics_locationSamplesWithinFiveHundredMetersRadiusTextView.setText(String.valueOf(getNumberOfLocationStatisticsInRadius.getNumberOfLocationSamples()));

            } else {
              Toast.makeText(StatisticsActivity.this, getNumberOfLocationStatisticsInRadius.getFailureMessage(), Toast.LENGTH_SHORT).show();
              Log.e("GetNumberOfLocationStatisticsInRadius", getNumberOfLocationStatisticsInRadius.getFailureMessage(), getNumberOfLocationStatisticsInRadius.getFailureException());

            }
          }
        });
      }
    }).start();

  }

  public void updateServerStatistics() {

    new Thread(new Runnable() {
      @Override
      public void run() {

        final GetServerStatistics getServerStatistics = new GetServerStatistics();
        getServerStatistics.run();

        runOnUiThread(new Runnable() {
          @Override
          public void run() {

            if (getServerStatistics.getSuccess()) {

              server_statistics_numberOfAccountsTextView.setText(String.valueOf(getServerStatistics.getNumberOfAccounts()));
              server_statistics_numberOfLocationSamplesTextView.setText(String.valueOf(getServerStatistics.getNumberOfLocationSamples()));
              server_statistics_numberOfPostalCodesTextView.setText(String.valueOf(getServerStatistics.getNumberOfPostalCodes()));
              server_statistics_numberOfPostalTownsTextView.setText(String.valueOf(getServerStatistics.getNumberOfPostalTowns()));

            } else {
              Toast.makeText(StatisticsActivity.this, getServerStatistics.getFailureMessage(), Toast.LENGTH_SHORT).show();
              Log.e("GetServerStatistics", getServerStatistics.getFailureMessage(), getServerStatistics.getFailureException());

            }
          }
        });

      }
    }).start();

  }

}
