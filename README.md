Navigation
========

Navigation is a simple way to manage application navigation as a state

## Add library

```kotlin

// Add dependencies to use without UI
implementation("io.github.stedis23:navigation-core:0.4.6")

// For project with Compose
implementation("io.github.stedis23:navigation-compose:0.4.6")

```

## Compose Sample with Navigation

```kotlin

//create names for your hosts
object Hosts {
    const val ROOT = "root"
}

//implement your composable screens
@Composable
fun FirstSampleScreen() {
    val navigationManager = LocalNavigationManager.current
    val onItemSelected: (String) -> Unit = { text ->
        navigationManager.execute(ForwardCommand(SecondSampleDestination(text)))
    }
}

//add destinations and associations for composable screens
@Parcelize
class FirstSampleDestination : ComposeDestination {

    override val composable: @Composable (Destination) -> Unit = {
        FirstSampleScreen()
    }
}

@Parcelize
data class SecondSampleDestination(val text: String) : ComposeDestination {

    override val composable: @Composable (Destination) -> Unit = { destination ->
        SecondSampleScreen((destination as SecondSampleDestination).text)
    }
}

//add Navigation function in composable block
Navigation(
    navigationManager = rememberNavigationManager(
        NavigationState(
            NavigationHost(
                hostName = Hosts.ROOT,
                initialDestination = SampleDestination
            )
        )
    )
) {
    Pane(rememberCurrentDestination() as ComposeDestination)
}
```

## Sample chain of commands

```kotlin
object SampleCommand : NavigationCommand {
    
    override fun execute(navigationState: NavigationState): NavigationState =
        CommandsChain(navigationState) {
            ReplaceCommand(FirstSampleScreen()) then
                    ForwardCommand(SecondSampleScreen())
        }
}
```