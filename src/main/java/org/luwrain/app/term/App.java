/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

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
import java.util.concurrent.*;
import java.io.*;

import com.pty4j.*;
import com.pty4j.unix.*;

import org.luwrain.core.*;
import org.luwrain.linux.*;
import org.luwrain.app.base.*;

import org.luwrain.app.term.Strings;

public final class App extends AppBase<Strings>
{
    static final String LOG_COMPONENT = "term";
    static private final long LISTENING_DELAY = 10;

    final TermInfo termInfo;
    final String startingDir;
    private UnixPtyProcess  pty = null;
    private MainLayout layout = null;

    private volatile StringBuilder termOutput = new StringBuilder();
    private volatile long latestOutputTimestamp = new Date().getTime();

    public App(TermInfo termInfo)
    {
	super(Strings.NAME, Strings.class, "luwrain.linux.term");
	NullCheck.notNull(termInfo, "termInfo");
	this.termInfo = termInfo;
	this.startingDir = null;
    }

    public App(TermInfo termInfo, String startingDir)
    {
	super(Strings.NAME, Strings.class, "luwrain.linux.term");
	NullCheck.notNull(termInfo, "termInfo");
	this.termInfo = termInfo;
	this.startingDir = startingDir;
    }

    @Override public boolean onAppInit() throws IOException
    {
	final Map<String, String> env = new HashMap(System.getenv());
	env.put("TERM", "linux");
	this.pty = (UnixPtyProcess)(new PtyProcessBuilder(new String[]{"/bin/bash", "-l"})
				    .setEnvironment(env)
				    .setDirectory((this.startingDir != null && !startingDir.isEmpty())?startingDir:getLuwrain().getProperty("luwrain.dir.userhome"))
				    .setConsole(false)
				    .start());
	Log.debug(LOG_COMPONENT, "pty created, pid=" + pty.getPid() + ", running=" + pty.isRunning());
	getLuwrain().executeBkg(new FutureTask(()->readOutput(), null));
		getLuwrain().executeBkg(new FutureTask(()->listening(), null));
	setAppName(getStrings().appName());
	this.layout = new MainLayout(this);
	return true;
    }

    private void readOutput()
    {
	try {
	    try {
		final InputStream is = pty.getInputStream();
		final InputStreamReader r = new InputStreamReader(is, "UTF-8");
		while(pty.isRunning())
		{

			final int c = r.read();
			if (c < 0)
			{
			    Log.debug(LOG_COMPONENT, "negative character from the terminal: " + c);
			    break;
			}
					    synchronized(App.this) {
			this.termOutput.append((char)c);
			this.latestOutputTimestamp = new Date().getTime();
		    }
		    		}
		Log.debug(LOG_COMPONENT, "closing the terminal");
		//FIXME: Read complete output
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

    private void listening()
    {
	try {
	    while(pty.isRunning())
	    {
		try {
		    Thread.sleep(LISTENING_DELAY);
		}
		catch(InterruptedException e)
		{
		    Thread.currentThread().interrupt();
		}
		final String output;
		synchronized(App.this) { 
		    if (this.termOutput.length() == 0)
			continue;
		    final long timestamp = new Date().getTime();
		    if (timestamp - latestOutputTimestamp < LISTENING_DELAY)
			continue;
		    output = new String(this.termOutput);
		    this.termOutput = new StringBuilder();
		}
		getLuwrain().runUiSafely(()->{
			if (layout != null)
			    layout.termText(output);
		    });
	    }
	}
	finally {
	    Log.debug(LOG_COMPONENT, "finishing listening thread, running=" + pty.isRunning());
	    synchronized(App.this) {
		final String output = new String(this.termOutput);
		this.termOutput = new StringBuilder();
		if (!output.isEmpty())
		    getLuwrain().runUiSafely(()->{
			    if (layout != null)
				layout.termText(output);
			});
	    };
	}
    }

void sendChar(char ch)
    {
	try {

	    	if (ch < 32)
	{
	    pty.getOutputStream().write((byte)ch);
	    return;
	}
	    final OutputStreamWriter w = new OutputStreamWriter(pty.getOutputStream(), "UTF-8");
	    w.write(ch);
	    w.flush();
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
