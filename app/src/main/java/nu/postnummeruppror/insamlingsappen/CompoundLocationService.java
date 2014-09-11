package nu.postnummeruppror.insamlingsappen;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by kalle on 09/09/14.
 */
public class CompoundLocationService {

  private LocationManager locationManager;

  public CompoundLocationService(Context applicationContext) {
    locationManager = (LocationManager) applicationContext.getSystemService(Context.LOCATION_SERVICE);
  }

  private long maximumMillisecondsAgeOfGpsLocation = 1000 * 10; // 10 seconds

  private Location mostRecentGpsLocation;
  private Location mostRecentNetworkLocation;

  private Location mostRecentLocation;

  private void publishLocationChanged(Location location) {
    mostRecentLocation = location;
    for (LocationListener listener : listeners) {
      listener.onLocationChanged(location);
    }
  }

  private Set<LocationListener> listeners = new HashSet<LocationListener>();

  public void requestLocationUpdates(LocationListener locationListener) {
    listeners.add(locationListener);
  }

  public void removeUpdates(LocationListener locationListener) {
    listeners.remove(locationListener);
  }

  private LocationListener gpsLocationListener = new LocationListener() {
    @Override
    public void onLocationChanged(Location location) {
      mostRecentGpsLocation = location;
      publishLocationChanged(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }
  };

  private LocationListener networkLocationListener = new LocationListener() {
    @Override
    public void onLocationChanged(Location location) {
      mostRecentNetworkLocation = location;
      if (mostRecentGpsLocation == null
          || mostRecentGpsLocation.getTime() < location.getTime() - maximumMillisecondsAgeOfGpsLocation) {
        publishLocationChanged(location);
      }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }
  };


  public void start() {
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsLocationListener);
    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, networkLocationListener);
  }

  public void stop() {
    locationManager.removeUpdates(gpsLocationListener);
    locationManager.removeUpdates(networkLocationListener);
  }


  public Location getMostRecentGpsLocation() {
    return mostRecentGpsLocation;
  }

  public Location getMostRecentNetworkLocation() {
    return mostRecentNetworkLocation;
  }

  public long getMaximumMillisecondsAgeOfGpsLocation() {
    return maximumMillisecondsAgeOfGpsLocation;
  }

  public void setMaximumMillisecondsAgeOfGpsLocation(long maximumMillisecondsAgeOfGpsLocation) {
    this.maximumMillisecondsAgeOfGpsLocation = maximumMillisecondsAgeOfGpsLocation;
  }

  public Location getMostRecentLocation() {
    return mostRecentLocation;
  }
}

