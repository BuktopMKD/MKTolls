package com.denofdevelopers.mktolls.application;

import android.app.Application;
import android.content.Context;

import com.denofdevelopers.mktolls.R;
import com.denofdevelopers.mktolls.di.module.AppModule;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;


public class App extends Application {


    private AppComponent appComponent;

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initAppComponent();
        //initLeakCanary();
        initCalligraphyConfig();
    }

//    private void initLeakCanary() {
//        LeakCanary.install(this);
//    }

    private void initAppComponent() {
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    private void initCalligraphyConfig() {
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Montserrat-Regular.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
