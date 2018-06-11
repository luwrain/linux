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

package org.luwrain.linux;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.luwrain.base.*;
import org.luwrain.core.*;

final class Hardware
{
    static private final String LOG_COMPONENT = Linux.LOG_COMPONENT;

        private final File sysBlockDir;
    private final File pciDevDir;
    private final PciIds pciIds = new PciIds();
    private AudioMixer mixer;
    private final Scripts scripts;
    private final PropertiesBase props;
        private final Path scriptsDir;

    Hardware(PropertiesBase props)
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

    public SysDevice[] getSysDevices()
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
	    devices.add(new SysDevice(type,
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
