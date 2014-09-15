package nu.postnummeruppror.insamlingsappen.domain;

/**
 * Created by kalle on 12/09/14.
 */
public class LocationSample {

  private Long localIdentity;
  private Long serverIdentity;

  private Coordinate coordinate;
  private PostalAddress postalAddress;


  private Long timestampCreated;
  private Long timestampPublished;

  @Override
  public String toString() {
    return "LocationSample{" +
        "localIdentity=" + localIdentity +
        ", serverIdentity=" + serverIdentity +
        ", coordinate=" + coordinate +
        ", postalAddress=" + postalAddress +
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

  public Coordinate getCoordinate() {
    return coordinate;
  }

  public void setCoordinate(Coordinate coordinate) {
    this.coordinate = coordinate;
  }

  public PostalAddress getPostalAddress() {
    return postalAddress;
  }

  public void setPostalAddress(PostalAddress postalAddress) {
    this.postalAddress = postalAddress;
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
