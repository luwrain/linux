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

package org.luwrain.linux.script;

import java.io.*;
import java.util.*;

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

import org.luwrain.core.*;
import org.luwrain.script.core.*;
import org.luwrain.script2.*;
import org.luwrain.linux.*;

final class LinuxObj implements ProxyObject
{
    final Luwrain luwrain;

    LinuxObj(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
    }

    @Override public Object getMember(String name)
    {
	if (name == null)
	    return null;
	switch(name)
	{
	case "run":
	    return(ProxyExecutable)this::run;
	default:
	    return null;
	}
    }

    @Override public boolean hasMember(String name)
    {
	switch(name)
	{
	case "run":
	    return true;
	default:
	    return false;
	}
    }

    @Override public Object getMemberKeys()
    {
	return new String[]{
	    "run",
	};
    }

    @Override public void putMember(String name, Value value)
    {
	throw new RuntimeException("The linux object doesn't support updating of its variables");
    }

    private Object run(Value[] args)
    {
	if (!ScriptUtils.notNullAndLen(args, 1))
	    throw new ScriptException("Linux.run() takes exactly one argument with the command line to run");
	final String command = ScriptUtils.asString(args[0]);
	if (command == null || command.isEmpty())
	    throw new ScriptException("Linux.run() takes a non-empty string as the first argument");
	final BashProcess p = new BashProcess(command);
	try {
	    p.run();
	}
	catch(IOException e)
	{
	    throw new ScriptException(e);
	}
	return new BashProcessObj(p);
    }
}
