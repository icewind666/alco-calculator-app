package simo.com.alco.postal;

import org.json.JSONException;
import org.json.JSONObject;

public class DadataHouse {
    public String name;

    JSONObject sourceObj;


    public String street_fias_id() throws JSONException {
        return sourceObj.getJSONObject("data").getString("street_fias_id");
    }

    public String city_fias_id() throws JSONException {
        return sourceObj.getJSONObject("data").getString("city_fias_id");
    }

    public String region_fias_id() throws JSONException {
        return sourceObj.getJSONObject("data").getString("region_fias_id");
    }

    public String house_fias_id() throws JSONException {
        return sourceObj.getJSONObject("data").getString("street_fias_id");
    }

    public String postalCode() throws JSONException {
        return sourceObj.getJSONObject("data").getString("postal_code");
    }


    @Override
    public String toString() {
        return name;
    }
}
