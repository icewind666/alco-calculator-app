package simo.com.alco.postal;

import org.json.JSONException;
import org.json.JSONObject;

public class DadataRegion {
    public String name;
    public String region_fias_id;
    public String city_fias_id;
    public String region_kladr_id;
    public JSONObject sourceObj;


    public String region_fias_id() throws JSONException {
        if (sourceObj.getJSONObject("data").has("regions_fias_id")) {
            return sourceObj.getJSONObject("data").getString("region_fias_id");
        }
        return null;
    }

    public String area_fias_id() throws JSONException {
        if (sourceObj.getJSONObject("data").has("area_fias_id")) {
            return sourceObj.getJSONObject("data").getString("area_fias_id");
        }
        return null;
    }


    @Override
    public String toString() {
        return name;
    }
}
