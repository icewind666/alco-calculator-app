package simo.com.alco.api;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ApiOrder {
    public List<ApiCartItem> items;
    public int order_id;
    public OrderStatuses status;
    public String statusTitle;
    public String name;
    public String lastname;
    public String region;
    public String city;
    public String street;
    public String house;
    public String post_index;
    public String email;
    public String phone;
    public String building;
    public String flat;
    public String comment;
    public double postalFee;
    public Date createdDate;

    public ApiOrder() {
        items = new ArrayList<>();
        createdDate = new Date();
    }

    /**
     * Weight of all items in order
     * @return
     */
    public double getWeight() {
        double overallWeight = 0;
        for (ApiItem i :this.items) {
            overallWeight += i.weight();
        }
        return overallWeight;
    }

    public double getPrice() {
        double overallPrice = 0;
        for (ApiCartItem i :this.items) {
            overallPrice += i.price() * i.quantityInCart;
        }
        return overallPrice;
    }

    @Override
    public String toString() {
        return String.format("Заказ №%s", this.order_id);
    }

}
