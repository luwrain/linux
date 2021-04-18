/*
   Copyright 2012-2021 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.linux;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.io.*;

import org.luwrain.core.*;

public final class BashProcess
{
    public enum Flags {ROOT};

    public interface Listener
    {
	void onOutputLine(String line);
	void onErrorLine(String line);
	void onFinishing(int exitCode);
    }

    private final String command;
    private final Set<Flags> flags;
    private final Listener listener;
    private Process p = null;
    private int pid = -1;
    private int exitCode = -1;
    private final ArrayList<String> output = new ArrayList();
    private final ArrayList<String> errors = new ArrayList();
    private final AtomicBoolean done = new AtomicBoolean(false);
    private final AtomicBoolean doneOutput = new AtomicBoolean(false);
    private final AtomicBoolean doneErrors = new AtomicBoolean(false);

    public BashProcess(String command, Set<Flags> flags, Listener listener)
    {
	NullCheck.notEmpty(command, "command");
	NullCheck.notNull(flags, "flags");
	NullCheck.notNull(listener, "listener");
	this.command = command;;
	this.flags = flags;
	this.listener = listener;
    }

    public BashProcess(String command, Set<Flags> flags)
    {
	this(command, flags, new EmptyListener());
    }

    public BashProcess(String command)
    {
	this(command, EnumSet.noneOf(Flags.class));
    }

    public void run() throws IOException
    {
	this.p = new ProcessBuilder(prepareCmd()).start();
	p.getOutputStream().close();
	final BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
	final String pidStr = r.readLine();
	if (pidStr == null)
	    throw new IOException("Bash process didn't return its pid");
	try {
	    this.pid = Integer.parseInt(pidStr);
	    Log.debug("proba", "pid=" + pid);
	}
	catch(NumberFormatException e)
	{
	    throw new IOException("'" + pidStr + "' is not a valid pid");
	}
	readOutput(r);
	readErrors(new BufferedReader(new InputStreamReader(p.getErrorStream())));
	new Thread(()->{
		try {
		    p.waitFor();
		    this.exitCode = p.exitValue();
		    synchronized(done){
			done.set(true);
			done.notifyAll();
		    }
		    listener.onFinishing(exitCode);
		}
		catch(InterruptedException e)
		{
		    Thread.currentThread().interrupt();
		}
	}).start();
    }

    public int waitFor()
    {
	try {
	    synchronized(done) {
		while(!done.get())
		    done.wait();
	    }
	    synchronized(doneOutput) {
		while (!doneOutput.get())
		    doneOutput.wait();
	    }
	    synchronized(doneErrors) {
		while (!doneErrors.get())
		    doneErrors.wait();
	    }
	    return exitCode;
	}
	catch(InterruptedException e)
	{
	    Thread.currentThread().interrupt();
	    return -1;
	}
    }

    private String[] prepareCmd()
    {
	if (flags.contains(Flags.ROOT))
	    return new String[]{
		"sudo",
		"setsid",
		"/bin/bash",
		"-c",
		"echo $$; " + this.command
	    };
	return new String[]{
	    "/bin/bash",
	    "-c",
	    "echo $$; " + this.command
	};
    }

    private void readOutput(BufferedReader r)
    {
	new Thread(()->{
		try {
		    try {
			String line = r.readLine();
			while (line != null)
			{
			    output.add(line);
			    listener.onOutputLine(line);
			    line = r.readLine();
			}
		    }
		    finally {
			r.close();
			synchronized(doneOutput) {
			    doneOutput.set(true);
			    doneOutput.notifyAll();
			}
		    }
		}
		catch(IOException e)
		{
		    Log.error(Linux.LOG_COMPONENT, "unable to read the output of the bash process '" + command + "': " + e.getClass().getName() + ": " + e.getMessage());
		    e.printStackTrace();
		}
	}).start();
    }

    private void readErrors(BufferedReader r)
    {
	new Thread(()->{
		try {
		    try {
			String line = r.readLine();
			while (line != null)
			{
			    errors.add(line);
			    listener.onErrorLine(line);
			    line = r.readLine();
			}
		    }
		    finally {
			r.close();
			synchronized(doneErrors) {
			    doneErrors.set(true);
			    doneErrors.notifyAll();
			}
		    }
		}
		catch(IOException e)
		{
		    Log.error(Linux.LOG_COMPONENT, "unable to read the errors of the bash process '" + command + "': " + e.getClass().getName() + ": " + e.getMessage());
		    e.printStackTrace();
		}
	}).start();
    }

        public String[] getOutput()
    {
	return output.toArray(new String[output.size()]);
    }

    public String[] getErrors()
    {
	return errors.toArray(new String[errors.size()]);
    }

    static public String escape(String value)
    {
	return "'" + value.replaceAll("'", "'\\\\''") + "'";
    }

    static public final class EmptyListener implements Listener
    {
	@Override public void onOutputLine(String line) {}
	@Override public void onErrorLine(String line) {}
	@Override public void onFinishing(int exitCode) {}
    }
}
