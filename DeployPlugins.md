# Deploying Clamshell-Cli Plugins #
Clamshell-Cli uses a plugin architecture.  All extension points of the framework can be customized by providing an implementation of one of the plugin interface.  If you are not familiar with the  process of creating your own Clamshell plugin, take a look at CreatingCommandPlugin.  This section shows how to deploy a plugin.

The following will walk through deploying the Jmx-Cli plugins (see https://github.com/vladimirvivien/jmx-cli.  The collection of Jmx-Cli plugins implement a fully-functional console-based JMX tool with command-line interface.

## Download Runtime ##
To get started, download and test the runtime:
  * First, download the default Clamshell-Cli runtime (zipped binary distribution).
  * Unzip at a locatin of your choice (will refer to it as {CLI\_HOME}).
  * Change directory inside {CLI\_HOME} and inspect the files.  You will see the followings:
```
-rw-r--r--@ 1 299   Mar  17 cli.config
-rw-r--r--@ 1 3748  Mar  18 cli.jar
drwxrwxrwx  5 170   Mar  18 clilib
drwxrwxrwx  4 136   Mar  18 lib
drwxrwxrwx  5 170   Mar  18 plugins
```
    * `cli.config` - Clamshell-Cli configuration file
    * `cli.jar` - the launcher jar file
    * `clilib` - lib files to boot Clamshell-Cli
    * `lib` - place your dependency jars here
    * `plugins` - location for Clamshell-Cli plugin jars

Now, launch the Clamshell-Cli runtime by typing the following:

`> java -jar cli.jar`

You will see the Clamshell-Cli splashscreen followed by the prompt indicating that the runtimem is ready.

```
 .d8888b.  888                         .d8888b.  888               888 888
d88P  Y88b 888                        d88P  Y88b 888               888 888
888    888 888                        Y88b.      888               888 888
888        888  8888b.  88888b.d88b.   :Y888b.   88888b.   .d88b.  888 888
888        888     :88b 888 :888 :88b     :Y88b. 888 :88b d8P  Y8b 888 888
888    888 888 .d888888 888  888  888       :888 888  888 88888888 888 888
Y88b  d88P 888 888  888 888  888  888 Y88b  d88P 888  888 Y8b.     888 888
 :Y8888P:  888 :Y888888 888  888  888  :Y8888P:  888  888  :Y8888  888 888

                                                  Command-Line Interpreter

Java version: 1.6.0_22
Java Home: /usr/lib/jvm/java-6-openjdk/jre
OS: Linux, Version: 2.6.38-10-generic

prompt> _
```

Next, type `help` and verify the list of installed `Command` plugins.

```
prompt> help

Available Commands
------------------
      exit       Exits ClamShell.
      help       Displays help information for available commands.
   sysinfo       Displays current JVM runtime information.
      time       Prints current date/time

prompt> _
```

## Install Plugins ##
To keep things simple, we will install the Jmx-Cli plugins to run on the runtime ([Jmx-Cli on Github](https://github.com/vladimirvivien/jmx-cli)).
  * Download Jmx-Cli binaries (or you can build from source using Maven)
  * All of the Jmx-Cli plugins are packaged in one jar.  Drop the jar file in the `plugins` directory.
```
>ls -al 
clamshell-echo-controller-0.5.0.jar
clamshell-impl-default-0.5.0.jar
jmxcli-0.1.0.jar
```

Once the plugin is installed, you have to tell Clamshell-Cli about its capabilities.  You do that in the config file.

## Configuring the Plugin ##
You must tell Clamshell-Cli about the capabilities of the plugins you just dropped in.  Particularly, you have to configure any InputController that may have been installed with the plugins.  Jmx-Cli comes with its own InputController.  Therefore, we will tell Clamshell-Cli what input pattern the jmx-cli controller can handle.  To do that, edit file `cli.config` and add the following under the `controllers` section of the configuration file:

```
"org.clamshellcli.jmx.JmxController":{
	"enabled":"true",
	"inputPattern":"\\s*(ps|connect|mbean|desc|list|exec)\\b.*",
	"expectedInputs":[]
}
```

The snippet above sets up the regular expression input pattern for the JmxController.  The controller will only be invoked when the command-line input starts with one of the following strings (which represent a Jmx-Cli command)

`\\s*(ps|connect|mbean|desc|list|exec)\\b.*`

The entire config file should look like this:

```
{
    "properties":{
        "libDir":"./lib",
        "pluginsDir":"./plugins"
    },
    "controllers":{
        "org.clamshellcli.impl.CmdController":{
            "enabled":"true",
            "inputPattern":"\\s*(exit|help|sysinfo|time)\\b.*",
            "expectedInputs":[]
        },
        "org.clamshellcli.jmx.JmxController":{
            "enabled":"true",
            "inputPattern":"\\s*(ps|connect|mbean|desc|list|exec)\\b.*",
            "expectedInputs":[]
        }
    }
}
```

## Test Plugins Installation ##
If all went well, you should have a fully-functional JMX command-line tool at your disposal.  To test it, start the Clamshell-Cli runtime.  As an indication that the deployment works, you should see the prompt changed to `jmx-cli >`.  Now type help, you should see additional commands listed in the output

```
jmx-cli > help

Available Commands
------------------
      exit       Exits ClamShell.
      help       Displays help information for available commands.
   sysinfo       Displays current JVM runtime information.
      time       Prints current date/time
        ps       Displays a list of running JVM processes (similar to jps tool)
   connect       Connects to local or remote JVM MBean server.
     mbean       Creates a label for identifying an MBean
      desc       Prints description for specified mbean.
      list       Lists JMX MBeans.
      exec       Execute MBean operations and getter/setter attributes.

jmx-cli > 
```

As expected, you now have six new Command plugins installed.  You can test the installation by typing `ps` and enter.  This command should list all local running JVM processes and their ID's.