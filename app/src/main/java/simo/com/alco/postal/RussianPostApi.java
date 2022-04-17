package simo.com.alco.postal;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import simo.com.alco.api.ApiBaseHandler;
import simo.com.alco.api.ApiRequestAsyncTask;

public class RussianPostApi {
    private ApiBaseHandler mHandler;
    private String indexFrom = "390048";
    private String postApiAddr = "https://otpravka-api.pochta.ru/1.0/tariff";


    public RussianPostApi(ApiBaseHandler handler) {
        mHandler = handler;
    }

    /**
     *
     * @param addr
     * @param requestBody
     * @param doPost
     */
    private void makeRequest(String addr, String requestBody, boolean doPost) {
        ApiRequestAsyncTask task = new ApiRequestAsyncTask(mHandler, doPost);

        if(doPost) {
            task.setPostArguments(requestBody);
        }

        task.setAdditionalHeader("Authorization", "AccessToken mLpNdpdQrICCGjFdRNxXQqq1mEZayH8j");
        task.setAdditionalHeader("X-User-Authorization", "Basic c2ltb3Jha2thdXNAeWFuZGV4LnJ1OnF5Y2Nvdy1jZXJ3dTAtcnlSdnl3");
        task.execute(addr, requestBody, doPost);
    }


    /**
     * Returns amount of money post will cost
     * @param to
     * @param mass
     */
    public void getFee(String to, double mass) {

        try {
            JSONObject postBody = new JSONObject("{\n" +
                    "  \"index-from\": \"390047\",\n" +
                    "  \"mail-type\": \"POSTAL_PARCEL\",\n" +
                    "  \"index-to\": \""+to+"\",\n" +
                    "  \"mail-category\": \"ORDINARY\",\n" +
                    "  \"mass\": "+mass+",\n" +
                    "  \"with-order-of-notice\": false,\n"+
                    "  \"with-simple-notice\": true"+

            "}");
            makeRequest(postApiAddr, postBody.toString(), true);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("API", "getFee() method got exception");
        }

    }


}
