Navigation
========

# ‼️ WORK IN PROGRESS ‼️

Navigation is a simple way to manage application navigation as a state

## Add library

```kotlin

// Add dependencies to use without UI
implementation("io.github.stedis23:navigation-core:0.1.5")

// For project with Compose
implementation("io.github.stedis23:navigation-compose-ui:0.1.5")

```

## Compose Sample with Navigation

```kotlin

//create names for your hosts
object Hosts {
    const val ROOT = "root"
}

//create navigation graph
object NavigationGraph : ComposeNavigationGraph(ComposeAssociationManager())

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
class FirstSampleDestination : Destinations() {
    override fun associate() = associate { FirstSampleScreen() }
}

@Parcelize
data class SecondSampleDestination(val text: String) : Destinations() {
    override fun associate() = associate { destination ->
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
    //subscribe to calling screens from the navigation graph when the state changes
    NavigationGraph.Screen(rememberCurrentDestination())
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