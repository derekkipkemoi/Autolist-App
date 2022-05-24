package org.carlistingapp.autolist

import android.app.Application
import org.carlistingapp.autolist.data.network.ListingCarsAPI
import org.carlistingapp.autolist.data.network.NetworkConnectionInterceptor
import org.carlistingapp.autolist.data.repositories.UserRepository
import org.carlistingapp.autolist.data.repositories.AuthRepository
import org.carlistingapp.autolist.ui.auth.viewModel.AuthViewModelFactory
import org.carlistingapp.autolist.ui.home.listCar.viewModels.ListCarViewModelFactory
import org.carlistingapp.autolist.ui.home.postCar.viewModels.PostCarViewModelFactory
import org.carlistingapp.autolist.ui.home.profile.viewModel.UserViewModelFactory
import org.carlistingapp.autolist.utils.ImageResizer
import org.carlistingapp.autolist.utils.Session
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

class MotiApplication : Application() , KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@MotiApplication))
        bind() from singleton { NetworkConnectionInterceptor(instance()) }
        bind() from singleton { Session(instance()) }
        bind() from singleton { ImageResizer() }
        bind() from singleton { ListingCarsAPI(instance()) }
        bind() from singleton { AuthRepository(instance()) }
        bind() from singleton { UserRepository(instance()) }
        bind() from singleton { UserViewModelFactory(instance()) }
        bind() from singleton { PostCarViewModelFactory(instance()) }
        bind() from singleton { ListCarViewModelFactory(instance()) }
        bind() from singleton { AuthViewModelFactory(instance()) }
    }
}