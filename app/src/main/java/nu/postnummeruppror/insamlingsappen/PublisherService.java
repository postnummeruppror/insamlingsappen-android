package nu.postnummeruppror.insamlingsappen;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

import nu.postnummeruppror.insamlingsappen.commands.CreateLocationSample;
import nu.postnummeruppror.insamlingsappen.domain.Account;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;

/**
 * Sends data to the server
 */
public class PublisherService extends Service {
  public PublisherService() {
  }

  private PublisherRunnable runnable = new PublisherRunnable();


  @Override
  public void onCreate() {
    super.onCreate();
    new Thread(runnable).start();
  }

  @Override
  public void onDestroy() {
    runnable.stop = true;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return Service.START_STICKY;
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  private class PublisherRunnable implements Runnable {

    private boolean stop = false;

    @Override
    public void run() {

      long previousAction = 0;

      while (!stop) {

        if (previousAction + 10000 <= System.currentTimeMillis()) {

          DataStore dataStore = new DataStore();
          dataStore.open(PublisherService.this);
          try {

            List<LocationSample> nonPublishedLocationSamples = dataStore.listNonPublished();
            if (nonPublishedLocationSamples.isEmpty()) {

              Log.d("PublisherServer", "No unpublished location samples to process.");

            } else {

              Account account = Account.load(PublisherService.this);

              for (LocationSample locationSample : nonPublishedLocationSamples) {

                CreateLocationSample createLocationSample = new CreateLocationSample();

                createLocationSample.setApplication(Application.application);
                createLocationSample.setApplicationVersion(Application.version);
                createLocationSample.setAccountIdentity(account.getIdentity());

                if (locationSample.getCoordinate() != null) {
                  createLocationSample.setProvider(locationSample.getCoordinate().getProvider());
                  createLocationSample.setLongitude(locationSample.getCoordinate().getLongitude());
                  createLocationSample.setLatitude(locationSample.getCoordinate().getLatitude());
                  createLocationSample.setAccuracy(locationSample.getCoordinate().getAccuracy());
                  createLocationSample.setAltitude(locationSample.getCoordinate().getAltitude());
                }

                if (locationSample.getPostalAddress() != null) {
                  createLocationSample.setStreetName(locationSample.getPostalAddress().getStreetName());
                  createLocationSample.setHouseNumber(locationSample.getPostalAddress().getHouseNumber());
                  createLocationSample.setHouseName(locationSample.getPostalAddress().getHouseName());
                  createLocationSample.setPostalCode(locationSample.getPostalAddress().getPostalCode());
                  createLocationSample.setPostalTown(locationSample.getPostalAddress().getPostalTown());
                }

                createLocationSample.run();

                if (createLocationSample.getSuccess()) {

                  locationSample.setServerIdentity(createLocationSample.getIdentity());
                  locationSample.setTimestampPublished(System.currentTimeMillis());
                  dataStore.update(locationSample);

                  Log.i("PublisherService", "Successfully published location sample " + locationSample.toString());

                } else {

                  Log.e("PublisherService/CreateLocationSample", createLocationSample.getFailureMessage(), createLocationSample.getFailureException());

                }
              }
            }

          } finally {
            dataStore.close();
          }


          previousAction = System.currentTimeMillis();
        }

        try {
          Thread.sleep(1000);
        } catch (InterruptedException ie) {
          Log.e("PublisherRunnable", "Caught interruption", ie);
        }
      }

      stop = true;
    }
  }
}
