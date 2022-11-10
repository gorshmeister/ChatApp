package ru.gorshenev.themesstyles.di.module

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import ru.gorshenev.themesstyles.data.database.AppDataBase
import ru.gorshenev.themesstyles.di.component.*
import javax.inject.Singleton

@Module(
    includes = [NetworkModule::class],
    subcomponents = [StreamComponent::class, ChatComponent::class, PeopleComponent::class,ProfileComponent::class]
)
class AppModule {

    @Singleton
    @Provides
    fun provideMyDB(context: Context): AppDataBase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDataBase::class.java,
            AppDataBase.DB_NAME,
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideMyScheduler(): Scheduler {
        return Schedulers.io()
    }
}