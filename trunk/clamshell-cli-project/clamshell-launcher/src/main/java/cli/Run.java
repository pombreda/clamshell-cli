package cli;

import cli.clamshell.api.Configurator;
import cli.clamshell.commons.ShellContext;
import cli.clamshell.api.Context;
import cli.clamshell.api.Shell;
import java.io.File;

/**
 * This is the entry point of the entire clamshell cli container (Main).
 * This is a thin starter module that serves as a bootloader for the system.
 * <ul>
 * <li> ensure that the container's folder follows the expected layout convention</li>
 * <li>Load and prepare all plugins.</li>
 * <li>Look for a Shell component.  If none is found, abort</li>
 * <li>Hand off the continuation of the booting process to the Shell instance</li>
 * </ul>
 * 
 * <b>Argument Layout</b><br/>
 * 
 * 
 * @author vladimir.vivien
 */
public class Run {
    public static void main(String[] args) throws Exception{        
        File pluginsDir = new File(Configurator.VALUE_DIR_PLUGINS);
        if(!pluginsDir.exists()){
            System.out.printf("%nPugins directory [%s] not found. ClamShell-Cli will exit.%n%n", pluginsDir.getCanonicalPath());
            System.exit(1);
        }
        
        // create/confiugre the context
        Context context = ShellContext.createInstance();
        // only continue if plugins are found
        context.putValue(Context.KEY_INPUT_STREAM, System.in);
        context.putValue(Context.KEY_OUTPUT_STREAM, System.out);
        if(context.getPlugins().size() > 0){
            Shell shell = context.getShell();
            if(context.getShell() != null){
                shell.plug(context);
            }else{
                System.out.printf ("%nNo Shell component found in plugins directory [%s]."
                        + " ClamShell-Cli will exit now.%n", Configurator.VALUE_DIR_PLUGINS);
                System.exit(1);
            }
        }else{
            System.out.printf ("%nNo plugins found in [%s]. ClamShell-Cli will exit now.%n%n", Configurator.VALUE_DIR_PLUGINS);
            System.exit(1);
        }
    }
}
