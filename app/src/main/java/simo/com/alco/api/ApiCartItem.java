package simo.com.alco.api;

import org.json.JSONException;
import org.json.JSONObject;

public class ApiCartItem extends ApiItem {
    public int quantityInCart;
    public int orderId;

    public ApiCartItem() {

    }

    public ApiCartItem(ApiItem item) {
        this.category = item.category;
        this.desc = item.desc;
        this.id = item.id;
        this.imageBitmaps = item.imageBitmaps;
        this.imagePaths = item.imagePaths;
        this.inStock = item.inStock;
        this.paymentComment = item.paymentComment;
        this.paymentMethods = item.paymentMethods;
        this.price = item.price;
        this.process = item.process;
        this.selfPickup = item.selfPickup;
        this.seller = item.seller;
        this.shipmentMethods = item.shipmentMethods;
        this.shippingComment = item.shippingComment;
        this.shippingGeo = item.shippingGeo;
        this.specs = item.specs;
        this.subcategory = item.subcategory;
        this.title = item.title;
        this.weight = item.weight;
    }

    public static ApiCartItem fromJSONObject(JSONObject obj) {
        if (obj == null)
            return null;

        ApiCartItem newItem = new ApiCartItem();

        try {
            newItem.quantityInCart = obj.getInt("quantity");
            newItem.orderId = obj.getInt("order_id");
            newItem.id = obj.getInt("item_id");
            newItem.price = String.valueOf(obj.getDouble("price"));
            newItem.title = obj.getString("title");
            newItem.weight = obj.getString("weight");
            return newItem;
        }
        catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }

    }
}
