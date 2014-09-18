package insamlingsappen.postnummeruppror.nu.insamlingsappen;

import android.app.Application;
import android.test.ApplicationTestCase;

import nu.postnummeruppror.insamlingsappen.DataStore;
import nu.postnummeruppror.insamlingsappen.domain.Coordinate;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import nu.postnummeruppror.insamlingsappen.domain.PostalAddress;

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

      locationSample.setCoordinate(new Coordinate());

      locationSample.getCoordinate().setLatitude(2d);
      locationSample.getCoordinate().setLongitude(3d);
      locationSample.getCoordinate().setAltitude(4d);
      locationSample.getCoordinate().setAccuracy(5d);
      locationSample.getCoordinate().setProvider("6");

      locationSample.setPostalAddress(new PostalAddress());

      locationSample.getPostalAddress().setHouseNumber("7");
      locationSample.getPostalAddress().setHouseName("8");
      locationSample.getPostalAddress().setStreetName("9");
      locationSample.getPostalAddress().setPostalCode("10");
      locationSample.getPostalAddress().setPostalTown("11");

      dataStore.create(locationSample);

      assertNotNull(locationSample.getLocalIdentity());

      assertEquals(1, dataStore.listAll().size());
      assertEquals(1, dataStore.listNonPublished().size());



      locationSample = dataStore.listAll().get(0);

      assertEquals(null, locationSample.getTimestampPublished());
      assertEquals(new Long(1l), locationSample.getTimestampCreated());
      assertEquals(new Double(2d), locationSample.getCoordinate().getLatitude());
      assertEquals(new Double(3d), locationSample.getCoordinate().getLongitude());
      assertEquals(new Double(4d), locationSample.getCoordinate().getAltitude());
      assertEquals(new Double(5d), locationSample.getCoordinate().getAccuracy());
      assertEquals("6", locationSample.getCoordinate().getProvider());

      assertEquals("7", locationSample.getPostalAddress().getHouseNumber());
      assertEquals("8", locationSample.getPostalAddress().getHouseName());
      assertEquals("9", locationSample.getPostalAddress().getStreetName());
      assertEquals("10", locationSample.getPostalAddress().getPostalCode());
      assertEquals("11", locationSample.getPostalAddress().getPostalTown());


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
      assertEquals(new Double(2d), locationSample.getCoordinate().getLatitude());
      assertEquals(new Double(3d), locationSample.getCoordinate().getLongitude());
      assertEquals(new Double(4d), locationSample.getCoordinate().getAltitude());
      assertEquals(new Double(5d), locationSample.getCoordinate().getAccuracy());
      assertEquals("6", locationSample.getCoordinate().getProvider());

      assertEquals("7", locationSample.getPostalAddress().getHouseNumber());
      assertEquals("8", locationSample.getPostalAddress().getHouseName());
      assertEquals("9", locationSample.getPostalAddress().getStreetName());
      assertEquals("10", locationSample.getPostalAddress().getPostalCode());
      assertEquals("11", locationSample.getPostalAddress().getPostalTown());


    } finally {
      dataStore.close();
    }

  }
}