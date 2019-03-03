package com.beekay.thoughts.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.CircularProgressDrawable;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

@GlideModule
public class MyGlide extends AppGlideModule {

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        CircularProgressDrawable placeHolder = new CircularProgressDrawable(context);
        placeHolder.setStrokeWidth(5f);
        placeHolder.setCenterRadius(30f);
        placeHolder.start();
        builder.setDefaultRequestOptions(new RequestOptions().placeholder(placeHolder).fitCenter().diskCacheStrategy(DiskCacheStrategy.DATA));
    }
}
