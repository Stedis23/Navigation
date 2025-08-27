Navigation
========

Navigation does not limit us!

## Add library

```kotlin

// Add dependencies to use without UI
implementation("io.github.stedis23:navigation-core:0.6.0")

// For project with Compose
implementation("io.github.stedis23:navigation-compose:0.6.0")

```

## Why Navigation?

- Manage navigation as a state
- You can choose any structure for your navigation state. Stack, Tree or something completely new.
  The library tools will allow you to design any structure
- Have full access to control navigation state
- Manage navigation state through navigation commands. You can create new navigation commands, as
  well as chain existing navigation commands for maximum efficiency and flexibility
- Navigation has host tree traversal tools that allow you to easily make changes to any part of your
  navigation state
- Support for declarative coding style
- Navigation does not dictate what architecture you will have on your project, you can use any
  architecture with minimal changes to implement the library
- The library has support for the life cycle of view models and states of screen compositions.

## Basic compose Sample with Navigation

```kotlin
//implement your composable components
@Composable
fun FirstSamplePane() {
    //get navigation manager to manage navigation inside the component
    val navigationManager = LocalNavigationManager.current
    var text by remember { mutableStateOf("") }

    Column(
    ) {
        TextField(
            value = text,
            onValueChange = { text = it },
        )
        Button(onClick = {
            // navigation command to change the navigation state
            navigationManager.execute(ForwardCommand(SecondSampleDestination(text)))
        }) {
            Text("Open")
        }
    }
}

@Composable
fun SecondSamplePane(text: String) {
    Text(text = text)
}

//Add destinations and associations for composable components.
@Parcelize
class FirstSampleDestination : ComposeDestination {

    @IgnoredOnParcel
    override val composable: @Composable (Destination) -> Unit = {
        FirstSamplePane()
    }
}

@Parcelize
data class SecondSampleDestination(val text: String) : ComposeDestination {


    @IgnoredOnParcel
    override val composable: @Composable (Destination) -> Unit = { destination ->
        //You can also pass parameters from your destination.
        //This is just one of several ways you can pass parameters.
        SecondSamplePane((destination as SecondSampleDestination).text)
    }
}

//Create a navigation manager, passing the navigation state to it.
val navigationManager = rememberNavigationManager(
    NavigationState(
        NavigationHost(
            hostName = "Main",
            initialDestination = FirstSampleDestination(),
        )
    )
)

//Add Navigation, which is the entry point for navigation in the application.
Navigation(
    navigationManager = navigationManager
) {
    //Pass the current host to the panel to display the current destination.
    //You can also pass the current destination directly.
    Pane(rememberNavigationHost("Main"))
}
```

## Other Samples

- [baseApp](https://github.com/Stedis23/Navigation/blob/cff6950be654ceaada28f5b66188ff0abcdda6e1/samples/basesample)

## Sample chain of commands

```kotlin
object SampleCommand : NavigationCommand {

    override fun execute(navigationState: NavigationState): NavigationState =
        CommandsChain(navigationState) {
            ReplaceCommand(SubscribersDestination) then
                    ForwardCommand(statisticsDestination()) then
                    NavigationCommand {
                        traversalContext
                            .inside("Main")
                            .inside("Player")
                            .perform { addDestination(AdPlayerDestination) }
                    }
        }
}
```