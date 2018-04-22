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

import java.io.*;
import java.util.concurrent.*;
import org.luwrain.core.events.*;

import org.luwrain.base.*;
import org.luwrain.core.*;

class FifoInputListening
{
    static private final String LOG_COMPONENT = Linux.LOG_COMPONENT;

    static private final String COMMAND_PREFIX = "command ";
        static private final String UNIREF_PREFIX = "uniref ";

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Luwrain luwrain;
    private final String fileName;
    private FutureTask task = null;

    FifoInputListening(Luwrain luwrain, String fileName)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notEmpty(fileName, "fileName");
	this.luwrain = luwrain;
	this.fileName = fileName;
    }

    void run()
    {
	task = createTask();
	Log.debug(LOG_COMPONENT, "starting fifo input listening on " + fileName);
	executor.execute(task);
    }

    private FutureTask createTask()
    {
	return new FutureTask(()->{
		BufferedReader reader = null;
		FileOutputStream output = null;
		try {
		    reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
		    output = new FileOutputStream(fileName);
		    String line = null;
		    do {
			line = reader.readLine();
			if (line != null)
			    processLine(line);
		    } while(line != null);
		}
		catch(Exception e)
		{
		    try {
			if (reader != null)
			    reader.close();
			if (output != null)
			    output.close();
		    }
		    catch(IOException ee)
		    {
		    }
		    Log.error(LOG_COMPONENT, "unable to get pointer input events:" + e.getClass().getName() + ":" + e.getMessage());
		}
	}, null);
    }

    private void processLine(String line)
    {
	NullCheck.notNull(line, "line");
	if (line.startsWith(COMMAND_PREFIX))
	{
	    final String command = line.substring(COMMAND_PREFIX.length()).trim();
	    if (!command.isEmpty())
	luwrain.runUiSafely(()->{
		luwrain.runCommand(command);
	    });
	return;
	}
		if (line.startsWith(UNIREF_PREFIX))
	{
	    final String uniref = line.substring(UNIREF_PREFIX.length()).trim();
	    if (!uniref.isEmpty())
	luwrain.runUiSafely(()->{
		luwrain.openUniRef(uniref);
	    });
	return;
	}
    }
}
