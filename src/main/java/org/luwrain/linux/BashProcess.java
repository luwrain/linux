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

    private final String command;
    private final Set<Flags> flags;
    private Process p = null;
    private final AtomicBoolean done = new AtomicBoolean(false);
    private int pid = -1;
    private int exitCode = -1;
    private final ArrayList<String> output = new ArrayList();
    private final ArrayList<String> errors = new ArrayList();

    public BashProcess(String command, Set<Flags> flags)
    {
	NullCheck.notEmpty(command, "command");
	NullCheck.notNull(flags, "flags");
	this.command = command;;
	this.flags = flags;
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
	    return exitCode;
	}
	catch(InterruptedException e)
	{
	    Thread.currentThread().interrupt();
	    return -1;
	}
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
		line = r.readLine();
	    }
	}
	finally {
	    r.close();
	}
		}
		catch(IOException e)
		{
		    Log.error(Linux.LOG_COMPONENT, "unable to read the output of the bash process '" + command + "': " + e.getClass().getName() + ": " + e.getMessage());
		    e.printStackTrace();
		}
	}).start();
    }

    private String[] prepareCmd()
    {
	return new String[0];
    }

    							   public String[] getOUtput()
							   {
							       return output.toArray(new String[output.size()]);
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
		line = r.readLine();
	    }
	}
	finally {
	    r.close();
	}
		}
		catch(IOException e)
		{
		    Log.error(Linux.LOG_COMPONENT, "unable to read the errors of the bash process '" + command + "': " + e.getClass().getName() + ": " + e.getMessage());
		    e.printStackTrace();
		}
	}).start();
    }



							   public String[] getErrors()
							   {
							       return errors.toArray(new String[errors.size()]);
							   }
}
