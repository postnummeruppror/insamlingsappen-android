package insamlingsappen.postnummeruppror.nu.insamlingsappen.commands;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.StringWriter;

/**
 * Created by kalle on 07/09/14.
 */
public abstract  class PostJsonToServerCommand implements Runnable {

  private HttpClient httpClient;
  private String serverHostname;

  // response values

  private Boolean success;
  private String failureMessage;
  private Exception failureException;

  /** @return Path part of URL, needs to start with a forward slash. E.g. '/api/foo/bar' */
  protected abstract String postUrlPathFactory();
  protected abstract void requestJsonBuilder(JSONObject json) throws JSONException;

  @Override
  public void run() {
    JSONObject json = new JSONObject();

    try {
      
      requestJsonBuilder(json);

    } catch (JSONException e) {
      success = false;
      failureException = e;
      failureMessage = "Kunde inte skapa JSON-förfrågan.";
      return;
    }

    HttpResponse response;
    try {
      HttpPost post = new HttpPost("http://" + serverHostname + postUrlPathFactory());
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

  }

  public HttpClient getHttpClient() {
    return httpClient;
  }

  public void setHttpClient(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public String getServerHostname() {
    return serverHostname;
  }

  public void setServerHostname(String serverHostname) {
    this.serverHostname = serverHostname;
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
