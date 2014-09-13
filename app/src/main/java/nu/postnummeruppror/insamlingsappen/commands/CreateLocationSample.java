package nu.postnummeruppror.insamlingsappen.commands;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kalle on 07/09/14.
 */
public class CreateLocationSample extends ServerJSONAPICommand {

  private String accountIdentity;

  private String application;
  private String applicationVersion;

  private String provider;
  private Double longitude;
  private Double latitude;
  private Double accuracy;
  private Double altitude;

  private String streetName;
  private String houseNumber;
  private String houseName;
  private String postalCode;
  private String postalTown;


  // response value

  private Long identity;



  public CreateLocationSample() {
  }


  @Override
  protected String getJSONAPIURLSuffix() {
    return "location_sample/create";
  }

  @Override
  protected void assembleRequestJSON(JSONObject requestJSON) throws JSONException {
    requestJSON.put("accountIdentity", accountIdentity);

    requestJSON.put("application", application);
    requestJSON.put("applicationVersion", applicationVersion);

    requestJSON.put("latitude", latitude);
    requestJSON.put("longitude", longitude);
    requestJSON.put("altitude", altitude);
    requestJSON.put("provider", provider);
    requestJSON.put("accuracy", accuracy);

    requestJSON.put("postalCode", postalCode);
    requestJSON.put("postalTown", postalTown);
    requestJSON.put("streetName", streetName);
    requestJSON.put("houseNumber", houseNumber);
    requestJSON.put("houseName", houseName);

  }

  @Override
  protected void processSuccessfulResponse(JSONObject responseJSON) throws JSONException {
    identity = responseJSON.getLong("identity");
  }

  public String getHouseName() {
    return houseName;
  }

  public void setHouseName(String houseName) {
    this.houseName = houseName;
  }

  public String getAccountIdentity() {
    return accountIdentity;
  }

  public void setAccountIdentity(String accountIdentity) {
    this.accountIdentity = accountIdentity;
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public Double getAccuracy() {
    return accuracy;
  }

  public void setAccuracy(Double accuracy) {
    this.accuracy = accuracy;
  }

  public Double getAltitude() {
    return altitude;
  }

  public void setAltitude(Double altitude) {
    this.altitude = altitude;
  }

  public String getStreetName() {
    return streetName;
  }

  public void setStreetName(String streetName) {
    this.streetName = streetName;
  }

  public String getHouseNumber() {
    return houseNumber;
  }

  public void setHouseNumber(String houseNumber) {
    this.houseNumber = houseNumber;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public String getPostalTown() {
    return postalTown;
  }

  public void setPostalTown(String postalTown) {
    this.postalTown = postalTown;
  }

  public String getApplication() {
    return application;
  }

  public void setApplication(String application) {
    this.application = application;
  }

  public String getApplicationVersion() {
    return applicationVersion;
  }

  public void setApplicationVersion(String applicationVersion) {
    this.applicationVersion = applicationVersion;
  }

  // response value


  public Long getIdentity() {
    return identity;
  }
}
