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

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

class PciIds
{
    private Pattern VENDOR_PATTERN = Pattern.compile("^([^\\s]+)\\s+([^\\s].*)$");
    private Pattern DEVICE_PATTERN = Pattern.compile("^\t([^\\s]+)\\s+([^\\s].*)$");

    private final static String PCIIDS_FILE = "/usr/share/misc/pci.ids";

    private class Device
    {
	public String name;

	public Device(String name)
	{
	    this.name = name;
	}
    }

    private class Vendor
    {
	public String name;
	public TreeMap<String, Device> devices = new TreeMap<String, Device>();

	public Vendor(String name)
	{
	    this.name = name;
	}
    }

    private TreeMap<String, Vendor> vendors = new TreeMap<String, Vendor>();
    private Vendor lastVendor = null;

    public String findVendor(String code)
    {
	if (!vendors.containsKey(code))
	    return null;
	return vendors.get(code).name;
    }

    public String findDevice(String vendorCode, String deviceCode)
    {
	//	System.out.println(vendorCode + " " + deviceCode);
	if (!vendors.containsKey(vendorCode))
	    return null;
final Vendor v = vendors.get(vendorCode);
//System.out.println(v.devices.size());
if (!v.devices.containsKey(deviceCode))
    return null;
//System.out.println("found");
return v.devices.get(deviceCode).name;
    }

    private void onVendor(String line)
    {
	final Matcher matcher = VENDOR_PATTERN.matcher(line);
	if (!matcher.find())
	    return;
	final Vendor v = new Vendor(matcher.group(2));
	vendors.put(matcher.group(1).trim(), v);
	lastVendor = v;
    }

    private void onDevice(String line)
    {
	final Matcher matcher = DEVICE_PATTERN.matcher(line);
	//	System.out.println("matcher");
	if (!matcher.find())
	    return;
	//	System.out.println(matcher.group(1));
	final Device d = new Device(matcher.group(2));
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

    public void load()
    {
	Path path = Paths.get(PCIIDS_FILE);
	try {
	    try (Scanner scanner =  new Scanner(path, "UTF-8"))
		{
		    while (scanner.hasNextLine())
			onLine(scanner.nextLine());
		}
	}
	catch(IOException e)
	{
	    e.printStackTrace();
	}
    }
}
