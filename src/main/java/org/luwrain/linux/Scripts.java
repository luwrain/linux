/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

class Scripts
{
    static private String SCRIPTS_DIR_PROP = "luwrain.dir.scripts";

    enum ID {
	MOUNT,
	UMOUNT,
	SHUTDOWN,
	REBOOT,
	SUSPEND,
    };

    private File scriptsDir;

    Scripts(org.luwrain.base.CoreProperties props)
    {
	NullCheck.notNull(props, "props");
	final File file = props.getFileProperty(SCRIPTS_DIR_PROP);
	if (file == null)
	    throw new RuntimeException("No \'" + SCRIPTS_DIR_PROP + "\' property required for scripts processing");
	this.scriptsDir = file;
    }

    boolean runSync(ID id, boolean sudo)
    {
	NullCheck.notNull(id, "id");
	return runSync(translateId(id), sudo);
    }

    boolean runSync(String scriptName, boolean sudo)
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

    boolean runSync(ID id, String[] args, boolean sudo)
    {
	NullCheck.notNull(id, "id");
	NullCheck.notNullItems(args, "args");
	return runSync(translateId(id), args, sudo);
    }

    boolean runSync(String scriptName, String[] args, boolean sudo)
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

    boolean exists(String scriptName)
    {
	NullCheck.notEmpty(scriptName, "scriptName");
	final File file = new File(scriptsDir, scriptName);
	return file.exists() && !file.isDirectory();
    }

        boolean exists(ID id)
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
