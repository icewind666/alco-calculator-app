package simo.com.alco.api;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * Shop items entity
 */
public class ApiItem {
    public int id;
    public String title;
    public String desc;
    public String specs;
    public String price;
    public ApiSeller seller;
    public String[] imagePaths;
    public Bitmap[] imageBitmaps;
    public ApiCategory category;
    public ApiSubcategory subcategory;
    public ApiProcess process;
    public String shippingGeo;
    public String shippingComment;
    public ApiShipmentMethod[] shipmentMethods;

    public ApiPaymentMethod[] paymentMethods;
    public String paymentComment;

    public String selfPickup;

    public int inStock;
    public String weight;

    public double price() {
        try {
            return Double.parseDouble(this.price);
        }
        catch (NumberFormatException ex) {
            // item is invalid. skipping
            Log.d("API", "Invalid item price detected. Item skipped. Price discarded");
        }
        return 0;
    }

    double weight() {
        try {
            return Double.parseDouble(this.weight);
        }
        catch (NumberFormatException ex) {
            // item is invalid. skipping
            Log.d("API", "Invalid item weight detected. Item skipped. Price discarded");
        }
        return 0;
    }

}
