/*
   Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.linux.lib;

import java.util.*;
import java.io.*;

import com.google.auto.service.*;

import org.luwrain.core.*;
import org.luwrain.linux.*;

import static org.luwrain.core.NullCheck.*;

@AutoService(JobLauncher.class)
public final class SysJob implements JobLauncher
{

    @Override public Job launch(Job.Listener listener, String[] args, String dir)
    {
	notNull(listener, "listener");
	notNullItems(args, "args");
	if (args.length == 0 || args[0].isEmpty())
	    return new ErrorJobInstance("sys", "No command");
	final Data data = new Data();
	final Job ins = new Job(){
		@Override public void stop() { if (data.stopProc != null) data.stopProc.run(); }
	    	@Override public String getInstanceName() { return args[0]; }
		@Override public Status getStatus() { return data.finished?Status.FINISHED:Status.RUNNING; }
		@Override public int getExitCode() { return data.exitCode; }
		@Override public boolean isFinishedSuccessfully() { return data.finished && data.exitCode == 0; }
		@Override public List<String> getInfo(String type)
		{
		    notEmpty(type, "type");
		    switch(type)
		    {
		    case "brief":
			return Arrays.asList(data.state);
		    case "main":
			return data.mlState;
		    default:
			return Arrays.asList();
		    }
		}
			    };
	final BashProcess p = new BashProcess(buildCmd(args), dir, EnumSet.noneOf(BashProcess.Flags.class), new BashProcess.Listener(){
		@Override public void onOutputLine(String line)
		{
		    data.mlState.add(line);
		    listener.onInfoChange(ins, "main", data.mlState);
		}
		@Override public void onErrorLine(String line)
		{
		    data.mlState.add(line);
		    listener.onInfoChange(ins, "main", data.mlState);
		}
		@Override public void onFinishing(int exitCode)
		{
		    data.finished = true;
		    data.exitCode = exitCode;
		    listener.onStatusChange(ins);
		}
	    });
	try {
	    p.run();
	}
	catch(IOException e)
	{
	    return new ErrorJobInstance(args[0], e.getMessage());
	}
	data.stopProc = ()->p.stop();
	return ins;
    }

    @Override public String getExtObjName()
    {
	return "sys";
    }

    @Override public Set<Flags> getJobFlags()
    {
	return EnumSet.noneOf(Flags.class);
    }

    static private String buildCmd(String[] args)
    {
	NullCheck.notNullItems(args, "args");
	if (args.length == 0)
	    throw new IllegalArgumentException("args can't be empty");
	if (args[0].isEmpty())
	    throw new IllegalArgumentException("args[0] can't be empty");
	final StringBuilder b = new StringBuilder();
	b.append(args[0]);
	for(int i = 1;i < args.length;i++)
	    b.append(" ").append(args[i]);
	return new String(b);
    }

    static private final class Data
    {
	boolean finished = false;
	int exitCode = -1;
	String state = "";
	final List<String> mlState = new ArrayList<>();
	Runnable stopProc = null;
    }
}
