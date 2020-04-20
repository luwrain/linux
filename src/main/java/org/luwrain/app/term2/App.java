
package org.luwrain.app.term2;

import java.util.*;
import java.io.*;

import jpty.*;

import org.luwrain.core.*;
import org.luwrain.template.*;

import org.luwrain.app.term.Strings;

public class App extends AppBase<Strings>
{
    private Pty pty = null;
    private MainLayout layout = null;

    public App()
    {
	super("term", Strings.class);
    }

    @Override public boolean onAppInit()
    {
	String[] cmd = { "/bin/bash"};
	String[] env = { "TERM=xterm" };
	this.pty = JPty.execInPTY( cmd[0], cmd, env );
	TaskId taskId = newTaskId();
	runTask(taskId, ()->work());
	this.layout = new MainLayout(this);
	return true;
	    }

    private void work()
    {
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
	    getLuwrain().crash(e);
	}
    }

    /*
		    try {
			Thread.sleep(STEP_DELAY);
		    } 
		    catch (InterruptedException e) 
		    {
			Thread.currentThread().interrupt();
		    }
    */

        @Override public AreaLayout getDefaultAreaLayout()
    {
	return this.layout.getLayout();
    }
}
