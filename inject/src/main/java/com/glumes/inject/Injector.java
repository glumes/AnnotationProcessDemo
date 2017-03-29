package com.glumes.inject;

import com.glumes.inject.provider.Provider;

/**
 * Created by zhaoying on 2017/3/29.
 */

public interface Injector<T> {

    /**
     *
     * @param host
     * @param source
     * @param provider
     */
    void inject(T host, Object source, Provider provider) ;
}
