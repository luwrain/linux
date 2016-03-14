/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.luwrain.core.Log;
import org.luwrain.hardware.*;

final class Hardware implements org.luwrain.hardware.Hardware
{
    private final PciIds pciIds = new PciIds();
    private AudioMixer mixer;
    private Path scriptsDir;

    Hardware(Path scriptsDir)
    {
	pciIds.load();
	this.scriptsDir = scriptsDir;
    }

    @Override public SysDevice[] getSysDevices()
    {
	final LinkedList<SysDevice> devices = new LinkedList<SysDevice>();
	final File[] pciDirs = new File("/sys/bus/pci/devices").listFiles();
	for(File d: pciDirs)
	{
	    final SysDevice dev = new SysDevice();
	    dev.type = SysDevice.PCI;
	    dev.id = d.getName();
	    final String vendorStr = readTextFile(new File(d, "vendor").getAbsolutePath());
	    dev.vendor = vendorStr;
	    if (dev.vendor != null && dev.vendor.startsWith("0x"))
		dev.vendor = pciIds.findVendor(dev.vendor.substring(2));
	    if (dev.vendor == null || dev.vendor.isEmpty())
		dev.vendor = vendorStr;

	    final String classStr = readTextFile(new File(d, "class").getAbsolutePath());
	    dev.cls = classStr;

	    final String modelStr = readTextFile(new File(d, "device").getAbsolutePath());
	    dev.model = modelStr;
	    if (vendorStr != null && vendorStr.startsWith("0x") &&
		modelStr != null && modelStr.startsWith("0x"))
	    {
		dev.model = pciIds.findDevice(vendorStr.substring(2), dev.model.substring(2));
	    }
	    if (dev.model == null || dev.model.isEmpty())
		dev.model = modelStr;

	    devices.add(dev);
	}
	return devices.toArray(new SysDevice[devices.size()]);
    }

    @Override public StorageDevice[] getStorageDevices()
    {
	final LinkedList<StorageDevice> devices = new LinkedList<StorageDevice>();
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
	if (device == null)
	    return -1;
	int count = 0;
	if (mount(device.devName, new File(new File(Constants.MEDIA_DIR), device.devName).getAbsolutePath()))
	    ++count;
	final File[] files = new File(new File(Constants.SYS_BLOCK_DIR), device.devName).listFiles();
	for(File f: files)
	{
	    if (!f.isDirectory() || !f.getName().startsWith(device.devName))
		continue;
	    if (mount(f.getName(), new File(new File(Constants.MEDIA_DIR), f.getName()).getAbsolutePath()))
	    ++count;
	}
	    return count;
    }

    @Override public int umountAllPartitions(StorageDevice device)
    {
	if (device == null)
	    return -1;
	int count = 0;
	if (umount(new File(new File(Constants.MEDIA_DIR), device.devName).getAbsolutePath()))
	    ++count;
	final File[] files = new File(new File(Constants.SYS_BLOCK_DIR), device.devName).listFiles();
	for(File f: files)
	{
	    if (!f.isDirectory() || !f.getName().startsWith(device.devName))
		continue;
	    if (umount(new File(new File(Constants.MEDIA_DIR), f.getName()).getAbsolutePath()))
	    ++count;
	}
	    return count;
    }

    private boolean mount(String devName, String mountPoint)
    {
	if (exec("sudo mkdir -p \'" + mountPoint + "\'") != 0)
	    return false;
	if (exec("sudo mount -o umask=000  \'/dev/" + devName + "\' \'" + mountPoint + "\'") == 0)
	    return true;
	if (exec("sudo mount \'/dev/" + devName + "\' \'" + mountPoint + "\'") == 0)
	    return true;
	exec("sudo rmdir -p \'" + mountPoint + "\'");
	return false;
    }

    private boolean umount(String mountPoint)
    {
	if (exec("sudo umount \'" + mountPoint + "\'") != 0)
	    return false;
	exec("sudo rmdir \'" + mountPoint + "\'");
	return true;
    }

    @Override public Partition[] getMountedPartitions()
    {
	return MountedPartitions.getMountedPartitions();
    }

    @Override public org.luwrain.hardware.AudioMixer getAudioMixer()
    {
	if (mixer == null)
	    mixer = new AudioMixer(scriptsDir);
	return mixer;
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

    static private int exec(String cmd)
    {
	Log.debug("linux", "executing:" + cmd);
	try {
	    final Process p = Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c", cmd});
	    p.waitFor();
	    final int res = p.exitValue();
	    Log.debug("linux", "exit code:" + res);
	    return res;
	}
	catch (InterruptedException e)
	{
	    e.printStackTrace();
	    Thread.currentThread().interrupt();
	    return -1;
	}
	catch(IOException e)
	{
	    e.printStackTrace();
	    return -1;
	}
    }

    @Override public Battery[] getBatteries()
    {
	return null;
    }
}
