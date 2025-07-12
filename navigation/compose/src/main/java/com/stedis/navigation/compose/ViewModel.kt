package com.stedis.navigation.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stedis.navigation.core.Destination

/**
 * Creates and provides an instance of [NavigationViewModel], binding it to the lifecycle of the current
 * [Destination] within a Compose environment. This function leverages [NavigationViewModel] to manage
 * the ViewModel's lifecycle in accordance with navigation state.
 *
 * **Important:** This ViewModel **must only be used within the scope of a [Pane] composable function**,
 * as its lifecycle is intrinsically tied to the context of the [Pane] in which it is instantiated.
 *
 * The lifecycle of the ViewModel is controlled via the [lifecycleMode] parameter:
 * - In `SHORT` mode (the default), the ViewModel remains alive only while the `Pane` in which it was created
 *   is present on screen. Once the `Pane` is removed, the ViewModel is disposed.
 * - In `LONG` mode, the ViewModel persists as long as the associated navigation destination exists in the
 *   navigation state. Notably, in this mode, the ViewModel continues to live even after the `Pane` leaves the screen.
 *
 * If no [ViewModelFactory] is provided, the default factory from the [Navigation] context will be used.
 *
 * @param factory Optional factory to create the [NavigationViewModel] instance. Defaults to the navigation's default factory if `null`.
 * @param key Optional key to uniquely identify the ViewModel instance. If `null`, a key is generated based on the current destination and ViewModel class name.
 *            Use distinct keys if multiple instances of the same ViewModel class need to coexist.
 * @param lifecycleMode Controls the lifecycle duration of the ViewModel. Defaults to [LifecycleMode.SHORT].
 *
 * Example usage:
 * ```
 * @Composable
 * fun UserPane() {
 *     // No key required when a single instance is sufficient
 *     val loginViewModel = NavigationViewModel<LoginViewModel>()
 *
 *     // Distinct keys differentiate multiple instances of the same ViewModel type
 *     val userViewModel1 = NavigationViewModel<UserViewModel>(key = "UserViewModel1")
 *     val userViewModel2 = NavigationViewModel<UserViewModel>(key = "UserViewModel2")
 *
 *     //will live as long as the destination lives in the navigation state
 *     val timerViewModel = NavigationViewModel<TimerViewModel>(lifecycleMode = LifecycleMode.LONG)
 * }
 * ```
 *
 * @return An instance of [NavigationViewModel] scoped to the current destination's lifecycle.
 *
 * @throws IllegalArgumentException If the ViewModel cannot be created with the provided factory.
 */

@Composable
inline fun <reified VM : ViewModel> NavigationViewModel(
    factory: ViewModelProvider.Factory? = null,
    key: String? = null,
    lifecycleMode: LifecycleMode = LifecycleMode.LONG,
): VM {
    val navigationViewModel = getMainNavigationViewModel()
    val viewModelFactory = factory ?: getViewModelFactory()
    val currentDestination = LocalDestination.current
    val key = "${currentDestination}_${VM::class.simpleName ?: VM::class.java.simpleName}_${key}"

    navigationViewModel.setViewModelStoreOwnerStatus(key, true)

    DisposableEffect(Unit) {
        onDispose {
            if (lifecycleMode == LifecycleMode.SHORT) {
                navigationViewModel.setViewModelStoreOwnerStatus(key, false)
            }
        }
    }

    return viewModel(
        viewModelStoreOwner = navigationViewModel.getViewModelStoreOwner(key)
    ) {
        viewModelFactory.create(VM::class.java)
    }
}

public enum class LifecycleMode {
    SHORT,
    LONG,
}

@Composable
@PublishedApi
internal fun getMainNavigationViewModel(): MainNavigationViewModel =
    checkNotNull(LocalMainNavigationViewModel.current) {
        "No MainNavigationViewModel was provided via LocalMainNavigationViewModel"
    }

@PublishedApi
internal object LocalMainNavigationViewModel {

    private val LocalMainNavigationViewModel =
        compositionLocalOf<MainNavigationViewModel?> { null }

    public val current: MainNavigationViewModel?
        @Composable
        get() = LocalMainNavigationViewModel.current

    public infix fun provides(navigationViewModel: MainNavigationViewModel):
            ProvidedValue<MainNavigationViewModel?> {
        return LocalMainNavigationViewModel.provides(navigationViewModel)
    }
}

@PublishedApi
internal class MainNavigationViewModel : ViewModel() {

    val resultManager = ResultManager()
    private val viewModelStoreOwners =
        mutableMapOf<String, Pair<NavigationViewModelStoreOwner, Boolean>>()

    internal fun getKeys(): MutableSet<String> =
        viewModelStoreOwners.keys

    public fun getViewModelStoreOwner(key: String): ViewModelStoreOwner {
        val viewModelStoreOwner = viewModelStoreOwners[key]
        if (viewModelStoreOwner != null) {
            return viewModelStoreOwner.first
        } else {
            val newViewModelStoreOwner = NavigationViewModelStoreOwner()
            viewModelStoreOwners[key] = newViewModelStoreOwner to true
            return newViewModelStoreOwner
        }
    }

    public fun setViewModelStoreOwnerStatus(key: String, active: Boolean): Boolean {
        val viewModelStoreOwner = viewModelStoreOwners[key]
        return if (viewModelStoreOwner != null) {
            viewModelStoreOwners[key] = viewModelStoreOwner.first to active
            true
        } else {
            false
        }
    }

    public fun getViewModelStoreOwnerStatus(key: String): Boolean? {
        val viewModelStoreOwner = viewModelStoreOwners[key]
        return viewModelStoreOwner?.second
    }

    public fun remote(key: String) {
        val viewModelStoreOwner = viewModelStoreOwners[key] ?: return
        viewModelStoreOwner.first.viewModelStore.clear()
        viewModelStoreOwners.remove(key)
    }

    inner class NavigationViewModelStoreOwner : ViewModelStoreOwner {

        public override val viewModelStore: ViewModelStore = ViewModelStore()
    }
}

/**
 * Retrieves the current [ViewModelProvider.Factory] from the [LocalViewModelFactory].
 *
 * @return The current instance of [ViewModelProvider.Factory].
 * @throws IllegalStateException If no [ViewModelProvider.Factory] was provided.
 */
@Composable
@PublishedApi
internal fun getViewModelFactory(): ViewModelProvider.Factory =
    checkNotNull(LocalViewModelFactory.current) {
        "No ViewModelFactory was provided via LocalViewModelFactory"
    }


/**
 * The [LocalViewModelFactory] object serves as a centralized provider for [ViewModelProvider.Factory]
 * instances within a Jetpack Compose hierarchy.
 *
 * It utilizes the Composition Local API to allow composables to access the current
 * [ViewModelProvider.Factory] instance seamlessly. This is particularly useful for implementing
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
        compositionLocalOf<ViewModelProvider.Factory?> { null }

    public val current: ViewModelProvider.Factory?
        @Composable
        get() = LocalViewModelFactory.current

    public infix fun provides(viewModelFactory: ViewModelProvider.Factory):
            ProvidedValue<ViewModelProvider.Factory?> {
        return LocalViewModelFactory.provides(viewModelFactory)
    }
}

/**
 * The [DefaultViewModelFactory] class provides a default implementation of the [ViewModelProvider.Factory]
 * interface for creating [ViewModel] instances.
 *
 * This factory uses reflection to instantiate [ViewModel] objects without the need for explicitly
 * defining their constructors. It is particularly useful for cases where the [ViewModel] does not
 * require any parameters in its constructor.
 */
public class DefaultViewModelFactory : ViewModelProvider.Factory {

    public override fun <VM : ViewModel> create(modelClass: Class<VM>): VM =
        modelClass.getConstructor().newInstance()
}