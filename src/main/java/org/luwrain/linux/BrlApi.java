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

import java.util.concurrent.*;

import org.a11y.BrlAPI.*;
import org.a11y.BrlAPI.Constants;

import org.luwrain.core.*;
import org.luwrain.core.events.*;import org.a11y.BrlAPI.Constants;

public class BrlApi implements Braille
{
    static private final int STEP_DELAY = 10;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private FutureTask task = null;

    private Brlapi brlApi = null;
    private EventConsumer eventConsumer;
    private String driverName;
    private DisplaySize displaySize;

    @Override public InitResult init(EventConsumer eventConsumer)
    {
	NullCheck.notNull(eventConsumer, "eventConsumer");
	this.eventConsumer = eventConsumer;
	final ConnectionSettings settings = new ConnectionSettings();
	settings.host = "";
	try {
	    Log.debug("linux", "connecting to BrlAPI");
brlApi = new Brlapi(settings);
	    Log.debug("linux", "brlTTY connected: fd=" + brlApi.getFileDescriptor());
	    Log.debug("linux", "using key file for braille:" + brlApi.getAuth());
	    Log.debug("linux", "braille driver is " + brlApi.getDriverName());
	    driverName = brlApi.getDriverName();
displaySize = brlApi.getDisplaySize();
	    Log.debug("linux", "braille display size is " + displaySize.getWidth() + "x" + displaySize.getHeight());
brlApi.enterTtyModeWithPath(new int[0]);
task = createTask();
executor.execute(task);
Log.debug("braille", "braile keys service started");
Log.info("linux", "braille supported successfully initialized");
						return new InitResult();
	}
	catch(UnsatisfiedLinkError e)
	{
	    Log.error("linux", "unable to connect to brltty:" + e.getMessage());
	    e.printStackTrace();
	    brlApi = null;
	    return new InitResult(InitResult.Type.FAILURE, e.getMessage());
	}
	catch (java.lang.Exception e)
	{
	    Log.error("linux", "unable to connect to brltty:" + e.getMessage());
	    e.printStackTrace();
	    brlApi = null;
	    return new InitResult(e);
	}
    }

    @Override synchronized public void writeText(String text)
    {
	NullCheck.notNull(text, "text");
	if (brlApi == null)
	    return;
	brlApi.writeText(text);
    }

    @Override public String getDriverName()
    {
	return driverName != null?driverName:"";
    }

    @Override public int getDisplayWidth()
    {
	return displaySize.getWidth();
    }

    @Override public int getDisplayHeight()
    {
	return displaySize.getHeight();
    }

    synchronized private void readKeys()
    {
	if (brlApi == null)
	    return;
	try {
	    final long key = brlApi.readKey(false);
	    if (key != -1)
		onKey(new Key(key));
	    /*
	    {
		eventConsumer.enqueueEvent(new InputEvent('a'));
	    }
	    */
	}
	catch(java.lang.Exception e)
	{
	    	    Log.error("linux", "unable to read a key from brlapi:" + e.getClass().getName() + ":" + e.getMessage());
	    e.printStackTrace();
	}
    }

    private FutureTask createTask()
    {
	return new FutureTask<>(()->{
	    while(!Thread.currentThread().isInterrupted())
	    {
		try {
		    Thread.sleep(STEP_DELAY);
		    readKeys();
		}
		catch(InterruptedException e)
		{
		    Thread.currentThread().interrupt();
		}
	    }
	}, null);
    }

    private void onKey(Key key)
    {

	
	Log.debug("linux", "processing brltty key " + key.getCode());
	Log.debug("linux", "type is " + key.getType());
	Log.debug("linux", "argument "+ key.getArgument());
		Log.debug("linux", "flags "+ key.getFlags());

		if (key.getType() == 0)
		    {
			switch (key.getArgument())
			    {
			    case 32:
					    eventConsumer.enqueueEvent(new InputEvent(' '));
					    break;


			    case 13:
					    eventConsumer.enqueueEvent(new InputEvent(InputEvent.Special.ENTER));
					    break;

			    case 8:					    
					    eventConsumer.enqueueEvent(new InputEvent(InputEvent.Special.BACKSPACE));
					    break;

			    default:
				if ((key.getFlags() & 4) > 0)
				    eventConsumer.enqueueEvent(new InputEvent(Character.toUpperCase((char)key.getArgument()))); else
		    eventConsumer.enqueueEvent(new InputEvent((char)key.getArgument()));
			    }
		    return;
		    }
		if (key.getType() == Constants.KEY_TYPE_CMD)
	    {
		switch(key.getCommand())
		    {
		    case 30:
			eventConsumer.enqueueEvent(new InputEvent(InputEvent.Special.ESCAPE));
break;

		    case 29:
			eventConsumer.enqueueEvent(new InputEvent(InputEvent.Special.WINDOWS));
break;


		    case Constants.KEY_CMD_CHRLT:
						eventConsumer.enqueueEvent(new InputEvent(InputEvent.Special.ARROW_LEFT));
			break;
		    case Constants.KEY_CMD_CHRRT:
									eventConsumer.enqueueEvent(new InputEvent(InputEvent.Special.ARROW_RIGHT));
			break;
					    case Constants.KEY_CMD_LNEND:
						 			eventConsumer.enqueueEvent(new InputEvent(InputEvent.Special.END));
									break;
		    case Constants.KEY_CMD_LNBEG:
 			eventConsumer.enqueueEvent(new InputEvent(InputEvent.Special.HOME));
			break;
			
			/*
					    case Constants.KEY_CMD_CHRRT:
									eventConsumer.enqueueEvent(new InputEvent(InputEvent.Special.ARROW_RIGHT));
			break;
			
		    case Constants.KEY_CMD_LNBEG:
 			eventConsumer.enqueueEvent(new InputEvent(InputEvent.Special.END));
			*/
		    case Constants.KEY_CMD_LNDN:
			eventConsumer.enqueueEvent(new InputEvent(InputEvent.Special.ARROW_DOWN));
			break;

					    case Constants.KEY_CMD_LNUP:
			eventConsumer.enqueueEvent(new InputEvent(InputEvent.Special.ARROW_UP));
			break;

			
		    case Constants.KEY_CMD_WINDN:
			break;
		    case Constants.KEY_CMD_WINUP:
			break;
		    case Constants.KEY_SYM_BACKSPACE:
			break;
		    case Constants.KEY_SYM_DELETE:
			break;
		    case Constants.KEY_SYM_DOWN:
			break;
		    case Constants.KEY_SYM_END:
			break;
		    case Constants.KEY_SYM_ESCAPE:
			break;
		    case Constants.KEY_SYM_F1:
			break;
		    case Constants.KEY_SYM_F10:
			break;
		    case Constants.KEY_SYM_F11:
			break;
		    case Constants.KEY_SYM_F12:
			break;
		    case Constants.KEY_SYM_F13:
			break;
		    case Constants.KEY_SYM_F14:
			break;
		    case Constants.KEY_SYM_F15:
			break;
		    case Constants.KEY_SYM_F16:
			break;
		    case Constants.KEY_SYM_F17:
			break;
		    case Constants.KEY_SYM_F18:
			break;
		    case Constants.KEY_SYM_F19:
			break;
		    case Constants.KEY_SYM_F2:
			break;
		    case Constants.KEY_SYM_F20:
			break;
		    case Constants.KEY_SYM_F21:
			break;
		    case Constants.KEY_SYM_F22:
			break;
		    case Constants.KEY_SYM_F23:
			break;
		    case Constants.KEY_SYM_F24:
			break;
		    case Constants.KEY_SYM_F25:
			break;
		    case Constants.KEY_SYM_F26:
			break;
		    case Constants.KEY_SYM_F27:
			break;
		    case Constants.KEY_SYM_F28:
			break;
		    case Constants.KEY_SYM_F29:
			break;
		    case Constants.KEY_SYM_F3:
			break;
		    case Constants.KEY_SYM_F30:
			break;
		    case Constants.KEY_SYM_F31:
			break;
		    case Constants.KEY_SYM_F32:
			break;
		    case Constants.KEY_SYM_F33:
			break;
		    case Constants.KEY_SYM_F34:
			break;
		    case Constants.KEY_SYM_F35:
			break;
		    case Constants.KEY_SYM_F4:
			break;
		    case Constants.KEY_SYM_F5:
			break;
		    case Constants.KEY_SYM_F6:
			break;
		    case Constants.KEY_SYM_F7:
			break;
		    case Constants.KEY_SYM_F8:
			break;
		    case Constants.KEY_SYM_F9:
			break;
		    case Constants.KEY_SYM_HOME:
			eventConsumer.enqueueEvent(new InputEvent(InputEvent.Special.HOME));
			break;
		    case Constants.KEY_SYM_INSERT:
			break;
		    case Constants.KEY_SYM_LEFT:
						eventConsumer.enqueueEvent(new InputEvent(InputEvent.Special.ARROW_LEFT));
			break;
		    case Constants.KEY_SYM_LINEFEED:
			break;
		    default:
			Log.debug("linux", "command is " + key.getCommand());
		    }
	    }
    }
}
