package nu.postnummeruppror.insamlingsappen.commands;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kalle on 08/09/14.
 */
public class FindPostalTownFromPostalCode extends ServerJSONAPICommand {

  public FindPostalTownFromPostalCode() {
    setApiVersion("0.0.6");
  }

  private String postalCode;

  // response values

  private String postalTown;

  @Override
  protected String getJSONAPIURLSuffix() {
    return "postalTown/findUsingPostalCode";
  }

  @Override
  protected void assembleRequestJSON(JSONObject requestJSON) throws JSONException {
    requestJSON.put("postalCode", postalCode);
  }

  @Override
  protected void processSuccessfulResponse(JSONObject responseJSON) throws JSONException {
    if (responseJSON.getBoolean("success")) {
      JSONArray results = responseJSON.getJSONArray("results");
      if (results.length() == 1
          || (results.length() > 1 && results.getJSONObject(0).getInt("score") >= results.getJSONObject(1).getInt("score") * 3)) {
        postalTown = results.getJSONObject(0).getString("postalTown");
      }
    }
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
}
