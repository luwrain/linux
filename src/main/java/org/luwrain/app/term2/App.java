
package org.luwrain.app.term2;

import java.util.*;
import java.io.*;

import jpty.*;

import org.luwrain.core.*;
import org.luwrain.template.*;

import org.luwrain.app.term.Strings;

public final class App extends AppBase<Strings>
{
    static final String LOG_COMPONENT = "term";

    private Pty pty = null;
    private MainLayout layout = null;

    public App()
    {
	super(Strings.NAME, Strings.class);
    }

    @Override public boolean onAppInit()
    {
	String[] cmd = { "/bin/bash"};
	String[] env = { "TERM=xterm" };
		Log.debug(LOG_COMPONENT, "pty created, starting the dispatching task");
	this.pty = JPty.execInPTY( cmd[0], cmd, env );
	TaskId taskId = newTaskId();
	runTask(taskId, ()->work());
	this.layout = new MainLayout(this);
	Log.debug(LOG_COMPONENT, "terminal launched");
	return true;
	}

    private void work()
    {
	try {
	InputStream is = pty.getInputStream();
	    while(true)
	    {
		int b = is.read();
		Log.debug("term2", "get " + (char)b);

		    getLuwrain().runUiSafely(()->{
			    		if (this.layout != null)
			    this.layout.update(b);
			});
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
