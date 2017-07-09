/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import java.util.*;
import java.nio.file.*;
import java.io.File;

import org.luwrain.base.*;
import org.luwrain.core.*;

class MountedPartitions
{
    static Partition[] getMountedPartitions()
    {
	final List<Partition> remotes = new LinkedList<Partition>();
	final List<Partition> removables = new LinkedList<Partition>();
	final List<Partition> regulars = new LinkedList<Partition>();
	final List<Partition> other = new LinkedList<Partition>();
	final FileSystem fs = FileSystems.getDefault();
	Iterable<FileStore> stores = fs.getFileStores();
	for(FileStore store: stores)
	{
	    final String type = store.type();
	    if (!type.startsWith("ext") &&
		!type.equals("iso9660") &&
		!type.equals("vfat") &&
		!type.equals("fat") &&
		!type.equals("cifs"))
		continue;
	    final String[] nameParts = store.toString().split(" \\(");
	    if (nameParts.length < 1 || nameParts[0] == null)
		continue;
	    final String path = nameParts[0];
	    Partition l = null;
	    if (store.type().equals("cifs"))
		l = remote(store, path); else
		if (path.startsWith(Constants.MEDIA_DIR))
		    l = removable(store, path); else 
		    l = regular(store, path);
	    if (l != null)
		switch(l.getPartType())
		{
		case REMOVABLE:
		    removables.add(l);
		    break;
		case REMOTE:
		    remotes.add(l);
		    break;
		case REGULAR:
		    regulars.add(l);
		    break;
		default:
		    other.add(l);
		}
	}
	final List<Partition> res = new LinkedList<Partition>();
	for(Partition p: removables)
	    res.add(p);
	for(Partition p: remotes)
	    res.add(p);
	for(Partition p: regulars)
	    res.add(p);
	res.add(new PartitionImpl(Partition.Type.ROOT, new File("/"), "/", true));
	return res.toArray(new Partition[res.size()]);
    }

    static private Partition removable(FileStore store, String path)
    {
	NullCheck.notNull(store, "store");
	NullCheck.notEmpty(path, "path");
	final String[] parts = store.name().split("/");
	if (parts == null || parts.length < 1 || parts[0] == null)
	    return null;
	return new PartitionImpl(Partition.Type.REMOVABLE, new File(path), parts[parts.length - 1], true);
    }

    static private Partition remote(FileStore store, String path)
    {
	NullCheck.notNull(store, "store");
	NullCheck.notEmpty(path, "path");
	return new PartitionImpl(Partition.Type.REMOTE, new File(path), store.name(), true);
    }

    static private Partition regular(FileStore store, String path)
    {
	NullCheck.notNull(store, "store");
	NullCheck.notEmpty(path, "path");
	if (path.equals("/"))
	    return null;
	return new PartitionImpl(Partition.Type.REGULAR, new File(path), path, true);
    }
}
