package simo.com.alco.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.common.api.Api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.List;

import simo.com.alco.MainActivity;
import simo.com.alco.R;
import simo.com.alco.fragments.events.OnPreloadDoneListener;

public class ApiDataManager {

    public static List<ApiProcess> PROCESSES = new ArrayList<>();
    public static List<ApiCategory> CATEGORIES;
    public static List<ApiSubcategory> SUBCATEGORIES;
    public static List<ApiItem> ITEMS = new ArrayList<>();
    public static OnPreloadDoneListener preloadDoneCallback;
    public static MainActivity contextActivity;
    public static double FREE_DELIVERY_FROM;
    public static double MIN_PRICE;


    private ApiDataManager() {

    }


    public static void preloadShopData() {
        loadProcesses();
    }

    /**
     *
     */
    public static void loginUser(ApiUser user, final ApiCallback callback) {
        AlcoAppApi.user = user;

        ApiBaseHandler apiHandler = new ApiBaseHandler() {
            @Override
            public void onResponse(JSONObject arg) {
                try {
                    boolean result = false;

                    if(arg.has("token")) {
                        AlcoAppApi.AUTH_TICKET = arg.getString("token");
                        int isAdmin = arg.getInt("is_admin");

                        ApiUser.isAdmin = isAdmin == 1;
                        AlcoAppApi.user.username = arg.getString("username");
                        SharedPreferences sharedPref = contextActivity.getPreferences(Context.MODE_PRIVATE);
                        sharedPref.edit().putString(contextActivity.getString(R.string.usertoken), AlcoAppApi.AUTH_TICKET).apply();
                        sharedPref.edit().putInt("is_admin", isAdmin).apply();
                        sharedPref.edit().putString("username", arg.getString("username")).apply();

                        Authenticator.setDefault(new Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(AlcoAppApi.AUTH_TICKET, new char[] {});
                            }
                        });

                        result = true;
                    }
                    callback.onEvent(result);
                } catch (JSONException e) {
                    Log.e("API","Login failed");
                    e.printStackTrace();
                    callback.onEvent(false);
                }
            }
        };
        new AlcoAppApi(apiHandler).loginUser();
    }


    /**
     * New user registration
     */
    public static void registerUser(ApiUser user, final ApiCallback callback) {
        AlcoAppApi.user = user;

        // define response handler first
        ApiBaseHandler regResultHandler = new ApiBaseHandler() {
            @Override
            public void onResponse(JSONObject arg) {
                try {

                    if(arg.has("result") && arg.getString("result").equals("ok")) {
                        // here we should log user in.
                        ApiBaseHandler loginResultHandler = new ApiBaseHandler() {
                            @Override
                            public void onResponse(JSONObject arg) {
                                boolean result = false;

                                if(arg.has("token")) {
                                    try {
                                        AlcoAppApi.AUTH_TICKET = arg.getString("token");
                                        AlcoAppApi.user = new ApiUser();
                                        AlcoAppApi.user.username = arg.getString("username");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    result = true;

                                }
                                callback.onEvent(result);
                            }
                        };
                        new AlcoAppApi(loginResultHandler).loginUser();
                    }
                } catch (JSONException e) {
                    Log.e("API","Login failed");
                    e.printStackTrace();
                }
            }
        };
        new AlcoAppApi(regResultHandler).registerUser(user);
    }

    public static void loadProcesses() {
        ApiBaseHandler pHandler = new ApiBaseHandler() {
            @Override
            public void onResponse(JSONObject arg) {
                try {
                    JSONArray processes = arg.getJSONArray("data");
                    List<ApiProcess> processesList = new ArrayList<>();

                    for (int i = 0; i < processes.length(); i++) {
                        JSONObject currentProcess = processes.getJSONObject(i);
                        ApiProcess x = new ApiProcess(currentProcess.getInt("id"),
                                currentProcess.getString("name"));
                        processesList.add(x);
                    }
                    PROCESSES = processesList;

                    // init categories
                    loadCategories();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        new AlcoAppApi(pHandler).getProcesses();
    }


    /**
     * Loads all categories in cache.
     * After loading inits subcategory cache load
     *
     */
    public static void loadCategories() {
        ApiBaseHandler pHandler = new ApiBaseHandler() {
            @Override
            public void onResponse(JSONObject arg) {
                try {
                    JSONArray jsonCategories = arg.getJSONArray("data");
                    List<ApiCategory> categoryList = new ArrayList<>();

                    for (int i = 0; i < jsonCategories.length(); i++) {
                        JSONObject currentCategory = jsonCategories.getJSONObject(i);
                        int categoryId = currentCategory.getInt("id");
                        int processId = currentCategory.getInt("process_id");
                        String categoryName = currentCategory.getString("name");

                        ApiCategory x = new ApiCategory();
                        x.id = categoryId;
                        x.process_id = processId;
                        x.name = categoryName;
                        categoryList.add(x);
                    }

                    Log.d("API", String.format("Got %d categories", categoryList.size()));
                    CATEGORIES = categoryList;

                    // init sub categories
                    loadSubcategories();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        new AlcoAppApi(pHandler).getCategories();
    }


    /**
     * Loads all subcategories in cache.
     * After loading inits items cache load
     *
     */
    public static void loadSubcategories() {
        ApiBaseHandler pHandler = new ApiBaseHandler() {
            @Override
            public void onResponse(JSONObject arg) {
                try {
                    Log.d("API", "Subcategories:");
                    JSONArray jsonData = arg.getJSONArray("data");
                    List<ApiSubcategory> subcategoryList = new ArrayList<>();

                    for (int i = 0; i < jsonData.length(); i++) {

                        JSONObject currentSubcategory = jsonData.getJSONObject(i);

                        int categoryId = currentSubcategory.getInt("category_id");
                        int processId = currentSubcategory.getInt("process_id");
                        String categoryName = currentSubcategory.getString("category_name");
                        int subcategoryId = currentSubcategory.getInt("id");
                        String subcategoryName = currentSubcategory.getString("name");


                        ApiSubcategory x = new ApiSubcategory();
                        x.id = subcategoryId;
                        x.processId = processId;
                        x.name = subcategoryName;
                        x.categoryId = categoryId;
                        x.categoryName = categoryName;

                        subcategoryList.add(x);
                    }
                    Log.d("API", String.format("Got %d subcategories", subcategoryList.size()));
                    SUBCATEGORIES = subcategoryList;

                    if (ApiDataManager.preloadDoneCallback != null)
                        ApiDataManager.preloadDoneCallback.preloadDone();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        new AlcoAppApi(pHandler).getSubcategories();
    }


    public static void loadItems() {
        ApiBaseHandler pHandler = new ApiBaseHandler() {
            @Override
            public void onResponse(JSONObject arg) {
                try {
                    Log.d("API", "Items:");
                    JSONArray jsonData = arg.getJSONArray("data");
                    List<ApiItem> itemsList = new ArrayList<>();

                    for (int i = 0; i < jsonData.length(); i++) {

                        JSONObject currentItem = jsonData.getJSONObject(i);
                        Log.d("API",currentItem.toString(4));
                        int categoryId = currentItem.getInt("category_id");
                        int processId = currentItem.getInt("process_id");
                        int subcategoryId = currentItem.getInt("subcategory_id");
                        int itemId = currentItem.getInt("id");
                        String itemName = currentItem.getString("title");


                        ApiItem x = new ApiItem();
                        x.id = itemId;
                        x.process = getProcessById(processId);
                        x.category = getCategoryById(categoryId);
                        x.subcategory = getSubcategoryById(subcategoryId);
                        x.title = itemName;
                        x.price = currentItem.getString("price");
                        x.inStock = currentItem.getInt("in_stock");
                        x.desc = currentItem.getString("desc");

                        JSONArray imgs = currentItem.getJSONArray("images_paths");
                        List<String> images = new ArrayList<>();

                        for (int j = 0; j < imgs.length(); j++) {
                            String currentImg = ApiKnownUrls.BASE_URL + "/" +
                                    imgs.getString(j);
                            images.add(currentImg);

                        }
                        x.imagePaths = images.toArray(new String[0]);

                        itemsList.add(x);
                    }

                    ITEMS = itemsList;

                    if (ApiDataManager.preloadDoneCallback != null)
                        ApiDataManager.preloadDoneCallback.preloadDone();
                    //loadItems();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        new AlcoAppApi(pHandler).getItems();
    }

    public static ApiProcess getProcessById(int id) {
        ApiProcess result = null;
        for (ApiProcess c: PROCESSES) {
            if(c.id == id) {
                result = c;
                break;
            }
        }
        return result;
    }

    public static ApiCategory getCategoryById(int id) {
        ApiCategory result = null;
        for (ApiCategory c: CATEGORIES) {
            if(c.id == id) {
                result = c;
                break;
            }
        }
        return result;
    }

    public static ApiSubcategory getSubcategoryById(int id) {
        ApiSubcategory result = null;
        for (ApiSubcategory c: SUBCATEGORIES) {
            if(c.id == id) {
                result = c;
                break;
            }
        }
        return result;
    }

    public static List<ApiCategory> categoriesByProcess(ApiProcess process) {
        List<ApiCategory> result = new ArrayList<>();

        for (ApiCategory c: CATEGORIES) {
            if(c.process_id == process.id) {
                result.add(c);
            }
        }

        return result;
    }

    public static List<ApiSubcategory> subcategoriesByCategory(ApiCategory category) {
        List<ApiSubcategory> result = new ArrayList<>();

        for (ApiSubcategory c: SUBCATEGORIES) {
            if(c.categoryId == category.id) {
                result.add(c);
            }
        }

        return result;
    }

    public static void itemsBySubcategory(ApiSubcategory subcategory, ApiBaseHandler pHandler) {
        new AlcoAppApi(pHandler).getItemsBySubcategory(subcategory);
    }


    public static void userCartItemsCount(ApiBaseHandler pHandler) {
        new AlcoAppApi(pHandler).cartItemsCount();
    }

    public static void addItemToUserCart(ApiCartItem item, ApiBaseHandler handler) {
        new AlcoAppApi(handler).addToCart(item);
    }

    public static void changeItemQuantity(ApiCartItem item, ApiBaseHandler handler) {
        new AlcoAppApi(handler).changeCartItemQuantity(item);
    }

    public static void removeItemFromCart(ApiCartItem item, ApiBaseHandler handler) {
        new AlcoAppApi(handler).removeItemFromCart(item);
    }


    public static void cartItems() {
        ApiBaseHandler pHandler = new ApiBaseHandler() {
            @Override
            public void onResponse(JSONObject arg) {
                try {
                    JSONObject jsonObjData = arg.getJSONObject("data");
                    JSONArray jsonData = jsonObjData.getJSONArray("items");
                    final ApiOrder theOrder = new ApiOrder();

                    if(jsonData.length() > 0) {
                        theOrder.order_id = jsonObjData.getInt("order_id");

                        for (int i = 0; i < jsonData.length(); i++) {
                            JSONObject currentItem = jsonData.getJSONObject(i);
                            int itemId = currentItem.getInt("id");
                            String itemName = currentItem.getString("title");

                            ApiCartItem x = new ApiCartItem();

                            x.id = itemId;
                            x.title = itemName;
                            x.inStock = currentItem.getInt("in_stock");
                            x.quantityInCart = currentItem.getInt("quantity");
                            x.price = currentItem.getString("price");
                            x.weight = currentItem.getString("weight");

                            JSONArray imgs = currentItem.getJSONArray("image_paths");

                            List<String> images = new ArrayList<>();

                            for (int j = 0; j < imgs.length(); j++) {
                                String currentImg = ApiKnownUrls.BASE_URL + "/" +
                                        imgs.getString(j);
                                images.add(currentImg);
                            }

                            x.imagePaths = images.toArray(new String[0]);
                            theOrder.items.add(x);
                        }

                    }

                    if (ApiDataManager.preloadDoneCallback != null)
                        ApiDataManager.preloadDoneCallback.preloadCartOrderDone(theOrder);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("API", "There was an error during json response procesing: /cart");
                }
            }
        };
        new AlcoAppApi(pHandler).cartItems();
    }

    public static void orderInfo(int orderId) {
        new AlcoAppApi(new ApiBaseHandler() {
            @Override
            public void onResponse(JSONObject arg) {
                try {
                    JSONObject jsonObjData = arg.getJSONObject("data");
                    ApiOrder order = new ApiOrder();
                    order.building = jsonObjData.getString("building");
                    order.phone = jsonObjData.getString("phone");
                    order.order_id = jsonObjData.getInt("order_id");
                    order.postalFee = jsonObjData.getDouble("postal_fee");
                    order.city = jsonObjData.getString("city");
                    order.email = jsonObjData.getString("email");
                    order.flat = jsonObjData.getString("flat");
                    order.house = jsonObjData.getString("house");
                    order.name = jsonObjData.getString("name");
                    order.lastname = jsonObjData.getString("lastname");
                    order.post_index = jsonObjData.getString("post_index");
                    order.region = jsonObjData.getString("region");
                    order.street = jsonObjData.getString("street");
                    order.status = OrderStatuses.fromValue(jsonObjData.getInt("status"));

                    JSONArray items = jsonObjData.getJSONArray("items");

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject jsonItem = items.getJSONObject(i);
                        ApiCartItem apiItem = ApiCartItem.fromJSONObject(jsonItem);
                        order.items.add(apiItem);
                    }

                    if (ApiDataManager.preloadDoneCallback != null)
                        ApiDataManager.preloadDoneCallback.preloadCartOrderDone(order);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).orderInfo(orderId);
    }

    public static void getFreeDeliveryFrom(ApiBaseHandler handler) {
        new AlcoAppApi(handler).freeDeliveryFrom();
    }
    public static void getMinOrderPrice(ApiBaseHandler handler) {
        new AlcoAppApi(handler).getMinPrice();
    }
}
