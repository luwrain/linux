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
import java.util.regex.*;

import org.luwrain.core.*;

class PciIds
{
    private final static String PCIIDS_FILE = "/usr/share/misc/pci.ids";
    static private final Pattern VENDOR_PATTERN = Pattern.compile("^([^\\s]+)\\s+([^\\s].*)$");
    static private final Pattern DEVICE_PATTERN = Pattern.compile("^\t([^\\s]+)\\s+([^\\s].*)$");

    private final TreeMap<String, Vendor> vendors = new TreeMap<String, Vendor>();
    private Vendor lastVendor = null;

    String findVendor(String code)
    {
	NullCheck.notEmpty(code, "code");
	if (!vendors.containsKey(code))
	    return null;
	return vendors.get(code).name;
    }

    String findDevice(String vendorCode, String deviceCode)
    {
	NullCheck.notNull(vendorCode, "vendorCode");
	NullCheck.notNull(deviceCode, "deviceCode");
	if (!vendors.containsKey(vendorCode))
	    return null;
	final Vendor v = vendors.get(vendorCode);
	if (!v.devices.containsKey(deviceCode))
	    return null;
	return v.devices.get(deviceCode).name;
    }

    void load()
    {
	try  {
	    final InputStream is = new FileInputStream(PCIIDS_FILE);
	    try {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = null;
		while ( (line = reader.readLine()) != null)
		    onLine(line);
	    }
	    finally {
		is.close();
	    }
	}
	catch(Exception e)
	{
	    Log.error("linux", "unable to read PCIIDs from :" + e.getClass().getName() + ":" + e.getMessage());
	}
    }

    private void onVendor(String line)
    {
	NullCheck.notNull(line, "line");
	final Matcher matcher = VENDOR_PATTERN.matcher(line);
	if (!matcher.find())
	    return;
	final Vendor v = new Vendor(matcher.group(2));
	vendors.put(matcher.group(1).trim(), v);
	lastVendor = v;
    }

    private void onDevice(String line)
    {
	NullCheck.notNull(line, "line");
	final Matcher matcher = DEVICE_PATTERN.matcher(line);
	if (!matcher.find())
	    return;
	final Device d = new Device(matcher.group(2));
	NullCheck.notNull(lastVendor, "lastVendor");
	lastVendor.devices.put(matcher.group(1).trim(), d);
    }

    private void onLine(String line)
    {
	if (line.length() < 2 || line.charAt(0) == '#')
	    return;
	if (line.charAt(0) != '\t')
	    onVendor(line); else
	    if (lastVendor != null && line.charAt(1) != '\t')
		onDevice(line);
    }

    static private class Device
    {
	final String name;

	Device(String name)
	{
	    NullCheck.notNull(name, "name");
	    this.name = name;
	}
    }

    static private class Vendor
    {
	final String name;
	final TreeMap<String, Device> devices = new TreeMap<String, Device>();

	Vendor(String name)
	{
	    NullCheck.notNull(name, "name");
	    this.name = name;
	}
    }
}
