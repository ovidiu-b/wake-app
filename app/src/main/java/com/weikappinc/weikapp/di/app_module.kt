package com.weikappinc.weikapp.di

import androidx.room.Room
import com.weikappinc.weikapp.data.AppDatabase
import com.weikappinc.weikapp.data.DATABASE_NAME
import com.weikappinc.weikapp.presenters.SingleAlarmPresenter
import com.weikappinc.weikapp.repositories.AlarmRepository
import com.weikappinc.weikapp.view_models.audio_chooser_activity.AudioChooserActivityViewModel
import com.weikappinc.weikapp.view_models.audio_chooser_activity.AudioFileRecolector
import com.weikappinc.weikapp.view_models.main_activity.MainActivityViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModules = module {

    // MainActivityViewModel
    viewModel { MainActivityViewModel(get()) }

    // AlarmRepository
    single(createdAtStart = true) { AlarmRepository(get()) }

    // AlarmDao
    single { get<AppDatabase>().alarmDao() }

    // CurrentUserDao
    single { get<AppDatabase>().currentUserData() }

    // Room Database Instance
    single { Room.databaseBuilder(androidContext(), AppDatabase::class.java, DATABASE_NAME).build() }

    // SigleAlarmPresenter
    factory { SingleAlarmPresenter(get()) }

    // AudioChooserActivityViewModel
    viewModel { AudioChooserActivityViewModel(get()) }

    // AudioFileRecolector
    single { AudioFileRecolector(androidContext()) }
}
