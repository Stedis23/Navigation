Navigation
========

# ‼️ WORK IN PROGRESS ‼️

Navigation is a simple way to manage application navigation as a state

## Add library

Facts about library:

- The library is not tied to the UI and can be used in any project
- State management based on command pattern
- - Fully async using Coroutines

```kotlin

// Add dependencies to use without UI
implementation("io.github.stedis23:navigation-core:0.1.1")

```

## Compose Sample with Navigation

```kotlin

object Hosts {
    const val ROOT_HOST = "root_host"
}

class FirstSampleScreen : Destination

data class SecondSampleScreen(val text: String) : Destination

val navigationManager = NavigationManager {
    NavigationState(initialHost = NavigationHost(Hosts.ROOT_HOST, FirstSampleScreen()))
}

@Composable
fun FirstSampleScreen(navigationManager: NavigationManager) {
    val onItemSelected: (String) -> Unit = { text -> navigationManager.execute(ForwardCommand(SecondSampleScreen(text))) }
}

val navigationState = navigationManager.stateFlow.collectAsState()

when (val destination = navigationState.value.currentDestination) {
    // ...
    is SecondSampleScreen -> SecondSampleScreen(navigationManager, destination.text)
    // ...
}
```

## Sample chain of commands

```kotlin
class SampleCommand() : NavigationCommand {
    
    override fun execute(navigationState: NavigationState): NavigationState =
        CommandsChain(navigationState) {
            ReplaceCommand(FirstSampleScreen()) then
                    ForwardCommand(SecondSampleScreen())
        }
}
```