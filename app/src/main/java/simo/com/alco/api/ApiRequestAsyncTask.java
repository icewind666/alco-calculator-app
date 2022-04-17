package simo.com.alco.api;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;


/**
 * Async task for all API requests.
 * In case of HTTP POST - should set postRequestBody
 * ( this serialized json will be passed to api)
 */
public class ApiRequestAsyncTask extends AsyncTask<Object, Void, JSONObject> {
    private ApiBaseHandler mHandler; // called on response received
    private boolean doPost; // switch if request is HTTP POST or GET
    private String postRequestBody;
    private Map<String, String> additionalHeaders = new HashMap<>();

    public ApiRequestAsyncTask(ApiBaseHandler handler, boolean isPostRequest) {
        mHandler = handler;
        doPost = isPostRequest;
    }

    public void setPostArguments(String jsonPostBody) {
        postRequestBody = jsonPostBody;
    }

    /**
     * Set all additional headers at once
     * @param headers
     */
    public void setAdditionalHeaders(Map<String, String> headers) {
        additionalHeaders = headers;
    }

    /**
     * Set single additional header
     * @param header
     * @param headerValue
     */
    public void setAdditionalHeader(String header, String headerValue) {
        additionalHeaders.put(header, headerValue);
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected JSONObject doInBackground(Object... params) {
        URLConnection urlConnection;

        try {
            URL theURL = new URL((String) params[0]);
            urlConnection = theURL.openConnection();
            urlConnection.setRequestProperty("Content-Type", "application/json");

            if(additionalHeaders.size() > 0) {
              Set<String> keys = additionalHeaders.keySet();
                for (String key :
                        keys) {
                    String value = additionalHeaders.get(key);
                    urlConnection.setRequestProperty(key, value);
                }
            }

            if(doPost) {
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                if(urlConnection instanceof HttpsURLConnection) {
                    ((HttpsURLConnection) urlConnection).setRequestMethod("POST");
                }
                else {
                    ((HttpURLConnection) urlConnection).setRequestMethod("POST");
                }

                urlConnection.setUseCaches(false);

                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(postRequestBody);
                writer.flush();
                writer.close();
            }

            int responseCode;

            if(urlConnection instanceof HttpsURLConnection) {
                responseCode = ((HttpsURLConnection) urlConnection).getResponseCode();
            }
            else {
                responseCode = ((HttpURLConnection) urlConnection).getResponseCode();
            }

            if(responseCode == HttpsURLConnection.HTTP_OK) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                byte[] contents = new byte[1024];
                int bytesRead;
                StringBuilder strFileContents = new StringBuilder();

                while ((bytesRead = in.read(contents)) != -1) {
                    strFileContents.append(new String(contents, 0, bytesRead));
                }

                return new JSONObject(strFileContents.toString());
            } else {
                Log.d("API", "HTTP REQUEST FAILED");
                JSONObject errorObj = new JSONObject();
                errorObj.put("error", "API Request failed");
                return errorObj;
            }
        } catch (IOException e) {
            //TODO: this is a mess:)))
            e.printStackTrace();
            Log.d("API", "REQUEST FAILED");
            JSONObject errorObj = new JSONObject();

            try {
                errorObj.put("error", "request failed");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            return errorObj;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("API", "REQUEST FAILED");
            JSONObject errorObj = new JSONObject();

            try {
                errorObj.put("error", "request failed");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            return errorObj;

        }
    }


    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        if(jsonObject != null) {
            mHandler.onResponse(jsonObject);
        }
        else {
            Log.d("API", "Received null json while making request");
        }
    }
}