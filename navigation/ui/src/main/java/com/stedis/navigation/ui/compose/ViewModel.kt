package com.stedis.navigation.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
inline fun <reified VM : ViewModel> ViewModel(factory: ViewModelFactory? = null): VM {
    val navigationViewModel = getNavigationViewModel()
    val viewModelFactory = factory ?: getViewModelFactory()
    return viewModel(
        viewModelStoreOwner = navigationViewModel.getViewModelStoreOwner(
            rememberCurrentDestination().toString()
        )
    ) {
        viewModelFactory.create(VM::class.java)
    }
}

@Composable
@PublishedApi
internal fun getNavigationViewModel(): NavigationViewModel =
    checkNotNull(LocalNavigationViewModel.current) {
        "No NavigationViewModel was provided via LocalNavigationViewModel"
    }

@PublishedApi
internal object LocalNavigationViewModel {

    private val LocalNavigationViewModel =
        compositionLocalOf<NavigationViewModel?> { null }

    public val current: NavigationViewModel?
        @Composable
        get() = LocalNavigationViewModel.current

    public infix fun provides(navigationViewModel: NavigationViewModel):
            ProvidedValue<NavigationViewModel?> {
        return LocalNavigationViewModel.provides(navigationViewModel)
    }
}

@PublishedApi
internal class NavigationViewModel : ViewModel() {

    val resultManager = ResultManager()
    private val viewModelStoreOwners = mutableMapOf<String, NavigationViewModelStoreOwner>()

    public fun getViewModelStoreOwner(key: String): ViewModelStoreOwner {
        val viewModelStoreOwner = viewModelStoreOwners[key]
        if (viewModelStoreOwner != null) {
            return viewModelStoreOwner
        } else {
            val newViewModelStoreOwner = NavigationViewModelStoreOwner()
            viewModelStoreOwners[key] = newViewModelStoreOwner
            return newViewModelStoreOwner
        }
    }

    public fun remote(key: String) {
        val viewModelStoreOwner = viewModelStoreOwners[key] ?: return
        viewModelStoreOwner.viewModelStore.clear()
        viewModelStoreOwners.remove(key)
    }

    inner class NavigationViewModelStoreOwner : ViewModelStoreOwner {

        public override val viewModelStore: ViewModelStore = ViewModelStore()
    }
}

@Composable
@PublishedApi
internal fun getViewModelFactory(): ViewModelFactory =
    checkNotNull(LocalViewModelFactory.current) {
        "No ViewModelFactory was provided via LocalViewModelFactory"
    }

public object LocalViewModelFactory {

    private val LocalViewModelFactory =
        compositionLocalOf<ViewModelFactory?> { null }

    public val current: ViewModelFactory?
        @Composable
        get() = LocalViewModelFactory.current

    public infix fun provides(viewModelFactory: ViewModelFactory):
            ProvidedValue<ViewModelFactory?> {
        return LocalViewModelFactory.provides(viewModelFactory)
    }
}

public class DefaultViewModelFactory : ViewModelFactory {

    public override fun <VM : ViewModel> create(modelClass: Class<VM>): VM =
        modelClass.getConstructor().newInstance()
}

public interface ViewModelFactory {

    public fun <VM : ViewModel> create(modelClass: Class<VM>): VM
}