# Creating a Command Plugin #
Here are a few simple steps for creating your own Command:
  * Create a new Java project (standard or Maven) in your favorite IDE
  * Create a class that extends interface `org.clamshellcli.api.Command` (see detail below).
  * Setup configure class as a ServiceLoader.
  * Package your project as a jar and drop it Clamshell-Cli directory `plugins`

## The Command Class ##
```
public class TimeCmd implements Command {
    private static final String NAMESPACE = "syscmd";
    private static final String ACTION_NAME = "time";

    @Override
    public Object execute(Context ctx) {
        IOConsole console = ctx.getIoConsole();
        console.writeOutput(String.format("%n%s%n%n",new Date().toString()));
        return null;
    }

    @Override
    public void plug(Context plug) {
        // no load-time setup needed
    }
    
    @Override
    public Command.Descriptor getDescriptor(){
        return new Command.Descriptor() {
            @Override public String getNamespace() {return NAMESPACE;}
            
            @Override
            public String getName() {
                return ACTION_NAME;
            }

            @Override
            public String getDescription() {
               return "Prints current date/time";
            }

            @Override
            public String getUsage() {
                return "Type 'time'";
            }

            @Override
            public Map<String, String> getArguments() {
                return Collections.emptyMap();
            }
        };
    }
}
```

A quick explanation of the code is in order:
  * Method `execute()` - invoked by the input controller instance when it detects the String `time` from the command-line.  The method retrieves the IOConsole from the context object and use it to print the time.  It returns null to the controller (indicating the command did not generate a result).
  * Method `plug()` - a lifecycle method that is invoked by the framework when the command is first initialized.  For our example, there nothing to do.
  * Method `getDescriptor()` - returns an instance of interface `Command.Descriptor` which is used to describe the features and document the Command.  For our example, the Descriptor interface is implemented anonymously with the following methods:
    * Method `Descriptor.getNamespace()` - returns a string identifying the command's namespace.  This value can be used by input controllers to avoid command name collisions.
    * Method `Descriptor.getName()` - returns the string mapped to this command object.  In our implementation, it returns "time".
    * Method `Descriptor.getUsage()` - intended to provide a descriptive way of using the command.
    * Method `Descriptor.getArguments()` - returns a Map containing the description for each arguments that may be attached to the command.  This example uses none.

## Packaging Your Plugin ##
Once your class compiles properly, package the project as a standard Java Service provider (SPI).  To do this, do the followings:
  * In your source tree, create service descriptor text file META-INF/services/org.clamshellcli.api.Plugin
  * On the first line of that file, put `demo.command.TimeCmd` (assuming the class is placed in package `demo.command`).
  * Next, save the text file and package the project as a jar file (**it is important that the descriptor text file gets copied into your jar**)

Find more information on Java's Service Loader API:
  * [Service Loader API](http://download.oracle.com/javase/6/docs/api/java/util/ServiceLoader.html)
  * [Article on Java Extensibility](http://java.sun.com/developer/technicalArticles/javase/extensible/)

## Deploy and Test the Command ##
  * Drop the jar file in the `plugins/` directory (shown above).
  * Start the Clamshell-Cli runtime.
  * From the command-line type 'help' and you should see your new command listed.