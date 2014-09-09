package insamlingsappen.postnummeruppror.nu.insamlingsappen.commands;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kalle on 08/09/14.
 */
public class GetServerStatistics extends PostJsonToServerCommand {

  // response values

  private Integer numberOfLocationSamples;
  private Integer numberOfAccounts;
  private Integer numberOfPostalCodes;
  private Integer numberOfPostalTowns;

  @Override
  protected String postUrlPathFactory() {
    return "/api/statistics/server";
  }

  @Override
  protected void assembleRequestJson(JSONObject json) throws JSONException {
  }

  @Override
  protected void processSuccessfulResponse(JSONObject json) throws JSONException {

    numberOfAccounts = json.getInt("numberOfAccounts");
    numberOfLocationSamples = json.getInt("numberOfLocationSamples");
    numberOfPostalCodes = json.getInt("numberOfPostalCodes");
    numberOfPostalTowns = json.getInt("numberOfPostalTowns");

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
