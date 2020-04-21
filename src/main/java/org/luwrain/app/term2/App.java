
package org.luwrain.app.term2;

import java.util.*;
import java.io.*;

import jpty.*;

import org.luwrain.core.*;
import org.luwrain.linux.*;
import org.luwrain.template.*;

import org.luwrain.app.term.Strings;

public final class App extends AppBase<Strings>
{
    static final String LOG_COMPONENT = "term";

    final TermInfo termInfo;
    private Pty pty = null;
    private final LinkedList<byte[]> input = new LinkedList();
    private MainLayout layout = null;

    public App(TermInfo termInfo)
    {
	super(Strings.NAME, Strings.class);
		NullCheck.notNull(termInfo, "termInfo");

	this.termInfo = termInfo;
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
	    final InputStream is = pty.getInputStream();
	    final OutputStream os = pty.getOutputStream();
	    final InputStreamReader r = new InputStreamReader(is, "UTF-8");
	    while(true)
	    {
		synchronized (App.this){
byte[] b = input.pollFirst();
while(b != null)
{
		    os.write(b);
		    b = input.pollFirst();
}
		}
		if (r.ready())
		    		{
		    /*
		int b = is.read();
		Log.debug("term2", "get " + (char)b);
		    */
		    final char c = (char)r.read();
		    getLuwrain().runUiSafely(()->{
			    		if (this.layout != null)
			    this.layout.update(c);
			});
		}
		    try {
			Thread.sleep(10);
		    } 
		    catch (InterruptedException e) 
		    {
			return;
		    }
	    }
	}
	catch(Exception e)
	{
	    getLuwrain().crash(e);
	}
    }

    synchronized void sendByte(byte b)
    {
	input.add(new byte[]{b});
    }

        @Override public AreaLayout getDefaultAreaLayout()
    {
	return this.layout.getLayout();
    }
}
