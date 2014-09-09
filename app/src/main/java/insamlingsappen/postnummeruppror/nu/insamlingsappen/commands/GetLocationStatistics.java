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

  private Integer locationSamplesWithinOneHundredMetersRadius;
  private Integer locationSamplesWithinFiveHundredMetersRadius;

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

    locationSamplesWithinOneHundredMetersRadius = json.getInt("locationSamplesWithinOneHundredMetersRadius");
    locationSamplesWithinFiveHundredMetersRadius = json.getInt("locationSamplesWithinFiveHundredMetersRadius");

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


  public Integer getLocationSamplesWithinOneHundredMetersRadius() {
    return locationSamplesWithinOneHundredMetersRadius;
  }

  public Integer getLocationSamplesWithinFiveHundredMetersRadius() {
    return locationSamplesWithinFiveHundredMetersRadius;
  }
}
