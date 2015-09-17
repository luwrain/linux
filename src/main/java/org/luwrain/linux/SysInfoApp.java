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

public class SysInfoApp
{
    public static void main(String[] args)
    {
	Hardware h = new Hardware();
	for(org.luwrain.hardware.Device d:h.getDevices())
	{
	    System.out.println("# PCI device " + d.id);
	    System.out.println("Class: " + d.cls);
	    System.out.println("Vendor: " + d.vendor);

	    System.out.println("Model: " + d.model);
	    System.out.println("");
	}

	for(org.luwrain.hardware.StorageDevice d:h.getStorageDevices())
	{
	    System.out.println("# " + "/dev/" + d.devName);
	    System.out.println("Model: " + d.model );
	    final long gValue = d.capacity / (1024 * 1024 * 1024);
	    long decValue = d.capacity - gValue * 1024 * 1024 * 1024;
	    decValue /= (1024 * 1024 * 103);


	    System.out.println("Capacity: " + gValue + "." + decValue + "G");
	    System.out.println("Removable: " + (d.removable?"yes":"no"));

	    System.out.println();
	}
    }
}
