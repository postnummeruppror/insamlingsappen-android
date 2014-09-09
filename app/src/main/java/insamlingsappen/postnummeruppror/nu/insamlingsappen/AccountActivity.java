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

  private EditText account_setup_emailAddressEditText;
  private CheckBox account_setup_ccZeroCheckBox;
  private Button account_setup_submitAccount;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_account);

    account_setup_ccZeroCheckBox = (CheckBox) findViewById(R.id.account_setup_cc_zero);
    account_setup_emailAddressEditText = (EditText) findViewById(R.id.account_setup_email_address);
    account_setup_submitAccount = (Button) findViewById(R.id.account_setup_submit_account);


    // make cc0-link clickable
    account_setup_emailAddressEditText.setMovementMethod(LinkMovementMethod.getInstance());

    Account account = Account.load(this);
    if (account != null) {
      account_setup_ccZeroCheckBox.setChecked(account.isAcceptingCcZero());
      account_setup_emailAddressEditText.setText(account.getEmailAddress());
    }

    account_setup_submitAccount.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        // todo assert email is set!
        // todo plead for cc0 if not checked!

        Account account = Account.load(AccountActivity.this);
        if (account == null) {
          account = new Account();
          account.setIdentity(UUID.randomUUID().toString());
        }

        account.setEmailAddress(account_setup_emailAddressEditText.getText().toString());
        account.setAcceptingCcZero(account_setup_ccZeroCheckBox.isChecked());

        SetAccount setAccount = new SetAccount();

        setAccount.setServerHostname(Application.serverHostname);
        setAccount.setHttpClient(new DefaultHttpClient());

        setAccount.setIdentity(account.getIdentity());
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
