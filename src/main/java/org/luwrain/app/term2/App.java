
package org.luwrain.app.term2;

import java.util.*;
import java.io.*;

import com.pty4j.*;
import com.pty4j.unix.*;

import org.luwrain.core.*;
import org.luwrain.linux.*;
import org.luwrain.template.*;

import org.luwrain.app.term.Strings;

public final class App extends AppBase<Strings>
{
    static final String LOG_COMPONENT = "term";

    final TermInfo termInfo;

    private final LinkedList<byte[]> input = new LinkedList();
    private boolean closing = false;
    private MainLayout layout = null;

    public App(TermInfo termInfo)
    {
	super(Strings.NAME, Strings.class);
		NullCheck.notNull(termInfo, "termInfo");

	this.termInfo = termInfo;
    }

    @Override public boolean onAppInit()
    {
	TaskId taskId = newTaskId();
	runTask(taskId, ()->work());
	this.layout = new MainLayout(this);
	return true;
    }

    private void work()
    {
	try {
	try {

	    //	    	String[] cmd = { "/tmp/p"};
	    final Map<String, String> env = new HashMap();
	    env.put("TERM", "linux");
	    //	String[] env = { "TERM=linux" };
	    final UnixPtyProcess pty = (UnixPtyProcess)(new PtyProcessBuilder(new String[]{"/bin/bash"})
							.setEnvironment(env)
							.start());
	    Log.debug(LOG_COMPONENT, "pty created, running=" + pty.isRunning());
	    final InputStream is = pty.getInputStream();
	    	    final InputStream es = pty.getErrorStream();
	    final OutputStream os = pty.getOutputStream();
	    final InputStreamReader r = new InputStreamReader(is, "UTF-8");
	    	    final InputStreamReader er = new InputStreamReader(es, "UTF-8");
	    while(!closing)
	    {
		if (!pty.isRunning())
		{
		    Log.warning(LOG_COMPONENT, "PTY not running, closing");
		    break;
		}

		
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
		    final char c = (char)r.read();
		    Log.debug(LOG_COMPONENT, "get char '" + c + "'");
		    if (c < 0)
			break;
		    getLuwrain().runUiSafely(()->{
			    		if (this.layout != null)
			    this.layout.update(c);
			});
				}

				if (er.ready())
		    		{
		    final char c = (char)er.read();
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
	    Log.debug(LOG_COMPONENT, "closing the terminal");
	    r.close();
	    er.close();
	    is.close();
	    es.close();
	    os.close();
	    try {
		pty.waitFor();
	    }
	    catch(InterruptedException e)
	    {
		Thread.currentThread().interrupt();
	    }
	    Log.debug(LOG_COMPONENT, "exit value is " + pty.exitValue());
	}
	catch(Exception e)
	{
	    getLuwrain().crash(e);
	}
	}
	catch(Throwable t)
	{
	    Log.error(LOG_COMPONENT, "pty: " + t.getClass().getName() + ":" + t.getMessage());
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

    @Override public void closeApp()
    {
	closing = true;
	super.closeApp();
    }
}
