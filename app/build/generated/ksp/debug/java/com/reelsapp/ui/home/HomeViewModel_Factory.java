package com.reelsapp.ui.home;

import dagger.internal.DaggerGenerated;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class HomeViewModel_Factory {
  public HomeViewModel get(HomeState initialState) {
    return newInstance(initialState);
  }

  public static HomeViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static HomeViewModel newInstance(HomeState initialState) {
    return new HomeViewModel(initialState);
  }

  private static final class InstanceHolder {
    static final HomeViewModel_Factory INSTANCE = new HomeViewModel_Factory();
  }
}
