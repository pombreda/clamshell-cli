## ClamShell-Cli Design ##
The Clamshell-Cli framework has been intentionally designed to be simple.  Really simple.  The idea is not to make Clamshell-Cli a bloated piece of software trying to handle everyting when it comes to a console-based app (that would be too complex), but rather provide an extensible platform that lets developers build console-based tools by implementing pieces of functionality via plugins.

All major aspects of a working console-based command-line tool are represented by statically defined interfaces.  For instance, if you want to change the console prompt, you simply implement the `Prompt` interface to return the prompt you want displayed.  Then deploy your jar in the `plugin` directory, done!  Next time the Clamshell-Cli runtime starts, you will see your new prompt.  This section explores the major components

## Launcher ##
The launcher (`cli.jar`) is the main entry point for the runtime.  It is a small kernel that primarly boots up the runtime by creating a class loader then immediately looks for a `Shell` plugin.  Once found, it (Launcher) delegates startup sequence to the `Shell` component.  The launcher component is not intended to be customizable since it does so little.  If you want to change the way things work, look at the Shell, InputController, IOConsole, and Command components.

## Context ##
The Context object is an internal component (not extensible) this is used as a conduit to carry common data and functionality among plugins.  The Context makes the following information available at runtime:
  * A Map for storing shared values accessible by all components.
  * Access to the loaded plugin instances
  * Numerous convenience methods for looking up plugins

The default runtime implementation of the Context is backed by a simple HashMap for storing values.  It is not designed for thread-safety. If you have multi-threaded plugins, take care to wrap content stored in the Context into thread-aware containers.

## Shell ##
The Shell is a Plugin that is intended to kickstart the console runtime environment.  In the default implementation, the Shell is responsible for loacating and intializing all other plugins by calling the `Plug.plug(Context)` method on each Plugin.  Its responsibility is summarized below:
  * Plug the IOConsole instance (cardinality = 1)
  * Plug and render any SplashScreen plugins (cardinality = 0 or more)
  * Plug the InputController instances (cadinality = 0 or more)

The Shell itsefl has a cardinality of 1.  The Launcher will load only the first Shell instance from the plugins directory.  Additional shell's will be ignored.  The default Shell implementation will apply the cardinality rules for other components mentioned above.

## Prompt ##
All shell-based console application supports the notion of a prompt.  In Clamshell-Cli, the prompt is represented by the interface Prompt.  It returns a string that is displayed by the IOConsole as the input prompt. The default runtime will look for a Prompt plugin, if none is found, it will display the user's login name as the prompt.

The Prompt plugin has a cardinality of 1 in the default implementation.  The framework will load only one Prompt instance.  All others will be ignored.

## IOConsole ##
As its name suggests, the IOConsole is responsible for capturing user input and displaying user output.  The default implementation maps input to `System.in` and output to `System.out`.  The loaded instance of IOConsole is stored in the Context.  When a user enters an input string fromt the console, the IOConsole component passes the input to all of the installed (active) input controllers for futher handling (see below).

IOConsole has an expected cardinality of 1 in the default implementation.  The framework will load only the first instance of IOConsole from the plugins directory.  All other instances will be ignored.

## InputController ##
The `InputController` is the component responsible for taking the text input from the console and figure out how to handle it.  In a simple implementation, the InputController could handle the input string directly with no futher delegation (see [EchoController.java](http://code.google.com/p/clamshell-cli/source/browse/trunk/clamshell-cli-project/clamshell-echo-controller/src/main/java/org/clamshellcli/impl/EchoController.java).

For more complex implementation, you may elect to separate the functions of the input controller from that of executing/handling the tasks associated with the input. An `InputController` may delegate handling of its tasks to a `Command` object.  The default implementation of Clamshell-Cli comes with `CmdController`, an input controller that delegates handling of input to Command objects.  When a user presses enter (at the end of a command), `CmdController` parses the string and looks for a `Command` object that is mapped the to the name of the command.  If a command exists, it invokes the command's `Command.execut(Context)` method.

InputController plugin have a cardinality of 0..`*`. The framework will search for a controller that is capable of handling the input (based on input pattern configuration in the config file).

## Command ##
The `Command` plugin can be used as a delegate to handle actions for the framework.  Each command instance is expected to expose an identifier via the `Command.Descriptor.getName()` method.  The value returned by this method is used to map the Command object with its name.  In the default implementation, when the CmdController receives an input, it splits the input string invokes the Command object that is mapped to value `input[0]`.

The Command plugin has a cardinality of 0..`*`. The implemented dispatch mechanism will figure out how to invoke the commands installed.  The Command.Descriptor object also exposes `getNamespace()` method which returns a string that can be used to futher classify Command objects.

## Command.Descriptor ##
Text-driven UI rely heavily on descriptive narrative to provide hints about its functionality (unlike GUI where the shape, location, and loo-n-feel of widgets affords users hints of their behaviors).  The Command.Descriptor is a class available to describe the features/behavior of an installed command.  Each Command instance is required to provide an implementation that returns the name of the command, a description, and a list of any parameters that may be accepted by the command.

The Command.Descriptor exposes the follwoing methods:
  * Method `Descriptor.getNamespace()` - returns a string identifying the command's namespace.  This value can be used by input controllers to avoid command name collisions.
  * Method `Descriptor.getName()` - returns the string mapped to this command object.  In our implementation, it returns "time".
  * Method `Descriptor.getUsage()` - intended to provide a descriptive way of using the command.
  * Method `Descriptor.getArguments()` - returns a Map containing the description for each arguments that may be attached to the command.  This example uses none.