package nu.postnummeruppror.insamlingsappen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import nu.postnummeruppror.insamlingsappen.domain.Coordinate;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import nu.postnummeruppror.insamlingsappen.domain.PostalAddress;

/**
 * Created by kalle on 12/09/14.
 */
public class DataStore {

  private static final String database_name = "location_samples.db";
  private static final int database_version = 1;

  private static final String table_locationSamples = "location_samples";


  private static final String column_local_identity = "local_identity";
  private static final String column_server_identity = "server_identity";
  private static final String column_streetName = "street_name";
  private static final String column_houseNumber = "house_number";
  private static final String column_houseName = "house_name";
  private static final String column_postalCode = "postal_code";
  private static final String column_postalTown = "postal_town";

  private static final String column_latitude = "latitude";
  private static final String column_longitude = "longitude";

  private static final String column_accuracy = "accuracy";
  private static final String column_altitude = "altitude";
  private static final String column_provider = "provider";

  private static final String column_timestampCreated = "timestamp_created";
  private static final String column_timestampPublished = "timestamp_published";

  private SQLiteOpenHelper openHelper;

  private SQLiteDatabase database;

  public void open(Context context) {

    openHelper = new SQLiteOpenHelper(context, database_name, null, database_version) {
      @Override
      public void onCreate(SQLiteDatabase database) {

        database.execSQL(new StringBuilder(1024)
            .append("CREATE TABLE ").append(table_locationSamples).append(" (")
            .append(column_local_identity).append(" INTEGER primary key autoincrement,")
            .append(column_server_identity).append(" INTEGER,")
            .append(column_streetName).append(" TEXT,")
            .append(column_houseNumber).append(" TEXT,")
            .append(column_houseName).append(" TEXT,")
            .append(column_postalCode).append(" TEXT,")
            .append(column_postalTown).append(" TEXT,")
            .append(column_latitude).append(" REAL,")
            .append(column_longitude).append(" REAL,")
            .append(column_accuracy).append(" REAL,")
            .append(column_altitude).append(" REAL,")
            .append(column_provider).append(" TEXT,")
            .append(column_timestampCreated).append(" INTEGER,")
            .append(column_timestampPublished).append(" INTEGER );").toString());
      }

      @Override
      public void onUpgrade(SQLiteDatabase database, int previousVersion, int currentVersion) {
        // todo
      }
    };

    database = openHelper.getWritableDatabase();

  }

  public void close() {

    openHelper.close();
    database = null;

  }


  public void clear() {
    database.execSQL("DELETE FROM " + table_locationSamples);
  }


  public void update(LocationSample locationSample) {

    ContentValues values = new ContentValues();
    values.put(column_server_identity, locationSample.getServerIdentity());

    if (locationSample.getPostalAddress() != null) {
      values.put(column_streetName, locationSample.getPostalAddress().getStreetName());
      values.put(column_houseNumber, locationSample.getPostalAddress().getHouseNumber());
      values.put(column_houseName, locationSample.getPostalAddress().getHouseName());
      values.put(column_postalCode, locationSample.getPostalAddress().getPostalCode());
      values.put(column_postalTown, locationSample.getPostalAddress().getPostalTown());
    }
    if (locationSample.getCoordinate() != null) {
      values.put(column_latitude, locationSample.getCoordinate().getLatitude());
      values.put(column_longitude, locationSample.getCoordinate().getLongitude());
      values.put(column_accuracy, locationSample.getCoordinate().getAccuracy());
      values.put(column_altitude, locationSample.getCoordinate().getAltitude());
      values.put(column_provider, locationSample.getCoordinate().getProvider());
    }
    values.put(column_timestampCreated, locationSample.getTimestampCreated());
    values.put(column_timestampPublished, locationSample.getTimestampPublished());

    database.update(table_locationSamples, values, column_local_identity + "=?", new String[]{String.valueOf(locationSample.getLocalIdentity())});

  }

  public void create(LocationSample locationSample) {

    ContentValues values = new ContentValues();
    if (locationSample.getPostalAddress() != null) {
      values.put(column_streetName, locationSample.getPostalAddress().getStreetName());
      values.put(column_houseNumber, locationSample.getPostalAddress().getHouseNumber());
      values.put(column_houseName, locationSample.getPostalAddress().getHouseName());
      values.put(column_postalCode, locationSample.getPostalAddress().getPostalCode());
      values.put(column_postalTown, locationSample.getPostalAddress().getPostalTown());
    } else {
      values.put(column_streetName, (String)null);
      values.put(column_houseNumber, (String)null);
      values.put(column_houseName, (String)null);
      values.put(column_postalCode, (String)null);
      values.put(column_postalTown, (String)null);
    }

    if (locationSample.getCoordinate() != null) {
      values.put(column_latitude, locationSample.getCoordinate().getLatitude());
      values.put(column_longitude, locationSample.getCoordinate().getLongitude());
      values.put(column_accuracy, locationSample.getCoordinate().getAccuracy());
      values.put(column_altitude, locationSample.getCoordinate().getAltitude());
      values.put(column_provider, locationSample.getCoordinate().getProvider());
    } else {
      values.put(column_latitude, (Double) null);
      values.put(column_longitude, (Double) null);
      values.put(column_accuracy, (Double) null);
      values.put(column_altitude, (Double) null);
      values.put(column_provider, (String) null);

    }

    values.put(column_timestampCreated, locationSample.getTimestampCreated());
    values.put(column_timestampPublished, locationSample.getTimestampPublished());

    locationSample.setLocalIdentity(database.insert(
        table_locationSamples,
        null,
        values
    ));

  }

  public List<LocationSample> listAll() {

    List<LocationSample> list = new ArrayList<LocationSample>();

    Cursor cursor = database.query(false, table_locationSamples, new String[]{
        column_local_identity,
        column_server_identity,
        column_streetName,
        column_houseNumber,
        column_houseName,
        column_postalCode,
        column_postalTown,
        column_latitude,
        column_longitude,
        column_accuracy,
        column_altitude,
        column_provider,
        column_timestampCreated,
        column_timestampPublished
    }, null, null, null, null, null, null);

    try {

      while (cursor.moveToNext()) {

        LocationSample locationSample = new LocationSample();

        locationSample.setLocalIdentity(cursor.getLong(0));
        if (!cursor.isNull(1)) {
          locationSample.setServerIdentity(cursor.getLong(1));
        }
        if (!cursor.isNull(2)) {
          if (locationSample.getPostalAddress() == null) {
            locationSample.setPostalAddress(new PostalAddress());
          }
          locationSample.getPostalAddress().setStreetName(cursor.getString(2));
        }
        if (!cursor.isNull(3)) {
          if (locationSample.getPostalAddress() == null) {
            locationSample.setPostalAddress(new PostalAddress());
          }
          locationSample.getPostalAddress().setHouseNumber(cursor.getString(3));
        }
        if (!cursor.isNull(4)) {
          if (locationSample.getPostalAddress() == null) {
            locationSample.setPostalAddress(new PostalAddress());
          }
          locationSample.getPostalAddress().setHouseName(cursor.getString(4));
        }
        if (!cursor.isNull(5)) {
          if (locationSample.getPostalAddress() == null) {
            locationSample.setPostalAddress(new PostalAddress());
          }
          locationSample.getPostalAddress().setPostalCode(cursor.getString(5));
        }
        if (!cursor.isNull(6)) {
          if (locationSample.getPostalAddress() == null) {
            locationSample.setPostalAddress(new PostalAddress());
          }
          locationSample.getPostalAddress().setPostalTown(cursor.getString(6));
        }

        if (!cursor.isNull(7)) {
          if (locationSample.getCoordinate() == null) {
            locationSample.setCoordinate(new Coordinate());
          }
          locationSample.getCoordinate().setLatitude(cursor.getDouble(7));
        }
        if (!cursor.isNull(8)) {
          if (locationSample.getCoordinate() == null) {
            locationSample.setCoordinate(new Coordinate());
          }
          locationSample.getCoordinate().setLongitude(cursor.getDouble(8));
        }
        if (!cursor.isNull(9)) {
          if (locationSample.getCoordinate() == null) {
            locationSample.setCoordinate(new Coordinate());
          }
          locationSample.getCoordinate().setAccuracy(cursor.getDouble(9));
        }
        if (!cursor.isNull(10)) {
          if (locationSample.getCoordinate() == null) {
            locationSample.setCoordinate(new Coordinate());
          }
          locationSample.getCoordinate().setAltitude(cursor.getDouble(10));
        }
        if (!cursor.isNull(11)) {
          if (locationSample.getCoordinate() == null) {
            locationSample.setCoordinate(new Coordinate());
          }
          locationSample.getCoordinate().setProvider(cursor.getString(11));
        }


        if (!cursor.isNull(12)) {
          locationSample.setTimestampCreated(cursor.getLong(12));
        }
        if (!cursor.isNull(13)) {
          locationSample.setTimestampPublished(cursor.getLong(13));
        }

        list.add(locationSample);

      }

      return list;

    } finally {
      cursor.close();
    }

  }

  public List<LocationSample> listNonPublished() {

    List<LocationSample> list = new ArrayList<LocationSample>();

    Cursor cursor = database.query(false, table_locationSamples, new String[]{
        column_local_identity,
        column_server_identity,
        column_streetName,
        column_houseNumber,
        column_houseName,
        column_postalCode,
        column_postalTown,
        column_latitude,
        column_longitude,
        column_accuracy,
        column_altitude,
        column_provider,
        column_timestampCreated,
        column_timestampPublished
    }, column_server_identity + " is null", null, null, null, null, null);

    try {

      while (cursor.moveToNext()) {

        LocationSample locationSample = new LocationSample();

        locationSample.setLocalIdentity(cursor.getLong(0));
        if (!cursor.isNull(1)) {
          locationSample.setServerIdentity(cursor.getLong(1));
        }
        if (!cursor.isNull(2)) {
          if (locationSample.getPostalAddress() == null) {
            locationSample.setPostalAddress(new PostalAddress());
          }
          locationSample.getPostalAddress().setStreetName(cursor.getString(2));
        }
        if (!cursor.isNull(3)) {
          if (locationSample.getPostalAddress() == null) {
            locationSample.setPostalAddress(new PostalAddress());
          }
          locationSample.getPostalAddress().setHouseNumber(cursor.getString(3));
        }
        if (!cursor.isNull(4)) {
          if (locationSample.getPostalAddress() == null) {
            locationSample.setPostalAddress(new PostalAddress());
          }
          locationSample.getPostalAddress().setHouseName(cursor.getString(4));
        }
        if (!cursor.isNull(5)) {
          if (locationSample.getPostalAddress() == null) {
            locationSample.setPostalAddress(new PostalAddress());
          }
          locationSample.getPostalAddress().setPostalCode(cursor.getString(5));
        }
        if (!cursor.isNull(6)) {
          if (locationSample.getPostalAddress() == null) {
            locationSample.setPostalAddress(new PostalAddress());
          }
          locationSample.getPostalAddress().setPostalTown(cursor.getString(6));
        }

        if (!cursor.isNull(7)) {
          if (locationSample.getCoordinate() == null) {
            locationSample.setCoordinate(new Coordinate());
          }
          locationSample.getCoordinate().setLatitude(cursor.getDouble(7));
        }
        if (!cursor.isNull(8)) {
          if (locationSample.getCoordinate() == null) {
            locationSample.setCoordinate(new Coordinate());
          }
          locationSample.getCoordinate().setLongitude(cursor.getDouble(8));
        }
        if (!cursor.isNull(9)) {
          if (locationSample.getCoordinate() == null) {
            locationSample.setCoordinate(new Coordinate());
          }
          locationSample.getCoordinate().setAccuracy(cursor.getDouble(9));
        }
        if (!cursor.isNull(10)) {
          if (locationSample.getCoordinate() == null) {
            locationSample.setCoordinate(new Coordinate());
          }
          locationSample.getCoordinate().setAltitude(cursor.getDouble(10));
        }
        if (!cursor.isNull(11)) {
          if (locationSample.getCoordinate() == null) {
            locationSample.setCoordinate(new Coordinate());
          }
          locationSample.getCoordinate().setProvider(cursor.getString(11));
        }


        if (!cursor.isNull(12)) {
          locationSample.setTimestampCreated(cursor.getLong(12));
        }
        if (!cursor.isNull(13)) {
          locationSample.setTimestampPublished(cursor.getLong(13));
        }

        list.add(locationSample);

      }

      return list;

    } finally {
      cursor.close();
    }

  }

}
