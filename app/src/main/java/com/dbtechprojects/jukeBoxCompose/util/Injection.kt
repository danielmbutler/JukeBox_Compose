package com.dbtechprojects.jukeBoxCompose.util

import com.dbtechprojects.jukeBoxCompose.AlbumRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object Injection {

    @Provides
    fun provideRepository(): AlbumRepository { return AlbumRepository() }
}