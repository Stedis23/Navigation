package com.stedis.navigation.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stedis.navigation.core.Destination

/**
 * Creates and provides an instance of [ViewModel], binding it to the lifecycle of the current
 * [Destination] within a Compose environment. This function utilizes [NavigationViewModel] to
 * manage the lifecycle of the [ViewModel].
 *
 * If a [ViewModelFactory] is not provided, the default factory from the [Navigation] method
 * will be used.
 *
 * @param factory An optional factory for creating the [ViewModel] instance. If `null`, the
 * default factory will be utilized.
 *
 * @return An instance of [ViewModel] that is tied to the lifecycle of the current destination.
 *
 * @throws IllegalArgumentException If the [ViewModel] cannot be created using the provided
 * factory.
 */
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

/**
 * Retrieves the current [ViewModelFactory] from the [LocalViewModelFactory].
 *
 * @return The current instance of [ViewModelFactory].
 * @throws IllegalStateException If no [ViewModelFactory] was provided.
 */
@Composable
@PublishedApi
internal fun getViewModelFactory(): ViewModelFactory =
    checkNotNull(LocalViewModelFactory.current) {
        "No ViewModelFactory was provided via LocalViewModelFactory"
    }


/**
 * The [LocalViewModelFactory] object serves as a centralized provider for [ViewModelFactory]
 * instances within a Jetpack Compose hierarchy.
 *
 * It utilizes the Composition Local API to allow composables to access the current
 * [ViewModelFactory] instance seamlessly. This is particularly useful for implementing
 * dependency injection patterns in a composable context, ensuring that the appropriate
 * factory is available to create [ViewModel] instances.
 *
 * Example usage:
 * ```
 * CompositionLocalProvider(LocalViewModelFactory provides myViewModelFactory) {
 *     // Access the ViewModelFactory within this composition
 *     val viewModelFactory = LocalViewModelFactory.current
 * }
 * ```
 */
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

/**
 * The [DefaultViewModelFactory] class provides a default implementation of the [ViewModelFactory]
 * interface for creating [ViewModel] instances.
 *
 * This factory uses reflection to instantiate [ViewModel] objects without the need for explicitly
 * defining their constructors. It is particularly useful for cases where the [ViewModel] does not
 * require any parameters in its constructor.
 */
public class DefaultViewModelFactory : ViewModelFactory {

    public override fun <VM : ViewModel> create(modelClass: Class<VM>): VM =
        modelClass.getConstructor().newInstance()
}

/**
 * The [ViewModelFactory] interface defines a contract for creating instances of [ViewModel].
 *
 * This interface is utilized within the library to allow for the creation of custom factories
 * for [ViewModel]. If you wish to implement your own factory, you will need to override this
 * interface.
 */
public interface ViewModelFactory {

/**
 * Creates a new instance of the specified [ViewModel] class.
 *
 * @param modelClass The class of the [ViewModel] to be instantiated.
 * @return A new instance of the specified [ViewModel].
 */
    public fun <VM : ViewModel> create(modelClass: Class<VM>): VM
}