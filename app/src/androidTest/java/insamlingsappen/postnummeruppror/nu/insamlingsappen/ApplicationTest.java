package insamlingsappen.postnummeruppror.nu.insamlingsappen;

import android.app.Application;
import android.test.ApplicationTestCase;

import java.util.List;

import nu.postnummeruppror.insamlingsappen.DataStore;
import nu.postnummeruppror.insamlingsappen.LocationSample;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
  public ApplicationTest() {
    super(Application.class);
  }

  public void testDataStore() throws Exception {

    DataStore dataStore = new DataStore();
    dataStore.open(getContext());
    try {

      dataStore.clear();

      LocationSample locationSample = new LocationSample();

      locationSample.setTimestampPublished(null);
      locationSample.setTimestampCreated(1l);

      locationSample.setLatitude(2d);
      locationSample.setLongitude(3d);
      locationSample.setAltitude(4d);
      locationSample.setAccuracy(5d);
      locationSample.setProvider("6");

      locationSample.setHouseNumber("7");
      locationSample.setHouseName("8");
      locationSample.setStreetName("9");
      locationSample.setPostalCode("10");
      locationSample.setPostalTown("11");

      dataStore.create(locationSample);

      assertNotNull(locationSample.getLocalIdentity());

      assertEquals(1, dataStore.listAll().size());
      assertEquals(1, dataStore.listNonPublished().size());



      locationSample = dataStore.listAll().get(0);

      assertEquals(null, locationSample.getTimestampPublished());
      assertEquals(new Long(1l), locationSample.getTimestampCreated());
      assertEquals(new Double(2d), locationSample.getLatitude());
      assertEquals(new Double(3d), locationSample.getLongitude());
      assertEquals(new Double(4d), locationSample.getAltitude());
      assertEquals(new Double(5d), locationSample.getAccuracy());
      assertEquals("6", locationSample.getProvider());
      assertEquals("7", locationSample.getHouseNumber());
      assertEquals("8", locationSample.getHouseName());
      assertEquals("9", locationSample.getStreetName());
      assertEquals("10", locationSample.getPostalCode());
      assertEquals("11", locationSample.getPostalTown());


      locationSample.setServerIdentity(12l);
      locationSample.setTimestampPublished(13l);
      dataStore.update(locationSample);

      assertNotNull(locationSample.getLocalIdentity());

      assertEquals(1, dataStore.listAll().size());
      assertEquals(0, dataStore.listNonPublished().size());


      locationSample = dataStore.listAll().get(0);

      assertEquals(new Long(12l), locationSample.getServerIdentity());
      assertEquals(new Long(13l), locationSample.getTimestampPublished());
      assertEquals(new Long(1l), locationSample.getTimestampCreated());
      assertEquals(new Double(2d), locationSample.getLatitude());
      assertEquals(new Double(3d), locationSample.getLongitude());
      assertEquals(new Double(4d), locationSample.getAltitude());
      assertEquals(new Double(5d), locationSample.getAccuracy());
      assertEquals("6", locationSample.getProvider());
      assertEquals("7", locationSample.getHouseNumber());
      assertEquals("8", locationSample.getHouseName());
      assertEquals("9", locationSample.getStreetName());
      assertEquals("10", locationSample.getPostalCode());
      assertEquals("11", locationSample.getPostalTown());


    } finally {
      dataStore.close();
    }

  }
}