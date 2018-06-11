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
//import java.nio.file.*;
import java.util.*;

import org.luwrain.base.*;
import org.luwrain.core.*;
import org.luwrain.linux.*;

public final class Mounting
{
    public interface MountPointConstructor
    {
	File constructMountPoint(File devFile);
    }

    private final MountPointConstructor mountPointConstructor;
    private final Scripts scripts;

    public Mounting(PropertiesBase propertiesBase, MountPointConstructor mountPointConstructor)
    {
	NullCheck.notNull(propertiesBase, "propertiesBase");
	NullCheck.notNull(mountPointConstructor, "mountPointConstructor");
	this.mountPointConstructor = mountPointConstructor;
	this.scripts = new Scripts(propertiesBase);
    }

public File[] mountAll(Disk disk)
    {
	NullCheck.notNull(disk, "disk");
	final List<File> res = new LinkedList();
	    final Partition[] parts = disk.getPartitions();
	    for(Partition p: parts)
	    {
		final File mountPoint = mountPointConstructor.constructMountPoint(p.getDevFile());
		if (mount(p.getDevFile(), mountPoint))
		    res.add(mountPoint);
	    }
	    if (!res.isEmpty())
		return res.toArray(new File[res.size()]);
	    final File mountPoint = mountPointConstructor.constructMountPoint(disk.getDevFile());
	    if (mount(disk.getDevFile(), mountPoint))
    return new File[]{mountPoint};
	    return new File[0];
    }

public int umountAllPartitions(Disk disk)
    {
	return 0;
    }

    private boolean mount(File dev, File mountPoint)
    {
	NullCheck.notNull(dev, "dev");
	NullCheck.notNull(mountPoint, "mountPoint");
	return scripts.runSync(Scripts.ID.MOUNT, new String[]{dev.getAbsolutePath(), mountPoint.getAbsolutePath()}, true);
    }

    private boolean umount(String mountPoint)
    {
	NullCheck.notEmpty(mountPoint, "mountPoint");
	return scripts.runSync(Scripts.ID.UMOUNT, new String[]{mountPoint}, true);
    }
}
