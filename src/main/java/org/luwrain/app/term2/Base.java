
package org.luwrain.app.term2;

import java.io.*;
import java.util.concurrent.*;

import jpty.*;

import org.luwrain.linux.term.*;
import org.luwrain.core.*;

import org.luwrain.app.term.Strings;

class Base
{
    static private final int STEP_DELAY = 10;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Luwrain luwrain;
    private final Terminal terminal = new Terminal();
    private FutureTask task;

private Pty pty = null;

    boolean init(Luwrain luwrain)
    {
	Log.debug("term2", "starting");
	this.luwrain = luwrain;
	NullCheck.notNull(luwrain, "luwrain");
	return true;
    }

    boolean start(String dir,
		  final Actions actions)
    {

	if (task != null && !task.isDone())
	    return false;

			try {
			    String[] cmd = { "/bin/bash"};
    String[] env = { "TERM=xterm" };
this.pty = JPty.execInPTY( cmd[0], cmd, env );
	}
	catch(Throwable e)
	{
	    Log.error("term2", e.getClass().getName() + ":" + e.getMessage());
	    return false;
	}
	Log.debug("term2", "ok");
	task = new FutureTask(()->{

		InputStream is = pty.getInputStream();
		try {
		    while(true)
		    {
		    int b = is.read();
		    Log.debug("term2", "get " + (char)b);
		    }
		}
		catch(Exception e)
		{
		    luwrain.crash(e);
		}


		    try {
			Thread.sleep(STEP_DELAY);
		    } 
		    catch (InterruptedException e) 
		    {
			Thread.currentThread().interrupt();
		    }
	    }, null);
	executor.execute(task);
	return true;
    }

    void close()
    {
	terminal.close();
    }

    Terminal getTerm()
    {
	return terminal;
    }
}
