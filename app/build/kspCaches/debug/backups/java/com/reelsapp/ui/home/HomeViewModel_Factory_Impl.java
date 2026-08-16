package com.reelsapp.ui.home;

import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class HomeViewModel_Factory_Impl implements HomeViewModel.Factory {
  private final HomeViewModel_Factory delegateFactory;

  HomeViewModel_Factory_Impl(HomeViewModel_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public HomeViewModel create(HomeState state) {
    return delegateFactory.get(state);
  }

  public static Provider<HomeViewModel.Factory> create(HomeViewModel_Factory delegateFactory) {
    return InstanceFactory.create(new HomeViewModel_Factory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<HomeViewModel.Factory> createFactoryProvider(
      HomeViewModel_Factory delegateFactory) {
    return InstanceFactory.create(new HomeViewModel_Factory_Impl(delegateFactory));
  }
}
