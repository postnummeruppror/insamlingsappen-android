package insamlingsappen.postnummeruppror.nu.insamlingsappen.commands;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kalle on 08/09/14.
 */
public class GetLocationStatistics extends PostJsonToServerCommand {

  private Double latitude;
  private Double longitude;

  // response values

  private Integer numberOfLocationSamples;
  private Integer numberOfAccounts;

  @Override
  protected String postUrlPathFactory() {
    return "/api/statistics/location";
  }

  @Override
  protected void assembleRequestJson(JSONObject json) throws JSONException {
    json.put("latitude", latitude);
    json.put("longitude", longitude);
  }

  @Override
  protected void processSuccessfulResponse(JSONObject json) throws JSONException {

    JSONObject systemJson = json.getJSONObject("system");

    numberOfAccounts = systemJson.getInt("numberOfAccounts");
    numberOfLocationSamples = systemJson.getInt("numberOfLocationSamples");

    // todo within 100 meters
    // todo within 500 meters

  }

  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  // response values


  public Integer getNumberOfLocationSamples() {
    return numberOfLocationSamples;
  }

  public Integer getNumberOfAccounts() {
    return numberOfAccounts;
  }
}
