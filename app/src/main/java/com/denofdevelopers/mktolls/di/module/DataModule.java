package com.denofdevelopers.mktolls.di.module;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.denofdevelopers.mktolls.di.NamedValues;
import com.denofdevelopers.mktolls.model.Toll;
import com.f2prateek.rx.preferences2.Preference;
import com.f2prateek.rx.preferences2.RxSharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    RxSharedPreferences provideRxSharedPreferences(SharedPreferences sharedPreferences) {
        return RxSharedPreferences.create(sharedPreferences);
    }

    @Provides
    @Singleton
    @Named(NamedValues.tolls)
    Preference<String> provideTolls(RxSharedPreferences rxSharedPreferences) {
        return rxSharedPreferences.getString("tolls", "");
    }

    @Provides
    @Singleton
    @Named(NamedValues.tollList)
    List<Toll> provideTollList(@Named(NamedValues.tolls) Preference<String> tolls, Gson gson) {
        String json = tolls.get();
        if (json.isEmpty()) {
            return new ArrayList<>();
        }
        List<Toll> tollList = gson.fromJson(json, new TypeToken<List<Toll>>() {
        }.getType());
        return tollList != null ? tollList : new ArrayList<>();
    }
}
