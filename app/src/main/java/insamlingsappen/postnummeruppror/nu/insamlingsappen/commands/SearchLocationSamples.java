package insamlingsappen.postnummeruppror.nu.insamlingsappen.commands;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kalle on 08/09/14.
 */
public class SearchLocationSamples extends PostJsonToServerCommand {

  private Double south;
  private Double west;
  private Double north;
  private Double east;

  private Integer maximumHits;
  private String reference;

  public SearchLocationSamples() {
  }

  @Override
  protected String postUrlPathFactory() {
    return "/api/location_sample/search";
  }

  @Override
  protected void assembleRequestJson(JSONObject json) throws JSONException {
    json.put("maximumHits", maximumHits);
    json.put("reference", reference);
    json.put("south", south);
    json.put("west", west);
    json.put("north", north);
    json.put("east", east);
  }

  private Hits hits;

  @Override
  protected void processSuccessfulResponse(JSONObject json) throws JSONException {
    hits = new Hits();
    hits.numberOfHits = json.getInt("numberOfHits");
    hits.reference = json.getString("reference");
    JSONArray jsonHits = json.getJSONArray("hits");
    for (int i = 0; i < jsonHits.length(); i++) {
      JSONObject jsonHit = jsonHits.getJSONObject(i);
      Hit hit = new Hit();
      hit.identity = jsonHit.getLong("identity");
      hit.latitude = jsonHit.getDouble("latitude");
      hit.longitude = jsonHit.getDouble("longitude");
      hit.accuracy = jsonHit.getDouble("accuracy");
      hit.altitude = jsonHit.getDouble("altitude");

      hit.postalCode = jsonHit.getString("postalCode");
      hit.postalTown = jsonHit.getString("postalTown");
      hit.streetName = jsonHit.getString("streetName");
      hit.houseNumber = jsonHit.getString("houseNumber");

      hits.add(hit);
    }

  }

  public Double getSouth() {
    return south;
  }

  public void setSouth(Double south) {
    this.south = south;
  }

  public Double getWest() {
    return west;
  }

  public void setWest(Double west) {
    this.west = west;
  }

  public Double getNorth() {
    return north;
  }

  public void setNorth(Double north) {
    this.north = north;
  }

  public Double getEast() {
    return east;
  }

  public void setEast(Double east) {
    this.east = east;
  }

  public Integer getMaximumHits() {
    return maximumHits;
  }

  public void setMaximumHits(Integer maximumHits) {
    this.maximumHits = maximumHits;
  }

  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }

// response values

  public Hits getHits() {
    return hits;
  }


  public static class Hits extends ArrayList<Hit> {
    private int numberOfHits;
    private String reference;

    public int getNumberOfHits() {
      return numberOfHits;
    }

    public String getReference() {
      return reference;
    }
  }

  public static class Hit {

    private long identity;
    private double latitude;
    private double longitude;
    private double accuracy;
    private double altitude;
    private String postalCode;
    private String postalTown;
    private String streetName;
    private String houseNumber;


    public long getIdentity() {
      return identity;
    }

    public double getLatitude() {
      return latitude;
    }

    public double getLongitude() {
      return longitude;
    }

    public double getAccuracy() {
      return accuracy;
    }

    public double getAltitude() {
      return altitude;
    }

    public String getPostalCode() {
      return postalCode;
    }

    public String getPostalTown() {
      return postalTown;
    }

    public String getStreetName() {
      return streetName;
    }

    public String getHouseNumber() {
      return houseNumber;
    }
  }


}
