package simo.com.alco.fragments.events;

import simo.com.alco.api.ApiCartItem;
import simo.com.alco.api.ApiOrder;

public interface OnCartOrderAction {
    void OnCartOrder(ApiOrder theCartOrder);
    void OnChangeCartItem(ApiCartItem itemToChange);
}
