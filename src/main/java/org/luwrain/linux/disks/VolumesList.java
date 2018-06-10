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

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.luwrain.core.*;

public final class VolumesList
{
    static final String MEDIA_DIR = "/media";

    public Volume[] getVolumes()
    {
	final FileSystem fileSystem = FileSystems.getDefault();
	final List<Volume> remotes = new LinkedList();
	final List<Volume> removables = new LinkedList();
	final List<Volume> regulars = new LinkedList();
	final List<Volume> other = new LinkedList();
	Iterable<FileStore> stores = fileSystem.getFileStores();
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
	    if (nameParts.length == 0)
		continue;
	    final String path = nameParts[0];
	    final Volume l;
	    if (store.type().equals("cifs"))
		l = newRemote(store, path); else
		if (path.startsWith(MEDIA_DIR))
		    l = newRemovable(store, path); else 
		    l = newRegular(store, path);
	    if (l != null)
		switch(l.type)
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
	final List<Volume> res = new LinkedList();
	for(Volume v: removables)
	    res.add(v);
	for(Volume v: remotes)
	    res.add(v);
	for(Volume v: regulars)
	    res.add(v);
	res.add(new Volume(Volume.Type.ROOT, new File("/"), "/"));
	return res.toArray(new Volume[res.size()]);
    }

    private Volume newRemovable(FileStore store, String path)
    {
	NullCheck.notNull(store, "store");
	NullCheck.notEmpty(path, "path");
	final String[] parts = store.name().split("/");
	if (parts.length == 0)
	    return null;
	return new Volume(Volume.Type.REMOVABLE, new File(path), parts[parts.length - 1]);
    }

    private Volume newRemote(FileStore store, String path)
    {
	NullCheck.notNull(store, "store");
	NullCheck.notEmpty(path, "path");
	return new Volume(Volume.Type.REMOTE, new File(path), store.name());
    }

    private Volume newRegular(FileStore store, String path)
    {
	NullCheck.notNull(store, "store");
	NullCheck.notEmpty(path, "path");
	if (path.equals("/"))
	    return null;
	return new Volume(Volume.Type.REGULAR, new File(path), path);
    }
}
