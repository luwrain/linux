/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.app.term;

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
    private MainLayout layout = null;

    public App(TermInfo termInfo)
    {
	super(Strings.NAME, Strings.class);
	NullCheck.notNull(termInfo, "termInfo");
	this.termInfo = termInfo;
    }

    @Override public boolean onAppInit() throws IOException
    {
	final Map<String, String> env = new HashMap(System.getenv());
	env.put("TERM", "linux");
	//	env.put("HOME", "/home/msp");
	this.pty = (UnixPtyProcess)(new PtyProcessBuilder(new String[]{"/bin/bash", "-l"})
				    .setEnvironment(env)
				    .setDirectory(getLuwrain().getProperty("luwrain.dir.userhome"))
				    .setConsole(false)
				    .start());
	Log.debug(LOG_COMPONENT, "pty created, pid=" + pty.getPid() + ", running=" + pty.isRunning());
	TaskId taskId = newTaskId();
	runTask(taskId, ()->work());
	setAppName(getStrings().appName());
	this.layout = new MainLayout(this);
	return true;
    }

    private void work()
    {
	try {
	    try {
		final InputStream is = pty.getInputStream();
		final InputStreamReader r = new InputStreamReader(is, "UTF-8");
		while(true)
		{
		    if (!pty.isRunning())
		    {
			Log.warning(LOG_COMPONENT, "PTY not running, closing");
			break;
		    }
		    final char c = (char)r.read();
		    if (c < 0)
		    {
			Log.debug(LOG_COMPONENT, "negative character from the terminal: " + c);
			break;
		    }
		    getLuwrain().runUiSafely(()->{
			    if (this.layout != null)
				this.layout.update(c);
			});
		}
		Log.debug(LOG_COMPONENT, "closing the terminal");
		r.close();
		is.close();
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
	pty.hangup();
	pty.terminate();
	super.closeApp();
    }
}
