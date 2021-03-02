package com.fitbit.api.loaders;

import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;

import com.fitbit.authentication.Scope;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ResourceLoaderFactory<T> {

    public static String NextAPIURL = null;
    private String urlFormat;
    private Class<T> classType;
    private int limit = 5;
    private int offset = 0;
    public static String afterDate = null;
    public static String beforeDate = null;

    public ResourceLoaderFactory(String urlFormat, Class<T> classType) {
        this.urlFormat = urlFormat;
        this.classType = classType;
    }

    public ResourceLoader<T> newResourceLoader(Activity contextActivity, Scope[] requiredScopes, String... pathParams) {

        String url = urlFormat;
        if(afterDate == null){
            afterDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        }
        if(beforeDate == null){
            Date dt = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(dt);
            c.add(Calendar.DATE, 1);
            dt = c.getTime();
            beforeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(dt);
        }
        if (pathParams != null && pathParams.length > 0) {
            url = String.format(urlFormat, pathParams);
        }

        url = url.concat("?afterDate="+afterDate+"&limit="+limit+"&sort=asc&offset="+offset);
        //url = url.concat("?limit="+limit+"&sort=asc&offset="+offset);

        if(!TextUtils.isEmpty(NextAPIURL)){
            return new ResourceLoader<T>(contextActivity, NextAPIURL, requiredScopes, new Handler(), classType);
        }
        else {
            return new ResourceLoader<T>(contextActivity, url, requiredScopes, new Handler(), classType);
        }
    }
}
