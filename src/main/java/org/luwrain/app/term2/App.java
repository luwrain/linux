
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
    private UnixPtyProcess  pty = null;

    private final LinkedList<byte[]> input = new LinkedList();
    private boolean closing = false;
    private MainLayout layout = null;

    public App(TermInfo termInfo)
    {
	super(Strings.NAME, Strings.class);
		NullCheck.notNull(termInfo, "termInfo");

	this.termInfo = termInfo;
    }

    @Override public boolean onAppInit() throws IOException
    {
			final Map<String, String> env = new HashMap();
		env.put("TERM", "linux");
		this.pty = (UnixPtyProcess)(new PtyProcessBuilder(new String[]{"//bin/bash", "-l"})
							.setEnvironment(env)
					    							    .setConsole(false)
							.start());
Log.debug(LOG_COMPONENT, "pty created, pid=" + pty.getPid() + ", running=" + pty.isRunning());
TaskId taskId = newTaskId();
runTask(taskId, ()->work());
this.layout = new MainLayout(this);
return true;
    }

    private void work()
    {
	try {
	    try {
	    final InputStream is = pty.getInputStream();
	    //	    	    final InputStream es = pty.getErrorStream();
	    //	    final OutputStream os = pty.getOutputStream();
	    final InputStreamReader r = new InputStreamReader(is, "UTF-8");
	    //	    	    final InputStreamReader er = new InputStreamReader(es, "UTF-8");
	    while(!closing)
	    {
		if (!pty.isRunning())
		{
		    Log.warning(LOG_COMPONENT, "PTY not running, closing");
		    break;
		}
		    final char c = (char)r.read();
		    Log.debug(LOG_COMPONENT, "get char '" + c + "'");
		    if (c < 0)
			break;
		    getLuwrain().runUiSafely(()->{
			    		if (this.layout != null)
			    this.layout.update(c);
			});

		    /*
		while (true)
		{
		    Log.debug(LOG_COMPONENT, "reading the char");
		    int b = is.read();
		    if (b < 0)
			break;
		    Log.debug(LOG_COMPONENT, "read byte " + (char)b);
		}
		    */

				
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
	    //	    er.close();
	    is.close();
	    //	    es.close();
	    //	    os.close();
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

void sendByte(byte b)
    {
	try {
pty.getOutputStream().write(b);
	pty.getOutputStream().flush();
	}
	catch(IOException e)
	{
	    getLuwrain().crash(e);
	}
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
