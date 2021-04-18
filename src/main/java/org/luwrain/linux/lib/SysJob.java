
package org.luwrain.linux.lib;

import java.util.*;
import java.io.*;

import org.luwrain.base.*;
import org.luwrain.core.*;
import org.luwrain.linux.*;

public final class SysJob implements Job
{

    @Override public Instance launch(Listener listener, String[] args)
    {
	NullCheck.notNull(listener, "listener");
	NullCheck.notNullItems(args, "args");
	if (args.length == 0 || args[0].isEmpty())
	    return new ErrorJob("sys", "No command");
	final BashProcess p = new BashProcess(args[0]);
	try {
	    p.run();
	}
	catch(IOException e)
	{
	    return new ErrorJob(args[0], e.getMessage());
	}
	return new Instance(){
	    @Override public void stop()
	    {
	    }

	    	@Override public String getInstanceName()
	    {
		return "";
	    }
	@Override public Status getStatus()
	    {
		return Status.FINISHED;
	    }
	@Override public int getExitCode()
	    {
		return 0;
	    }
	@Override public boolean isFinishedSuccessfully()
	    {
		return false;
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
	};
    }

        @Override public String getExtObjName()
    {
	return "sys";
    }


    @Override public Set<Flags> getJobFlags()
    {
	return EnumSet.noneOf(Flags.class);
    }

    
}
