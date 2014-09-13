package nu.postnummeruppror.insamlingsappen;

/**
 * Created by kalle on 12/09/14.
 */
public class LocationSample {

  private Long localIdentity;
  private Long serverIdentity;

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

  private Long timestampCreated;
  private Long timestampPublished;

  @Override
  public String toString() {
    return "LocationSample{" +
        "localIdentity=" + localIdentity +
        ", serverIdentity=" + serverIdentity +
        ", provider='" + provider + '\'' +
        ", longitude=" + longitude +
        ", latitude=" + latitude +
        ", accuracy=" + accuracy +
        ", altitude=" + altitude +
        ", streetName='" + streetName + '\'' +
        ", houseNumber='" + houseNumber + '\'' +
        ", houseName='" + houseName + '\'' +
        ", postalCode='" + postalCode + '\'' +
        ", postalTown='" + postalTown + '\'' +
        ", timestampCreated=" + timestampCreated +
        ", timestampPublished=" + timestampPublished +
        '}';
  }

  public Long getLocalIdentity() {
    return localIdentity;
  }

  public void setLocalIdentity(Long localIdentity) {
    this.localIdentity = localIdentity;
  }

  public Long getServerIdentity() {
    return serverIdentity;
  }

  public void setServerIdentity(Long serverIdentity) {
    this.serverIdentity = serverIdentity;
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

  public String getHouseName() {
    return houseName;
  }

  public void setHouseName(String houseName) {
    this.houseName = houseName;
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

  public Long getTimestampCreated() {
    return timestampCreated;
  }

  public void setTimestampCreated(Long timestampCreated) {
    this.timestampCreated = timestampCreated;
  }

  public Long getTimestampPublished() {
    return timestampPublished;
  }

  public void setTimestampPublished(Long timestampPublished) {
    this.timestampPublished = timestampPublished;
  }
}
