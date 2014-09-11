package nu.postnummeruppror.insamlingsappen;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by kalle on 08/09/14.
 */
public class Account {

  private static final String preferencesFile = "Account";
  private static final String field_identity = "identity";
  private static final String field_emailAddress = "emailAddress";
  private static final String field_firstName = "firstName";
  private static final String field_lastName = "lastName";
  private static final String field_acceptingCcZero = "acceptingCcZero";

  public static SharedPreferences getAccountPreferences(Context context) {
    return context.getSharedPreferences(preferencesFile, Context.MODE_PRIVATE);
  }

  public static Account load(Context context) {

    SharedPreferences accountPreferences = getAccountPreferences(context);

    Account account = null;
    String identity = accountPreferences.getString(field_identity, null);
    if (identity != null) {
      account = new Account();
      account.setIdentity(identity);
      account.setEmailAddress(accountPreferences.getString(field_emailAddress, null));
      account.setAcceptingCcZero(accountPreferences.getBoolean(field_acceptingCcZero, false));
      account.setFirstName(accountPreferences.getString(field_firstName, null));
      account.setLastName(accountPreferences.getString(field_lastName, null));
    }

    return account;

  }


  public static void save(Context context, Account account) {

    SharedPreferences accountPreferences = getAccountPreferences(context);

    SharedPreferences.Editor editor = accountPreferences.edit();
    editor.putString(field_identity, account.getIdentity());
    editor.putString(field_emailAddress, account.getEmailAddress());
    editor.putString(field_firstName, account.getFirstName());
    editor.putString(field_lastName, account.getLastName());
    editor.putBoolean(field_acceptingCcZero, account.isAcceptingCcZero());
    editor.apply();

  }

  private String identity;
  private String emailAddress;
  private boolean acceptingCcZero;
  private String firstName;
  private String lastName;

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
