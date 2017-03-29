package com.glumes.inject;

import android.app.Activity;
import android.view.View;

import com.glumes.inject.provider.ActivityProvider;
import com.glumes.inject.provider.Provider;
import com.glumes.inject.provider.ViewProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaoying on 2017/3/29.
 */

/**
 * 注入实现的 API 类
 * 大概原理为：从 apt 生成的类中得到对应的注入类
 * 然后反射获取注入类并缓存，通过注入类实现注入。
 *
 * 而注入类则实现 Injector 接口，通过从 Provider 中得到的 View 实现注入
 */
public class Inject  {

    private static final ActivityProvider activityProvider = new ActivityProvider();
    private static final ViewProvider viewProvider = new ViewProvider() ;

    private static final Map<String,Injector> InjectorMap = new HashMap<>() ;

    public static void inject(Activity activity) {
        inject(activity,activity,activityProvider);
    }

    public static void inject(View view){
        inject(view,view,viewProvider) ;
    }


    public static void inject(Object host, Object source, Provider provider){
        String className = host.getClass().getName() ;
        try {
            Injector injector = InjectorMap.get(className) ;
            if (injector == null){

                // 通过反射来构造类，并缓存起来
                Class<?> injectorClass = Class.forName(className + "$$Finder") ;
                injector = (Injector) injectorClass.newInstance();
                InjectorMap.put(className,injector) ;
            }
            injector.inject(host,source,provider);
        }catch (Exception e){
            throw new RuntimeException("can't  inject for " + className ) ;
        }
    }
}
