/*
   Copyright 2012-2019 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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
import java.util.*;
import java.util.regex.*;

import org.luwrain.core.*;

import static org.luwrain.util.FileUtils.*;

public final class Parted
{
    static public final String
	GPT= "gpt",
	NVME = "nvme"
    SCSI = "scsi";

    static private final Pattern PAT_DEVICE = Pattern.compile("^(/[^:]*):([^:]*):([^:]*):([^:]*):([^:]*):([^:]*):([^:]*):.*;$", Pattern.CASE_INSENSITIVE);

    public interface Caller
    {
	String[] call(String[] args) throws IOException;
    }

    private final String device;
    private final Caller caller;
    private String size = "", type = "", partTableType = "", name = "";

    public Parted(String device, Caller caller)
    {
	NullCheck.notEmpty(device, "device");
	NullCheck.notNull(caller, "caller");
	this.device = device;
	this.caller = caller;
    }

    public void init() throws IOException
    {
	final String[] lines = caller.call(new String[]{device, "print"});
	for(String line: lines)
	{
	    final Matcher m = PAT_DEVICE.matcher(line.trim());
	    if (!m.find())
		continue;
	    this.size = m.group(2);
	    this.type = m.group(3);
	    this.partTableType = m.group(6);
	    this.name = m.group(7);
	}
    }

    public String getSize() { return size; }
    public String getType() { return type; }
    public String getPartTableType() { return partTableType; }
    public String getName() { return name; }
}
