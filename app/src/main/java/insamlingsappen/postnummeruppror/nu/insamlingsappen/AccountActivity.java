package insamlingsappen.postnummeruppror.nu.insamlingsappen;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.impl.client.DefaultHttpClient;

import java.util.UUID;

import insamlingsappen.postnummeruppror.nu.insamlingsappen.commands.SetAccount;


public class AccountActivity extends ActionBarActivity {

  private EditText firstName;
  private EditText lastName;
  private EditText emailAddress;
  private CheckBox acceptingCcZero;
  private Button submit;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_account);

    acceptingCcZero = (CheckBox) findViewById(R.id.account_setup_cc_zero);
    emailAddress = (EditText) findViewById(R.id.account_setup_email_address);
    firstName = (EditText) findViewById(R.id.account_setup_fist_name);
    lastName = (EditText) findViewById(R.id.account_setup_last_name);
    submit = (Button) findViewById(R.id.account_setup_submit_account);


    // make cc0-link clickable
    emailAddress.setMovementMethod(LinkMovementMethod.getInstance());

    Account account = Account.load(this);
    if (account != null) {
      acceptingCcZero.setChecked(account.isAcceptingCcZero());
      emailAddress.setText(account.getEmailAddress());
      firstName.setText(account.getFirstName());
      lastName.setText(account.getLastName());
    }

    submit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        // todo assert email is set!
        // todo plead for cc0 if not checked!

        Account account = Account.load(AccountActivity.this);
        if (account == null) {
          account = new Account();
          account.setIdentity(UUID.randomUUID().toString());
        }

        account.setEmailAddress(emailAddress.getText().toString());
        account.setAcceptingCcZero(acceptingCcZero.isChecked());
        account.setFirstName(firstName.getText().toString());
        account.setLastName(lastName.getText().toString());

        SetAccount setAccount = new SetAccount();

        setAccount.setIdentity(account.getIdentity());
        setAccount.setFirstName(account.getFirstName());
        setAccount.setLastName(account.getLastName());
        setAccount.setEmailAddress(account.getEmailAddress());
        setAccount.setAcceptingCcZero(account.isAcceptingCcZero());

        setAccount.run();

        if (setAccount.getSuccess()) {

          Account.save(AccountActivity.this, account);
          Toast.makeText(AccountActivity.this, "Kontodetaljer registrerade.", Toast.LENGTH_LONG);
          AccountActivity.this.finish();

        } else {

          Toast.makeText(AccountActivity.this, setAccount.getFailureMessage(), Toast.LENGTH_SHORT).show();
          Log.e("SetAccount", setAccount.getFailureMessage(), setAccount.getFailureException());


        }

      }
    });

  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.account, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
