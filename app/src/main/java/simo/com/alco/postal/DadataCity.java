package simo.com.alco.postal;

import org.json.JSONException;
import org.json.JSONObject;

public class DadataCity {
    public String name;
    public String region_fias_id;
    public String city_fias_id;
    public String region_kladr_id;

    public JSONObject sourceObj;


    public String city_fias_id() throws JSONException {
        return sourceObj.getJSONObject("data").getString("city_fias_id");
    }


    @Override
    public String toString() {
        return name;
    }
}
