package insamlingsappen.postnummeruppror.nu.insamlingsappen;

/**
 * Created by kalle on 08/09/14.
 */
public class Account {

  private String identity;
  private String emailAddress;
  private boolean acceptingCcZero;

  public String getIdentity() {
    return identity;
  }

  public void setIdentity(String identity) {
    this.identity = identity;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public boolean isAcceptingCcZero() {
    return acceptingCcZero;
  }

  public void setAcceptingCcZero(boolean acceptingCcZero) {
    this.acceptingCcZero = acceptingCcZero;
  }
}
