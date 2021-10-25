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
import java.io.*;

import org.luwrain.core.*;

public final class Scripts
{
    static private String SCRIPTS_DIR_PROP = "luwrain.dir.scripts";

    public enum ID {
	BATTERY_PERCENT,
	INSTALL,
	MAN_SEARCH,
	MAN_PAGE,
	MOUNT,
	UMOUNT,
	SHUTDOWN,
	REBOOT,
	SUSPEND,
	WIFI_CONNECT,
	WIFI_DISCONNECT,
	WIFI_SCAN,
    };

    private File scriptsDir;

    public Scripts(PropertiesBase props)
    {
	NullCheck.notNull(props, "props");
	final File file = props.getFileProperty(SCRIPTS_DIR_PROP);
	if (file == null)
	    throw new RuntimeException("No \'" + SCRIPTS_DIR_PROP + "\' property required for scripts processing");
	this.scriptsDir = file;
    }

    public boolean runSync(ID id, boolean sudo)
    {
	NullCheck.notNull(id, "id");
	return runSync(translateId(id), sudo);
    }

    public boolean runSync(String scriptName, boolean sudo)
    {
	NullCheck.notNull(scriptName, "scriptName");
	try {
	    final Process p = sudo?
	    new ProcessBuilder("sudo", getScriptPath(scriptName)).start():
	    new ProcessBuilder(getScriptPath(scriptName)).start();
	    p.getOutputStream().close();
	    p.waitFor();
	    return p.exitValue() == 0;
	}
	catch(InterruptedException e)
	{
	    Thread.currentThread().interrupt();
	    return false;
	}
	catch(IOException e)
	{
	    Log.error(Linux.LOG_COMPONENT, "unable to run the script \'" + scriptName + "\':" + e.getClass().getName() + ":" + e.getMessage());
	    return false;
	}
    }

    public boolean runSync(ID id, String[] args, boolean sudo)
    {
	NullCheck.notNull(id, "id");
	NullCheck.notNullItems(args, "args");
	return runSync(translateId(id), args, sudo);
    }

    public boolean runSync(String scriptName, String[] args, boolean sudo)
    {
	NullCheck.notNull(scriptName, "scriptName");
	NullCheck.notNullItems(args, "args");
	final List<String> cmd = new LinkedList<String>();
	if (sudo)
	    cmd.add("sudo");
	cmd.add(getScriptPath(scriptName));
	for(String s: args)
	    cmd.add(s);
	try {
	    final Process p = new ProcessBuilder(cmd.toArray(new String[cmd.size()])).start();
	    p.getOutputStream().close();
	    p.waitFor();
	    final int res = p.exitValue();
	    Log.debug("linux", "exit code is " + res);
	    return res == 0;
	}
	catch(InterruptedException e)
	{
	    Thread.currentThread().interrupt();
	    return false;
	}
	catch(IOException e)
	{
	    Log.error(Linux.LOG_COMPONENT, "unable to run the script \'" + scriptName + "\':" + e.getClass().getName() + ":" + e.getMessage());
	    return false;
	}
    }

    public Process runAsync(ID id, String[] args, boolean sudo)
    {
	NullCheck.notNull(id, "id");
	NullCheck.notNullItems(args, "args");
	return runAsync(translateId(id), args, sudo);
    }

    public Process runAsync(String scriptName, String[] args, boolean sudo)
    {
	NullCheck.notNull(scriptName, "scriptName");
	NullCheck.notNullItems(args, "args");
	final List<String> cmd = new ArrayList<>();
	if (sudo)
	    cmd.add("sudo");
	cmd.add(getScriptPath(scriptName));
	for(String s: args)
	    cmd.add(s);
	try {
	    return new ProcessBuilder(cmd.toArray(new String[cmd.size()])).start();
	}
	catch(IOException e)
	{
	    Log.error(Linux.LOG_COMPONENT, "unable to run the script \'" + scriptName + "\':" + e.getClass().getName() + ":" + e.getMessage());
	    return null;
	}
    }

    public String runSingleLineOutput(ID id, String[] args, boolean sudo)
    {
	NullCheck.notNull(id, "id");
	NullCheck.notNullItems(args, "args");
	final Process p = runAsync(id, args, sudo);
	if (p == null)
	    return null;
			final StringBuilder b = new StringBuilder();
	try {
	p.getOutputStream().close();
		final BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = r.readLine();
		while (line != null)
		{
		    b.append(line);
		    line = r.readLine();
		}
	}
	catch(IOException e)
	{
	    return null;
	}
	try {
		p.waitFor();
	}
	catch(InterruptedException e)
	{
	    Thread.currentThread().interrupt();
	}
		if (p.exitValue() == 0)
		    return new String(b);
		return null;
    }

    public boolean exists(String scriptName)
    {
	NullCheck.notEmpty(scriptName, "scriptName");
	final File file = new File(scriptsDir, scriptName);
	return file.exists() && !file.isDirectory();
    }

    public boolean exists(ID id)
    {
	NullCheck.notNull(id, "id");
	final File file = new File(scriptsDir, translateId(id));
	return file.exists() && !file.isDirectory();
    }

    private String getScriptPath(String scriptName)
    {
	NullCheck.notEmpty(scriptName, "scriptName");
	return new File(scriptsDir, scriptName).toString();
    }

    private String translateId(ID id)
    {
	NullCheck.notNull(id, "id");
	return "lwr-" + id.toString().toLowerCase().replaceAll("_", "-");
    }
}
