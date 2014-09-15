package nu.postnummeruppror.insamlingsappen;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import insamlingsappen.postnummeruppror.nu.insamlingsappen.R;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;

public class LocationSamplesActivity extends ActionBarActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_location_samples);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.location_samples, menu);
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

  /**
   * onResume is executed after onCreate AND when returning to the application.
   */
  @Override
  protected void onResume() {
    super.onResume();

    TableLayout table = (TableLayout)findViewById(R.id.location_reports_table);

    DataStore dataStore = new DataStore();
    dataStore.open(this);
    try {

      List<LocationSample> locationSamples = dataStore.listAll();



      for (LocationSample locationSample : locationSamples) {

        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        if (locationSample.getPostalAddress() != null) {
          addColumn(row, locationSample.getPostalAddress().getPostalTown());
          addColumn(row, locationSample.getPostalAddress().getPostalCode());
          addColumn(row, locationSample.getPostalAddress().getStreetName());
          addColumn(row, locationSample.getPostalAddress().getHouseNumber());
          addColumn(row, locationSample.getPostalAddress().getHouseName());
        }

        table.addView(row);

        // todo on click

      }

    } finally {
      dataStore.close();
    }

  }

  private void addColumn(TableRow row, String text) {

    TextView column = new TextView(this);
    column.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
    column.setText(text);

    row.addView(column);

  }

}
