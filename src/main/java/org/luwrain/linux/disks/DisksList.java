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

public final class DisksList
{
    static public final File SYS_BLOCK_DIR = new File("/sys/block");

    static public Disk[] getRemovableDisks()
    {
	final List<Disk> res = new LinkedList();
	final File[] files = SYS_BLOCK_DIR.listFiles();
	if (files == null)
	    return new Disk[0];
	for(File f: files )
	    if (f.exists() && f.isDirectory())
	    {
		final Disk disk = new Disk(f);
		if (disk.isRemovable())
		    res.add(disk);
	    }
	return res.toArray(new Disk[res.size()]);
    }
}
