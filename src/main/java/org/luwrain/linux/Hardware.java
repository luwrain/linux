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
import org.luwrain.base.*;

final class Hardware implements org.luwrain.base.Hardware
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

    Hardware(Scripts scripts, org.luwrain.base.CoreProperties props)
    {
	NullCheck.notNull(scripts, "scripts");
	NullCheck.notNull(props, "props");
	this.scripts = scripts;
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
	    final String vendorStr = readTextFile(new File(d, "vendor").getAbsolutePath());
	    final String modelStr = readTextFile(new File(d, "device").getAbsolutePath());
	    final SysDevice.Type type = SysDevice.Type.PCI;
	    final String name = d.getName();
	    final String vendor;
	    if (vendorStr != null && vendorStr.startsWith("0x"))
	    {
		final String res = pciIds.findVendor(vendorStr.substring(2)); 
		if (res != null && res.isEmpty())
		    vendor = res; else
		    vendor = vendorStr;
	    } else
		vendor = vendorStr;
	    final String classStr = readTextFile(new File(d, "class").getAbsolutePath());
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
					  classStr,
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

    @Override public Partition[] getMountedPartitions()
    {
	return MountedPartitions.getMountedPartitions();
    }

    @Override public org.luwrain.base.AudioMixer getAudioMixer()
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
