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

package org.luwrain.linux.disks;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.util.*;

public final class Partition extends Base
{
    public Partition(File path)
    {
	super(path);
    }

    public boolean isPartition()
    {
	final File partFile = new File(path, "partition");
	if (!partFile.exists() || partFile.isDirectory())
	    return false;
	try {
	    final String text = FileUtils.readTextFileSingleString(partFile, "UTF-8");
	    return text.equals("1\n");
	}
	catch(IOException e)
	{
	    return false;
	}
    }
}
