
package org.luwrain.linux;

import java.util.*;
import java.nio.file.*;
import java.io.*;

import org.luwrain.core.*;

class Scripts
{
    private Path scriptsDir;

    Scripts(Path scriptsDir)
    {
	NullCheck.notNull(scriptsDir, "scriptsDir");
	this.scriptsDir = scriptsDir;
    }

    boolean runSync(String scriptName, boolean sudo)
    {
	NullCheck.notNull(scriptName, "scriptName");
	try {
	    final Process p = sudo?new ProcessBuilder("sudo", scriptsDir.resolve(scriptName).toString()).start():
	    new ProcessBuilder(scriptsDir.resolve(scriptName).toString()).start();
	    p.getOutputStream().close();
	    p.waitFor();
	    return p.exitValue() == 0;
	}
	catch(InterruptedException e)
	{
	    Thread.currentThread().interrupt();
	    return false;
	}
	catch(IOException e)
	{
	    e.printStackTrace();
	    return false;
	}
    }

    boolean runSync(String scriptName, String[] args,
		    boolean sudo)
    {
	NullCheck.notNull(scriptName, "scriptName");
	NullCheck.notNullItems(args, "args");
	Log.debug("linux", "running script \'" + scriptName + "\' with " + args.length + " argument(s)" + (sudo?" using sudo":""));
	for(int i = 0;i < args.length;++i)
	    Log .debug("linux", "arg" + i + ":" + args[i]);
	final LinkedList<String> cmd = new LinkedList<String>();
	if (sudo)
	    cmd.add("sudo");
	cmd.add(scriptsDir.resolve(scriptName).toString());
	for(String s: args)
	    cmd.add(s);
	try {
	    final Process p = new ProcessBuilder(cmd.toArray(new String[cmd.size()])).start();
	    p.getOutputStream().close();
	    p.waitFor();
final int res = p.exitValue();
Log.debug("linux", "exit code is " + res);
return res == 0;
	}
	catch(InterruptedException e)
	{
	    Thread.currentThread().interrupt();
	    return false;
	}
	catch(IOException e)
	{
	    e.printStackTrace();
	    return false;
	}
    }


}
