/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

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

import java.nio.file.*;
import java.io.File;
import java.util.LinkedList;

import org.luwrain.hardware.Partition;

class MountedPartitions
{
    static public Partition[] getMountedPartitions()
    {
	final LinkedList<Partition> res = new LinkedList<Partition>();
	res.add(new Partition(Partition.ROOT, new File("/"), "/", true));
	final FileSystem fs = FileSystems.getDefault();
	Iterable<FileStore> stores = fs.getFileStores();
	for(FileStore store: stores)
	{
	    final String type = store.type();
	    if (type.equals("proc") ||
		type.equals("devpts") ||
		type.equals("sysfs") ||
		type.equals("tmpfs"))
		continue;
	    final String[] nameParts = store.toString().split(" \\(");
	    if (nameParts == null || nameParts.length < 1 || nameParts[0] == null)
		continue;
	    final String path = nameParts[0];
	    Partition l = null;
	    if (store.type().equals("cifs"))
		l = remote(store, path); else
		if (path.startsWith("/media"))
		    l = removable(store, path); else 
		    l = regular(store, path);
	    if (l != null)
		res.add(l);
	}
	return res.toArray(new Partition[res.size()]);
    }

    static public Partition removable(FileStore store, String path)
    {
	String[] parts = store.name().split("/");
	if (parts == null || parts.length < 1 || parts[0] == null)
	    return null;
	return new Partition(Partition.REMOVABLE, new File(path), parts[parts.length - 1], true);
    }

    static public Partition remote(FileStore store, String path)
    {
	return new Partition(Partition.REMOTE, new File(path), store.name(), true);
    }

    static public Partition regular(FileStore store, String path)
    {
	if (path.equals("/"))
	    return null;
	return new Partition(Partition.REGULAR, new File(path), path, true);
    }
}
