package nu.postnummeruppror.insamlingsappen.commands;

import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOError;
import java.io.IOException;
import java.io.StringWriter;

import nu.postnummeruppror.insamlingsappen.Application;

/**
 * Created by kalle on 07/09/14.
 */
public abstract class ServerJSONAPICommand implements Runnable {

  // response values

  private Boolean success;
  private String failureMessage;
  private Exception failureException;

  /**
   * @return Path part of URL suffixing /api/[version]/, e.g. 'location_sample/search'
   */
  protected abstract String getJSONAPIURLSuffix();

  protected abstract void assembleRequestJSON(JSONObject requestJSON) throws JSONException;

  protected void processSuccessfulResponse(JSONObject responseJSON) throws JSONException {

  }

  @Override
  public void run() {
    JSONObject json = new JSONObject();

    try {

      assembleRequestJSON(json);

    } catch (JSONException e) {
      success = false;
      failureException = e;
      failureMessage = "Kunde inte skapa JSON-förfrågan.";
      return;
    }


    RequestConfig.Builder requestBuilder = RequestConfig.custom();
    requestBuilder = requestBuilder.setConnectTimeout(30000);
    requestBuilder = requestBuilder.setConnectionRequestTimeout(30000);

    HttpClientBuilder builder = HttpClientBuilder.create();
    builder.setDefaultRequestConfig(requestBuilder.build());
    builder.setConnectionManager(new BasicHttpClientConnectionManager());
    CloseableHttpClient httpClient = builder.build();
    try {

      HttpResponse response;
      try {
        String suffix = getJSONAPIURLSuffix();
        while (suffix.startsWith("/")) {
          suffix = suffix.substring(1);
        }
        HttpPost post = new HttpPost("http://" + Application.serverHostname + "/api/" + Application.serverVersion + "/" + suffix);
        post.setEntity(new StringEntity(json.toString(), "UTF-8"));

        response = httpClient.execute(post);

      } catch (Exception e) {
        success = false;
        failureException = e;
        failureMessage = "Kunde inte skicka förfrågan till server.";
        return;
      }


      if (response.getStatusLine().getStatusCode() != 200) {
        success = false;
        failureException = null;
        failureMessage = "Servern svarade med HTTP " + response.getStatusLine().getStatusCode();
        return;
      }

      try {

        StringWriter jsonWriter = new StringWriter(1024);
        IOUtils.copy(response.getEntity().getContent(), jsonWriter);
        JSONObject responseJson = new JSONObject(new JSONTokener(jsonWriter.toString()));
        if (responseJson.getBoolean("success")) {
          success = true;
          processSuccessfulResponse(responseJson);

        } else {
          success = false;
          failureException = null;
          failureMessage = "Servern avvisade förfrågan.";

        }


      } catch (Exception e) {
        success = false;
        failureException = e;
        failureMessage = "Kunde inte läsa svar från servern.";

      }

    } finally {
      try {
        httpClient.close();
      } catch (IOException ioe) {
        Log.e(getClass().getSimpleName(), "Failed to close HTTP client", ioe);
      }
    }

  }

  // response values

  public Boolean getSuccess() {
    return success;
  }

  public String getFailureMessage() {
    return failureMessage;
  }

  public Exception getFailureException() {
    return failureException;
  }
}
