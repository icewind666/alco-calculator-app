package simo.com.alco.fragments.events;

import simo.com.alco.api.ApiCategory;
import simo.com.alco.api.ApiItem;
import simo.com.alco.api.ApiOrder;
import simo.com.alco.api.ApiProcess;
import simo.com.alco.api.ApiSubcategory;

public interface OnListFragmentItemSelection {
    void onSubcategoryListFragmentInteraction(ApiSubcategory item);
    void onCategoryListFragmentInteraction(ApiCategory item);
    void onProcessListFragmentInteraction(ApiProcess item);
    void onShopItemsListFragmentInteraction(ApiItem item);
    void onMyOrderItemClick(ApiOrder order);
}
