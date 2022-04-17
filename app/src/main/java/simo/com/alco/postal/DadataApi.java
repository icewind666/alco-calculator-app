package simo.com.alco.postal;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import simo.com.alco.R;

/**
 * Wrapper for Dadata suggestions api.
 *
 */
public class DadataApi {
    OkHttpClient client = new OkHttpClient();
    public Context context;
    private String apiToken = "e7a2c3626b5473a9af6c0a8570b85e0658ad4345";


    public void searchRegions(String expr, Callback handler) {
        /*
            {
              "from_bound": {
                "value": "region"
              },
              "to_bound": {
                "value": "region"
              },
              "query": "самарская"
            }
         */
        try {
            JSONObject regionSearchExpr = new JSONObject();
            JSONObject valueObj = new JSONObject();
            valueObj.put("value", "region");
            regionSearchExpr.put("from_bound", valueObj);
            regionSearchExpr.put("query", expr);
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"), regionSearchExpr.toString());
            String headersStr = "Token " + apiToken;
            Request request = new Request.Builder()
                    .url(context.getString(R.string.dadataAddressUrl))
                    .addHeader("Authorization", headersStr)
                    .post(body)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(handler);
        } catch (JSONException e) {
            Log.e("API", "Error while receiving dadata regions");
            e.printStackTrace();
        }

    }

    public JSONArray searchRegionsSync(String expr) {
        try {
            JSONObject regionSearchExpr = new JSONObject();
            JSONObject valueObj = new JSONObject();
            valueObj.put("value", "region");
            regionSearchExpr.put("from_bound", valueObj);
            regionSearchExpr.put("to_bound", valueObj);
            regionSearchExpr.put("query", expr);
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"), regionSearchExpr.toString());
            String headersStr = "Token " + apiToken;
            Request request = new Request.Builder()
                    .url(context.getString(R.string.dadataAddressUrl))
                    .addHeader("Authorization", headersStr)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            JSONObject responseObj = new JSONObject(response.body().string());
            JSONArray suggestions = responseObj.getJSONArray("suggestions");
            return suggestions;
        } catch (JSONException e) {
            Log.e("API", "Error while receiving dadata regions");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("API", "Error while receiving dadata regions");
            e.printStackTrace();
        }
        return null;
    }

    public JSONArray searchCitiesSync(String expr, String region_fias) {
        try {
            JSONObject citySearchExpr = new JSONObject();
            JSONObject valueObj = new JSONObject();

            JSONArray locations = new JSONArray();
            JSONObject region = new JSONObject();
            region.put("region_fias_id", region_fias);
            locations.put(region);

            citySearchExpr.put("locations", locations);

            valueObj.put("value", "city");
            citySearchExpr.put("from_bound", valueObj);
            citySearchExpr.put("to_bound", valueObj);
            citySearchExpr.put("restrict_value", "true");
            citySearchExpr.put("query", expr);
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"), citySearchExpr.toString());
            String headersStr = "Token " + apiToken;
            Request request = new Request.Builder()
                    .url(context.getString(R.string.dadataAddressUrl))
                    .addHeader("Authorization", headersStr)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            JSONObject responseObj = new JSONObject(response.body().string());
            JSONArray suggestions = responseObj.getJSONArray("suggestions");
            return suggestions;
        } catch (JSONException e) {
            Log.e("API", "Error while receiving dadata cities");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("API", "Error while receiving dadata cities");
            e.printStackTrace();
        }
        return null;
    }


    public JSONArray searchStreetsSync(String expr, String city_fias) {
        try {
            JSONObject citySearchExpr = new JSONObject();
            JSONObject valueObj = new JSONObject();

            JSONArray locations = new JSONArray();
            JSONObject region = new JSONObject();
            region.put("city_fias_id", city_fias);
            locations.put(region);

            citySearchExpr.put("locations", locations);

            valueObj.put("value", "streets");
            citySearchExpr.put("from_bound", valueObj);
            citySearchExpr.put("to_bound", valueObj);
            citySearchExpr.put("restrict_value", "true");
            citySearchExpr.put("query", expr);
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"), citySearchExpr.toString());
            String headersStr = "Token " + apiToken;
            Request request = new Request.Builder()
                    .url(context.getString(R.string.dadataAddressUrl))
                    .addHeader("Authorization", headersStr)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            JSONObject responseObj = new JSONObject(response.body().string());
            JSONArray suggestions = responseObj.getJSONArray("suggestions");
            return suggestions;
        } catch (JSONException e) {
            Log.e("API", "Error while receiving dadata streets");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("API", "Error while receiving dadata streets");
            e.printStackTrace();
        }
        return null;
    }



    public JSONArray searchHousesSync(String expr, String street_fias) {
        try {
            JSONObject citySearchExpr = new JSONObject();
            JSONObject valueObj = new JSONObject();

            JSONArray locations = new JSONArray();
            JSONObject region = new JSONObject();
            region.put("street_fias_id", street_fias);
            locations.put(region);

            citySearchExpr.put("locations", locations);

            valueObj.put("value", "house");
            citySearchExpr.put("from_bound", valueObj);
            citySearchExpr.put("restrict_value", "true");
            citySearchExpr.put("query", expr);
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"), citySearchExpr.toString());
            String headersStr = "Token " + apiToken;
            Request request = new Request.Builder()
                    .url(context.getString(R.string.dadataAddressUrl))
                    .addHeader("Authorization", headersStr)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            JSONObject responseObj = new JSONObject(response.body().string());
            JSONArray suggestions = responseObj.getJSONArray("suggestions");
            return suggestions;
        } catch (JSONException e) {
            Log.e("API", "Error while receiving dadata streets");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("API", "Error while receiving dadata streets");
            e.printStackTrace();
        }
        return null;
    }



}
