package nu.postnummeruppror.insamlingsappen.commands;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kalle on 08/09/14.
 */
public class GetServerStatistics extends ServerJSONAPICommand {

  // response values

  private Integer numberOfLocationSamples;
  private Integer numberOfAccounts;
  private Integer numberOfPostalCodes;
  private Integer numberOfPostalTowns;

  @Override
  protected String getJSONAPIURLSuffix() {
    return "statistics/server";
  }

  @Override
  protected void assembleRequestJSON(JSONObject requestJSON) throws JSONException {
  }

  @Override
  protected void processSuccessfulResponse(JSONObject responseJSON) throws JSONException {

    numberOfAccounts = responseJSON.getInt("numberOfAccounts");
    numberOfLocationSamples = responseJSON.getInt("numberOfLocationSamples");
    numberOfPostalCodes = responseJSON.getInt("numberOfPostalCodes");
    numberOfPostalTowns = responseJSON.getInt("numberOfPostalTowns");

  }

  // response values



  public Integer getNumberOfLocationSamples() {
    return numberOfLocationSamples;
  }

  public Integer getNumberOfPostalCodes() {
    return numberOfPostalCodes;
  }

  public Integer getNumberOfPostalTowns() {
    return numberOfPostalTowns;
  }

  public Integer getNumberOfAccounts() {
    return numberOfAccounts;
  }
}
