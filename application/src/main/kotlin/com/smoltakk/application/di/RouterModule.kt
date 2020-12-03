package com.smoltakk.application.di

import com.smoltakk.application.controllers.*
import dagger.Module
import dagger.Provides
import com.smoltakk.application.web.Router

@Module
class RouterModule {
    @Provides
    fun provideRouter(
        homePageController: HomePageController,
        messagesController: MessagesController,
        profileController: ProfileController,
        loginController: LoginController,
        adminController: AdminController) = Router(homePageController, messagesController, profileController, loginController, adminController)
}