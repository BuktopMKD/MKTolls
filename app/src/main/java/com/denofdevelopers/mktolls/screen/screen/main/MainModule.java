package com.denofdevelopers.mktolls.screen.screen.main;

import com.denofdevelopers.mktolls.di.ActivityScope;
import com.denofdevelopers.mktolls.network.ApiService;

import dagger.Module;
import dagger.Provides;

@Module
public class MainModule {

    private MainActivity mainActivity;

    public MainModule(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Provides
    @ActivityScope
    MainActivity provideMainActivity() {
        return mainActivity;
    }

    @Provides
    @ActivityScope
    MainPresenter
    provideMainPresenter(ApiService apiService) {
        return new MainPresenter(apiService, mainActivity);
    }
}
