package com.jast.haarsalon.core.di;

import android.app.Application;

import com.jast.haarsalon.core.share_prefer.SharedPrefManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    @Provides
    @Singleton
    public SharedPrefManager provideSharedPrefManager(Application application) {
        return new SharedPrefManager(application);
    }
}