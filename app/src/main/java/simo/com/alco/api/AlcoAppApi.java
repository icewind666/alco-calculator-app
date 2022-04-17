package simo.com.alco.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;

import ru.yandex.money.android.sdk.PaymentMethodType;

/**
 * Proxy for our serverside api.
 *
 */
public class AlcoAppApi  {
    public static String AUTH_TICKET;
    public static ApiUser user;
    private ApiBaseHandler mHandler;
    private String applicationSecretToken = "api_secret_token_password!@#$%";

    public AlcoAppApi(ApiBaseHandler handler) {
        mHandler = handler;
    }


    /**
     * Logs user in
     */
    public void loginUser() {
        createAuthenticator(user.username, user.password);
        makeRequestWithoutToken(ApiKnownUrls.LOGIN_URL, "", false);
    }


    public void getUserByTicket(String authTicket) {

    }


    /**
     * Add items to user cart
     */
    public void registerUser(ApiUser newUser) {
        JSONObject postData = new JSONObject();

        try {
            postData.put("username", newUser.username);
            postData.put("password", newUser.password);
            postData.put("opt_in", newUser.optIn);
            postData.put("application_id", applicationSecretToken);

        } catch (JSONException e) {
            Log.e("API", "Failed to set POST arguments", e);
        }

        makeRequest(ApiKnownUrls.REGISTER_URL, postData.toString(), true);
    }

    /**
     *
     * @param username
     * @param password
     */
    private static void createAuthenticator(final String username, final String password) {
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password.toCharArray());
            }
        });

    }

    /**
     * Returns categories by process
     * @param process
     */
    public void getCategoriesByProcess(ApiProcess process) {
        if(notReadyForRequest())
            return;
        makeRequest(String.format(ApiKnownUrls.CATEGORIES_BY_PROCESS_URL, process.id),
                "", false);
    }

    /**
     *
     * @param category
     */
    public void getSubcategoriesByCategory(ApiCategory category) {
        if(notReadyForRequest())
            return;
        makeRequest(String.format(ApiKnownUrls.SUBCATEGORIES_BY_CATEGORY_URL, category.id),
                "", false);

    }

    /**
     *
     */
    public void getProcesses() {
        if(notReadyForRequest())
            return;
        makeRequest(ApiKnownUrls.PROCESSES_URL, "", false);
    }

    /**
     *
     * @param url
     * @return
     */
    public static Bitmap GetImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (Exception e) {
            Log.e("API", "Error getting bitmap", e);
        }
        return bm;
    }





    /**
     *
     */
    public void getItems() {
        if(notReadyForRequest())
            return;
        makeRequest(ApiKnownUrls.ITEMS_URL, "", false);
    }

    /**
     *
     */
    public void getItemsBySubcategory(ApiSubcategory subcat) {
        if(notReadyForRequest())
            return;
        String url = String.format(ApiKnownUrls.ITEMS_BY_SUBCAT_URL, subcat.id);
        makeRequest(url, "", false);
    }

    /**
     *
     */
    public void getCategories() {
        if(notReadyForRequest())
            return;
        makeRequest(ApiKnownUrls.CATEGORIES_URL, "", false);
    }

    /**
     * Add items to user cart
     */
    public void addToCart(ApiCartItem itemToAdd) {
        if(notReadyForRequest())
            return;

        JSONObject postData = new JSONObject();

        try {
            postData.put("shipping_method", 1);
            postData.put("quantity", itemToAdd.quantityInCart);
            postData.put("payment_method", 2);

        } catch (JSONException e) {
            Log.e("API", "Failed to set POST arguments", e);
        }

        String addToCartUrl = String.format(ApiKnownUrls.ADD_TO_CART, itemToAdd.id);
        makeRequest(addToCartUrl, postData.toString(), true);
    }

    /**
     *
     */
    public void cartItemsCount() {
        if(notReadyForRequest())
            return;
        makeRequest(ApiKnownUrls.CART_COUNT, "", false);
    }


    /**
     *
     */
    public void getSubcategories() {
        if(notReadyForRequest())
            return;
        makeRequest(ApiKnownUrls.SUBCATEGORIES_URL, "", false);
    }

    /**
     *
     * @return
     */
    private boolean notReadyForRequest() {
        if(this.mHandler == null) {
            Log.d("API", "Api precheck FAILED");
            return true;
        }


        if (AlcoAppApi.AUTH_TICKET != null)
            return AlcoAppApi.AUTH_TICKET.isEmpty();
        else {
            Log.d("API", "Api precheck FAILED");
        }

        return true;
    }


    /**
     *
     * @param addr
     * @param requestBody
     * @param doPost
     */
    private void makeRequest(String addr, String requestBody, boolean doPost) {
        createAuthenticator(AlcoAppApi.AUTH_TICKET, "");
        ApiRequestAsyncTask task = new ApiRequestAsyncTask(mHandler, doPost);
        if (doPost) {
            task.setPostArguments(requestBody);
        }
        task.execute(addr, requestBody, doPost);
    }


    private void makeRequestWithoutToken(String addr, String requestBody, boolean doPost) {
        ApiRequestAsyncTask task = new ApiRequestAsyncTask(mHandler, doPost);
        if (doPost) {
            task.setPostArguments(requestBody);
        }
        task.execute(addr, requestBody, doPost);
    }

    public void cartItems() {
        if(notReadyForRequest())
            return;
        makeRequest(ApiKnownUrls.CART_ITEMS, "", false);

    }


    /**
     * Creates an order for current user in backend api db.
     * Handler will receive order id in return
     * @param orderInfo
     */
    public void createOrder(ApiOrder orderInfo) {
        //TODO: pass the order id in order not in static
        if(notReadyForRequest()) return;

        try {
            JSONObject bodyObj = new JSONObject();
            bodyObj.put("order_id", orderInfo.order_id);
            bodyObj.put("lastname", orderInfo.lastname);
            bodyObj.put("name", orderInfo.name);
            bodyObj.put("phone", orderInfo.phone);
            bodyObj.put("email", orderInfo.email);
            bodyObj.put("post_index", orderInfo.post_index);
            bodyObj.put("region", orderInfo.region);
            bodyObj.put("city", orderInfo.city);
            bodyObj.put("street", orderInfo.street);
            bodyObj.put("house", orderInfo.house);
            bodyObj.put("flat", orderInfo.flat);
            bodyObj.put("comment", orderInfo.comment);
            bodyObj.put("building", orderInfo.building);
            bodyObj.put("postal_fee", orderInfo.postalFee);

            makeRequest(ApiKnownUrls.CREATE_ORDER, bodyObj.toString(), true);
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.e("API", "createOrder() caused an exception");
        }
    }


    /**
     * Creates payment on server side and returns confirm url in response
     * @param token yandex payment token
     * @param methodType payment method
     * @param order the order which is payed for
     */
    public void createPayment(String token, PaymentMethodType methodType,
                              ApiOrder order) {
        String paymentMethodName = methodType.name();

        if(notReadyForRequest())
            return;

        JSONObject postData = new JSONObject();

        try {
            postData.put("amount", order.getPrice());
            postData.put("payment_token", token);
            postData.put("payment_type", paymentMethodName);
            postData.put("order_id", order.order_id);
            postData.put("postal_fee", order.postalFee);
        } catch (JSONException e) {
            Log.e("API", "Failed to set POST arguments", e);
        }

        String fullUrl = String.format(ApiKnownUrls.CREATE_PAYMENT, order.order_id);
        makeRequest(fullUrl, postData.toString(), true);
    }


    void changeCartItemQuantity(ApiCartItem item) {
        if(notReadyForRequest())
            return;

        String changeQuantityUrl = String.format(ApiKnownUrls.CHANGE_ITEM_QUANTITY, item.id,
                item.quantityInCart);
        makeRequest(changeQuantityUrl, "", false);
    }

    void removeItemFromCart(ApiCartItem item) {
        if(notReadyForRequest())
            return;

        String removeItemUrl = String.format(ApiKnownUrls.REMOVE_ITEM_FROM_CART, item.id);
        makeRequest(removeItemUrl, "", false);
    }


    /**
     * Sets the status of the order to PAYED
     * @param orderId id of the order
     */
    public void setOrderIsPayed(int orderId) {
        if(notReadyForRequest())
            return;

        JSONObject postData = getBodyObject(orderId, OrderStatuses.PAYED);

        String setOrderStatusUrl = String.format(ApiKnownUrls.SET_ORDER_STATUS, orderId);
        makeRequest(setOrderStatusUrl, postData.toString(), true);
    }

    @NonNull
    private JSONObject getBodyObject(int orderId, OrderStatuses payed) {
        JSONObject postData = new JSONObject();

        try {
            postData.put("order_id", orderId);
            postData.put("new_status", payed.getValue());
        } catch (JSONException e) {
            Log.e("API", "Failed to set POST arguments", e);
        }
        return postData;
    }

    /**
     * Sets status CANCELLED to given order
     * @param orderId id of the order
     */
    public void setOrderIsCancelled(int orderId) {
        if(notReadyForRequest())
            return;

        JSONObject postData = getBodyObject(orderId, OrderStatuses.CANCELLED);

        String setOrderStatusUrl = String.format(ApiKnownUrls.SET_ORDER_STATUS, orderId);
        makeRequest(setOrderStatusUrl, postData.toString(), true);
    }


    /**
     * Loads all user's orders.
     * Calls handler after load
     */
    public void orders() {
        if(notReadyForRequest())
            return;
        makeRequest(ApiKnownUrls.MY_ORDERS, "", false);
    }


    /**
     * Loads all user's orders.
     * Calls handler after load
     */
    public void orderInfo(int orderId) {
        if(notReadyForRequest())
            return;
        String orderInfoUrl = String.format(ApiKnownUrls.ORDER_INFO, orderId);
        makeRequest(orderInfoUrl, "", false);
    }

    public void freeDeliveryFrom() {
        if(notReadyForRequest())
            return;
        makeRequest(ApiKnownUrls.FREE_SHIPPING, "", false);
    }

    public void getMinPrice() {
        if(notReadyForRequest())
            return;
        makeRequest(ApiKnownUrls.MIN_PRICE, "", false);
    }

}
