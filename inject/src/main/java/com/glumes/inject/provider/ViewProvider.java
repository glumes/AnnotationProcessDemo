package com.glumes.inject.provider;

import android.content.Context;
import android.view.View;

/**
 * Created by zhaoying on 2017/3/29.
 */

public class ViewProvider implements Provider {

    @Override
    public Context getContext(Object source) {
        return ((View)source).getContext() ;
    }

    @Override
    public View getView(Object source, int id) {
        return ((View)source).findViewById(id) ;
    }

}
