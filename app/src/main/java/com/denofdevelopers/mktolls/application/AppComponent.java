package com.denofdevelopers.mktolls.application;

import com.denofdevelopers.mktolls.di.module.AppModule;
import com.denofdevelopers.mktolls.di.module.DataModule;
import com.denofdevelopers.mktolls.di.module.ApiModule;
import com.denofdevelopers.mktolls.screen.screen.main.MainComponent;
import com.denofdevelopers.mktolls.screen.screen.main.MainModule;
import com.denofdevelopers.mktolls.screen.screen.map.MapActivity;
import com.denofdevelopers.mktolls.screen.screen.result.ResultActivity;
import com.denofdevelopers.mktolls.screen.screen.splash.SplashScreenActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {
                AppModule.class,
                ApiModule.class,
                DataModule.class
        }
)
public interface AppComponent {

    void plus(MapActivity mapActivity);

    void plus(SplashScreenActivity splashScreenActivity);

    void plus(ResultActivity resultActivity);

    MainComponent plus(MainModule mainModule);

}
