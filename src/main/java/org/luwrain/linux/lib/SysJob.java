
package org.luwrain.linux.lib;

import java.util.*;

import org.luwrain.base.*;
import org.luwrain.core.*;

public final class SysJob implements Job
{

    @Override public String getExtObjName()
    {
	return "sys";
    }

        @Override public Instance launch(Listener listener, String[] args)
    {
	return new Instance(){
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
	@Override public void stop()
	    {
	    }
	};
    }

    @Override public Set<Flags> getJobFlags()
    {
	return EnumSet.noneOf(Flags.class);
    }

    
}
