package com.glumes.inject.provider;

import android.app.Activity;
import android.content.Context;
import android.view.View;

/**
 * Created by zhaoying on 2017/3/29.
 */

public class ActivityProvider implements Provider{

    @Override
    public Context getContext(Object source) {
        return ((Activity)source) ;
    }

    @Override
    public View getView(Object source, int id) {
        return ((Activity)source).findViewById(id);
    }
}
