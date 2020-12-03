package com.smoltakk.application.di

import com.smoltakk.application.controllers.*
import dagger.Binds
import dagger.Module

@Module
interface ControllersModule {
    @Binds
    fun bindAdminController(adminControllerImpl: AdminControllerImpl): AdminController
    @Binds
    fun bindHomePageController(homePageControllerImpl: HomePageControllerImpl): HomePageController
    @Binds
    fun bindLoginController(loginControllerImpl: LoginControllerImpl): LoginController
    @Binds
    fun bindMessagesController(messagesControllerImpl: MessagesControllerImpl): MessagesController
    @Binds
    fun bindProfileController(profileControllerImpl: ProfileControllerImpl): ProfileController
}