
package org.luwrain.linux;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.luwrain.hardware.*;

class Hardware implements org.luwrain.hardware.Hardware
{
    private PciIds pciIds = new PciIds();

    public Hardware()
    {
	pciIds.load();
    }

    @Override public Device[] getDevices()
    {
	LinkedList<Device> devices = new LinkedList<Device>();
	final File[] pciDirs = new File("/sys/bus/pci/devices").listFiles();
	for(File d: pciDirs)
	{
	    final Device dev = new Device();
	    dev.type = Device.PCI;
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
		//		System.out.println(dev.model);
	    }
	    if (dev.model == null || dev.model.isEmpty())
		dev.model = modelStr;





	    devices.add(dev);
	    }
	return devices.toArray(new Device[devices.size()]);
    }

    @Override public StorageDevice[] getStorageDevices()
    {
	LinkedList<StorageDevice> devices = new LinkedList<StorageDevice>();
	final File[] files = new File("/sys/block").listFiles();
	for(File f: files)
	{
	    final File deviceDir = new File(f, "device");
	    if (!deviceDir.exists())
		continue;
	    final StorageDevice dev = new StorageDevice();
	    dev.devName = f.getName();
	    dev.model = readTextFile(new File(deviceDir, "model").getAbsolutePath());
	    try {
		dev.capacity = Long.parseLong(readTextFile(new File(f, "size").getAbsolutePath()));
	    }
	    catch(NumberFormatException e)
	    {
		e.printStackTrace();
		dev.capacity = 0;
	    }
	    dev.capacity *= 512;
	    dev.removable = readTextFile(new File(f, "removable").getAbsolutePath()).equals("1");
	    devices.add(dev);
	}
	return devices.toArray(new StorageDevice[devices.size()]);
    }

    private static String     readTextFile(String fileName)
    {
	StringBuilder s = new StringBuilder();
	Path path = Paths.get(fileName);
	try {
	    try (Scanner scanner =  new Scanner(path, "UTF-8"))
		{
		    while (scanner.hasNextLine())
		    {
			if (!s.toString().isEmpty())
			    s.append(" ");
			s.append(scanner.nextLine());
		    }
		}
	}
	catch(IOException e)
	{
	    e.printStackTrace();
	    return "";
	}
	return s.toString();
    }
}
