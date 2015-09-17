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

package org.luwrain.linux.term;

import java.util.*;

class PT
{
    native static private int createImpl();
    native static private int launchImpl(int fd, String cmd);
    native static private void closeImpl(int fd);
    native static private byte[] readImpl(int fd);
    native static private int writeImpl(int fd, byte[] data);
    native static private String errnoString();

    private int fd = -1;
    private int pid = -1;

    public boolean create()
    {
	final int res = createImpl(); 
	if (res < 0)
	    return false;
	fd = res;
	return true;
    }

    public boolean launch(String cmd)
    {
	final int res = launchImpl(fd, cmd);
	if (res < 0)
	    return false;
	pid = res;
	return true;
    }

    //returns the actual amount of written data or -1 if the terminal is closed
    public int write(byte[] data)
    {
	if (data == null || data.length <= 0)
	    return 0;
	System.out.println("Need to write " + Arrays.toString(data));
	final int res = writeImpl(fd, data);
	System.out.println("res=" + res);
	if (res < 0)
	{
	    close();
	    return -1;
	}
	return res;
    }

    public byte[] read()
    {
	if (pid < 0 || fd < 0)
	{
	    close();
	    return null;
	}
	final byte[] res = readImpl(fd);
	if (res == null)
	{
	    close();
	    return null;
	}
	return res;
    }

    public void close()
    {
	closeImpl(fd);
	pid = -1;
	fd = -1;
    }

    public boolean isOpened()
    {
	return pid > 0 && fd >= 0;
    }
}
