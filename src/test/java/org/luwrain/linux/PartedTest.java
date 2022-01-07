/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>

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
import java.net.*;

import org.junit.*;

import org.luwrain.core.*;

public class PartedTest extends Assert
{
    private Parted parted = null;

    @Test public void info() throws IOException
    {
	parted.init();
	assertEquals("128GB", parted.getSize());
	assertEquals(Parted.NVME, parted.getType());
	assertEquals(Parted.GPT, parted.getPartTableType());
    }

    @Before public void create()
    {
	this.parted = new Parted("/dev/nvme0n1", caller());
    }

    private Parted.Caller caller()
    {
	return (args)->{
	    return new String[]{
		"BYT;",
		"/dev/nvme0n1:128GB:nvme:512:512:gpt:KBG40ZMT128G TOSHIBA MEMORY:;",
		"1:1049kB:211MB:210MB:fat16::загрузочный, esp;",
		"2:211MB:31,7GB:31,5GB:ext4::;",
		"3:31,7GB:42,2GB:10,5GB:ext4::;",
		"4:42,2GB:128GB:85,9GB:::;"
	    };
	};
    }
}
