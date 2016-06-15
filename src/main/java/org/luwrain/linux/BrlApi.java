
package org.luwrain.linux;

import java.util.concurrent.*;

import org.a11y.BrlAPI.*;
import org.a11y.BrlAPI.Constants;

import org.luwrain.core.*;
import org.luwrain.core.events.*;import org.a11y.BrlAPI.Constants;

public class BrlApi implements org.luwrain.os.Braille
{
    static private final int STEP_DELAY = 1000;

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
		eventConsumer.enqueueEvent(new KeyboardEvent('a'));
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
	return new FutureTask(()->{
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

	if (key.getType() == Constants.KEY_TYPE_CMD)
	    {
		switch(key.getCommand())
		    {
		    case Constants.KEY_CMD_CHRLT:
			break;
		    }
	    }
	    }
}
