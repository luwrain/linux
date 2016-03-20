
package org.luwrain.linux;

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
}
