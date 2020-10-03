package com.armjld.gymzo.language;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

public class Gymzo extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleManager.setLocale(this);
    }
}