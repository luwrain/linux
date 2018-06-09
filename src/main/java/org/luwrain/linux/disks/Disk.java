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

package org.luwrain.linux.disks;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;

public final class Disk extends Base
{
    public Disk(File path)
    {
	super(path);
    }

    public Partition[] getPartitions()
    {
	final List<Partition> res = new LinkedList();
	final File[] files = path.listFiles();
	if (files == null)
	    return new Partition[0];
	for(File f: files)
	{
	    if (!f.isDirectory())
		continue;
	    final Partition part = new Partition(f);
	    if (part.isPartition())
		res.add(part);
	}
	return res.toArray(new Partition[res.size()]);
    }
}
