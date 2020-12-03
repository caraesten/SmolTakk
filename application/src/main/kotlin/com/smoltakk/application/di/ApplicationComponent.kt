package com.smoltakk.application.di

import com.smoltakk.repositories.di.RepositoriesComponent
import dagger.Component
import com.smoltakk.application.web.Router

@ApplicationSingleton
@Component(
    dependencies = [RepositoriesComponent::class],
    modules = [ControllersModule::class, RouterModule::class])
interface ApplicationComponent {
    fun router(): Router

    @Component.Builder
    interface Builder {
        fun setRepositoriesComponent(repositoriesComponent: RepositoriesComponent): Builder
        fun build(): ApplicationComponent
    }
}