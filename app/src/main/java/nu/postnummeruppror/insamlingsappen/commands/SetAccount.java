package nu.postnummeruppror.insamlingsappen.commands;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kalle on 07/09/14.
 */
public class SetAccount extends ServerJSONAPICommand {

  private String identity;
  private String emailAddress;
  private String firstName;
  private String lastName;
  private Boolean acceptingCcZero;

  public SetAccount() {
  }

  @Override
  protected String getJSONAPIURLSuffix() {
    return "account/set";
  }

  @Override
  protected void assembleRequestJSON(JSONObject requestJSON) throws JSONException {
    requestJSON.put("identity", identity);
    requestJSON.put("emailAddress", emailAddress);
    requestJSON.put("acceptingCcZero", acceptingCcZero);
    requestJSON.put("firstName", firstName);
    requestJSON.put("lastName", lastName);
  }

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

  public Boolean getAcceptingCcZero() {
    return acceptingCcZero;
  }

  public void setAcceptingCcZero(Boolean acceptingCcZero) {
    this.acceptingCcZero = acceptingCcZero;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
}
