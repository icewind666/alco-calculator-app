package simo.com.alco.postal;

import org.json.JSONException;
import org.json.JSONObject;

public class DadataStreet {
    public String name;

    public JSONObject sourceObj;


    public String street_fias_id() throws JSONException {
        return sourceObj.getJSONObject("data").getString("street_fias_id");
    }


    @Override
    public String toString() {
        return name;
    }
}
