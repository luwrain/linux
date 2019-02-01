/*
   Copyright 2012-2019 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import org.luwrain.base.*;
import org.luwrain.core.*;

final class ScriptsCommandLineTool implements CommandLineTool
{
    private final Scripts scripts;
    private final String name;

    ScriptsCommandLineTool(PropertiesBase props, String name)
    {
	NullCheck.notNull(props, "props");
	NullCheck.notEmpty(name, "name");
	this.scripts = new Scripts(props);
	this.name = name;
    }

    @Override public String getExtObjName()
    {
	return name;
    }

    @Override public Set<Flags> getToolFlags()
    {
	return EnumSet.noneOf(Flags.class);	
    }

    @Override public Instance launch(Listener listener, String[] args)
    {
	NullCheck.notNullItems(args, "args");
	return null;
    }

    static private class Instance implements org.luwrain.base.CommandLineTool.Instance
    {
	private final String name;
	private final Listener listener;
	private final Scripts scripts;
	private final String cmd;
	private final boolean sudo;
	private boolean res = false;
	private boolean finished = false;

	Instance(String name, Listener listener,
		 Scripts scripts, String cmd, boolean sudo)
	{
	    NullCheck.notNull(name, "name");
	    NullCheck.notNull(scripts, "scripts");
	    NullCheck.notEmpty(cmd, "cmd");
	    this.name = name;
	    this.listener = listener;
	    this.scripts = scripts;
	    this.cmd = cmd;
	    this.sudo = sudo;
	}

	void run()
	{
	    new Thread(()->{
		    res = scripts.runSync(cmd, sudo);
		    finished = true;
		    listener.onStatusChange(Instance.this);
	    }).start();
	}

	@Override public String getInstanceName()
	{
	    return name;
	}

	@Override public Status getStatus()
	{
	    return finished?Status.FINISHED:Status.RUNNING;
	}

	@Override public int getExitCode()
	{
	    return res?0:1;
	}

	@Override public boolean isFinishedSuccessfully()
	{
	    return res;
	}

	@Override public void stop()
	{
	}

	@Override public String getSingleLineState()
	{
	    return "";
	}

	@Override public String[] getMultilineState()
	{
	    return new String[0];
	}

	@Override public String[] getNativeState()
	{
	    return new String[0];
	}
    }
}
