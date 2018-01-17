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

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.luwrain.core.*;
import org.luwrain.base.hardware.*;

final class Hardware implements org.luwrain.base.hardware.Hardware
{
    static private final String LOG_COMPONENT = Linux.LOG_COMPONENT;

        static final String MEDIA_DIR = "/media";

    private final File sysBlockDir;
    private final File pciDevDir;
    private final PciIds pciIds = new PciIds();
    private AudioMixer mixer;
    private final Scripts scripts;
    private final org.luwrain.base.CoreProperties props;
    private final Path scriptsDir;

    Hardware(org.luwrain.base.CoreProperties props)
    {
	NullCheck.notNull(props, "props");
	this.scripts = new Scripts(props);
	this.props = props;
	final File pciidsFile = props.getFileProperty("luwrain.linux.pciids");
	if (pciidsFile != null)
	pciIds.load(pciidsFile);
	this.scriptsDir = props.getFileProperty("luwrain.dir.scripts").toPath();
	this.sysBlockDir = props.getFileProperty("luwrain.linux.sysblockdir");
	if (sysBlockDir == null)
	    Log.warning(LOG_COMPONENT, "no \'luwrain.linux.sysblockdir\' property");
	this.pciDevDir = props.getFileProperty("luwrain.linux.pcidevdir");
	if (pciDevDir == null)
	    Log.warning(LOG_COMPONENT, "no \'luwrain.linux.pcidevdir\' property");
    }

    @Override public SysDevice[] getSysDevices()
    {
	if (pciDevDir == null)
	    return new SysDevice[0];
	final List<SysDevice> devices = new LinkedList<SysDevice>();
	final File[] pciDirs = pciDevDir.listFiles();
	for(File d: pciDirs)
	{
	    final String classStr = readTextFile(new File(d, "class").getAbsolutePath());
	    final String vendorStr = readTextFile(new File(d, "vendor").getAbsolutePath());
	    final String modelStr = readTextFile(new File(d, "device").getAbsolutePath());
	    final SysDevice.Type type = SysDevice.Type.PCI;
	    final String name = d.getName();
	    final String cls;
	    if (classStr != null && classStr.startsWith("0x"))
	    {
		final String res = pciIds.findClass(classStr.substring(2)); 
		if (res != null && !res.isEmpty())
		    cls = res; else
		    cls = classStr;
	    } else
		cls = classStr;
	    final String vendor;
	    if (vendorStr != null && vendorStr.startsWith("0x"))
	    {
		final String res = pciIds.findVendor(vendorStr.substring(2)); 
		if (res != null && !res.isEmpty())
		    vendor = res; else
		    vendor = vendorStr;
	    } else
		vendor = vendorStr;
	    final String model;
	    if (vendorStr != null && vendorStr.startsWith("0x") &&
		modelStr != null && modelStr.startsWith("0x"))
	    {
		final String res = pciIds.findDevice(vendorStr.substring(2), modelStr.substring(2));
		if (res != null && !res.isEmpty())
		    model = res; else
		    model = modelStr;
	    } else
		model = modelStr;
	    devices.add(new SysDeviceImpl(type,
					  name,
					  cls,
					  vendor,
					  model,
					  "", //driver
					  "" //module
					  ));
	}
	return devices.toArray(new SysDevice[devices.size()]);
    }

    @Override public StorageDevice[] getStorageDevices()
    {
	final List<StorageDevice> devices = new LinkedList<StorageDevice>();
	final File[] files = new File("/sys/block").listFiles();
	for(File f: files)
	{
	    final File deviceDir = new File(f, "device");
	    if (!deviceDir.exists() || !deviceDir.isDirectory())
		continue;
	    final StorageDevice dev = new StorageDevice();
	    dev.devName = f.getName();
	    dev.model = readTextFile(new File(deviceDir, "model").getAbsolutePath());
	    try {
		dev.capacity = Long.parseLong(readTextFile(new File(f, "size").getAbsolutePath()));
	    }
	    catch(NumberFormatException e)
	    {
		//		e.printStackTrace();
		dev.capacity = 0;
	    }
	    dev.capacity *= 512;
	    dev.removable = readTextFile(new File(f, "removable").getAbsolutePath()).equals("1");
	    devices.add(dev);
	}
	return devices.toArray(new StorageDevice[devices.size()]);
    }

    @Override public int mountAllPartitions(StorageDevice device)
    {
	NullCheck.notNull(device, "device");
	if (sysBlockDir == null)
	    return 0;
	int count = 0;
	final File[] files = new File(sysBlockDir, device.devName).listFiles();
	for(File f: files)
	{
	    if (!f.isDirectory() || !f.getName().startsWith(device.devName))
		continue;
	    if (mount(f.getName(), new File(new File(MEDIA_DIR), f.getName()).getAbsolutePath()))
	    ++count;
	}
	if (mount(device.devName, new File(new File(MEDIA_DIR), device.devName).getAbsolutePath()))
	    ++count;
	    return count;
    }

    @Override public int umountAllPartitions(StorageDevice device)
    {
	NullCheck.notNull(device, "device");
	if (sysBlockDir == null)
	    return 0;
	int count = 0;
	if (umount(new File(new File(MEDIA_DIR), device.devName).getAbsolutePath()))
	    ++count;
	final File[] files = new File(sysBlockDir, device.devName).listFiles();
	for(File f: files)
	{
	    if (!f.isDirectory() || !f.getName().startsWith(device.devName))
		continue;
	    if (umount(new File(new File(MEDIA_DIR), f.getName()).getAbsolutePath()))
	    ++count;
	}
	    return count;
    }

    @Override public org.luwrain.base.hardware.AudioMixer getAudioMixer()
    {
	if (mixer == null)
	    mixer = new AudioMixer(scriptsDir);
	return mixer;
    }

    @Override public Battery[] getBatteries()
    {
	return null;
    }

    private boolean mount(String devName, String mountPoint)
    {
	return scripts.runSync(Scripts.ID.MOUNT, new String[]{devName, mountPoint}, true);
    }

    private boolean umount(String mountPoint)
    {
	return scripts.runSync(Scripts.ID.UMOUNT, new String[]{mountPoint}, true);
    }

    @Override public Partition[] getMountedPartitions()
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
		l = createRemotePartition(store, path); else
		if (path.startsWith(MEDIA_DIR))
		    l = createRemovablePartition(store, path); else 
		    l = createRegularPartition(store, path);
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

private Partition createRemovablePartition(FileStore store, String path)
    {
	NullCheck.notNull(store, "store");
	NullCheck.notEmpty(path, "path");
	final String[] parts = store.name().split("/");
	if (parts == null || parts.length < 1 || parts[0] == null)
	    return null;
	return new PartitionImpl(Partition.Type.REMOVABLE, new File(path), parts[parts.length - 1], true);
    }

private Partition createRemotePartition(FileStore store, String path)
    {
	NullCheck.notNull(store, "store");
	NullCheck.notEmpty(path, "path");
	return new PartitionImpl(Partition.Type.REMOTE, new File(path), store.name(), true);
    }

private Partition createRegularPartition(FileStore store, String path)
    {
	NullCheck.notNull(store, "store");
	NullCheck.notEmpty(path, "path");
	if (path.equals("/"))
	    return null;
	return new PartitionImpl(Partition.Type.REGULAR, new File(path), path, true);
    }

    static private String     readTextFile(String fileName)
    {
	Path path = Paths.get(fileName);
	try {
	    final byte[] bytes = Files.readAllBytes(path);
final String s = new String(bytes, "US-ASCII");
final StringBuilder b = new StringBuilder();
for(int i = 0;i < s.length();++i)
    if (!Character.isISOControl(s.charAt(i)))
	b.append(s.charAt(i));
return b.toString();
	}
	catch(IOException e)
	{
	    e.printStackTrace();
	    return "";
	}
    }
}
