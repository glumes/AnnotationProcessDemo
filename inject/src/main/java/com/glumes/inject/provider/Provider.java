package com.glumes.inject.provider;

import android.content.Context;
import android.view.View;

/**
 * Created by zhaoying on 2017/3/29.
 */

public interface Provider {

    Context getContext(Object source);

    View getView(Object source,int id) ;
}
