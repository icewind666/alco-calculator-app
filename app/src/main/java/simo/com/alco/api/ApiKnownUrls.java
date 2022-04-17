package simo.com.alco.api;

public class ApiKnownUrls {
    //public static String BASE_URL = "https://icewind.online";
    public static String BASE_URL = "https://samogon-calculator.ru";

    //local debug url
    //public static String BASE_URL = "http://10.0.2.2:5002";
    public static boolean IS_DEBUG_RUN = true;


    public static String LOGIN_URL = BASE_URL + "/api/token";
    public static String PROCESSES_URL = BASE_URL + "/api/processes";
    public static String CATEGORIES_URL = BASE_URL + "/api/categories";
    public static String ITEMS_URL = BASE_URL + "/api/items";
    public static String ITEMS_BY_SUBCAT_URL = BASE_URL + "/api/subcategory/%s/items";
    public static String SUBCATEGORIES_URL = BASE_URL + "/api/subcategories";
    public static String CATEGORIES_BY_PROCESS_URL = BASE_URL + "/api/processes/%s/categories";
    public static String SUBCATEGORIES_BY_CATEGORY_URL = BASE_URL + "/api/categories/%s/subcategories";
    public static String ADD_TO_CART = BASE_URL + "/api/cart/add/%s";
    public static String CART_COUNT = BASE_URL + "/api/cart/count";
    public static String CART_ITEMS = BASE_URL + "/api/cart";
    public static String CREATE_PAYMENT = BASE_URL + "/api/order/%s/create_payment";
    public static String CREATE_ORDER = BASE_URL + "/api/cart/order";
    public static String SET_ORDER_STATUS = BASE_URL + "/api/order/statuschange";
    public static String RETURN_URL = BASE_URL + "/api/return_url";
    public static String REGISTER_URL = BASE_URL + "/api/register_user";
    public static String CHANGE_ITEM_QUANTITY = BASE_URL + "/api/cart/%s/change_item_quantity_to/%s";
    public static String REMOVE_ITEM_FROM_CART = BASE_URL + "/api/cart/remove/%s";
    public static String MY_ORDERS = BASE_URL + "/api/my_orders";
    public static String ORDER_INFO = BASE_URL + "/api/order_info/%s";
    public static String FREE_SHIPPING = BASE_URL + "/api/free_shipping_price";
    public static String MIN_PRICE = BASE_URL + "/api/min_price";


}
