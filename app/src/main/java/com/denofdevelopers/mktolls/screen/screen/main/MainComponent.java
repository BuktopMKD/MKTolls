package com.denofdevelopers.mktolls.screen.screen.main;

import com.denofdevelopers.mktolls.di.ActivityScope;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(
        modules = MainModule.class
)
public interface MainComponent {
    MainActivity inject(MainActivity mainActivity);
}
