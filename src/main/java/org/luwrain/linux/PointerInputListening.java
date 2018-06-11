/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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


class PointerInputListening
{
    static private final String LOG_COMPONENT = Linux.LOG_COMPONENT;

    static private final int STEP_X = 30;
    static private final int STEP_Y = 30;

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final EventConsumer consumer;
    private final String fileName;
    private FutureTask task = null;

    private int posX = 0;
    private int posY = 0;

    PointerInputListening(EventConsumer consumer, String fileName)
    {
	NullCheck.notNull(consumer, "consumer");
	NullCheck.notEmpty(fileName, "fileName");
	this.consumer = consumer;
	this.fileName = fileName;
    }

    void run()
    {
	task = createTask();
	Log.debug(LOG_COMPONENT, "starting pointer input listening on " + fileName);
	executor.execute(task);
    }

    private FutureTask createTask()
    {
	return new FutureTask(()->{
		DataInputStream s = null;
		try {
		    s = new DataInputStream(new FileInputStream("/dev/input/mice"));
		    do {
			final int code = s.readUnsignedByte();
			final int x = s.readByte();
			final int y = s.readByte();
			if ((code & 1) > 0)
			    consumer.enqueueEvent(new KeyboardEvent(KeyboardEvent.Special.ENTER));
			if ((code & 2) > 0)
			    consumer.enqueueEvent(new KeyboardEvent(KeyboardEvent.Special.CONTEXT_MENU));
			if ((code & 8) > 0)
			    onOffset(x, y);
		    } while(true);
		}
		catch(Exception e)
		{
		    try {
			if (s != null)
			    s.close();
		    }
		    catch(IOException ee)
		    {
		    }
		    Log.error(LOG_COMPONENT, "unable to get pointer input events:" + e.getClass().getName() + ":" + e.getMessage());
		}
	}, null);
    }

    private void onOffset(int x, int y)
    {
	posX += x;
	posY += y;
	boolean step = false;
	do {
	    step = false;
	    while (posX > STEP_X)
	    {
		consumer.enqueueEvent(new KeyboardEvent(KeyboardEvent.Special.ARROW_RIGHT));
		posX -= STEP_X;
		step = true;
	    }
	    while (posY > STEP_Y)
	    {
		consumer.enqueueEvent(new KeyboardEvent(KeyboardEvent.Special.ARROW_UP));
		posY -= STEP_Y;
		step = true;
	    }
	    while (posX < -1 * STEP_X)
	    {
		consumer.enqueueEvent(new KeyboardEvent(KeyboardEvent.Special.ARROW_LEFT));
		posX += STEP_X;
		step = true;
	    }
	    while (posY < -1 * STEP_Y)
	    {
		consumer.enqueueEvent(new KeyboardEvent(KeyboardEvent.Special.ARROW_DOWN));
		posY += STEP_Y;
		step = true;
	    }
	} while(step);
    }
}
