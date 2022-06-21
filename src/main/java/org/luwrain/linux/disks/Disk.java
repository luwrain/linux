
package org.luwrain.linux.disks;

import java.io.*;

import org.luwrain.core.*;
import org.luwrain.popups.*;

public final class Disk implements DisksPopup.Disk
{
    final String name;

    Disk(String name)
    {
	NullCheck.notNull(name, "name");
	this.name = name;
    }

    @Override public  File activate()
    {
	return new File(name);
    }

    @Override public boolean isActivated()
    {
	return false;
    }

    @Override public String toString()
    {
	return this.name;
    }
}
