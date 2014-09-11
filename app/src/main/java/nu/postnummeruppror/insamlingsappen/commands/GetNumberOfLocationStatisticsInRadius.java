package nu.postnummeruppror.insamlingsappen.commands;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kalle on 08/09/14.
 */
public class GetNumberOfLocationStatisticsInRadius extends ServerJSONAPICommand {

  private Double centroidLatitude;
  private Double centroidLongitude;
  private Double radiusKilometers;

  private String reference;

  // response values

  private Integer numberOfLocationSamples;

  @Override
  protected String getJSONAPIURLSuffix() {
    return "location_sample/search";
  }

  @Override
  protected void assembleRequestJSON(JSONObject requestJSON) throws JSONException {

    requestJSON.put("limit", 0);
    requestJSON.put("reference", reference);


    JSONObject jsonQuery = new JSONObject();
    requestJSON.put("query", jsonQuery);

    jsonQuery.put("type", "coordinate circle envelope");
    jsonQuery.put("latitudeField", "latitude");
    jsonQuery.put("longitudeField", "longitude");
    jsonQuery.put("centroidLatitude", centroidLatitude);
    jsonQuery.put("centroidLongitude", centroidLongitude);
    jsonQuery.put("radiusKilometers", radiusKilometers);
  }


  @Override
  protected void processSuccessfulResponse(JSONObject responseJSON) throws JSONException {
    numberOfLocationSamples = responseJSON.getInt("totalNumberOfMatches");
  }

  public Double getRadiusKilometers() {
    return radiusKilometers;
  }

  public void setRadiusKilometers(Double radiusKilometers) {
    this.radiusKilometers = radiusKilometers;
  }

  public Double getCentroidLatitude() {
    return centroidLatitude;
  }

  public void setCentroidLatitude(Double centroidLatitude) {
    this.centroidLatitude = centroidLatitude;
  }

  public Double getCentroidLongitude() {
    return centroidLongitude;
  }

  public void setCentroidLongitude(Double centroidLongitude) {
    this.centroidLongitude = centroidLongitude;
  }

  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }
  // response values


  public Integer getNumberOfLocationSamples() {
    return numberOfLocationSamples;
  }
}
