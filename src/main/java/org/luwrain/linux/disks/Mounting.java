
package org.luwrain.linux.disks;

import java.io.*;
import java.nio.file.*;
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

public Base[] mountAll(Disk disk)
    {
	NullCheck.notNull(disk, "disk");
	final List<Base> res = new LinkedList();
	    final Partition[] parts = disk.getPartitions();
	    for(Partition p: parts)
		if (mount(p.getDevFile(), mountPointConstructor.constructMountPoint(p.getDevFile())))
			res.add(p);
	    if (!res.isEmpty())
		return res.toArray(new Base[res.size()]);
	    if (mount(disk.getDevFile(), mountPointConstructor.constructMountPoint(disk.getDevFile())))
    return new Base[]{disk};
	    return new Base[0];
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
