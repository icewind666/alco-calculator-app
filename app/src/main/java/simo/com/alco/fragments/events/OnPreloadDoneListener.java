package simo.com.alco.fragments.events;

import android.graphics.Bitmap;

import simo.com.alco.api.ApiOrder;

public interface OnPreloadDoneListener {
    void preloadDone();
    void preloadCartOrderDone(ApiOrder order);
    void preloadImagesDone(Bitmap[] bitmaps);
}
